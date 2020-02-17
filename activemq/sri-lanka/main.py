import argparse
import time
import stomp
import logging
import sys
import json
import uuid

LOGGER = logging.getLogger("Provider")
LOGGER.setLevel(logging.DEBUG)
LOGGER.addHandler(logging.StreamHandler(sys.stdout))

class CommandsListener(stomp.ConnectionListener):
    def __init__(self, connection, producer, production):
        super().__init__()
        self.connection = connection
        self.outputMessage = {'producer': producer, 'quantity': production}
    def on_error(self, headers, message):
        LOGGER.error('received an error. headers: %s message:"%s"' % (headers, message))
    def on_message(self, headers, inputMessage):
        if headers["destination"]=="tea.commands":
            command = json.loads(inputMessage)
            produced = 0
            while produced<command['required']:
#                LOGGER.debug("Sending %s"%self.outputMessage)
#                time.sleep(1)
                self.connection.send(body=json.dumps(self.outputMessage), destination='tea.proposals')
                produced += self.outputMessage['quantity']
            LOGGER.info("command should be honored, no?")
        else:
            LOGGER.info("Received contract! %s"%inputMessage)

def main():
    connectionAddress = ('127.0.0.1', '21613')
    LOGGER.info('Connecting to %s', connectionAddress)
    conn = stomp.Connection([connectionAddress])
    conn.set_listener('', CommandsListener(connection=conn, producer="Sri-Lanka", production=10))
    conn.connect('artemis', 'artemis', wait=True)
    LOGGER.info("Connected! Listening for tea commands!")
    # Listen for messages sent by tea producers
    conn.subscribe(destination='tea.commands', id=uuid.uuid1(), ack='auto')
    conn.subscribe(destination='tea.contracts', id=uuid.uuid1(), ack='auto')
    count = 0
    while True:
        time.sleep(1)
    conn.disconnect()


main()
