import pygatt
import time
from constants import Melon
from packet_parser import parse
from pylsl import StreamInfo, StreamOutlet

adapter = pygatt.GATTToolBackend()

# Create an LSL stream outlet
info = StreamInfo('MelonEEG_Stream',        # Name of the stream
                  'EEG',                    # Type of data
                  len(Melon.CHANNELS),      # Number of channels
                  Melon.POLLINGRATE,        # Sampling rate
                  'float32',                # Data type
                  'MelonDevice')            # Device name

outlet = StreamOutlet(info)

def handle_data(h, value):
    sample1, sample2, sample3 = parse(value.hex())  # Parse the packet
    parsed_data = [sample1, sample2, sample3]
    print(parsed_data)
    for sample in parsed_data:
        outlet.push_sample(sample)

try:
    adapter.start()
    try:
        device = adapter.connect(Melon.ADDRESS,
                                 address_type=pygatt.BLEAddressType.random)
        Melon.connected = True

    except Exception as e:
        Melon.connected = False
        print("Could not connect to Melon Headband: " + Melon.ADDRESS + " because of error:\n" + str(e))
        exit()

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