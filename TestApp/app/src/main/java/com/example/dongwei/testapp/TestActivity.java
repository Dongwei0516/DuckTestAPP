package com.example.dongwei.testapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dongwei on 2017/3/8.
 */

public class TestActivity extends Activity{

    private NumberPicker temMin,temMax,humiMin,humiMax,volMin,volMax;
    private ImageView temImg,humiImg,volImg;
    private Button failBtn,finishBtn,startBtn,restartBtn,dmpBtn;
    private TextView bottomMsgTest;
    private TestListViewAdapter mTextListViewAdapter;
    private List<TestMsg> mTestList = new ArrayList<TestMsg>();
    private ListView mTestListView;
    private int temMinDefault = 10, temMaxDefault = 25;
    private int humiMinDefault = 30, humiMaxDefault = 60;
    private int volMinDefault = 1, volMaxDefault = 3;
    private double volMinValue, volMaxValue;
    private Context context;

    private int testTemValue = 29;
    private int testHumiValue = 22;
    private double testVolValue = 1.7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work);

        temMin = (NumberPicker)findViewById(R.id.tempLow);
        temMax = (NumberPicker)findViewById(R.id.tempHigh);
        humiMin = (NumberPicker)findViewById(R.id.humiLow);
        humiMax = (NumberPicker)findViewById(R.id.humiHigh);
        volMin = (NumberPicker)findViewById(R.id.volLow);
        volMax = (NumberPicker)findViewById(R.id.volHigh);

        temImg = (ImageView)findViewById(R.id.imgTem);
        humiImg = (ImageView)findViewById(R.id.imgHumi);
        volImg = (ImageView)findViewById(R.id.imgVol);

        startBtn = (Button)findViewById(R.id.button5);
        failBtn = (Button)findViewById(R.id.button6);
        finishBtn = (Button)findViewById(R.id.button7);
        restartBtn = (Button)findViewById(R.id.button8);
        dmpBtn = (Button)findViewById(R.id.button14);

        mTestListView = (ListView)findViewById(R.id.test_msg);
        bottomMsgTest = (TextView)findViewById(R.id.bottom_msg_test);

        temMin.setMinValue(0);
        temMin.setMaxValue(40);
        temMin.setValue(temMinDefault);
        temMin.setWrapSelectorWheel(false);
        temMin.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                temMinDefault = newVal;
            }
        });

        temMax.setMinValue(0);
        temMax.setMaxValue(40);
        temMax.setValue(temMaxDefault);
        temMax.setWrapSelectorWheel(false);
        temMax.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                temMaxDefault = newVal;
            }
        });

        humiMin.setMinValue(10);
        humiMin.setMaxValue(80);
        humiMin.setValue(humiMinDefault);
        humiMin.setWrapSelectorWheel(false);
        humiMin.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                humiMinDefault = newVal;
            }
        });

        humiMax.setMinValue(10);
        humiMax.setMaxValue(80);
        humiMax.setValue(humiMaxDefault);
        humiMax.setWrapSelectorWheel(false);
        humiMax.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                humiMaxDefault = newVal;
            }
        });

        volMin.setMinValue(0);
        volMin.setMaxValue(30);
        volMin.setDisplayedValues(new String[]{"0.0","0.1","0.2","0.3","0.4","0.5","0.6","0.7","0.8","0.9","1.0","1.1","1.2","1.3","1.4","1.5","1.6","1.7",
                                                 "1.8","1.9","2.0","2.1","2.2","2.3","2.4","2.5","2.6","2.7","2.8","2.9","3.0"});
        volMin.setValue(10);
        volMin.setWrapSelectorWheel(false);
        volMinValue = volMin.getValue()/10.0;

        volMax.setMinValue(0);
        volMax.setMaxValue(30);
        volMax.setDisplayedValues(new String[]{"0.0","0.1","0.2","0.3","0.4","0.5","0.6","0.7","0.8","0.9","1.0","1.1","1.2","1.3","1.4","1.5","1.6","1.7",
                "1.8","1.9","2.0","2.1","2.2","2.3","2.4","2.5","2.6","2.7","2.8","2.9","3.0"});
        volMax.setValue(20);
        volMax.setWrapSelectorWheel(false);
        volMaxValue = volMax.getValue()/10.0;



        TestMsg testMsgA = new TestMsg(R.drawable.waitting,"等待温度测试..");
        TestMsg testMsgB = new TestMsg(R.drawable.waitting,"等待湿度测试..");
        TestMsg testMsgC = new TestMsg(R.drawable.waitting,"等待电压测试..");
        mTestList.add(testMsgA);
        mTestList.add(testMsgB);
        mTestList.add(testMsgC);
        Log.d("111", String.valueOf(mTestList.size()));

        mTextListViewAdapter = new TestListViewAdapter(mTestList,context);
        mTestListView.setAdapter(mTextListViewAdapter);

        bottomMsgTest.setText("等待测试..");

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBtn.setVisibility(View.GONE);
                onTest();
            }
        });

        restartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
//                onTest();
            }
        });

        dmpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //段码屏测试
            }
        });

        failBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //未通过测试
                Toast.makeText(getApplicationContext(),"向服务器汇报数据",Toast.LENGTH_SHORT).show();

            }
        });

        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //通过测试
                Toast.makeText(getApplicationContext(),"向服务器汇报数据",Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void onTest(){

        int temMinValue,temMaxValue;
        int humiMinValue,humiMaxValue;
        double volMinValue,volMaxValue;
        boolean testPass = true;

        temMinValue = temMin.getValue();
        temMaxValue = temMax.getValue();
        humiMinValue = humiMin.getValue();
        humiMaxValue = humiMax.getValue();
        volMinValue = volMin.getValue()/10.0;
        volMaxValue = volMax.getValue()/10.0;

        if(testTemValue>temMinValue && testTemValue<temMaxValue){
            mTestList.get(0).setImage(R.drawable.done);
            mTestList.get(0).setText("温度测试通过, Temp="+testTemValue);
        }else {
            mTestList.get(0).setImage(R.drawable.error);
            mTestList.get(0).setText("温度测试未通过, Temp="+testTemValue);
            testPass = false;
        }

        if (testHumiValue>humiMinValue && testHumiValue<humiMaxValue){
            mTestList.get(1).setImage(R.drawable.done);
            mTestList.get(1).setText("湿度测试通过, Humi="+testHumiValue);
        }else {
            mTestList.get(1).setImage(R.drawable.error);
            mTestList.get(1).setText("湿度测试未通过, Humi=" + testHumiValue);
            testPass = false;
        }

        if (testVolValue>volMinValue && testVolValue<volMaxValue){
            mTestList.get(2).setImage(R.drawable.done);
            mTestList.get(2).setText("电压测试通过, Vol=" + testVolValue);
        }else {
            mTestList.get(2).setImage(R.drawable.error);
            mTestList.get(2).setText("电压测试未通过, Vol=" + testVolValue);
            testPass = false;
        }

        mTestListView.setAdapter(mTextListViewAdapter);

        if (testPass ){
            doneTestMsg();
        }else{
            failTestMsg();
        }


    }

    private void failTestMsg(){
        bottomMsgTest.setText("测试未通过！");
        failBtn.setVisibility(View.VISIBLE);
        failBtn.setText("测试结束");
        restartBtn.setVisibility(View.VISIBLE);
    }

    private void doneTestMsg(){
        bottomMsgTest.setText("所有测试项目通过！");
        finishBtn.setVisibility(View.VISIBLE);
        finishBtn.setText("测试结束");
        restartBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
//        Intent intent = getIntent();
//        finish();
//        startActivity(intent);
    }

    public class TestMsg {
        private int image;
        private String text;

        public TestMsg(int image,String text){
            this.image = image;
            this.text = text;
        }

        public void setImage(int image){
            this.image = image;
        }

        public void setText(String text){
            this.text = text;
        }

        public int getImage(){
            return image;
        }

        public String getText(){
            return text;
        }
    }

    public class TestListViewAdapter extends BaseAdapter {

        private LayoutInflater layoutInflater;

        public TestListViewAdapter(List<TestMsg> testMsgList, Context context){
            mTestList = testMsgList;
            layoutInflater = LayoutInflater.from(TestActivity.this);
        }

        @Override
        public int getCount() {
            return mTestList.size();
        }

        @Override
        public Object getItem(int position) {
            return mTestList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView ==null){
                viewHolder = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.test_list_item,null);
                viewHolder.imageView = (ImageView)convertView.findViewById(R.id.stateIv);
                viewHolder.textView = (TextView)convertView.findViewById(R.id.stateTv);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (ViewHolder)convertView.getTag();
            }

            viewHolder.imageView.setImageResource(mTestList.get(position).getImage());
            viewHolder.textView.setText(mTestList.get(position).getText());
            return convertView;
        }

        class ViewHolder{
            ImageView imageView;
            TextView textView;
        }
    }

}
