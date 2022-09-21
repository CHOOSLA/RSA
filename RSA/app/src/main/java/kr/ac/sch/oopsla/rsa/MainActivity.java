package kr.ac.sch.oopsla.rsa;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Button;


import java.util.Collections;
import java.util.List;

import static android.Manifest.permission.CAMERA;


public class MainActivity extends AppCompatActivity{

    Button mRsaBtn;
    Button mViewBtn;
    Button mTestBtn;

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


        mRsaBtn.setOnClickListener((v) -> {
            Intent intent = new Intent(MainActivity.this,MeasureActivity.class);
            startActivity(intent);
        });

        mViewBtn.setOnClickListener((v)->{
            Intent intent = new Intent(MainActivity.this,ViewSelectActivity.class);
            startActivity(intent);
        });

        /*
        //스크린이 계속 켜져 있게 하는 것
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

         */


    }

}