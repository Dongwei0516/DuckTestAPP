package com.example.dongwei.testapp;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * Created by dongwei on 2017/2/27.
 */

public class DeviceMsgActivity extends AppCompatActivity{

    private static String token,humi;
    private boolean getToken = false;
    private boolean getService = false;
    private Button btnTestStart,buttonDFU, btnTestStop;

    private LinearLayout deviceMsg;
    private TextView bottomMsgTest;
    private Handler mHandler;
    private int rssi;
    private LinearLayout testMsg;
    private static BluetoothGatt mBluetoothGatt;
    private static BluetoothDevice mBluetoothDevice;
    private Context context;
    private BluetoothAdapter mBluetoothAdapter;
    private  static String address,name;
    private TextView deviceName,deviceAddress,deviceRssi,deviceToken,tem,hum,electric,httpToken,tokenVerify;
    private String CompanyName,Humiture,TokenString,HttpTokenString;
    private ProgressBar progressBar;

    public static String SESSION = null;
    public static String USERNAME = null;
    public static String DEVICE_TOKEN = null;

    private int progressStatus = 0;
    private Timer timer = new Timer();
    private BluetoothGattService humitureService;
    private BluetoothGattCharacteristic humitureCharacteristic;
    private BluetoothGattCharacteristic tokenCha;

    public final static String ACTION_GATT_CONNECTED =
            "com.cleargrass.ducktestapp.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.cleargrass.ducktestapp.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.cleargrass.ducktestapp.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.cleargrass.ducktestapp.ACTION_DATA_AVAILABLE";
    public final static String ACTION_DATA_WRITE =
            "com.cleargrass.ducktestapp.ACTION_DATA_WRITE";
    public final static String COMPANY_DATA = "com.cleargrass.ducktestapp.EXTRA_DATA";
    public final static String HUMITURE_DATA = "com.cleargrass.ducktestapp.HUMITURE_DATA";
    public static String TOKEN_DATA = "com.cleargrass.ducktestapp.TOKEN_DATA";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        this.setTitle("设备信息");
        setContentView(R.layout.activity_devicemsg);


        btnTestStart = (Button)findViewById(R.id.startDmpTestBtn);
        btnTestStop = (Button)findViewById(R.id.stopDmpTestBtn);
        buttonDFU = (Button)findViewById(R.id.DfuBtn);
        bottomMsgTest = (TextView)findViewById(R.id.bottom_msg_test);
        deviceName = (TextView)findViewById(R.id.device_name);
        deviceAddress = (TextView)findViewById(R.id.device_address);
        deviceToken = (TextView)findViewById(R.id.device_token);
        httpToken = (TextView)findViewById(R.id.httpTokenTv);
        tokenVerify = (TextView)findViewById(R.id.tokenVerify);
        tem = (TextView)findViewById(R.id.tem);
        hum = (TextView)findViewById(R.id.humi);
        electric = (TextView)findViewById(R.id.electric);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);

//        Log.d("onCreate", "after find");
        Intent intent = this.getIntent();
        mBluetoothDevice = intent.getParcelableExtra("device");
        name = mBluetoothDevice.getName();
        address = mBluetoothDevice.getAddress();

        deviceName.setText("设备名: " + name);
        deviceAddress.setText("Mac地址: " + address);

        btnTestStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getService) {
                    httpBeginTest();
                    startDmpTest();
                    startTimeTask();
                }

            }
        });

        btnTestStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressStatus = 0;
                btnTestStop.setVisibility(View.GONE);
                btnTestStart.setVisibility(View.VISIBLE);
                stopDmpTest();
                showDialog();
            }
        });

        buttonDFU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getToken();
                Intent intent = new Intent(DeviceMsgActivity.this,DfuActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("address",address);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        Log.d("onCreate", "after set");

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DeviceMsgActivity.ACTION_GATT_CONNECTED);
        intentFilter.addAction(DeviceMsgActivity.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(DeviceMsgActivity.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(DeviceMsgActivity.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(DeviceMsgActivity.ACTION_DATA_WRITE);
        registerReceiver(mReceiver,intentFilter);

        Log.d("onCreate", "after register");

        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        String lastName = sharedPreferences.getString("lastUser", null);
        String session = sharedPreferences.getString("session",null);
        USERNAME = lastName;
        SESSION = session;

        mBluetoothGatt = mBluetoothDevice.connectGatt(this,false,bluetoothGattCallback);
        Log.d("onCreate", "after conntect");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("onResume", "");
    }

    private void startTimeTask(){
        stopTimeTask();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (progressStatus < 100){
                            progressStatus +=1;
                            progressBar.setProgress(progressStatus);
                        }else {
                            stopTimeTask();
                                btnTestStart.setVisibility(View.GONE);
                                btnTestStop.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        },0,50);
    }

    private void stopTimeTask(){
        if (timer!= null){
            timer.cancel();
            timer = null;
        }
    }

    private void showDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("测试结果反馈");
        builder.setMessage("是否通过测试?");
        builder.setNeutralButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("通过", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                httpTestPass();
                dialog.dismiss();
                Intent intent = new Intent(DeviceMsgActivity.this,DmpTestActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("address",address);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("未通过", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                httpTestFailed();
                dialog.dismiss();
                Intent intent = new Intent(DeviceMsgActivity.this,DmpTestActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("address",address);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        builder.setCancelable(false);
        builder.show();
    }



    private void setHumitureUi(final String data){
//        deviceMsg.setVisibility(View.VISIBLE);
        if (data!=null||!"".equals(data)) {
            final String temp = data.substring(data.indexOf("=") + 1, data.indexOf(" "));
            final String humi = data.substring(data.indexOf("=", data.indexOf("=") + 1) + 1, data.indexOf(" ", data.indexOf(" ") + 1));
            final String distance = data.substring(data.indexOf(" ", data.indexOf(" ") + 1), data.indexOf("r") + 1);
            Log.d("setHumitureUi", String.format("parsed: %s %s %s", temp, humi, distance));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    tem.setText("温度："+ temp );
                    hum.setText("湿度："+ humi + "%");
                    electric.setText("磁铁状态："+distance);
                }
            });
            getToken();
        }

    }

    private void setTokenUi(final String data){
        if (!TextUtils.isEmpty(data)&&data.contains("0003")){
            final String token = data.substring(data.indexOf("03")+2);
            DEVICE_TOKEN = token;
            Log.d("Token",token);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    deviceToken.setText("Token: "+ token);
                }
            });
        }

    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            String action = intent.getAction();
//            BluetoothDevice device;
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//            if (MainActivity.ACTION_GATT_CONNECTED.equals(action)) {
//                Log.d("State", "Connected");
//
//            } else if (MainActivity.ACTION_GATT_DISCONNECTED.equals(action)) {
//                Log.d("State", "DisConnected");
//
//            } else
                if (MainActivity.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                Log.d("State", "Discovered");

                humitureService = mBluetoothGatt.getService(UUID.fromString("226c0000-6476-4566-7562-66734470666d"));
                humitureCharacteristic = humitureService.getCharacteristic(UUID.fromString("226caa55-6476-4566-7562-66734470666d"));
                tokenCha = humitureService.getCharacteristic(UUID.fromString("226cbb55-6476-4566-7562-66734470666d"));

                mBluetoothGatt.setCharacteristicNotification(humitureCharacteristic, true);
                mBluetoothGatt.setCharacteristicNotification(tokenCha,true);

                for (BluetoothGattDescriptor descriptor : humitureCharacteristic.getDescriptors()) {
                    if (descriptor != null) {
                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    }
                    mBluetoothGatt.readDescriptor(descriptor);
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mBluetoothGatt.writeDescriptor(descriptor);

                }
                for (BluetoothGattDescriptor descriptor : tokenCha.getDescriptors()) {
                    if (descriptor != null) {
                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    }
                    mBluetoothGatt.readDescriptor(descriptor);
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mBluetoothGatt.writeDescriptor(descriptor);

                }

            } else if (MainActivity.ACTION_DATA_AVAILABLE.equals(action)) {
                    Log.d("State", "DataAvailable");

                    if (!getToken){
                        getToken();
                        Log.d("getToken","TRUE");
                    }
                }
        }
    };


    BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, int status,int newState){
            super.onConnectionStateChange(gatt,status,newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                gatt.discoverServices();
                Log.d("Connected", "onConnected");
                broadcastUpdate(ACTION_GATT_CONNECTED);
//                mBluetoothGatt.discoverServices();
            }else {
                Log.d("Connected" ,"DisConnected: "+ status);
//                Toast.makeText(getApplicationContext(),"蓝牙连接失败",Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status){
            if (status == BluetoothGatt.GATT_SUCCESS){
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
                getService = true;
                Log.d("State","ServicesDiscovered");
            }else{
                Log.d("State","SvcDiscoveredFailed: "+status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status){
            if (status == BluetoothGatt.GATT_SUCCESS){

//                if (characteristic == deviceNameCharacteristic) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic, HUMITURE_DATA);
//                    Log.d("22222","222222");
//                }else if (characteristic == humitureCharacteristic){
//                    broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic, HUMITURE_DATA);
//                    Log.d("33333","333333");
//                }
                Log.d("State","CharRead");
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,BluetoothGattCharacteristic characteristic){
            if (characteristic == humitureCharacteristic) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic,HUMITURE_DATA);
            }else if (characteristic == tokenCha){
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic,TOKEN_DATA);
            }
//            Log.d("ValueChanged", String.valueOf(characteristic.getValue()));
        }



        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic,int status) {
//            gatt.writeCharacteristic(characteristic);
            broadcastUpdate(ACTION_DATA_WRITE, characteristic);
            Log.d("State","CharWrite");
//            startDmpTest();
        }
//
//        @Override
//        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status){
//
//        }
//
//        @Override
//        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
//            if (descriptor!=null) {
//                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//                gatt.writeDescriptor(descriptor);
//            }
//        }
    };

    private void broadcastUpdate(final String action){
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action,final  BluetoothGattCharacteristic characteristic){
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic,final String message) {
        final byte[] data = characteristic.getValue();
        Log.d("onCharacteristicChanged", "data: " + data.toString());

//        Log.d("action",action);
        if (Objects.equals(message, HUMITURE_DATA)) {
            Log.d("string","HUMITURE_DATA");
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for (byte byteChar : data) {
                    stringBuilder.append(String.format("%02X", byteChar));
                }
                humi  = new String(data).trim();
                setHumitureUi(humi);
                Log.d("humi",humi);

            }
        }else if (Objects.equals(message, TOKEN_DATA)){
            Log.d("string","TOKEN_DATA");
            setTokenUi(Utils.bytesToHexString(data));
            token = Utils.bytesToHexString(data);
        }
    }

//    public void readCharacteristic(BluetoothGattCharacteristic characteristic){
//
//        mBluetoothGatt.readCharacteristic(characteristic);
//
//    }

    public static void writeCharacteristic(BluetoothGattCharacteristic characteristic){

        mBluetoothGatt.writeCharacteristic(characteristic);
    }

    public static void stopBleConnect(){
        mBluetoothGatt.disconnect();
    }

    public  static void startDmpTest(){
        BluetoothGattService messageService;
        BluetoothGattCharacteristic messageCharacteristic;


        messageService = mBluetoothGatt.getService(UUID.fromString("226c0000-6476-4566-7562-66734470666d"));
        messageCharacteristic = messageService.getCharacteristic(UUID.fromString("226cbb55-6476-4566-7562-66734470666d"));

        byte[] bytes = Utils.hexStringToBytes("4347000104");
        messageCharacteristic.setValue(bytes);
        String str = new String(bytes);
        Log.d("bytes",str);
        writeCharacteristic(messageCharacteristic);
        Log.d("dmpTest","Write");

    }

    public static void stopDmpTest(){
        BluetoothGattService messageService;
        BluetoothGattCharacteristic messageCharacteristic;

        messageService = mBluetoothGatt.getService(UUID.fromString("226c0000-6476-4566-7562-66734470666d"));
        messageCharacteristic = messageService.getCharacteristic(UUID.fromString("226cbb55-6476-4566-7562-66734470666d"));

        byte[] bytes = Utils.hexStringToBytes("4347000105");
        messageCharacteristic.setValue(bytes);
        writeCharacteristic(messageCharacteristic);

    }

    public void getToken(){
        BluetoothGattService tokenService;
        BluetoothGattCharacteristic tokenCharacteristic;


        tokenService = mBluetoothGatt.getService(UUID.fromString("226c0000-6476-4566-7562-66734470666d"));
        tokenCharacteristic = tokenService.getCharacteristic(UUID.fromString("226cbb55-6476-4566-7562-66734470666d"));

        byte[] bytes = Utils.hexStringToBytes("43470003");
        tokenCharacteristic.setValue(bytes);
        writeCharacteristic(tokenCharacteristic);

        getToken = true;

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if (mBluetoothGatt!=null) {
            mBluetoothGatt.disconnect();
        }
        unregisterReceiver(mReceiver);
    }

    private void httpBeginTest(){
        HttpUtils httpUtils = new HttpUtils();

        try {
            final HttpUtils.NetCallback callback = new HttpUtils.NetCallback() {

                @Override
                public void onResult(String string) {
                    Log.d("Callback", string);
                    final String token = string.substring(string.indexOf("03")+2,string.indexOf("\"}"));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            httpToken.setText("网络Token: "+token);
                            Log.d("1",token);
                            if (token.equals(DEVICE_TOKEN)){
                                tokenVerify.setText("比对结果: Token验证成功");
                            }else {
                                tokenVerify.setText("比对结果: Token验证失败");
                            }
                        }
                    });

                }
            };

            SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
            String lastName = sharedPreferences.getString("lastUser", null);
            String session = sharedPreferences.getString("session",null);

            httpUtils.beginTest("http://ducktest.cleargrass.com/api/begin_test/" + address + "/" + token,lastName,session, callback);
            Log.d("address", address);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void httpTestPass(){

        HttpUtils httpUtils = new HttpUtils();

        String testdata = httpUtils.testData(token, "pass", "pass");

        try {
            final HttpUtils.NetCallback callback = new HttpUtils.NetCallback() {
                @Override
                public void onResult(String string) {
                    Log.d("Callback", string);
                }
            };

            SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
            String lastName = sharedPreferences.getString("lastUser", null);
            String session = sharedPreferences.getString("session",null);
            httpUtils.testResult("http://ducktest.cleargrass.com/api/test_pass/" + address, testdata,lastName,session, callback);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void httpTestFailed(){

        HttpUtils httpUtils = new HttpUtils();

        String testdata = httpUtils.testData(token, "failed", "failed");

        try {
            final HttpUtils.NetCallback callback = new HttpUtils.NetCallback() {
                @Override
                public void onResult(String string) {
                    Log.d("Callback", string);
                }
            };

            SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
            String lastName = sharedPreferences.getString("lastUser", null);
            String session = sharedPreferences.getString("session",null);
            httpUtils.testResult("http://ducktest.cleargrass.com/api/test_failed/" + address, testdata,lastName,session, callback);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
