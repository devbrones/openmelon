package com.axio.melonplatformkit;

import android.util.Log;

class k implements Runnable {
  k(DeviceHandle.Peripheral paramPeripheral) {}
  
  public void run() {
    if (DeviceHandle.Peripheral.b(this.a, "S00")) {
      String str = DeviceHandle.Peripheral.m(this.a);
      if (str == null)
        str = "???"; 
      Log.i(DeviceHandle.Peripheral.b(this.a), "Device closing stream: " + str);
    } 
  }
}


/* Location:              /home/tb/repos/openmelon/src/MPK-Decompile/classes/!/com/axio/melonplatformkit/k.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */