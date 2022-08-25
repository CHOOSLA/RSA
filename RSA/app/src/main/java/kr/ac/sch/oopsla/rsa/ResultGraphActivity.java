package kr.ac.sch.oopsla.rsa;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import kr.ac.sch.oopsla.rsa.db.DBtype;
import kr.ac.sch.oopsla.rsa.process.CustomGraphView2;
import kr.ac.sch.oopsla.rsa.process.SharedData;

public class ResultGraphActivity extends Activity implements OnClickListener {

	private TextView day_titleTxt;
	private TextView timeTxt;
	private TextView symp_evaluation;
	private TextView para_evaluation;
	
	private TextView rsaText;

	private ImageView rsaImage, eiImage;

	private Button btnSave;
	private Button btnBack;
	
	private CustomGraphView2 heartGraph;

	private ArrayList<String> mArrayList = new ArrayList<String>();
	
	private TextView tv1; // avg hr
	private TextView tv2; // rsa
	private TextView tv4; // name

	private Button btnLeft;
	private Button btnRight;

	private SharedData sharedData = new SharedData();
	private SharedPreferences sd;
	private SharedPreferences.Editor ed;

	private DBtype db = null;
	Cursor cursor = null;
	int count = 0;
	int max = 0;
	double rsastats = -1, eistats = -1;

	private ArrayList<String> mArrayDateList = new ArrayList<String>();
	
	private ArrayList<String> HrArrays = new ArrayList<String>();
	private ArrayList<String> UpPeaks = new ArrayList<String>();
	private ArrayList<String> DwPeaks = new ArrayList<String>();
	private ArrayList<Double> RSAs = new ArrayList<Double>();

	private ArrayList<Integer> AvgHRs = new ArrayList<Integer>();
	private ArrayList<String> mnameArrayList = new ArrayList<String>();
	private ArrayList<Integer> Ages = new ArrayList<Integer>();
	private ArrayList<Integer> Rights = new ArrayList<Integer>();
	private ArrayList<Integer> Lefts = new ArrayList<Integer>();

	public void sharedLoad() {
		sd = getSharedPreferences("pref", MODE_PRIVATE);
		ed = sd.edit();

		sharedData.setPre(sd, ed);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.l53);

		sharedLoad();
		
		Intent intent = getIntent();
		mArrayList = intent.getStringArrayListExtra("Date");


		String first_day = mArrayList.get(0).substring(0, 10);

		String[] arrayDay = first_day.split("-");
		first_day = arrayDay[1] + "-" + arrayDay[2] + "-" + arrayDay[0];

		day_titleTxt = (TextView) findViewById(R.id.l41_DayInterval);
		day_titleTxt.setText(first_day);

		timeTxt = (TextView) findViewById(R.id.l41time);
		
		btnSave = (Button) findViewById(R.id.l41save1);
		btnSave.setOnClickListener(this);

		btnBack = (Button) findViewById(R.id.l41back1);
		btnBack.setOnClickListener(this);

		/*
		btnLeft = (Button) findViewById(R.id.l43mleft);
		btnLeft.setOnClickListener(this);

		btnRight = (Button) findViewById(R.id.l43mright);
		btnRight.setOnClickListener(this);

		 */

		
		tv1 = (TextView) findViewById(R.id.m3heartnum); // hr
		tv2 = (TextView) findViewById(R.id.m3RSA); // rsa
		tv4 = (TextView) findViewById(R.id.l41name); // name
		
		heartGraph = (CustomGraphView2) findViewById(R.id.bview);
		heartGraph.DetectMessage(true);

		rsaText = (TextView) findViewById(R.id.m3evaluationRSA);
		rsaImage = (ImageView)findViewById(R.id.rsaimage);

		initMax();
		SeeText(count);
	}

	private void initMax() {
		int avghr, age, right, left;
		String time1, time2, name;
		String hrarr, uppeak, dwpeak;
		double rsa;
		
		db = new DBtype(ResultGraphActivity.this);
		
		for (int i = 0; i < mArrayList.size(); i++) {
			String date = mArrayList.get(i).substring(0, 10);
			String time = mArrayList.get(i).substring(11);
			String sql = "select * from DATA2 where date='" + date + "' and time='" + time + "';";
			cursor = db.selectRawQuery(sql);
			max = max + cursor.getCount();
			cursor.moveToFirst();

			while (!cursor.isAfterLast()) {

				time1 = cursor.getString(1);
				time2 = split_Time_not_milliSecond(cursor.getString(2));
				hrarr = cursor.getString(3);
				uppeak = cursor.getString(4);
				dwpeak = cursor.getString(5);
				rsa = cursor.getDouble(6);
				avghr = cursor.getInt(7);
				name = cursor.getString(9);
				age = cursor.getInt(11);
				right = cursor.getInt(12);
				left = cursor.getInt(13);
				
				mArrayDateList.add(time1 + " " + time2);
				HrArrays.add(hrarr);
				UpPeaks.add(uppeak);
				DwPeaks.add(dwpeak);
				RSAs.add(rsa);
				AvgHRs.add(avghr);
				mnameArrayList.add(name);
				Ages.add(age);
				Rights.add(right);
				Lefts.add(left);

				cursor.moveToNext();
			}
		}
	}

	public String split_Time_not_milliSecond(String str) {
		String[] array;
		array = str.split(":");

		return array[0] + ":" + array[1];
	}

	public void SeeText(int i) {
		double[] HR_fin, up, dw;
		int leftStart, rightStart;
		String[] tmpstr;
		
		tmpstr = HrArrays.get(i).split(",");
		HR_fin = new double[tmpstr.length];
		for(int j=0;j<tmpstr.length;j++){
			HR_fin[j] = Double.parseDouble(tmpstr[j]);
		}
		
		tmpstr = UpPeaks.get(i).split(",");
		up = new double[tmpstr.length];
		for(int j=0;j<tmpstr.length;j++){
			up[j] = Double.parseDouble(tmpstr[j]);
		}

		tmpstr = DwPeaks.get(i).split(",");
		dw = new double[tmpstr.length];
		for(int j=0;j<tmpstr.length;j++){
			dw[j] = Double.parseDouble(tmpstr[j]);
		}

		heartGraph.addArr(HR_fin, up, dw, Rights.get(i), Lefts.get(i)); // graph reversal
		
		tv1.setText(AvgHRs.get(i) + "");
		tv2.setText(Math.round(RSAs.get(i)*100)/100.0 + "");
		tv4.setText(mnameArrayList.get(i));

		rsastats = evalRsa(RSAs.get(i), Ages.get(i));
		
		String textTmp ="";
		if(rsastats == -1){
			textTmp += "Not measurable\r\n";
			rsaImage.setImageResource(R.drawable.muted);
		}
		else if(rsastats == 0){
			textTmp += "Abnormal\r\n";
			rsaImage.setImageResource(R.drawable.abnormall);
		}
		else if(rsastats == 1){
			textTmp += "Normal\r\n";
			rsaImage.setImageResource(R.drawable.normall);
		}
		
		rsaText.setText(textTmp);

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.l41save1) {
			finish();
		} 
		else if (id == R.id.l41back1) {
			finish();
		}
		/*
		else if (id == R.id.l43mleft) {
			if (count < 1) {
				count = 0;
				SeeText(count);
			} else {
				count--;
				SeeText(count);

			}
		} 
		else if (id == R.id.l43mright) {

			if (count < max - 1) {
				count++;
				SeeText(count);
			} else {
				SeeText(max - 1);
			}

		}

		 */
	}
	
	public double evalRsa(double rsa, double age){
		double stats = 0; // -1 = 측정 불가 나이, 1 = 정상, 0 = 비정상
		if(age <= 9){
			stats = -1;
		}
		else if(age <=29 && age >= 10){
			if(rsa>=14){
				stats = 1;
			}
			else{
				stats = 0;
			}
		}
		else if(age <=39 && age >= 30){
			if(rsa>=12){
				stats = 1;
			}
			else{
				stats = 0;
			}
		}
		else if(age <=49 && age >= 40){
			if(rsa>=10){
				stats = 1;
			}
			else{
				stats = 0;
			}
		}
		else if(age <=59 && age >= 50){
			if(rsa>=9){
				stats = 1;
			}
			else{
				stats = 0;
			}
		}
		else if(age <= 69&& age >= 60){
			if(rsa>=7){
				stats = 1;
			}
			else{
				stats = 0;
			}
		}
		else{
			stats = -1;
		}
		return stats;
	}
}