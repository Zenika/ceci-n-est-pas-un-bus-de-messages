import argparse
import time
import stomp
import sys
import logging
import json
import uuid

LOGGER = logging.getLogger("London")
LOGGER.setLevel(logging.INFO)
LOGGER.addHandler(logging.StreamHandler(sys.stdout))

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
                    LOGGER.debug("producers promise %d (we expect at least %d)"%(self.total, self.expected))
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
    def __init__(self):
        super().__init__()
        self.contract = None
    def on_error(self, headers, message):
        LOGGER.error('received an error. headers: %s message:"%s"' % (headers, message))
    def on_message(self, headers, message):
        data = json.loads(message)
        LOGGER.debug("Received message at London containing (headers:\"%s\", message:\"%s\")"%(headers, message))
        if self.contract:
            self.contract.add(data)

def main():
    connectionAddress = ('127.0.0.1', '21613')
    LOGGER.info('Connecting to %s', connectionAddress)
    conn = stomp.Connection([('127.0.0.1', '21613')])
    conn.connect('artemis', 'artemis', wait=True)
    buying = BuyListener()
    conn.set_listener('', buying)
    LOGGER.info("Connected! Sending tea commands!")
    # Listen for messages sent by tea producers
    listening = False
    while True:
        buying.contract = Contract(quantity=100)
        LOGGER.info("Sending contract %s"%buying.contract)
        conn.send(body=json.dumps(buying.contract.call_offers()), destination='tea.commands')
        if not listening:
            try:
                conn.subscribe(destination='tea.proposals', id=uuid.uuid1(), ack='auto')
                listening = True
            except:
                LOGGER.warning("There is no tea coming from providers, we will retry later.")
        # Now wait for contract completing
        while not buying.contract.complete():
            time.sleep(1)
        # Contract is complete ? Find something to do ...
        LOGGER.info("Contract is complete, sending it to all producers")
        conn.send(body=json.dumps(buying.contract.__dict__), destination="tea.contracts")
    conn.disconnect()


main()
