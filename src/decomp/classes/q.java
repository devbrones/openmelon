package com.axio.melonplatformkit;

import android.os.Handler;
import android.os.Looper;
import java.util.ArrayDeque;
import java.util.HashSet;

class q {
  final ArrayDeque a = new ArrayDeque();
  
  private HashSet b = new HashSet();
  
  private boolean c = true;
  
  private Thread d = null;
  
  private boolean e = false;
  
  private static q f = new q(true);
  
  public q() {
    this.d = new Thread(new r(this));
    this.d.start();
  }
  
  private q(boolean paramBoolean) {
    this.e = paramBoolean;
  }
  
  public static q a() {
    return f;
  }
  
  public void a(Runnable paramRunnable) {
    if (this.e) {
      synchronized (this) {
        this.b.add(paramRunnable);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new s(this, paramRunnable));
      } 
    } else {
      synchronized (this) {
        this.a.offer(paramRunnable);
        notifyAll();
      } 
    } 
  }
  
  private void b() {
    Object object = new Object();
    while (this.c) {
      Runnable runnable = null;
      synchronized (this) {
        while (this.a.isEmpty()) {
          try {
            notifyAll();
            wait();
          } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
          } 
        } 
        runnable = this.a.poll();
      } 
      if (runnable != null)
        try {
          runnable.run();
        } catch (Exception exception) {
          exception.printStackTrace();
        }  
    } 
  }
}


/* Location:              /home/tb/repos/openmelon/src/MPK-Decompile/classes/!/com/axio/melonplatformkit/q.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */