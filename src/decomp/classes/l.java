package com.axio.melonplatformkit;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

class l implements BluetoothAdapter.LeScanCallback {
  l(DeviceManager paramDeviceManager) {}
  
  public void onLeScan(BluetoothDevice paramBluetoothDevice, int paramInt, byte[] paramArrayOfbyte) {
    if (paramBluetoothDevice != null)
      DeviceManager.access$100(DeviceManager.access$000(), paramBluetoothDevice, paramInt, paramArrayOfbyte); 
  }
}


/* Location:              /home/tb/repos/openmelon/src/MPK-Decompile/classes/!/com/axio/melonplatformkit/l.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */