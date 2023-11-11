package com.axio.melonplatformkit;

class s implements Runnable {
  s(q paramq, Runnable paramRunnable) {}
  
  public void run() {
    try {
      this.a.run();
    } catch (Exception exception) {
      exception.printStackTrace();
    } 
    q.b(this.b).remove(this.a);
    synchronized (this) {
      notifyAll();
    } 
  }
}


/* Location:              /home/tb/repos/openmelon/src/MPK-Decompile/classes/!/com/axio/melonplatformkit/s.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */