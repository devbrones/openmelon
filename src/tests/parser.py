class SignalAnalyzer:
    def __init__(self):
        self.d = []
        self.rawsamplebytes = [0] * 18
        self.a = 2.4
        self.b = 6.0
        self.c = 0.4 / (2.0 ** 23 - 1) * 1000000

    def enqeueSample(self, sample, value):
        self.queue.append((sample, value))

    def convertSamples(self, rawData: bytearray):
        self.rawsamplebytes[0:18] = rawData[2:20]  # Assuming the indexing matches
        sample1, sample2, sample3 = [], [], []

        for b1 in range(0, 18, 3):  # every 3 hex is a sample
            for b in range(2):  # 2 samples per 3 hex
                arrayOfShort = self.rawsamplebytes[b1:b1 + 3]  # read 3 hex 
                i = arrayOfShort[0] & 0xFF
                j = arrayOfShort[1] & 0xFF
                k = arrayOfShort[2] & 0xFF
                m = (i << 16) + (j << 8) + k

                if b == 0:
                    sample1.append(m)
                elif b == 1:
                    sample2.append(m)
                else:
                    sample3.append(m)

        sample1 = self.multiplier(sample1)
        sample2 = self.multiplier(sample2)
        sample3 = self.multiplier(sample3)

        return [sample1, sample2, sample3]

    def multiplier(self, paramArrayOfint):
        return [x * self.c for x in paramArrayOfint]

# Modify your onCharacteristicChanged method to utilize the updated 'y' class
def onCharacteristicChanged(paramBluetoothGatt, paramBluetoothGattCharacteristic):
    arrayOfByte = paramBluetoothGattCharacteristic.getValue()
    arrayOfShort = [b & 0xFF for b in arrayOfByte]
    
    if arrayOfByte and len(arrayOfByte) == 20 and arrayOfByte[0] == -96:
        peripheral = y()
        peripheral.a(arrayOfShort)
        writeBytesToS3File(arrayOfByte)  # Simulating the write function
    else:
        try:
            str = arrayOfByte.decode("utf-8")
            if len(str) == 4:
                a(this.a, str[0:len(str) - 2])
                h(this.a)
        except Exception as e:
            pass


def parseData(rxbytes: bytearray):
    if not rxbytes:
        return
    else:
        test = [b & 0xFF for b in rxbytes]

        print(test[0])
        if len(rxbytes) == 20 and rxbytes[0] == -96:
            peripheral = y()
            peripheral.convertSamples(rxbytes)
        else:
            print(f"Error: Invalid data received; {rxbytes}")
            
            

