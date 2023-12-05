# Melon Headband ble protocol reverse engineering

NOV 11 2023

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

<hr>

## Data format

NOV 12 2023

According to this post, found on the Something Awful forums, the data is formatted as follows:

![forum-1](/docs/resources/forum-1.png)

>\- the hardware uses BTLE and like all things that uses BTLE it implements everything wrong, so if you e.g. try to ask for GATT characteristic descriptors it hangs and you have to hard-reset it
>
>\- all its doing with BTLE is using it as a crappy bidirectional serial port, which it then uses to implement its own protocol
>
>\- that protocol involves sending it magic strings like "DW0308" which I think is some kind of init command, you have to send it before doing anything else, or "DR0401" which returns the battery voltage, a feature BTLE supports as standard but they didn't implement
>
>\- most important though is command "S01", which starts it sending data. as far as i can tell all it does is take raw 24-bit ADC readings from the two probes (it has a left and right channel) at a non-changeable sample rate of 250Hz, pack them into 18 bytes (three samples per message, each sample is a [left, right] pair of 24-bit readings) and send em' off
>
>\- the SDK decodes the bytes back into ints, turns those ints into floating point numbers by multiplying by a seemingly-arbitrary conversion factor


I wont pay 10$ to get access to the forum, so I'll have to work out what is going on myself. The post however establishes some important facts about the protocol, we already knew that it communicates over the standard nRF UART implementation. 


Figuring out how the SDK communicates with the device was fairly easy. looking through all the decompiled classes until I found the one that contained the DW0308 command and then working back from there. Finding the one that was responsible for recieving data came soon after. 

```java
private boolean a(byte[] param1ArrayOfbyte) { // send command to device
      if (this.m == null)
        return false; 
      BluetoothGattService bluetoothGattService = this.m.getService(this.e); // UART service
      if (bluetoothGattService == null)
        return false; 
      BluetoothGattCharacteristic bluetoothGattCharacteristic = bluetoothGattService.getCharacteristic(this.f); // TX characteristic
      if (bluetoothGattCharacteristic == null)
        return false; 
      bluetoothGattCharacteristic.setValue(param1ArrayOfbyte); 
      boolean bool = this.m.writeCharacteristic(bluetoothGattCharacteristic);
      Log.w(this.i, "write TXchar - status=" + bool);
      return bool;
    }
```
```java
private boolean t() { // recieve data?
      if (this.m == null)
        return false; 
      if (this.p == DeviceHandle.DeviceState.CONNECTING)
        try {
          Thread.sleep(1000L);
        } catch (InterruptedException interruptedException) {
          interruptedException.printStackTrace();
        }  
      BluetoothGattService bluetoothGattService = this.m.getService(this.e); // UART service
      if (bluetoothGattService == null) {
        Log.e(this.i, "registerForTXNotifications:BluetoothGattService:s = NULL");
        return false;
      } 
      BluetoothGattCharacteristic bluetoothGattCharacteristic = bluetoothGattService.getCharacteristic(this.g); // RX characteristic
      if (bluetoothGattCharacteristic == null) {
        Log.e(this.i, "registerForTXNotifications:BluetoothGattCharacteristic:tx = NULL");
        return false;
      } 
      null = this.m.setCharacteristicNotification(bluetoothGattCharacteristic, true);
      BluetoothGattDescriptor bluetoothGattDescriptor = bluetoothGattCharacteristic.getDescriptor(this.d); // Client Characteristic Configuration
      bluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
      return this.m.writeDescriptor(bluetoothGattDescriptor);
    }

```

My eyes are now bleeding after having had to read through hundreds or thousands of lines of stupid fucking coffee language. :goberserk:\
And i still dont understand a thing :,)

At least i got the serial console to work! (see ```src/tests/sercons.py```). After entering DW0803 followed by S01, and after a moment S00 to close the stream, data is dumped as hex format. like this: (highlighted for your viewing pleasure)

![hexdump](/docs/resources/hexdump.png)

>**Note**
>Since the Headband dumps data into the serial console at 250 times per second, it tends to fill up your terminal instantly...

i have tried to find the function that decodes this string into useable floats (the multiply by arbitrary value thing mentioned in the post), but i cant figure out which one it is since the decomp has marked some functions with the same letter (but different return types) so going through each one is just not practical. I have also tried to decode some of the functions using ChatGPT / Copilot but to little success.




```java
public void onCharacteristicChanged(BluetoothGatt paramBluetoothGatt, BluetoothGattCharacteristic paramBluetoothGattCharacteristic) {
    byte[] arrayOfByte = paramBluetoothGattCharacteristic.getValue();
    short[] arrayOfShort = new short[arrayOfByte.length];
    for (byte b1 = 0; b1 < arrayOfByte.length; b1++)
      arrayOfShort[b1] = (short)(arrayOfByte[b1] & 0xFF); 
    if (arrayOfByte != null)
      if (arrayOfByte.length == 20 && arrayOfByte[0] == -96) {
        DeviceHandle.Peripheral.k(this.a).a(arrayOfShort);
        S3File.getInstance();
        if (S3File.isInitialized())
          S3File.getInstance().writeBytesToS3File(arrayOfByte); 
      } else {
        try {
          String str = new String(arrayOfByte, "UTF-8");
          if (str.length() == 4) {
            DeviceHandle.Peripheral.a(this.a, str.substring(0, str.length() - 2));
            DeviceHandle.Peripheral.l(this.a);
          } 
        } catch (Exception exception) {}
      }  
  }
```

```java
private boolean u() {
      byte[] arrayOfByte = this.t;
      ArrayList<UUID> arrayList = new ArrayList();
      String str = null;
      ByteBuffer byteBuffer = ByteBuffer.wrap(arrayOfByte).order(ByteOrder.LITTLE_ENDIAN);
      while (byteBuffer.remaining() > 2) {
        byte[] arrayOfByte1;
        byte b1 = byteBuffer.get();
        if (b1 == 0)
          break; 
        byte b2 = byteBuffer.get();
        switch (b2) {
          case 2:
          case 3:
            while (b1 >= 2) {
              arrayList.add(UUID.fromString(String.format("%08x-0000-1000-8000-00805f9b34fb", new Object[] { Short.valueOf(byteBuffer.getShort()) })));
              b1 = (byte)(b1 - 2);
            } 
            continue;
          case 6:
          case 7:
            while (b1 >= 16) {
              long l1 = byteBuffer.getLong();
              long l2 = byteBuffer.getLong();
              arrayList.add(new UUID(l2, l1));
              b1 = (byte)(b1 - 16);
            } 
            continue;
          case 9:
            arrayOfByte1 = new byte[b1 - 1];
            byteBuffer.get(arrayOfByte1);
            try {
              str = new String(arrayOfByte1, "utf-8");
            } catch (UnsupportedEncodingException unsupportedEncodingException) {
              unsupportedEncodingException.printStackTrace();
            } 
            continue;
        } 
        byteBuffer.position(byteBuffer.position() + b1 - 1);
      } 
      if (str != null) {
        a(str);
        return true;
      } 
      return false;
    }
```

```java
private void h() {
      String str = this.q;
      Long long_ = Long.decode("0x" + str.toLowerCase());
      int i = long_.intValue();
      float f1 = 3.6F;
      float f2 = 3.956F;
      float f3 = 4.7F;
      float f4 = i * 2.0F / 100.0F;
      float f5 = (f4 - f1) / (f2 - f1);
      float f6 = Math.min(1.0F, Math.max(0.0F, f5));
      this.v = Float.valueOf(f4);
      this.u = Float.valueOf(f6);
    }
```

```java
private boolean u() {
      byte[] arrayOfByte = this.t;
      ArrayList<UUID> arrayList = new ArrayList();
      String str = null;
      ByteBuffer byteBuffer = ByteBuffer.wrap(arrayOfByte).order(ByteOrder.LITTLE_ENDIAN);
      while (byteBuffer.remaining() > 2) {
        byte[] arrayOfByte1;
        byte b1 = byteBuffer.get();
        if (b1 == 0)
          break; 
        byte b2 = byteBuffer.get();
        switch (b2) {
          case 2:
          case 3:
            while (b1 >= 2) {
              arrayList.add(UUID.fromString(String.format("%08x-0000-1000-8000-00805f9b34fb", new Object[] { Short.valueOf(byteBuffer.getShort()) })));
              b1 = (byte)(b1 - 2);
            } 
            continue;
          case 6:
          case 7:
            while (b1 >= 16) {
              long l1 = byteBuffer.getLong();
              long l2 = byteBuffer.getLong();
              arrayList.add(new UUID(l2, l1));
              b1 = (byte)(b1 - 16);
            } 
            continue;
          case 9:
            arrayOfByte1 = new byte[b1 - 1];
            byteBuffer.get(arrayOfByte1);
            try {
              str = new String(arrayOfByte1, "utf-8");
            } catch (UnsupportedEncodingException unsupportedEncodingException) {
              unsupportedEncodingException.printStackTrace();
            } 
            continue;
        } 
        byteBuffer.position(byteBuffer.position() + b1 - 1);
      } 
      if (str != null) {
        a(str);
        return true;
      } 
      return false;
    }
```

```java
class y {
  private ArrayList d = new ArrayList();
  
  private short[] e = new short[18];
  
  final float a = 2.4F;
  
  final float b = 6.0F;
  
  final float c = 0.4F / ((float)Math.pow(2.0D, 23.0D) - 1.0F) * 1000000.0F;
  
  protected void a(short[] paramArrayOfshort) {
    System.arraycopy(paramArrayOfshort, 2, this.e, 0, 18);
    int[] arrayOfInt1 = new int[2];
    int[] arrayOfInt2 = new int[2];
    int[] arrayOfInt3 = new int[2];
    byte b1 = 0;
    for (byte b2 = 0; b1 < 18; b2++) {
      for (byte b = 0; b < 2; b++) {
        short[] arrayOfShort = new short[3];
        int i;
        for (i = 0; i < 3; i++)
          arrayOfShort[i] = this.e[b1 + i + b * 3]; 
        i = arrayOfShort[0] & 0xFF;
        int j = arrayOfShort[1] & 0xFF;
        int k = arrayOfShort[2] & 0xFF;
        int m = (i << 16) + (j << 8) + k;
        if (!b2) {
          arrayOfInt1[b] = m;
        } else if (b2 == 1) {
          arrayOfInt2[b] = m;
        } else if (b2 == 2) {
          arrayOfInt3[b] = m;
        } 
      } 
      b1 += 6;
    } 
    float[] arrayOfFloat1 = a(arrayOfInt1);
    float[] arrayOfFloat2 = a(arrayOfInt2);
    float[] arrayOfFloat3 = a(arrayOfInt3);
    synchronized (this.d) {
      for (SignalAnalyzer signalAnalyzer : this.d) {
        signalAnalyzer.enqeueSample(arrayOfFloat1, true);
        signalAnalyzer.enqeueSample(arrayOfFloat2, true);
        signalAnalyzer.enqeueSample(arrayOfFloat3, false);
      } 
    } 
  }
  
  public void a(SignalAnalyzer paramSignalAnalyzer) {
    synchronized (this.d) {
      if (this.d.contains(paramSignalAnalyzer))
        return; 
      this.d.add(paramSignalAnalyzer);
      paramSignalAnalyzer.startAnalyzerThread();
    } 
  }
  
  private float[] a(int[] paramArrayOfint) {
    int i = paramArrayOfint.length;
    float[] arrayOfFloat = new float[i];
    for (byte b = 0; b < i; b++) {
      float f1 = paramArrayOfint[b];
      float f2 = f1 * this.c;
      arrayOfFloat[b] = f2;
    } 
    return arrayOfFloat;
  }
}
```


<hr>

## Reverse engineering and understanding the class y

DEC 1 2023


>If we consider the byte to be treated as unsigned (i.e., ranging from 0 to 255 instead of -128 to 127 in Java's signed byte
>representation), then the value 160 (0xA0 in hexadecimal) would indeed be interpreted as 160 in an unsigned context.

>In this case, the condition arrayOfByte[0] == -96 is evaluating whether the first element of arrayOfByte is equal to -96
>when treated as an unsigned byte.

>Given that the value at arrayOfByte[0] is 160 and we're interpreting it as an unsigned byte, the check -96 would correspond
>to the same binary representation as 160 in an unsigned context. Hence, the condition arrayOfByte[0] == -96 would pass when
>treating the byte array as unsigned bytes.

kill me

i have been looking at the class y for a while now, and i think i have figured out how it works.\
The class y is responsible for decoding the raw data from the headband into floating point numbers.\


```java
protected void conversionMethod(short[] rawSamples) { // where raw sample is the 20 byte array, with the first 2 bytes removed so it is 18 bytes long
    int[] Sample1 = new int[2];
    int[] Sample2 = new int[2];
    int[] Sample3 = new int[2];
    byte skipBit = 0;
    for (byte i = 0; skipBit < 18; i++) { // use skipBit to iterate through the 18 hex values in the array
      for (byte j = 0; j < 2; j++) { // iterate throught the 3 samples

        short[] tempSample = new short[3];
        for (int loop = 0; loop < 3; loop++) // iterate through the 3 bytes in each sample
          tempSample[loop] = rawSamples[skipBit + loop + j * 3]; // 
        
        int val1 = tempSample[0] & 0xFF; // convert to unsigned int
        int val2 = tempSample[1] & 0xFF; // 
        int val3 = tempSample[2] & 0xFF; // 
        int sampleValue = (val1 << 16) + (val2 << 8) + val3; // combine the 3 bytes into one int


        if (i == 0) {    // write to the right sample array
          Sample1[j] = sampleValue;
        } else if (i == 1) {
          Sample2[j] = sampleValue;
        } else if (i == 2) {
          Sample3[j] = sampleValue;
        } 
      } 
      skipBit += 6;
    } 
    float[] finalSample1 = multiplier(Sample1); // convert the int arrays to float arrays
    float[] finalSample2 = multiplier(Sample2); //
    float[] finalSample3 = multiplier(Sample3); //
}

private float[] multiplier(int[] sample) {
  float[] finalSample = new float[i];
  for (byte i = 0; i < sample.length; i++) {
    finalSample[i] = sample[i] * 0.4F / ((float)Math.pow(2.0D, 23.0D) - 1.0F) * 1000000.0F; // multiply by 0.4 / 2^23 - 1 * 1000000, why may you ask? I have no idea. EDIT: it could be a machine epsilon funcion!
  } 
  return finalSample;
}
```

<hr>

## Bit-shift mysteries and odd binary logic

DEC 5 2023


Finally some good fucking progress!

Sample rate has been 100% confirmed. Did a recording of 100 seconds, which gave 8349 packets, so 83.49 packets/sec. 83.49 multiplied by the amounts of samples (3 samples) yields a sample rate of ≈ 250 samples/second or 250 Hz rate!


![i cant annotate this image im sorry](/docs/resources/yipee.png)

Took a closer look at the raw hex data and noticed something very silly. The first 2 bytes (marked yellow) are of course omitted, as we can see in this code (un-reverese-engineered ```y.java```)
```java
protected void a(short[] paramArrayOfshort) {
    System.arraycopy(paramArrayOfshort, 2, this.e, 0, 18);
```
then we can see quite easily that the data is segmented into six parts (3 samples per 2 channels, each sample is marked as a group of "orange,red,green"). where the two first bytes (orange and red) are simply counting up from some initial value to ```0xffff``` and then rolls over. The values (or value rather since it really is a 2 byte value) is incremented every 28-29 values, so 3 times per second, is my best guess. This is most likely done to keep track of the time, since the sample rate may fluctuate a bit. The green values are the actual samples themselves!

In the 
```java 
conversionMethod()
``` 
function shown above:

```java
int sampleValue = (val1 << 16) + (val2 << 8) + val3;  // combine the 3 bytes into one int
// It will become a 24 bit integer, BUT IT IS NOT!
// T represents timestamp part 1 and U represents timestamp part 2 and S represents sample value
// TTTTTTTTUUUUUUUUSSSSSSSS
// The sample is hence only located in the last 8 bits!
```

Each 3 byte block is parsed as following:
- create new int to store the data
- sign the bytes as unsigned ints
- add the first byte (Timestamp 1) shifted 16 bits to the left  | ```TTTTTTTT0000000000000000```
- add the second byte (Timestamp 2) shifted 8 bits to the left  | ```TTTTTTTTUUUUUUUU00000000```
- add the sample byte (Sample) to the int                       | ```TTTTTTTTUUUUUUUUSSSSSSSS```

The sample is hence only located in the last 8 bits! So what we really want to do before multiplying the sample with the 
```java
multiplier()
```
method is to just keep the last 8 bits of the int, i wonder if this is what the original code does?

```java
private float[] multiplier(int[] sample) {
  float[] finalSample = new float[sample.length]; // set up the float array
  for (byte i = 0; i < sample.length; i++) { // iterate through the sample array
    finalSample[i] = sample[i] * 0.4F / ((float)Math.pow(2.0D, 23.0D) - 1.0F) * 1000000.0F; // multiply by 0.4 / 2^23 - 1 * 1000000, why may you ask? I have no idea.
  } 
  return finalSample;
}
```

lets take a closer look at the fourth row of code:
```java 
finalSample[i] = sample[i] * 0.4F / ((float)Math.pow(2.0D, 23.0D) - 1.0F) * 1000000.0F;
```
it takes the sample int (remember, 24 bit representation of a 8 bit value, where the 16 higher bits is just a incrementor) and does the following:

$$sampleFloat=\frac{sampleInt \cdot 0.4}{2^{23}-1}\cdot 1\mathrm{e}{6}$$

Very interesting, i have absolutely no clue as to what this could do.

I think im going to just bitwise and the value by 0xff and see what happens.



