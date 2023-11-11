package com.axio.melonplatformkit;

import android.content.Context;
import android.util.Log;
import com.amazonaws.auth.AWSAbstractCognitoDeveloperIdentityProvider;
import com.amazonaws.regions.Regions;
import org.json.JSONException;
import org.json.JSONObject;

public class DeveloperAuthenticationProvider extends AWSAbstractCognitoDeveloperIdentityProvider {
  private final String TAG = DeveloperAuthenticationProvider.class.getSimpleName();
  
  private static final String _providerName = "MelonCognito";
  
  private String _cachedIdentityId;
  
  private static DaqriDeveloperAuthenticationClient devAuthClient;
  
  private String identityPoolId;
  
  private String userId;
  
  private String password;
  
  private Context context;
  
  public DeveloperAuthenticationProvider(String paramString1, String paramString2, Regions paramRegions) {
    super(paramString1, paramString2, paramRegions);
    devAuthClient = new DaqriDeveloperAuthenticationClient();
  }
  
  public String getProviderName() {
    return "MelonCognito";
  }
  
  public void login(String paramString1, String paramString2, Context paramContext) {
    this.userId = paramString1;
    this.password = paramString2;
    this.context = paramContext;
    Log.d(this.TAG, "userName: " + paramString1);
    callBackend();
  }
  
  private void callBackend() {
    JSONObject jSONObject = AuthConnection.retrieveToken(new LoginCredentials(this.userId, this.password));
    if (jSONObject == null) {
      Log.e(this.TAG, "AuthConnection returned null");
    } else {
      Log.d(this.TAG, "json for S3File from server: " + jSONObject);
    } 
    this.token = null;
    try {
      this.identityId = String.valueOf(jSONObject.get("id"));
      this.token = String.valueOf(jSONObject.get("token"));
      this.identityPoolId = String.valueOf(jSONObject.get("identityPoolId"));
      this._cachedIdentityId = this.identityId;
      update(this.identityId, this.token);
    } catch (JSONException jSONException) {
      jSONException.printStackTrace();
    } 
  }
  
  public String refresh() {
    if (this.token == null || this.identityId == null) {
      setToken(null);
      callBackend();
      update(this.identityId, this.token);
    } 
    return this.token;
  }
  
  public String getIdentityId() {
    this.identityId = this._cachedIdentityId;
    if (this.identityId == null) {
      callBackend();
      return this.identityId;
    } 
    return this.identityId;
  }
  
  public static DaqriDeveloperAuthenticationClient getDevAuthClientInstance() {
    if (devAuthClient == null)
      throw new IllegalStateException("Dev Auth Client not initialized yet"); 
    return devAuthClient;
  }
}


/* Location:              /home/tb/repos/openmelon/src/MPK-Decompile/classes/!/com/axio/melonplatformkit/DeveloperAuthenticationProvider.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */