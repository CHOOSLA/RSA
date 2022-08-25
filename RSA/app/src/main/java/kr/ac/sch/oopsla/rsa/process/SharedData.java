package kr.ac.sch.oopsla.rsa.process;

import android.app.Activity;
import android.content.SharedPreferences;

public class SharedData extends Activity {
	private String callDrPhone_state;
	private String callDrPhone_area;
	private String callDrPhone_first;
	private String callDrPhone_last;
	private String callDrFirstName;
	private String callDrMiddleName;
	private String callDrEmail_id;
	private String callDrEmail_host_name;
	private String callDrLastname;
	
	private String callPersonPhone_state;
	private String callPersonPhone_area;
	private String callPersonPhone_first;
	private String callPersonPhone_last;
	private String callPersonFirstName;
	private String callPersonMiddleName;
	private String callPersonEmail_id;
	private String callPersonEmail_host_name;
	private String callPersonLastname;
	private String callPersonBirthday;
	private int callPersonAge;
	private String callPerGender;
	private String callPerPosition;
	private String callPerExercise;
	
	private int callPersonPosition;
	private int callPersonGender;
	private int callPersonExercise;
	private SharedPreferences pref;
	private SharedPreferences.Editor editor;
	private int callTime;
	private int callPosition;

	private int call_MiniHR;
	private int call_MaxHR;
	private int call_MiniSym;
	private int call_MaxSym;
	private int call_MiniPara;
	private int call_MaxPara;
	private int sendRadio;

	private String PPG_SIGNAL_PATH;

	public void setPre(SharedPreferences pr, SharedPreferences.Editor ed) {
		pref = pr;
		editor = ed;
		init();
	}

	public void setPreTime(SharedPreferences pr, SharedPreferences.Editor ed) {
		pref = pr;
		editor = ed;
		callTime = pref.getInt("time", 0);
		callPosition = pref.getInt("position", 0);
	}

	public void init() {
		callDrPhone_state = pref.getString("DrPhone_state", "");
		callDrPhone_area = pref.getString("DrPhone_area", "");
		callDrPhone_first = pref.getString("DrPhone_first", "");
		callDrPhone_last = pref.getString("DrPhone_last", "");
		callDrFirstName = pref.getString("DrFirst", "");
		callDrMiddleName = pref.getString("DrMiddle", "");
		callDrEmail_id = pref.getString("DrEmail_id", "");
		callDrEmail_host_name = pref.getString("DrEmail_host_name", "");
		callDrLastname = pref.getString("DrLast", "");
		
		callPersonFirstName = pref.getString("PFirst", "");
		callPersonMiddleName = pref.getString("PMiddle", "");
		callPersonEmail_id = pref.getString("PEmail_id", "");
		callPersonEmail_host_name = pref.getString("PEmail_host_name", "");
		callPersonBirthday = pref.getString("PBD", "");
		callPersonLastname = pref.getString("PLast", "");
		callPersonPhone_state = pref.getString("PPhone_state", "");
		callPersonPhone_area = pref.getString("PPhone_area", "");
		callPersonPhone_first = pref.getString("PPhone_first", "");
		callPersonPhone_last = pref.getString("PPhone_last", "");
		callPersonAge = pref.getInt("Age", 0);
		callPerGender = pref.getString("Gender", "");
		callPerPosition = pref.getString("Position", "");
		callPerExercise = pref.getString("Exercise", "");
		
		callPersonExercise = pref.getInt("exercise", 0);
		callPersonPosition = pref.getInt("position", 0);
		callPersonGender = pref.getInt("gender", 0);
		callTime = pref.getInt("time", 0);
		callPosition = pref.getInt("position", 0);

		call_MaxHR = pref.getInt("MaxHR", 0);
		call_MaxPara = pref.getInt("MaxPara", 0);
		call_MaxSym = pref.getInt("MaxSym", 0);
		call_MiniHR = pref.getInt("MiniHR", 0);
		call_MiniPara = pref.getInt("MiniPara", 0);
		call_MiniSym = pref.getInt("MiniSym", 0);
		sendRadio = pref.getInt("SendRadio", 0);

	}

	// sendRadio?
	public void setSendRadio(int check) {
		sendRadio = check;
		editor.putInt("SendRadio", check);
		editor.commit();
	}

	// Setter
	public void setDrPhone_state(String phone_state) {
		callDrPhone_state = phone_state;
		editor.putString("DrPhone_state", phone_state);
		editor.commit();
	}

	public void setDrPhone_area(String phone_area) {
		callDrPhone_area = phone_area;
		editor.putString("DrPhone_area", phone_area);
		editor.commit();
	}

	public void setDrPhone_first(String phone_first) {
		callDrPhone_first = phone_first;
		editor.putString("DrPhone_first", phone_first);
		editor.commit();
	}

	public void setDrPhone_last(String phone_last) {
		callDrPhone_last = phone_last;
		editor.putString("DrPhone_last", phone_last);
		editor.commit();
	}

	public void setDrFirstName(String fn) {
		callDrFirstName = fn;
		editor.putString("DrFirst", fn);
		editor.commit();
	}

	public void setDrMiddleName(String mn) {
		callDrMiddleName = mn;
		editor.putString("DrMiddle", mn);
		editor.commit();
	}

	public void setDrEmail_id(String email_id) {
		callDrEmail_id = email_id;
		editor.putString("DrEmail_id", email_id);
		editor.commit();
	}

	public void setDrEmail_host_name(String email_host_name) {
		callDrEmail_host_name = email_host_name;
		editor.putString("DrEmail_host_name", email_host_name);
		editor.commit();
	}

	public void setDrLastname(String ln) {
		callDrLastname = ln;
		editor.putString("DrLast", ln);
		editor.commit();
	}

	/////////////////////////////////////////// 샤용자 set
	public void setPersonFirstName(String fn) {
		callPersonFirstName = fn;
		editor.putString("PFirst", fn);
		editor.commit();
	}

	public void setPersonMiddleName(String mn) {
		callPersonMiddleName = mn;
		editor.putString("PMiddle", mn);
		editor.commit();
	}

	public void setPersonLastName(String ln) {
		callPersonLastname = ln;
		editor.putString("PLast", ln);
		editor.commit();
	}

	public void setPersonEmail_id(String email_id) {
		callPersonEmail_id = email_id;
		editor.putString("PEmail_id", email_id);
		editor.commit();
	}

	public void setPersonEmail_host_name(String email_host_name) {
		callPersonEmail_host_name = email_host_name;
		editor.putString("PEmail_host_name", email_host_name);
		editor.commit();
	}

	public void setPersonBirthday(String bd) {
		callPersonBirthday = bd;
		editor.putString("PBD", bd);
		editor.commit();
	}

	public void setPersonGender(int g) {
		callPersonGender = g;
		editor.putInt("gender", g);
		editor.commit();
	}
	
	public void setPersonPosition(int g) {
		callPersonPosition = g;
		editor.putInt("position", g);
		editor.commit();
	}
	
	public void setPersonExercise(int g) {
		callPersonExercise = g;
		editor.putInt("exercise", g);
		editor.commit();
	}

	public void setTime(int g) {
		callTime = g;
		editor.putInt("time", g);
		editor.commit();
	}

	public void setPosition(int g) {
		callPosition = g;
		editor.putInt("position", g);
		editor.commit();
	}

	public void set_PPG_Path(String path) {
		PPG_SIGNAL_PATH = path;
		editor.putString("ppg_path", path);
		editor.commit();
	}

	public void setPersonPhone_state(String p_state) {
		callPersonPhone_state = p_state;
		editor.putString("PPhone_state", p_state);
		editor.commit();
	}

	public void setPersonPhone_area(String p_area) {
		callPersonPhone_area = p_area;
		editor.putString("PPhone_area", p_area);
		editor.commit();
	}

	public void setPersonPhone_first(String p_first) {
		callPersonPhone_first = p_first;
		editor.putString("PPhone_first", p_first);
		editor.commit();
	}

	public void setPersonPhone_last(String p_last) {
		callPersonPhone_last = p_last;
		editor.putString("PPhone_last", p_last);
		editor.commit();
	}

	public void set_MaxHR(int g) {
		call_MaxHR = g;
		editor.putInt("MaxHR", g);
		editor.commit();

	}

	public void set_MaxSym(int g) {
		call_MaxSym = g;
		editor.putInt("MaxSym", g);
		editor.commit();
	}

	public void set_MaxPara(int g) {
		call_MaxPara = g;
		editor.putInt("MaxPara", g);
		editor.commit();
	}

	public void set_MiniHR(int g) {
		call_MiniHR = g;
		editor.putInt("MiniHR", g);
		editor.commit();
	}

	public void set_MiniSym(int g) {
		call_MiniSym = g;
		editor.putInt("MiniSym", g);
		editor.commit();
	}

	public void set_MiniPara(int g) {
		call_MiniPara = g;
		editor.putInt("MiniPara", g);
		editor.commit();
	}
	
	public void set_PsAge(int g){
		callPersonAge = g;
		editor.putInt("Age",g);
		editor.commit();
	}
	
	public void set_Gender(String g){
		callPerGender = g;
		editor.putString("Gender",g);
		editor.commit();
	}
	
	public void set_Position(String g){
		callPerPosition = g;
		editor.putString("Position",g);
		editor.commit();
	}
	
	public void set_Exercise(String g){
		callPerExercise = g;
		editor.putString("Exercise",g);
		editor.commit();
	}
	
	
	public String get_Exercise(){
		return callPerExercise; 
	}
	
	public String get_Position(){
		return callPerPosition; 
	}
	
	public String get_Gender(){
		return callPerGender;
	}
	
	public int get_PsAge(){
		return callPersonAge;
	}
	

	// Getter
	public int getSendRadio(){
		return sendRadio;
	}
	
	public String getDrPh_state() {
		return callDrPhone_state;
	}

	public String getDrPh_area() {
		return callDrPhone_area;
	}

	public String getDrPh_first() {
		return callDrPhone_first;
	}

	public String getDrPh_last() {
		return callDrPhone_last;
	}

	public String getDrFn() {
		return callDrFirstName;
	}

	public String getDrMn() {
		return callDrMiddleName;
	}

	public String getDrEm_id() {
		return callDrEmail_id;
	}

	public String getDrEm_host_name() {
		return callDrEmail_host_name;
	}

	public String getDrLn() {
		return callDrLastname;
	}

	public String getPerEm_id() {
		return callPersonEmail_id;
	}

	public String getPerEm_host_name() {
		return callPersonEmail_host_name;
	}

	public String getPerBd() {
		return callPersonBirthday;
	}

	public String getPerFn() {
		return callPersonFirstName;
	}

	public String getPerMn() {
		return callPersonMiddleName;
	}

	public String getPerLn() {
		return callPersonLastname;
	}

	public String getPerPhone_state() {
		return callPersonPhone_state;
	}

	public String getPerPhone_area() {
		return callPersonPhone_area;
	}

	public String getPerPhone_first() {
		return callPersonPhone_first;
	}

	public String getPerPhone_last() {
		return callPersonPhone_last;
	}

	public int getPerGender() {
		return callPersonGender;
	}
	
	public int getPersonPosition() {
		return callPersonPosition;
	}
	
	public int getPersonExercise() {
		return callPersonExercise;
	}

	public int getTime() {
		return callTime;
	}

	public int getPosition() {
		return callPosition;
	}

	public int getTotalTime() {
		return pref.getInt("time", 0);
	}

	public int get_MaxHR() {
		return call_MaxHR;
	}

	public int get_MaxSym() {
		return call_MaxSym;
	}

	public int get_MaxPara() {
		return call_MaxPara;
	}

	public int get_MiniHR() {
		return call_MiniHR;
	}

	public int get_MiniSym() {
		return call_MiniSym;
	}

	public int get_MiniPara() {
		return call_MiniPara;
	}
	// Process

}