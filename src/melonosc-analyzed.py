import simplepyble
import time
import sys
import getopt
from constants import Melon
from packet_parser import parse
from pythonosc import udp_client
from pylsl import StreamInfo, StreamOutlet


class Connection:
    port = 9000
    server = "127.0.0.1"

# Create an LSL stream outlet
info = StreamInfo('MelonEEG_Stream',        # Name of the stream
                  'EEG',                    # Type of data
                  len(Melon.CHANNELS),      # Number of channels
                  Melon.POLLINGRATE,        # Sampling rate
                  'float32',                # Data type
                  'MelonDevice')            # Device name

outlet = StreamOutlet(info)

# Create an OSC client
#client = udp_client.SimpleUDPClient(Connection.server, Connection.port)


def handle_data(value):
    sample1, sample2, sample3 = parse(value.hex())  # Parse the packet
    parsed_data = [sample1, sample2, sample3]
    if debug:
        print(parsed_data)
    for sample in parsed_data:
        outlet.push_sample(sample)

# Initialize the adapter
adapters = simplepyble.Adapter.get_adapters()
if not adapters:
    raise Exception("No Bluetooth adapter found")

print("Found the following Bluetooth adapters:")
for i, adapter in enumerate(adapters):
    print(f"{i}: {adapter.identifier()} [{adapter.address()}]")
print("")
retry = False
bladapt = None
debug = False

# list of command line arguments
argumentList = sys.argv[1:]

# Options
options = "dha:rp:s:"

# Long options
long_options = ["debug","help", "adapter", "retry", "port", "server"]
try:
    # Parsing argument
    arguments, values = getopt.getopt(argumentList, options, long_options)
    
    # checking each argument
    for currentArgument, currentValue in arguments:

        if currentArgument in ("-d", "--debug"):
            print ("Enabling Debug Mode")
            debug = True

        elif currentArgument in ("-h", "--help"):
            print ("Displaying Help")
            
        elif currentArgument in ("-a", "--adapter"):
            # get adapter by identifier (integer)
            bladapt = adapters[int(currentValue)]
            
        elif currentArgument in ("-r", "--retry"):
            retry = True

        elif currentArgument in ("-p", "--port"):
            Connection.port = int(currentValue)
            #client = udp_client.SimpleUDPClient(Connection.server, Connection.port)
        
        elif currentArgument in ("-s", "--server"):
            Connection.server = currentValue
            #client = udp_client.SimpleUDPClient(Connection.server, Connection.port)

except getopt.error as err:
    print("Unknown argument error:", str(err))

if not bladapt:
    bladapt = adapters[0]

if debug:
    print(f"Using adapter: {bladapt.identifier()} [{bladapt.address()}]\n")

try:
    bladapt.scan_for(5000)  # Scan for 10 seconds
    peripherals = bladapt.scan_get_results()
    
    melon_device = None
    for peripheral in peripherals:
        if peripheral.address() == Melon.ADDRESS.upper():
            if debug:
                print("Found Melon Headband\n")
            melon_device = peripheral
            break

    if not melon_device:
        Melon.connected = False
        raise Exception(f"Could not find Melon Headband: {Melon.ADDRESS}\n")

    melon_device.connect()
    Melon.connected = True
    print("Connected to Melon Headband\n")

    try:
        melon_device.notify(Melon.NRFSERVICEUUID, Melon.NRFRXCHARUUID, handle_data)
        if debug:
            print("Subscribed to nordicUART recieve characteristic\n")

    except Exception as e:
        print(f"Could not subscribe to nordicUART: {e}\n")
        melon_device.disconnect()
        Melon.connected = False
        raise e
        #exit()
    try:
        melon_device.write_request(Melon.NRFSERVICEUUID, Melon.NRFTXCHARUUID, bytes(Melon.INITCMD.encode('ASCII')))
        melon_device.write_request(Melon.NRFSERVICEUUID, Melon.NRFTXCHARUUID, bytes(Melon.STARTCMD.encode('ASCII')))

    except Exception as e:
        print(f"Could not write to nordicUART: {e}\n")
        melon_device.disconnect()
        Melon.connected = False
        raise e
        #exit()
    while True:
        time.sleep(10)
finally:
    if melon_device:
        melon_device.write_request(Melon.NRFSERVICEUUID, Melon.NRFTXCHARUUID, bytes(Melon.STOPCMD.encode('ASCII')))
        melon_device.disconnect()
