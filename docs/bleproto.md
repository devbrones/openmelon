# Melon Headband ble protocol reverse engineering

The Melon Headband communicates with the users device using BLE. All communication from and to the band is done using nRF:s UART over BLE functionality. This is exposed over one service with two characteristics:

```
SERVICE: 6e400001-b5a3-f393-e0a9-e50e24dcca9e | Nordic UART Service
- CHARACTERISTIC: 6e400003-b5a3-f393-e0a9-e50e24dcca9e | Nordic UART RX
- CHARACTERISTIC: 6e400002-b5a3-f393-e0a9-e50e24dcca9e | Nordic UART TX
```

Once a client connects to the Headband, it starts listening on the TX line for the initialisation command ```DW0308```, as is defined in the decompiled Java class available in ```/src/decomp/classes/i.java```.
```
SamsungE_xx:xx:xx (Samsung Galaxy S5) | xx:xx:xx:xx:xx:xx (Melon_XXXXXXXX)	ATT	15	Sent Write Command, Handle: 0x0011 (Nordic UART Service: Nordic UART Tx)
    0000   02 40 00 0d 00 09 00 04 00 52 11 00 44 57 30 33   .@.......R..DW03
    0010   30 38                                             08

```
The band is now initialised and ready to receive instructions. No response is sent back to the client.\
The client now sends the "Start Stream" command ```S01```, as is defined in the decompiled Java class available in ```/src/decomp/classes/j.java```.
```
SamsungE_xx:xx:xx (Samsung Galaxy S5) | xx:xx:xx:xx:xx:xx (Melon_XXXXXXXX)	ATT	15	Sent Write Command, Handle: 0x0011 (Nordic UART Service: Nordic UART Tx)
    0000   02 40 00 0a 00 06 00 04 00 52 11 00 53 30 31      .@.......R..S01

```

The band immediately starts spitting out the raw data on the RX line.\
Example packet:

```
xx:xx:xx:xx:xx:xx (Melon_XXXXXXXX)	SamsungE_xx:xx:xx (Samsung Galaxy S5)	ATT	32	Rcvd Handle Value Notification, Handle: 0x000e (Nordic UART Service: Nordic UART Rx)
    0000   02 40 20 1b 00 17 00 04 00 1b 0e 00 a0 f8 c9 07   .@ .............
    0010   45 d2 7f 46 c9 08 51 d2 7e a6 c9 05 11 d2 7f 2a   E..F..Q.~......*

```

TO BE CONTINUED... 

## Data format
