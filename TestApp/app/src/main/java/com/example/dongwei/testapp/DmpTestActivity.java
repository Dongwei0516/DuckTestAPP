package com.example.dongwei.testapp;

import android.app.Activity;
import android.bluetooth.BluetoothGatt;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

/**
 * Created by dongwei on 2017/3/16.
 */

public class DmpTestActivity extends Activity {

    private Button testPassBtn, testFailBtn, printBtn, newTestBtn;
    private String address;
    private ImageView codeImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dmp_test);

        testPassBtn = (Button)findViewById(R.id.button16);
        testFailBtn = (Button)findViewById(R.id.button17);
        printBtn = (Button)findViewById(R.id.button18);
        newTestBtn = (Button)findViewById(R.id.button19);
        codeImg = (ImageView)findViewById(R.id.codeImg);

        codeImg.bringToFront();

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        address = bundle.getString("address");

        testPassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                testPass();
                MainActivity.stopDmpTest();
                Toast.makeText(getApplicationContext(),"向服务器反馈数据",Toast.LENGTH_SHORT).show();
                testPassBtn.setVisibility(View.INVISIBLE);
                testFailBtn.setVisibility(View.INVISIBLE);
                printBtn.setVisibility(View.VISIBLE);
                newTestBtn.setVisibility(View.VISIBLE);
            }
        });

        testFailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                testFail();
                MainActivity.stopDmpTest();
                Toast.makeText(getApplicationContext(),"向服务器反馈数据",Toast.LENGTH_SHORT).show();
                testPassBtn.setVisibility(View.INVISIBLE);
                testFailBtn.setVisibility(View.INVISIBLE);
                printBtn.setVisibility(View.VISIBLE);
                newTestBtn.setVisibility(View.VISIBLE);
            }
        });

        printBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bitmap bitmap = null;
                try{
                    if (address!=null && !address.equals("")){
                        bitmap = CreateCodeImg(address);
                    }
                }catch (WriterException e){
                    e.printStackTrace();
                }

                if (bitmap != null){
                    codeImg.setVisibility(View.VISIBLE);
                    codeImg.setImageBitmap(bitmap);
                }
            }
        });

        newTestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DmpTestActivity.this,MainActivity.class);
                MainActivity.stopBleConnect();
                finish();
                startActivity(intent);
            }
        });

    }


    private void testPass(){

    }

    private void testFail(){

    }

    public Bitmap CreateCodeImg(String string) throws WriterException{
        BitMatrix matrix = new MultiFormatWriter().encode(string, BarcodeFormat.CODE_128,700,400);
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = 0xff000000;
                }
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels,0,width,0,0,width,height);

        return bitmap;
    }
}
