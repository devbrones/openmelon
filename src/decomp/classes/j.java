package com.axio.melonplatformkit;

import android.util.Log;

class j implements Runnable {
  j(DeviceHandle.Peripheral paramPeripheral) {}
  
  public void run() {
    if (DeviceHandle.Peripheral.b(this.a, "S01")) { // if send start stream command is successful
      String str = DeviceHandle.Peripheral.m(this.a);
      if (str == null)
        str = "???"; 
      Log.i(DeviceHandle.Peripheral.b(this.a), "Device opening stream: " + str);
    } 
  }
}


/* Location:              /home/tb/repos/openmelon/src/MPK-Decompile/classes/!/com/axio/melonplatformkit/j.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */