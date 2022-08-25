package kr.ac.sch.oopsla.rsa.process;

import android.os.AsyncTask;

public class PeakSync extends AsyncTask<DataSet, Void, Integer>{

	@Override
	protected Integer doInBackground(DataSet... signals) {
		return (int) signals[0].complexHeartRate(4);
	}
}
