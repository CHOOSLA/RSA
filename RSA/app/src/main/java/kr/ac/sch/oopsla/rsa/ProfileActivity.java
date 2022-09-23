package kr.ac.sch.oopsla.rsa;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import kr.ac.sch.oopsla.rsa.process.SharedData;

public class ProfileActivity extends Activity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private static final String TAG = null;
    private Button btnSave;
    private Button btnCancel;
    private SharedPreferences sd;
    private SharedPreferences.Editor ed;

    private EditText pFN;
    private EditText pMN; // 중간 이름
    private EditText pLN;
    private EditText pDOB; // 생년월일
    private EditText pAGE; // 나이
    private EditText pPhone_state; // 국가 번호
    private EditText pPhone_area; // 지역번호
    private EditText pPhone_first; // 앞 번호
    private EditText pPhone_last; // 뒷번호
    private EditText pEmail_id; // 메일 아이디
    private EditText pEmail_host_name; // 메일 서버
    private String pGender;
    private String pPosition;
    private String pExer;

    private EditText dFN;
    private EditText dMN; // (의사)중간 이름
    private EditText dLN;
    private EditText dPhone_state; // 국가 번호
    private EditText dPhone_area; // 지역번호
    private EditText dPhone_first; // 앞 번호
    private EditText dPhone_last; // 뒷번호
    private EditText dEmail_id; // 메일 아이디
    private EditText dEmail_host_name; // 메일 서버주소

    private EditText Time;
    private EditText Mini_Sym;
    private EditText Mini_Para;
    private EditText Mini_HR;
    private EditText Max_Sym;
    private EditText Max_Para;
    private EditText Max_HR;

    private RadioGroup rd;
    private RadioButton male;
    private RadioButton female;
    private RadioButton temp;
    private int check = 0;

    private RadioGroup rd_position,rd_exer;
    private RadioButton upright,yes;
    private RadioButton supine,no;
    private int check_position = 0,check_exer = 0;
    private RadioButton temp2,temp3;

    private RadioGroup sendRd;
    private RadioButton send_yes, send_no;
    private int check_send = 0;

    SharedData s = new SharedData();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ////////////////////// (id 변경해야됨)//////////////////////////////
        // 사용자 정보
        pFN = (EditText) findViewById(R.id.pmpfn);
        pMN = (EditText) findViewById(R.id.pmpmn);// personal mid name
        pLN = (EditText) findViewById(R.id.pmpln);
        pDOB = (EditText) findViewById(R.id.pmpbd);
        pPhone_state = (EditText) findViewById(R.id.pmpstate);// 국가번호
        pPhone_area = (EditText) findViewById(R.id.pmparea);// 지역번호
        pPhone_first = (EditText) findViewById(R.id.pmpfirst);// 앞번호
        pPhone_last = (EditText) findViewById(R.id.pmplast);// 뒷번호
        pEmail_id = (EditText) findViewById(R.id.pmpemail);// 메일 아이디
        pEmail_host_name = (EditText) findViewById(R.id.pmplstemail); // 메일 서버주소
        pAGE = (EditText) findViewById(R.id.Edit_age);

        // 의사 정보
        dFN = (EditText) findViewById(R.id.pmdfn);
        dMN = (EditText) findViewById(R.id.pmpfn);// personal mid name
        dLN = (EditText) findViewById(R.id.pmdln);
        dPhone_state = (EditText) findViewById(R.id.pmdstate);// 국가번호
        dPhone_area = (EditText) findViewById(R.id.pmdarea);// 지역번호
        dPhone_first = (EditText) findViewById(R.id.pmdfirst);// 앞번호
        dPhone_last = (EditText) findViewById(R.id.pmdlast);// 뒷번호
        dEmail_id = (EditText) findViewById(R.id.pmdemail);// 메일 아이디
        dEmail_host_name = (EditText) findViewById(R.id.pmdlstemail); // 메일 서버주소

        rd = (RadioGroup) findViewById(R.id.pmgender);
        male = (RadioButton) findViewById(R.id.pmmale);
        female = (RadioButton) findViewById(R.id.pmfemale);
        rd_position = (RadioGroup) findViewById(R.id.pmposition);
        upright = (RadioButton) findViewById(R.id.pmupright);
        supine = (RadioButton) findViewById(R.id.pmsupine);
        rd_exer = (RadioGroup) findViewById(R.id.pmexercise);
        yes = (RadioButton)findViewById(R.id.pmYes);
        no = (RadioButton)findViewById(R.id.pmNo);

        Time = (EditText) findViewById(R.id.pmsec);
        Mini_HR = (EditText) findViewById(R.id.hr_min);
        Mini_Para = (EditText) findViewById(R.id.pmpara_min);
        Mini_Sym = (EditText) findViewById(R.id.pmsym_min);
        Max_HR = (EditText) findViewById(R.id.hr_max);
        Max_Para = (EditText) findViewById(R.id.pmpara_max);
        Max_Sym = (EditText) findViewById(R.id.pmsym_max);

        rd.setOnCheckedChangeListener(this);
        rd_position.setOnCheckedChangeListener(this);
        rd_exer.setOnCheckedChangeListener(this);

        send_yes = (RadioButton) findViewById(R.id.radioBtn_yes);
        send_no = (RadioButton) findViewById(R.id.radioBtn_No);

        sendRd = (RadioGroup) findViewById(R.id.radiosend);
        sendRd.setOnCheckedChangeListener(this);

        openPM();

        btnSave = (Button) findViewById(R.id.pmbtn1);
        btnSave.setOnClickListener(this);

        btnCancel = (Button) findViewById(R.id.pmbtn2);
        btnCancel.setOnClickListener(this);
    }

    public void openPM() {
        sd = getSharedPreferences("pref", MODE_PRIVATE);
        ed = sd.edit();

        s.setPre(sd, ed);

        pDOB.setText(s.getPerBd());
        pLN.setText(s.getPerLn());
        pMN.setText(s.getPerMn());//
        pFN.setText(s.getPerFn());
        pPhone_state.setText(s.getPerPhone_state());//
        pPhone_area.setText(s.getPerPhone_area());//
        pPhone_first.setText(s.getPerPhone_first());//
        pPhone_last.setText(s.getPerPhone_last());//
        pEmail_id.setText(s.getPerEm_id());//
        pEmail_host_name.setText(s.getPerEm_host_name());//
        pAGE.setText(String.valueOf(s.get_PsAge()));
        pGender = s.get_Gender().toString();
        pPosition = s.get_Position().toString();
        pExer = s.get_Exercise().toString();

        dFN.setText(s.getDrFn());
        dMN.setText(s.getDrMn());
        dLN.setText(s.getDrLn());
        dPhone_state.setText(s.getDrPh_state());//
        dPhone_area.setText(s.getDrPh_area());//
        dPhone_first.setText(s.getDrPh_first());//
        dPhone_last.setText(s.getDrPh_last());//
        dEmail_id.setText(s.getDrEm_id());//
        dEmail_host_name.setText(s.getDrEm_host_name());//

        temp = (RadioButton) rd.getChildAt(s.getPerGender());
        temp.setChecked(true);
        Time.setText(s.getTime() + "");
        temp2 = (RadioButton) rd_position.getChildAt(s.getPersonPosition());
        temp2.setChecked(true);
        temp3 = (RadioButton) rd_exer.getChildAt(s.getPersonExercise());
        temp3.setChecked(true);

        Max_HR.setText(s.get_MaxHR() + "");
        Max_Para.setText(s.get_MaxPara() + "");
        Max_Sym.setText(s.get_MaxSym() + "");
        Mini_HR.setText(s.get_MiniHR() + "");
        Mini_Para.setText(s.get_MiniPara() + "");
        Mini_Sym.setText(s.get_MiniSym() + "");

        if (s.getSendRadio() == 1) {
            send_yes.setChecked(true);
            check_send = 1;
        } else {
            send_no.setChecked(true);
            check_send = 0;
        }
    }

    public void savePM() {
        sd = getSharedPreferences("pref", MODE_PRIVATE);
        ed = sd.edit();

        s.setPre(sd, ed);

        s.setDrEmail_id(dEmail_id.getText().toString());
        s.setDrEmail_host_name(dEmail_host_name.getText().toString());
        s.setDrFirstName(dFN.getText().toString());
        s.setDrMiddleName(dMN.getText().toString());
        s.setDrLastname(dLN.getText().toString());
        s.setDrPhone_state(dPhone_state.getText().toString());
        s.setDrPhone_area(dPhone_area.getText().toString());
        s.setDrPhone_first(dPhone_first.getText().toString());
        s.setDrPhone_last(dPhone_last.getText().toString());

        s.setTime(Integer.parseInt(Time.getText().toString()));

        s.setPersonBirthday(pDOB.getText().toString());
        s.setPersonEmail_id(pEmail_id.getText().toString());
        s.setDrEmail_host_name(pEmail_host_name.getText().toString());
        s.setPersonFirstName(pFN.getText().toString());
        s.setPersonMiddleName(pMN.getText().toString());
        s.setPersonLastName(pLN.getText().toString());
        s.setDrPhone_state(pPhone_state.getText().toString());
        s.setDrPhone_area(pPhone_area.getText().toString());
        s.setDrPhone_first(pPhone_first.getText().toString());
        s.setDrPhone_last(pPhone_last.getText().toString());
        s.setPersonGender(check);
        s.setPersonPosition(check_position);
        s.setPersonExercise(check_exer);

        s.set_PsAge(Integer.parseInt(pAGE.getText().toString()));
        s.set_Gender(pGender);
        s.set_Position(pPosition);
        s.set_Exercise(pExer);

        s.set_MaxHR(Integer.parseInt(Max_HR.getText().toString()));
        s.set_MaxPara(Integer.parseInt(Max_Para.getText().toString()));
        s.set_MaxSym(Integer.parseInt(Max_Sym.getText().toString()));
        s.set_MiniHR(Integer.parseInt(Mini_HR.getText().toString()));
        s.set_MiniPara(Integer.parseInt(Mini_Para.getText().toString()));
        s.set_MiniSym(Integer.parseInt(Mini_Sym.getText().toString()));
        s.setSendRadio(check_send);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.pmbtn1) {
            savePM();
            finish();
        } else if (id == R.id.pmbtn2) {
            finish();
        }
        // TODO Auto-generated method stub

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        int id = group.getId();
        if (id == R.id.pmgender) {
            pGender = null;
            int checkedRadioButtonId = group.getCheckedRadioButtonId();
            if (checkedRadioButtonId == R.id.pmmale) {
                check = 0;
                pGender = "M";
            } else if (checkedRadioButtonId == R.id.pmfemale) {
                check = 1;
                pGender = "F";
            }
        } else if (id == R.id.radiosend) {
            int checkedRadioButtonId = group.getCheckedRadioButtonId();
            if (checkedId == R.id.radioBtn_yes) {
                check_send = 1;
            } else if (checkedId == R.id.radioBtn_No) {
                check_send = 0;
            }
        } else 	if(id == R.id.pmposition){
            pPosition = null;
            int checkedRadioButtonId = group.getCheckedRadioButtonId();
            if(checkedRadioButtonId == R.id.pmupright){
                check_position = 0;
                pPosition = "Upright";
            }else if(checkedRadioButtonId == R.id.pmsupine){
                check_position = 1;
                pPosition = "Supine";
            }
        } else 	if(id == R.id.pmexercise){
            pExer = null;
            int checkedRadioButtonId = group.getCheckedRadioButtonId();
            if(checkedRadioButtonId == R.id.pmYes){
                check_exer = 0;
                pExer = "Yes";
            }else if(checkedRadioButtonId == R.id.pmNo){
                check_exer = 1;
                pExer = "No";
            }
        }

        Log.e("Gender", check+"");
        Log.e("Exer", check_exer+"");
        Log.e("position", check_position+"");

    }
}
