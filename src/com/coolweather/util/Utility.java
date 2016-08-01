package com.coolweather.util;

import android.text.TextUtils;

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
}
