import asyncio
import pygatt
import matplotlib.pyplot as plt

# Function for parsing received data
def parse_data(raw_data):
    # Implement your data parsing logic here
    # For example, if receiving float values in bytes, you might do:
    parsed_data = [float(raw_data[i]) for i in range(len(raw_data))]
    return parsed_data

received_data = []  # List to store received data

def handle_data(handle, value_bytes):
    # Parse the received data
    parsed_data = parse_data(value_bytes)
    
    # Extend the list with parsed data
    received_data.extend(parsed_data)
    
    # Update plot with received data
    plt.clf()
    plt.plot(received_data)
    plt.xlabel('Time')
    plt.ylabel('Value')
    plt.title('Real-time Data Plot')
    plt.pause(0.1)  # Adjust the pause duration as needed
    plt.show(block=False)

async def list_devices():
    adapter = pygatt.GATTToolBackend(search_window_size=2048)  # Increase the search window size

    try:
        adapter.start(reset_on_start=False)
        devices = await adapter.scan()
        melon_devices = [device for device in devices if "melon" in device["name"].lower()]
        if not melon_devices:
            print("No 'melon' devices found.")
            return None
        else:
            print("Found 'melon' devices:")
            for i, device in enumerate(melon_devices):
                print(f"{i+1}. {device['name']} - {device['address']}")
            device_index = int(input("Select a device number to connect to: ")) - 1
            selected_device = melon_devices[device_index]
            return selected_device["address"]
    except KeyboardInterrupt:
        pass
    finally:
        adapter.stop()

async def subscribe_nordic_uart(address):
    adapter = pygatt.GATTToolBackend()

    try:
        adapter.start(reset_on_start=False)
        device = adapter.connect(address)
        print("Connected to device.")

        # Replace with actual characteristic UUIDs
        rx_characteristic_uuid = "00002a19-0000-1000-8000-00805f9b34fb"
        tx_characteristic_uuid = "00002a19-0000-1000-8000-00805f9b34fb"

        device.subscribe(rx_characteristic_uuid, callback=handle_data)
        
        while True:
            user_input = input("Enter a command: ")
            if user_input.lower() == "exit":
                break
            else:
                # Assuming writing data to the TX characteristic for commands
                device.char_write(tx_characteristic_uuid, bytearray(user_input.encode()))

    except pygatt.exceptions.NotConnectedError:
        print("Device not connected.")
    finally:
        adapter.stop()

async def main():
    selected_device = await list_devices()
    if selected_device:
        await subscribe_nordic_uart(selected_device)

if __name__ == "__main__":
    loop = asyncio.get_event_loop()
    loop.run_until_complete(main())
