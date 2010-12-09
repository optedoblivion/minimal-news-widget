package com.optedoblivion.MinimalNewsWidget.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.optedoblivion.MinimalNewsWidget.data.DBAdapter;

public class UpdaterService extends Service{
    private static final String TAG = "UpdaterService";
    private DBAdapter dbAdapter;
    private SharedPreferences prefs;
    private Context mContext;
    private Thread uThread;
    private static final String PREFS_NAME
            = "com.optedoblivion.MinimalNewsWidget.MinimalNewsWidgetProvider";
    private static final String BG_PREFIX_KEY = "bg_";
    private static final String LINK_PREFIX_KEY = "link_";
    private static final String COMPLETED_PREFIX_KEY = "completed_";
    private Set<String> extras;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public void onStart(Intent intent, int startId){
        super.onStart(intent, startId);
        int tmpAppWidgetId = 0;
        if (intent != null){
             tmpAppWidgetId = intent.getFlags();
        }
        Log.i(TAG, "" + tmpAppWidgetId);
        final int appWidgetId = tmpAppWidgetId;
        uThread = new Thread(){
            public void run(){
                String RSSLink = null;
                String XML = null;
                String inputLine = null;
                syncPrefs();
                XML = "";
                RSSLink = prefs.getString(LINK_PREFIX_KEY + appWidgetId, null);
                if (RSSLink == null){
                    return;
                }
                try{
                    URL u = new URL(RSSLink);
                    URLConnection uc = u.openConnection();
                    BufferedReader in = new BufferedReader(
                              new InputStreamReader(uc.getInputStream()));
                    while((inputLine = in.readLine()) != null){
                        XML += inputLine;
                    }
                    in.close();
                    insertXML(XML, appWidgetId);
                }catch(Exception e){
                    Log.e(TAG, "uThread Work: " + e.toString());
                }
            }
        };
        uThread.start();
    }

    public void onCreate(){
        super.onCreate();
        dbAdapter = new DBAdapter(this);
        mContext = this;
    }

    public void insertXML(String XML, int appWidgetId) 
                                             throws SAXException, IOException{
        try {
            DocumentBuilderFactory docBuilderFactory = 
                                         DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = 
                                       docBuilderFactory.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(XML));
            Document document = docBuilder.parse(is);
            NodeList nodes = document.getElementsByTagName("entry");
            int i;
            String title = null;
            String link = null;
            String updated = null;
            dbAdapter.clearFeeds(appWidgetId);
            for (i=0;i<nodes.getLength();i++){
                NodeList nl = (NodeList) nodes.item(i).getChildNodes();
                title = getValue((Element) nl.item(3));
                link = nl.item(7).getTextContent().toString();
                updated = nl.item(9).getTextContent().toString();
                updated = updated.substring(0, 10) + " " + 
                                    updated.substring(11, updated.length()-1);
                ContentValues values = new ContentValues();
                values.put("title", title);
                values.put("link", link);
                values.put("updated", updated);
                values.put("appWidgetId", appWidgetId);
                dbAdapter.insertFeeds(values);
            }
        } catch (ParserConfigurationException e) {
            Log.e(TAG, "Inserting XML: " + e.toString());
        }
        
    }
    
    public String getValue(Element e) {
        Node child = e.getFirstChild();
        if (child instanceof CharacterData) {
            CharacterData cd = (CharacterData) child;
            return cd.getData();
        }
        return null;
    }
    public void syncPrefs(){
        prefs = this.getSharedPreferences(PREFS_NAME, 0);
    }
}
