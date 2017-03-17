package com.example.dongwei.testapp;

import android.app.Activity;
import android.bluetooth.BluetoothGatt;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by dongwei on 2017/3/10.
 */

public class LoginActivity extends Activity {

    private BluetoothGatt bluetoothGatt;
    private EditText nameEt, passwordEt;
    private Button btnLogin, btnModify;
    private String url = "http://ducktest.cleargrass.com/api/login";
    public static String SESSION = null;
    public static String USERNAME = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        nameEt = (EditText)findViewById(R.id.nameEt);
        passwordEt = (EditText)findViewById(R.id.passwordEt);
        btnLogin = (Button)findViewById(R.id.button11);
        btnModify = (Button)findViewById(R.id.button15);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
//                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
//                finish();
//                startActivity(intent);
            }
        });

        btnModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userModify();
            }
        });
    }

    private void login(){
        final String name = nameEt.getText().toString();
        final String pass = passwordEt.getText().toString();

        if (TextUtils.isEmpty(name)||TextUtils.isEmpty(pass)){
            Toast.makeText(getApplicationContext(),"不能为空",Toast.LENGTH_SHORT).show();

            return;
        }

        new Thread(){
                @Override
                public void run(){

                String md5Pass = getMd5(pass);
                Log.d("passMd5",md5Pass);

                HttpUtils httpUtils = new HttpUtils();
                String user = httpUtils.userJson(name,md5Pass);
                Log.d("user",user);
                    USERNAME = name;
                final Request request = new Request.Builder().url(url).build();
                try {
                    final String result = httpUtils.login(url,user);
                    Log.d("result",result);
                    String session = result.substring(result.indexOf("session")+10,result.indexOf("\"}"));
                    SESSION = session;
                    Log.d("session",session);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String code = result.substring(result.indexOf(":")+1,result.indexOf(","));
                            if ("0".equals(code)){
                                Log.d("1111","登录成功");
                                Toast.makeText(getApplicationContext(),"登录成功",Toast.LENGTH_SHORT).show();
                                Log.d("aaa",USERNAME);
                                Log.d("bbb",SESSION);
                                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                finish();
                                startActivity(intent);
                            }else {
                                Log.d("1111","登录失败");
                                Toast.makeText(getApplicationContext(),"登录失败"+"\n"+result,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }catch (IOException e){
                    e.printStackTrace();
                }

            }
        }.start();

    }

    public class HttpUtils{
        OkHttpClient client = new OkHttpClient();
        public final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        public String login(String url,String json)throws IOException{
            RequestBody body = RequestBody.create(JSON, json);

            Request request = new Request.Builder().url(url).post(body).build();

            Response response = client.newCall(request).execute();
            String result = response.body().string();

            return result;
        }


        public String modifyUser(String url, String json)throws IOException{
            Long unixtime = System.currentTimeMillis()/1000;
            RequestBody body  = RequestBody.create(JSON, json);
            Request request = new Request.Builder().url(url).post(body)
                    .addHeader("Username",USERNAME)
                    .addHeader("User-Session",SESSION)
                    .addHeader("Timestamp", String.valueOf(unixtime))
                    .addHeader("Encrypted", String.valueOf(unixtime))
                    .build();
            Response response = client.newCall(request).execute();
            String result = response.body().string();

            return result;
        }


        public String userJson(String name,String pass){
            return "{\"username\":"+"\""+name+"\""+","+"\"password\":"+"\""+pass+"\""+"}";
        }

        public String modifyJson(String newname,String newpass){
            return "{\"new_name\":"+"\""+newname+"\""+","+"\"new_password\":"+"\""+newpass+"\""+"}";
        }
    }

    public static String getMd5(String text){
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(text.getBytes());
            byte b[] = md.digest();

            int i;

            StringBuffer buffer = new StringBuffer("");
            for (int offset = 0; offset <b.length; offset++){
                i = b[offset];
                if (i<0){
                    i+=256;
                }
                if (i<16){
                    buffer.append("0");
                }
                buffer.append(Integer.toHexString(i));
            }
            return buffer.toString();
        }catch (NoSuchAlgorithmException e){
            e.printStackTrace();
            return null;
        }
    }

    public void userModify(){

        new Thread(){
            @Override
            public void run(){
                final String newname = nameEt.getText().toString();
                final String newpass = passwordEt.getText().toString();

                String md5Pass = getMd5(newpass);

                if (TextUtils.isEmpty(newname)||TextUtils.isEmpty(newpass)){
                    Toast.makeText(getApplicationContext(),"不能为空",Toast.LENGTH_SHORT).show();

                    return;
                }

                HttpUtils httpUtils = new HttpUtils();
        String user = httpUtils.modifyJson(newname,md5Pass);

        try {
            final String result = httpUtils.modifyUser("http://ducktest.cleargrass.com/api/modify_user",user);
            Log.d("result",result);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String code = result.substring(result.indexOf(":")+1,result.indexOf(","));
                    if ("0".equals(code)){
                        Log.d("3333","修改成功");
                        Toast.makeText(getApplicationContext(),"修改成功!",Toast.LENGTH_SHORT).show();
                    }else {
                        Log.d("4444","修改失败");
                        Toast.makeText(getApplicationContext(),"修改失败!"+"\n"+result,Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }catch (IOException e){
            e.printStackTrace();
        }
            }
        }.start();
    }
}

