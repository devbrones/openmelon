package com.axio.melonplatformkit;

public interface IDeviceManagerListener {
  void onDeviceScanStopped();
  
  void onDeviceScanStarted();
  
  void onDeviceFound(DeviceHandle paramDeviceHandle);
  
  void onDeviceReady(DeviceHandle paramDeviceHandle);
  
  void onDeviceDisconnected(DeviceHandle paramDeviceHandle);
  
  void onDeviceConnected(DeviceHandle paramDeviceHandle);
  
  void onDeviceConnecting(DeviceHandle paramDeviceHandle);
  
  void onDeviceUnknowStatus(DeviceHandle paramDeviceHandle);
}


/* Location:              /home/tb/repos/openmelon/src/MPK-Decompile/classes/!/com/axio/melonplatformkit/IDeviceManagerListener.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */