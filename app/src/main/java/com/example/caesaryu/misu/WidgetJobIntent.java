package com.example.caesaryu.misu;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.util.Log;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class WidgetJobIntent extends JobIntentService {
    // 周期性更新 widget 的周期
    private static final int UPDATE_TIME = 10*60*1000;
    public static String strSeparator = "//";
    private final String ACTION_UPDATE_ALL = "update misu tool";
    private SQLiteDatabase myCourseDBHelper;
    private Timer mTimer;
    private TimerTask mTimerTask;

    static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, WidgetJobIntent.class, 123, work);

    }

    public static String[] convertStringToArray(String str) {
        String[] arr = str.split(strSeparator);
        return arr;
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.d("TEST", "enqueueWork: "+intent.getStringExtra("ACTION"));
            myCourseDBHelper = new MyCourseDBHelper(WidgetJobIntent.this, "m107_1").getWritableDatabase();
            // 每经过指定时间，发送一次广播

            mTimer = new Timer();
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    Intent updateIntent = new Intent(WidgetJobIntent.this, WidgetProvider.class);
                    updateIntent.putExtra("data", updateTool());
                    updateIntent.setAction(ACTION_UPDATE_ALL);
                    // PendingIntent pendingIntent=PendingIntent.getBroadcast(WidgetJobIntent.this,0,updateIntent,PendingIntent.FLAG_UPDATE_CURRENT);
                    sendBroadcast(updateIntent);
                    Log.d("TEST", "run: SEND");
                }
            };
            mTimer.schedule(mTimerTask, 1000, UPDATE_TIME);
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

        return out;
    }

}
