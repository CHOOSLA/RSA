package kr.ac.sch.oopsla.rsa;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applikeysolutions.cosmocalendar.model.Day;
import com.applikeysolutions.cosmocalendar.selection.OnDaySelectedListener;
import com.applikeysolutions.cosmocalendar.selection.RangeSelectionManager;
import com.applikeysolutions.cosmocalendar.selection.SingleSelectionManager;
import com.applikeysolutions.cosmocalendar.utils.SelectionType;
import com.applikeysolutions.cosmocalendar.view.CalendarView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;


public class ViewActivity extends AppCompatActivity implements ViewRecyclerAdapter.ItemClickListener, ImageButton.OnClickListener{

    ViewRecyclerAdapter mViewRecyclerAdapter;

    CalendarView calendarView;
    ImageButton mBtnDateSingle,mBtnDateRange;
    String startDate, endDate;
    RangeSelectionManager rangeSelectionManager;
    RangeManager rangeManager = new RangeManager(()->{});
    SingleManager singleManager = new SingleManager(()->{});
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        mBtnDateSingle = findViewById(R.id.button_date_single);
        mBtnDateRange = findViewById(R.id.button_date_range);

        mBtnDateSingle.setOnClickListener(this);
        mBtnDateRange.setOnClickListener(this);

        recyclerView = findViewById(R.id.recycler_history);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mViewRecyclerAdapter = new ViewRecyclerAdapter(this);
        mViewRecyclerAdapter.setClickListener(this);
        recyclerView.setAdapter(mViewRecyclerAdapter);

        // 커스텀 달력 설정
        calendarView = findViewById(R.id.cosmo_calendar);
        // 주의 첫번 째 요일을 월요일로
        calendarView.setFirstDayOfWeek(Calendar.MONDAY);

        //Orientation 0 = Horizontal | 1 = Vertical
        calendarView.setCalendarOrientation(0);

        //휴일을 일요일로
        calendarView.setWeekendDays(new HashSet(){{
            add(Calendar.SUNDAY);
        }});


        // 처음시작시 기간으로 설정
        calendarView.setSelectionType(SelectionType.RANGE);

        calendarView.setSelectionManager(rangeManager);

        /*
        button.setOnClickListener((v) -> {
            if (calendarView.getSelectionManager() instanceof RangeSelectionManager) {
                RangeSelectionManager rangeSelectionManager = (RangeSelectionManager) calendarView.getSelectionManager();
                if(rangeSelectionManager.getDays() != null) {
                    startDate = rangeSelectionManager.getDays().first.toString();
                    endDate = rangeSelectionManager.getDays().second.toString();

                    Date start = rangeSelectionManager.getDays().first.getCalendar().getTime();
                    Date end = rangeSelectionManager.getDays().second.getCalendar().getTime();

                    // 포맷팅 정의
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

                    // 포맷팅 적용
                    String formatedStart = formatter.format(start);
                    String formatedEnd = formatter.format(end);

                    Log.e("Range Data","Start : " + startDate + " , End : " + endDate);
                } else {
                    Toast.makeText(ViewActivity.this, "Invalid Selection", Toast.LENGTH_SHORT).show();
                }
            }
                }
        );

         */



        Date now = new Date();
        Date previous = new Date();
        try {
            previous = AddDate(now,0,0,-7);
        } catch (Exception e) {
            e.printStackTrace();
        }

        calendarView.getSelectionManager().toggleDay(new Day(previous));
        calendarView.getSelectionManager().toggleDay(new Day(now));


    }

    @Override
    public void onItemClick(View view, int position){
        Log.i("TAG","itm: "+mViewRecyclerAdapter.getItem(position));
        Intent intent1 = new Intent(ViewActivity.this,ResultGraphActivity.class);
        ArrayList<String> tmp = new ArrayList<String>();
        tmp.add(mViewRecyclerAdapter.getItem(position));
        intent1.putExtra("Date",tmp );
        startActivity(intent1);
    }

    private static Date AddDate(Date date, int year, int month, int day) {


        Calendar cal = Calendar.getInstance();

        cal.setTime(date);


        cal.add(Calendar.YEAR,  year);
        cal.add(Calendar.MONTH, month);
        cal.add(Calendar.DATE,  day);

        return cal.getTime();
    }

    private void searchDate(Date start, Date end){

    }

    private void searchDate(Date date){

    }

    class SingleManager extends SingleSelectionManager{

        DaySelectedListener daySelectedListener = new DaySelectedListener();

        public SingleManager(OnDaySelectedListener onDaySelectedListener) {
            super(onDaySelectedListener);
            this.onDaySelectedListener = daySelectedListener;
        }
        class DaySelectedListener implements OnDaySelectedListener{

            @Override
            public void onDaySelected() {
                Toast.makeText(ViewActivity.this, calendarView.getSelectedDays().get(0).getCalendar().getTime().toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    class RangeManager extends RangeSelectionManager{

        DaySelectedListener daySelectedListener = new DaySelectedListener();

        public RangeManager(OnDaySelectedListener onDaySelectedListener) {
            super(onDaySelectedListener);

            this.onDaySelectedListener = daySelectedListener;
        }

        class DaySelectedListener implements OnDaySelectedListener{

            @Override
            public void onDaySelected() {
                if(getDays() != null) {
                    startDate = getDays().first.toString();
                    endDate = getDays().second.toString();


                    Date start = getDays().first.getCalendar().getTime();
                    Date end = getDays().second.getCalendar().getTime();

                    // 포맷팅 정의
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

                    // 포맷팅 적용
                    String formatedStart = formatter.format(start);
                    String formatedEnd = formatter.format(end);

                    mViewRecyclerAdapter.searchDate(formatedStart,formatedEnd);


                    Log.e("Range Data","Start : " + startDate + " , End : " + endDate);
                } else {
                    //Toast.makeText(ViewActivity.this, "Invalid Selection", Toast.LENGTH_SHORT).show();
                    if(mViewRecyclerAdapter.mArrayList!=null){
                        mViewRecyclerAdapter.mArrayList.clear();
                    }



                }

                recyclerView.setAdapter(mViewRecyclerAdapter);

            }
        }


    }



    @Override
    public void onClick(View v) {
        final int sdk = android.os.Build.VERSION.SDK_INT;
        LinearLayout linSingle = findViewById(R.id.linear_date_single);
        LinearLayout linRange = findViewById(R.id.linear_date_range);
        switch (v.getId()){
            case R.id.button_date_single:
                calendarView.setSelectionType(SelectionType.SINGLE);
                calendarView.setSelectionManager(singleManager);

                if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    linSingle.setBackgroundDrawable(ContextCompat.getDrawable(linSingle.getContext(), R.drawable.background_circle_orange) );
                    linRange.setBackgroundDrawable(null);
                } else {
                    linSingle.setBackground(ContextCompat.getDrawable(linSingle.getContext(), R.drawable.background_circle_orange));
                    linRange.setBackground(null);
                }
                break;
            case R.id.button_date_range:
                calendarView.setSelectionType(SelectionType.RANGE);
                calendarView.setSelectionManager(rangeManager);
                calendarView.clearSelections();
                if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    linRange.setBackgroundDrawable(ContextCompat.getDrawable(linRange.getContext(), R.drawable.background_circle_orange) );
                    linSingle.setBackground(null);
                } else {
                    linRange.setBackground(ContextCompat.getDrawable(linRange.getContext(), R.drawable.background_circle_orange));
                    linSingle.setBackground(null);
                }
                break;
        }
    }
}
