package kr.ac.sch.oopsla.rsa;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import kr.ac.sch.oopsla.rsa.db.DBtype;

public class ViewListActivity extends Activity implements View.OnClickListener {
    private Button btnCallNext;
    private Button btnCallBack;
    private TextView mDateDisplay1;
    private TextView mDateDisplay2;
    private String endDay;
    private String startDay;
    private CustomAdapter mCustomAdapter = null;
    private ArrayList<String> mArrayList = new ArrayList<String>();
    private Set<String> mArrayDateList = new HashSet<String>();

    private ArrayList<String> mCheckArrayList = new ArrayList<String>();

    private ListView mListView = null;
    private CheckBox mAllCheckBox = null;
    private DBtype db = null;
    Cursor cursor = null;
    private int position = 0;
    private String Selected;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        Intent intent = getIntent();
        endDay = intent.getStringExtra("endDay");
        startDay = intent.getStringExtra("startDay");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_list);
        setLayout();
        MainMethod();


        btnCallBack = (Button) findViewById(R.id.button_result_list_back);
        btnCallBack.setOnClickListener(this);
        btnCallNext = (Button) findViewById(R.id.button_result_list_next);
        btnCallNext.setOnClickListener(this);

        mDateDisplay1 = (TextView)findViewById(R.id.text_result_list_start);
        String[] arrayDay = startDay.split("-");
        mDateDisplay1.setText(arrayDay[1]+"-"+arrayDay[2]+"-"+arrayDay[0]);

        mDateDisplay2 = (TextView)findViewById(R.id.text_result_list_end);

        arrayDay = endDay.split("-");
        mDateDisplay2.setText(arrayDay[1]+"-"+arrayDay[2]+"-"+arrayDay[0]);

//		mDateDisplay2.setText(Selected+"");
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.button_result_list_back) { // back
            finish();
        }
        else if (id == R.id.button_result_list_next) { // next
            int count = mCustomAdapter.getChecked().size();


                if(count == 0) {
                    Toast.makeText(getApplicationContext(), "Select at least one!", Toast.LENGTH_SHORT).show();
                }
                else {
                    for(int i = 0; i < mCustomAdapter.getChecked().size(); i++) {
                        position = mCustomAdapter.getChecked().get(i);
                        mCheckArrayList.add(mArrayList.get(position));
                    }


                    Intent intent1 = new Intent(ViewListActivity.this, ViewSelectTypeActivity.class);
                    intent1.putExtra("Date", mCheckArrayList);
                    intent1.putExtra("startDay", startDay);
                    intent1.putExtra("endDay",endDay);
                    startActivity(intent1);

                }

        }
    }

    // Custom Adapter
    class CustomAdapter extends BaseAdapter {

        private ViewHolder viewHolder = null;
        // ?뜝?뜲????? ?뜝?룞?삕?뜝?룞?삕 ?뜝?룞?삕?뜝?룞?삕?뜝????? ?뜝?룞?삕?뜝?룞?삕 Inflater
        private LayoutInflater inflater = null;
        private ArrayList<String> sArrayList = new ArrayList<String>();
        private boolean[] isCheckedConfrim;

        public CustomAdapter (Context c , ArrayList<String> mList) {
            inflater = LayoutInflater.from(c);
            this.sArrayList = mList;
            // ArrayList Size ?뜝?룞?삕?겮?뜝?룞?삕 boolean ?뜝?띁?뿴?뜝?룞?삕 ?뜝?룞?삕?뜝?룞?삕?뜝?????.
            // CheckBox?뜝?룞?삕 true/false?뜝?룞?삕 ?뜝?룞?삕?뜝?룞?삕 ?뜝?떦源띿??? ?뜝?룞?삕?뜝?룞?삕
            this.isCheckedConfrim = new boolean[sArrayList.size()];
        }

        // CheckBox?뜝?룞?삕 ?뜝?룞?삕?뜝????? ?뜝?룞?삕?뜝?룞?삕?뜝?떦?뙋?삕 ?뜝?뙣?눦?삕?뜝?룞?삕
        public void setAllChecked(boolean ischeked) {
            int tempSize = isCheckedConfrim.length;
            for(int a=0 ; a<tempSize ; a++){
                isCheckedConfrim[a] = ischeked;
            }
        }

        public void setChecked(int position) {
            isCheckedConfrim[position] = !isCheckedConfrim[position];
        }

        public ArrayList<Integer> getChecked(){
            int tempSize = isCheckedConfrim.length;
            ArrayList<Integer> mArrayList = new ArrayList<Integer>();
            for(int b=0 ; b<tempSize ; b++){
                if(isCheckedConfrim[b]){
                    mArrayList.add(b);
                }
            }
            return mArrayList;
        }

        @Override
        public int getCount() {
            return sArrayList.size();
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // ConvertView?뜝?룞?삕 null ?뜝?룞?삕 ?뜝?룞?삕?뜝?????
            View v = convertView;

            if( v == null ){
                viewHolder = new ViewHolder();
                // View?뜝?룞?삕 inflater ?뜝?룞?삕?뜝?룞?삕?뜝?뙏?뙋?삕.
                v = inflater.inflate(R.layout.item_view_list, null);
                viewHolder.cBox = (CheckBox) v.findViewById(R.id.checkBoxListPeriod);
                v.setTag(viewHolder);
            }

            else {
                viewHolder = (ViewHolder)v.getTag();
            }

            // CheckBox?뜝?룞?삕 ?뜝?뜦蹂멨????룞?삕?뜝?룞?삕?뜝?룞?삕 ?뜝?떛??????????듃?뜝?룞?삕 ?뜝?룞?삕?뜝?룞?삕?뜝?룞?삕 ?뜝?뙇源띿??? ?뜝?룞?삕?뜝?룞?삕?뜝?룞?삕 ListView?뜝?룞?삕 ?뜝?룞?삕?뜝?룞?삕?뜝?룞?삕
            // ?겢?뜝?룞?삕?뜝?룞?삕?뜝?룞?삕鼇뜹???????? ?뜝?룞?삕?뜝?룞?삕閭먨???????? ?뜝?룞?삕?뜝?뙏?눦?삕?뜝?룞?삕 CheckBox?뜝?룞?삕 ?뜝?떛??????????듃?뜝?룞?삕 ?뜝?룞?삕?뜝?룞?삕 ?뜝?뙇?뼲?삕?뜝????? ?뜝?떬?뙋?삕.
            viewHolder.cBox.setClickable(false);
            viewHolder.cBox.setFocusable(false);

            viewHolder.cBox.setText(sArrayList.get(position));
            // isCheckedConfrim ?뜝?띁?뿴?뜝?룞?삕 ?뜝?떗源띿????솕?뜝?룞?삕 ?뜝?룞?삕?뜝????? false?뜝?룞?삕 ?뜝?떗源띿????솕 ?뜝?떎湲곕븣?????룞?삕?뜝?룞?삕
            // ?뜝?뜦蹂멨????룞?삕?뜝?룞?삕?뜝?룞?삕 false?뜝?룞?삕 ?뜝?떗源띿????솕 ?뜝?룞?삕?궗 ?뜝?룞?삕 ?뜝?뙇?뙋?삕.
            viewHolder.cBox.setChecked(isCheckedConfrim[position]);

            return v;
        }
    }

    class ViewHolder {
        // ?뜝?룞?삕?뜝?떥?슱?삕 Row?뜝?룞?삕 ?뜝?룞?삕????????? CheckBox
        private CheckBox cBox = null;
    }

    private void setLayout(){
        mListView = (ListView) findViewById(R.id.listview_result_list_list);

        mAllCheckBox = (CheckBox) findViewById(R.id.checkbox_result_list_checkbox);
        // ?뜝?룞?삕泥? 泥댄?? ?뜝?룞?삕?듉 ?겢?뜝?룞?삕?뜝?룞?삕 Listener
        mAllCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomAdapter.setAllChecked(mAllCheckBox.isChecked());
                // Adapter?뜝?룞?삕 Data?뜝?룞?삕 ?뜝?룞?삕?솕?뜝?룞?삕 ?뜝?룞?삕?뜝?룞?삕?뜝?룞?삕?뜝?룞?삕 Adapter?뜝?룞?삕 ?뜝?떙琉꾩????뜝?뙏?뙋?삕.
                mCustomAdapter.notifyDataSetChanged();
            }
        });
    }


    private void MainMethod() {
        String query = null;

            query = "SELECT * FROM DATA2";


//		Toast.makeText(getApplicationContext(), selected, Toast.LENGTH_SHORT).show();
//
//		query = "SELECT * FROM DATA2";

        mArrayList = new ArrayList<String>();

        db = new DBtype(ViewListActivity.this);
        cursor = db.selectRawQuery(query);

        String s, c;

        if(cursor.moveToFirst()){
            do{
                s = cursor.getString(cursor.getColumnIndex("date"));
                c = cursor.getString(cursor.getColumnIndex("time"));

                if(s==null || c==null){
                    break;
                }
                Log.e(s, c);
                if(startDay.compareTo(s) <= 0 && endDay.compareTo(s)>= 0){
                    mArrayList.add(s + " " + c);
                }
            }while(cursor.moveToNext());
        }

        //mArrayList = new ArrayList<String>(mArrayDateList);

        mCustomAdapter = new CustomAdapter(ViewListActivity.this , mArrayList);
        mListView.setAdapter(mCustomAdapter);
        mListView.setOnItemClickListener(mItemClickListener);
    }


    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
            mCustomAdapter.setChecked(position);
            // Data ?뜝?룞?삕?뜝?룞?삕?뜝????? ?샇?뜝?룞?삕 Adapter?뜝?룞?삕 Data ?뜝?룞?삕?뜝?룞?삕 ?뜝?룞?삕?뜝?룞?삕?뜝????? ?뜝?떙琉꾩????뜝?뛿?꽌 Update ?뜝?룞?삕.
            mCustomAdapter.notifyDataSetChanged();

        }
    };
}
