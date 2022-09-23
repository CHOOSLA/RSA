package kr.ac.sch.oopsla.rsa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;


public class MainActivity extends AppCompatActivity{

    Button mRsaBtn;
    Button mViewBtn;
    Button mProfileBtn;

    static {
        System.loadLibrary("opencv_java4");
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mRsaBtn = (Button)findViewById(R.id.button_rsa);
        mViewBtn = (Button)findViewById(R.id.button_view);
        mProfileBtn = (Button)findViewById(R.id.button_profile);


        mRsaBtn.setOnClickListener((v) -> {
            Intent intent = new Intent(MainActivity.this,MeasureActivity.class);
            startActivity(intent);
        });

        mViewBtn.setOnClickListener((v)->{
            Intent intent = new Intent(MainActivity.this, ViewDateActivity.class);
            startActivity(intent);
        });

        mProfileBtn.setOnClickListener((v)->{
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        /*
        //스크린이 계속 켜져 있게 하는 것
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

         */


    }

}