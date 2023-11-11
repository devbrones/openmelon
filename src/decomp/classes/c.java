package com.axio.melonplatformkit;

import android.util.Log;
import java.util.HashSet;

class c implements Runnable {
  c(DeviceHandle.Peripheral paramPeripheral, HashSet paramHashSet, DeviceHandle paramDeviceHandle) {}
  
  public void run() {
    String str = DeviceHandle.Peripheral.m(this.c);
    if (str == null)
      str = "???"; 
    Log.i(DeviceHandle.Peripheral.b(this.c), "Device is ready: " + str);
    q.a().a(new d(this));
  }
}


/* Location:              /home/tb/repos/openmelon/src/MPK-Decompile/classes/!/com/axio/melonplatformkit/c.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */