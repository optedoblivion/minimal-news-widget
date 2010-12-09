package com.optedoblivion.MinimalNewsWidget.settings;

import com.optedoblivion.MinimalNewsWidget.R;
import com.optedoblivion.MinimalNewsWidget.R.id;
import com.optedoblivion.MinimalNewsWidget.R.layout;
import com.optedoblivion.MinimalNewsWidget.service.UpdaterService;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;


public class MinimalNewsWidgetConfigure extends Activity{
    static final String TAG = "MinimalNewsWidgetConfigure";
    private static final String PREFS_NAME
            = "com.optedoblivion.MinimalNewsWidget.MinimalNewsWidgetProvider";
    private static final String BG_PREFIX_KEY = "bg_";
    private static final String LINK_PREFIX_KEY = "link_";

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    CheckBox mUseBackground;
    EditText mRssLink;
    
    public MinimalNewsWidgetConfigure() {
        super();
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setResult(RESULT_CANCELED);
        setContentView(R.layout.minimal_news_configure);
        
        mUseBackground = (CheckBox)findViewById(R.id.useBackground);
        mRssLink = (EditText)findViewById(R.id.RSSLink);
        findViewById(R.id.SaveButton).setOnClickListener(mOnClickListener);
    
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, 
                                       AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If they gave us an intent without the widget id, just bail.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        //mAppWidgetPrefix.setText(loadTitlePref(MinimalNewsWidgetConfigure.this, mAppWidgetId));
    }
    
    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = MinimalNewsWidgetConfigure.this;

            String rssLink = mRssLink.getText().toString();
            boolean isChecked = mUseBackground.isChecked();
            String useBackground = (isChecked) ? "true" : "false";
            saveTitlePref(context, LINK_PREFIX_KEY, mAppWidgetId, rssLink);
            saveTitlePref(context, BG_PREFIX_KEY, mAppWidgetId, 
                                                               useBackground);
            
            
            // Push widget update to surface with newly set prefix
            AppWidgetManager appWidgetManager = AppWidgetManager
                                                        .getInstance(context);
//            MinimalNewsWidgetProvider.updateAppWidget(context, 
//                    appWidgetManager,
//                    mAppWidgetId, useBackground);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 
                                                                mAppWidgetId);

            Intent updaterServiceIntent = 
                                    new Intent(context, UpdaterService.class);
            updaterServiceIntent.setAction(
                         "com.optedoblivion.MinimalNewsWidget.START_SERVICE");
            startService(updaterServiceIntent);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    // Write the prefix to the SharedPreferences object for this widget
    static void saveTitlePref(Context context, String prefix, 
                                               int appWidgetId, String text) {
        SharedPreferences.Editor prefs = 
                           context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(prefix, text);
        prefs.commit();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
//    static String loadTitlePref(Context context, int appWidgetId) {
//        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
//        String prefix = prefs.getString(BG_PREFIX_KEY + appWidgetId, null);
//        if (prefix != null) {
//            return prefix;
//        } else {
//            return context.getString(R.string.appwidget_prefix_default);
//        }
//    }
//
//    static void deleteTitlePref(Context context, int appWidgetId) {
//    }
//
//    static void loadAllTitlePrefs(Context context, ArrayList<Integer> appWidgetIds,
//            ArrayList<String> texts) {
//    }

    
}
