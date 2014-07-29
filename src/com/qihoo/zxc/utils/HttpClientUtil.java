package com.qihoo.zxc.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.qihoo.zxc.constant.LittleBoyApplication;
import com.qihoo.zxc.model.PackageHidden;



public class HttpClientUtil {
	final static String TAG = "HttpClientUtil";
	public static enum RETURN_TYPE{
		NETWORK,ERROR_PCK,NOAPP,NOTGAME,NORMAL
	} 
	
	public static RETURN_TYPE checkGame(String pck) {
		Log.d(TAG, "checkGame");
		URL url;
		try {
			url = new URL("http://openbox.mobilem.360.cn/mintf/getAppInfoByIds?pname=" + pck);
			Log.d(TAG, "http://openbox.mobilem.360.cn/mintf/getAppInfoByIds?pname=" + pck);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(15000);
			if (conn.getResponseCode() != 200) {
				return RETURN_TYPE.NETWORK;
			}
			StringBuffer stringBuffer = new StringBuffer();
			String lines;
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			while ((lines = reader.readLine()) != null) {
				stringBuffer.append(lines);
			}
			
			JSONObject jsonObject = new JSONObject(stringBuffer.toString());
			int errno = jsonObject.getInt("errno");
			if (0 != errno) {//有错误
				Log.d(TAG, "RETURN_TYPE.ERROR_PCK");
				LittleBoyApplication.getInstance().myHelper.saveAppHidden(pck, 0, "", 0,0);
				return RETURN_TYPE.ERROR_PCK;
			}
			int total = jsonObject.getInt("total");
			if (0 >= total) {
				Log.d(TAG, "RETURN_TYPE.NOAPP");
				LittleBoyApplication.getInstance().myHelper.saveAppHidden(pck, 0, "", 0,0);
				return RETURN_TYPE.NOAPP;
			}
			JSONArray datas = jsonObject.getJSONArray("data");
			JSONObject data0 = (JSONObject) datas.opt(0);
			String type = data0.getString("type");
			if (!type.equals("game")) {
				Log.d(TAG, "RETURN_TYPE.NOTGAME");
				LittleBoyApplication.getInstance().myHelper.saveAppHidden(pck, 0, "", 0,0);
				return RETURN_TYPE.NOTGAME;
			}
			JSONObject gl = null;
			String strategy = "";
			try {
				gl = data0.getJSONObject("community").getJSONObject("gl");
				strategy = gl.toString();
//				for (Iterator<String> iterator = gl.keys();iterator.hasNext();) {
//					String k = iterator.next();
//					JSONObject ob = gl.getJSONObject(k);
//					try {
//						strategy = ob.getString("more");
//					} catch (JSONException e) {
//						// 无攻略
//						strategy = "";
//					}
//					if (!strategy.equals("")) {
//						//有攻略,写数据库
//						break;
//					}
//				}
			} catch (JSONException e) {
				strategy = "";
			}
			int hasGift = 0;
			try {
				if(data0.getJSONObject("community").has("libao"))
					hasGift = 1;
			} catch (JSONException e) {
				hasGift = 0;
			}
			
			LittleBoyApplication.getInstance().myHelper.saveAppHidden(pck, 0, strategy, 1,hasGift);
			Log.d(TAG, pck+"-RETURN_TYPE.NORMAL: 是否有礼包-"+hasGift + "是否有攻略" + !strategy.equals(""));
			return RETURN_TYPE.NORMAL;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return RETURN_TYPE.NETWORK;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LittleBoyApplication.getInstance().myHelper.saveAppHidden(pck, 0, "", 0,0);
			return RETURN_TYPE.ERROR_PCK;
		}catch (NullPointerException e) {
			// TODO: handle exception
			LittleBoyApplication.getInstance().myHelper.saveAppHidden(pck, 0, "", 0,0);
			return RETURN_TYPE.ERROR_PCK;
		}
	}
}
