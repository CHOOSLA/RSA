package kr.ac.sch.oopsla.rsa;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.anychart.APIlib;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Line;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Anchor;
import com.anychart.enums.MarkerType;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.Stroke;

import org.w3c.dom.EntityReference;

import java.util.ArrayList;
import java.util.List;

import kr.ac.sch.oopsla.rsa.db.DBtype;

public class ViewTrendActivity extends Activity{

    String startDay,endDay;
    ArrayList<String> mArrayList = new ArrayList<String>();
   // AnyChartView lineChart;
    TextView trendTv;

    private DBtype db = null;
    Cursor cursor = null;
    int count = 0;
    int max = 0;

    private ArrayList<String> mArrayDateList = new ArrayList<String>();

    private ArrayList<String> HrArrays = new ArrayList<String>();
    private ArrayList<String> UpPeaks = new ArrayList<String>();
    private ArrayList<String> DwPeaks = new ArrayList<String>();
    private ArrayList<Double> RSAs = new ArrayList<Double>();

    private ArrayList<Integer> AvgHRs = new ArrayList<Integer>();
    private ArrayList<String> mnameArrayList = new ArrayList<String>();
    private ArrayList<Integer> Ages = new ArrayList<Integer>();
    private ArrayList<Integer> Rights = new ArrayList<Integer>();
    private ArrayList<Integer> Lefts = new ArrayList<Integer>();

    Button backBtn;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_trend);

        Intent intent = getIntent();
        endDay = intent.getStringExtra("endDay");
        startDay = intent.getStringExtra("startDay");
        mArrayList = intent.getStringArrayListExtra("Date");

        trendTv = (TextView) findViewById(R.id.text_view_trend_period);
        trendTv.setText(startDay + " ~ " + endDay);

        backBtn = (Button) findViewById(R.id.button_view_trend_back);
        backBtn.setOnClickListener((v)->{finish();});

        // HR 차트 설정

        AnyChartView lineChart1 = (AnyChartView) findViewById(R.id.HR_chart_view_trend_chart);
        APIlib.getInstance().setActiveAnyChartView(lineChart1);

        Cartesian cartesian1 = AnyChart.line();

        cartesian1.animation(true);
        cartesian1.padding(0d, 0d, 0d, 0d);
        cartesian1.xMinorGrid(10);
        cartesian1.yMinorGrid(10);
        cartesian1.yScale().softMaximum(110);
        cartesian1.yScale().softMinimum(50);
        cartesian1.yScale().ticks().interval(5);
        cartesian1.crosshair().enabled(true);
        cartesian1.crosshair()
                .yLabel(true)
                // TODO ystroke
                .yStroke((Stroke) null, null, null, (String) null, (String) null);
        cartesian1.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian1.title("HR");

        List<DataEntry> seriesData1 = new ArrayList<>();

        initMax();

        for (int i = 0; i < mArrayList.size(); i++) {
            seriesData1.add(new CustomDataEntry(mArrayList.get(i),AvgHRs.get(i)));
        }
        Set set = Set.instantiate();
        set.data(seriesData1);
        Mapping seriesMapping = set.mapAs("{ x: 'x', value: 'value' }");

        Line series = cartesian1.line(seriesMapping);
        series.name("HR");
        series.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        series.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5d)
                .offsetY(5d);
        series.color("red");


//        cartesian1.legend().enabled(true);
//        cartesian1.legend().fontSize(13d);
//        cartesian1.legend().padding(0d, 0d, 10d, 0d);

        lineChart1.setChart(cartesian1);

        // RSA 차트 설정

        AnyChartView lineChart2 = (AnyChartView) findViewById(R.id.RSA_chart_view_trend_chart);
        APIlib.getInstance().setActiveAnyChartView(lineChart2);

        Cartesian cartesian2 = AnyChart.line();

        cartesian2.animation(true);
        cartesian2.padding(0d, 0d, 0d, 0d);
        cartesian2.xMinorGrid(10);
        cartesian2.yMinorGrid(10);
        cartesian2.yScale().softMaximum(25);
        cartesian2.yScale().softMinimum(0);
        cartesian2.yScale().ticks().interval(1);
        cartesian2.crosshair().enabled(true);
        cartesian2.crosshair()
                .yLabel(true)
                // TODO ystroke
                .yStroke((Stroke) null, null, null, (String) null, (String) null);
        cartesian2.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian2.title("RSA");

        List<DataEntry> seriesData2 = new ArrayList<>();

        for (int i = 0; i < mArrayList.size(); i++) {
            seriesData2.add(new CustomDataEntry(mArrayList.get(i),RSAs.get(i)));
        }

        Set set2 = Set.instantiate();
        set2.data(seriesData2);
        Mapping seriesMapping2 = set2.mapAs("{ x: 'x', value: 'value' }");

        Line series2 = cartesian2.line(seriesMapping2);
        series2.name("RSA");
        series2.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        series2.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5d)
                .offsetY(5d);
        series2.color("blue");


//        cartesian2.legend().enabled(true);
//        cartesian2.legend().fontSize(13d);
//        cartesian2.legend().padding(0d, 0d, 10d, 0d);

        lineChart2.setChart(cartesian2);
    }

    private class CustomDataEntry extends ValueDataEntry{

        public CustomDataEntry(String x, Number value) {
            super(x, value);
        }
    }

    private void initMax() {
        int avghr, age, right, left;
        String time1, time2, name;
        String hrarr, uppeak, dwpeak;
        double rsa;

        db = new DBtype(ViewTrendActivity.this);

        for (int i = 0; i < mArrayList.size(); i++) {
            String date = mArrayList.get(i).substring(0, 10);
            String time = mArrayList.get(i).substring(11);
            String sql = "select * from DATA2 where date='" + date + "' and time='" + time + "';";
            cursor = db.selectRawQuery(sql);
            max = max + cursor.getCount();
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {

                time1 = cursor.getString(1);
                time2 = split_Time_not_milliSecond(cursor.getString(2));
                hrarr = cursor.getString(3);
                uppeak = cursor.getString(4);
                dwpeak = cursor.getString(5);
                rsa = cursor.getDouble(6);
                avghr = cursor.getInt(7);
                name = cursor.getString(9);
                age = cursor.getInt(11);
                right = cursor.getInt(12);
                left = cursor.getInt(13);

                mArrayDateList.add(time1 + " " + time2);
                HrArrays.add(hrarr);
                UpPeaks.add(uppeak);
                DwPeaks.add(dwpeak);
                RSAs.add(rsa);
                AvgHRs.add(avghr);
                mnameArrayList.add(name);
                Ages.add(age);
                Rights.add(right);
                Lefts.add(left);

                cursor.moveToNext();
            }
        }
    }

    public String split_Time_not_milliSecond(String str) {
        String[] array;
        array = str.split(":");

        return array[0] + ":" + array[1];
    }
}
