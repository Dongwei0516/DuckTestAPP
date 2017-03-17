package com.example.dongwei.testapp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by dongwei on 2017/2/27.
 */

public class DeviceMsgActivity extends Activity{

    private Button btnTestStart,buttonDFU;

    private LinearLayout deviceMsg;
    private TextView bottomMsgTest;
    private Handler mHandler;
    private LinearLayout testMsg;
    private BluetoothGatt mBluetoothGatt;
    private Context context;
    private BluetoothAdapter mBluetoothAdapter;
    private  static String address,name;
    private TextView deviceName,deviceAddress,deviceRssi,deviceToken,companyName,tem,hum,electric;

    BluetoothGattService messageService;
    BluetoothGattCharacteristic messageCharacteristic;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        btnTestStart = (Button)findViewById(R.id.button2);
        buttonDFU = (Button)findViewById(R.id.buttonDFU);
        deviceMsg = (LinearLayout) findViewById(R.id.device_msg);
        bottomMsgTest = (TextView)findViewById(R.id.bottom_msg_test);
        deviceName = (TextView)findViewById(R.id.device_name);
        deviceAddress = (TextView)findViewById(R.id.device_address);
        deviceRssi = (TextView)findViewById(R.id.device_rssi);
        deviceToken = (TextView)findViewById(R.id.device_token);
        companyName = (TextView)findViewById(R.id.Cleargrass);
        tem = (TextView)findViewById(R.id.tem);
        hum = (TextView)findViewById(R.id.humi);
        electric = (TextView)findViewById(R.id.electric);

        setDeviceMsg();
//        MainActivity.getHumiMsg();
        btnTestStart.setVisibility(View.VISIBLE);
        btnTestStart.setText("开始段码屏测试");
        btnTestStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                messageService = mBluetoothGatt.getService(UUID.fromString("226c0000-6476-4566-7562-66734470666d"));
//                messageCharacteristic = messageService.getCharacteristic(UUID.fromString("226cbb55-6476-4566-7562-66734470666d"));
//
//                byte[] bytes = Utils.hexStringToBytes("4347000104");
//                messageCharacteristic.setValue(bytes);
//                MainActivity.writeCharacteristic(messageCharacteristic);
//                Log.d("dmpTest","Start");

                MainActivity.startDmpTest();
//                MainActivity.stopDmpTest();

                Intent intent = new Intent(DeviceMsgActivity.this,DmpTestActivity.class);
                Bundle bundle = new Bundle();
                Log.d("address",address);
                bundle.putString("address",address);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        buttonDFU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(DeviceMsgActivity.this,DfuActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("address",address);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

    }

    private void setDeviceMsg(){
        int rssi;
        String token,data;
        deviceMsg.setVisibility(View.VISIBLE);


        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        name = bundle.getString("name");
        address = bundle.getString("address");
        rssi = bundle.getInt("rssi");
        token = bundle.getString("token");
//        company = bundle.getString("company");
        data = bundle.getString("humiture");
//        Log.d("company",company);
        String temp = data.substring(data.indexOf("=")+1,data.indexOf(" "));
        Log.d("tttt",temp);

        String humi = data.substring(data.indexOf("=",data.indexOf("=")+1)+1,data.indexOf(" ",data.indexOf(" ")+1));
        Log.d("hhhh",humi);

        String distance = data.substring(data.indexOf(" ",data.indexOf(" ")+1),data.indexOf("r")+1);

        deviceName.setText("Name: "+name);
        deviceAddress.setText("Address: "+address);
        deviceRssi.setText("RSSI: "+ rssi);
        deviceToken.setText("Token: "+ token);
        companyName.setText("Company: Cleargrass Inc");
        tem.setText("温度："+ temp);
        hum.setText("湿度: "+ humi);
        electric.setText("磁铁状态："+distance);
//        bottomMsgTest.setText("验证成功！");

    }
}
