package com.coolweather.util;

import java.io.BufferedReader;
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
						URL url;
						HttpURLConnection conn=null;
						BufferedReader reader;
						InputStream input;
						StringBuilder response=new StringBuilder();
						
						try {
							url=new URL(address);
							 conn=(HttpURLConnection) url.openConnection();
							 conn.setRequestMethod("GET");
							 //conn.setConnectTimeout(8000);
							// conn.setReadTimeout(8000);
							 input=conn.getInputStream();
							 reader=new BufferedReader(new InputStreamReader(input));
							 String line="";
							 while((line=reader.readLine())!=null){
								response.append(line);
							 }
							
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
						}
					
						
						
						
						
						
					}}).start();
	}
}
