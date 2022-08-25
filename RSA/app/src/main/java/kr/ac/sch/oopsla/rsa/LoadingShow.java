package kr.ac.sch.oopsla.rsa;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import kr.ac.sch.oopsla.rsa.algorithms.Peak_Detector;
import kr.ac.sch.oopsla.rsa.common.TextFileWrite;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LoadingShow extends Activity implements OnClickListener {

    private ProgressBar progressBar;
    private final String TAG = "Loading2";
    private double avg = 0;
    String s;
    int i;
    double d1, d2;
    double[] HRarr, HR_fin;
    int PersonAge;
    int[] up_fin, dw_fin;
    int intervalCal;
    int normalTime;

    double[] filteredHRs;
    double[] filteredHRs_2;

    private double rsa = 0;
    private double ei_ratio = 0;

    private Button cancelBtn;
    private TextFileWrite<Double> textFile = new TextFileWrite<Double>();

    //   private final int LEFT_LENGTH = 70, DEEP_LENGTH = 123; // 8초 - 125
//   private final int LEFT_LENGTH = 72, DEEP_LENGTH = 114; // 5초 - 125
    private final int LEFT_LENGTH = 53, DEEP_LENGTH = 117; // 5초 - 120초

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_show);

        cancelBtn = (Button) findViewById(R.id.button_cancel);
        cancelBtn.setOnClickListener(this);

        // get intent
        intervalCal = getIntent().getExtras().getInt("INTERVAL");
        PersonAge = getIntent().getExtras().getInt("AGE");
//      avg = getIntent().getExtras().getDouble("AVG");
        HRarr = getIntent().getDoubleArrayExtra("HRARR");
//      leftNormal = getIntent().getDoubleArrayExtra("leftNormal");
//      rightNormal = getIntent().getDoubleArrayExtra("rightNormal");
        String textFilePath = getIntent().getStringExtra("textFilePath");
        normalTime = getIntent().getExtras().getInt("normalTime");

        textFile.TextFileInit(textFilePath, "DEEPBR_HR_arr");
        textFile.add(TextFileWrite.wrapperArray(HRarr));

        textFile.TextFileInit(textFilePath, "age");
        textFile.add((int) PersonAge);

        //new HTTPTask().execute();  호흡 데이터 가져오기
        //
        //             작성해야함
        //

        //////////////////////////////// HRARR filter!!!!!!!
        filteredHRs = filterHR(HRarr);
        filteredHRs_2 = filterHR(filteredHRs);

        textFile.TextFileInit(textFilePath, "filtered_HR_arr");
        textFile.add(TextFileWrite.wrapperArray(filteredHRs_2));

        /////////////////////////////////////// remove !55~120

        int left_count = 0;
        int deep_count = 0;

        ArrayList<Double> filtered_list = new ArrayList<Double>();
        double[] removedArr;

        for (int i = 0; i < filteredHRs_2.length; i++) {
            if (filteredHRs_2[i] <= 55 || filteredHRs_2[i] >= 120) {
                //필터링된 HRarr(평균RGB값)이 55이하이거나 120이상일때
                if (i < LEFT_LENGTH) {
                    //5초 미만이면 여기
                    left_count = left_count + 1;
                } else if (i < LEFT_LENGTH + DEEP_LENGTH + 1) {
                    //126초 미만이면 여기
                    deep_count = deep_count + 1;
                }
                //나머진 여기
                filteredHRs_2[i] = 0;
            }
        }

        for (int i = 0; i < filteredHRs_2.length; i++) {
            if (filteredHRs_2[i] != 0) {
                filtered_list.add(filteredHRs_2[i]);
            }
        }

        //removedArr로 옮겨짐
        removedArr = new double[filtered_list.size()];
        for (int i = 0; i < removedArr.length; i++) {
            removedArr[i] = filtered_list.get(i);
        }

        textFile.TextFileInit(textFilePath, "removedArr");
        textFile.add(TextFileWrite.wrapperArray(removedArr));

        //////////////////////////////// get hr avg
        double sum = 0;
        for (int i = 0; i < removedArr.length; i++) {
            sum += removedArr[i];
        }
        avg = sum / removedArr.length;

        //////////////////////////////// left - db data split!!!!!!!
//      double[] leftHr, dbHr;
//      
//      
//      int leftHrSize = (int) 55;
//      int dbHRSize = (int) 180;
//      
//      leftHr = new double[leftHrSize];
//      dbHr = new double[dbHRSize - leftHr.length];
//      
//      for(int i=0;i<leftHr.length;i++){
//    	  leftHr[i] = filteredHRs_2[i];
//      }
//      for(int i=0;i<dbHr.length;i++){
//    	  dbHr[i] = filteredHRs_2[i+leftHr.length];
//      }
//      
        //////////////////////////////// get PEAKS
        double[] up, dw;
        double[][] result;
        rsa = 0;

//      result = getpeaks(dbHr, intervalCal);
        result = getPeaksByPeakDetection(removedArr, 30);

        up = result[0];
        dw = result[1];

        ArrayList<Integer> upFin = new ArrayList<Integer>();
        ArrayList<Integer> dwFin = new ArrayList<Integer>();

        for (int i = 0; i < up.length; i++) {
//    	  up[i] = (int) (up[i]+leftHr.length);
//    	  up_fin[i] = (int) up[i];
            if (up[i] >= LEFT_LENGTH - left_count && up[i] <= LEFT_LENGTH + DEEP_LENGTH - left_count - deep_count) {
                upFin.add((int) up[i]);
            }
        }

        for (int i = 0; i < dw.length; i++) {
//    	  dw[i] = (int) (dw[i]+leftHr.length);
//    	  dw_fin[i] = (int) dw[i];
            if (dw[i] >= LEFT_LENGTH - left_count && dw[i] <= LEFT_LENGTH + DEEP_LENGTH - left_count - deep_count) {
                dwFin.add((int) dw[i]);
            }
        }

        double[] up_fin, dw_fin;

        up_fin = new double[upFin.size()];
        dw_fin = new double[dwFin.size()];

        for (int i = 0; i < upFin.size(); i++) {
            up_fin[i] = upFin.get(i);
        }
        for (int i = 0; i < dwFin.size(); i++) {
            dw_fin[i] = dwFin.get(i);
        }

        textFile.TextFileInit(textFilePath, "upPeaks");
        textFile.add(TextFileWrite.wrapperArray(up_fin));

        textFile.TextFileInit(textFilePath, "downPeaks");
        textFile.add(TextFileWrite.wrapperArray(dw_fin));

        // 최좌측 최우측 피크가 올바른지 검사
        // up_fin 이나 dw_fin 배열의 크기가 0인 경우를 고려해야 한다.
        try {
            double[] peak_temp;
            if (up_fin[0] > dw_fin[0]) {
                peak_temp = new double[dw_fin.length - 1];
                for (int i = 0; i < peak_temp.length; i++) {
                    peak_temp[i] = dw_fin[i + 1];
                }
                dw_fin = peak_temp;
            }

            if (up_fin[up_fin.length - 1] > dw_fin[dw_fin.length - 1]) {
                peak_temp = new double[up_fin.length - 1];
                for (int i = 0; i < peak_temp.length; i++) {
                    peak_temp[i] = up_fin[i];
                }
                up_fin = peak_temp;
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }

        // 피크배열의 최소 길이 구하기
        double minLength;

        if (up_fin.length > dw_fin.length) {
            minLength = dw_fin.length;
        } else {
            minLength = up_fin.length;
        }

        // ei_ratio 산출
        double highAvg = 0;
        double lowAvg = 0;

        for (int i = 0; i < minLength; i++) {
            int up_fin_idx = (int) up_fin[i];
            int dw_fin_idx = (int) dw_fin[i];

            if (up_fin_idx < removedArr.length) {
                highAvg += removedArr[up_fin_idx];
            }

            if (dw_fin_idx < removedArr.length) {
                lowAvg += removedArr[dw_fin_idx];
            }
        }
        highAvg = highAvg / minLength;
        lowAvg = lowAvg / minLength;
        ei_ratio = highAvg / lowAvg;

        // rsa 산출
        for (int i = 0; i < minLength; i++) {
            rsa += removedArr[(int) up_fin[i]] - removedArr[(int) dw_fin[i]];
        }
        if (rsa < 0) {
            rsa = -rsa;
        }
        rsa = rsa / minLength;

        textFile.TextFileInit(textFilePath, "rsa");
        textFile.add(rsa);

        textFile.TextFileInit(textFilePath, "ei_ratio");
        textFile.add(ei_ratio);

        progressBar = (ProgressBar) findViewById(R.id.progress_circular);

        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("AVG", avg);
        intent.putExtra("RSA", rsa);
        intent.putExtra("EIRATIO", ei_ratio);
        intent.putExtra("AGE", PersonAge);
        intent.putExtra("HRARR", removedArr);
        intent.putExtra("UP", up_fin);
        intent.putExtra("DOWN", dw_fin);
        intent.putExtra("LEFTSTART", LEFT_LENGTH - left_count);
        intent.putExtra("RIGHTSTART", LEFT_LENGTH + DEEP_LENGTH - left_count - deep_count);

        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        finish();
    }

    public static double[] getdwPeaks(List<ValueIndex> hrList, int intervalTime) {
        List<ValueIndex> TmpList, dwResult;
        dwResult = new ArrayList<ValueIndex>();
        double[] dw_fin;
        int left = 0;
        int right = 10;
        int base = 20;

        TmpList = getListByStartEnd(hrList, left, right);

        if (TmpList != null) {
            Collections.sort(TmpList);
            dwResult.add(TmpList.get(0));
            left = right;

        }

        right = left + base;

        do {
            TmpList = getListByStartEnd(hrList, left, right);

            if (TmpList == null) {
                break;
            } else {
                Collections.sort(TmpList);
                dwResult.add(TmpList.get(0));
            }

            left = right;
            right = left + base;
        } while (TmpList != null);

        dw_fin = new double[dwResult.size()];
        for (int i = 0; i < dwResult.size(); i++) {
            dw_fin[i] = dwResult.get(i).getIndex();
        }

        return dw_fin;
    }

    public static double[][] simplePeaks(double[] HRarr) {
        // input hr array
        // return [0][] is uppeaks, [1][] is down peaks
        // process matlab - peaks
        double[] up, dw;

        double[] ds1 = new double[HRarr.length - 1];
        for (int i = 0; i < HRarr.length - 1; i++) {
            ds1[i] = HRarr[i + 1] - HRarr[i];
        }

        // ds=[ds(1);ds];
        double[] ds2 = new double[HRarr.length];
        ds2[0] = ds1[0];
        for (int i = 1; i < HRarr.length; i++) {
            ds2[i] = ds1[i - 1];
        }

        // filter=find(ds(2:end)==0)+1
        int fcount = 0;
        for (int i = 1; i < ds2.length; i++) {
            if (ds2[i] == 0) {
                fcount++;
            }
        }
        int[] filter = new int[fcount];
        fcount = 0;
        for (int i = 1; i < ds2.length; i++) {
            if (ds2[i] == 0) {
                filter[fcount++] = i;
            }
        }

        // ds(filter)=ds(filter-1)
        for (int i = 0; i < filter.length; i++) {
            ds2[filter[i]] = ds2[filter[i] - 1];
        }

        // ds=sign(ds)
        int[] sign_ds = new int[ds2.length];
        for (int i = 0; i < ds2.length; i++) {
            if (ds2[i] > 0) {
                sign_ds[i] = 1;
            } else {
                sign_ds[i] = -1;
            }
        }

        // ds=diff(ds)
        double[] ds3 = new double[sign_ds.length - 1];
        for (int i = 0; i < sign_ds.length - 1; i++) {
            ds3[i] = sign_ds[i + 1] - sign_ds[i];
        }

        // p=find(ds>0); == up peaks t=find(ds<0); == down peaks
        int pcount = 0, mcount = 0;
        for (int i = 0; i <= ds3.length - 1; i++) {
            if (ds3[i] > 0) {
                pcount++;
            } else if (ds3[i] < 0) {
                mcount++;
            }
        }
        double[] p = new double[mcount];
        double[] t = new double[pcount];
        pcount = 0;
        mcount = 0;
        for (int i = 0; i <= ds3.length - 1; i++) {
            if (ds3[i] > 0) {
                t[pcount++] = i;
            } else if (ds3[i] < 0) {
                p[mcount++] = i;
            }
        }

        up = new double[p.length];
        dw = new double[t.length];
        for (int i = 0; i < up.length; i++) {
            up[i] = p[i];
        }
        for (int i = 0; i < dw.length; i++) {
            dw[i] = t[i];
        }

        up = new double[p.length];
        dw = new double[t.length];
        for (int i = 0; i < up.length; i++) {
            up[i] = p[i];
        }
        for (int i = 0; i < dw.length; i++) {
            dw[i] = t[i];
        }

        double[][] result = new double[2][];
        result[0] = up;
        result[1] = dw;
        return result;
    }

    public static double[][] getpeaks(double[] HRarr, int intervalTime) {
        // input hr array
        // return [0][] is uppeaks, [1][] is down peaks

        //////////////////// get down peaks
        double[] dw_fin, up_fin;

        List<ValueIndex> hrList = new ArrayList<ValueIndex>();
        for (int i = 0; i < HRarr.length; i++) {
            hrList.add(new ValueIndex(HRarr[i], i));
        }

        dw_fin = getdwPeaks(hrList, intervalTime);

        double[][] peaks_one = simplePeaks(HRarr);

        double[] up_one = peaks_one[0];
        double[] dw_one = peaks_one[1];

        ///////////////////// get up peaks
        List<ValueIndex> upPeaks = new ArrayList<ValueIndex>();

        for (int i = 0; i < up_one.length; i++) {
            upPeaks.add(new ValueIndex(HRarr[(int) up_one[i]], (int) up_one[i]));
        }

        up_fin = getupPeaks(upPeaks, dw_fin);

        double[][] result = new double[2][];
        result[0] = up_fin;
        result[1] = dw_fin;

        return result;
    }

    public static double[] getupPeaks(List<ValueIndex> upPeaks, double[] dw_fin) {
        List<ValueIndex> TmpList, upResult;
        upResult = new ArrayList<ValueIndex>();
        double[] up_fin;

        for (int i = 0; i < dw_fin.length - 1; i++) {
            TmpList = getListByStartEnd(upPeaks, (int) dw_fin[i], (int) dw_fin[i + 1]);
            if (TmpList == null) {
                break;
            } else {
                Collections.sort(TmpList);
                upResult.add(TmpList.get(TmpList.size() - 1));
            }
        }

        up_fin = new double[upResult.size()];
        for (int i = 0; i < upResult.size(); i++) {
            up_fin[i] = upResult.get(i).getIndex();
        }

        return up_fin;
    }

    public static double[] filterHR(double[] arr) {
        double[] HRarr = new double[arr.length];
        for (int i = 0; i < arr.length; i++) {
            HRarr[i] = arr[i];
        }
        double slope_sub, slope_left, slope_right;
        double left_value, right_value;
        boolean isSignEqual, isContinuous;
        int count, index;


        //
        for (int i = 1; i < HRarr.length - 1; i++) {
            count = 0;
            index = i;


            do {
                //현재 점에서 왼쪽과 오른쪽의 차이를 구함
                slope_left = (HRarr[index] - HRarr[index - 1]);
                slope_right = (HRarr[index + 1] - HRarr[index]);
                slope_sub = Math.abs(slope_left - slope_right);

                if (slope_left > 0 && slope_right > 0) {
                    //현재 점이 왼쪽보다 크고, 오른쪽보다 작은 경우
                    //그래프에서 보면 상승 중인 것을 생각하면 됨
                    isSignEqual = true;
                } else if (slope_left < 0 && slope_right < 0) {
                    //현재 점이 왼쪽보다 작고, 오른쪽보다 큰경우
                    //그래프에서 보면 하강 중인 것을 생각하면 됨
                    isSignEqual = true;
                } else {
                    //그렇지 않을 경우
                    //꼭짓점이 생긴 경우가 여기로 들어감
                    isSignEqual = false;
                }

                if (slope_sub >= 5 && !isSignEqual) {
                    //그 차이가 5이상이고 그래프 상에서 상승중이거나 하강중이 아닌 경우 즉, 꼭짓점이 생긴경우
                    isContinuous = true;
                    //아마도 이것의 카운트는 꼭짓점을 말하는 것 같기도 하다
                    count = count + 1;
//					HRarr[index] = (HRarr[index+1] + HRarr[index-1]) / 2;
                } else {
                    isContinuous = false;
                }

                index = index + 1;

                //배열의 끝이면 종료
                if (index == HRarr.length - 1) break;
            } while (isContinuous); //꼭짓점이 발견될때 까지 반복


            if (count > 0) {
                if (index >= HRarr.length - 1) {
                    //마지막으로 발견된 꼭짓점이 마지막 인덱스일 경우
                    index = HRarr.length - 2;
                    //마지막부분에서 하나 뒤로 보냄? 왜 그렇죠
                }

                left_value = HRarr[i - 1];
                //시작됬던 곳 하나 전
                right_value = HRarr[index];
                //마지막에 발견된 꼭짓점

                for (int j = 0; j < count; j++) {
                    HRarr[i + j] = left_value + ((right_value - left_value) / (count + 1) * (j + 1));
                    //HRarr[i+j]의 의미는 꼭짓점들을 순회하겠단 것을 의미한다
                    //시작됬던 곳 + ((마지막에 발견된 꼭짓점이 시작됬던 곳의 차) / (
                    // 떠는 값들을 일정하게 늘려주어서 없애주는것
                }

                i = i + count;
                //fitered 된 꼭짓점들은 스킵
            }
        }

        return HRarr;
    }

    public static double[][] getPeaksByPeakDetection(double[] arr, double fs) {
        // input  : HR array, fs
        // return : result[2][] result[0][] is up peaks / result[1][] is down peaks
        int[] up, dw;
        double[] uup, ddw;

        // get up peaks
        up = Peak_Detector.Peak_Detection_for_hrArr(arr, 30);

        // get Reversed arr
        double[] arrReverse = new double[arr.length];
        double avg = 0;
        for (int i = 0; i < arr.length; i++) {
            avg += arr[i];
        }
        avg = avg / arr.length;
        for (int i = 0; i < arr.length; i++) {
            arrReverse[i] = avg + (avg - arr[i]);
        }

        //get dw peaks
        dw = Peak_Detector.Peak_Detection_for_hrArr(arrReverse, 30);

        uup = new double[up.length];
        ddw = new double[dw.length];
        for (int i = 0; i < up.length; i++) {
            uup[i] = up[i];
        }
        for (int i = 0; i < dw.length; i++) {
            ddw[i] = dw[i];
        }

        double[][] result = new double[2][];
        result[0] = uup;
        result[1] = ddw;

        return result;
    }

    public static List<ValueIndex> getListByStartEnd(List<ValueIndex> list, int start, int end) {
        int s = 0, e = 0;
        if (start <= 0) {
            start = 0;
            if (end <= 0) {
                end = 0;
            }
        }

        if (end >= list.get(list.size() - 1).getIndex()) {
            end = list.get(list.size() - 1).getIndex();
            if (start >= list.get(list.size() - 1).getIndex()) {
                start = list.get(list.size() - 1).getIndex();
            }
        }

        if (start == end) {
            return null;
        } else {
            for (int i = 0; i < list.size(); i++) {
                if (start <= list.get(i).getIndex()) {
                    s = i;
                    break;
                }
            }

            for (int i = list.size() - 1; i >= 0; i--) {
                if (end >= list.get(i).getIndex()) {
                    e = i;
                    break;
                }
            }

            List<ValueIndex> result = new ArrayList<ValueIndex>();
            for (int i = s; i <= e; i++) {
                result.add(list.get(i));
            }
            return result;
        }
    }
}

class ValueIndex implements Comparable<ValueIndex> {
    private double value;
    private int index;

    public ValueIndex(double value, int index) {
        this.value = value;
        this.index = index;
    }

    public double getValue() {
        return this.value;
    }

    public int getIndex() {
        return this.index;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public int compareTo(ValueIndex o) {
        // TODO Auto-generated method stub
        if (value < o.getValue()) {
            return -1;
        } else if (value == o.getValue()) {
            return 0;
        } else {
            return 1;
        }
    }

    public String getString() {
        return value + "," + index;
    }
}
