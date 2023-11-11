package com.axio.melonplatformkit;

import java.util.HashSet;

class g implements Runnable {
  g(DeviceHandle.Peripheral paramPeripheral, HashSet paramHashSet, DeviceHandle paramDeviceHandle) {}
  
  public void run() {
    for (IDeviceManagerListener iDeviceManagerListener : this.a)
      iDeviceManagerListener.onDeviceConnecting(this.b); 
  }
}


/* Location:              /home/tb/repos/openmelon/src/MPK-Decompile/classes/!/com/axio/melonplatformkit/g.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */