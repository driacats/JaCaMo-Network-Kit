import asyncio
import websockets
from websockets.server import serve
import json
import logging
import argparse
from dataclasses import dataclass

port = 9080
ip = "localhost"
port_registry = {}          # port-hostname
websocket_registry = {}     # hostname-websocket

nclients = 0;
all_clients_connected = False

logger = logging.getLogger(__name__)
logger.setLevel(logging.DEBUG)
formatter = logging.Formatter('%(asctime)s - %(levelname)s - %(message)s')

handler = logging.StreamHandler()
handler.setLevel(logging.DEBUG)
handler.setFormatter(formatter)
logger.addHandler(handler)


async def handle_incoming_messages(websocket):
    global all_clients_connected

    async for message in websocket:
        try:
            client_ip, client_port, *_ = websocket.remote_address
            logger.info(f"Client connected from {client_ip}:{client_port}")
        
            try:
                json_data = json.loads(message)
                message_type = json_data.get("message_type")

                match message_type:
                    case "init":
                        agent_name = json_data.get("agent_name")
                        if not client_port in port_registry:
                            logger.debug(f"Received init message from {agent_name}")
                            
                            if agent_name in websocket_registry:
                                for p, n in port_registry.items():
                                    if n == agent_name:
                                        logger.debug(f"{agent_name} was previously associated to port {p}, updating the registry.")
                                        port_registry.pop(p)
                                        break

                            websocket_registry[agent_name] = websocket
                            port_registry[client_port] = agent_name
                            
                            if len(port_registry.keys()) == nclients:
                                logger.info("All the expected clients are connected.")
                                for port in port_registry.keys():
                                    logger.debug(f" └> sending start signal to {port_registry[port]}")
                                    await websocket_registry[port_registry[port]].send(json.dumps(StartGameMessage().__dict__))
                                all_clients_connected = True
                        else:
                            logger.debug(f"Received a init message from {agent_name}, port {client_port}, but I already know him...")

                    case "percept":
                        logger.debug(f"Received a {message_type} message from {port_registry[client_port]}")
                        
                        if not all_clients_connected:
                            logger.debug("Unfortunately not all client are connected...")
                            break;
                        
                        if client_port in port_registry:
                            receiver = json_data.get("receiver")
                            logger.debug(f" └> forwarding it to {receiver}")
                            await websocket_registry[receiver].send(message);
                    
                    case "action":
                        logger.debug(f"Received a {message_type} message from {port_registry[client_port]}")

                        if not all_clients_connected:
                            logger.debug("Unfortunately not all client are connected...")
                            break
                        
                        if client_port in port_registry:
                            if "unity" in websocket_registry:
                                logger.debug(f" └> forwarding it to {receiver}")
                                await websocket_registry[receiver].send(message);
                            else:
                                logger.debug(f" └> {receiver} is an unknown host.")
            
            except json.JSONDecodeError:
                logger.error(f"Error decoding the JSON message")

        except websockets.ConnectionClosed:
            logger.info(f"Connection closed for {client_ip}:{client_port}")
        finally:
            logger.info(f"Client disconnected from {client_ip}:{client_port}")

async def main():
    global nclients, port, ip

    parser = argparse.ArgumentParser();
    parser.add_argument("-nc", "--nclients", type=int, required=True, help="The number of clients from which the server awaits a connection")
    parser.add_argument("-p", "--port", type=str, required=False, help="The port on which to serve")
    parser.add_argument("-a", "--address", type=str, required=False, help="The address on which to serve")
    
    args = parser.parse_args()
    nclients = args.nclients
    port = args.port
    ip = args.address

    logger.info(f"Server up and running, serving at {ip} on port {port}")

    async with serve(handle_incoming_messages, ip, port):
        await asyncio.Future()

asyncio.run(main())