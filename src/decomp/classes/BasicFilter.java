package com.axio.melonplatformkit;

class BasicFilter {
  private double a = 0.0D;
  
  private double b = 0.0D;
  
  private double c;
  
  private double d = 0.0D;
  
  private double e = 0.0D;
  
  private u f = null;
  
  private FilterType g;
  
  private a h;
  
  private float[] i = null;
  
  private float[] j = null;
  
  private float[] k = new float[2];
  
  private float[] l = new float[2];
  
  private float[] m = null;
  
  BasicFilter(FilterType paramFilterType, float paramFloat1, float paramFloat2, float paramFloat3) {
    this.c = paramFloat2;
    this.e = paramFloat1;
    this.d = 0.707D;
    this.g = paramFilterType;
    if (this.f != null) {
      float f = (float)Math.sqrt((this.f.a * this.f.b));
      this.c = f;
    } 
    this.h = new a(this, this.g, this.d, this.c, this.e);
  }
  
  BasicFilter(FilterType paramFilterType, float paramFloat1, u paramu, float paramFloat2) {
    this.g = paramFilterType;
    this.f = paramu;
    this.e = paramFloat1;
    float f1 = (float)Math.round(Math.sqrt((this.f.a * this.f.b)));
    float f2 = f1 / (this.f.b - this.f.a);
    this.c = f1;
    this.d = 0.707D;
    this.h = new a(this, this.g, this.d, this.c, this.e);
  }
  
  private void a(int paramInt) {
    int i = paramInt + 2;
    if (this.i == null || this.j == null || this.m == null) {
      this.i = new float[i];
      this.j = new float[i];
      this.m = new float[paramInt];
    } else if (this.i.length != i || this.j.length != i || this.m.length != paramInt) {
      this.i = new float[i];
      this.j = new float[i];
      this.m = new float[paramInt];
    } 
  }
  
  public void a(BasicFilter paramBasicFilter, float[] paramArrayOffloat) {
    a(paramArrayOffloat.length);
    a(paramArrayOffloat);
    if (paramBasicFilter != null)
      paramBasicFilter.a(paramArrayOffloat); 
  }
  
  private void a(float[] paramArrayOffloat) {
    int i = paramArrayOffloat.length;
    a(paramArrayOffloat.length);
    byte b;
    for (b = 0; b < 2; b++)
      this.i[b] = this.k[b]; 
    for (b = 0; b < 2; b++)
      this.j[b] = this.l[b]; 
    for (b = 2; b < i + 2; b++)
      this.i[b] = paramArrayOffloat[b - 2]; 
    a(this.i, this.h.j, this.j, i);
    for (b = 0; b < i; b++)
      paramArrayOffloat[b] = this.j[b]; 
    this.k[0] = this.i[i];
    this.k[1] = this.i[i + 1];
    this.l[0] = this.j[i];
    this.l[1] = this.j[i + 1];
  }
  
  private void a(float[] paramArrayOffloat1, double[] paramArrayOfdouble, float[] paramArrayOffloat2, int paramInt) {
    for (byte b = 2; b < paramInt + 2; b++)
      paramArrayOffloat2[b] = paramArrayOffloat1[b - 0] * (float)paramArrayOfdouble[0] + paramArrayOffloat1[b - 1] * (float)paramArrayOfdouble[1] + paramArrayOffloat1[b - 2] * (float)paramArrayOfdouble[2] - paramArrayOffloat2[b - 1] * (float)paramArrayOfdouble[3] - paramArrayOffloat2[b - 2] * (float)paramArrayOfdouble[4]; 
  }
  
  private class a {
    double a;
    
    double b;
    
    double c;
    
    double d;
    
    double e;
    
    double f;
    
    double g;
    
    double h;
    
    double i;
    
    double[] j;
    
    a(BasicFilter this$0, BasicFilter.FilterType param1FilterType, double param1Double1, double param1Double2, double param1Double3) {
      double d2;
      double d3;
      double d4;
      double d5;
      this.j = new double[5];
      this.h = param1Double1;
      this.i = param1Double2;
      this.g = param1Double3;
      double d1 = Math.PI;
      switch (BasicFilter.null.a[param1FilterType.ordinal()]) {
        case 1:
          d2 = 6.283185307179586D * this.i / this.g;
          d3 = Math.sin(d2);
          d4 = Math.cos(d2);
          d5 = d3 / 2.0D * this.h;
          this.a = 1.0D + d5;
          this.d = d5 / this.a;
          this.e = 0.0D;
          this.f = -1.0D * d5 / this.a;
          this.b = -2.0D * d4 / this.a;
          this.c = (1.0D - d5) / this.a;
          this.j[0] = this.d;
          this.j[1] = this.e;
          this.j[2] = this.f;
          this.j[3] = this.b;
          this.j[4] = this.c;
          return;
        case 2:
          d2 = 6.283185307179586D * this.i / this.g;
          d3 = Math.sin(d2);
          d4 = Math.cos(d2);
          d5 = d3 / 2.0D * this.h;
          this.a = 1.0D + d5;
          this.d = 1.0D / this.a;
          this.e = -2.0D * d4 / this.a;
          this.f = 1.0D / this.a;
          this.b = -2.0D * d4 / this.a;
          this.c = (1.0D - d5) / this.a;
          this.j[0] = this.d;
          this.j[1] = this.e;
          this.j[2] = this.f;
          this.j[3] = this.b;
          this.j[4] = this.c;
          return;
        case 3:
          d2 = 6.283185307179586D * this.i / this.g;
          d3 = Math.sin(d2);
          d4 = Math.cos(d2);
          d5 = d3 / 2.0D * this.h;
          this.a = 1.0D + d5;
          this.d = (1.0D - d4) / 2.0D / this.a;
          this.e = (1.0D - d4) / this.a;
          this.f = (1.0D - d4) / 2.0D / this.a;
          this.b = -2.0D * d4 / this.a;
          this.c = (1.0D - d5) / this.a;
          this.j[0] = this.d;
          this.j[1] = this.e;
          this.j[2] = this.f;
          this.j[3] = this.b;
          this.j[4] = this.c;
        case 4:
          d2 = 6.283185307179586D * this.i / this.g;
          d3 = Math.sin(d2);
          d4 = Math.cos(d2);
          d5 = d3 / 2.0D * this.h;
          this.a = 1.0D + d5;
          this.d = (1.0D + d4) / 2.0D / this.a;
          this.e = -1.0D * (1.0D + d4) / this.a;
          this.f = (1.0D + d4) / 2.0D / this.a;
          this.b = -2.0D * d4 / this.a;
          this.c = (1.0D - d5) / this.a;
          this.j[0] = this.d;
          this.j[1] = this.e;
          this.j[2] = this.f;
          this.j[3] = this.b;
          this.j[4] = this.c;
          return;
      } 
      this.a = 0.0D;
      this.b = 0.0D;
      this.c = 0.0D;
      this.d = 0.0D;
      this.e = 0.0D;
      this.f = 0.0D;
    }
  }
  
  public enum FilterType {
    BANDPASS, NOTCH, LOWPASS, HIGHPASS;
  }
}


/* Location:              /home/tb/repos/openmelon/src/MPK-Decompile/classes/!/com/axio/melonplatformkit/BasicFilter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */