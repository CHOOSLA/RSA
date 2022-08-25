package kr.ac.sch.oopsla.rsa.common;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TextFileWrite<T>  implements Serializable{
	private final String TAG = "TEXT_FILE_WRITE";
	private String path;
	private File file;

	public TextFileWrite() {}
	
	// folder name
	public TextFileWrite(String folderName) {
		String path = Environment.getExternalStorageDirectory().toString() + "/" + folderName;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.KOREA);
		Date currentTime = new Date();
		String str = formatter.format(currentTime).replace(':', '_');

		file = new File(path);
		if (!file.exists()) {
			file.mkdir();
			Log.e(TAG, folderName + "폴더생성");
		} else {
			Log.e(TAG, folderName + "폴더생성");
		}

		file = new File(path +"/"+ str);

		if (!file.exists()) {
			file.mkdir();
			Log.e(TAG, str + "폴더생성");
		} else {
			Log.e(TAG, str + "폴더생성");
		}

		this.path = path +"/"+ str;
	}

	
	public TextFileWrite(String PerFn, String PerLn, String pos, String ex, String folderName) {
		
		String path = Environment.getExternalStorageDirectory().toString() + "/" + folderName;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm분", Locale.KOREA);
		Date currentTime = new Date();
		String str = PerLn + PerFn +" "+ pos +" "+ ex + " " + formatter.format(currentTime).replace(':', '시');
		
		file = new File(path);
		if (!file.exists()) {
			file.mkdir();
			Log.e(TAG, folderName + "폴더생성");
		} else {
			Log.e(TAG, folderName + "폴더생성");
		}

		file = new File(path +"/"+ str);

		if (!file.exists()) {
			file.mkdir();
			Log.e(TAG, str + "폴더생성");
		} else {
			Log.e(TAG, str + "폴더생성");
		}

		this.path = path +"/"+ str;
	}

	public void TextFileInit(String path, String fileName) {
		file = new File(path, fileName + ".txt");
	}

	public void add(T data, double time) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(file, true));
			out.append(time + ", "+String.valueOf(data)+"\r\n");
			out.flush();
			out.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Double[] wrapperArray(double[] array){
		Double[] result = new Double[array.length];
		
		for(int n =0; n < array.length; n++){
			result[n] = new Double(array[n]);
		}
		return result;
	}


	public void add(T[] data) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(file, true));
			for (int n = 0; n < data.length; n++) {
				out.append(String.valueOf(data[n]) + " ");
				out.flush();				
			}
			out.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void add(int data) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(file, true));
			out.append(data+" ");
			out.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void add(double data) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(file, true));
			out.append(data+" ");
			out.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getPath() {
		return path;
	}
}
