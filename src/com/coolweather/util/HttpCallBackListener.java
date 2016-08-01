package com.coolweather.util;

public interface HttpCallBackListener {
		void onfinish(String request);
		void onError(Exception e);
		
	
}
