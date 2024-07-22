from rasa_sdk import Action
from websocket import create_connection

class Send(Action):

    host = "localhost"
    port = 5002

    def name(self):
        return "send"

    def run(self, dispatcher, tracker, domain):
        ws = create_connection( "ws://" + self.host + ":" + str(self.port) )
        ws.send("Hello World!")
        result = ws.recv()
        ws.close()