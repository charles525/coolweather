package com.coolweather.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

public class HttpUtil {

	public static void sendHttpRequest(final String address,final HttpCallBackListener listener){
		
		new Thread(
				new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						URL url=null;
						HttpURLConnection conn=null;
						BufferedReader reader=null;
						InputStream input=null;
						StringBuilder response=new StringBuilder();
						
						
						try {
							url=new URL(address);
							 conn=(HttpURLConnection) url.openConnection();
							 //conn.connect();
							 conn.setRequestMethod("GET");
							   conn.setRequestProperty("Content-type", "text/html");
//							   conn.setRequestProperty("Accept-Charset", "utf-8");
//							   conn.setRequestProperty("contentType", "utf-8");
							 
							 conn.setConnectTimeout(8000);
							 conn.setReadTimeout(8000);
							// Log.i("data",conn.getResponseCode()+"");
							 input= conn.getInputStream();
							 reader=new BufferedReader(new InputStreamReader(input,"utf-8"));
							 String line=null;

							 while((line=reader.readLine())!=null){

								response.append(line);
							 }
//							char[] data=new char[150];
//							int len=reader.read(data);
//							String str=String.valueOf(data,0,len);
//							Log.i("data",str);
//							 
							 
							 
							 if(listener!=null){
								 listener.onfinish(response.toString());
							 }
							 
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							if(listener!=null){
								listener.onError(e);
							}
						}finally{
							if(conn!=null){
								conn.disconnect();
							}
							
								try {
									
									if(reader!=null)
									reader.close();
									
									if(input!=null){
										input.close();
									}
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							
							
							
							
						}	
						
					}}).start();
	}
}
