package com.coolweather.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.coolweather.model.City;
import com.coolweather.model.Country;
import com.coolweather.model.Province;

public class CoolWeatherDB {
	/**
	 * ���ݿ������
	 */
	public static final String DB_NAME="cool_weather";
	/**
	 * ���ݿ�İ汾
	 */
	public static final int VERSON=1;
	
	private static CoolWeatherDB coolWeatherDB;
	private SQLiteDatabase db;
	
	private CoolWeatherDB(Context context){
		CoolWeatherOpenHelper helper=new CoolWeatherOpenHelper(context,DB_NAME,null,VERSON);
		db=helper.getWritableDatabase();
	}
	
	/**
	 * ��ȡʵ��
	 * @param context
	 * @return
	 */
	public static CoolWeatherDB getInstance(Context context){
		if(coolWeatherDB==null){
			coolWeatherDB=new CoolWeatherDB(context);
		}
		return coolWeatherDB;
	}
	/**
	 * �洢provinceʵ�������ݿ�
	 * @param province
	 */
	public void saveProvince(Province province){
		if(province!=null){
			ContentValues values=new ContentValues();
			values.put("provice_name",province.getProvinceName());
			values.put("province_code",province.getPorvinceCode());
			db.insert("Province",null, values);
			
		}
		
	
	}
	
	/**
	 * �����ݿ��ж�ȡȫ�����е�ʡ����Ϣ
	 */
	public List<Province> loadProvince(){
		List<Province> list=new ArrayList<Province>();
		Cursor cursor=db.query("Province",null,null,null,null,null,null);
		if(cursor.moveToFirst()){
			
			do{
				Province province=new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
				province.setPorvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
				list.add(province);
			}
			while(cursor.moveToNext());
			
		}
		if(cursor!=null){
			cursor.close();
		}
		return list;
	}
	
	public void saveCity(City city){
		ContentValues values=new ContentValues();
		values.put("city_name",city.getCityName());
		values.put("city_code",city.getCityCode());
		values.put("province_id",city.getProvinceId());
		db.insert("City", null, values);
		
		
	}
	
	public List<City> loadCities(int provinceId){
		List<City> list=new ArrayList<City>();
		Cursor cursor=db.query("City",null,"province_id=?",new String[]{String.valueOf(provinceId)},null,null,null);
		if(cursor.moveToFirst()){
			do{
				City city=new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				city.setProvinceId(cursor.getInt(cursor.getColumnIndex("province_id")));
				list.add(city);
			}
			while(cursor.moveToNext());
		}
		return list;
	}
	
	public void saveContry(Country country){
		ContentValues values=new ContentValues();
		values.put("country_name",country.getCountryName());
		values.put("country_code",country.getCountryCode());
		values.put("city_id",country.getCityId());
		db.insert("Country",null,values);
	}
	
	public List<Country> loadCountries(int cityId){
		List<Country> list=new ArrayList<Country>();
		Country country=new Country();
		Cursor cursor=db.query("Country",null,"city_id=?",new String[]{String.valueOf(cityId)},null,null,null);
		if(cursor.moveToFirst()){
			do{
				country.setCityId(cityId);
				country.setCountryName(cursor.getString(cursor.getColumnIndex("country_name")));
				country.setCountryCode(cursor.getString(cursor.getColumnIndex("country_code")));
				country.setId(cursor.getInt(cursor.getColumnIndex("id")));
				
				
				
			}
			while(cursor.moveToNext());
		}
		
		return list;
	}

}
