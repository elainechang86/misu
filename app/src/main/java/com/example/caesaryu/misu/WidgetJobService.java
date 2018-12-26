package com.example.caesaryu.misu;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.PersistableBundle;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class WidgetJobService extends JobService {
    // 周期性更新 widget 的周期
    private static final int UPDATE_TIME = 10000;
    public static String strSeparator = "//";
    private final String ACTION_UPDATE_ALL = "update misu tool";
    private String str = "";
    private SQLiteDatabase myCourseDBHelper;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private boolean jobCanceled = false;

    public static String[] convertStringToArray(String str) {
        String[] arr = str.split(strSeparator);
        return arr;
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        // Log.d("TEST", "onStartJob: START JOB");
        PersistableBundle persistableBundle = params.getExtras();
        str = persistableBundle.getString("Action");
        //Log.d("TEST", "收到: " + str);

        if (str.equals("update")) {
        updateWork(params);
        }

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {

        // mTimer.cancel();
        // mTimerTask.cancel();
        jobCanceled = true;
        //Log.d("TEST", "onStartJob: STOP JOB");
        return true;
    }

    public String updateTool() {

        String target = getDayForTool();
        String output = "";
        String same = "";
        Cursor c;
        c = myCourseDBHelper.rawQuery("SELECT * FROM m107_1", null);
        c.moveToFirst();
        if (c.getCount() == 0) {
            output = "還沒新增課程呢";
        }
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
                        same = "下節：" + name + " 在" + classrooms[q];
                    }
                } else if (target.toCharArray()[0] == "0".toCharArray()[0]) {
                    output = "假奔時間～";
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


    private void updateWork(final JobParameters params) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                myCourseDBHelper = new MyCourseDBHelper(WidgetJobService.this, "m107_1").getWritableDatabase();
                // 每经过指定时间，发送一次广播
                //Log.d("TEST", "run: SEND");
                mTimer = new Timer();
                mTimerTask = new TimerTask() {
                    @Override
                    public void run() {
                        if (jobCanceled) {
                            return;
                        }
                        Intent updateIntent = new Intent(WidgetJobService.this, WidgetProvider.class);
                        updateIntent.putExtra("data", updateTool());
                        updateIntent.putExtra("action", "update");
                        sendBroadcast(updateIntent);
                    }
                };

                mTimer.schedule(mTimerTask, 1000, UPDATE_TIME);
                jobFinished(params, true);
            }
        }).start();
    }

}
