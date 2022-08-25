package kr.ac.sch.oopsla.rsa.process;

import android.os.Environment;
import android.util.Log;

import kr.ac.sch.oopsla.rsa.algorithms.Peak_Detector;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class DataSet {

	private static final String TAG = "DATA_SET";
	private ArrayList<DataPoint> set = new ArrayList<DataPoint>();
	private ArrayList<Double> RR = new ArrayList<Double>();
	private ArrayList<Double> HR = new ArrayList<Double>();
	private ArrayList<Double> SpO2 = new ArrayList<Double>();
	
	// 테스트용 임시 어레이리스트
	private static ArrayList<Double> kurtosis = new ArrayList<Double>();
	private static ArrayList<Double> TurningPointRatio = new ArrayList<Double>();
	
	private double setSum = 0;
	private double setSize = 0;
	private double hrSum = 0;
	private double hrSize = 0;
	
	private static int sigCheckFlag = 0; //사이즈 : 1, 첨도 : 2, 변동정도 : 3, hr 비교 : 4
	private int sigCheckNum = 0;
	
	public DataSet() {
		// creates an empty data set.
	}

	public DataSet(ArrayList<Double> newdata) {
		// creates a dataset with the input arrayset of doubles
		for (int i = 0; i < newdata.size(); i++) {
			set.add(new DataPoint(newdata.get(i)));
		}
	}

	public int getSetSize(){
		return set.size();
	}
	
	public double getKur(int index){
		return kurtosis.get(index);
	}
	
	public int getKurSize(){
		return kurtosis.size();
	}
	
	public double getTPR(int index){
		return TurningPointRatio.get(index);
	}
	
	public int getTPRSize(){
		return TurningPointRatio.size();
	}

	public int getsigCheckNum(){
		return sigCheckNum;
	}
	
	public void setsigCheckFlag(int val){
		sigCheckFlag = val;
	}
	
	public int getsigCheckFlag(){
		return sigCheckFlag;
	}
	
	public void clearDataSet() {
		set = new ArrayList<DataPoint>();
		RR = new ArrayList<Double>();
		HR = new ArrayList<Double>();
		SpO2 = new ArrayList<Double>();
	}

	public void addValue(double invalue) {
		set.add(new DataPoint(invalue));
	}

	public void addValue(int invalue) {
		set.add(new DataPoint((double) invalue));
	}

	public void addValueIndex(int index, double invalue) {
		set.add(index, new DataPoint(invalue));
	}

	public void addPointTime(double invalue, double intime) {
		set.add(new DataPoint(invalue, intime));
	}

	public void addSetSum(double invalue){
		setSum += invalue;
		setSize++;
	}
	
	public void addhrSum(double invalue){
		hrSum += invalue;
		hrSize++;
	}
	
	public double getSetAvg(){
		if(setSize != 0){
			return setSum / setSize;
		}
		else{
			return 0;
		}
	}
	
	public double gethrAvg(){
		if(hrSize != 0){
			return hrSum / hrSize;
		}
		else{
			return 0;
		}
	}

	public void removeSetSum(double invalue){
		setSum -= invalue;
		setSize--;
	}
	
	public void removehrSum(double invalue){
		hrSum -= invalue;
		hrSize--;
	}

	public void addPointTimeIndex(int index, double invalue, double intime) {
		set.add(index, new DataPoint(invalue, intime));
	}

	public void addHRPoint(double nHR) {
		HR.add(nHR);
	}

	public void addSpO2(double inSpO2) {
		SpO2.add(inSpO2);
	}

	public void addHRandSpO2(double nHR, double inSpO2) {
		HR.add(nHR);
		SpO2.add(inSpO2);
	}

	public void remove(int index) {
		set.remove(index);
	}

	public void removeHr(int index){
		if(index>=0){
			HR.remove(index);
		}
	}
	
	// %%%%%%%%%%%%%%%%%%%% Separation for GETTING Values %%%%%%%%%%%%%%%%%%%
	public int[] getRpeak(){
	      int frames = (int) this.getFrameRate();
	      if(frames < 15)
	      {
	         frames = 15;	// 최소 프레임 15?
	      }
	      
	      Log.e("DataSet", String.valueOf(frames));

	      double[] data_set = getValueArray();
	      double[] time_set = getTimeArray();
//	         
//	      if(time_set.length < 1)
//	      {
//	         return null;
//	      }
//	      
//	      double startTime = time_set[0];
//	      
//	      //Interpolation of values here to 30Hz here.
//	      Spline s11 = new Spline(time_set, data_set);
//	      int newLength = (int) ((time_set[time_set.length -1] - time_set[0])*30/1000);
//	      double[] interpolated_data = new double[newLength];
//	      
//	      double time_stamp = startTime;
//	      for(int i = 0; i < newLength; i++)
//	      {
//	         interpolated_data[i] = s11.spline_value(time_stamp);
//	         //time_stamp represents value in milliseconds
//	         time_stamp = time_stamp + 1000D/30D;
//	      }
//	      
//	      System.out.println("Length of the Data Set: " + data_set.length);
//	      
	      //Due to interpolation to 30 hz frames is changed to 30 hz.
	      int[] peaks = Peak_Detector.Peak_Detection(data_set, frames); 
	      
	      return peaks;      
	   }

	public double getValue(int index) {
		return set.get(index).getValue();
	}

	public double getTime(int index) {
		return set.get(index).getTime();
	}

	public double getHR(int index) {
		if(index <0){
			return 0;
		}
		else{
			return HR.get(index);
		}
	}
	
	public double getHRavg() {
		double avgg=0;
		for(int i=0;i<HR.size()-1;i++){
			avgg += HR.get(i);
		}
		if(HR.size()-1 != 0){
			return avgg / (HR.size()-1);
		}
		else{
			return 0;
		}
	}
	
	public double[] getHR(){
		double[] HRarr = new double[HR.size()];
		
		for(int i=0;i<HR.size();i++){
			HRarr[i] = HR.get(i);
		}

		return HRarr;
	}

	public double[] getHRArray(int intervalCal, int scopeCal){
		//int intervalCal;  .n 초에 한번
		//int scopeCal; 	n 초간의 범위

		List<Double> HrArr = new ArrayList<Double>();
		int start=0;
		double timeTmp; 
		double beforeTime; 
		double hr;

		DataSet dbSet = new DataSet();
		for(int i=0;i<set.size();i++){
			dbSet.addPointTime(set.get(i).getValue(), set.get(i).getTime());
		}

		timeTmp = dbSet.getTime(0);
		beforeTime = dbSet.getTime(0);
		while(start < dbSet.getSetSize()){
			// 150초부터?
			if(dbSet.getSetSize() - start < scopeCal * 30){  
				break;
			}
			timeTmp = dbSet.getTime(start);
			// 0.5초 간격으로
			if(timeTmp - beforeTime > intervalCal * 100){
				beforeTime = timeTmp;
				hr = dbSet.complexHeartRate(scopeCal, start);
				HrArr.add(hr);
			}
			start++;
		}
		
		double[] result = new double[HrArr.size()];
		for(int i =0;i<HrArr.size();i++){
			result[i] = HrArr.get(i);
		}
		
		return result;
	}
	
	public double getMax() {
		double max = 0;
		double temp = 0;
		for (int i = 0; i < set.size(); i++) {
			temp = set.get(i).getValue();
			if (temp > max)
				max = temp;
		}
		return max;

	}

	public int size() {
		return set.size();
	}

	
	public int hrSize(){
		return HR.size();
	}
	
	public void clear() {
		set.clear();
	}

	public double[] getValueArray() {
		double returnArray[] = new double[set.size()];
		for (int i = 0; i < set.size(); i++) {
			returnArray[i] = set.get(i).getValue();
		}

		return returnArray;
	}

	public double[] getValueArray(int start, int length) {
		double returnArray[] = new double[length];

		// ensures the start is within range else returns entire array
		if (start < set.size()) {
			// ensures that the length doesnt put array out of bounds
			if (length > set.size() - start) {
				length = set.size() - start;
			}

			for (int i = 0; i < length; i++) {
				returnArray[i] = set.get(i + start).getValue();
			}
			return returnArray;
		} else {
			return getValueArray();
		}
	}

	public double[] getTimeArray() {
		double returnArray[] = new double[set.size()];
		for (int i = 0; i < set.size(); i++) {
			returnArray[i] = set.get(i).getTime();
		}

		return returnArray;
	}

	// Finds the average HR as long as HR is above 20
	public double getAvgHR() {
		double returnHR = 0;
		int HRCount = 0;
		for (int i = 0; i < HR.size(); i++) {
			if (HR.get(i) > 20) {
				returnHR = returnHR + HR.get(i);
				HRCount++;
			}
		}
		returnHR = returnHR / HRCount;

		return returnHR;
	}

	public void saveData() {
		Calendar dateIn = Calendar.getInstance();
		SimpleDateFormat dateForm = new SimpleDateFormat("MM|dd|yyyy HH:mm:ss");
		String date = dateForm.format(dateIn.getTime());

		try {
			String eol = System.getProperty("line.separator");
			// File path =
			// Environment.getExternalStoragePublicDirectory("HRMonitor");
			File sd = Environment.getExternalStorageDirectory();
			File path = new File(sd.getAbsolutePath() + "/HRMonitor");
			if (!path.exists()) {
				if (path.mkdir())
					;
			}

			if (path.canWrite()) {
				File datafile = new File(path, date + ".txt");
				FileWriter dataWriter = new FileWriter(datafile);
				BufferedWriter dataOut = new BufferedWriter(dataWriter);

				for (int i = 0; i < set.size(); i++) {
					// dataOut.write(((Float)(ECG.getValue(i)/1000)).toString()
					// + "," + ((Float)ECG.getTime(i)).toString() + eol);
					dataOut.write(((Double) (set.get(i).getValue())).toString() + " ; "
							+ ((Double) set.get(i).getTime()).toString() + eol);
				}
				dataOut.close();
			} else {
				Log.d("Saving Data", "Cannot Write File to Directory");
			}
		} catch (IOException e) {
			Log.e("Saving Data", "Could not write file " + e.getMessage());
		}
	}

	public void saveInformation(String name, Object[] dataToSave) {

		try {
			String eol = System.getProperty("line.separator");
			// File path =
			// Environment.getExternalStoragePublicDirectory("HRMonitor");
			File sd = Environment.getExternalStorageDirectory();
			File path = new File(sd.getAbsolutePath() + "/HRMonitor");
			if (!path.exists()) {
				if (path.mkdir())
					;
			}

			if (path.canWrite()) {
				File datafile = new File(path, name + ".txt");
				FileWriter dataWriter = new FileWriter(datafile);
				BufferedWriter dataOut = new BufferedWriter(dataWriter);

				for (int i = 0; i < dataToSave.length; i++) {
					// dataOut.write(((Float)(ECG.getValue(i)/1000)).toString()
					// + "," + ((Float)ECG.getTime(i)).toString() + eol);
					dataOut.write((dataToSave[i]).toString() + eol);
				}
				dataOut.close();
			} else {
				Log.d("Saving Data", "Cannot Write File to Directory");
			}
		} catch (IOException e) {
			Log.e("Saving Data", "Could not write file " + e.getMessage());
		}
	}

	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Corrupt Data Detection
	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	
	/*
	public boolean isSignalClean(double SetAvg) {
		if (set.size() < 60) {
			sigCheckFlag = 1;
			return false;
		}
		else {
			double[] sig = getValueArray(set.size() - 60, 60);
			//double Avg = 0;
			for(int i=0;i<sig.length;i++){
				if(Math.abs(sig[i] - SetAvg) > 20){
					sigCheckNum = (int)Math.abs(sig[i] - SetAvg);
					sigCheckFlag = 2;
					return false;
				}
				//Avg += sig[i];
			}
			boolean result = isSignalApplicable(sig);
//			Log.e("sig_result = ",String.valueOf(result));
			if(!result){
				sigCheckFlag = 3;
			}
			return result;
		}
	}
	*/
	
	public boolean isSignalClean() {
		if (set.size() < 60) {
			sigCheckFlag = 1;
			return false;
		}
		else {
			double[] sig = getValueArray(set.size() - 60, 60);
			
			boolean result = isSignalApplicable(sig);
//			Log.e("sig_result = ",String.valueOf(result));
			return result;
		}
	}

	/*
	 * 1. 첨도를 구한다. 뽀죡한 정도를 구함 
	 * 2. 신호의 변동정도를 계산해서 이게 꺠끗한지 아닌지를 확인함. 엄청 단순함
	 */
	public static boolean isSignalApplicable(double[] sig) {
		double[] x = highpass_filter(sig, 0.5);
		double xmean = mean(x);
		double[] x0 = new double[x.length];
		double[] x02 = new double[x.length];
		double[] x04 = new double[x.length];

		double s2, m4;

		for (int index = 0; index < x.length; index++) {
			x0[index] = x[index] - xmean;
			x02[index] = Math.pow(x0[index], 2);
			x04[index] = Math.pow(x0[index], 4);
		}

		s2 = mean(x02);
		m4 = mean(x04);

		// Log.e(TAG, "첨도: "+m4/Math.pow(s2, 2)+"");
		double kur = m4 / Math.pow(s2, 2);
		
		// 테스트용 데이터 저장
		//kurtosis.add(kur);
		
		if (kur >= 3.4) {
			System.out.println("Kurtosis");
			sigCheckFlag = 2;
			return false;
		}

		double j = 0;
		
		for (int k = 1; k < 59; k++) {
			// ^ 형태일 경우
			if ((x[k] - x[k - 1]) * (x[k] - x[k + 1]) > 0) {
				j++;
			}
		}
		
		double TPR = j / 58.0;
		
		// 테스트용 데이터 저장
		//TurningPointRatio.add(TPR);
		
		// Turning Point Ratio >= 0.325
		// ^ 형태가 많을 경우
		if (TPR >= 0.4) {
			sigCheckFlag = 3;
			// System.out.println("Turning Point");
			return false;
		}
		
		return true;
	}

	public static double[] highpass_filter(double[] signal, double fc) {
		double[] a = new double[6];
		double[] b = new double[6];
		double[] zi = new double[5];
		double[] y;
		if (fc == 0.5) {
			a[0] = 1;
			a[1] = -4.60699664684803;
			a[2] = 8.50751263449196;
			a[3] = -7.86788770781740;
			a[4] = 3.64220253554851;
			a[5] = -0.674746958146318;

			b[0] = 0.824665274293227;
			b[1] = -4.11207512603613;
			b[2] = 8.21293284109676;
			b[3] = -8.21293284109676;
			b[4] = 4.11207512603613;
			b[5] = -0.824665274293227;

			zi[0] = -0.824665274306237;
			zi[1] = 3.28740985178983;
			zi[2] = -4.92552298941761;
			zi[3] = 3.28740985178150;
			zi[4] = -0.824665274302005;
		} else if (fc == 0.25) {
			a[0] = 1;
			a[1] = -4.80408432849615;
			a[2] = 9.23570696080354;
			a[3] = -8.88100025696535;
			a[4] = 4.27127693759408;
			a[5] = -0.821896436153646;

			b[0] = 0.907459640661287;
			b[1] = -4.53420467473151;
			b[2] = 9.06531814461358;
			b[3] = -9.06531814461359;
			b[4] = 4.53420467473152;
			b[5] = -0.907459640661288;

			zi[0] = -0.907459641778597;
			zi[1] = 3.62674503832057;
			zi[2] = -5.43857311661216;
			zi[3] = 3.62674503792426;
			zi[4] = -0.907459641579602;
		}

		int nfact = 3 * (Math.max(a.length, b.length) - 1);
		// Log.e("nfact",String.valueOf(nfact));

		/*
		 * % Non-causal filtering x=[2*signal(1)-signal(nfact+1:-1:2); signal;
		 * 2*signal(end)-signal(end-1:-1:end-nfact)];
		 */
		double[] temp1 = new double[nfact];
		double[] temp2 = new double[nfact];
		for (int index = 0; index < nfact; index++) {
			temp1[index] = 2 * signal[0] - signal[nfact - index];
			temp2[index] = 2 * signal[signal.length - 1] - signal[signal.length - 2 - index];
		}
		
		double[] x = new double[nfact * 2 + signal.length];
		System.arraycopy(temp1, 0, x, 0, temp1.length);
		System.arraycopy(signal, 0, x, temp1.length, signal.length);
		System.arraycopy(temp2, 0, x, temp1.length + signal.length, temp2.length);

		y = filter_DFIITS(x, b, a, zi);
		
		double[] ytemp = new double[y.length];
		for (int ind = 0; ind < y.length; ind++) {
			ytemp[y.length - 1 - ind] = y[ind];
		}
		y = ytemp;

		y = filter_DFIITS(y, b, a, zi);
		
		double[] sig_filt = new double[y.length - 2 * nfact];
		for (int ind = 0; ind < sig_filt.length; ind++) {
			sig_filt[ind] = y[y.length - nfact - 1 - ind];
		}
		
		return sig_filt;
	}

	public static double[] filter_DFIITS(double[] x, double[] b, double[] a, double[] zi) {
		// % Filtering
		double[] y = new double[x.length];
		double[] z = new double[zi.length + 1];
		double[] xt = new double[b.length - 1];
		double[] yt = new double[a.length - 1];

		for (int index = 0; index < zi.length; index++) {
			z[index] = zi[index] * x[0];
		}
		z[z.length - 1] = 0;

		for (int m = 0; m < x.length; m++) {
			y[m] = b[0] * x[m] + z[0];

			// indexes start at one because in matlab they start at 2
			for (int index = 1; index < b.length; index++) {
				xt[index - 1] = b[index] * x[m];
			}
			for (int index = 1; index < a.length; index++) {
				yt[index - 1] = a[index] * y[m];
			}

			// z=xt+z(2:end)-yt;
			// z=[z; 0];
			for (int index = 0; index < z.length - 1; index++) {
				z[index] = xt[index] + z[index + 1] - yt[index];
			}
			z[z.length - 1] = 0;
		}
		return y;
	}

	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Get RRI
	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	// Function for getting the RRI interval
	public double[] getRRI() {
		int frames = (int) this.getFrameRate();
		if (frames < 15) {
			frames = 15;
		}

		double[] data_set = getValueArray();
/*		double[] time_set = getTimeArray();

		if (time_set.length < 1) {
			return null;
		}

		double startTime = time_set[0];

		// Interpolation of values here to 30Hz here.
		Spline s11 = new Spline(time_set, data_set);
		int newLength = (int) ((time_set[time_set.length - 1] - time_set[0]) * 30 / 1000);
		double[] interpolated_data = new double[newLength];

		double time_stamp = startTime;
		for (int i = 0; i < newLength; i++) {
			interpolated_data[i] = s11.spline_value(time_stamp);
			// time_stamp represents value in milliseconds
			time_stamp = time_stamp + 1000D / 30D;
		}

		System.out.println("Length of the Data Set: " + data_set.length);
*/
		// Due to interpolation to 30 hz frames is changed to 30 hz.
		
		int[] peaks = Peak_Detector.Peak_Detection(data_set, frames);
		
		//int[] peaks = Peak_Detector.Peak_Detection(data_set, 30);
		
		if (peaks.length <= 1) {
			return null;
		}

		/*
		 * Integer[] peaksToSave = new Integer[peaks.length]; Double[]
		 * dataToSave = new Double[interpolated_data.length]; Double[]
		 * originalData = new Double[data_set.length]; for(int i = 0; i <
		 * data_set.length;i++) { originalData[i] = data_set[i]; } for(int i =
		 * 0; i < interpolated_data.length;i++) { dataToSave[i] =
		 * interpolated_data[i]; } for(int i = 0; i < peaks.length;i++) {
		 * //Saved peaks augments to peaks[i] + 1 due to 0 start index in java
		 * vs 1 start index in matlab peaksToSave[i] = peaks[i] + 1; }
		 * 
		 * //Use these lines only for testing as they create extra files on the
		 * phone saveInformation("Original3", originalData);
		 * saveInformation("Splined3", dataToSave); saveInformation("Peaks3",
		 * peaksToSave);
		 */

		System.out.println("Frame Rate: " + frames + "    Size: " + peaks.length);

		// RR represents the RR intervals in terms of time
		double[] RRF = new double[peaks.length - 1];

		for (int RRI = 1; RRI < peaks.length; RRI++) {
			// RRF[RRI - 1] = (set.get(peaks[RRI]).getTime() -
			// set.get(peaks[RRI-1]).getTime())/1000;

			// After interpolation:
			RRF[RRI - 1] = ((double) (peaks[RRI] - peaks[RRI - 1])) / frames;
		}

		return RRF;
	}

	// %%%%%%%%%%%%%%%%%%%%%%%%% AFib
	// Detection%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

	public boolean getAfib() {
		int frames = (int) this.getFrameRate();
		// int frames = 30;
		if (frames <= 15) {
			frames = 15;
		}

		double[] data_set = getValueArray();
		int[] peaks = Peak_Detector.Peak_Detection(data_set, frames);

		if (peaks.length <= 1) {
			return false;
		}

		double[] RR = new double[peaks.length - 1];

		for (int RRI = 1; RRI < peaks.length; RRI++) {
			RR[RRI - 1] = set.get(peaks[RRI]).getTime() - set.get(peaks[RRI - 1]).getTime();
		}

		int[] beats = AFdetect(RR);

		int AFcount = 0;
		if (beats.length > 31) {
			for (int index = 32; index < beats.length; index++) {
				if (beats[index] == 1) {
					AFcount++;
				}
			}
			if (AFcount > (beats.length - 32) / 2)
				return true;
			else
				return false;
		} else {
			return false;
		}
	}

	public static int[] AFdetect(double[] RRI) {
		int Nbeats = 32;
		int m = 1;
		double thr = -1.4;
		double r = 0.02;

		int[] AF = new int[RRI.length - 1];
		Arrays.fill(AF, 0);

		double[] sampEN = new double[RRI.length];
		double[] Cos_sampEN = new double[RRI.length];

		double[] RRI_temp = new double[Nbeats];
		int flag;
		double[] result;
		for (int i = 1; i <= RRI.length - Nbeats; i++) {
			// [sampEn(i+Nbeats-1), r, flag] =
			// SampEnCalculation(RRI(i:i+Nbeats-1), m, r); %use 32 beats (peaks)
			System.arraycopy(RRI, i - 1, RRI_temp, 0, Nbeats);
			result = SampEnCalculation(RRI_temp, m, r);
			sampEN[i + Nbeats - 2] = result[0];
			r = result[1];
			flag = (int) result[2];

			// Coefficient of sample entroy
			Cos_sampEN[i + Nbeats - 2] = sampEN[i + Nbeats - 2] + Math.log(2 * r) - Math.log(mean(RRI_temp));

			if (Cos_sampEN[i + Nbeats - 2] > thr) {
				AF[i + Nbeats - 2] = 1;
			} else {
				AF[i + Nbeats - 2] = 0;
			}

		}

		return AF;
	}

	public static double[] SampEnCalculation(double[] x, int m, double rfact) {
		// result contains 3 values:
		// result[0] = shannon entropy
		// result[1] = the input rfact
		// result[2] = the flag value generated for reference
		double[] result = new double[3];

		double[][] xm;
		double[][] xm1;
		double[] nm;
		double[] nm1;
		double[] comp;
		double[] comp1;

		double[] temp;
		double[] temp1;

		double d = 0;
		double d1 = 0;
		int nLength = x.length;

		xm = new double[m][nLength - m];
		xm1 = new double[m + 1][nLength - m];
		nm = new double[nLength - m];
		nm1 = new double[nLength - m];
		comp = new double[nLength - m];
		comp1 = new double[nLength - m];

		// used for normal caclulation
		temp = new double[m];
		temp1 = new double[m + 1];

		// (pattern of length m and a length m +1)
		for (int i = 0; i < nLength - m; i++) {
			int j = 0;
			for (j = 0; j < m; j++) {
				xm[j][i] = x[i + j];
			}
			for (j = 0; j <= m; j++) {
				xm1[j][i] = x[i + j];
			}
			// The above loops could be combined into one loop, and
			// one extra line, but for the simplicity of looking at
			// the code they are kept separate, as will a few other
			// loops from this point.

		}

		// (Similarity calculation SampEn)
		// nm,Nm1,fi,fi1,comp and comp1 are all instantiated above,
		// with appropriate sizes.
		for (int i = 0; i < nLength - m; i++) {
			Arrays.fill(comp, 0);
			Arrays.fill(comp1, 0);
			for (int j = 0; j < nLength - m; j++) {
				if (j != i) {
					for (int k = 0; k < xm.length; k++) {
						temp[k] = xm[k][i] - xm[k][j];
					}
					d = normCalc(temp);

					for (int k = 0; k < xm1.length; k++) {
						temp1[k] = xm1[k][i] - xm1[k][j];
					}
					d1 = normCalc(temp1);

					if (d <= rfact || Math.abs(d - rfact) < 0.0001) {
						// If d and rfact are close enough that they are
						// reasonably equal then this statement will still work
						comp[j] = 1;
					}
					if (d1 <= rfact || Math.abs(d1 - rfact) < 0.0001) {
						// If d and rfact are close enough that they are
						// reasonably equal then this statement will still work
						comp1[j] = 1;
					}
				}
			}
			int sum = 0;
			int sum1 = 0;

			for (int k = 0; k < comp.length; k++) {
				sum += comp[k];
			}
			nm[i] = sum;

			for (int k = 0; k < comp1.length; k++) {
				sum1 += comp1[k];
			}
			nm1[i] = sum1;

		}

		// Variable to hold the sum of nm
		int sum = 0;
		// Variable to hold the sum of nm1
		int sum1 = 0;

		for (int i = 0; i < nm.length; i++) {
			sum += nm[i];
		}
		for (int i = 0; i < nm1.length; i++) {
			sum1 += nm1[i];
		}

		if (sum == 0) {
			result[0] = (double) 0;
			// result[0].isNaN();
			result[2] = (double) -1;
		} else if (sum1 == 0) {
			result[0] = (double) -Math.log((double) 1 / sum);
			result[2] = (double) 1;
		} else {
			result[0] = (double) -Math.log((double) sum1 / sum);
			result[2] = (double) 0;
		}

		result[1] = rfact;
		// result contains 3 values:
		// result[0] = shannon entropy
		// result[1] = the input rfact
		// result[2] = the flag value generated for reference
		return result;

	}

	public static double normCalc(double[] input) {
		int lSize = input.length;
		double sum = 0;
		for (int i = 0; i < lSize; i++) {
			// Adds the square of each value
			sum += Math.pow(input[i], 2);
		}
		// Takes the squareroot of the sum of squares
		return (double) Math.sqrt(sum);
	}

	// %%%%%%%%%%%%%%%%%%%%%%%%% Peak Detection
	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

	public double complexHeartRate(int sec, int start) {
		// if the set size is less than 30 points there is not enough points to
		// determine anything
		if (set.size() < 30) {
			return 0;
		}

		// want to look at "sec" seconds of data
		double data_win = 30 * sec;

		int frames = (int) this.getFrameRate((int) data_win);
		// 최소 프레임 15?
		if (frames < 15) {
			frames = 15;
		}
		
		double[] data_set;
		data_set = getValueArray(start, (int) data_win);
		
		int[] peaks = Peak_Detector.Peak_Detection(data_set, frames);

		if (peaks.length < 2) {
			return 0;
		}

		double HR = 0;
		double beats = 0;
		for (int i = 0; i < peaks.length - 1; i++) {
			HR = HR + set.get(peaks[i + 1]).getTime() - set.get(peaks[i]).getTime();
			beats++;
		}
		
		HR = 60000.0 * beats / HR;

		if (HR < 30 || HR > 150)
			return 0;		

		return HR;
	}

	public double complexHeartRate(int sec) { // sec == 4
		// if the set size is less than 30 points there is not enough points to
		// determine anything
		if (set.size() < 30) {
			return 0;
		}

		// want to look at "sec" seconds of data
		double data_win = 30 * sec;

		int frames = (int) this.getFrameRate((int) data_win);
		if (frames < 15) {
			frames = 15;
		}
		
		double[] data_set;
		if (set.size() < data_win) {
			data_set = getValueArray();
		} else {
			data_set = getValueArray((int) (set.size() - data_win), (int) data_win);
		}

		Log.e(TAG, "samplerate"+frames);
		
		int[] peaks = Peak_Detector.Peak_Detection(data_set, frames);
		// int[] peaks = Peak_Detector.Peak_Detection(data_set, 30);

		if (peaks.length < 2) {
			return 0;
		}

		double HR = 0;
		double beats = 0;
		for (int i = 0; i < peaks.length - 1; i++) {
			HR = HR + set.get(peaks[i + 1]).getTime() - set.get(peaks[i]).getTime();
			beats++;
		}
		Log.e("TAG peaks", peaks[0]+"");
		Log.e("TAG beats", beats+"");
		
		// 60000을 곱하는 이유는? 60초?
		HR = 60000.0 * beats / HR;

		if (HR < 30 || HR > 150)
			return 0;		
		

		return HR;
	}

	public double getFrameRate() {
		
		double sum = 0;
		
		for (int i = 1; i < set.size(); i++)
			sum += set.get(i).getTime() - set.get(i - 1).getTime();
		return 1000 / (sum / (set.size() - 1));
	}

	public double getFrameRate(int points) {
		
		if (set.size() <= points + 1) {
			return getFrameRate();
		} else {
			double sum = 0;
			for (int i = set.size() - points + 1; i < set.size(); i++)
				sum += set.get(i).getTime() - set.get(i - 1).getTime();

			return 1000 / (sum / (points));
		}
	}

	public ArrayList<Double> getRR() {
		return RR;
	}

	public void PeakDetection() {

		// Extra variable for intialization
		int TH;

		// Does initial Setup of data
		double[] sig_all = new double[set.size()];
		for (int i = 0; i < set.size(); i++)
			sig_all[i] = set.get(i).getValue();

		int fs = (int) getFrameRate();

		int Lx = sig_all.length;
		double[] sig_temp = Arrays.copyOf(sig_all, Lx);
		double[] final_points = new double[set.size()];

		int sh = fs;
		int w = fs * 2;

		double[] pc_G_pre;
		int TH_maxtomedian = 1000;

		int i = 1;
		int j = 1;

		double[] sig;
		sig = new double[w - 1];

		while (i < Lx - w) {
			System.arraycopy(sig_all, i - 1, sig, i - 1, w - 1);
			TH = TH_maxtomedian * 3;

		}
	}

	// function of mean (average)
	public static double mean(double[] p) {
		double sum = 0;// sum of elements
		for (int i = 0; i < p.length; i++) {
			sum += p[i];
		}
		return sum / p.length;
	}

	// function of maximum index
	public static int max_ind(double[] t) {
		int j = 0;
		double maximum = t[0];
		for (int i = 0; i < t.length; i++) {
			if (t[i] > maximum) {
				j = i;
			}
		}
		return j;
	}

	public static double max(double[] inArray) {
		if (inArray.length < 1) {
			return 0;
		}
		double max_val = inArray[0];
		for (int i = 0; i < inArray.length; i++) {
			if (inArray[i] > max_val)
				max_val = inArray[i];
		}
		return max_val;
	}

	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%PAC
	// Detection%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

	public int detect_pac() {
		double[] RRI = this.getRRI();
		
		if (RRI == null) {
			return 0;
		} else {
			return pac_detect(RRI);
		}
	}

	public int pac_detect(double[] RRI) {
		// Returns 0 for normal
		// Returns 3 for Trigemini
		// Returns 4 for quadgemini

		int len = RRI.length - 1;

		double[] y1temp = new double[len];
		double[] y2temp = new double[len];

		double[] k1_arr, k2_arr;

		int[] seq = new int[len];
		double THR_small = 0.1;

		System.arraycopy(RRI, 1, y1temp, 0, len);
		System.arraycopy(RRI, 0, y2temp, 0, len);

		k1_arr = diff(y1temp);
		k2_arr = diff(y2temp);

		for (int i = 0; i < len - 1; i++) {
			if (k1_arr[i] > THR_small && k2_arr[i] > -THR_small && k2_arr[i] < THR_small) {
				seq[i] = 1;

			} else if (k1_arr[i] > THR_small && k2_arr[i] > -THR_small && k2_arr[i] > THR_small) {
				seq[i] = 5;
			} else if (k1_arr[i] < -THR_small && k2_arr[i] > THR_small) {
				seq[i] = 2;
			} else if (k1_arr[i] < THR_small && k2_arr[i] < -THR_small && k1_arr[i] > -THR_small) {
				seq[i] = 3;
			} else if (k1_arr[i] < THR_small && k2_arr[i] < -THR_small && k1_arr[i] < -THR_small) {
				seq[i] = 6;
			} else if (k1_arr[i] > THR_small && k2_arr[i] < -THR_small) {
				seq[i] = 4;
			} else {
				seq[i] = 0;
			}
		}

		if (findSeq3(seq)) {
			// Trigemini Sequence detected return 3
			return 3;
		}

		if (findSeq4(seq)) {
			// quadgemini sequence detected return 4
			return 4;
		}

		return 0;
	}

	public boolean findSeq4(int[] seq) {
		boolean output = false;
		for (int i = 2; i < seq.length; i++) {
			// Tests to see if the sequence 1,2,3 exists and if any instance is
			// found return true for trigemini
			if (seq[i] == 3 && seq[i - 1] == 2 && seq[i - 2] == 1) {
				output = true;
				System.out.println("Quadgemini Detected: " + (i - 1));
				// return true;
			}
		}

		System.out.println("PAC calculation finished");
		return output;
	}

	public boolean findSeq3(int[] seq) {
		boolean output = false;
		for (int i = 2; i < seq.length; i++) {
			// Tests to see if the sequence 1,2,3 exists and if any instance is
			// found return true for trigemini
			if (seq[i] == 2 && seq[i - 1] == 4 && seq[i - 2] == 2) {
				output = true;
				System.out.println("Trigemini Detected: " + (i - 1));
				// return true;
			}

			// Tests to see if the sequence 1,2,3 exists and if any instance is
			// found return true for trigemini
			if (seq[i] == 4 && seq[i - 1] == 2 && seq[i - 2] == 4) {
				output = true;
				System.out.println("Trigemini Detected: " + (i - 1));
				// return true;
			}
		}

		return output;
	}

	public static double[] diff(double[] inArray) {
		double[] result = new double[inArray.length - 1];
		for (int i = 0; i < result.length; i++) {
			result[i] = inArray[i + 1] - inArray[i];
		}
		return result;
	}
}
