import tkinter as tk
import asyncio
from bleak import BleakScanner, BleakClient

class BLEInterface:
    def __init__(self, root):
        self.root = root
        self.root.title("BLE Interface")

        self.received_data = tk.Text(self.root, height=20, width=50)
        self.received_data.pack()

        self.command_entry = tk.Entry(self.root)
        self.command_entry.pack()

        self.send_button = tk.Button(self.root, text="Send Command", command=self.send_command)
        self.send_button.pack()

        self.loop = asyncio.get_event_loop()
        self.client = None

        self.loop.run_until_complete(self.connect_to_device())

    async def connect_to_device(self):
        try:
            devices = await BleakScanner.discover()
            device = next((device for device in devices if "melon" in device.name.lower()), None)

            if device:
                self.client = BleakClient(device)
                await self.client.connect()

                self.received_data.insert(tk.END, f"Connected to {device.name}.\n")
                self.received_data.see(tk.END)

                await self.subscribe_notifications()  # Call your subscription function here

            else:
                self.received_data.insert(tk.END, "Device not found.\n")
                self.received_data.see(tk.END)
        except Exception as e:
            self.received_data.insert(tk.END, f"Failed to connect: {e}\n")
            self.received_data.see(tk.END)

    async def subscribe_notifications(self):
        try:
            services = await self.client.get_services()
            # Find and handle your service and characteristic here

            if service and characteristic:
                await self.client.start_notify(characteristic, self.handle_notifications)
                self.received_data.insert(tk.END, f"Subscribed to notifications.\n")
                self.received_data.see(tk.END)
            else:
                self.received_data.insert(tk.END, "Service or characteristic not found.\n")
                self.received_data.see(tk.END)

        except Exception as e:
            self.received_data.insert(tk.END, f"Failed to subscribe to notifications: {e}\n")
            self.received_data.see(tk.END)

    def handle_notifications(self, sender, data):
        self.received_data.insert(tk.END, f"Received: {data}\n")
        self.received_data.see(tk.END)

    def send_command(self):
        command = self.command_entry.get()
        if command and self.client and self.client.is_connected:
            try:
                # Find and handle your service and characteristic here
                await self.client.write_gatt_char(characteristic, command.encode())
                self.received_data.insert(tk.END, f"Sent command: {command}\n")
                self.received_data.see(tk.END)
            except Exception as e:
                self.received_data.insert(tk.END, f"Failed to send command: {e}\n")
                self.received_data.see(tk.END)
        else:
            self.received_data.insert(tk.END, "Device is not connected or command is empty.\n")
            self.received_data.see(tk.END)

def main():
    root = tk.Tk()
    ble_interface = BLEInterface(root)
    root.mainloop()

if __name__ == "__main__":
    main()
import tkinter as tk
import asyncio
from bleak import BleakScanner, BleakClient

class BLEInterface:
    def __init__(self, root):
        self.root = root
        self.root.title("BLE Interface")

        self.received_data = tk.Text(self.root, height=20, width=50)
        self.received_data.pack()

        self.command_entry = tk.Entry(self.root)
        self.command_entry.pack()

        self.send_button = tk.Button(self.root, text="Send Command", command=self.send_command)
        self.send_button.pack()

        self.loop = asyncio.get_event_loop()
        self.client = None

        self.loop.run_until_complete(self.connect_to_device())

    async def connect_to_device(self):
        try:
            devices = await BleakScanner.discover()
            device = next((device for device in devices if "melon" in device.name.lower()), None)

            if device:
                self.client = BleakClient(device)
                await self.client.connect()

                self.received_data.insert(tk.END, f"Connected to {device.name}.\n")
                self.received_data.see(tk.END)

                await self.subscribe_notifications()  # Call your subscription function here

            else:
                self.received_data.insert(tk.END, "Device not found.\n")
                self.received_data.see(tk.END)
        except Exception as e:
            self.received_data.insert(tk.END, f"Failed to connect: {e}\n")
            self.received_data.see(tk.END)

    async def subscribe_notifications(self):
        try:
            services = await self.client.get_services()
            # Find and handle your service and characteristic here

            if service and characteristic:
                await self.client.start_notify(characteristic, self.handle_notifications)
                self.received_data.insert(tk.END, f"Subscribed to notifications.\n")
                self.received_data.see(tk.END)
            else:
                self.received_data.insert(tk.END, "Service or characteristic not found.\n")
                self.received_data.see(tk.END)

        except Exception as e:
            self.received_data.insert(tk.END, f"Failed to subscribe to notifications: {e}\n")
            self.received_data.see(tk.END)

    def handle_notifications(self, sender, data):
        self.received_data.insert(tk.END, f"Received: {data}\n")
        self.received_data.see(tk.END)

    def send_command(self):
        command = self.command_entry.get()
        if command and self.client and self.client.is_connected:
            try:
                # Find and handle your service and characteristic here
                self.client.write_gatt_char(characteristic, command.encode())
                self.received_data.insert(tk.END, f"Sent command: {command}\n")
                self.received_data.see(tk.END)
            except Exception as e:
                self.received_data.insert(tk.END, f"Failed to send command: {e}\n")
                self.received_data.see(tk.END)
        else:
            self.received_data.insert(tk.END, "Device is not connected or command is empty.\n")
            self.received_data.see(tk.END)

def main():
    root = tk.Tk()
    ble_interface = BLEInterface(root)
    root.mainloop()

if __name__ == "__main__":
    main()
