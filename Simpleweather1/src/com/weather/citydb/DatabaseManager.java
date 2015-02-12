package com.weather.citydb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import com.example.simpleweather1.R;

import android.R.integer;
import android.R.string;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem.OnMenuItemClickListener;

public class DatabaseManager {

	private Context context;
	private SQLiteDatabase sLiteDatabase;
	private final int BUFFER_SIZE = 400000;
	public static final String DB_NAME = "city.db"; // 保存的数据库文件名
	public static final String PACKAGE_NAME = "com.example.simpleweather1";
	public static final String DB_PATH = "/data"
			+ Environment.getDataDirectory().getAbsolutePath() + "/"
			+ PACKAGE_NAME; // 在手机里存放数据库的位置(/data/data/com.cssystem.activity/cssystem.db)

	public DatabaseManager(Context context) {
		this.context = context;
	}
	
	public SQLiteDatabase getDatabase (){
		return sLiteDatabase;
	}
	

	/**
	 * 打开数据库
	 * @throws FileNotFoundException 
	 */
	public void open() throws FileNotFoundException {

		String dbname = DB_PATH + "/" + DB_NAME;
		if (!(new File(dbname).exists())) {
			InputStream inputStream = this.context.getResources().openRawResource(R.raw.city);
			 //欲导入的数据库
			FileOutputStream outputStream = new FileOutputStream(dbname);
			byte[] data = new byte[BUFFER_SIZE];
			int count = 0;
			try {
				while((count = inputStream.read(data)) != -1){
					outputStream.write(data, 0, count);
				}
				inputStream.close();
				outputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
		sLiteDatabase = SQLiteDatabase.openOrCreateDatabase(dbname,null);
	}
	
	public void closedatabase() {
		this.sLiteDatabase.close();
	}

}