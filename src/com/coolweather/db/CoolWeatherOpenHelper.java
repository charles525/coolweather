package com.coolweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class CoolWeatherOpenHelper extends SQLiteOpenHelper {
	
	public static final String CREATE_PROVICE="create table Province{ " +
			" id integer primary autoincrement," +
			"province_name text," +
			"province_code text}";
	
	public static final String CREATE_CITY="create table City{" +
			"id integer primary autoincrement," +
			"city_name text," +
			"city_code text," +
			"province_id integer}";
	public static final String CREATE_CONTRY="create table Country{" +
			"id integer primary autoincrement," +
			"country_name text," +
			"country_code text," +
			"city_id integer}";

	public CoolWeatherOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CREATE_PROVICE);//����province ��
		db.execSQL(CREATE_CITY);//����city��
		db.execSQL(CREATE_CONTRY);//����country��
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}