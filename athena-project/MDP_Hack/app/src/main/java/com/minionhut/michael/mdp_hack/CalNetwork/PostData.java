package com.minionhut.michael.mdp_hack.CalNetwork;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

/**
 * Created by Kyle on 10/23/2014.
 */
public class PostData {

    public static class PostDataObj {
        private String url;
        private String data;

        public PostDataObj(String url, String data) {
            this.url = url;
            this.data = data;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }

    public static class PostDataTask extends AsyncTask<PostDataObj, Void, HttpResponse> {

        @Override
        protected HttpResponse doInBackground(PostDataObj... postDataObjs) {
            HttpPost httppost = new HttpPost(postDataObjs[0].getUrl());
            try {
                httppost.setEntity(new StringEntity(postDataObjs[0].getData()));
                httppost.setHeader("Accept", "application/json");
                httppost.setHeader("Content-type", "application/json");
                // Execute HTTP Post Request
                return new DefaultHttpClient().execute(httppost);
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    /*public static HttpResponse PostData(String URL, String Data) {
        HttpPost httppost = new HttpPost(URL);
        try {
            httppost.setEntity(new StringEntity(Data));
            httppost.setHeader("Accept", "application/json");
            httppost.setHeader("Content-type", "application/json");
            // Execute HTTP Post Request
            return new DefaultHttpClient().execute(httppost);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
        */
}
