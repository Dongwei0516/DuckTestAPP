package com.example.dongwei.testapp;

import android.app.Activity;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.internal.Util;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by dongwei on 2017/3/10.
 */

public class LoginActivity extends AppCompatActivity {

    private EditText nameEt, passwordEt;
    private TextView lastUser;
    private Button btnLogin, btnModify;
    private String url = "http://ducktest.cleargrass.com/api/login";
    public static String SESSION = null;
    public static String USERNAME = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("Duck工装测试程序");
        setContentView(R.layout.activity_login);

        nameEt = (EditText)findViewById(R.id.nameEt);
        passwordEt = (EditText)findViewById(R.id.passwordEt);
        btnLogin = (Button)findViewById(R.id.loginBtn);
        btnModify = (Button)findViewById(R.id.modifyBtn);

        passwordEt.requestFocus();

        InputMethodManager inputMethodManager = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(passwordEt.getWindowToken(),0);

        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        String lastName = sharedPreferences.getString("lastUser", null);
        String session = sharedPreferences.getString("session",null);
        if (!TextUtils.isEmpty(lastName)){
            nameEt.setText(lastName);
        }


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
//                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
//                finish();
//                startActivity(intent);
            }
        });

//        btnModify.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                userModify();
//            }
//        });
    }

    private void login(){
        final String name = nameEt.getText().toString();
        final String pass = passwordEt.getText().toString();

        if (TextUtils.isEmpty(name)||TextUtils.isEmpty(pass)){
            Toast.makeText(getApplicationContext(),"不能为空",Toast.LENGTH_SHORT).show();

            return;
        }

                String md5Pass = Utils.getMd5(pass);
                Log.d("passMd5",md5Pass);

                HttpUtils httpUtils = new HttpUtils();
                String user = httpUtils.userJson(name,md5Pass);
                Log.d("user",user);
                    USERNAME = name;
                try {
                    final HttpUtils.NetCallback callback = new HttpUtils.NetCallback() {
                        @Override
                        public void onResult(final String string) {
                            final String session = string.substring(string.indexOf("session")+10,string.indexOf("\"}"));
                            SESSION = session;
                            Log.d("session",session);
                            SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                            sharedPreferences.edit().putString("lastUser", name).putString("session",session).commit();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String code = string.substring(string.indexOf(":")+1,string.indexOf(","));
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
                                        Toast.makeText(getApplicationContext(),"登录失败"+"\n"+string,Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    };

                    httpUtils.login(user,callback);

    } catch (IOException e) {
                    e.printStackTrace();
                }
    }

//    public void userModify(){
//
//        new Thread(){
//            @Override
//            public void run(){
//                final String newname = nameEt.getText().toString();
//                final String newpass = passwordEt.getText().toString();
//
//                String md5Pass = Utils.getMd5(newpass);
//
//                if (TextUtils.isEmpty(newname)||TextUtils.isEmpty(newpass)){
//                    Toast.makeText(getApplicationContext(),"不能为空",Toast.LENGTH_SHORT).show();
//
//                    return;
//                }
//
//                Utils.HttpUtils httpUtils = new Utils.HttpUtils();
//        String user = httpUtils.modifyJson(newname,md5Pass);
//
//        try {
//            final String result = httpUtils.modifyUser("http://ducktest.cleargrass.com/api/modify_user",user);
//            Log.d("result",result);
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    String code = result.substring(result.indexOf(":")+1,result.indexOf(","));
//                    if ("0".equals(code)){
//                        Log.d("3333","修改成功");
//                        Toast.makeText(getApplicationContext(),"修改成功!",Toast.LENGTH_SHORT).show();
//                    }else {
//                        Log.d("4444","修改失败");
//                        Toast.makeText(getApplicationContext(),"修改失败!"+"\n"+result,Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
//        }catch (IOException e){
//            e.printStackTrace();
//        }
//            }
//        }.start();
//    }
}

