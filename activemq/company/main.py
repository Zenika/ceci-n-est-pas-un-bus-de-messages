import argparse
import time
import stomp
import sys
import logging
import json
import uuid
import random
import datetime
import cherrypy
from babel.dates import format_datetime, format_timedelta
from string import Template
# An independent thread will be used for STOMP communication
import threading
import humanize

class Company:
    def __init__(self):
        self.contracts = 0
        self.quantity = 0
        self.creation_date = datetime.datetime.now()
        self.messages = {
            "sent":0,
            "received":0
        }
    def add_contract(self, contract):
        self.contracts+=1
        self.quantity += contract.expected

class Contract:
    def __init__(self, quantity):
        super().__init__()
        self.providers = {}
        self.expected = quantity
        self.total = 0
    def add(self, message):
        if isinstance(message, dict):
            if message['producer']:
                if message['quantity']:
                    if not message['producer'] in self.providers:
                        self.providers[message['producer']]=0
                    self.providers[message['producer']]+=message['quantity']
                    self.total+=message['quantity']
    def complete(self):
        return self.total>=self.expected
    def call_offers(self):
        return {
            "description": "Can I get %d Kg of tea, please?"%self.expected,
            "required": self.expected
            }
    def __str__(self):
        return str(self.__dict__)

class BuyListener(stomp.ConnectionListener):
    def __init__(self, company):
        super().__init__()
        self.contract = None
        self.company = company
    def on_error(self, headers, message):
        # ERROR !
        print('received an error. headers: %s message:"%s"' % (headers, message))
    def on_message(self, headers, message):
        data = json.loads(message)
        self.company.messages["received"]+=1
        if self.contract:
            self.contract.add(data)

def exchange_contracts(artemis_host, artemis_port, quantity, company):
    # The sleep os here to make sure producers qtart first (and listen to our company orders)
    time.sleep(15)
    connectionAddress = (artemis_host, artemis_port)
    print('Connecting to %s', connectionAddress)
    conn = stomp.Connection([connectionAddress])
    conn.connect('artemis', 'artemis', wait=True)
    buying = BuyListener(company)
    conn.set_listener('', buying)
    # Reset company age when starting to send messages
    company.creation_date = datetime.datetime.now()
    print("Connected! Sending tea commands!")
    # Listen for messages sent by tea producers
    listening = False
    while True:
        buying.contract = Contract(quantity=random.randrange(1, quantity))
#        print("Sending contract %s"%buying.contract)
        conn.send(body=json.dumps(buying.contract.call_offers()), destination='tea.commands')
        company.messages["sent"]+=1
        if not listening:
            try:
                conn.subscribe(destination='tea.proposals', id=uuid.uuid1(), ack='auto')
                listening = True
            except:
                # WARNING
                print("There is no tea coming from providers, we will retry later.")
        # Now wait for contract completing
        while not buying.contract.complete():
            time.sleep(0.001)
        # Contract is complete ? Find something to do ...
        conn.send(body=json.dumps(buying.contract.__dict__), destination="tea.contracts")
        company.messages["sent"]+=1
        company.add_contract(buying.contract)
    conn.disconnect()

class Server:
    def __init__(self, company):
        self.company = company
    @cherrypy.expose
    def index(self):
        now = datetime.datetime.now()
        # This is a TimeDelta object !
        age = now - self.company.creation_date
        # Create a time delta to get company "age"
        formatted = format_timedelta(age)
        age_seconds=age.total_seconds()
        # Yes we *could* use Jinja templating, but why for?
        returned = Template("""<html>
    <head>
        <meta http-equiv="refresh" content="1">
    </head>
    <body>
        <table>
            <tr><th></th><th>Count</th><th>Per second</th></tr>
            <tr><td>Contracts</td><td>$contracts</td><td>$cps</td></tr>
            <tr><td>Quantity</td><td>$quantity</td><td>$qps</td></tr>
            <tr><td>Messages sent</td><td>$sent</td><td>$sps</td></tr>
            <tr><td>Messages received</td><td>$received</td><td>$rps</td></tr>
            <tr><td>Messages exchanged</td><td>$total</td><td>$tps</td></tr>
        </table>
        <p>Company age $age</p>
    </body>
</html>""")
        return returned.substitute(
            age = formatted,
            contracts=humanize.intword(self.company.contracts),
            cps="{0:.2f}".format(self.company.contracts/age_seconds),
            quantity=humanize.intword(self.company.quantity),
            qps="{0:.2f}".format(self.company.quantity/age_seconds),
            sent=humanize.intword(self.company.messages["sent"]),
            sps="{0:.2f}".format(self.company.messages["sent"]/age_seconds),
            received=humanize.intword(self.company.messages["received"]),
            rps="{0:.2f}".format(self.company.messages["received"]/age_seconds),
            total=humanize.intword(self.company.messages["sent"]+self.company.messages["sent"]),
            tps="{0:.2f}".format((self.company.messages["sent"]+self.company.messages["sent"])/age_seconds),
            )

def start_server(http_port, company):
    cherrypy.config.update({
        'server.socket_host': '0.0.0.0',
        'server.socket_port': http_port,
    })
    cherrypy.quickstart(Server(company))

def main(artemis_host, artemis_port, http_port, quantity):
    company = Company()
    t = threading.Thread(target=exchange_contracts, args=(artemis_host, artemis_port, quantity, company))
    t.daemon = True
    t.start()
    start_server(http_port, company)

if __name__ == '__main__':
    company = argparse.ArgumentParser(description='A configurable tea company')
    company.add_argument('--artemis-host', action='store', type=str, nargs='?', help="ActiveMQ Artemis host", default='localhost')
    company.add_argument('--artemis-port', action='store', type=int, nargs='?', help="ActiveMQ Artemis port", default=21613)
    company.add_argument('--http-port', action='store', type=int, nargs='?', help="CheeryPy HTTP Port", default=8080)
    company.add_argument('--quantity', action='store', type=int, nargs='?', help="Contract quantity", default=100)
    args = company.parse_args()
    main(**vars(args))
