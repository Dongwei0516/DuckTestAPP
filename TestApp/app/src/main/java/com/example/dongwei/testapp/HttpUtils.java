package com.example.dongwei.testapp;

import android.content.Context;
import android.content.SharedPreferences;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by dongwei on 2017/3/21.
 */

public class HttpUtils {
    OkHttpClient client = new OkHttpClient();
    public final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static String SESSION;
    public static String USERNAME;

    private Request.Builder createBuilder(String url) {
        Long unixtime = System.currentTimeMillis()/1000;

        return  new Request.Builder().url(url)
                .addHeader("Username",USERNAME)
                .addHeader("User-Session",SESSION)
                .addHeader("Timestamp", String.valueOf(unixtime))
                .addHeader("Encrypted", String.valueOf(unixtime));
    }

    private void asyncCallNetwork(final Request request, final NetCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    String result = response.body().string();
                    callback.onResult(result);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void login(String json, NetCallback callback)throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder().url("http://ducktest.cleargrass.com/api/login").post(body).build();
        asyncCallNetwork(request, callback);
    }

    public void modifyUser(String url, String json, NetCallback callback)throws IOException{
        RequestBody body  = RequestBody.create(JSON, json);
        Request request = createBuilder(url).post(body)
                .build();
        asyncCallNetwork(request, callback);
    }

    public void beginTest(String url,String username,String session, NetCallback callback)throws IOException{
        Long unixtime = System.currentTimeMillis()/1000;
        Request request = new Request.Builder().url(url)
                .addHeader("Username",username)
                .addHeader("User-Session",session)
                .addHeader("Timestamp", String.valueOf(unixtime))
                .addHeader("Encrypted", String.valueOf(unixtime)).get().build();
        asyncCallNetwork(request, callback);
    }

    public void  testResult(String url,String json,String username,String session, NetCallback callback)throws IOException{
        Long unixtime = System.currentTimeMillis()/1000;
        RequestBody body  = RequestBody.create(JSON, json);
        Request request =  new Request.Builder().url(url)
                .addHeader("Username",username)
                .addHeader("User-Session",session)
                .addHeader("Timestamp", String.valueOf(unixtime))
                .addHeader("Encrypted", String.valueOf(unixtime))
                .post(body)
                .build();

        asyncCallNetwork(request, callback);
    }

    public String userJson(String name,String pass){
        return "{\"username\":"+"\""+name+"\""+","+"\"password\":"+"\""+pass+"\""+"}";
    }

    public String modifyJson(String newname,String newpass){
        return "{\"new_name\":"+"\""+newname+"\""+","+"\"new_password\":"+"\""+newpass+"\""+"}";
    }

    public String testData(String token,String tokenVerify,String testVerify){
        return "{\"test_data\":{"+"\"token\":"+"\""+token+"\""+","+"\"token_verify\":"+"\""+tokenVerify+"\""+","+"\"test_verify\":"+"\""+testVerify+"\""+"}}";
    }

    public interface NetCallback {
        void onResult(String string);
    }
}