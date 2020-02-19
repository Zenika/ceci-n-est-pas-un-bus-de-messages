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
                time.sleep(0.01)
                self.connection.send(body=json.dumps(self.outputMessage), destination='tea.proposals')
                produced += self.outputMessage['quantity']
            LOGGER.debug("command should be honored, no?")
        else:
            LOGGER.info("Received contract! %s"%inputMessage)

def main(host, port, name, production):
    connectionAddress = (host, port)
    LOGGER.info('Connecting to %s', connectionAddress)
    conn = stomp.Connection([connectionAddress])
    conn.set_listener('', CommandsListener(connection=conn, producer=name, production=production))
    conn.connect('artemis', 'artemis', wait=True)
    LOGGER.info("Connected! Listening for tea commands!")
    # Listen for messages sent by tea producers
    conn.subscribe(destination='tea.commands', id=uuid.uuid1(), ack='auto')
    conn.subscribe(destination='tea.contracts', id=uuid.uuid1(), ack='auto')
    count = 0
    while True:
        time.sleep(1)
    conn.disconnect()


time.sleep(10)
producer = argparse.ArgumentParser(description='A configurable tea producer')
producer.add_argument('--host', action='store', type=str, nargs='?', help="ActiveMQ Artemis host", default='localhost')
producer.add_argument('--port', action='store', type=int, nargs='?', help="ActiveMQ Artemis port", default=21613)
producer.add_argument('--name', action='store', type=str, nargs='?', help="Name of the producer", default='SriLanka')
producer.add_argument('--production', action='store', type=int, nargs='?', help="Production of the tea producer", default=1)
args = producer.parse_args()
main(**vars(args))
