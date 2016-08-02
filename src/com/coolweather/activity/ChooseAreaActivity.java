 package com.coolweather.activity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.db.CoolWeatherDB;
import com.coolweather.model.City;
import com.coolweather.model.Country;
import com.coolweather.model.Province;
import com.coolweather.util.HttpCallBackListener;
import com.coolweather.util.HttpUtil;
import com.coolweather.util.Utility;
import com.example.coolweather.R;

public class ChooseAreaActivity extends Activity {

	public static final int LEVEL_PROVINCE=0;
	public static final int LEVEL_CITY=1;
	public static final int LEVEL_COUNTRY=2;
	
	private ProgressDialog progressDialog;
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private CoolWeatherDB coolWeatherDB;
	private List<String > dataList=new ArrayList<String>();
	private ProgressDialog progressDailog;
	/**
	 * 省列表
	 */
	private List<Province> provinceList;
	
	/**
	 * 市列表
	 */
	
	private List<City> cityList;
	
/**
 * 县列表
 */
	private List<Country> countryList;
/**
 * 选中的省份
 */
	private Province selectProvince;
	/**
	 * 选中的市
	 */
	private City selectCity;
	/**
	 * 当前选中的级别
	 */
	private int currentLevel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		/**
		 * 地区已选择，则直接跳转到天气预报界面WeatherActivity
		 */
		SharedPreferences sp=PreferenceManager.getDefaultSharedPreferences(this);
		if(sp.getBoolean("city_selected",false)){
			Intent intent=new Intent(this,WeatherActivity.class);
			startActivity(intent);
			finish();
		}
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);

		
		
	
		coolWeatherDB= CoolWeatherDB.getInstance(this);
		titleText=(TextView) findViewById(R.id.title_text);
		listView=(ListView) findViewById(R.id.list_view);
		adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
					// TODO Auto-generated method stub
					
					if(currentLevel==LEVEL_PROVINCE){
						selectProvince=provinceList.get(arg2);
						queryCity();
					}else if(currentLevel==LEVEL_CITY){
						selectCity=cityList.get(arg2);
						queryCountry();
					}else if(currentLevel==LEVEL_COUNTRY){
					   String countryCode=countryList.get(arg2).getCountryCode();
					   Intent intent=new Intent(ChooseAreaActivity.this,WeatherActivity.class);
					   intent.putExtra("country_code",countryCode);
					   startActivity(intent);
					   finish();
					}
					
				}
			});
		
		queryProvince();
	}
		
	/**
	 * 查询全国所有的省，优先从数据库查询，如果没有查询到灾区服务器上查询
	 */
		public void queryProvince(){
			provinceList=coolWeatherDB.loadProvince();
			
			if(provinceList.size()>0){
				dataList.clear();
				for(Province province:provinceList){
					dataList.add(province.getProvinceName());
					
				}
				adapter.notifyDataSetChanged();
				listView.setSelection(0);
				titleText.setText("中国");
				currentLevel=LEVEL_PROVINCE;
				
			}else{
				queryFromServer(null,"province");
			}
		}
		
		/**
		 * 查询全国所有市
		 */
		public void queryCity(){
			cityList=coolWeatherDB.loadCities(selectProvince.getId());
			if(cityList.size()>0){
				dataList.clear();
				for(City c:cityList){
					dataList.add(c.getCityName());
				}
				adapter.notifyDataSetChanged();
				listView.setSelection(0);
				titleText.setText(selectProvince.getProvinceName());
				currentLevel=LEVEL_CITY;
			}else{
				queryFromServer(selectProvince.getPorvinceCode(),"city");
			}
			
		}
		
		public void queryCountry(){
			
			countryList=coolWeatherDB.loadCountries(selectCity.getId());
			if(countryList.size()>0){
				dataList.clear();
				for(Country c:countryList){
					dataList.add(c.getCountryName());
					
				}
				adapter.notifyDataSetChanged();
				listView.setSelection(0);
				titleText.setText(selectCity.getCityName());
				currentLevel=LEVEL_COUNTRY;
			}else{
				queryFromServer(selectCity.getCityCode(),"country");
			}
			
		}
		/**
		 * 根据传入的代码和类型从服务器上查询省市县数据
		 */
		public void queryFromServer(String code,final String type){
			String address;
			if(!TextUtils.isEmpty(code)){
				address="http://www.weather.com.cn/data/list3/city"+code+".xml";
			}else{
				address="http://www.weather.com.cn/data/list3/city.xml";
			}
			showProgressDialog();
			HttpUtil.sendHttpRequest(address, new HttpCallBackListener(){

				@Override
				public void onfinish(String response) {
					// TODO Auto-generated method stub
					boolean result=false;
					if("province".equals(type)){
						result=Utility.HandleProvincesResponse(coolWeatherDB, response);
					}else if("city".equals(type)){
						result=Utility.HandleCityResponse(coolWeatherDB, response, selectProvince.getId());
					}else if("country".equals(type)){
						result=Utility.HandleCountryResponse(coolWeatherDB, response,selectCity.getId());
					}
					
					if(result){
						runOnUiThread(new Runnable(){//回到主线程

							@Override
							public void run() {
								// TODO Auto-generated method stub
								closeProgressDialog();
								if("province".equals(type)){
									queryProvince();
								}else if("city".equals(type)){
									queryCity();
								}else if("country".equals(type)){
									queryCountry();
								}
							}});
					}
					
				}

				@Override
				public void onError(Exception e) {
					// TODO Auto-generated method stub
					runOnUiThread(new Runnable(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							closeProgressDialog();
							Toast.makeText(ChooseAreaActivity.this,"加载失败",Toast.LENGTH_SHORT).show();
						}});
				}});
		}
		
		/**
		 * 显示进度对话框
		 */
		private void showProgressDialog(){
			if(progressDailog==null){
				progressDailog=new ProgressDialog(this);
				progressDailog.setMessage("加载中");
				progressDailog.setCancelable(false);
			}
			progressDailog.show();
		}
		private void closeProgressDialog(){
			if(progressDailog!=null){
				progressDailog.dismiss();
			}
		}
		
		@Override
		public void onBackPressed() {
			// TODO Auto-generated method stub
			if(currentLevel==LEVEL_COUNTRY){
				queryCity();
			}else if(currentLevel==LEVEL_CITY){
				queryProvince();
			}else{
				finish();
			}
		}
		
		
		
		
		
		
		
		
		
		
}
