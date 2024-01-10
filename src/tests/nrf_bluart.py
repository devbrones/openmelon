import pygatt
from pygatt.exceptions import BLEError
from pylsl import StreamInfo, StreamOutlet
import threading
import time
from queue import Queue
from packet_parser import conversion_method as convert
from constants import Melon

# BLE device characteristics and commands
INIT_COMMAND = Melon.INITCMD
START_COMMAND = Melon.STARTCMD
STOP_COMMAND = Melon.STOPCMD

last_packet = None  # Variable to store the last received packet

# Create an LSL stream outlet
info = StreamInfo('MelonEEG_Stream', 'EEG', 2, 250, 'float32', 'MelonDevice') 
outlet = StreamOutlet(info)

# Callback function for handling received notifications
def handle_data(handle, value):
    global last_packet
    data = bytes(value)

    # Validate incoming packet against the last one
    if data != last_packet:
        last_packet = data
        sample1, sample2, sample3 = convert(data.hex())  # Parse the packet
        parsed_data = [sample1, sample2, sample3]
        for sample in parsed_data:
            outlet.push_sample(sample)
        
        print(f"Parsed Data: {parsed_data}")

# Function to receive notifications continuously
def receive_notifications(device):
    try:
        device.subscribe(Melon.NRFRXCHARUUID, callback=handle_data)  # Replace with your characteristic UUID
        while True:
            device.wait_for_notifications(timeout=1.0)
    except BLEError as e:
        print(f"BLE Error: {e}")
        # Handle BLE errors

# Function to find a device with "melon" in the name
def find_melon_device(adapter):
    devices = adapter.scan()  # Scan for devices for 5 seconds
    melon_devices = [device for device in devices if device['name'] and "melon" in device['name'].lower()]
    if melon_devices:
        return melon_devices[0]['address']  # Return the first melon device found
    else:
        return None

# Establish BLE connection and handle notifications
try:
    adapter = pygatt.GATTToolBackend()
    adapter.start()

    melon_device_mac = find_melon_device(adapter)
    if melon_device_mac:
        try:
            device = adapter.connect(melon_device_mac.lower())

            # Create a thread for receiving notifications
            notification_thread = threading.Thread(target=receive_notifications, args=(device,))
            notification_thread.start()

            # Send initialization command
            device.char_write_handle(Melon.NRFTXCHARUUID, INIT_COMMAND, wait_for_response=True)

            # Send start command to begin streaming
            device.char_write_handle(Melon.NRFTXCHARUUID, START_COMMAND, wait_for_response=True)

            notification_thread.join()

            device.char_write_handle(Melon.NRFTXCHARUUID, STOP_COMMAND, wait_for_response=False)

        except pygatt.exceptions.BLEError as ble_error:
            print(f"BLE Error: {ble_error}")

    else:
        print("No Melon device found.")

except pygatt.exceptions.BLEError as ble_error:
    print(f"BLE Error: {ble_error}")

finally:
    adapter.stop()  # Ensure the adapter is stopped after use
