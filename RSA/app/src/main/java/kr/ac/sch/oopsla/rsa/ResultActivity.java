package kr.ac.sch.oopsla.rsa;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;

import kr.ac.sch.oopsla.rsa.db.DBtype;
import kr.ac.sch.oopsla.rsa.process.CustomGraphView2;
import kr.ac.sch.oopsla.rsa.process.SharedData;

public class ResultActivity extends Activity implements View.OnClickListener {
    private String TAG = "ResultActivity";


    private TextView mRsaText;
    private ImageView mRsaImage;

    int leftStart = 70;
    int rightStart = 185;

    int maxPoint = 150;
    ProgressBar pb;
    double avgHeart = 0;
    double rsa = 0;
    double ei_ratio = 0;
    double age = 20;


    Button mBackBtn;
    Button mSaveBtn;
    TextView symview;

    DBtype db;
    private SharedData sharedData = new SharedData();
    private SharedPreferences sd;
    private SharedPreferences.Editor ed;
    SharedData s = new SharedData();

    private String hrarray = null;
    private String uppeak = null;
    private String downpeak = null;

    private File file;
    double[] HR_fin;
    double[] up, dw;
    double rsastats = -1, eistats = -1;
    private CustomGraphView2 mHeartGraph;
    String textTmp = "";

    public void sharedLoad() {
        sd = getSharedPreferences("pref", MODE_PRIVATE);
        ed = sd.edit();

        sharedData.setPre(sd, ed);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        db = new DBtype(ResultActivity.this);

        sd = getSharedPreferences("pref", MODE_PRIVATE);
        ed = sd.edit();

        s.setPre(sd, ed);

        sharedLoad();

        /////////////// get Data //////////////
        Intent intent = getIntent();
        avgHeart = intent.getExtras().getDouble("AVG");
        rsa = intent.getExtras().getDouble("RSA");
        ei_ratio = intent.getExtras().getDouble("EIRATIO");
        age = intent.getExtras().getInt("AGE");
        HR_fin = intent.getDoubleArrayExtra("HRARR");
        up = intent.getDoubleArrayExtra("UP");
        dw = intent.getDoubleArrayExtra("DOWN");
        leftStart = intent.getExtras().getInt("LEFTSTART");
        rightStart = intent.getExtras().getInt("RIGHTSTART");

        /////////////// String hrarray, uppeak, downpeak //////////////
        hrarray ="";
        hrarray += HR_fin[0]+"";
        for(int i=1;i<HR_fin.length;i++){
            hrarray += ","+HR_fin[i];
        }

        uppeak ="";
        uppeak += up[0]+"";
        for(int i=1;i<up.length;i++){
            uppeak += ","+up[i];
        }

        downpeak ="";
        downpeak += dw[0]+"";
        for(int i=1;i<dw.length;i++){
            downpeak += ","+dw[i];
        }

        mHeartGraph = (CustomGraphView2) findViewById(R.id.custom_graph_view2);
        mHeartGraph.DetectMessage(true);
        mHeartGraph.addArr(HR_fin, up, dw, leftStart, rightStart);

        mRsaText = (TextView) findViewById(R.id.text_result_rsa);


        rsastats = evalRsa(rsa, age);

        if(rsastats == -1){
            textTmp += "Not measurable\r\n";
        }
        else if(rsastats == 0){
            textTmp += "Abnormal\r\n";
        }
        else if(rsastats == 1){
            textTmp += "Normal\r\n";
        }

        mRsaText.setText(textTmp);

        symview = (TextView) findViewById(R.id.text_result_hr);
        symview.setText(Math.round(avgHeart) + "");

        symview = (TextView) findViewById(R.id.text_result_rsa);
        symview.setText(Math.round(rsa*100)/100.0 + "");

        mBackBtn =findViewById(R.id.button_result_back);
        mBackBtn.setOnClickListener(this);
        mSaveBtn = findViewById(R.id.button_result_save);
        mSaveBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.button_result_save) {         // save 버튼

            try {
                saveDB();
            } catch (SocketException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            Toast.makeText(this,"저장완료", Toast.LENGTH_SHORT).show();
        }else if(id==R.id.button_result_back){
            Intent intent1 = new Intent(ResultActivity.this, MainActivity.class);
            startActivity(intent1);
            finish();
        }

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

    public void saveDB() throws SocketException, IOException, Exception {
        db.inserResRSA(
                hrarray.toString(),
                uppeak.toString(),
                downpeak.toString(),
                Double.parseDouble(String.format("%.2f", rsa)),
                (int)avgHeart,
                s.getPerBd().toString(),
                s.getPerFn().toString() + "_" + s.getPerMn().toString() + s.getPerLn().toString(),
                s.get_Gender().toString(),
                s.get_PsAge(),
                leftStart,
                rightStart);
    }
}
