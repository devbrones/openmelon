package com.axio.melonplatformkit;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DeviceHandle {
  private Peripheral _peripheral = null;
  
  protected DeviceHandle(BluetoothDevice paramBluetoothDevice, BluetoothAdapter paramBluetoothAdapter, byte[] paramArrayOfbyte) {
    this._peripheral = new Peripheral(this, paramBluetoothDevice, paramBluetoothAdapter, this, paramArrayOfbyte);
  }
  
  protected void updateSystemState() {
    Peripheral.o(this._peripheral);
  }
  
  public float getBatteryLevel() {
    return (Peripheral.p(this._peripheral) == null) ? 0.0F : Peripheral.p(this._peripheral).floatValue();
  }
  
  public boolean getCharging() {
    return (Peripheral.q(this._peripheral) != null) ? ((Peripheral.q(this._peripheral).floatValue() > 4.7D)) : false;
  }
  
  public boolean getReady() {
    return Peripheral.r(this._peripheral);
  }
  
  public String getName() {
    return Peripheral.c(this._peripheral);
  }
  
  public void addAnalyzer(SignalAnalyzer paramSignalAnalyzer) {
    Peripheral.a(this._peripheral, paramSignalAnalyzer);
  }
  
  public void removeAnalyzer(SignalAnalyzer paramSignalAnalyzer) {
    Peripheral.a(this._peripheral, paramSignalAnalyzer);
  }
  
  public void connect() {
    Peripheral.s(this._peripheral);
  }
  
  public void disconnect() {
    Peripheral.t(this._peripheral);
  }
  
  public void startStreaming() {
    Peripheral.u(this._peripheral);
  }
  
  public void stopStreaming() {
    Peripheral.v(this._peripheral);
  }
  
  public DeviceState getState() {
    return Peripheral.w(this._peripheral);
  }
  
  private class Peripheral extends Service {
    private WeakReference c = null;
    
    final ScheduledExecutorService a = Executors.newSingleThreadScheduledExecutor();
    
    private final UUID d = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"); // Descriptor
    
    private final UUID e = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e"); // Service UART nRF
    
    private final UUID f = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e"); // Characteristic TX
    
    private final UUID g = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e"); // Characteristic RX
    
    private final y h = new y();
    
    private final String i = Peripheral.class.getSimpleName();
    
    private String j;
    
    private BluetoothAdapter k;
    
    private String l = null;
    
    private BluetoothGatt m;
    
    private boolean n = false;
    
    private q o = new q();
    
    private DeviceHandle.DeviceState p = DeviceHandle.DeviceState.DISCONNECTED;
    
    private String q;
    
    private boolean r = false;
    
    private boolean s = false;
    
    private byte[] t = null;
    
    private Float u = null;
    
    private Float v = null;
    
    private final BluetoothGattCallback w = new b(this);
    
    private final IBinder x = (IBinder)new LocalBinder();
    
    private Float a() {
      return this.u;
    }
    
    private Float b() {
      return this.v;
    }
    
    private DeviceHandle.DeviceState c() {
      return this.p;
    }
    
    private boolean d() {
      return this.s;
    }
    
    private void a(SignalAnalyzer param1SignalAnalyzer) {
      this.h.a(param1SignalAnalyzer);
    }
    
    public Peripheral(DeviceHandle this$0, BluetoothDevice param1BluetoothDevice, BluetoothAdapter param1BluetoothAdapter, DeviceHandle param1DeviceHandle1, byte[] param1ArrayOfbyte) {
      this.j = param1BluetoothDevice.getAddress();
      this.c = new WeakReference<>(param1DeviceHandle1);
      this.t = (byte[])param1ArrayOfbyte.clone();
      this.k = param1BluetoothAdapter;
      u();
      if (this.l == null) {
        Log.i(this.i, "Performing Expensive Name Lookup...");
        g();
      } else {
        this.n = true;
      } 
      Log.i(this.i, "Discovered device: " + this.l);
    }
    
    private BluetoothDevice e() {
      return (this.j == null) ? null : this.k.getRemoteDevice(this.j);
    }
    
    private String f() {
      String str = null;
      synchronized (this) {
        while (!this.n) {
          try {
            wait();
          } catch (InterruptedException interruptedException) {}
        } 
        str = this.l;
      } 
      return str;
    }
    
    public void a(String param1String) { //SENDER?
      synchronized (this) {
        this.l = param1String;
        this.n = true;
        notifyAll(); // wake all listener threads
        if (param1String != null)
          Log.i(this.i, "Device name: " + param1String); 
      } 
    }
    
    private void g() {
      if (e().getName() != null) {
        a(e().getName());
        return;
      } 
      byte b = 0;
      Peripheral peripheral = this;
      while (peripheral.e().getName() == null) {
        b++;
        try {
          Thread.sleep(10L, 0);
        } catch (InterruptedException interruptedException) {
          interruptedException.printStackTrace();
        } 
        if (b > 'Ǵ')
          break; 
      } 
      String str = peripheral.e().getName();
      if (b > 0)
        Log.e(this.i, "Spins to find name " + b); 
      peripheral.a(str);
    }
    
    private void h() {
      String str = this.q;
      Long long_ = Long.decode("0x" + str.toLowerCase());
      int i = long_.intValue();
      float f1 = 3.6F;
      float f2 = 3.956F;
      float f3 = 4.7F;
      float f4 = i * 2.0F / 100.0F;
      float f5 = (f4 - f1) / (f2 - f1);
      float f6 = Math.min(1.0F, Math.max(0.0F, f5));
      this.v = Float.valueOf(f4);
      this.u = Float.valueOf(f6);
    }
    
    private void i() {
      HashSet hashSet;
      DeviceHandle deviceHandle = this.c.get();
      synchronized (this) {
        hashSet = new HashSet(DeviceManager.getManager().getListeners());
      } 
      this.a.schedule(new c(this, hashSet, deviceHandle), 100L, TimeUnit.MILLISECONDS);
    }
    
    private void j() {
      DeviceHandle deviceHandle;
      HashSet hashSet;
      String str = this.l;
      if (str == null)
        str = "???"; 
      Log.i(this.i, "Device disconnected: " + str);
      synchronized (this) {
        hashSet = new HashSet(DeviceManager.getManager().getListeners());
        deviceHandle = this.c.get();
      } 
      q.a().a(new e(this, hashSet, deviceHandle));
    }
    
    private void k() {
      DeviceHandle deviceHandle;
      HashSet hashSet;
      String str = this.l;
      if (str == null)
        str = "???"; 
      Log.i(this.i, "Device connected: " + str);
      synchronized (this) {
        hashSet = new HashSet(DeviceManager.getManager().getListeners());
        deviceHandle = this.c.get();
      } 
      q.a().a(new f(this, hashSet, deviceHandle));
    }
    
    private void l() {
      DeviceHandle deviceHandle;
      HashSet hashSet;
      String str = this.l;
      if (str == null)
        str = "???"; 
      Log.i(this.i, "Device connected: " + str);
      synchronized (this) {
        hashSet = new HashSet(DeviceManager.getManager().getListeners());
        deviceHandle = this.c.get();
      } 
      q.a().a(new g(this, hashSet, deviceHandle));
    }
    
    private void m() {
      DeviceHandle deviceHandle;
      HashSet hashSet;
      String str = this.l;
      if (str == null)
        str = "???"; 
      Log.i(this.i, "Device UnKnowStatus: " + str);
      synchronized (this) {
        hashSet = new HashSet(DeviceManager.getManager().getListeners());
        deviceHandle = this.c.get();
      } 
      q.a().a(new h(this, hashSet, deviceHandle));
    }
    
    private void n() {
      this.a.schedule(new i(this), 100L, TimeUnit.MILLISECONDS);
    }
    
    private boolean o() {
      String str = this.l;
      if (str == null)
        str = "???"; 
      Log.d(this.i, "DeviceState.DISCONNECTED=" + DeviceHandle.DeviceState.DISCONNECTED + " _state=" + this.p);
      if (this.p == DeviceHandle.DeviceState.DISCONNECTED) {
        DeviceManager.getManager().stopScan();
        if (e() != null && this.m != null)
          if (this.m.connect()) {
            this.p = DeviceHandle.DeviceState.CONNECTED;
            Log.i(this.i, "Device finalizing connection: " + str);
            this.m.discoverServices();
          } else {
            this.p = DeviceHandle.DeviceState.DISCONNECTED;
            return false;
          }  
        BluetoothDevice bluetoothDevice = e();
        if (bluetoothDevice == null) {
          Log.w(this.i, "Device not found.  Unable to connect.");
          return false;
        } 
        try {
          this.m = bluetoothDevice.connectGatt((Context)this, false, this.w);
        } catch (IllegalArgumentException illegalArgumentException) {
          this.p = DeviceHandle.DeviceState.DISCONNECTED;
          if (this.m != null) {
            this.m.disconnect();
            this.m.close();
          } 
          return false;
        } 
        Log.i(this.i, "Device attempting to connect: " + str);
        this.p = DeviceHandle.DeviceState.CONNECTING;
        return true;
      } 
      return false;
    }
    
    private void p() {
      this.a.schedule(new j(this), 100L, TimeUnit.MILLISECONDS);
    }
    
    private void q() {
      this.a.schedule(new k(this), 100L, TimeUnit.MILLISECONDS);
    }
    
    private void r() {
      if (this.m == null)
        return; 
      String str = this.l;
      if (str == null)
        str = "???"; 
      Log.i(this.i, "Device disconnecting: " + str);
      this.m.disconnect();
      this.p = DeviceHandle.DeviceState.DISCONNECTED;
    }
    
    private boolean b(String param1String) { // send command to device CONVERTER
      if (this.m == null || this.p == DeviceHandle.DeviceState.DISCONNECTED)
        return false; 
      boolean bool = false;
      try {
        byte[] arrayOfByte = param1String.getBytes("UTF-8");
        bool = a(arrayOfByte); // send command to device
      } catch (UnsupportedEncodingException unsupportedEncodingException) {}
      return bool;
    }
    
    private boolean s() {
      return (this.m == null) ? false : b("DR0401");
    }
    
    private boolean t() { // RECV?
      if (this.m == null)
        return false; 
      if (this.p == DeviceHandle.DeviceState.CONNECTING)
        try {
          Thread.sleep(1000L);
        } catch (InterruptedException interruptedException) {
          interruptedException.printStackTrace();
        }  
      BluetoothGattService bluetoothGattService = this.m.getService(this.e);
      if (bluetoothGattService == null) {
        Log.e(this.i, "registerForTXNotifications:BluetoothGattService:s = NULL");
        return false;
      } 
      BluetoothGattCharacteristic bluetoothGattCharacteristic = bluetoothGattService.getCharacteristic(this.g);
      if (bluetoothGattCharacteristic == null) {
        Log.e(this.i, "registerForTXNotifications:BluetoothGattCharacteristic:tx = NULL");
        return false;
      } 
      null = this.m.setCharacteristicNotification(bluetoothGattCharacteristic, true);
      BluetoothGattDescriptor bluetoothGattDescriptor = bluetoothGattCharacteristic.getDescriptor(this.d);
      bluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
      return this.m.writeDescriptor(bluetoothGattDescriptor);
    }
    
    private boolean a(byte[] param1ArrayOfbyte) { // send command to device
      if (this.m == null)
        return false; 
      BluetoothGattService bluetoothGattService = this.m.getService(this.e);
      if (bluetoothGattService == null)
        return false; 
      BluetoothGattCharacteristic bluetoothGattCharacteristic = bluetoothGattService.getCharacteristic(this.f);
      if (bluetoothGattCharacteristic == null)
        return false; 
      bluetoothGattCharacteristic.setValue(param1ArrayOfbyte);
      boolean bool = this.m.writeCharacteristic(bluetoothGattCharacteristic);
      Log.w(this.i, "write TXchar - status=" + bool);
      return bool;
    }
    
    private boolean u() {
      byte[] arrayOfByte = this.t;
      ArrayList<UUID> arrayList = new ArrayList();
      String str = null;
      ByteBuffer byteBuffer = ByteBuffer.wrap(arrayOfByte).order(ByteOrder.LITTLE_ENDIAN);
      while (byteBuffer.remaining() > 2) {
        byte[] arrayOfByte1;
        byte b1 = byteBuffer.get();
        if (b1 == 0)
          break; 
        byte b2 = byteBuffer.get();
        switch (b2) {
          case 2:
          case 3:
            while (b1 >= 2) {
              arrayList.add(UUID.fromString(String.format("%08x-0000-1000-8000-00805f9b34fb", new Object[] { Short.valueOf(byteBuffer.getShort()) })));
              b1 = (byte)(b1 - 2);
            } 
            continue;
          case 6:
          case 7:
            while (b1 >= 16) {
              long l1 = byteBuffer.getLong();
              long l2 = byteBuffer.getLong();
              arrayList.add(new UUID(l2, l1));
              b1 = (byte)(b1 - 16);
            } 
            continue;
          case 9:
            arrayOfByte1 = new byte[b1 - 1];
            byteBuffer.get(arrayOfByte1);
            try {
              str = new String(arrayOfByte1, "utf-8");
            } catch (UnsupportedEncodingException unsupportedEncodingException) {
              unsupportedEncodingException.printStackTrace();
            } 
            continue;
        } 
        byteBuffer.position(byteBuffer.position() + b1 - 1);
      } 
      if (str != null) {
        a(str);
        return true;
      } 
      return false;
    }
    
    public IBinder onBind(Intent param1Intent) {
      return this.x;
    }
    
    public boolean onUnbind(Intent param1Intent) {
      r();
      return super.onUnbind(param1Intent);
    }
    
    public class LocalBinder extends Binder {
      DeviceHandle.Peripheral getService() {
        return DeviceHandle.Peripheral.this;
      }
    }
  }
  
  public enum DeviceState {
    DISCONNECTED, CONNECTING, CONNECTED, UnKnown;
  }
}


/* Location:              /home/tb/repos/openmelon/src/MPK-Decompile/classes/!/com/axio/melonplatformkit/DeviceHandle.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */