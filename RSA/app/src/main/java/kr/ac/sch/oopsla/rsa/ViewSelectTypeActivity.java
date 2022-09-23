package kr.ac.sch.oopsla.rsa;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ViewSelectTypeActivity extends Activity implements View.OnClickListener {
    
    String startDay,endDay;
    ArrayList<String> mCheckArrayList = new ArrayList<String>();
    
    Button individualBtn,trendBtn;
    TextView periodTv;
    Button backBtn;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_select_type);

        Intent intent = getIntent();
        endDay = intent.getStringExtra("endDay");
        startDay = intent.getStringExtra("startDay");
        mCheckArrayList = intent.getStringArrayListExtra("Date");
        
        individualBtn = (Button) findViewById(R.id.button_view_select_type_individual);
        individualBtn.setOnClickListener(this);

        trendBtn = (Button) findViewById(R.id.button_view_select_type_trend);
        trendBtn.setOnClickListener(this);

        periodTv = (TextView) findViewById(R.id.text_view_select_type_period);
        periodTv.setText(startDay + " ~ " + endDay);

        backBtn = (Button) findViewById(R.id.button_view_select_type_back);
        backBtn.setOnClickListener((v)->{finish();});
        
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id==R.id.button_view_select_type_individual){
            Intent intent1 = new Intent(ViewSelectTypeActivity.this, ViewIndividualActivity.class);
            intent1.putExtra("Date", mCheckArrayList);
            startActivity(intent1);
        }else if(id==R.id.button_view_select_type_trend){
            Intent intent1 = new Intent(ViewSelectTypeActivity.this, ViewTrendActivity.class);
            intent1.putExtra("Date", mCheckArrayList);
            intent1.putExtra("startDay", startDay);
            intent1.putExtra("endDay", endDay);
            startActivity(intent1);
        }
    }
}
