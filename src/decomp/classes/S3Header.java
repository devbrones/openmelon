package com.axio.melonplatformkit;

import java.util.HashMap;
import org.json.JSONObject;

public class S3Header {
  HashMap _data = new HashMap<>();
  
  public void put(String paramString1, String paramString2) {
    this._data.put(paramString1, paramString2);
  }
  
  public String toString() {
    JSONObject jSONObject = new JSONObject(this._data);
    null = jSONObject.toString();
    return Integer.toString(null.length() + 15) + "\n" + null + "\n";
  }
  
  protected String get(String paramString) {
    return (String)this._data.get(paramString);
  }
}


/* Location:              /home/tb/repos/openmelon/src/MPK-Decompile/classes/!/com/axio/melonplatformkit/S3Header.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */