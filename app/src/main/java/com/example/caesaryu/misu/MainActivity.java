package com.example.caesaryu.misu;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayout;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    public static String strSeparator = "//";
    private GridLayout gl;
    private SQLiteDatabase myCourseDBHelper;
    private ArrayList<String> items = new ArrayList<>();
    private TextView temp;
    JobScheduler mJobScheduler;

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

    public static String[] convertStringToArray(String str) {
        String[] arr = str.split(strSeparator);
        return arr;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_temp);
        getSupportActionBar().hide();
        gl = findViewById(R.id.gl);

        myCourseDBHelper = new MyCourseDBHelper(MainActivity.this, "m107_1")
                .getWritableDatabase();
        refresh();
        widgetJob(MainActivity.this,false);
       // createNotificationChannel();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myCourseDBHelper.close();

    }

    public void courseInsert(String name, String teacher, int credit, String time, String
            classroom, String color) {
        try {
            myCourseDBHelper.execSQL("INSERT INTO m107_1(name, teacher, credit, time, classroom, color) VALUES (?,?,?,?,?,?)",
                    new Object[]{name, teacher, credit, time, classroom, color});
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "新增失敗", Toast.LENGTH_SHORT).show();
        }
    }

   /* @Override
   public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_top, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, setCourseActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.action_help) {
            try {
                courseInsert("werwer", "BABA", 5, "54//53", "九教306//877", "#77FFCC");
                courseInsert("ASD", "BABA", 5, "52//51", "寶貝520//754", "#FF7744");
                courseInsert("ASsD", "BABA", 5, "12//13", "我好20//87", "#DDFF77");
                courseInsert("ASDqe", "BABA", 5, "82", "超級20", "#7788CC");
                Toast.makeText(MainActivity.this, "成功", Toast.LENGTH_SHORT).show();
                refresh();
            } catch (Exception e) {
            }
        } else if (item.getItemId() == R.id.test) {
           widgetJob(MainActivity.this);

        }
        return super.onOptionsItemSelected(item);

    }*/

    @Override
    protected void onResume() {
        super.onResume();

        refresh();
    }

    public void refresh() {
        init();
        Cursor c;
        c = myCourseDBHelper.rawQuery("SELECT * FROM m107_1", null);
        c.moveToFirst();
        for (int x = 0; x < c.getCount(); x++) {
            String[] times = convertStringToArray(c.getString(4));
            String[] classrooms = convertStringToArray(c.getString(5));
            String name = c.getString(1);
            for (int q = 0; q < times.length; q++) {
                for (int i = 0; i < gl.getChildCount(); i++) {
                    String nowId = getId(gl.getChildAt(i));
                    temp = findViewById(gl.getChildAt(i).getId());
                    if (nowId != "no_id") {
                        // Log.d("TEST", times[q] + " " + nowId);
                        if (times[q].toCharArray()[0] == nowId.toCharArray()[0]
                                && times[q].toCharArray()[1] == nowId.toCharArray()[1]) {
                            // Log.d("TEST", "SAME");
                            temp.setText(name + "\n" + classrooms[q]);
                            temp.setBackgroundColor(Color.parseColor(c.getString(6)));
                        }
                    }
                }
            }
            c.moveToNext();
        }
        c.close();
    }

    public void init() {
        TextView tp;
        gl = findViewById(R.id.gl);
        for (int i = 0; i < gl.getChildCount(); i++) {
            try {
                tp = findViewById(gl.getChildAt(i).getId());
              /*  if (17 < i && i < 30) {
                    //tp.setBackgroundColor(Color.parseColor("#eba79d"));
                    tp.setBackgroundColor(Color.parseColor("#ededed"));
                } else if (29 < i && i < 42) {
                   // tp.setBackgroundColor(Color.parseColor("#f3ca9c"));
                    tp.setBackgroundColor(Color.parseColor("#ededed"));
                } else if (41 < i && i < 54) {
                    //tp.setBackgroundColor(Color.parseColor("#f1f581"));
                    tp.setBackgroundColor(Color.parseColor("#ededed"));
                } else if (53 < i && i < 66) {
                   // tp.setBackgroundColor(Color.parseColor("#FF96E3AE"));
                    tp.setBackgroundColor(Color.parseColor("#ededed"));
                } else if (65 < i && i < 78) {
                   // tp.setBackgroundColor(Color.parseColor("#75dafe"));
                    tp.setBackgroundColor(Color.parseColor("#ededed"));
                }*/
              if(17<i&&i<78){
                  if(i%2==0){
                      tp.setBackgroundColor(Color.parseColor("#fff6d4"));
                  }else if(i%2==1){
                      tp.setBackgroundColor(Color.parseColor("#ededed"));
                  }
              }
                tp.setText("");
            } catch (Exception e) {
            }
        }
    }

    public String updateTool() {

        String target = getDayForTool();
        String output = "";
        String same = "";
        Cursor c;
        c = myCourseDBHelper.rawQuery("SELECT * FROM m107_1", null);
        c.moveToFirst();
        for (int x = 0; x < c.getCount(); x++) {
            String[] times = convertStringToArray(c.getString(4));
            String[] classrooms = convertStringToArray(c.getString(5));
            String name = c.getString(1);
            for (int q = 0; q < times.length; q++) {

                if (target.toCharArray()[0] != "0".toCharArray()[0]) {

                    if (times[q].toCharArray()[0] != target.toCharArray()[0]
                            || times[q].toCharArray()[1] != target.toCharArray()[1]) {
                        output = "下一節沒課呢";
                    } else {
                        same = name + "\n" + classrooms[q];
                    }
                } else if (target.toCharArray()[0] == "0".toCharArray()[0]) {
                    output = "假奔時間";
                }

            }
            c.moveToNext();
        }
        if (same != "") {
            output = same;
        }
        c.close();
        return output;
    }

    public String getDayForTool() {
        Calendar calendar = Calendar.getInstance();
        int week = 0;//1~7
        int hour = calendar.get(Calendar.HOUR_OF_DAY);//0~23
        int min = calendar.get(Calendar.MINUTE);//
        String t = "";
        String out = "";
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:
                week = 1;
                break;
            case Calendar.TUESDAY:
                week = 2;
                break;
            case Calendar.WEDNESDAY:
                week = 3;
                break;
            case Calendar.THURSDAY:
                week = 4;
                break;
            case Calendar.FRIDAY:
                week = 5;
                break;
            case Calendar.SATURDAY:
                week = 6;
                break;
            case Calendar.SUNDAY:
                week = 7;
                break;
        }

        switch (hour) {

            case 8:
                if (min > 20) {
                    t = "2";
                } else {
                    t = "1";
                }
                break;
            case 9:
                if (min > 20) {
                    t = "3";
                } else {
                    t = "2";
                }
                break;
            case 10:
                if (min > 20) {
                    t = "4";
                } else {
                    t = "3";
                }
                break;
            case 11:
                if (min > 20) {
                    t = "0";
                } else {
                    t = "4";
                }
                break;
            case 12:
                if (min > 20) {
                    t = "5";
                } else {
                    t = "0";
                }
                break;
            case 13:
                if (min > 20) {
                    t = "6";
                } else {
                    t = "5";
                }
                break;
            case 14:
                if (min > 20) {
                    t = "7";
                } else {
                    t = "6";
                }
                break;
            case 15:
                if (min > 20) {
                    t = "8";
                } else {
                    t = "7";
                }
                break;
            case 16:
                if (min > 20) {
                    t = "9";
                } else {
                    t = "8";
                }
                break;
            case 17:
                if (min > 20) {
                    t = "A";
                } else {
                    t = "9";
                }
                break;
            case 18:
                if (min > 20) {
                    t = "B";
                } else {
                    t = "A";
                }
                break;
            case 19:
                if (min > 20) {
                    t = "C";
                } else {
                    t = "B";
                }
                break;
            case 20:
                t = "C";
                break;
            default:
                t = "1";
                break;
        }

        if (week == 5) {
            if (21 <= hour && hour <= 23) {
                week = 1;
            }
        } else if (week == 6 || week == 7) {
            t = "1";
            week = 1;
        } else {
            if (21 <= hour && hour <= 23) {
                week = week + 1;
            }
        }
        out = t + week;
        if (week == 0)
            Toast.makeText(MainActivity.this, "WRONG", Toast.LENGTH_SHORT).show();
        return out;
    }
    /*private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Example Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
*/
    public void menuClick(View v){
        if(v.getId()==R.id.btn_course){
            Intent intent = new Intent(MainActivity.this, setCourseActivity.class);
            startActivity(intent);
        }else if(v.getId()==R.id.btn_score){
            Intent intent = new Intent(MainActivity.this, scoreView.class);
            startActivity(intent);
        }
    }
    private void widgetJob(Context context, boolean once) {
        JobInfo.Builder builder = new JobInfo.Builder(0, new ComponentName(context, WidgetJobService.class));// 获取到我们自己的jobservice，同时启动该service
        if (once == false) {
            builder.setMinimumLatency(800);
            builder.setOverrideDeadline(1000);
        } else {
            builder.setMinimumLatency(800);
            builder.setOverrideDeadline(1000);
        }
        builder.setPersisted(true);
        PersistableBundle persistableBundle = new PersistableBundle();
        persistableBundle.putString("Action", "update");
        builder.setExtras(persistableBundle);
        mJobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        // 这里就将开始在service里边处理我们配置好的job
        mJobScheduler.schedule(builder.build());
    }
}









