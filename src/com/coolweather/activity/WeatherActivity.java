 package com.coolweather.activity;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coolweather.util.HttpCallBackListener;
import com.coolweather.util.HttpUtil;
import com.coolweather.util.Utility;
import com.example.coolweather.R;

public class WeatherActivity extends Activity{
	private LinearLayout weatherInfoLayout;
	private TextView cityNameText;
	private TextView publishText;
	private TextView weatherDespText;
	private TextView temp1Text;
	private TextView temp2Text;
	private TextView currentDateText;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		weatherInfoLayout=(LinearLayout) findViewById(R.id.weather_info_layout);
		cityNameText=(TextView) findViewById(R.id.city_name);
		publishText=(TextView) findViewById(R.id.publish_text);
		weatherDespText=(TextView) findViewById(R.id.weather_desp);
		temp1Text=(TextView) findViewById(R.id.temp1);
		temp2Text=(TextView) findViewById(R.id.temp2);
		currentDateText=(TextView) findViewById(R.id.current_data);
		
		String countryCode=getIntent().getStringExtra("country_code");
		
		if(!TextUtils.isEmpty(countryCode)){
			publishText.setText("同步中。。。");
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);

			queryWeatherCode(countryCode);
			
		}else{
			//有没有县级代码时，显示本地天气
			showWeather();
		}
		
	
		
	}
	/**
	 * 查询天气信息
	 * @param weatherCode
	 */
	private void queryWeaterInfo(String weatherCode){
		String address="http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
	
//Log.i("data","weatherInfo address"+address);		
		
		queryWeatherCodeFromService(address,"weatherCode");
	}
	
	public void queryWeatherCodeFromService(final String address,String weatherCode){
		
		new Thread(new Runnable(){
			HttpURLConnection conn;
			URL url;
			InputStream input;
			BufferedReader reader;
			@Override
			public void run() {
				try {
					url=new URL(address);
					conn=(HttpURLConnection) url.openConnection();
					input=conn.getInputStream();
					reader=new BufferedReader(new InputStreamReader(input));
					char[] data=new char[150];
					int len=reader.read(data);
					String content=String.valueOf(data,0,len);
					//Log.i("data","weatherIfo:  "+content);
					Utility.handleWeatherResponse(WeatherActivity.this,content);
					
					
					runOnUiThread(new Runnable(){
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							showWeather();
						}});
					
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
					if(conn!=null){
					conn.disconnect();
					}
					try {
						if(input!=null){
							input.close();
						}
						if(reader!=null){
							reader.close();
						}
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					
				}
				
			}}).start();
	}
	
	/**
	 * 查询县级代码对应的天气
	 * @param countryCode
	 */
	private void queryWeatherCode(String countryCode){
		String address="http://www.weather.com.cn/data/list3/city"+countryCode+".xml";
		
		queryFromService(address,"countryCode");
	}
	/**
	 * 根据传入的地址和类型区向服务器查询天气代码或者天气信息
	 * @param address
	 * @param type
	 */
	private void queryFromService(final String address,final String type){
		HttpUtil.sendHttpRequest(address, new HttpCallBackListener(){

			@Override
			public void onfinish(String request) {
				// TODO Auto-generated method stub

				if("countryCode".equals(type)){
					if(!TextUtils.isEmpty(request)){
						//从服务器中解析天气代码
						String[] array=request.split("\\|");
						if(array!=null&&array.length==2){
							String weatherCode=array[1];

							queryWeaterInfo(weatherCode);
							
						}
					}
//				}else if("weatherCode".equals(type)){
//
//					Utility.handleWeatherResponse(WeatherActivity.this,request);
//
//					runOnUiThread(new Runnable(){
//
//						@Override
//						public void run() {
//							// TODO Auto-generated method stub
//							showWeather();
//							
//						}});
				}
			}

			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable(){

					@Override
					public void run() {
						// TODO Auto-generated method stub
						publishText.setText("同步失败");
					}});			
				}});
		
	}
	
	/**
	 * 查询本地天气
	 */
	private void showWeather(){
		SharedPreferences sp=PreferenceManager.getDefaultSharedPreferences(this);
		cityNameText.setText(sp.getString("city_name",""));
		temp1Text.setText(sp.getString("temp1",""));
		temp2Text.setText(sp.getString("temp2",""));
		weatherDespText.setText(sp.getString("weather_desp",""));
		publishText.setText(sp.getString("publish_time","")+"发布");
		currentDateText.setText(sp.getString("current_date",""));
		Log.i("data",sp.getString("current_date",""));
		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
		
	}
	/**
	 *按钮点击事件，重新选择城市
	 * @param view
	 */
	public void switchCity(View view){
		Intent intent=new Intent(WeatherActivity.this,ChooseAreaActivity.class);
		intent.putExtra("isFromWeatherActivity",true);
		startActivity(intent);
	}
	/**
	 * 按钮点击事件，刷新天气预报
	 * @param view
	 */
	public void refreshWeather(View view){
		SharedPreferences sp=PreferenceManager.getDefaultSharedPreferences(this);
		String weatherCode=sp.getString("weatherCode","");
		if(!TextUtils.isEmpty(weatherCode)){
		queryWeaterInfo(weatherCode);
		}
	}
}
