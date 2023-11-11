package com.axio.melonplatformkit;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

class b extends BluetoothGattCallback {
  b(DeviceHandle.Peripheral paramPeripheral) {}
  
  public void onConnectionStateChange(BluetoothGatt paramBluetoothGatt, int paramInt1, int paramInt2) {
    Log.d("DeviceHandle", "connected=2 connecting=1 disconnected=0 newState=" + paramInt2 + " status=" + paramInt1);
    if (paramInt1 == 0) {
      if (paramInt2 == 2) {
        DeviceHandle.Peripheral.a(this.a, DeviceHandle.DeviceState.CONNECTED);
        Log.i("DeviceHandle", "connected Stopping scan for new connection");
        DeviceHandle.Peripheral.a(this.a);
        DeviceManager.getManager().stopScan();
        Log.i(DeviceHandle.Peripheral.b(this.a), "Attempting to start service discovery:" + DeviceHandle.Peripheral.c(this.a));
        if (!paramBluetoothGatt.discoverServices())
          Log.e(DeviceHandle.Peripheral.b(this.a), "Failed to start service discovery:" + DeviceHandle.Peripheral.c(this.a)); 
      } else if (paramInt2 == 0) {
        DeviceHandle.Peripheral.d(this.a);
        Log.i("DeviceHandle", "Device Disconnected, restarting scan...");
        DeviceHandle.Peripheral.e(this.a).close();
        DeviceHandle.Peripheral.a(this.a, DeviceHandle.DeviceState.DISCONNECTED);
      } else if (paramInt2 == 1) {
        Log.i("DeviceHandle", "Device Connecting");
        DeviceHandle.Peripheral.f(this.a);
        DeviceHandle.Peripheral.a(this.a, DeviceHandle.DeviceState.CONNECTING);
      } 
    } else {
      Log.i(DeviceHandle.Peripheral.b(this.a), "Device unknow status " + paramInt2);
      DeviceHandle.Peripheral.e(this.a).disconnect();
      DeviceHandle.Peripheral.e(this.a).close();
      DeviceHandle.Peripheral.g(this.a);
      DeviceHandle.Peripheral.a(this.a, DeviceHandle.DeviceState.DISCONNECTED);
    } 
  }
  
  public void onServicesDiscovered(BluetoothGatt paramBluetoothGatt, int paramInt) {
    if (paramInt == 0) {
      BluetoothGattService bluetoothGattService = paramBluetoothGatt.getService(DeviceHandle.Peripheral.h(this.a));
      if (bluetoothGattService != null) {
        Log.i(DeviceHandle.Peripheral.b(this.a), "Found correct services!");
        if (DeviceHandle.Peripheral.i(this.a))
          DeviceHandle.Peripheral.j(this.a); 
      } 
    } else {
      Log.i(DeviceHandle.Peripheral.b(this.a), "onServicesDiscovered received: " + paramInt);
    } 
  }
  
  public void onCharacteristicRead(BluetoothGatt paramBluetoothGatt, BluetoothGattCharacteristic paramBluetoothGattCharacteristic, int paramInt) {
    if (paramInt == 0);
  }
  
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
}


/* Location:              /home/tb/repos/openmelon/src/MPK-Decompile/classes/!/com/axio/melonplatformkit/b.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */