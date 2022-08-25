package kr.ac.sch.oopsla.rsa;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import kr.ac.sch.oopsla.rsa.common.TextFileWrite;
import kr.ac.sch.oopsla.rsa.process.CustomGraphView;
import kr.ac.sch.oopsla.rsa.process.DataSet;
import kr.ac.sch.oopsla.rsa.process.HeartRate;
import kr.ac.sch.oopsla.rsa.process.SharedData;

public class MeasureActivity extends Activity implements View.OnClickListener {
    private final String TAG = "MeasureActivity";

    private CamView mCamview;
    private Button mStopBtn;
    private Button mStartBtn;
    private Camera mCamera;
    private HeartRate mHeartRate;
    private CustomGraphView mCustomGraphView;

    private progressBarThread pThread = null;
    private ProgressBar mProgressBar;
    private int elapsedTime = 0;
    private TextView progBtotalTime;
    private TextView progTime;
    private int totalTime = 0;

    private DataSet mDataSet;
    private DataSet fulsig, leftNormal, rightNormal, DeepBreathing;

    private SharedPreferences sd;
    private SharedPreferences.Editor ed;
    SharedData s = new SharedData();

    private TextFileWrite<Double> textFile;
    private String exe="";

    private int PersonAge = 20;
    boolean flag = false;

    private int normalTime = 30;

    private int intervalCal = 5;

    @Override
    public void onDestroy(){
        if(pThread != null){
            if(pThread.isAlive()){
                pThread.stopThread();
            }
        }

        super.onDestroy();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("test");
        setContentView(R.layout.activity_measure);
        System.out.println("test2");

        mProgressBar = findViewById(R.id.progressbar);
        mHeartRate = findViewById(R.id.heartrate);
        mCustomGraphView = findViewById(R.id.custom_graph_view);

        mStartBtn = findViewById(R.id.button_start);
        mStartBtn.setOnClickListener(this);
        mStopBtn = findViewById(R.id.button_stop);
        mStopBtn.setOnClickListener(this);

        // 공유 저장 데이터 초기화 및 프로그레스바의 최대 범위 설정
        openPM();

        if(s.get_Position() == "Upright"){
            if(s.get_Exercise() == "No"){
                exe = "운동 전";
            }
            else{
                exe = "운동 후";
            }
        }
        else{
            exe = "";
        }

        // SurfaceView
        mCamview = new CamView(this, mCamera, mCustomGraphView, mHeartRate,
                s.getPerFn(), s.getPerLn(), s.get_Position(), exe, mStartBtn, mStopBtn);

        ((FrameLayout) findViewById(R.id.preview)).addView(mCamview);
    }

    @Override
    public void onClick(View v) {


        int id = v.getId();
        System.out.println(mStartBtn.getText() +"입니다");
        if(id== R.id.button_start){
            System.out.println("눌렸습니다");
        }
        if (id == R.id.button_stop) {
            if (mCamview != null) {
                pThread.stopThread();
                mCamview.stopView();

                fulsig = mCamview.getSignal();
                DeepBreathing = mCamview.getDeepbreathing();

                textFile = new TextFileWrite<Double>(s.getPerFn(), s.getPerLn(), s.get_Position(), exe, "ANS_SIGNAL_WRITE");

                SharedData s = new SharedData();
                SharedPreferences sd = getSharedPreferences("pref", MODE_PRIVATE);
                SharedPreferences.Editor ed = sd.edit();
                s.setPre(sd,ed);
                s.set_PPG_Path(textFile.getPath());

                textFile.TextFileInit(textFile.getPath(), "fulSignal");
                for(int i=0; i<fulsig.size(); i++){
                    textFile.add(fulsig.getValue(i), fulsig.getTime(i));
                }

                textFile.TextFileInit(textFile.getPath(), "DeepBreathing");
                for(int i=0; i<DeepBreathing.size(); i++){
                    textFile.add(DeepBreathing.getValue(i), DeepBreathing.getTime(i));
                }

                double[] hrarr = DeepBreathing.getHRArray(intervalCal, 5);

                Intent intent = new Intent(getApplicationContext(), LoadingShow.class);
                intent.putExtra("INTERVAL", intervalCal);
                intent.putExtra("AGE", PersonAge);
                intent.putExtra("normalTime", normalTime);
                intent.putExtra("HRARR", hrarr);
                intent.putExtra("textFilePath", textFile.getPath());
                startActivity(intent);
                finish();
            }
            else {
                Toast.makeText(getApplicationContext(), "측정을 시작해야합니다.", Toast.LENGTH_SHORT).show();
            }
        }
        else if (id == R.id.button_start && mStartBtn.getText().equals("Start")) {
            System.out.println("started");

            mStartBtn.setText("Restart");
            mCamview.setStartState();
            pThread = new progressBarThread();
            pThread.start();
        }
        else if (id == R.id.button_start && mStartBtn.getText().equals("Restart")) {
            if (mCamview != null) {
                mStartBtn.setText("Start");
                mCamview.signal_initialization();
                pThread.stopThread();
                elapsedTime = 0;
                mProgressBar.setProgress(elapsedTime);
            }
        }

    }

    public void openPM() {
        // 공유 저장 데이터 초기화
        sd = getSharedPreferences("pref", MODE_PRIVATE);
        ed = sd.edit();
        s.setPre(sd, ed);

        PersonAge = s.get_PsAge();
        totalTime = 60 + normalTime*2;

        // 프로그레스바의 최대 범위 설정
        mProgressBar.setMax(totalTime);
    }
    // 쓰레드 핸들러
    Handler progressBarHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mCamview.getStartState() && !mCamview.getHaveToStopProgress()) {
                // 경과 시간 증가
                elapsedTime++;
                mProgressBar.setProgress(elapsedTime);

                // 시간이 모두 경과됬을 경우 멈춤
                if (elapsedTime == totalTime) {
                    mStopBtn.performClick();
                }
            }
        }
    };

    // 쓰레드
    public class progressBarThread extends Thread{
        private boolean flag = true;

        public progressBarThread(){
            elapsedTime = 0;
            mProgressBar.setProgress(elapsedTime);
        }

        public void stopThread(){
            this.flag = false;
        }

        @Override
        public void run() {
            try
            {
                while(this.flag){
                    progressBarHandle.sendMessage(progressBarHandle.obtainMessage());
                    Thread.sleep(1000);
                }
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

}
