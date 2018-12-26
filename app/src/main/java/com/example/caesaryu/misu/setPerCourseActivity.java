package com.example.caesaryu.misu;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;

import android.widget.EditText;

import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

public class setPerCourseActivity extends AppCompatActivity implements TextWatcher {
    EditText etName, etTeacher, etCredit;
    Button btn_timeset, btn_color, btn_OK;
    int colorNow = Color.WHITE;
    private ColorPickerDialog dialog;
    private SQLiteDatabase myCourseDBHelper;
    private SQLiteDatabase myScoreDBHelper;

    String name = null, teacher = null, time = null, classroom = null, color = null;
    int credit = 0;
    String[] times;
    String[] classrooms;
    CheckBox cb;
    ArrayList<String> checked = new ArrayList<>();
    ArrayList<String> days = new ArrayList<>();
    int[] week = {0, 0, 0, 0, 0};
    EditText et1, et2;
    TextView tv2;
    int[] t = {0, 0};
    AlertDialog dialog1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_per_course);
        getSupportActionBar().hide();
        myCourseDBHelper = new MyCourseDBHelper(setPerCourseActivity.this, "m107_1").getWritableDatabase();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        etCredit = findViewById(R.id.etCredit);
        etName = findViewById(R.id.etName);
        etTeacher = findViewById(R.id.etTeacher);
        btn_color = findViewById(R.id.btn_color);
        btn_OK = findViewById(R.id.btn_OK);
        btn_timeset = findViewById(R.id.btn_timeSet);


        btn_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setColor();
                dialog.setmListener(new ColorPickerDialog.OnColorChangedListener() {
                    @Override
                    public void colorChanged(int color123) {
                        colorNow=color123;
                        color=String.format("#%06X", (0xFFFFFF & colorNow));
                    }
                });
            }

        });

        btn_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(setPerCourseActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                getValues();
                try{
                time=convertArrayToString(times);
                classroom=convertArrayToString(classrooms);
                if(color==null)
                    color="#ededed";
                if(classroom!=null&&time!=null){
                courseInsert(name, teacher, credit, time, classroom, color);}
                Cursor c=myCourseDBHelper.rawQuery("SELECT * FROM m107_1 WHERE name LIKE '"+name+"'",null);
                    c.moveToFirst();
                    int __id=c.getInt(0);
                    String scoreTable="__"+__id;
                    try {
                        myScoreDBHelper = new MyDBHelper(setPerCourseActivity.this, scoreTable).getWritableDatabase();
                    }catch (Exception e){
                        Toast.makeText(setPerCourseActivity.this,"新增配分資料表失敗",Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Toast.makeText(setPerCourseActivity.this,"新增失敗",Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });

        btn_timeset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(setPerCourseActivity.this);
                View view = getLayoutInflater().inflate(R.layout.activity_choose_time_dialog, null);
                tv2 = view.findViewById(R.id.tvClass2);
                et2 = view.findViewById(R.id.etClass2);
                et1 = view.findViewById(R.id.editClassroom);
                final TableLayout table = (TableLayout) view.findViewById(R.id.table);
                TableRow tr;
                checked.clear();
                for (int x = 1; x < 13; x++) {
                    try {
                        tr = (TableRow) table.getChildAt(x);
                        for (int y = 1; y < tr.getChildCount(); y++) {
                            cb = (CheckBox) findViewById(tr.getChildAt(y).getId());
                        }
                    } catch (Exception e) {
                    }
                }

                builder.setView(view);
                dialog1 = builder.create();
                dialog1.setTitle("選擇時間及輸入教室");
                dialog1.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                dialog1.show();
            }
        });
    }


    private String setColor() {
        dialog = new ColorPickerDialog(setPerCourseActivity.this, colorNow,
                "ChangeColor",
                new ColorPickerDialog.OnColorChangedListener() {
                    @Override
                    public void colorChanged(int color1) {
                        colorNow = color1;
                    }
                });
        dialog.show();
        String hexColor = String.format("#%06X", (0xFFFFFF & colorNow));
        return hexColor;
    }

    public void courseInsert(String name, String teacher, int credit, String time, String
            classroom, String color) {
        try {
            myCourseDBHelper.execSQL("INSERT INTO m107_1(name, teacher, credit, time, classroom, color) VALUES (?,?,?,?,?,?)",
                    new Object[]{name, teacher, credit, time, classroom, color});
        } catch (Exception e) {
            Toast.makeText(setPerCourseActivity.this, "新增失敗", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        getValues();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myCourseDBHelper.close();

    }

    public void getValues() {
        try {
            name = etName.getText().toString();
            teacher = etTeacher.getText().toString();
            credit = Integer.parseInt(etCredit.getText().toString());
        } catch (Exception e) {
            Toast.makeText(setPerCourseActivity.this, "請不要留空", Toast.LENGTH_SHORT).show();
        }
    }

    public static String getId(View view) {
        String str;
        try {
            str = view.getResources().getResourceName(view.getId());
            StringBuffer sb = new StringBuffer(str);
            return sb.substring(sb.length() - 2, sb.length());
        } catch (Resources.NotFoundException e) {
            return "no_id";
        }
    }

    public void checkCB(View v) {

        String strId = getId(v);
        cb = (AppCompatCheckBox) v;

        if (cb.isChecked()) {
            checked.add(strId);
            mySort(checked);
            getCount(checked);
            /*
            for (int i = 0; i < checked.size(); i++) {
                Log.d("TEST", "MYSORT: " + checked.get(i));
            }
            for (int i = 0; i < 5; i++) {
                Log.d("TEST", i + 1 + " count: " + week[i]);
            }
            */
            if (countDays() > 1) {
                setVisible();
            } else {
                setInvisible();
            }
            setHintDay();

        } else {
            checked.remove(strId);
            mySort(checked);
            getCount(checked);
            //countDays();
            if (countDays() > 1) {
                setVisible();
            } else {
                setInvisible();
            }
            setHintDay();

        }
    }

    public void btnYesClick(View v) {

        if (v.getId() == R.id.buttonYes) {
            if (checked.size() != 0) {
                times = new String[checked.size()];
                classrooms = new String[checked.size()];
                mySort(checked);
                for (int x = 0; x < checked.size(); x++) {
                    times[x] = checked.get(x);
                }
                try {
                    for (int x = 0; x < week[t[0] - 1]; x++) {
                        classrooms[x] = et1.getText().toString();
                    }
                    for (int x = week[t[0] - 1]; x < checked.size(); x++) {
                        classrooms[x] = et2.getText().toString();
                        ;
                    }
                } catch (Exception e) {
                    Toast.makeText(setPerCourseActivity.this, "請不要留空", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(setPerCourseActivity.this, "請選擇時間", Toast.LENGTH_SHORT).show();
            }
            dialog1.dismiss();
          /*  for (int x = 0; x < times.length; x++) {
                Log.d("TEST", times[x] + "  " + classrooms[x]);
            }*/
        }
    }


    public int countDays() {
        days.clear();
        Collections.sort(checked);
        for (int x = 0; x < checked.size(); x++) {
            for (int i = 1; i <= 5; i++) {
                if (String.valueOf(i).toCharArray()[0] == checked.get(x).toCharArray()[1]) {
                    if (days.contains(String.valueOf(i)) == false) {
                        days.add(String.valueOf(i));
                        Collections.sort(days);
                    }
                }
            }
        }
        return days.size();
    }

    public void setInvisible() {
        tv2.setVisibility(View.INVISIBLE);
        et2.setVisibility(View.INVISIBLE);
    }

    public void setVisible() {
        tv2.setVisibility(View.VISIBLE);
        et2.setVisibility(View.VISIBLE);
    }

    public void mySort(ArrayList<String> list) {
        String temp;
        for (int i = list.size() - 1; i > 0; --i) {
            for (int j = 0; j < i; ++j) {
                if (Integer.parseInt("" + list.get(j).toCharArray()[1]) > Integer.parseInt("" + list.get(j + 1).toCharArray()[1])) {
                    temp = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, temp);
                }
            }


        }
    }

    private void getCount(ArrayList<String> list) {
        for (int i = 0; i < 5; i++) {
            week[i] = 0;
        }
        for (int i = 0; i < list.size(); i++) {
            week[Integer.parseInt(list.get(i).toCharArray()[1] + "") - 1]++;
        }
    }

    private void setHintDay() {
        t[0] = 0;
        t[1] = 0;
        int temp;
        if (countDays() == 0) {
            et1.setHint("請先選時間");
        }
        if (countDays() == 1) {
            et2.setHint("輸入教室");

            for (int x = 0; x < 5; x++) {
                if (week[x] != 0) {
                    t[0] = x + 1;
                    et1.setHint("星期" + t[0] + " 的教室");
                }
            }

        }

        if (countDays() > 1) {
            for (int x = 0; x < week.length; x++) {
                if (week[x] != 0) {
                    t[1] = x + 1;
                    temp = x;
                    if (t[1] != 0) {
                        et2.setHint("星期" + t[1] + " 的教室");
                    }
                    for (int y = 0; y < temp; y++) {
                        if (week[y] != 0) {
                            t[0] = y + 1;
                        }
                        if (t[0] != 0) {
                            et1.setHint("星期" + t[0] + " 的教室");

                        }
                    }
                }
            }

        }


    }
    public static String strSeparator = "//";
    public static String convertArrayToString(String[] array) {
        String str = "";
        for (int i = 0; i < array.length; i++) {
            str = str + array[i];
            // Do not append comma at the end of last element
            if (i < array.length - 1) {
                str = str + strSeparator;
            }
        }
        return str;
    }

}

