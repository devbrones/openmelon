import pygatt
import time
from melon import Melon
from packet_parser import conversion_method as convert
from pylsl import StreamInfo, StreamOutlet

adapter = pygatt.GATTToolBackend()

# Create an LSL stream outlet
info = StreamInfo('MelonEEG_Stream', 'EEG', 2, 250, 'float32', 'MelonDevice')
outlet = StreamOutlet(info)

def handle_data(handle, value):
    sample1, sample2, sample3 = convert(value.hex())  # Parse the packet
    parsed_data = [sample1, sample2, sample3]
    print(f"Parsed Data: {parsed_data}")
    for sample in parsed_data:
        outlet.push_sample(sample)


try:
    adapter.start()
    device = adapter.connect('e6:4d:9b:8c:d8:53', address_type=pygatt.BLEAddressType.random)

    device.subscribe(Melon.NRFRXCHARUUID,
                     callback=handle_data)
    
    device.char_write(Melon.NRFTXCHARUUID, bytes(Melon.INITCMD.encode('ASCII')))
    device.char_write(Melon.NRFTXCHARUUID, bytes(Melon.STARTCMD.encode('ASCII')))

    #time.sleep(10)
    #device.char_write(Melon.NRFTXCHARUUID, bytes(Melon.STOPCMD.encode('ASCII')))

    # The subscription runs on a background thread. You must stop this main
    # thread from exiting, otherwise you will not receive any messages, and
    # the program will exit. Sleeping in a while loop like this is a simple
    # solution that won't eat up unnecessary CPU, but there are many other
    # ways to handle this in more complicated program. Multi-threaded
    # programming is outside the scope of this README.
    while True:
        time.sleep(10)
finally:
    device.char_write(Melon.NRFTXCHARUUID, bytes(Melon.STOPCMD.encode('ASCII')))
    adapter.stop()