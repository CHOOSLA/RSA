package kr.ac.sch.oopsla.rsa;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import kr.ac.sch.oopsla.rsa.db.DBtype;

public class ViewRecyclerAdapter extends RecyclerView.Adapter<ViewRecyclerAdapter.ViewHolder>{

    LayoutInflater mInflater;

    ItemClickListener mClickListener;
    ImageButton mImgButtonBack;
    TextView mTextViewDate;

    DBtype db;
    Cursor cursor;

    String query = "SELECT * FROM DATA2";
    ArrayList<String> mArrayList;
    ArrayList<String> mDateTimeList;

    ViewRecyclerAdapter(Context context){
        this.mInflater = LayoutInflater.from(context);

        db = new DBtype(context);

    }

    public void searchDate(String start,String end)  {


        String mDate, mTime, mRsaValue, mHeartRateValue;
        mArrayList = new ArrayList<String>();
        mDateTimeList = new ArrayList<String>();

        try{
            end = addDate(end,0,0,1);
        } catch (Exception e){
            System.out.println("날짜 계산 오류");
        }
        while(!start.equals(end)){
            String query = "SELECT * FROM DATA2 WHERE date = \"" + start + "\"";
            cursor = db.selectRawQuery(query);
            if(cursor.moveToFirst()){
                do{
                    mDate = cursor.getString(cursor.getColumnIndex("date"));
                    mTime = cursor.getString(cursor.getColumnIndex("time"));
                    mRsaValue = cursor.getString(cursor.getColumnIndex("rsa"));
                    mHeartRateValue = cursor.getString(cursor.getColumnIndex("heartrate"));
                    if(mDate==null||mTime==null){
                        continue;
                    }
                    Log.e(mDate, mTime);
                    mArrayList.add("[ " + mDate + " " + mTime + " ]\n"+
                            "RSA : " + mRsaValue + " , " +
                            "HEARTRATE : " + mHeartRateValue

                    );
                    mDateTimeList.add(mDate + " " + mTime);
                }while(cursor.moveToNext());
            }

            try{
                start = addDate(start,0,0,1);
            } catch (Exception e){
                break;
            }
        }


    }

    public void searchDate(String date){
        String query = "SELECT * FROM DATA2 WHERE date " + date;
        cursor = db.selectRawQuery(query);

        String mDate, mTime, mRsaValue, mHeartRateValue;
        mArrayList = new ArrayList<String>();
        mDateTimeList = new ArrayList<String>();

        if(cursor.moveToFirst()){
            do{
                mDate = cursor.getString(cursor.getColumnIndex("date"));
                mTime = cursor.getString(cursor.getColumnIndex("time"));
                mRsaValue = cursor.getString(cursor.getColumnIndex("rsa"));
                mHeartRateValue = cursor.getString(cursor.getColumnIndex("heartrate"));
                if(mDate==null||mTime==null){
                    continue;
                }
                Log.e(mDate, mTime);
                mArrayList.add("[ " + mDate + " " + mTime + " ]\n"+
                        "RSA : " + mRsaValue + " , " +
                        "HEARTRATE : " + mHeartRateValue

                );
                mDateTimeList.add(mDate + " " + mTime);
            }while(cursor.moveToNext());
        }
    }

    private String addDate(String strDate, int year, int month, int day) throws Exception {

        SimpleDateFormat dtFormat = new SimpleDateFormat("yyyy-MM-dd");

        Calendar cal = Calendar.getInstance();

        Date dt = dtFormat.parse(strDate);

        cal.setTime(dt);

        cal.add(Calendar.YEAR,  year);
        cal.add(Calendar.MONTH, month);
        cal.add(Calendar.DATE,  day);

        return dtFormat.format(cal.getTime());
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mTextView.setText(mArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    String getItem(int id){
        return mDateTimeList.get(id);
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    void setClickListener(ItemClickListener itemClickListener){
        this.mClickListener = itemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView mTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.info_item);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mClickListener !=null){
                mClickListener.onItemClick(v,getAdapterPosition());
            }

        }
    }
}
