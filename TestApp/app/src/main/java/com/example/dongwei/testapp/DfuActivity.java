package com.example.dongwei.testapp;

import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import no.nordicsemi.android.dfu.DfuProgressListener;
import no.nordicsemi.android.dfu.DfuServiceInitiator;
import no.nordicsemi.android.dfu.DfuServiceListenerHelper;

import static com.example.dongwei.testapp.R.id.device;
import static com.example.dongwei.testapp.R.id.strength;

/**
 * Created by dongwei on 2017/3/13.
 */

public class DfuActivity extends AppCompatActivity{

    private Button btnStartTest, btnDfuUpdate, btnScanStart;
    private TextView deviceDfu, lastDfu, btmMsg;
    private BluetoothGatt mBluetoothGatt;
    private Context context;
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;
    private BluetoothDevice dfuDevice;
    private int mFileTypeTmp,mFileType;
    private ListView listView;
    private boolean mStatusOk;
    private List<BluetoothDevice> mList = new ArrayList<>();
    private ArrayList<Integer> mRSSI;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        this.setTitle("DFU升级");
        setContentView(R.layout.activity_dfu);

        requestMacAddress();

        deviceDfu = (TextView)findViewById(R.id.device_Dfu);
        lastDfu = (TextView)findViewById(R.id.lastDfu);
        btnDfuUpdate = (Button)findViewById(R.id.dfuModeBtn);
        btnScanStart = (Button)findViewById(R.id.reconncetBtn);
        btmMsg = (TextView)findViewById(R.id.bottom_msg_dfu);
        listView = (ListView)findViewById(R.id.dfuLv);

        context = this;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

//
//        btnStartTest.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(DfuActivity.this,TestActivity.class);
//                Bundle bundle = new Bundle();
//                intent.putExtras(bundle);
//                startActivity(intent);
//            }
//        });

        btnDfuUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                onConnect();
                onUpload();
//                openFileChooser();
            }
        });

        btnScanStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DfuActivity.this,MainActivity.class);
                DeviceMsgActivity.stopBleConnect();
                finish();
                startActivity(intent);
            }
        });

    }

    private void requestMacAddress(){  //请求mac地址 获取版本号

    }

    private final DfuProgressListener mDfuProgressListener = new DfuProgressListener() {
        @Override
        public void onDeviceConnecting(String deviceAddress) {
            Log.i("dfu", "onDeviceConnecting");
            btmMsg.setText("设备连接中。。");
            onConnect();
        }

        @Override
        public void onDeviceConnected(String deviceAddress) {
            Log.i("dfu", "onDeviceConnected");
            btmMsg.setText("连接成功！");
        }

        @Override
        public void onDfuProcessStarting(String deviceAddress) {
            Log.i("dfu", "onDfuProcessStarting");
            btmMsg.setText("设备进入DFU模式");
        }

        @Override
        public void onDfuProcessStarted(String deviceAddress) {
            Log.i("dfu", "onDfuProcessStarted");
        }

        @Override
        public void onEnablingDfuMode(String deviceAddress) {
            Log.i("dfu", "onEnablingDfuMode");
            btmMsg.setText("DFU模式已启动！");
        }

        @Override
        public void onProgressChanged(String deviceAddress, int percent, float speed, float avgSpeed, int currentPart, int partsTotal) {
            Log.i("dfu", "onProgressChanged");
            Log.i("dfu", "onProgressChanged" + percent);
            btmMsg.setText("升级进度："+percent);
//            dfuDialogFragment.setProgress(percent);
        }

        @Override
        public void onFirmwareValidating(String deviceAddress) {
            Log.i("dfu", "onFirmwareValidating");
        }

        @Override
        public void onDeviceDisconnecting(String deviceAddress) {

            Log.i("dfu", "onDeviceDisconnecting");
            btmMsg.setText("正在断开连接。。");
        }

        @Override
        public void onDeviceDisconnected(String deviceAddress) {
            Log.i("dfu", "onDeviceDisconnected");
            btmMsg.setText("设备连接断开！");
            btnDfuUpdate.setVisibility(View.GONE);
            btnScanStart.setVisibility(View.VISIBLE);

        }

        @Override
        public void onDfuCompleted(String deviceAddress) {
            Log.i("dfu", "onDfuCompleted");
            Toast.makeText(getApplicationContext(), "DFU升级完成，请重新连接设备", Toast.LENGTH_SHORT).show();
//            stopDfu();
//            dfuDialogFragment.getProgressBar().setIndeterminate(true);
            //升级成功，重新连接设备
        }

        @Override
        public void onDfuAborted(String deviceAddress) {
            Log.i("dfu", "onDfuAborted");
        }

        @Override
        public void onError(String deviceAddress, int error, int errorType, String message) {
            Log.i("dfu", "onError");
//            Toast.makeText(getApplicationContext(), "升级失败，请重新点击升级。", Toast.LENGTH_SHORT).show();
        }
    };


//    private void openFileChooser(){
//        final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType(mFileTypeTmp == DfuService.TYPE_AUTO ? DfuService.MIME_TYPE_ZIP : DfuService.MIME_TYPE_OCTET_STREAM);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        if (intent.resolveActivity(getPackageManager()) != null){
//            startActivityForResult(intent, 1);
//        }
//    }



    public void onUpload(){
        if (isDfuServiceRunning()){

        }
        String address;
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        address = bundle.getString("address");

         new DfuServiceInitiator(address).setDisableNotification(true)
                .setZip(R.raw.nrfutil_dfu_v10).start(this,DfuService.class);
    }

    private boolean isDfuServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (DfuService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (device.getName()==null){

                    }else if (device.getName().equals("MJ_HT_V1_DFU")){
                        dfuDevice = device;
                        Log.d("device",dfuDevice.getName());
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    }
                    nHandler.sendEmptyMessage(1);

                }
            });
        }
    };

    public final Handler nHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case 1:

            }
        }
    };

    private void onConnect(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.startLeScan(mLeScanCallback);

        mHandler = new Handler();

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("DDDDD",dfuDevice.getName());
                mBluetoothGatt = dfuDevice.connectGatt(context,false,bluetoothGattCallback);
                new DfuServiceInitiator(dfuDevice.getAddress()).setDisableNotification(true)
                        .setZip(R.raw.nrfutil_dfu_v17).start(getApplicationContext(),DfuService.class);

            }
        },5000);


    }

    BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, int status,int newState){
            super.onConnectionStateChange(gatt,status,newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
//                gatt.discoverServices();
                Log.d("Connected", "onConnected");
                mBluetoothGatt.discoverServices();
            }else {
                Log.d("Connected" ,"DisConnected: "+ status);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status){
            if (status == BluetoothGatt.GATT_SUCCESS){
                Log.d("State","ServicesDiscovered");
            }else{
                Log.d("State","SvcDiscoveredFailed: "+status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status){
            if (status == BluetoothGatt.GATT_SUCCESS){
                Log.d("State","CharRead");
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,BluetoothGattCharacteristic characteristic){
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic,int status) {
            gatt.writeCharacteristic(characteristic);
        }

    };

    @Override
    protected void onResume() {
        DfuServiceListenerHelper.registerProgressListener(this, mDfuProgressListener);
        super.onResume();
    }

    @Override
    protected void onPause() {
        DfuServiceListenerHelper.unregisterProgressListener(this, mDfuProgressListener);
        super.onPause();
    }


}
