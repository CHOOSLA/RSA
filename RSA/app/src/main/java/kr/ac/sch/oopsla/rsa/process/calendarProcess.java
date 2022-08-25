package kr.ac.sch.oopsla.rsa.process;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class calendarProcess {

	public static String longToString(long day) {
		String dayChange;
		SimpleDateFormat fo = new SimpleDateFormat("yyyy-MM-dd");
		dayChange = fo.format(day);
		
		return dayChange; 
	}
	
	public static long stringToLong(String day) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date currentDay = dateFormat.parse(day, new ParsePosition(0));
		long currentLong = currentDay.getTime();
		
		return currentLong;
		
	}
	
	public static String subDay(String day,int subday, int flag) {
		
		
		Calendar cal = Calendar.getInstance();
	    cal.setTime(new Date());
	    
	    
	    if(flag == 0) {
	    	cal.add(Calendar.DATE, -subday);
	    }
	    else if(flag == 1 ) {
	    	cal.add(Calendar.DATE, -subday*7);
	    }
	    else if(flag == 2){
	    	cal.add(Calendar.MONTH, -subday);
	    }
	    
	    String s = calendarProcess.longToString(cal.getTimeInMillis());	  
		
		return s;		
	}
	
	public static String currentDate() {
		String s;
		
		long time = System.currentTimeMillis(); 
		SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-MM-dd"); // UTC 기준 ( 한국 시차 -9 시간)
		String str = dayTime.format(new Date(time));
				
		return str;
	}
	
	public static String currentTime() {
		String s;
		
		long time = System.currentTimeMillis(); 
		SimpleDateFormat dayTime = new SimpleDateFormat("hh:mm:ss:SSS"); // UTC 기준 ( 한국 시차 -9 시간)
		String str = dayTime.format(new Date(time));		
		
		return str;
	}
}
