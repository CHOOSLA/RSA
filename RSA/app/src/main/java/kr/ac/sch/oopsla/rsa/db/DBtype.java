/* 실질적인 DB 부분 데이터 삽입 및 삭제를 담당함 
 * 
 * 
 * 
 */

package kr.ac.sch.oopsla.rsa.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import java.util.Random;

import kr.ac.sch.oopsla.rsa.process.calendarProcess;

public class DBtype {

	
	//
	private DBContect sql;
	private SQLiteDatabase db;
	
	public DBtype(Context context) {
		sql = new DBContect(context);
	}
	
	public void inserResRSA(String hrarray, String uppeak, String downpeak, double rsa, int heart, String birth, String name, String gender, int age, int right, int left){
		db = sql.getWritableDatabase();
		ContentValues val = new ContentValues();

		val.put("date", calendarProcess.currentDate());
		val.put("time", calendarProcess.currentTime());
		val.put("hrarray", hrarray);
		val.put("uppeak", uppeak);
		val.put("downpeak", downpeak);
		val.put("rsa", rsa);
		val.put("heartrate", heart);
		val.put("birth", birth);
		val.put("name", name);
		val.put("gender", gender);
		val.put("age",age);
		val.put("right", right);
		val.put("left", left);

		db.insert("DATA2", null, val);		
	}
	
	public void insertRes(double sym, double para, int heart, String sta2, String sta3, String birth, String name, String position, String gender,String exerices, int age) // DB 삽입문
	{
		
		db = sql.getWritableDatabase();
		ContentValues val = new ContentValues();
		

		val.put("sym", sym);
		val.put("para", para);
		val.put("heartrate", heart);
		val.put("sym_state", sta2);
		val.put("psymp_state", sta3);
		val.put("birth", birth);
		val.put("name", name);
		val.put("P_position",position);
		val.put("gender", gender);
		val.put("exercise",exerices);		
		val.put("age",age);

		db.insert("DATA", null, val);		
	}
	
	public void insertSym(double sym) {
		insertRes(sym,0,0,null,null,null,null,null,null,null,0);
	}
	
	public void insertPara(double para) {
		insertRes(0,para,0,null,null,null,null,null,null,null,0);
	}
	
	public void insertHeart(int heart) {
		insertRes(0,0,heart,null,null,null,null,null,null,null,0);
	}
	
	public void insertsta2(String sta2){
		insertRes(0, 0, 0, sta2, null,null,null,null,null,null,0);
	}
	
	public void insertsta3(String sta3){
		insertRes(0, 0, 0, null, sta3,null,null,null,null,null,0);
	}
	
	public void insertsta4(String sta4){
		insertRes(0, 0, 0, null, null,sta4,null,null,null,null,0);
	}
	
	public void insertname(String name){
		insertRes(0, 0, 0, null, null, null,name,null,null,null,0);
	}
	
	public void insertposition(String posi){
		insertRes(0, 0, 0, null, null, null, null,posi,null,null,0);
	}	
	
	public void insertgender(String gender){
		insertRes(0, 0, 0, null, null,null,null,null,gender,null,0);
	}
	
	public void insertexercise(String exer){
		insertRes(0, 0, 0, null, null,null,null,null,null,exer,0);
	}
	
	public void insertage(int age){
		insertRes(0, 0, 0, null, null,null,null,null,null, null ,age);
	}

	
	public void insertTemp() {
		db = sql.getWritableDatabase();
		ContentValues val = new ContentValues();
		Random r = new Random();
		
		double sym;
		double para;
		int heart;
		sym = 8 + Math.random();
		para = 14 + Math.random();
		heart = r.nextInt(30) + 60;
		

		val.put("sym", sym);
		val.put("para", para);
		val.put("heartrate", heart);

		db.insert("DATA", null, val);
		Log.e("임시 데이터 들어감","check");
		
		//
	}

	public Cursor selectRawQuery(String query)
	{
		
		db = sql.getReadableDatabase();

		
		Cursor cursor = db.rawQuery(query, null);
		
		
		return cursor;
	}
}

