package com.example.dongwei.testapp;

import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.security.Key;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.example.dongwei.testapp.R.id.device;
import static com.example.dongwei.testapp.R.id.strength;

public class MainActivity extends Activity {

    private Button mButton,refreshBtn;
    private TextView bottomMsg;
    private RelativeLayout topBar;
    private ListView deviceList;
    private TestCountDownTimer testCountDownTimer;
    private Handler mHandler;
    private Context context;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGattServer bluetoothGattServer;
    private List<BluetoothDevice> mList = new ArrayList<>();
    private ListViewAdapter mListViewAdapter;
    private ArrayList<Integer> mRSSI;
    List<Device> mDeviceList = new ArrayList<Device>();
    private ArrayList<String> mUUID;
    private static BluetoothGatt mBluetoothGatt;

    private Handler mHandler01 = new Handler();
    private Handler mHandler02 = new Handler();
    private long notTouchTime = 1000*60*5;
    private Date lastUpdateTime;
    private long timePeriod;

    private ArrayList<BluetoothGatt> bluetoothGattArrayList = new ArrayList<BluetoothGatt>();

    //写数据
    private BluetoothGattCharacteristic writeChar;
    private BluetoothGattService writeService;
    //读数据
    private BluetoothGattCharacteristic deviceNameCharacteristic;
    private BluetoothGattService deviceNameService;
    private BluetoothGattService companyService;
    private BluetoothGattCharacteristic companyCharacteristic;
    private BluetoothGattService humitureService;
    private BluetoothGattCharacteristic humitureCharacteristic;
    private BluetoothGattService electricService;
    private BluetoothGattCharacteristic electricCharacteristic;

    BluetoothGattService messageService;
    BluetoothGattCharacteristic messageCharacteristic;

    private String CompanyName,Humiture,Electric;

    private boolean read = true;
    private boolean write = false;


    byte[] WriteBytes = new byte[20];

    public final static String ACTION_GATT_CONNECTED =
            "com.example.dongwei.testapp.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.dongwei.testapp.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.dongwei.testapp.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED_2 =
            "com.example.dongwei.testapp.ACTION_GATT_SERVICES_DISCOVERED_2";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.dongwei.testapp.ACTION_DATA_AVAILABLE";
    public final static String ACTION_DATA_WRITE =
            "com.example.dongwei.testapp.ACTION_DATA_WRITE";
    public final static String COMPANY_DATA = "com.example.dongwei.testapp.EXTRA_DATA";
    public final static String HUMITURE_DATA = "com.example.dongwei.testapp.HUMITURE_DATA";
    public final static String ELECTRIC_DATA = "com.example.dongwei.testapp.ELECTRIC_DATA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mButton = (Button)findViewById(R.id.button1);
        deviceList = (ListView)findViewById(R.id.devicelist);
        refreshBtn = (Button)findViewById(R.id.button19);
        bottomMsg = (TextView)findViewById(R.id.bottom_msg);
        topBar = (RelativeLayout) findViewById(R.id.topBar);
        testCountDownTimer = new TestCountDownTimer(5000,1000);

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
                topBar.setVisibility(View.GONE);
                deviceList.setVisibility(View.VISIBLE);
                refreshBtn.setVisibility(View.VISIBLE);
                bottomMsg.setVisibility(View.VISIBLE);
                onClick_Search(v);

//                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
//                finish();
//                startActivity(intent);
            }
        });

        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothAdapter.startLeScan(mLeScanCallback);
                mBluetoothAdapter.startDiscovery();
                mBluetoothAdapter.cancelDiscovery();
                mBluetoothGatt = mDeviceList.get(0).getDevices().connectGatt(context,false,bluetoothGattCallback);

                deviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                });
            }
        });

        mListViewAdapter = new ListViewAdapter(mList,context);
        context = this;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MainActivity.ACTION_GATT_CONNECTED);
        intentFilter.addAction(MainActivity.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(MainActivity.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(MainActivity.ACTION_DATA_AVAILABLE);
        registerReceiver(mReceiver,intentFilter);
    }

    public final Handler nHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case 1:

            }
        }
    };

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


//    @Override
//    protected void onResume(){
//        super.onResume();
//    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }


    private void onClick_Search(View v){

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

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mList.indexOf(device)==-1){
                        mList.add(device);
                        mRSSI.add(rssi);
                    }
                    if (mListViewAdapter.isEmpty()){

                    }else {
                        deviceList.setAdapter(mListViewAdapter);
                    }
                    nHandler.sendEmptyMessage(1);

                }
            });
        }
    };


    public class ListViewAdapter extends BaseAdapter{

        private List<BluetoothDevice> mBlueList;
        private LayoutInflater layoutInflater;

        public ListViewAdapter(List<BluetoothDevice> list,Context context){
            mBlueList = list;
            layoutInflater = LayoutInflater.from(MainActivity.this);
            mRSSI = new ArrayList<Integer>();
        }


        @Override
        public int getCount() {
            return mBlueList.size();
        }

        @Override
        public Object getItem(int position) {
            return mBlueList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            int n = mBlueList.size();
//            Log.d("nnn", String.valueOf(n));
            bottomMsg.setText("扫描到"+n+"个设备");
            Device mdevice = new Device(mBlueList.get(position),mRSSI.get(position));
            if (convertView == null){
                viewHolder = new ListViewAdapter.ViewHolder();
                convertView = layoutInflater.inflate(R.layout.list_item,null);
                viewHolder.device = (TextView)convertView.findViewById(device);
                viewHolder.image = (ImageView)convertView.findViewById(strength);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ListViewAdapter.ViewHolder)convertView.getTag();
            }

//            Log.d("List", String.valueOf(mBlueList));
            if (mDeviceList.indexOf(mdevice) == -1){
                mDeviceList.add(mdevice);
//               Log.d("i", String.valueOf(position));
               Log.d("device", String.valueOf(mdevice.getDevices().getName()));
                }
            Collections.sort(mDeviceList,new sortRssi());

            deviceList.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    switch (scrollState){
                        case SCROLL_STATE_IDLE:
                            mBluetoothAdapter.startDiscovery();
                            break;
                        case SCROLL_STATE_FLING:
                            break;
                        case SCROLL_STATE_TOUCH_SCROLL:
                            mBluetoothAdapter.cancelDiscovery();
                            break;
                    }
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                      mBluetoothAdapter.cancelDiscovery();
                }
            });

            viewHolder.device.setText( mDeviceList.get(position).getDevices().getName()+" "+mDeviceList.get(position).getDevices().getAddress() + " RSSI:" + mDeviceList.get(position).getRssis());

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
            ImageView image;
        }
    }


    class TestCountDownTimer extends CountDownTimer{

        public TestCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
//            mBluetoothAdapter.cancelDiscovery();
            bottomMsg.setText(millisUntilFinished/1000+"秒后连接到"+ "Address");

        }

        @Override
        public void onFinish() {
            bottomMsg.setText("正在连接"+"Address");
//            deviceList.setVisibility(View.GONE);
//            connectState();
//            connect(mDeviceList.get(0).getDevices());
            mBluetoothAdapter.cancelDiscovery();
            mBluetoothGatt = mDeviceList.get(0).getDevices().connectGatt(context,false,bluetoothGattCallback);
//            scanAndConnectDfu(mDeviceList.get(0).getDevices());
        }
    }

//    private void connectState(){
//
//        boolean connected = true;
//        if (connected){
//            mHandler = new Handler();
//            mHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    bottomMsg.setText("连接成功！");
////                    getToken();
//
//                }
//            },1000);
//
//        }else {
//            bottomMsg.setText("连接失败！");
//        }
//    }

//    private void getToken(){
//        ////
//        bottomMsg.setText("取得Token");
//        mHandler = new Handler();
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                setToken();
//
//            }
//        },1000);
//
//    }
//
//    private void setToken(){
//        bottomMsg.setText("写入Token");
////        deviceList.setVisibility(View.GONE);
//        mHandler = new Handler();
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
////                startTest();
//            }
//        },1000);
//
//    }

//    private void getMsg(){
//        read = false;
//        write = true;
//        humitureService = getSupportedGattServices(UUID.fromString("226c0000-6476-4566-7562-66734470666d"));
//        humitureCharacteristic = humitureService.getCharacteristic(UUID.fromString("226caa55-6476-4566-7562-66734470666d"));
//        readCharacteristic(humitureCharacteristic);
//        Intent intent = new Intent();
////        Humiture = intent.getStringExtra(MainActivity.HUMITURE_DATA);
////                Log.d("Humiture",Humiture);
////        startTest();
//    }

    private void startTest(){
        Intent intent = new Intent(MainActivity.this,DeviceMsgActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("name",mDeviceList.get(0).getDevices().getName());
        bundle.putString("address",mDeviceList.get(0).getDevices().getAddress());
        bundle.putInt("rssi",mDeviceList.get(0).getRssis());
        bundle.putString("token","token");
//        Log.d("company",CompanyName);
        bundle.putString("company",CompanyName);
        bundle.putString("humiture",Humiture);
        intent.putExtras(bundle);
//        finish();
        startActivity(intent);
    }


    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            String action = intent.getAction();
            String company;
            BluetoothDevice device;
            device = mDeviceList.get(0).getDevices();
//            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (MainActivity.ACTION_GATT_CONNECTED.equals(action)){
                bottomMsg.setText("Connected");
                Log.d("State","Connected");

            }else if (MainActivity.ACTION_GATT_DISCONNECTED.equals(action)){
                bottomMsg.setText("Error");
                Log.d("State","DisConnected");

            }else if (MainActivity.ACTION_GATT_SERVICES_DISCOVERED.equals(action)){
                Log.d("State","Discovered");
//
                humitureService = getSupportedGattServices(UUID.fromString("226c0000-6476-4566-7562-66734470666d"));
                humitureCharacteristic = humitureService.getCharacteristic(UUID.fromString("226caa55-6476-4566-7562-66734470666d"));
                mBluetoothGatt.setCharacteristicNotification(humitureCharacteristic,true);
                for (BluetoothGattDescriptor descriptor: humitureCharacteristic.getDescriptors()){
                    if (descriptor!=null){
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


//                companyService = getSupportedGattServices(UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb"));
//                companyCharacteristic = companyService.getCharacteristic(UUID.fromString("00002a29-0000-1000-8000-00805f9b34fb"));
//                readCharacteristic(companyCharacteristic);



            }else if (MainActivity.ACTION_DATA_AVAILABLE.equals(action)){
                Log.d("State","DataAvailable");
//                CompanyName = intent.getStringExtra(MainActivity.COMPANY_DATA);
//                if (write) {
                Humiture = intent.getStringExtra(MainActivity.HUMITURE_DATA);
//                }
//                if (read) {
//                    try {
//                        Thread.sleep(1000);
//                        getMsg();
//
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
                startTest();
                mBluetoothGatt.setCharacteristicNotification(humitureCharacteristic,false);
            }else if (MainActivity.ACTION_DATA_WRITE.equals(action)){

                Log.d("State","DataWrite");
            }
        }
    };


    public BluetoothGattService getSupportedGattServices(UUID uuid) {
        BluetoothGattService mBluetoothGattService;
        if (mBluetoothGatt == null) return null;
        mBluetoothGattService=mBluetoothGatt.getService(uuid);
        return mBluetoothGattService;
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        if(mBluetoothGatt !=null) {
            mBluetoothGatt.disconnect();
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mBluetoothGatt.close();
        }
//        deviceList.setAdapter(ListViewAdapter);
    }

    BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, int status,int newState){
            super.onConnectionStateChange(gatt,status,newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
//                gatt.discoverServices();
                Log.d("Connected", "onConnected");
                broadcastUpdate(ACTION_GATT_CONNECTED);
                mBluetoothGatt.discoverServices();
            }else {
                Log.d("Connected" ,"DisConnected: "+ status);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status){
            if (status == BluetoothGatt.GATT_SUCCESS){
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
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
//            if (characteristic == deviceNameCharacteristic) {
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic,HUMITURE_DATA);
            byte[] data = characteristic.getValue();
            Log.d("onCharacteristicChanged", "data: " + data.toString());

//            }else if (characteristic == humitureCharacteristic){
//                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic, HUMITURE_DATA);
//            }
//            Log.d("ValueChanged", String.valueOf(characteristic.getValue()));
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic,int status) {
//            gatt.writeCharacteristic(characteristic);
            broadcastUpdate(ACTION_DATA_WRITE, characteristic);
            Log.d("State","CharWrite");
//            startDmpTest();
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status){

        }

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
        final Intent intent = new Intent(action);
        final byte[] data = characteristic.getValue();

        Log.d("action",action);
        if (data!=null && data.length>0){
            final StringBuilder stringBuilder = new StringBuilder(data.length);
            for (byte byteChar : data){
                stringBuilder.append(String.format("%02X",byteChar));
            }
            intent.putExtra(message,new String(data) + "\n");
            if (characteristic == humitureCharacteristic){
//                HUMITURE_DATA = intent.getStringExtra(message);
                Log.d("humi",HUMITURE_DATA);
            }
            for (int i = 0;i<data.length;i++){
                String hex = Integer.toHexString(data[i]&0xFF);
                if (hex.length() == 1){
                    hex = '0'+hex;
                }
                Log.d("HHHHHH",hex);
            }
            Log.d("VVVVVV", String.valueOf(data));
            Log.d("FFFFFF", intent.getStringExtra(message));

        }
        sendBroadcast(intent);
    }

    public void readCharacteristic(BluetoothGattCharacteristic characteristic){

         mBluetoothGatt.readCharacteristic(characteristic);

    }

    public static void writeCharacteristic(BluetoothGattCharacteristic characteristic){

        mBluetoothGatt.writeCharacteristic(characteristic);
    }

    public static void stopBleConnect(){
        mBluetoothGatt.disconnect();
    }

    public static void getHumiMsg(){
        BluetoothGattService humitureService;
        BluetoothGattCharacteristic humitureCharacteristic;

        humitureService = mBluetoothGatt.getService(UUID.fromString("226c0000-6476-4566-7562-66734470666d"));
        humitureCharacteristic = humitureService.getCharacteristic(UUID.fromString("226caa55-6476-4566-7562-66734470666d"));
        mBluetoothGatt.setCharacteristicNotification(humitureCharacteristic,true);
        for (BluetoothGattDescriptor descriptor: humitureCharacteristic.getDescriptors()){
            if (descriptor!=null){
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

    public void logout(){
        Intent logoutIntent = new Intent(this,LoginActivity.class);
        logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(logoutIntent);
    }


}
