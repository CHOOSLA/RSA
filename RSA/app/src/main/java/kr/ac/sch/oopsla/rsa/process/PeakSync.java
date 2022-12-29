package kr.ac.sch.oopsla.rsa.process;

import android.os.AsyncTask;

public class PeakSync extends AsyncTask<DataSet, Void, Integer>{ // (rgb, time) 배열로부터 HR 산출

	@Override
	protected Integer doInBackground(DataSet... signals) {
		return (int) signals[0].complexHeartRate(4); //HR 반환
	}
}
