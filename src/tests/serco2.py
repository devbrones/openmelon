import asyncio
from bleak import BleakScanner, BleakClient
import matplotlib.pyplot as plt

# Placeholder function for parsing data
def parse_data(raw_data):
    # Implement your data parsing logic here
    # Return the parsed data
    pass

received_data = []  # List to store received data

async def subscribe_nordic_uart(address, loop):
    async with BleakClient(address, loop=loop) as client:
        # Placeholder for connection logic
        # Implement your connection logic here
        print("Connected to device.")

        async def notification_handler(sender, data):
            # Parse the received data
            parsed_data = parse_data(data)
            
            # Append the parsed data to the list
            received_data.append(parsed_data)
            
            # Print the received data (for testing)
            print(f"Received: {parsed_data}") 

            # Update plot with received data
            await update_plot(received_data)  # Function to update the plot
            
        # Subscribe to notifications (replace with actual characteristic UUID)
        rx_characteristic_uuid = "00002a19-0000-1000-8000-00805f9b34fb"
        rx_characteristic = next(
            (char for char in client.services.characteristics if char.uuid == rx_characteristic_uuid),
            None
        )
        await client.start_notify(rx_characteristic, notification_handler)

        while True:
            user_input = input("Enter a command: ")
            if user_input.lower() == "exit":
                break
            else:
                tx_characteristic_uuid = "00002a19-0000-1000-8000-00805f9b34fb"
                tx_characteristic = next(
                    (char for char in client.services.characteristics if char.uuid == tx_characteristic_uuid),
                    None
                )
                await client.write_gatt_char(tx_characteristic, user_input.encode())

async def update_plot(data):
    plt.plot(data)
    plt.draw()
    plt.pause(0.1)  # Adjust the pause duration as needed

async def scan_and_subscribe_nordic_uart():
    scanner = BleakScanner()
    devices = await scanner.discover()

    for device in devices:
        if "melon" in device.name.lower():  # Replace with your device name
            print(f"Found device: {device.name}, address: {device.address}")
            loop = asyncio.new_event_loop()
            asyncio.set_event_loop(loop)
            try:
                await subscribe_nordic_uart(device.address, loop)
            except Exception as e:
                print(f"Failed to subscribe to Nordic UART for {device.name}: {e}")
            loop.close()

# Main asyncio loop to run tasks
loop = asyncio.get_event_loop()
loop.run_until_complete(scan_and_subscribe_nordic_uart())
