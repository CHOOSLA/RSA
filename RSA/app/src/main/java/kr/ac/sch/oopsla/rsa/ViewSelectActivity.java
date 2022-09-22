package kr.ac.sch.oopsla.rsa;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import kr.ac.sch.oopsla.rsa.androidqa.ArrayWheelAdapter;
import kr.ac.sch.oopsla.rsa.androidqa.OnWheelChangedListener;
import kr.ac.sch.oopsla.rsa.androidqa.OnWheelScrollListener;
import kr.ac.sch.oopsla.rsa.androidqa.WheelView;
import kr.ac.sch.oopsla.rsa.process.calendarProcess;
import ru.slybeaver.slycalendarview.SlyCalendarDialog;

public class ViewSelectActivity extends AppCompatActivity implements SlyCalendarDialog.Callback {

    Context ctx;
    boolean wheelScrolled ;
    String wheelMenu1[] = new String[]{"1","2","3","4","5","6","7","8","9","10","11","12"};
    String wheelMenu2[] = new String[]{"Days", "Weeks", "Months","Years"};
    ImageButton mImgBtnCalendar;

    private String today;
    private String changeDay;
    private WheelView wvNum;
    private WheelView wvWord;

    Button mBtnBack,mBtnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_select);

        // wheel 부분 설정
        ctx = ViewSelectActivity.this;
        initWheelDay(R.id.lmcp2);
        initWheelNum(R.id.lmcp1);

        wvNum = (WheelView)findViewById(R.id.lmcp1);
        wvWord = (WheelView)findViewById(R.id.lmcp2);

        mImgBtnCalendar = (ImageButton) findViewById(R.id.ImgButton_Calendar);

        // SlyCalendar 설정

        FragmentManager fgManager = getSupportFragmentManager();
        mImgBtnCalendar.setOnClickListener((v) -> {
            new SlyCalendarDialog()
                    .setSingle(false)
                    .setCallback(this)
                    .show(fgManager, "TAG_SLYCALENDAR");
        });


        // SlyCalendar가 종료되었을 때 뜨는 콜백 메소드
        fgManager.registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
            @Override
            public void onFragmentDestroyed(@NonNull FragmentManager fm, @NonNull Fragment f) {
                super.onFragmentDestroyed(fm, f);
                Log.e("SlyCalendar","SlyCalendar Destroyed");

                    nextPage(changeDay,today);


            }
        }, true);

        today = new SimpleDateFormat(getString(R.string.dateFormat), Locale.getDefault()).format(new Date().getTime());

        mBtnBack = findViewById(R.id.button_result_view_back);
        mBtnNext = findViewById(R.id.button_result_view_next);

        mBtnBack.setOnClickListener((v) -> { finish();});
        mBtnNext.setOnClickListener((v) -> {nextPage(changeDay,today);});

    }



    // wheel 부분

    private void initWheelDay(int id)
    {
        WheelView wheel = (WheelView) findViewById(id);
        wheel.setViewAdapter(new ArrayWheelAdapter(ctx,wheelMenu2));
        wheel.setVisibleItems(2);
        wheel.setCurrentItem(0);
        wheel.addChangingListener(changedListener);
        wheel.addScrollingListener(scrolledListener);
    }

    private void initWheelNum(int id)
    {
        WheelView wheel = (WheelView) findViewById(id);
        wheel.setViewAdapter(new ArrayWheelAdapter(ctx, wheelMenu1));
        wheel.setVisibleItems(2);
        wheel.setCurrentItem(0);
        wheel.addChangingListener(changedListener);
        wheel.addScrollingListener(scrolledListener);
    }

    OnWheelScrollListener scrolledListener = new OnWheelScrollListener()
    {
        public void onScrollStarts(WheelView wheel)
        {
            wheelScrolled = true;
        }

        public void onScrollEnds(WheelView wheel)
        {
            wheelScrolled = false;
        }

        @Override
        public void onScrollingStarted(WheelView wheel) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onScrollingFinished(WheelView wheel) {
            // TODO Auto-generated method stub

        }
    };

    // Wheel changed listener
    private final OnWheelChangedListener changedListener = new OnWheelChangedListener()
    {
        public void onChanged(WheelView wheel, int oldValue, int newValue)
        {
            if (!wheelScrolled)
            {
                updateStatus();
            }
        }
    };

    /**
     * Updates entered PIN status
     */
    private void updateStatus()
    {

        changeDay = calendarProcess.subDay(today, wvNum.getCurrentItem()+1, wvWord.getCurrentItem());
        //Toast.makeText(this,changeDay,Toast.LENGTH_SHORT).show();

    }


    // 여기서 부턴 SlyCalendar 부분

    @Override
    public void onDataSelected(Calendar firstDate, Calendar secondDate, int hours, int minutes) {

        if (firstDate != null) {
            String firstDateFormat = new SimpleDateFormat(getString(R.string.dateFormat), Locale.getDefault()).format(firstDate.getTime());

            // 하나의 날짜만 선택됬을 경우
            if (secondDate == null) {
                firstDate.set(Calendar.HOUR_OF_DAY, hours);
                firstDate.set(Calendar.MINUTE, minutes);
                /*
                Toast.makeText(
                        this,
                        new SimpleDateFormat(getString(R.string.timeFormat), Locale.getDefault()).format(firstDate.getTime()),
                        Toast.LENGTH_LONG

                ).show();

                 */

                changeDay = firstDateFormat;
                today = firstDateFormat;

            } else {
                String secondDateFormat = new SimpleDateFormat(getString(R.string.dateFormat), Locale.getDefault()).format(secondDate.getTime());
                // 기간이 선택이 되었을 경우
                /*
                Toast.makeText(
                        this,
                        getString(
                                R.string.period,
                                new SimpleDateFormat(getString(R.string.dateFormat), Locale.getDefault()).format(firstDate.getTime()),
                                new SimpleDateFormat(getString(R.string.timeFormat), Locale.getDefault()).format(secondDate.getTime())
                        ),
                        Toast.LENGTH_LONG

                ).show();

                 */

                changeDay = firstDateFormat;
                today = secondDateFormat;

            }
        }
    }

    @Override
    public void onCancelled() {

    }

    private void nextPage(String changeDay,String today){
        if(changeDay==null||today==null){
            Toast.makeText(ctx,"Please Select Date/Period!",Toast.LENGTH_LONG).show();
        }else {
            Intent intent1 = new Intent(ViewSelectActivity.this, ViewListActivity.class);
            intent1.putExtra("startDay", changeDay);
            intent1.putExtra("endDay", today);
            startActivity(intent1);
        }
    }
}

