package com.example.simpleweather1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.xml.sax.DocumentHandler;

import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.DocumentsContract.Document;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

	private TextView temp;
	private TextView status;
	private TextView direction;
	private Button city;
	private Button district;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		temp = (TextView) findViewById(R.id.temp);
		status = (TextView) findViewById(R.id.status);
		direction = (TextView) findViewById(R.id.direction);
		city = (Button) findViewById(R.id.province);
		district = (Button) findViewById(R.id.city);
		closeStrictMode();
		
		direction.setText(getweather("茂名").toString());
	}
	
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressLint("NewApi")
	public void closeStrictMode() {
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
	}

	public List<Map<String, Object>> getweather(String city) {
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		try {
			String xml = getxml(URLEncoder.encode(city, "utf-8"));
			list = parsexml(xml, city);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;

	}

	private String getxml(String city) {
		// TODO Auto-generated method stub

		String requesturl = "http://api.map.baidu.com/telematics/v3/weather?location="
				+ city + "&output=xml&ak=x0IQ4RBWIjSvIqVVBC91DmjZ";
		StringBuffer stringBuffer = new StringBuffer();

		try {
			URL url = new URL(requesturl);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url
					.openConnection();
			httpURLConnection.setDoInput(true);
			httpURLConnection.setRequestMethod("GET");
			int resposecode = httpURLConnection.getResponseCode();
			if (resposecode == 200) {
				InputStream impStream = httpURLConnection.getInputStream();
//				Log.i("e", "error");
				InputStreamReader inputStreamReader = new InputStreamReader(
						impStream, "utf-8");
				BufferedReader bufferedReader = new BufferedReader(
						inputStreamReader);
				String str = null;
				while ((str = bufferedReader.readLine()) != null) {
//					Log.i("e", "error");
					stringBuffer.append(str);
				}
				bufferedReader.close();
				inputStreamReader.close();
				impStream.close();
				httpURLConnection.disconnect();
			} else {
				Log.e("连接失败", "连接失败");
			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return stringBuffer.toString();
	}

	private List<Map<String, Object>> parsexml(String xml, String city) {
		// TODO Auto-generated method stub
		Log.i("xml", xml);
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		StringBuffer stringBuffer = new StringBuffer();
		org.dom4j.Document document = null;
		List listdate = null; // 用来存放日期
		List listday = null; // 用来存放白天图片路径信息
		List listweather = null;
		List listwind = null;
		List listtem = null;
//		Log.i("e", "error");
		try {
			document = DocumentHelper.parseText(xml);
			Element root = document.getRootElement();
			String status = root.elementText("status");
			if (!status.equals("success")) {
				Log.i("e", "error");
				return null;
			} else {
				Iterator iterator = root.elementIterator("results");

				String date = root.elementText("data");
				while (iterator.hasNext()) {
					Element iElement = (Element) iterator.next();
					Iterator iterator2 = iElement
							.elementIterator("weather_data");

					while (iterator2.hasNext()) {
						Element elements = (Element) iterator2.next();
						listdate = elements.elements("date");
						// 将date集合放到listdate中
						listday = elements.elements("dayPictureUrl");
						listweather = elements.elements("weather");
						listwind = elements.elements("wind");
						listtem = elements.elements("temperature");

					}
					for (int i = 0; i < listdate.size(); i++) {
						Element eledate = (Element) listdate.get(i); // 依次取出date
						Element eleday = (Element) listday.get(i);// ..
						Element eleweather = (Element) listweather.get(i);
						Element elewind = (Element) listwind.get(i);
						Element eletem = (Element) listtem.get(i);
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("date", eledate.getText());
						map.put("day", eleday.getText());
						map.put("weather", eleweather.getText());
						map.put("wind", elewind.getText());
						map.put("tem", eletem.getText());
						list.add(map);
					}
				}

			}
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
