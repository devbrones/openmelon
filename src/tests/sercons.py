import asyncio
from bleak import BleakScanner, BleakClient
import parser as p

async def subscribe_nordic_uart(address, loop):
    async with BleakClient(address, loop=loop) as client:
        try:
            if not client.is_connected:
                await client.connect()

            
            """
            private void h() {
                  String str = this.q;
                  Long long_ = Long.decode("0x" + str.toLowerCase());
                  int i = long_.intValue();
                  float f1 = 3.6F;
                  float f2 = 3.956F;
                  float f3 = 4.7F;
                  float f4 = i * 2.0F / 100.0F;
                  float f5 = (f4 - f1) / (f2 - f1);
                  float f6 = Math.min(1.0F, Math.max(0.0F, f5));
                  this.v = Float.valueOf(f4);
                  this.u = Float.valueOf(f6);
                }
            """

            def decode_packet(packet: str) -> (float, float):
                if len(packet) >= 12:  # Ensure the packet has at least 12 bytes for further processing
                    try:
                        # Process the packet based on its structure
                        byte_data = bytes.fromhex(packet)
                        if len(byte_data) >= 6:
                            samples = [byte_data[i:i+3] for i in range(0, len(byte_data), 3)]

                            values = []
                            for sample in samples:
                                if len(sample) == 3:
                                    val = (sample[0] << 16) | (sample[1] << 8) | sample[2]
                                    # Apply the conversion to float
                                    f_val = (val * 0.4) / ((1 << 23) - 1) * 1000000.0
                                    values.append(f_val)

                            if len(values) >= 2:
                                return tuple(values[:2])  # Returning the first two values as floats

                    except Exception as e:
                        print(f"Error decoding packet: {e}")

                return 0.0, 0.0  # Return default values in case of errors



            # Set up notification handler for the Nordic UART RX characteristic
            def notification_handler(sender, data):
                hex_data = " ".join(f"{byte:02x}" for byte in data)
                print(f"{data}")

                # Decode received data into floating-point values
                decoded_values = p.parseData(data)
                #print("Decoded values:", decoded_values)

            # Find the Nordic UART service and its characteristics
            services = await client.get_services()
            uart_service = next(
                (service for service in services if service.uuid == "6e400001-b5a3-f393-e0a9-e50e24dcca9e"),
                None
            )

            if uart_service:
                rx_characteristic = next(
                    (char for char in uart_service.characteristics if char.uuid == "6e400003-b5a3-f393-e0a9-e50e24dcca9e"),
                    None
                )
                tx_characteristic = next(
                    (char for char in uart_service.characteristics if char.uuid == "6e400002-b5a3-f393-e0a9-e50e24dcca9e"),
                    None
                )

                if rx_characteristic and tx_characteristic:
                    print("Connected to Nordic UART Service.")
                    print("Type 'exit' to quit.")

                    # Subscribe to notifications for the Nordic UART RX characteristic
                    await client.start_notify(rx_characteristic, notification_handler)

                    # Run the sending commands and receiving notifications concurrently
                    await asyncio.gather(
                        send_commands(client, tx_characteristic),
                        receive_notifications()
                    )

                else:
                    print("Nordic UART characteristics not found.")
            else:
                print("Nordic UART Service not found.")

        except Exception as e:
            print(f"Failed to subscribe to Nordic UART: {e}")

async def send_commands(client, tx_characteristic):
    while True:
        user_input = input("Enter a command: ")
        if user_input.lower() == "exit":
            break
        else:
            await client.write_gatt_char(tx_characteristic, user_input.encode())

async def receive_notifications():
    while True:
        await asyncio.sleep(1)  # Adjust this time interval if needed

async def scan_and_subscribe_nordic_uart():
    scanner = BleakScanner()
    devices = await scanner.discover()

    for device in devices:
        if "melon" in device.name.lower():
            print(f"Found device: {device.name}, address: {device.address}")
            loop = asyncio.new_event_loop()
            asyncio.set_event_loop(loop)
            try:
                await subscribe_nordic_uart(device.address, loop)
            except Exception as e:
                print(f"Failed to subscribe to Nordic UART for {device.name}: {e}")
            loop.close()

loop = asyncio.get_event_loop()
loop.run_until_complete(scan_and_subscribe_nordic_uart())
