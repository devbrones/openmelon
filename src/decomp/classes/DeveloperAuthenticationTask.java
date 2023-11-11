package com.axio.melonplatformkit;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.amazonaws.regions.Regions;

public class DeveloperAuthenticationTask extends AsyncTask {
  private final String TAG = DeveloperAuthenticationTask.class.getSimpleName();
  
  private String userName;
  
  private boolean isSuccessful;
  
  private Context context;
  
  public DeveloperAuthenticationTask(Context paramContext) {
    this.context = paramContext;
  }
  
  protected Void doInBackground(Object... paramVarArgs) {
    LoginCredentials loginCredentials = (LoginCredentials)paramVarArgs[0];
    Context context = (Context)paramVarArgs[1];
    DeveloperAuthenticationProvider developerAuthenticationProvider = new DeveloperAuthenticationProvider(null, "us-east-1:750fe194-7155-48c2-9c43-4e2faf1e1440", Regions.US_EAST_1);
    this.userName = loginCredentials.getUsername();
    String str1 = loginCredentials.getPassword();
    Log.d(this.TAG, "logging in for S3File with userName " + this.userName);
    developerAuthenticationProvider.login(this.userName, str1, context);
    String str2 = developerAuthenticationProvider.getToken();
    Log.d(this.TAG, "S3File authentication token: " + str2);
    S3File._authenticationProvider = developerAuthenticationProvider;
    this.userName = loginCredentials.getUsername();
    this.isSuccessful = (str2 != null);
    if (this.isSuccessful)
      System.out.println("token: " + str2); 
    return null;
  }
  
  protected void onPostExecute(Void paramVoid) {
    if (!this.isSuccessful)
      (new AlertDialog.Builder(this.context)).setTitle("Login error").setMessage("Username or password do not match!!").show(); 
  }
}


/* Location:              /home/tb/repos/openmelon/src/MPK-Decompile/classes/!/com/axio/melonplatformkit/DeveloperAuthenticationTask.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */