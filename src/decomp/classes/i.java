package com.axio.melonplatformkit;

class i implements Runnable {
  i(DeviceHandle.Peripheral paramPeripheral) {}
  
  public void run() {
    DeviceHandle.Peripheral.b(this.a, "DW0308"); // send start command !! EUREKA
    DeviceHandle.Peripheral.a(this.a, DeviceHandle.DeviceState.CONNECTED);
    DeviceHandle.Peripheral.n(this.a);
  }
}


/* Location:              /home/tb/repos/openmelon/src/MPK-Decompile/classes/!/com/axio/melonplatformkit/i.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */