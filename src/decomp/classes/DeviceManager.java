package com.axio.melonplatformkit;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Timer;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class DeviceManager {
  protected HashSet _listeners = new HashSet();
  
  private static UUID _serviceUUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
  
  private static DeviceManager _instance = new DeviceManager();
  
  private boolean _scanning;
  
  private int _scan = 0;
  
  private BluetoothAdapter _adapter = BluetoothAdapter.getDefaultAdapter();
  
  private HashMap _devices = new HashMap<>();
  
  private HashSet _discoveredAddresses = new HashSet();
  
  private Timer _scanCancelTimer = null;
  
  private Timer _updateTimer = null;
  
  private BluetoothAdapter.LeScanCallback _scanCallback = new l(this);
  
  private static final long SCAN_PERIOD = 10000L;
  
  protected HashSet getListeners() {
    return this._listeners;
  }
  
  public boolean isScanning() {
    return this._scanning;
  }
  
  private void notifyStoppedScan() {
    HashSet hashSet = new HashSet(this._listeners);
    q.a().a(new m(this, hashSet));
  }
  
  private void notifyStartedScan() {
    HashSet hashSet = new HashSet(this._listeners);
    q.a().a(new n(this, hashSet));
  }
  
  private List parseUUIDs(byte[] paramArrayOfbyte) {
    ArrayList<UUID> arrayList = new ArrayList();
    for (int i = 0; i < paramArrayOfbyte.length - 2; i += b1 - 1) {
      byte b1 = paramArrayOfbyte[i++];
      if (b1 == 0)
        break; 
      byte b2 = paramArrayOfbyte[i++];
      switch (b2) {
        case 2:
        case 3:
          while (b1 > 1) {
            byte b = paramArrayOfbyte[i++];
            int j = b + (paramArrayOfbyte[i++] << 8);
            b1 -= 2;
            arrayList.add(UUID.fromString(String.format("%08x-0000-1000-8000-00805f9b34fb", new Object[] { Integer.valueOf(j) })));
          } 
          continue;
        case 6:
        case 7:
          while (b1 >= 16) {
            try {
              ByteBuffer byteBuffer = ByteBuffer.wrap(paramArrayOfbyte, i++, 16).order(ByteOrder.LITTLE_ENDIAN);
              long l1 = byteBuffer.getLong();
              long l2 = byteBuffer.getLong();
              arrayList.add(new UUID(l2, l1));
            } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
              Log.e("MPK ", indexOutOfBoundsException.toString());
            } finally {
              i += 15;
              b1 -= 16;
            } 
          } 
          continue;
      } 
    } 
    return arrayList;
  }
  
  private void onDiscover(BluetoothDevice paramBluetoothDevice, int paramInt, byte[] paramArrayOfbyte) {
    String str = paramBluetoothDevice.getAddress();
    if (str != null) {
      List list = parseUUIDs(paramArrayOfbyte);
      if (list == null)
        return; 
      if (list.contains(_serviceUUID) && !this._devices.containsKey(str)) {
        DeviceHandle deviceHandle = new DeviceHandle(paramBluetoothDevice, this._adapter, paramArrayOfbyte);
        this._devices.put(str, deviceHandle);
        notifyDeviceFound(deviceHandle);
      } 
    } 
  }
  
  private void notifyDeviceFound(DeviceHandle paramDeviceHandle) {
    HashSet hashSet = new HashSet(this._listeners);
    q.a().a(new o(this, hashSet, paramDeviceHandle));
  }
  
  private DeviceManager() {
    this._updateTimer = new Timer();
    this._updateTimer.scheduleAtFixedRate(new p(this), TimeUnit.SECONDS.toMillis(5L), TimeUnit.SECONDS.toMillis(5L));
  }
  
  public void addListener(IDeviceManagerListener paramIDeviceManagerListener) {
    this._listeners.add(paramIDeviceManagerListener);
  }
  
  public void removeListener(IDeviceManagerListener paramIDeviceManagerListener) {
    this._listeners.remove(paramIDeviceManagerListener);
  }
  
  public void disconnectDevice(DeviceHandle paramDeviceHandle) {
    paramDeviceHandle.disconnect();
  }
  
  public static DeviceManager getManager() {
    return _instance;
  }
  
  public void startScan() {
    if (this._scanning)
      stopScan(); 
    int i = ++this._scan;
    if (this._adapter != null && this._adapter.startLeScan(this._scanCallback))
      notifyStartedScan(); 
  }
  
  public void stopScan() {
    this._scanning = false;
    if (this._scanCancelTimer != null)
      this._scanCancelTimer.cancel(); 
    this._scanCancelTimer = null;
    this._adapter.stopLeScan(this._scanCallback);
    notifyStoppedScan();
  }
  
  public ArrayList getAvailableDevices() {
    ArrayList<DeviceHandle> arrayList = new ArrayList();
    synchronized (this._devices) {
      for (DeviceHandle deviceHandle : this._devices.values())
        arrayList.add(deviceHandle); 
    } 
    return arrayList;
  }
  
  public DeviceHandle getConnectedDevice() {
    DeviceHandle deviceHandle = null;
    synchronized (this._devices) {
      for (DeviceHandle deviceHandle1 : this._devices.values()) {
        if (deviceHandle1.getState() != DeviceHandle.DeviceState.DISCONNECTED) {
          deviceHandle = deviceHandle1;
          break;
        } 
      } 
    } 
    return deviceHandle;
  }
}


/* Location:              /home/tb/repos/openmelon/src/MPK-Decompile/classes/!/com/axio/melonplatformkit/DeviceManager.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */