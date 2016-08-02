package com.coolweather.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.coolweather.db.CoolWeatherDB;
import com.coolweather.model.City;
import com.coolweather.model.Country;
import com.coolweather.model.Province;
/**
 * 解析和处理服务器返回的省级数据
 * @author czr
 *
 */
public class Utility {
	public static boolean HandleProvincesResponse(CoolWeatherDB DB,String response){
		if(!TextUtils.isEmpty(response)){
			String[] allProvinces=response.split(",");
			if(allProvinces!=null&&allProvinces.length>0){
				for(String p:allProvinces){
					String[] array=p.split("\\|");
					Province province=new Province();
					province.setProvinceName(array[1]);
					province.setPorvinceCode(array[0]);
					DB.saveProvince(province);
				}
				return true;
			}
		}
		
		return false;
	}
	
	
	public static boolean HandleCityResponse(CoolWeatherDB DB,String response,int provinceId){
		if(!TextUtils.isEmpty(response)){
			String[] allCity=response.split(",");
			if(allCity!=null&&allCity.length>0){
				for(String c:allCity){
					String[] array=c.split("\\|");
					City city=new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					DB.saveCity(city);
				}
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 对country解析并存储到数据库中
	 * @param DB
	 * @param response
	 * @param cityId
	 * @return
	 */
	public static boolean HandleCountryResponse(CoolWeatherDB DB,String response, int cityId){
		if(!TextUtils.isEmpty(response)){
			String[] allCountry=response.split(",");
			if(allCountry!=null&&allCountry.length>0){
				for(String c:allCountry){
					String[] array=c.split("\\|");
					Country country=new Country();
					country.setCountryCode(array[0]);
					country.setCountryName(array[1]);
					country.setCityId(cityId);
					DB.saveContry(country);
				}
				
				return true;
			}
		}
		
		
		
		return false;
	}
	/**
	 * 解析天趣预报JSON数据
	 * @param context
	 * @param response
	 */
	
	public static void handleWeatherResponse(Context context,String response){
		try {
			JSONObject jsonObject=new JSONObject(response);
			JSONObject weatherInfo=jsonObject.getJSONObject("weatherinfo");
			String cityName=weatherInfo.getString("city");
			String weatherCode=weatherInfo.getString("cityid");
			String temp1=weatherInfo.getString("temp1");
			String temp2=weatherInfo.getString("temp2");
			String weatheDesp=weatherInfo.getString("weather");
			String publishTime=weatherInfo.getString("ptime");
			saveWeatherInfo(context,cityName,weatherCode,temp1,temp2,weatheDesp,publishTime);
		//Log.i("data","weather parse joson"+context+cityName+weatherCode+temp1+temp2+weatheDesp+publishTime);	
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	/**
	 * 将服务器返回的所有天气信息存储到SharedPreferences文件中
	 * @param context
	 * @param cityName
	 * @param weatherCode
	 * @param temp1
	 * @param temp2
	 * @param weatheDesp
	 * @param publisthTime
	 */
	public static void saveWeatherInfo(Context context,String cityName,String weatherCode,String temp1,String temp2,String weatherDesp,String publishTime){
		
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy年M月d日",Locale.CHINA);
		SharedPreferences.Editor editor=(Editor) PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected",true);
		editor.putString("city_name",cityName);
		editor.putString("weatherCode",weatherCode);
		editor.putString("temp1",temp1);
		editor.putString("temp2", temp2);
		editor.putString("weather_desp",weatherDesp);
		editor.putString("publish_time",publishTime);
		editor.putString("current_date",sdf.format(new Date()));
		editor.commit();
		
		
		
		
		
		
		
	}
}
