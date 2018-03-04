package com.example.a.myapplication;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Implementation of App Widget functionality.
 */
public class AppWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(final Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){
        RemoteViews updateviews=new RemoteViews(context.getPackageName(),R.layout.app_widget);
        Intent intent =new Intent(context,Goods_list.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(context,0,intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        updateviews.setOnClickPendingIntent(R.id.widget,pendingIntent);
        ComponentName componentName=new ComponentName(context,AppWidget.class);
        appWidgetManager.updateAppWidget(componentName,updateviews);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
    @Override
    public void onReceive(Context context,Intent intent){
        super.onReceive(context,intent);
        if(intent.getAction().equals("mystaticfilter")){
            Goods goods=(Goods)intent.getExtras().get("goods");
            RemoteViews updateviews=new RemoteViews(context.getPackageName(),R.layout.app_widget);
            updateviews.setTextViewText(R.id.widget_text,goods.getName()+"仅售"+goods.getPrice());
            updateviews.setImageViewResource(R.id.mwidget,goods.getImageid());

            Intent i=new Intent(context,GoodsInfo.class);
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            i.putExtras(intent.getExtras());
            PendingIntent pendingIntent=PendingIntent.getActivity(context,0,i,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            updateviews.setOnClickPendingIntent(R.id.widget,pendingIntent);
            ComponentName componentName=new ComponentName(context,AppWidget.class);
            AppWidgetManager appWidgetManager=AppWidgetManager.getInstance(context);
            appWidgetManager.updateAppWidget(componentName,updateviews);
        }
        else if(intent.getAction().equals("mydynamicfilter")){
            Goods goods=(Goods)intent.getExtras().get("goods");
            RemoteViews updateviews=new RemoteViews(context.getPackageName(),R.layout.app_widget);
            updateviews.setTextViewText(R.id.widget_text,goods.getName() + "已添加到购物车!");
            updateviews.setImageViewResource(R.id.mwidget,goods.getImageid());

            Intent i=new Intent(context,Goods_list.class);
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            i.putExtra("cut","cut");
            PendingIntent pendingIntent=PendingIntent.getActivity(context,0,i,PendingIntent.FLAG_UPDATE_CURRENT);
            updateviews.setOnClickPendingIntent(R.id.widget,pendingIntent);
            ComponentName componentName=new ComponentName(context,AppWidget.class);
            AppWidgetManager appWidgetManager=AppWidgetManager.getInstance(context);
            appWidgetManager.updateAppWidget(componentName,updateviews);
        }
    }
}

