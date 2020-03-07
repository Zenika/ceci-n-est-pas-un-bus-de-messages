import argparse
import time
import stomp
import logging
import sys
import json
import uuid
import datetime
import cherrypy
from babel.dates import format_datetime, format_timedelta
from string import Template
import humanize
# An independent thread will be used for STOMP communication
import threading

class CommandsListener(stomp.ConnectionListener):
    def __init__(self, connection, producer, production):
        super().__init__()
        self.connection = connection
        self.creation_date = datetime.datetime.now()
        self.outputMessage = {'producer': producer, 'quantity': production}
        self.messages = {
            "sent":0,
            "received":0
        }
    def on_error(self, headers, message):
        print('received an error. headers: %s message:"%s"' % (headers, message))
    def on_message(self, headers, inputMessage):
        self.messages["received"]+=1
        if headers["destination"]=="tea.commands":
            command = json.loads(inputMessage)
            produced = 0
            while produced<command['required']:
#                LOGGER.debug("Sending %s"%self.outputMessage)
                time.sleep(0.01)
                self.connection.send(body=json.dumps(self.outputMessage), destination='tea.proposals')
                self.messages["sent"]+=1
                produced += self.outputMessage['quantity']
        else:
            pass
#            LOGGER.info("Received contract! %s"%inputMessage)

class Server:
    def __init__(self, producer):
        self.producer = producer
    @cherrypy.expose
    def index(self):
        now = datetime.datetime.now()
        # This is a TimeDelta object !
        age = now - self.producer.creation_date
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
            <tr><td>Messages sent</td><td>$sent</td><td>$sps</td></tr>
            <tr><td>Messages received</td><td>$received</td><td>$rps</td></tr>
            <tr><td>Messages exchanged</td><td>$total</td><td>$tps</td></tr>
        </table>
        <p>Company age $age</p>
    </body>
</html>""")
        return returned.substitute(
            age = formatted,
            sent=humanize.intword(self.producer.messages["sent"]),
            sps="{0:.2f}".format(self.producer.messages["sent"]/age_seconds),
            received=humanize.intword(self.producer.messages["received"]),
            rps="{0:.2f}".format(self.producer.messages["received"]/age_seconds),
            total=humanize.intword(self.producer.messages["sent"]+self.producer.messages["sent"]),
            tps="{0:.2f}".format((self.producer.messages["sent"]+self.producer.messages["sent"])/age_seconds),
            )


def main(artemis_host, artemis_port, http_port, name, production):
    connectionAddress = (artemis_host, artemis_port)
    conn = stomp.Connection([connectionAddress])
    listener = CommandsListener(connection=conn, producer=name, production=production)
    t = threading.Thread(target=accept_contracts, args=(conn, listener))
    t.daemon = True
    t.start()
    start_server(http_port, listener)

def start_server(http_port, listener):
    cherrypy.config.update({
        'server.socket_host': '0.0.0.0',
        'server.socket_port': http_port,
    })
    cherrypy.quickstart(Server(listener))

def accept_contracts(conn, listener):
    conn.set_listener('', listener)
    conn.connect('artemis', 'artemis', wait=True)
    print("Connected! Listening for tea commands!")
    # Listen for messages sent by tea producers
    conn.subscribe(destination='tea.commands', id=uuid.uuid1(), ack='auto')
    conn.subscribe(destination='tea.contracts', id=uuid.uuid1(), ack='auto')
    count = 0
    while True:
        time.sleep(1)
    conn.disconnect()

if __name__ == '__main__':
    time.sleep(10)
    producer = argparse.ArgumentParser(description='A configurable tea producer')
    producer.add_argument('--artemis-host', action='store', type=str, nargs='?', help="ActiveMQ Artemis host", default='localhost')
    producer.add_argument('--artemis-port', action='store', type=int, nargs='?', help="ActiveMQ Artemis port", default=21613)
    producer.add_argument('--http-port', action='store', type=int, nargs='?', help="CheeryPy HTTP Port", default=8080)
    producer.add_argument('--name', action='store', type=str, nargs='?', help="Name of the producer", default='SriLanka')
    producer.add_argument('--production', action='store', type=int, nargs='?', help="Production of the tea producer", default=1)
    args = producer.parse_args()
    main(**vars(args))
