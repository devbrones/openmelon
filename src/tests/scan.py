import asyncio
from bleak import BleakScanner, BleakClient

async def subscribe_nordic_uart_rx(address, loop):
    async with BleakClient(address, loop=loop) as client:
        try:
            if not client.is_connected:
                await client.connect()

            # Set up notification handler for the Nordic UART RX characteristic
            def notification_handler(sender, data):
                print(f"Nordic UART RX notification received: {data}")

            # Subscribe to notifications for the Nordic UART RX characteristic
            await client.start_notify("6e400003-b5a3-f393-e0a9-e50e24dcca9e", notification_handler)
            
            # Keep the script running to receive notifications
            while True:
                await asyncio.sleep(1)

        except Exception as e:
            print(f"Failed to subscribe to Nordic UART RX: {e}")

async def scan_and_subscribe_nordic_uart_rx():
    scanner = BleakScanner()
    devices = await scanner.discover()

    for device in devices:
        if "melon" in device.name.lower():
            print(f"Found device: {device.name}, address: {device.address}")
            loop = asyncio.new_event_loop()
            asyncio.set_event_loop(loop)
            try:
                await subscribe_nordic_uart_rx(device.address, loop)
            except Exception as e:
                print(f"Failed to subscribe to Nordic UART RX for {device.name}: {e}")
            loop.close()

loop = asyncio.get_event_loop()
loop.run_until_complete(scan_and_subscribe_nordic_uart_rx())

