package com.axio.melonplatformkit;

import java.util.Iterator;

class FloatBuffer implements Iterable {
  private float[] a;
  
  private int b = 0;
  
  private int c = 0;
  
  private int d = 0;
  
  private boolean e = false;
  
  private float f = Float.MAX_VALUE;
  
  private float g = -3.4028235E38F;
  
  public void a() {
    for (byte b = 0; b < this.b; b++)
      this.a[b] = 0.0F; 
    this.d = 0;
    this.c = 0;
    this.e = false;
    this.f = Float.MAX_VALUE;
    this.g = -3.4028235E38F;
  }
  
  public float b() {
    return !this.e ? Float.NaN : this.g;
  }
  
  public float a(int paramInt) {
    return this.a[(paramInt + this.d) % this.b];
  }
  
  public float[] c() {
    float[] arrayOfFloat = new float[this.b];
    for (byte b = 0; b < this.b; b++)
      arrayOfFloat[b] = this.a[(b + this.d) % this.b]; 
    return arrayOfFloat;
  }
  
  public void a(double[] paramArrayOfdouble, int paramInt1, int paramInt2) {
    for (int i = paramInt1; i < paramInt1 + paramInt2; i++)
      paramArrayOfdouble[i - paramInt1] = this.a[(i + this.d) % this.b]; 
  }
  
  public FloatBuffer(int paramInt) {
    this.b = paramInt;
    this.c = 0;
    this.d = 0;
    this.a = new float[paramInt];
    for (byte b = 0; b < paramInt; b++)
      a(0.0F); 
  }
  
  public int d() {
    return this.b;
  }
  
  public void a(float sample) { // focus on this method for now
    this.f = Math.min(sample, this.f);
    this.g = Math.max(sample, this.g);
    this.e = true;
    int i = this.b;

    this.a[this.c % i] = sample;

    this.c++;

    if (this.c > i) {

      this.d++;

      if (this.c % i == 0)
        this.c = this.d; 

      if (this.d == i)
        this.d = 0; 
    
    } 

  }
  
  public Iterator iterator() {
    return new FloatBufferIterator();
  }
  
  public class FloatBufferIterator implements Iterator {
    private int _currentIndex = 0;
    
    public boolean hasNext() {
      return (this._currentIndex < FloatBuffer.this.d());
    }
    
    public Float next() {
      return Float.valueOf(FloatBuffer.this.a(this._currentIndex++));
    }
    
    public void remove() {
      throw new UnsupportedOperationException("FloatBuffer cannot be mutated while enumerating");
    }
  }
}


/* Location:              /home/tb/repos/openmelon/src/MPK-Decompile/classes/!/com/axio/melonplatformkit/FloatBuffer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */