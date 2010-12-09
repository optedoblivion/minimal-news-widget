package com.optedoblivion.MinimalNewsWidget.widget;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.optedoblivion.MinimalNewsWidget.R;
import com.optedoblivion.MinimalNewsWidget.R.layout;
import com.optedoblivion.MinimalNewsWidget.data.DBAdapter;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.TextView;

public class MinimalNewsWidgetProvider extends AppWidgetProvider {
    
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, 
                                                          int[] appWidgetIds){

            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new MyTime(context, appWidgetManager, appWidgetIds), 
                                                                     1, 15000);
            super.onUpdate(context, appWidgetManager, appWidgetIds);
        }
    }
    
    
    class MyTime extends TimerTask {
        private static final String TAG = "MNWProvider";
        RemoteViews remoteViews;
        AppWidgetManager appWidgetManager;
        ComponentName thisWidget;
        Context mContext;
        int[] appWidgetIds;

        TextView tView = null;
        DBAdapter dbAdapter = null;
        // Use this to emulate caching, so we don't hit the db every time we 
        // update.
        ArrayList<ArrayList<String>> feeds = new ArrayList<ArrayList<String>>();
        int currentFeed = 0;
        int maxFeeds = 0;

        public MyTime(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
            this.appWidgetManager = appWidgetManager;
            this.appWidgetIds = appWidgetIds;
            mContext = context;
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.minimal_news_widget);
            thisWidget = new ComponentName(context, MinimalNewsWidgetProvider.class);
            currentFeed = 0;
            maxFeeds = 0;
        }
        
        @Override
        public void run() {
            int N = appWidgetIds.length;
            ArrayList<String> tmp;
            for (int i=0; i<N; i++){
                int appWidgetId = appWidgetIds[i];
//                // Create an Intent to launch ExampleActivity
//                Intent intent = new Intent(context, ExampleActivity.class);
//                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
//                // Get the layout for the App Widget and attach an on-click listener to the button

                if (currentFeed >= maxFeeds){ 
                    dbAdapter = new DBAdapter(mContext);
                    Cursor items = dbAdapter.getFeeds();
                    if (items!=null){
                        int itemCount = items.getCount();
                        int c;
                        for(c=0;c<itemCount;c++){
                            items.moveToPosition(c);
                            String title = items.getString(
                                                       items.getColumnIndex("title"));
                            String link = items.getString(
                                                       items.getColumnIndex("link"));
                            tmp = new ArrayList<String>();
                            tmp.add(title);
                            tmp.add(link);
                            feeds.add(tmp);
                        }
                        maxFeeds = itemCount;
                        currentFeed = 0;
                    }
                    items.close();
                    dbAdapter.closeDb();
                }
                RemoteViews views = new RemoteViews(mContext.getPackageName(), 
                        R.layout.minimal_news_widget);
                if (!feeds.isEmpty()){
                    String displayTitle = feeds.get(i).get(0);
                    String displayLink = feeds.get(i).get(1);
                    feeds.remove(i);
                    maxFeeds -= 1;
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    PendingIntent pendingIntent = PendingIntent.getActivity(
                                                          mContext, 0, intent, 0);
                    //views.setOnClickPendingIntent(views.getLayoutId(), pendingIntent);
                    views.setTextViewText(R.id.TextView01, displayTitle);
                }
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
    
}