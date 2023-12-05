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
    
    protected void enqeueSample(float[] sample, boolean inv_lastSample) {
      synchronized (this) {
        this._samples.add(sample);
        if (!inv_lastSample || this._samples.size() >= (int)(this._analysisRate * 250.0F))
          // wake up threads if there are enough samples
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
    
    private void analyzeInBackground() { // this is the method that is called by the thread
      boolean bool = false;
      synchronized (this) {
        bool = this._runAnalyzerThread;
      } 

      float[] channel1 = null; // define both channels
      float[] channel2 = null; // 
      
      int i = -1;
      while (bool) {
        synchronized (this) {
          int nSamples = (int)(this._analysisRate * 250.0F); // 250 samples per second

          for (bool = this._runAnalyzerThread; bool && this._samples.size() < nSamples; bool = this._runAnalyzerThread) { // wait for enough samples
            try {
              wait();
            } catch (InterruptedException interruptedException) {}
          } 

          if (!bool) // if the thread is stopped,
            break; 

          if (i != nSamples) { 
            channel1 = new float[nSamples];
            channel2 = new float[nSamples];
            i = nSamples;
          } 

          for (byte b = 0; b < nSamples; b++) { // iterate through the samples
            float[] arrayOfFloat = this._samples.poll();
            channel1[b] = arrayOfFloat[0];
            channel2[b] = arrayOfFloat[1];
          } 
        } 
        AnalysisResult analysisResult1 = analyzeAndUpdate(channel1, true, 0, this._leftFFT, this._leftRawSignal, this._leftFilteredSignal);
        AnalysisResult analysisResult2 = analyzeAndUpdate(channel2, false, 1, this._rightFFT, this._rightRawSignal, this._rightFilteredSignal);
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
    
    private AnalysisResult analyzeAndUpdate(float[] channeldata, boolean param1Boolean, int channelid, FFTBuffer param1FFTBuffer, FloatBuffer channelrawsignal, FloatBuffer channelfilteredsignal) {
      for (float f : channeldata) // update the raw signal
        channelrawsignal.a(f); // floatbuffer a 

      this._notch[channelid].a(this._bandpass[channelid], channeldata);
      
      for (float f : channeldata)
        channelfilteredsignal.a(f); // floatbuffer a

      param1FFTBuffer.update(channelfilteredsignal);
      AnalysisResult analysisResult = new AnalysisResult();
      AnalysisResult.AnalysisResultBase analysisResultBase = analysisResult.getBase();
      analysisResultBase.rawSignal = channelrawsignal.c();
      analysisResultBase.filteredSignal = channelfilteredsignal.c();
      analysisResultBase.analysisRate = this._analysisRate;
      analysisResultBase.samplingRate = 250;
      analysisResultBase.sampleIndex = analysisResultBase.filteredSignal.length - (int)(this._analysisRate * 250.0F);
      analysisResultBase.channel = channelid;
      analysisResult.setUpFFT(channelfilteredsignal);
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