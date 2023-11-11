package com.axio.melonplatformkit;

public class LoginCredentials {
  private final String TAG = LoginCredentials.class.getSimpleName();
  
  private String username;
  
  private String password;
  
  public LoginCredentials(String paramString1, String paramString2) {
    this.username = paramString1;
    this.password = paramString2;
  }
  
  public String getUsername() {
    return this.username;
  }
  
  public void setUsername(String paramString) {
    this.username = paramString;
  }
  
  public String getPassword() {
    return this.password;
  }
  
  public void setPassword(String paramString) {
    this.password = paramString;
  }
}


/* Location:              /home/tb/repos/openmelon/src/MPK-Decompile/classes/!/com/axio/melonplatformkit/LoginCredentials.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */