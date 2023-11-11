import asyncio
from bleak import BleakScanner

class MyBleakScanner:
    async def scan_devices(self):
        devices = await BleakScanner.discover()
        return devices

# Usage
my_scanner = MyBleakScanner()
loop = asyncio.get_event_loop()
found_devices = loop.run_until_complete(my_scanner.scan_devices())
for device in found_devices:
    print(device)
