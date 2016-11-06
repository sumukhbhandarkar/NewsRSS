package com.sumukh_dev.news;

import android.app.ListActivity;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.ListViewCompat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ListActivity {

    List headlines;
    List links;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AsyncCall task = new AsyncCall();
        task.execute();
    }

    private class AsyncCall extends AsyncTask<Void, Void, Void> {
        private static final String TAG = "FeedTag";

        @Override
        protected void onPreExecute() {
            Log.i(TAG,"onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i(TAG, "doInBackground");
            getFeeds();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i(TAG, "onPostExecute");
            //Binding Data
            ArrayAdapter adapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, headlines);
            setListAdapter(adapter);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            Log.i(TAG, "onProgressUpdated");
        }
    }

    public void getFeeds() {
        headlines = new ArrayList();
        links = new ArrayList();
        try {
            URL url = new URL("http://feeds.feed-burner.com/digit/latest-from-digit");
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(getInputStream(url), "UTF_8");
            boolean insideItem = false;
            int eventType = xpp.getEventType();
            while(eventType != XmlPullParser.END_DOCUMENT) {
                if(eventType == XmlPullParser.START_TAG) {
                    if(xpp.getName().equalsIgnoreCase("item")) {
                        insideItem = true;
                    }
                    else if(xpp.getName().equalsIgnoreCase("title")) {
                        if(insideItem)
                            headlines.add(xpp.nextText());
                    }
                    else if(xpp.getName().equalsIgnoreCase("link")) {
                        if(insideItem)
                            links.add(xpp.nextText());
                    }
                }
                else if(eventType==XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")) {
                    insideItem = false;
                }
                eventType = xpp.next();
            }
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch(XmlPullParserException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public InputStream getInputStream(URL url) {
        try {
            return url.openConnection().getInputStream();
        }
        catch (IOException e) {
            return null;
        }
    }

    protected void onListItemClick(ListView l, View v, int position, long id) {
        Uri uri = Uri.parse((String) links.get(position));
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}