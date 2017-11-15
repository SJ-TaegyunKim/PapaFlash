package com.example.kimtaegyun.papaflash;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class Widget extends AppWidgetProvider {
    private static final String ACTION_WIDGET_RECEIVER = "ActionRecieverWidget";
    private static final String CLICK_ACTION = "com.example.kimtaegyun.papaflash.CLICK";
    private RemoteViews views;
    private ComponentName updateAppWidget;
    public static boolean widgetValue;
    //public android.hardware.Camera mCamera = null;
    //public Camera.Parameters mCameraParameter;



    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        views = new RemoteViews("com.example.kimtaegyun.papaflash", R.layout.widget);
        //views = new RemoteViews("com.example.doublei", R.layout.widget);


        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];
            Intent intent = new Intent(context, Widget.class);
            intent.setAction(ACTION_WIDGET_RECEIVER);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

            views.setOnClickPendingIntent(R.id.imageBtn, pendingIntent);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            boolean value = prefs.getBoolean("widgetOn", false);

            if (value) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("widgetOn", false);
                editor.commit();
            }
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int appWidgetId = intent.getIntExtra("appWidgetId", 0);

        if(intent.getAction().equals(ACTION_WIDGET_RECEIVER)) {
            // Log.e("WidgetPress","Press Widget"); // 위젯을 눌렀을 때 로그 확인

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            boolean value = prefs.getBoolean("widgetOn", true);
            Singleton.getInstance().setSwitchValue(value); // singleton 으로 value 변수 초기화
            Editor editor = prefs.edit(); // Preference Editor를 통해 변화된 값 저장

            if (value) { // value의 값을 확인하고 widgetValue 에 값 전달 (toggle)
                widgetValue = false;
            } else {
                widgetValue = true;
            }

            if (widgetValue) { // widgetValue 의 값이 true, 즉 현재 플래쉬가 꺼져있는 경우
                views = new RemoteViews(context.getPackageName(), R.layout.widget);

                //FlashON();

                views.setImageViewResource(R.id.imageBtn, R.drawable.on);
                // views 의 이미지 on으로 변경
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                appWidgetManager.updateAppWidget(appWidgetId, views);

                editor.putBoolean("widgetOn", true);
                editor.commit();


            } else { // widgetValue 의 값이 false, 즉 현재 플래쉬가 켜져있을 경우
                views = new RemoteViews(context.getPackageName(), R.layout.widget);

                //FlashOFF();

                views.setImageViewResource(R.id.imageBtn, R.drawable.off);
                // views 의 이미지 off로 변경
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                appWidgetManager.updateAppWidget(appWidgetId, views);

                editor.putBoolean("widgetOn", false);
                editor.commit();
            }

            Singleton.getInstance().setSwitchValue(widgetValue); // Singleton 변수 수정.
            updateAppWidget = new ComponentName( context, Widget.class );
            (AppWidgetManager.getInstance(context)).updateAppWidget( updateAppWidget, views );
        }
        else {
            super.onReceive(context, intent); // super.onReceive 를 통해 상위 클래스에 정보 전
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean value = prefs.getBoolean("widgetOn", false);

        if (value) {
            Editor editor = prefs.edit();
            editor.putBoolean("widgetOn", false);
            editor.commit();
        }
        super.onDeleted(context, appWidgetIds);
    }
    /*
    public void FlashON(){
        if(mCamera !=null){
            mCamera.release();
        }
        mCamera = android.hardware.Camera.open();
        mCameraParameter = mCamera.getParameters();
        mCameraParameter.setFlashMode("torch");
        mCamera.setParameters(mCameraParameter);
    }

    public void FlashOFF(){
        mCamera.release();
    }
    */
}
