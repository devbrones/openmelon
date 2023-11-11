package com.axio.melonplatformkit;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import org.json.JSONException;
import org.json.JSONObject;

public class AuthConnection {
  private static final String TAG = AuthConnection.class.getSimpleName();
  
  private static final String authServ = "http://melonauthentication-env.elasticbeanstalk.com/CognitoServlet";
  
  private static final String clientKey = "88b1437a8cc14806ad1ab80469f525061d6c2fae0c64431d822c45de9ebb338d";
  
  private static final String privateKey = "e0734a43f1514fe2b011ec150b3bac09c8b9227b729f498a95395034efd43a1e";
  
  private static String convertStreamToString(InputStream paramInputStream) {
    Scanner scanner = (new Scanner(paramInputStream, "UTF-8")).useDelimiter("\\A");
    return scanner.hasNext() ? scanner.next() : "";
  }
  
  private static String createLoginPacket(LoginCredentials paramLoginCredentials) {
    JSONObject jSONObject = new JSONObject();
    try {
      jSONObject.put("clientKey", "88b1437a8cc14806ad1ab80469f525061d6c2fae0c64431d822c45de9ebb338d");
      jSONObject.put("privateKey", "e0734a43f1514fe2b011ec150b3bac09c8b9227b729f498a95395034efd43a1e");
      jSONObject.put("userId", paramLoginCredentials.getUsername());
    } catch (JSONException jSONException) {
      jSONException.printStackTrace();
      return null;
    } 
    return jSONObject.toString();
  }
  
  public static JSONObject retrieveToken(LoginCredentials paramLoginCredentials) {
    String str = createLoginPacket(paramLoginCredentials);
    System.out.println("loginPacket: " + str);
    JSONObject jSONObject = null;
    if (str != null) {
      URL uRL = null;
      try {
        uRL = new URL("http://melonauthentication-env.elasticbeanstalk.com/CognitoServlet");
      } catch (MalformedURLException malformedURLException) {
        System.out.println("Malformed URL Exception");
        malformedURLException.printStackTrace();
        return null;
      } 
      HttpURLConnection httpURLConnection = null;
      String str1 = null;
      String str2 = null;
      try {
        httpURLConnection = (HttpURLConnection)uRL.openConnection();
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setConnectTimeout(50000);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(httpURLConnection.getOutputStream());
        bufferedOutputStream.write(str.getBytes());
        bufferedOutputStream.close();
        BufferedInputStream bufferedInputStream1 = new BufferedInputStream(httpURLConnection.getInputStream());
        str1 = convertStreamToString(bufferedInputStream1);
        BufferedInputStream bufferedInputStream2 = new BufferedInputStream(httpURLConnection.getErrorStream());
        str2 = convertStreamToString(bufferedInputStream2);
        System.out.println("String response from server: " + str1);
        System.out.println("Error from server: " + str2);
        bufferedInputStream1.close();
        bufferedInputStream2.close();
      } catch (IOException iOException) {
        System.out.println("Problem connecting to server");
        iOException.printStackTrace();
      } finally {
        httpURLConnection.disconnect();
      } 
      if (str1 != null && str1.length() > 0) {
        try {
          jSONObject = new JSONObject(str1);
          System.out.println("JSON response from server: " + jSONObject);
        } catch (JSONException jSONException) {
          jSONException.printStackTrace();
        } 
      } else {
        System.out.println("Response string was null or empty.");
      } 
    } 
    return jSONObject;
  }
}


/* Location:              /home/tb/repos/openmelon/src/MPK-Decompile/classes/!/com/axio/melonplatformkit/AuthConnection.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */