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