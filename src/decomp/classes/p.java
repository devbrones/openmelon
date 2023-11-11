package com.axio.melonplatformkit;

import java.util.TimerTask;

class p extends TimerTask {
  p(DeviceManager paramDeviceManager) {}
  
  public void run() {
    DeviceHandle deviceHandle = this.a.getConnectedDevice();
    if (deviceHandle != null)
      deviceHandle.updateSystemState(); 
  }
}


/* Location:              /home/tb/repos/openmelon/src/MPK-Decompile/classes/!/com/axio/melonplatformkit/p.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */