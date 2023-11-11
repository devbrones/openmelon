package com.axio.melonplatformkit;

import java.util.HashSet;

class o implements Runnable {
  o(DeviceManager paramDeviceManager, HashSet paramHashSet, DeviceHandle paramDeviceHandle) {}
  
  public void run() {
    for (IDeviceManagerListener iDeviceManagerListener : this.a)
      iDeviceManagerListener.onDeviceFound(this.b); 
  }
}


/* Location:              /home/tb/repos/openmelon/src/MPK-Decompile/classes/!/com/axio/melonplatformkit/o.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */