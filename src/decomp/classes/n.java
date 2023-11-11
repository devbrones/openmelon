package com.axio.melonplatformkit;

import java.util.HashSet;

class n implements Runnable {
  n(DeviceManager paramDeviceManager, HashSet paramHashSet) {}
  
  public void run() {
    for (IDeviceManagerListener iDeviceManagerListener : this.a)
      iDeviceManagerListener.onDeviceScanStarted(); 
  }
}


/* Location:              /home/tb/repos/openmelon/src/MPK-Decompile/classes/!/com/axio/melonplatformkit/n.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */