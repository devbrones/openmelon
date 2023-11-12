import asyncio
from bleak import BleakScanner, BleakClient

# Function to handle incoming notifications
def notification_handler(sender, data):
    print(f"Received: {data.decode()}")

async def connect_and_interact():
    # Scan for devices with "melon" in their name
    devices = await BleakScanner.discover()
    melon_device = next((device for device in devices if "melon" in device.name), None)

    if melon_device:
        async with BleakClient(melon_device) as client:
            print(f"Connecting to {melon_device.name}...")
            await client.connect()
            
            # Find the UART service and characteristics
            services = await client.get_services()
            uart_service = next(
                (service for service in services if "Nordic UART Service" in service.description),
                None
            )
            
            if uart_service:
                tx_characteristic = next(
                    (char for char in uart_service.characteristics if "TX" in char.description),
                    None
                )
                rx_characteristic = next(
                    (char for char in uart_service.characteristics if "RX" in char.description),
                    None
                )
                
                if tx_characteristic and rx_characteristic:
                    print("Connected to UART service. Type 'exit' to quit.")
                    
                    # Start notifications for receiving data
                    await client.start_notify(rx_characteristic, notification_handler)
                    
                    while True:
                        user_input = input("Enter a command: ")
                        if user_input.lower() == "exit":
                            break
                        else:
                            await client.write_gatt_char(tx_characteristic, user_input.encode())
                else:
                    print("UART characteristics not found.")
            else:
                print("Nordic UART Service not found.")
    else:
        print("Device with 'melon' in its name not found.")

# Run the interaction asynchronously
asyncio.run(connect_and_interact())
