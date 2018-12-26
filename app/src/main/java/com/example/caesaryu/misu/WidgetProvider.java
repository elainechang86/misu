package com.example.caesaryu.misu;


import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class WidgetProvider extends AppWidgetProvider {

    // 保存 widget 的id的HashSet，每新建一个 widget 都会为该 widget 分配一个 id。
    private static Set idsSet = new HashSet();
    private static String toolShow = "";
    // 更新 widget 的广播对应的action
    private static Context mcontext;
    private final String ACTION_UPDATE_ALL = "update misu tool";
    JobScheduler mJobScheduler;

    /**
     * 接收窗口小部件点击时发送的广播
     */
    @Override
    public void onReceive(final Context context, Intent intent) {
        super.onReceive(context, intent);

        //Log.d("TEST", "onReceive123: " + intent.getStringExtra("action"));
        if(intent.getStringExtra("action")!=null&&intent.getStringExtra("action").equals("click")){
            Toast.makeText(context, "重新整理", Toast.LENGTH_SHORT);
            Log.d("TEST", "onReceive123: " + "YAYA");
            mJobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            mJobScheduler.cancelAll();
            widgetJob(context,true);
        }
        final String str = intent.getStringExtra("data");
        if (intent.getStringExtra("action") != null) {
            if (intent.getStringExtra("action").equals("update")) {
                Log.d("TEST", "onReceive: " + str);
                toolShow = intent.getStringExtra("data");
                updateAllAppWidgets(context, AppWidgetManager.getInstance(context), idsSet);
            }
        }

        /*
        Log.d("TEST", "onReceive: " + intent.getCategories());
        if(intent.getCategories().toString().equals(Intent.CATEGORY_ALTERNATIVE)) {

           widgetJob(context,true);
            Log.d("TEST", "onReceive: " + "CLICK");
        }
*/
    }

    // 更新所有的 widget
    private void updateAllAppWidgets(Context context, AppWidgetManager appWidgetManager, Set set) {
        // widget 的id
        int appID;
        // 迭代器，用于遍历所有保存的widget的id
        Iterator it = set.iterator();

        // 要显示的那个数字，每更新一次 + 1
        // TODO:可以在这里做更多的逻辑操作，比如：数据处理、网络请求等。然后去显示数据


        while (it.hasNext()) {
            appID = ((Integer) it.next()).intValue();

            // 获取 example_appwidget.xml 对应的RemoteViews
            RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.app_widget);
            // 设置显示数字
            remoteView.setTextViewText(R.id.widget_txt, toolShow);
            // 设置点击按钮对应的PendingIntent：即点击按钮时，发送广播。
            remoteView.setOnClickPendingIntent(R.id.widget_btn_reset, getResetPendingIntent(context));
            // 更新 widget
            appWidgetManager.updateAppWidget(appID, remoteView);
        }
    }

    /**
     * 获取 重置数字的广播
     */
    private PendingIntent getResetPendingIntent(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, WidgetProvider.class);
        intent.putExtra("action", "click");

        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        return pi;
    }

    /**
     * 获取 打开 MainActivity 的 PendingIntent
     */
   /* private PendingIntent getOpenPendingIntent(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, MainActivity.class);
        intent.putExtra("main", "这句话是我从桌面点开传过去的。");
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
        return pi;
    }*/

    /**
     * 当该窗口小部件第一次添加到桌面时调用该方法，可添加多次但只第一次调用
     */
    @Override
    public void onEnabled(Context context) {
        // 在第一个 widget 被创建时，开启服务
      /*  Intent intent = new Intent(context, WidgetJobIntent.class);
       // context.startService(intent);
        intent.putExtra("ACTION","UPDATE");
       WidgetJobIntent.enqueueWork(context,WidgetJobIntent.class,123,intent);*/
        Toast.makeText(context, "小工具創建", Toast.LENGTH_SHORT).show();
        widgetJob(context, false);
        super.onEnabled(context);
    }

    // 当 widget 被初次添加 或者 当 widget 的大小被改变时，被调用
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle
            newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        widgetJob(context, false);
    }

    /**
     * 当小部件从备份恢复时调用该方法
     */

    @Override
    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
        super.onRestored(context, oldWidgetIds, newWidgetIds);
    }

    /**
     * 每次窗口小部件被点击更新都调用一次该方法
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        // 每次 widget 被创建时，对应的将widget的id添加到set中
        for (int appWidgetId : appWidgetIds) {
            idsSet.add(Integer.valueOf(appWidgetId));
        }
    }

    /**
     * 每删除一次窗口小部件就调用一次
     */
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // 当 widget 被删除时，对应的删除set中保存的widget的id
        for (int appWidgetId : appWidgetIds) {
            idsSet.remove(Integer.valueOf(appWidgetId));
        }
        super.onDeleted(context, appWidgetIds);
    }

    /**
     * 当最后一个该窗口小部件删除时调用该方法，注意是最后一个
     */
    @Override
    public void onDisabled(Context context) {
        // 在最后一个 widget 被删除时，终止服务
        Intent intent = new Intent(context, WidgetJobIntent.class);
        intent.putExtra("ACTION", "STOP");
        context.stopService(intent);
        //mJobScheduler.cancel(0);
        // JobIntentService.enqueueWork(context,WidgetJobIntent.class,123,intent);
        super.onDisabled(context);
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
