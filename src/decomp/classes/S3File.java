package com.axio.melonplatformkit;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCognitoIdentityProvider;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;
import com.amazonaws.mobileconnectors.s3.transfermanager.Upload;
import com.amazonaws.regions.Regions;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class S3File {
  private static final String TAG = S3File.class.getSimpleName();
  
  private final String DATABREAK = ".__data_region";
  
  private String _userId;
  
  private String _timestamp;
  
  private S3Header _header;
  
  private String _filename;
  
  private int _numberOfWrites;
  
  private int _minWrites;
  
  private int _maxWrites;
  
  private FileOutputStream _stream;
  
  private int _sequenceNum;
  
  protected static String token;
  
  protected static String identityId;
  
  protected static DeveloperAuthenticationProvider _authenticationProvider;
  
  private CognitoCachingCredentialsProvider _credentialsProvider;
  
  private static final String _bucket = "com.axio.melon";
  
  private boolean writing = false;
  
  private LocationManager locationManager;
  
  private Location location;
  
  private Context _context;
  
  private String locationAdmin;
  
  private boolean isInitialized = false;
  
  private Thread thread;
  
  private static final ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();
  
  private static S3File _instance = new S3File();
  
  protected S3File() {
    float f = 83.0F;
    this._maxWrites = (int)(f * 60.0F);
    this._minWrites = (int)(f * 15.0F);
  }
  
  private void init(S3Header paramS3Header, Context paramContext) {
    Log.d(TAG, "*** init ***");
    this._header = paramS3Header;
    this._userId = this._header.get("userId");
    this._context = paramContext;
    this._numberOfWrites = 0;
    this._sequenceNum = 0;
    Log.d(TAG, "userId: " + this._userId);
    LoginCredentials loginCredentials = new LoginCredentials(this._userId, "dummyPword");
    (new DeveloperAuthenticationTask(paramContext)).execute(new Object[] { loginCredentials, this._context });
    this.locationManager = (LocationManager)this._context.getSystemService("location");
    this.location = this.locationManager.getLastKnownLocation("gps");
    if (this.location != null) {
      Geocoder geocoder = new Geocoder(this._context, Locale.getDefault());
      List<Address> list = null;
      try {
        list = geocoder.getFromLocation(this.location.getLatitude(), this.location.getLongitude(), 1);
        this.locationAdmin = ((Address)list.get(0)).getAdminArea();
      } catch (IOException iOException) {
        iOException.printStackTrace();
        this.locationAdmin = "NA";
      } 
    } 
    refreshFile();
    this.writing = true;
    String str = this._header.toString();
    try {
      this._stream.write(str.getBytes());
      this._stream.write(".__data_region".getBytes());
    } catch (IOException iOException) {
      iOException.printStackTrace();
    } 
    this.isInitialized = true;
    v v = new v(this);
    worker.schedule(v, 1L, TimeUnit.SECONDS);
  }
  
  public static boolean isInitialized() {
    return (_instance == null) ? false : _instance.isInitialized;
  }
  
  public static void startRecording(S3Header paramS3Header, Context paramContext) {
    _instance = new S3File();
    Log.d(TAG, "header: " + paramS3Header);
    Log.d(TAG, "context: " + paramContext);
    _instance._header = paramS3Header;
    _instance.init(paramS3Header, paramContext);
  }
  
  public static S3File getInstance() {
    return _instance;
  }
  
  private void uploadFile(CognitoCachingCredentialsProvider paramCognitoCachingCredentialsProvider, DeveloperAuthenticationProvider paramDeveloperAuthenticationProvider, String paramString, File paramFile) {
    if (paramCognitoCachingCredentialsProvider == null && paramDeveloperAuthenticationProvider != null)
      paramCognitoCachingCredentialsProvider = new CognitoCachingCredentialsProvider(this._context, (AWSCognitoIdentityProvider)paramDeveloperAuthenticationProvider, Regions.US_EAST_1); 
    if (paramCognitoCachingCredentialsProvider != null) {
      Log.d(TAG, "credentials: " + paramCognitoCachingCredentialsProvider.getToken());
      TransferManager transferManager = new TransferManager((AWSCredentialsProvider)paramCognitoCachingCredentialsProvider);
      Upload upload = transferManager.upload(paramString, paramFile.getName(), paramFile);
      Log.d(TAG, "Starting upload: " + upload.getDescription());
      try {
        upload.waitForCompletion();
        Log.d(TAG, "Upload complete.");
      } catch (AmazonClientException amazonClientException) {
        Log.e(TAG, "Unable to upload file, upload was aborted.");
        amazonClientException.printStackTrace();
      } catch (InterruptedException interruptedException) {
        Log.e(TAG, "Unable to upload file, interrupted.");
        interruptedException.printStackTrace();
      } 
      transferManager.shutdownNow();
    } 
    paramFile.delete();
    setCredentialsProvider(paramCognitoCachingCredentialsProvider);
  }
  
  private Thread runUpload() {
    File file = this._context.getFileStreamPath(this._filename);
    w w = new w(this, file);
    return new Thread(w);
  }
  
  private void flushInstance() {
    this.isInitialized = false;
    if (this._context == null)
      return; 
    File file = this._context.getFileStreamPath(this._filename);
    if (this._numberOfWrites > this._minWrites) {
      Thread thread = runUpload();
      thread.start();
    } 
    this.writing = false;
    try {
      this._stream.close();
    } catch (IOException iOException) {
      iOException.printStackTrace();
    } 
  }
  
  public static void setCredentialsProvider(CognitoCachingCredentialsProvider paramCognitoCachingCredentialsProvider) {
    _instance._credentialsProvider = paramCognitoCachingCredentialsProvider;
  }
  
  public static void flush() {
    if (_instance != null)
      _instance.flushInstance(); 
  }
  
  private void ratchetS3File() {
    Log.d(TAG, "Ratcheting S3File: " + this._filename);
    try {
      this._stream.close();
    } catch (IOException iOException) {
      iOException.printStackTrace();
    } 
    Thread thread = runUpload();
    thread.start();
    this._sequenceNum++;
    refreshFile();
    String str = this._header.toString();
    try {
      this._stream.write(str.getBytes());
      this._stream.write(".__data_region".getBytes());
    } catch (IOException iOException) {
      iOException.printStackTrace();
    } 
  }
  
  private void refreshFile() {
    this._timestamp = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z")).format(new Date());
    this._filename = this._userId + "_" + this._timestamp;
    try {
      this._stream = this._context.openFileOutput(this._filename, 0);
    } catch (FileNotFoundException fileNotFoundException) {
      fileNotFoundException.printStackTrace();
    } 
    this._header.put("locationAdminCode", this.locationAdmin);
    this._header.put("timestamp", this._timestamp);
    this._header.put("sequenceNum", Integer.toString(this._sequenceNum));
    Log.d(TAG, this._header.toString());
  }
  
  protected void writeBytesToS3File(byte[] paramArrayOfbyte) {
    this._numberOfWrites++;
    try {
      this._stream.write(paramArrayOfbyte);
    } catch (IOException iOException) {
      Log.e(TAG, "Problem writing bytes to file");
      iOException.printStackTrace();
    } 
    if (this._numberOfWrites > this._maxWrites) {
      this._numberOfWrites = 0;
      ratchetS3File();
    } 
  }
}


/* Location:              /home/tb/repos/openmelon/src/MPK-Decompile/classes/!/com/axio/melonplatformkit/S3File.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */