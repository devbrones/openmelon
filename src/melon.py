class Melon:
    ADDRESS: str        = ""                                       #
    NRFSERVICEUUID: str = "6e400001-b5a3-f393-e0a9-e50e24dcca9e"   #
    NRFRXCHARUUID: str  = "6e400003-b5a3-f393-e0a9-e50e24dcca9e"   # Set up the UUIDs for the Melon Headband
    NRFTXCHARUUID: str  = "6e400002-b5a3-f393-e0a9-e50e24dcca9e"   #
    
    INITCMD: str        = "DW0308"                                 #
    STARTCMD: str       = "S01"                                    # Set up the commands for the Melon Headband
    STOPCMD: str        = "S00"                                    #
    GETBATT: str        = "DR0401"                                 #
    
    POLLINGRATE: int    = 250                                      # The polling rate is 250Hz
    CHANNELS: [str,str] = ["Fp1", "Fp2"]                           
    SAMPLEMODE: str     = "full"                                   # "full" for 24 bit sample including timestamp, 
                                                                                # "tiny" for 8 bit sample without timestamp, 
                                                                                # "raw" is like "tiny" but without applied multiplier
    BATTERY: int        = 0                                        # Initialize the battery variable
    lastSample: str     = ""
    connected: bool     = False                                    # Initialize the connected variable
    streaming: bool     = False                                    # Initialize the streaming variable

    



