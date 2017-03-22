package com.example.dongwei.testapp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import static com.example.dongwei.testapp.R.id.device;
import static com.example.dongwei.testapp.R.id.strength;

public class MainActivity extends AppCompatActivity{

    private Button mButton,refreshBtn;
    private TextView bottomMsg;
    private Switch autoConnectSwitch;
    private RelativeLayout topBar;
    private ListView deviceList;
    private TestCountDownTimer testCountDownTimer;
    private Handler mHandler;
    private Context context;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mDevice;
    private List<BluetoothDevice> mList = new ArrayList<>();
    private ListViewAdapter mListViewAdapter;
    private ArrayList<Integer> mRSSI;
    List<Device> mDeviceList = new ArrayList<Device>();
    private Device mdevice;
    private ArrayList<String> mUUID;
    private static BluetoothGatt mBluetoothGatt;

    private Handler mHandler01 = new Handler();
    private Handler mHandler02 = new Handler();
    private long notTouchTime = 1000*60*5;
    private Date lastUpdateTime;
    private long timePeriod;

    private boolean autoConnect = true;

    public final static String ACTION_GATT_CONNECTED =
            "com.cleargrass.ducktestapp.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.cleargrass.ducktestapp.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.cleargrass.ducktestapp.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.cleargrass.ducktestapp.ACTION_DATA_AVAILABLE";
    public final static String ACTION_DATA_WRITE =
            "com.ecleargrass.ducktestapp.ACTION_DATA_WRITE";
    public final static String HUMITURE_DATA = "com.cleargrass.ducktestapp.HUMITURE_DATA";
    public final static String TOKEN_DATA = "com.cleargrass.ducktestapp.TOKEN_DATA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("蓝牙列表");
        setContentView(R.layout.activity_main);


        autoConnectSwitch = (Switch)findViewById(R.id.auto_connect_switch);
        mButton = (Button)findViewById(R.id.startScanBtn);
        deviceList = (ListView)findViewById(R.id.devicelist);
        refreshBtn = (Button)findViewById(R.id.refresh_btn);
        bottomMsg = (TextView)findViewById(R.id.bottom_msg);
        topBar = (RelativeLayout) findViewById(R.id.topBar);
        testCountDownTimer = new TestCountDownTimer();

        autoConnectSwitch.setChecked(true);
        autoConnectSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    autoConnect = true;
                    testCountDownTimer.cancel();
                    testCountDownTimer.start();
                }else {
                    autoConnect = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    testCountDownTimer.cancel();
                    bottomMsg.setText("取消自动连接");
                }
            }
        });

        if(mBluetoothGatt !=null) {
            mBluetoothGatt.disconnect();
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mBluetoothGatt.close();
        }

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mButton.setVisibility(View.GONE);
                deviceList.setVisibility(View.VISIBLE);
                autoConnectSwitch.setVisibility(View.VISIBLE);
                refreshBtn.setVisibility(View.VISIBLE);
                bottomMsg.setVisibility(View.VISIBLE);
                onClickScan(v);

            }
        });

        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testCountDownTimer.cancel();
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
//                mList.clear();
                mBluetoothAdapter.startLeScan(mLeScanCallback);
//                mListViewAdapter = new ListViewAdapter(mList,context);
//                mListViewAdapter.notifyDataSetChanged();
                mBluetoothAdapter.startDiscovery();
                if (autoConnect) {
                    testCountDownTimer.start();
                }
//                mBluetoothGatt = mDeviceList.get(0).getDevices().connectGatt(context,false,bluetoothGattCallback);
            }
        });

        deviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Integer rssi = null;
                    mDevice = mDeviceList.get(position).getDevices();
                    rssi = mDeviceList.get(position).getRssis();
                    testCountDownTimer.cancel();
                    bottomMsg.setText("取消自动连接");
                    startTest(mDevice, rssi);

                }
            });

        mListViewAdapter = new ListViewAdapter(mDeviceList,context);
        context = this;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public static class Device extends Object{
        private BluetoothDevice devices;
        private int rssis;

        public  Device(BluetoothDevice devices, Integer rssis){
            this.devices = devices;
            this.rssis = rssis;
        }

        public void setDevices(BluetoothDevice devices){
            this.devices = devices ;
        }

        public void setRssis(int rssis){
            Intent intent = new Intent();
            this.rssis = intent.getShortExtra(devices.EXTRA_RSSI,Short.MIN_VALUE);
        }

        public BluetoothDevice getDevices(){
            return devices;
        }

        public Integer getRssis(){
            return rssis;
        }

        public String toString() {
            return this.getDevices().getAddress();
        }

        public boolean equals(Object obj){
            Device device =(Device)obj;
            if (device.devices.getAddress() == devices.getAddress()){
                return true;
            }else {
                return false;
            }
        }

        public int hashCode(){
            Device device = (Device)this;
            return device.hashCode();
        }

    }

    public class sortRssi implements Comparator<Device>{
        @Override
        public int compare(Device o1, Device o2){
            return o2.getRssis()-o1.getRssis();
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
//        unregisterReceiver(mReceiver);
    }


    private void onClickScan(View v){

        bottomMsg.setText("正在扫描..");
        mBluetoothAdapter.startLeScan(mLeScanCallback);
        deviceList.setVisibility(View.VISIBLE);
//        if (mBluetoothAdapter.isDiscovering()){
//            mBluetoothAdapter.cancelDiscovery();
//        }
        mBluetoothAdapter.startDiscovery();

        mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                bottomMsg.setText("扫描到"+"n"+"个设备");
                testCountDownTimer.start();

            }
        },5000);

    }

    private void autoConnect(){
        Integer rssi = null;
        mDevice = mDeviceList.get(0).getDevices();
        rssi = mDeviceList.get(0).getRssis();
        startTest(mDevice, rssi);

    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!mList.contains(device)){
                        mList.add(device);
                        mRSSI.add(rssi);
                        Device device1 = new Device(device,rssi);
                        if (!mDeviceList.contains(device1)){
                           mDeviceList.add(device1);
                        }
                        Collections.sort(mDeviceList,new sortRssi());
                        Log.d("List", String.valueOf(mList));
                        Log.d("DeviceList", String.valueOf(mDeviceList));

                    }
                    if (!mListViewAdapter.isEmpty()){
                        deviceList.setAdapter(mListViewAdapter);
                    }
                }
            });
        }
    };


    public class ListViewAdapter extends BaseAdapter{

        private LayoutInflater layoutInflater;

        public ListViewAdapter(List<Device> list,Context context){
            mDeviceList = list;
            layoutInflater = LayoutInflater.from(MainActivity.this);
            mRSSI = new ArrayList<Integer>();
        }


        @Override
        public int getCount() {
            return mDeviceList.size();
        }

        @Override
        public Object getItem(int position) {
            return mDeviceList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
//            int n = mBlueList.size();
//            Log.d("nnn", String.valueOf(n));
//            bottomMsg.setText("扫描到"+n+"个设备");
//            Device mdevice = new Device(mBlueList.get(position),mRSSI.get(position));
            if (convertView == null){
                viewHolder = new ListViewAdapter.ViewHolder();
                convertView = layoutInflater.inflate(R.layout.list_item,null);
                viewHolder.device = (TextView)convertView.findViewById(device);
                viewHolder.address = (TextView)convertView.findViewById(R.id.address);
                viewHolder.image = (ImageView)convertView.findViewById(strength);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ListViewAdapter.ViewHolder)convertView.getTag();
            }

//            if (mDeviceList.indexOf(mdevice) == -1){
//                mDeviceList.add(mdevice);
//               Log.d("device", String.valueOf(mdevice.getDevices().getName()));
//                }
//            Collections.sort(mDeviceList,new sortRssi());

            if (TextUtils.isEmpty(mDeviceList.get(position).getDevices().getName())){
                viewHolder.device.setText( "NoName");
            }else {
                viewHolder.device.setText( mDeviceList.get(position).getDevices().getName());
            }
            viewHolder.address.setText(mDeviceList.get(position).getDevices().getAddress() + "    RSSI:" + mDeviceList.get(position).getRssis());

//            Log.d("strength", String.valueOf(mDeviceList.get(position).getRssis()));
            if (mDeviceList.get(position).getRssis()>-20){
                viewHolder.image.setImageResource(R.drawable.max);
            }else if (mDeviceList.get(position).getRssis()>-50){
                viewHolder.image.setImageResource(R.drawable.high);
            }else if (mDeviceList.get(position).getRssis()>-70){
                viewHolder.image.setImageResource(R.drawable.middle);
            }else {
                viewHolder.image.setImageResource(R.drawable.low);
            }

//            viewHolder.device.setText(mBlueList.get(position).getName()+": "+mBlueList.get(position).getAddress()+" RSSI:"+mRSSI.get(position));
            return convertView;

        }

        class ViewHolder {
            TextView device ;
            TextView address;
            ImageView image;
        }
    }


    class TestCountDownTimer extends CountDownTimer{

        public TestCountDownTimer() {
            super(5000, 1000);
        }

        @Override
        public void onTick(long millisUntilFinished) {
//            mBluetoothAdapter.cancelDiscovery();
            bottomMsg.setText(millisUntilFinished/1000+"秒后连接到设备");

        }

        @Override
        public void onFinish() {
            bottomMsg.setText("");
            mBluetoothAdapter.cancelDiscovery();
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            if (autoConnect) {
                autoConnect();
            }
//            mBluetoothGatt = mDeviceList.get(0).getDevices().connectGatt(context,false,bluetoothGattCallback);
//            scanAndConnectDfu(mDeviceList.get(0).getDevices());
        }
    }

    private void startTest(BluetoothDevice device,Integer rssi){

        mBluetoothAdapter.cancelDiscovery();
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
        Intent intent = new Intent(MainActivity.this,DeviceMsgActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("device",device);
//        bundle.putString("name",device.getName());
//        bundle.putString("address",device.getAddress());
//        bundle.putInt("rssi",rssi);
        intent.putExtras(bundle);
//        finish();
        startActivity(intent);
    }



    @Override
    public void onBackPressed(){
        super.onBackPressed();
        disconnect();
    }

    public static void disconnect(){
        if(mBluetoothGatt !=null) {
            mBluetoothGatt.disconnect();
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mBluetoothGatt.close();
        }
    }



//    @Override
//    public boolean dispatchTouchEvent(MotionEvent event){
//        updateUserActionTime();
//        return super.dispatchTouchEvent(event);
//    }
//
//    public void updateUserActionTime(){
//        Date timeNow = new Date(System.currentTimeMillis());
//        timePeriod = timeNow.getTime() - lastUpdateTime.getTime();
//        lastUpdateTime.setTime(timeNow.getTime());
//
//    }

//    public void logout(){
//        Intent logoutIntent = new Intent(this,LoginActivity.class);
//        logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(logoutIntent);
//    }


}
