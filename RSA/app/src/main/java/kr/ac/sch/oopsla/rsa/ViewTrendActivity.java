package kr.ac.sch.oopsla.rsa;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

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
    AnyChartView lineChart;
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


        lineChart = (AnyChartView) findViewById(R.id.chart_view_trend_chart);

        backBtn = (Button) findViewById(R.id.button_view_trend_back);
        backBtn.setOnClickListener((v)->{finish();});

        Cartesian cartesian = AnyChart.line();

        cartesian.animation(true);
        cartesian.padding(10d, 20d, 5d, 20d);
        cartesian.xMinorGrid(10);
        cartesian.yMinorGrid(10);
        cartesian.yScale().softMaximum(90);
        cartesian.yScale().softMinimum(12);
        cartesian.yScale().ticks().interval(5);
        cartesian.crosshair().enabled(true);
        cartesian.crosshair()
                .yLabel(true)
                // TODO ystroke
                .yStroke((Stroke) null, null, null, (String) null, (String) null);
        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.title("Trend of RSAs And HRs");

        List<DataEntry> seriesData = new ArrayList<>();

        trendTv = (TextView) findViewById(R.id.text_view_trend_period);
        trendTv.setText(startDay + " ~ " + endDay);

        initMax();

        for (int i = 0; i < mArrayList.size(); i++) {
            seriesData.add(new CustomDataEntry(mArrayList.get(i),RSAs.get(i),AvgHRs.get(i)));
        }
        Set set = Set.instantiate();
        set.data(seriesData);
        Mapping seriesMapping = set.mapAs("{ x: 'x', value: 'value' }");
        Mapping series2Mapping = set.mapAs("{ x: 'x', value: 'value2' }");
        Line series = cartesian.line(seriesMapping);
        series.name("RSA");
        series.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        series.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5d)
                .offsetY(5d);
        series.color("blue");

        Line series2 = cartesian.line(series2Mapping);
        series2.name("AvgHR");
        series2.hovered().markers().enabled(true);
        series2.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        series2.tooltip()
                .position("right")
                .anchor(Anchor.LEFT_CENTER)
                .offsetX(5d)
                .offsetY(5d);
        series2.color("red");


        cartesian.legend().enabled(true);
        cartesian.legend().fontSize(13d);
        cartesian.legend().padding(0d, 0d, 10d, 0d);

        lineChart.setChart(cartesian);
    }

    private class CustomDataEntry extends ValueDataEntry{

        public CustomDataEntry(String x, Number value, Number value2) {
            super(x, value);
            setValue("value2",value2);

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
