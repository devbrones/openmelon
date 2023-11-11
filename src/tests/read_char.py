import asyncio
from bleak import BleakClient

class MyBleakClient:
    def __init__(self):
        self.client = BleakClient("E6:4D:9B:8C:D8:53")
        self.services_discovered = False

    async def connect_and_interact(self):
        async with self.client as client:
            if client.is_connected():
                await self.disconnect_client(client)

            await client.connect()
            self.client.set_disconnected_callback(self.on_disconnected)

            # Discover services
            await client.get_services()
            await self.read_characteristics(client)

            # Subscribe for characteristic changes
            await client.start_notify("6e400002-b5a3-f393-e0a9-e50e24dcca9e", self.notification_handler)

            # Disconnect before reconnecting
            await self.disconnect_client(client)

    async def disconnect_client(self, client):
        if await client.is_connected():
            await client.disconnect()
            
    async def read_characteristics(self, client):
        if self.services_discovered:
            # Read a specific characteristic value
            value = await client.read_gatt_char("6e400003-b5a3-f393-e0a9-e50e24dcca9e")
            print("Read value:", value)

    def on_disconnected(self, client):
        print("Disconnected from device")
        # Handle the disconnection event

    async def notification_handler(self, sender, data):
        print(f"Received notification from {sender}: {data}")
        # Handle the received notifications

    def on_services_resolved(self, client):
        print("Services resolved")
        self.services_discovered = True

# Usage
my_client = MyBleakClient()
loop = asyncio.get_event_loop()
loop.run_until_complete(my_client.connect_and_interact())
