package com.axio.melonplatformkit;

import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.EnumSet;

public class SignalAnalyzer {
  public static final EnumSet DEFAULT_ANALYSIS_OPTIONS = EnumSet.of(AnalysisOptions.ANALYZE_LEFT_CHANNEL, new AnalysisOptions[] { AnalysisOptions.ANALYZE_RIGHT_CHANNEL, AnalysisOptions.RESULTS_USE_PRIMITIVE_ARRAYS, AnalysisOptions.RESULTS_USE_FLOAT_BUFFERS, AnalysisOptions.CALCULATE_LEFT_CHANNEL_FOCUS_SCORE, AnalysisOptions.PERFORM_LEFT_CHANNEL_FFT, AnalysisOptions.PERFORM_RIGHT_CHANNEL_FFT });
  
  private SignalAnalyzerBase _base = new SignalAnalyzerBase(this);
  
  protected void enqeueSample(float[] paramArrayOffloat, boolean paramBoolean) {
    this._base.enqeueSample(paramArrayOffloat, paramBoolean);
  }
  
  protected void stopAnalyzerThread() {
    this._base.stopAnalyzerThread();
  }
  
  protected void startAnalyzerThread() {
    this._base.startAnalyzerThread();
  }
  
  public void addListener(ISignalAnalyzerListener paramISignalAnalyzerListener) {
    this._base.addListener(paramISignalAnalyzerListener);
  }
  
  public void removeListener(ISignalAnalyzerListener paramISignalAnalyzerListener) {
    this._base.removeListener(paramISignalAnalyzerListener);
  }
  
  public void removeAllListeners() {
    this._base.removeAllListeners();
  }
  
  protected class SignalAnalyzerBase {
    private EnumSet _options = SignalAnalyzer.DEFAULT_ANALYSIS_OPTIONS;
    
    private WeakReference _parent = null;
    
    private BasicFilter[] _notch = new BasicFilter[2];
    
    private BasicFilter[] _bandpass = new BasicFilter[2];
    
    private ArrayDeque _samples = new ArrayDeque(512);
    
    private FloatBuffer _leftRawSignal = new FloatBuffer(512);
    
    private FloatBuffer _leftFilteredSignal = new FloatBuffer(512);
    
    private FloatBuffer _rightRawSignal = new FloatBuffer(512);
    
    private FloatBuffer _rightFilteredSignal = new FloatBuffer(512);
    
    private FFTBuffer _leftFFT = new FFTBuffer();
    
    private FFTBuffer _rightFFT = new FFTBuffer();
    
    private a _focusAnalyzer = null;
    
    private ArrayList _listeners = new ArrayList();
    
    private boolean _runAnalyzerThread = true;
    
    private Thread _analyzerThread = null;
    
    private float _analysisRate = 0.1F;
    
    private boolean _waitingForThreadExit = false;
    
    private SignalValidator _signalValidator;
    
    protected void setAnalysisRate(float param1Float) {
      synchronized (this) {
        this._analysisRate = param1Float;
      } 
    }
    
    protected float getAnalysisRate() {
      float f = 0.0F;
      synchronized (this) {
        f = this._analysisRate;
      } 
      return f;
    }
    
    private SignalAnalyzerBase(SignalAnalyzer param1SignalAnalyzer1) {
      this._focusAnalyzer = new t();
      this._parent = new WeakReference<>(param1SignalAnalyzer1);
      u u = new u(1.0F, 50.0F);
      char c = 'ú';
      this._notch[0] = new BasicFilter(BasicFilter.FilterType.NOTCH, c, 60.0F, 0.70710677F);
      this._notch[1] = new BasicFilter(BasicFilter.FilterType.NOTCH, c, 60.0F, 0.70710677F);
      this._bandpass[0] = new BasicFilter(BasicFilter.FilterType.BANDPASS, c, u, 0.70710677F);
      this._bandpass[1] = new BasicFilter(BasicFilter.FilterType.BANDPASS, c, u, 0.70710677F);
      this._signalValidator = new SignalValidator();
    }
    
    protected void enqeueSample(float[] param1ArrayOffloat, boolean param1Boolean) {
      synchronized (this) {
        this._samples.add(param1ArrayOffloat);
        if (!param1Boolean || this._samples.size() >= (int)(this._analysisRate * 250.0F))
          notifyAll(); 
      } 
    }
    
    protected void startAnalyzerThread() {
      synchronized (this) {
        if (this._analyzerThread == null || !this._runAnalyzerThread) {
          this._runAnalyzerThread = true;
          this._analyzerThread = new Thread(new x(this));
          this._analyzerThread.start();
        } 
      } 
    }
    
    protected void stopAnalyzerThread() {
      Thread thread = null;
      synchronized (this) {
        if (this._analyzerThread != null) {
          thread = this._analyzerThread;
          this._analyzerThread = null;
          this._runAnalyzerThread = false;
          this._waitingForThreadExit = true;
          notifyAll();
        } 
      } 
      if (thread != null) {
        boolean bool = true;
        while (bool) {
          try {
            thread.join();
            bool = false;
          } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
          } 
        } 
      } 
    }
    
    private void analyzeInBackground() {
      boolean bool = false;
      synchronized (this) {
        bool = this._runAnalyzerThread;
      } 
      float[] arrayOfFloat1 = null;
      float[] arrayOfFloat2 = null;
      int i = -1;
      while (bool) {
        synchronized (this) {
          int j = (int)(this._analysisRate * 250.0F);
          for (bool = this._runAnalyzerThread; bool && this._samples.size() < j; bool = this._runAnalyzerThread) {
            try {
              wait();
            } catch (InterruptedException interruptedException) {}
          } 
          if (!bool)
            break; 
          if (i != j) {
            arrayOfFloat1 = new float[j];
            arrayOfFloat2 = new float[j];
            i = j;
          } 
          for (byte b = 0; b < j; b++) {
            float[] arrayOfFloat = this._samples.poll();
            arrayOfFloat1[b] = arrayOfFloat[0];
            arrayOfFloat2[b] = arrayOfFloat[1];
          } 
        } 
        AnalysisResult analysisResult1 = analyzeAndUpdate(arrayOfFloat1, true, 0, this._leftFFT, this._leftRawSignal, this._leftFilteredSignal);
        AnalysisResult analysisResult2 = analyzeAndUpdate(arrayOfFloat2, false, 1, this._rightFFT, this._rightRawSignal, this._rightFilteredSignal);
        SignalAnalyzer signalAnalyzer = this._parent.get();
        for (ISignalAnalyzerListener iSignalAnalyzerListener : this._listeners)
          iSignalAnalyzerListener.onAnalyzedSamples(signalAnalyzer, analysisResult1, analysisResult2); 
      } 
      synchronized (this) {
        this._samples.clear();
        this._waitingForThreadExit = false;
        notifyAll();
      } 
    }
    
    protected void addListener(ISignalAnalyzerListener param1ISignalAnalyzerListener) {
      synchronized (this._listeners) {
        if (!this._listeners.contains(param1ISignalAnalyzerListener))
          this._listeners.add(param1ISignalAnalyzerListener); 
      } 
    }
    
    protected void removeListener(ISignalAnalyzerListener param1ISignalAnalyzerListener) {
      synchronized (this._listeners) {
        this._listeners.remove(param1ISignalAnalyzerListener);
      } 
    }
    
    protected void removeAllListeners() {
      synchronized (this._listeners) {
        this._listeners.clear();
      } 
    }
    
    private AnalysisResult analyzeAndUpdate(float[] param1ArrayOffloat, boolean param1Boolean, int param1Int, FFTBuffer param1FFTBuffer, FloatBuffer param1FloatBuffer1, FloatBuffer param1FloatBuffer2) {
      for (float f : param1ArrayOffloat)
        param1FloatBuffer1.a(f); 
      this._notch[param1Int].a(this._bandpass[param1Int], param1ArrayOffloat);
      for (float f : param1ArrayOffloat)
        param1FloatBuffer2.a(f); 
      param1FFTBuffer.update(param1FloatBuffer2);
      AnalysisResult analysisResult = new AnalysisResult();
      AnalysisResult.AnalysisResultBase analysisResultBase = analysisResult.getBase();
      analysisResultBase.rawSignal = param1FloatBuffer1.c();
      analysisResultBase.filteredSignal = param1FloatBuffer2.c();
      analysisResultBase.analysisRate = this._analysisRate;
      analysisResultBase.samplingRate = 250;
      analysisResultBase.sampleIndex = analysisResultBase.filteredSignal.length - (int)(this._analysisRate * 250.0F);
      analysisResultBase.channel = param1Int;
      analysisResult.setUpFFT(param1FloatBuffer2);
      double d = (analysisResultBase.samplingRate * 1);
      analysisResultBase.headbandAlert = this._signalValidator.cumulativeHeadbandAlert(d, analysisResultBase.rawSignal, param1FFTBuffer.getFrequencies(), param1FFTBuffer.getAmplitude());
      if (param1Boolean)
        analysisResultBase.focusScore = this._focusAnalyzer.a(param1FFTBuffer.getAmplitude(), param1FFTBuffer.getFrequencies()); 
      return analysisResult;
    }
  }
  
  public enum AnalysisOptions {
    ANALYZE_LEFT_CHANNEL, ANALYZE_RIGHT_CHANNEL, RESULTS_USE_PRIMITIVE_ARRAYS, RESULTS_USE_FLOAT_BUFFERS, CALCULATE_LEFT_CHANNEL_FOCUS_SCORE, CALCULATE_RIGHT_CHANNEL_FOCUS_SCORE, PERFORM_LEFT_CHANNEL_FFT, PERFORM_RIGHT_CHANNEL_FFT, READ_ONLY;
  }
}


/* Location:              /home/tb/repos/openmelon/src/MPK-Decompile/classes/!/com/axio/melonplatformkit/SignalAnalyzer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */