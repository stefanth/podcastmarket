package com.olafurtorfi.www.podcastmarket.sync;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.olafurtorfi.www.podcastmarket.data.EpisodeContract;
import com.olafurtorfi.www.podcastmarket.data.PodcastContract;
import com.olafurtorfi.www.podcastmarket.utilities.NetworkUtil;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import java.net.URL;
import java.util.Date;
import java.util.List;

/**
 * Created by olitorfi on 15/02/2017.
 */

public class PodcastSyncTask {
    private static String ifNullMakeEmptyString(String input){
        if(input != null)
            return input;
        return "";
    }
    private static String TAG = "PodcastSynTask";
    synchronized public static void syncPodcast(Context context, String urlString) {

        try {
            /*
             * The getUrl method will return the URL that we need to get the podcast JSON for the
             * podcast. It will decide whether to create a URL based off of the latitude and
             * longitude or off of a simple location as a String.
             */
            URL podcastRequestUrl = new URL(urlString);
            Log.v(TAG, "syncPodcast: " + urlString);

            /* Use the URL to retrieve the JSON */
            String podcastResponse = NetworkUtil.getResponseFromHttpUrl(podcastRequestUrl);

            /* Parse the JSON into a list of podcast values */
            Log.v(TAG, "syncPodcast: response from url " + podcastResponse);


            //you can still change the format of the reader, but it's not recommended.
            SyndFeedInput feedIn = new SyndFeedInput();

            XmlReader xmlReader = new XmlReader(podcastRequestUrl);

            SyndFeed feed = feedIn.build(xmlReader);

            Log.v(TAG, "syncPodcast: " + feed);

            String author = ifNullMakeEmptyString(feed.getAuthor());
            String title = ifNullMakeEmptyString(feed.getTitle());
            String description = ifNullMakeEmptyString(feed.getDescription());
            Log.d(TAG, "syncPodcast: title" + title + ", author: " + author + ", description: " + description);
            ContentValues cv = new ContentValues();
            cv.put("title", title);
            cv.put("description", description);
            cv.put("author", author);
            Uri uri = context.getContentResolver().insert(PodcastContract.PodcastEntry.CONTENT_URI, cv);
            String podcastId = uri.getLastPathSegment();
            Log.d(TAG, "syncPodcast: last path segment, aka podcast id" + podcastId);

            List<SyndEntry> entries = feed.getEntries();
            String eTitle = "";
            String ePodcast = ifNullMakeEmptyString(podcastId);
            String eDescription = "";
            String eUri = "";
            Date eDate = null;


            for (SyndEntry entry : entries){
                eTitle = ifNullMakeEmptyString(entry.getTitle());
                eDescription = ifNullMakeEmptyString(entry.getDescription().getValue());
                eUri = ifNullMakeEmptyString(entry.getUri());
                eDate = entry.getPublishedDate();
                ContentValues eCv = new ContentValues();
                eCv.put("title", eTitle);
                eCv.put("description", eDescription);
                eCv.put("podcast", ePodcast);
                eCv.put("url", eUri);
                eCv.put("date", eDate.getTime());
                context.getContentResolver().insert(EpisodeContract.EpisodeEntry.CONTENT_URI, eCv);
            }
            Log.d(TAG, "syncPodcast: eTitle:"+eTitle+", ePodcast:"+ePodcast+", eDescription:"+eDescription+", eUri:"+eUri + ", eDate: "+eDate);

        } catch (Exception e) {
            /* Server probably invalid */
            e.printStackTrace();
        }
    }
}
