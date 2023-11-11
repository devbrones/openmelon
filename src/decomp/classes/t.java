package com.axio.melonplatformkit;

class t implements a {
  private FloatBuffer a = null;
  
  private FloatBuffer b = null;
  
  private FloatBuffer c = null;
  
  private FloatBuffer d = null;
  
  private z e = null;
  
  private z f = null;
  
  private z g = null;
  
  private z h = null;
  
  private z i = null;
  
  private z j = null;
  
  private float[] k = null;
  
  protected t() {
    this.k = new float[] { 0.6F, 1.4F, 2.0F, 0.75F, 1.25F, 2.0F, 0.9F, 0.1F };
    this.a = new FloatBuffer(10);
    this.b = new FloatBuffer(10);
    this.c = new FloatBuffer(10);
    this.d = new FloatBuffer(10);
    this.e = a(16);
    this.f = a(4);
    this.g = a(10);
    this.h = a(4);
    this.i = a(8);
    this.j = a(10);
  }
  
  private z a(int paramInt) {
    return new Averager(paramInt);
  }
  
  private float a(float[] paramArrayOffloat1, float[] paramArrayOffloat2, float paramFloat1, float paramFloat2) {
    float f1 = 0.0F;
    float f2 = 0.0F;
    for (byte b = 0; b < paramArrayOffloat2.length; b++) {
      float f = paramArrayOffloat2[b];
      if (f >= paramFloat1 && f <= paramFloat2) {
        f1 += paramArrayOffloat1[b];
        f2++;
      } 
    } 
    return (f2 > 0.0F) ? (f1 / f2) : 0.0F;
  }
  
  public float a(float[] paramArrayOffloat1, float[] paramArrayOffloat2) {
    float f1 = a(paramArrayOffloat1, paramArrayOffloat2, 2.0F, 4.0F);
    float f2 = a(paramArrayOffloat1, paramArrayOffloat2, 5.0F, 8.0F);
    if (f1 == 0.0F || f2 == 0.0F)
      return this.j.response(1); 
    float f3 = a(paramArrayOffloat1, paramArrayOffloat2, 10.0F, 15.0F);
    float f4 = a(paramArrayOffloat1, paramArrayOffloat2, 16.0F, 21.0F);
    if (f4 == 0.0D)
      f4 = 1.0E-4F; 
    if (f3 == 0.0F)
      f3 = f4; 
    float f5 = a(1.0F, f4 / f3);
    this.i.input(f5);
    float f6 = this.i.response(1);
    this.d.a(f4);
    this.h.input(f4);
    this.e.input(f1);
    this.f.input(f2);
    float f7 = this.e.response(1);
    float f8 = this.f.response(1);
    float f9 = this.h.response(1);
    float f10 = f9 / this.d.b();
    float[] arrayOfFloat = this.k;
    float f11 = a(1.0F, b(0.0F, a(1.0F, b(0.0F, (f6 * arrayOfFloat[3] + (1.0F - a(1.0F, b(0.0F, (f7 * arrayOfFloat[0] + f8 * arrayOfFloat[1]) / arrayOfFloat[2]))) * arrayOfFloat[4]) / arrayOfFloat[5])) / arrayOfFloat[6] - arrayOfFloat[7]));
    this.j.input(f11);
    return this.j.response(1);
  }
  
  private static float a(float paramFloat1, float paramFloat2) {
    return (paramFloat1 > paramFloat2) ? paramFloat2 : ((paramFloat1 < paramFloat2) ? paramFloat1 : ((paramFloat1 != paramFloat2) ? Float.NaN : ((Float.floatToRawIntBits(paramFloat1) == Integer.MIN_VALUE) ? -0.0F : paramFloat2)));
  }
  
  private float b(float paramFloat1, float paramFloat2) {
    return (paramFloat1 > paramFloat2) ? paramFloat1 : ((paramFloat1 < paramFloat2) ? paramFloat2 : ((paramFloat1 != paramFloat2) ? Float.NaN : ((Float.floatToRawIntBits(paramFloat1) != 0) ? paramFloat2 : 0.0F)));
  }
}


/* Location:              /home/tb/repos/openmelon/src/MPK-Decompile/classes/!/com/axio/melonplatformkit/t.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */