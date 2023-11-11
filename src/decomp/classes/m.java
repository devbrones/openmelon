package com.axio.melonplatformkit;

import java.util.HashSet;

class m implements Runnable {
  m(DeviceManager paramDeviceManager, HashSet paramHashSet) {}
  
  public void run() {
    for (IDeviceManagerListener iDeviceManagerListener : this.a)
      iDeviceManagerListener.onDeviceScanStopped(); 
  }
}


/* Location:              /home/tb/repos/openmelon/src/MPK-Decompile/classes/!/com/axio/melonplatformkit/m.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */