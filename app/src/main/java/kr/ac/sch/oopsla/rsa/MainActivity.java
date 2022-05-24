package kr.ac.sch.oopsla.rsa;

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

import androidx.appcompat.app.AppCompatActivity;

import kr.ac.sch.oopsla.rsa.common.TextFileWrite;
import kr.ac.sch.oopsla.rsa.process.DataSet;
import kr.ac.sch.oopsla.rsa.process.GraphView_cus;
import kr.ac.sch.oopsla.rsa.process.HeartRate;
import kr.ac.sch.oopsla.rsa.process.SharedData;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "RSA";

    private CamView2 view;
    private Button stopBtn;
    private Button startBtn;
    private Camera camera;
    private HeartRate heartRate;
    private GraphView_cus heartGraph;


    private progressBarThread pThread = null;
    private ProgressBar proBar;
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

    static {
        System.loadLibrary("jnilib");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.m4);




        proBar = (ProgressBar) findViewById(R.id.m2psbar1);

        // TextView
        heartRate = (HeartRate) findViewById(R.id.m2_heartRate);

        // ImageView
        heartGraph = (GraphView_cus) findViewById(R.id.bview);

        stopBtn = (Button) findViewById(R.id.stopButton);
        stopBtn.setOnClickListener(this);
        startBtn = (Button) findViewById(R.id.startButton);
        startBtn.setOnClickListener(this);

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
        view = new CamView2(this, camera, heartGraph, heartRate, s.getPerFn(), s.getPerLn(), s.get_Position(), exe, startBtn, stopBtn);

        ((FrameLayout) findViewById(R.id.preview)).addView(view);
    }

    public void openPM() {
        // 공유 저장 데이터 초기화
        sd = getSharedPreferences("pref", MODE_PRIVATE);
        ed = sd.edit();
        s.setPre(sd, ed);

        PersonAge = s.get_PsAge();
        totalTime = 60 + normalTime*2;

        // 프로그레스바의 최대 범위 설정
        proBar.setMax(totalTime);
    }

    // 쓰레드 핸들러
    Handler progressBarHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (view.getStartState() && !view.getHaveToStopProgress()) {
                // 경과 시간 증가
                elapsedTime++;
                proBar.setProgress(elapsedTime);

                // 시간이 모두 경과됬을 경우 멈춤
                if (elapsedTime == totalTime) {
                    stopBtn.performClick();
                }
            }
        }
    };

    // 쓰레드
    public class progressBarThread extends Thread{
        private boolean flag = true;

        public progressBarThread(){
            elapsedTime = 0;
            proBar.setProgress(elapsedTime);
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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.stopButton) {
            if (view != null) {
                pThread.stopThread();
                view.stopView();

                fulsig = view.getSignal();
                DeepBreathing = view.getDeepbreathing();

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

            }
            else {
                Toast.makeText(getApplicationContext(), "측정을 시작해야합니다.", Toast.LENGTH_SHORT).show();
            }
        }
        else if (id == R.id.startButton && startBtn.getText().equals("Start")) {
            startBtn.setText("Restart");
            view.setStartState();
            pThread = new progressBarThread();
            pThread.start();
        }
        else if (id == R.id.startButton && startBtn.getText().equals("Restart")) {
            if (view != null) {
                startBtn.setText("Start");
                view.signal_initialization();
                pThread.stopThread();
                elapsedTime = 0;
                proBar.setProgress(elapsedTime);
            }
        }
    }
}