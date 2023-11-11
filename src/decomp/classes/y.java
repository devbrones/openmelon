package com.axio.melonplatformkit;

import java.util.ArrayList;

class y {
  private ArrayList d = new ArrayList();
  
  private short[] e = new short[18];
  
  final float a = 2.4F;
  
  final float b = 6.0F;
  
  final float c = 0.4F / ((float)Math.pow(2.0D, 23.0D) - 1.0F) * 1000000.0F;
  
  protected void a(short[] paramArrayOfshort) {
    System.arraycopy(paramArrayOfshort, 2, this.e, 0, 18);
    int[] arrayOfInt1 = new int[2];
    int[] arrayOfInt2 = new int[2];
    int[] arrayOfInt3 = new int[2];
    byte b1 = 0;
    for (byte b2 = 0; b1 < 18; b2++) {
      for (byte b = 0; b < 2; b++) {
        short[] arrayOfShort = new short[3];
        int i;
        for (i = 0; i < 3; i++)
          arrayOfShort[i] = this.e[b1 + i + b * 3]; 
        i = arrayOfShort[0] & 0xFF;
        int j = arrayOfShort[1] & 0xFF;
        int k = arrayOfShort[2] & 0xFF;
        int m = (i << 16) + (j << 8) + k;
        if (!b2) {
          arrayOfInt1[b] = m;
        } else if (b2 == 1) {
          arrayOfInt2[b] = m;
        } else if (b2 == 2) {
          arrayOfInt3[b] = m;
        } 
      } 
      b1 += 6;
    } 
    float[] arrayOfFloat1 = a(arrayOfInt1);
    float[] arrayOfFloat2 = a(arrayOfInt2);
    float[] arrayOfFloat3 = a(arrayOfInt3);
    synchronized (this.d) {
      for (SignalAnalyzer signalAnalyzer : this.d) {
        signalAnalyzer.enqeueSample(arrayOfFloat1, true);
        signalAnalyzer.enqeueSample(arrayOfFloat2, true);
        signalAnalyzer.enqeueSample(arrayOfFloat3, false);
      } 
    } 
  }
  
  public void a(SignalAnalyzer paramSignalAnalyzer) {
    synchronized (this.d) {
      if (this.d.contains(paramSignalAnalyzer))
        return; 
      this.d.add(paramSignalAnalyzer);
      paramSignalAnalyzer.startAnalyzerThread();
    } 
  }
  
  private float[] a(int[] paramArrayOfint) {
    int i = paramArrayOfint.length;
    float[] arrayOfFloat = new float[i];
    for (byte b = 0; b < i; b++) {
      float f1 = paramArrayOfint[b];
      float f2 = f1 * this.c;
      arrayOfFloat[b] = f2;
    } 
    return arrayOfFloat;
  }
}


/* Location:              /home/tb/repos/openmelon/src/MPK-Decompile/classes/!/com/axio/melonplatformkit/y.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */