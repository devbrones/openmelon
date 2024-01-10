import pygatt
import time
from constants import Melon
from packet_parser import parse
from pylsl import StreamInfo, StreamOutlet

adapter = pygatt.GATTToolBackend()

# Create an LSL stream outlet
info = StreamInfo('MelonEEG_Stream',        # Name of the stream
                  'EEG',                    # Type of data
                  2,                        # Number of channels
                  250,                      # Sampling rate
                  'float32',                # Data type
                  'MelonDevice')            # Device name

outlet = StreamOutlet(info)

def handle_data(h, value):
    sample1, sample2, sample3 = parse(value.hex())  # Parse the packet
    parsed_data = [sample1, sample2, sample3]
    for sample in parsed_data:
        outlet.push_sample(sample)

try:
    adapter.start()
    device = adapter.connect('e6:4d:9b:8c:d8:53', 
                             address_type=pygatt.BLEAddressType.random)

    device.subscribe(Melon.NRFRXCHARUUID,
                     callback=handle_data)
    
    device.char_write(Melon.NRFTXCHARUUID, 
                      bytes(Melon.INITCMD.encode('ASCII')))
    
    device.char_write(Melon.NRFTXCHARUUID, 
                      bytes(Melon.STARTCMD.encode('ASCII')))
    while True:
        time.sleep(10)
finally:
    device.char_write(Melon.NRFTXCHARUUID, 
                      bytes(Melon.STOPCMD.encode('ASCII')))
    adapter.stop()