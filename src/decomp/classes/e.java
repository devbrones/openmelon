package com.axio.melonplatformkit;

import java.util.HashSet;

class e implements Runnable {
  e(DeviceHandle.Peripheral paramPeripheral, HashSet paramHashSet, DeviceHandle paramDeviceHandle) {}
  
  public void run() {
    for (IDeviceManagerListener iDeviceManagerListener : this.a)
      iDeviceManagerListener.onDeviceDisconnected(this.b); 
  }
}


/* Location:              /home/tb/repos/openmelon/src/MPK-Decompile/classes/!/com/axio/melonplatformkit/e.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */