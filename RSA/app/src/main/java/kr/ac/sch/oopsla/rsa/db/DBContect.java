/* )
 * 
 * 
 * 
 */


package kr.ac.sch.oopsla.rsa.db;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


@SuppressLint("SdCardPath") public class DBContect extends SQLiteOpenHelper{

	private static String name = "data.db";
	private static int version = 8;
	SQLiteDatabase db;
	SQLiteOpenHelper sql = null;
	
	public DBContect(Context context) { 
		super(context, "/mnt/sdcard/" + name, null, version);
		db = this.getWritableDatabase();
		
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) { 
		// TODO Auto-generated method stub
		// table DATA : ANS
		// table DATA2 : RSA
//		
//		db.execSQL("DROP TABLE IF EXISTS DATA");
//		db.execSQL("DROP TABLE IF EXISTS DATA2");
//		
		db.execSQL("CREATE TABLE DATA "
				+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "date TEXT,"
				+ "time TEXT, "
				+ "sym REAL, para REAL, "
				+ "heartrate INTEGER, "
				+ "sym_state TEXT, "
				+ "psymp_state TEXT, "
				+ "birth TEXT, "
				+ "name TEXT, "
				+ "P_position TEXT, "
				+ "gender TEXT, "
				+ "exercise TEXT ,"
				+ "age INTEGER);");
		
		db.execSQL("CREATE TABLE DATA2 "
				+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ "date TEXT, "
				+ "time TEXT, "
				+ "hrarray TEXT, "
				+ "uppeak TEXT,"
				+ "downpeak TEXT,"
				+ "rsa REAL,"
				+ "heartrate INTEGER, "
				+ "birth TEXT, "
				+ "name TEXT, "
				+ "gender TEXT, "
				+ "age INTEGER, "
				+ "right INTEGER, "
				+ "left INTEGER);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS DATA");
		db.execSQL("DROP TABLE IF EXISTS DATA2");

		onCreate(db);
	}	

}
