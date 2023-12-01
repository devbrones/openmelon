import asyncio
from bleak import BleakScanner, BleakClient
import matplotlib.pyplot as plt
import packetparser as p
import csv
from threading import Thread

async def subscribe_nordic_uart(address, loop):
    async with BleakClient(address, loop=loop) as client:
        try:
            if not client.is_connected:
                await client.connect()


            async def notification_handler(sender, data):
                decoded_values = p.parseData(data)
                await save_to_csv(decoded_values)
                print(f"Received: {decoded_values}")

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

                    await client.start_notify(rx_characteristic, notification_handler)

                    while True:
                        user_input = input("Enter a command: ")
                        if user_input.lower() == "exit":
                            break
                        else:
                            await client.write_gatt_char(tx_characteristic, user_input.encode())

                else:
                    print("Nordic UART characteristics not found.")
            else:
                print("Nordic UART Service not found.")

        except Exception as e:
            print(f"Failed to subscribe to Nordic UART: {e}")

async def save_to_csv(data):
    with open('received_data.csv', 'a', newline='') as csvfile:
        csv_writer = csv.writer(csvfile)
        # Reshape the data
        result = [
            [data[i][j], data[i][j + 1], data[i][j], data[i][j + 1]]
            for i in range(len(data))
            for j in range(0, len(data[i]), 2)
            if j + 1 < len(data[i])
        ]
        for row in result:
            csv_writer.writerow(row)


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


async def main():
    tasks = [
        scan_and_subscribe_nordic_uart(),

    ]
    await asyncio.gather(*tasks)

loop = asyncio.get_event_loop()
loop.run_until_complete(main())
