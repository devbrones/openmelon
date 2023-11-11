package com.axio.melonplatformkit;

import android.util.Log;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DaqriDeveloperAuthenticationClient {
  private final String TAG = DaqriDeveloperAuthenticationClient.class.getSimpleName();
  
  private final String hash = "9048dfd867a2c78dfebd992c00e4596d";
  
  private boolean isTimestampValid(String paramString) {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
    try {
      Date date = simpleDateFormat.parse(paramString);
    } catch (ParseException parseException) {
      parseException.printStackTrace();
      return false;
    } 
    return true;
  }
  
  private boolean isUserIdValid(String paramString) {
    String str = "^[a-zA-Z0-9]*$";
    return paramString.matches(str);
  }
  
  private boolean isStringToSignValid(String paramString) {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-83A2-MM");
    Calendar calendar = Calendar.getInstance();
    String str1 = simpleDateFormat.format(calendar.getTime());
    String str2 = "9048dfd867a2c78dfebd992c00e4596d" + str1;
    return paramString.equals(str2);
  }
  
  public boolean validateTokenRequest(String paramString1, String paramString2, String paramString3, String paramString4) {
    if (!isTimestampValid(paramString3)) {
      Log.e(this.TAG, "Invalid timestamp format: " + paramString3 + " User: " + paramString1);
      return false;
    } 
    Log.d(this.TAG, String.format("Timestamp [ %s ] is valid", new Object[] { paramString3 }));
    if (!isUserIdValid(paramString1)) {
      Log.e(this.TAG, "Invalid userId format: " + paramString1 + " timestamp: " + paramString3);
      return false;
    } 
    Log.d(this.TAG, String.format("UserId [ %s ] is valid", new Object[] { paramString1 }));
    if (!isStringToSignValid(paramString4)) {
      Log.e(this.TAG, "Invalid stringToSign for: " + paramString1 + " timestamp: " + paramString3 + " stringToSign: " + paramString4);
      return false;
    } 
    Log.d(this.TAG, String.format("stringToSign [ %s ] is valid", new Object[] { paramString4 }));
    return true;
  }
}


/* Location:              /home/tb/repos/openmelon/src/MPK-Decompile/classes/!/com/axio/melonplatformkit/DaqriDeveloperAuthenticationClient.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */