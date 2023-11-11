package com.axio.melonplatformkit;

import org.apache.commons.math3.stat.descriptive.c;

public class SignalValidator {
  private final String TAG = SignalValidator.class.getSimpleName();
  
  private int _sampleCount = 0;
  
  private int _startCountdown = 50;
  
  private int _falseCount = 0;
  
  private boolean _wasTrue;
  
  private int _minFalse = 30;
  
  private double _minCV = 0.55D;
  
  private double _maxCV = 5.0D;
  
  protected boolean flatLine(float[] paramArrayOffloat) {
    for (byte b = 1; b < paramArrayOffloat.length; b++) {
      if (paramArrayOffloat[b] != paramArrayOffloat[b - 1])
        return false; 
    } 
    return true;
  }
  
  protected boolean extremeChangeInRaw(float[] paramArrayOffloat) {
    double[] arrayOfDouble1 = new double[paramArrayOffloat.length - 1];
    double[] arrayOfDouble2 = new double[paramArrayOffloat.length - 1];
    double d1 = 0.0D;
    for (byte b = 0; b < paramArrayOffloat.length - 1; b++) {
      arrayOfDouble1[b] = Math.abs(paramArrayOffloat[b + 1] - paramArrayOffloat[b]);
      arrayOfDouble2[b] = paramArrayOffloat[b];
    } 
    c c = new c(arrayOfDouble2);
    double d2 = Math.abs(c.a());
    c = new c(arrayOfDouble1);
    double d3 = c.c() / c.a();
    return (d3 > this._maxCV || d3 < this._minCV);
  }
  
  protected boolean headbandAlert(float[] paramArrayOffloat1, float[] paramArrayOffloat2, float[] paramArrayOffloat3) {
    return extremeChangeInRaw(paramArrayOffloat1);
  }
  
  protected boolean cumulativeHeadbandAlert(double paramDouble, float[] paramArrayOffloat1, float[] paramArrayOffloat2, float[] paramArrayOffloat3) {
    this._startCountdown--;
    if (paramArrayOffloat1.length > 0 && paramArrayOffloat3.length > 0 && this._startCountdown <= 0) {
      boolean bool = headbandAlert(paramArrayOffloat1, paramArrayOffloat2, paramArrayOffloat3);
      if (bool) {
        this._sampleCount += paramArrayOffloat1.length;
        this._falseCount = 0;
        if (this._sampleCount > paramDouble) {
          this._wasTrue = true;
          return true;
        } 
      } else {
        this._sampleCount = 0;
        this._falseCount++;
        if (this._wasTrue && this._falseCount < this._minFalse)
          return true; 
        this._wasTrue = false;
      } 
    } 
    return false;
  }
}


/* Location:              /home/tb/repos/openmelon/src/MPK-Decompile/classes/!/com/axio/melonplatformkit/SignalValidator.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */