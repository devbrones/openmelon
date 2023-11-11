package com.axio.melonplatformkit;

public final class AnalysisResult {
  private final AnalysisResultBase _base = new AnalysisResultBase();
  
  public FFTBuffer fftBuffer = new FFTBuffer();
  
  protected AnalysisResultBase getBase() {
    return this._base;
  }
  
  public int getChannel() {
    return this._base.channel;
  }
  
  public float[] getFilteredSignal() {
    return this._base.filteredSignal;
  }
  
  public float[] getRawSignal() {
    return this._base.rawSignal;
  }
  
  public float getAnalysisRate() {
    return this._base.analysisRate;
  }
  
  public float getFocusScore() {
    return this._base.focusScore;
  }
  
  public boolean hasFocusScoare() {
    return this._base.hasFocusScore;
  }
  
  public int getSamplingRate() {
    return this._base.samplingRate;
  }
  
  public int getSampleIndex() {
    return this._base.sampleIndex;
  }
  
  public int getSampleSize() {
    return this._base.getSampleSize();
  }
  
  public float[] getAnalysisSample() {
    return this._base.cachedSample();
  }
  
  public boolean getHeadbandAlert() {
    return this._base.headbandAlert;
  }
  
  public void setUpFFT(FloatBuffer paramFloatBuffer) {
    this.fftBuffer.update(paramFloatBuffer);
  }
  
  protected final class AnalysisResultBase {
    protected int channel;
    
    protected float[] filteredSignal = null;
    
    protected float[] rawSignal = null;
    
    protected float[] analysisSample = null;
    
    protected int sampleSize;
    
    protected float focusScore = 0.0F;
    
    protected boolean hasFocusScore = false;
    
    protected boolean headbandAlert;
    
    protected float analysisRate;
    
    protected int samplingRate;
    
    protected int sampleIndex;
    
    private AnalysisResultBase() {}
    
    private float[] cachedSample() {
      if (this.analysisSample == null) {
        this.analysisSample = new float[getSampleSize()];
        System.arraycopy(this.filteredSignal, this.sampleIndex, this.analysisSample, 0, this.analysisSample.length);
      } 
      return this.analysisSample;
    }
    
    private int getSampleSize() {
      if (this.sampleSize == 0)
        this.sampleSize = this.filteredSignal.length - this.sampleIndex; 
      return this.sampleSize;
    }
  }
}


/* Location:              /home/tb/repos/openmelon/src/MPK-Decompile/classes/!/com/axio/melonplatformkit/AnalysisResult.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */