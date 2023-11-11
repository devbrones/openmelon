package com.axio.melonplatformkit;

public class Averager implements z {
  private FloatBuffer _buffer = null;
  
  private int _period = 0;
  
  private int _count = 0;
  
  private float _cumulativeAverage = 0.0F;
  
  private float _movingAverage = 0.0F;
  
  public Averager(int paramInt) {
    if (paramInt < 1)
      paramInt = 1; 
    this._period = paramInt;
    this._buffer = new FloatBuffer(paramInt);
  }
  
  public float getMovingAverage() {
    return this._movingAverage;
  }
  
  public float getCumulativeAverage() {
    return this._cumulativeAverage;
  }
  
  public float getMovingAverageIfPossible() {
    return (this._count >= this._period) ? this._movingAverage : this._cumulativeAverage;
  }
  
  public void reset() {
    this._cumulativeAverage = 0.0F;
    this._movingAverage = 0.0F;
    this._count = 0;
    this._buffer.a();
  }
  
  public float add(float paramFloat) {
    float f1 = this._buffer.a(0);
    this._buffer.a(paramFloat);
    float f2 = this._period;
    this._movingAverage = this._movingAverage - f1 / f2 + paramFloat / f2;
    this._count++;
    this._cumulativeAverage += (paramFloat - this._cumulativeAverage) / this._count;
    return this._movingAverage;
  }
  
  public float input(float paramFloat) {
    return add(paramFloat);
  }
  
  public float response(int paramInt) {
    return (paramInt == 0) ? getMovingAverage() : ((paramInt == 1) ? getMovingAverageIfPossible() : ((paramInt == 2) ? getCumulativeAverage() : Float.NaN));
  }
}


/* Location:              /home/tb/repos/openmelon/src/MPK-Decompile/classes/!/com/axio/melonplatformkit/Averager.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */