package com.axio.melonplatformkit;

class d implements Runnable {
  d(c paramc) {}
  
  public void run() {
    for (IDeviceManagerListener iDeviceManagerListener : this.a.a)
      iDeviceManagerListener.onDeviceReady(this.a.b); 
  }
}


/* Location:              /home/tb/repos/openmelon/src/MPK-Decompile/classes/!/com/axio/melonplatformkit/d.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */