package com.axio.melonplatformkit;

import org.apache.commons.math3.complex.a;
import org.apache.commons.math3.transform.a;
import org.apache.commons.math3.transform.b;
import org.apache.commons.math3.transform.c;

public class FFTBuffer {
  private FloatBuffer _buf = new FloatBuffer(256);
  
  private final int _size = 256;
  
  private final int _log2n = (int)(Math.log(256.0D) / Math.log(2.0D));
  
  private b _transform = null;
  
  private int _cycles = 0;
  
  private boolean _ready = false;
  
  private double[] _signal = new double[256];
  
  private float[] _frequencies = new float[128];
  
  private float[] _amplitude = new float[128];
  
  private float[] _img = new float[128];
  
  private float[] _real = new float[128];
  
  private float[] _cos;
  
  private float[] _sin;
  
  private double[][] _split = new double[2][128];
  
  public float[] getAmplitude() {
    return this._amplitude;
  }
  
  protected float[] getFrequencies() {
    return this._frequencies;
  }
  
  protected FFTBuffer() {
    this._transform = new b(a.a);
    float f1 = 0.9765625F;
    float f2 = 0.0F;
    byte b1;
    for (b1 = 0; b1 < ''; b1++) {
      this._frequencies[b1] = f2;
      f2 += f1;
    } 
    this._cos = new float[128];
    this._sin = new float[128];
    for (b1 = 0; b1 < ''; b1++) {
      this._cos[b1] = (float)Math.cos(-6.283185307179586D * b1 / 256.0D);
      this._sin[b1] = (float)Math.sin(-6.283185307179586D * b1 / 256.0D);
    } 
  }
  
  protected void update(FloatBuffer paramFloatBuffer) {
    paramFloatBuffer.a(this._signal, paramFloatBuffer.d() - 256, 256);
    b b1 = this._transform;
    int i = Math.round((this._signal.length / 256));
    double[] arrayOfDouble = new double[256];
    a[] arrayOfA = null;
    double[][] arrayOfDouble1 = this._split;
    double d = 0.001953125D;
    byte b2;
    for (b2 = 0; b2 < i && b2 * 256 + 256 <= this._signal.length; b2++) {
      byte b3;
      for (b3 = 0; b3 < 'Ā'; b3++)
        arrayOfDouble[b3] = this._signal[b2 * 256 + b3] * hamming(b3, 256); 
      arrayOfA = b1.a(arrayOfDouble, c.a);
      for (b3 = 0; b3 < ''; b3++)
        arrayOfDouble1[1][b3] = (b2 * arrayOfDouble1[1][b3] + arrayOfA[b3].a() * d) / (b2 + 1.0D); 
    } 
    for (b2 = 0; b2 < (arrayOfDouble1[0]).length; b2++)
      this._amplitude[b2] = (float)arrayOfDouble1[1][b2]; 
  }
  
  public static double hamming(int paramInt1, int paramInt2) {
    return 0.54D - 0.46D * Math.cos(6.283185307179586D * paramInt1 / (paramInt2 - 1));
  }
  
  public void transform(float[] paramArrayOffloat1, float[] paramArrayOffloat2) {
    char c = 'Ā';
    int i = this._log2n;
    byte b1 = 0;
    int j = 1;
    int k = 0;
    int m = 0;
    int n = c / 2;
    int i1 = 0;
    float f1 = 0.0F;
    float f2 = 0.0F;
    float f3 = 0.0F;
    float f4 = 0.0F;
    for (b1 = 1; b1 < c - 1; b1++) {
      for (m = n; j >= m; m /= 2)
        j -= m; 
      j += m;
      if (b1 < j) {
        f3 = paramArrayOffloat1[b1];
        paramArrayOffloat1[b1] = paramArrayOffloat1[j];
        paramArrayOffloat1[j] = f3;
        f3 = paramArrayOffloat2[b1];
        paramArrayOffloat2[b1] = paramArrayOffloat2[j];
        paramArrayOffloat2[j] = f3;
      } 
    } 
    m = 0;
    n = 1;
    for (b1 = 0; b1 < this._log2n; b1++) {
      m = n;
      n += n;
      i1 = 0;
      for (j = 0; j < m; j++) {
        f1 = this._cos[i1];
        f2 = this._sin[i1];
        i1 += 1 << i - b1 - 1;
        for (k = j; k < 256; k += n) {
          f3 = f1 * paramArrayOffloat1[k + m] - f2 * paramArrayOffloat2[k + m];
          f4 = f2 * paramArrayOffloat1[k + m] + f1 * paramArrayOffloat2[k + m];
          paramArrayOffloat1[k + m] = paramArrayOffloat1[k] - f3;
          paramArrayOffloat2[k + m] = paramArrayOffloat2[k] - f4;
          paramArrayOffloat1[k] = paramArrayOffloat1[k] + f3;
          paramArrayOffloat2[k] = paramArrayOffloat2[k] + f4;
        } 
      } 
    } 
  }
}


/* Location:              /home/tb/repos/openmelon/src/MPK-Decompile/classes/!/com/axio/melonplatformkit/FFTBuffer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */