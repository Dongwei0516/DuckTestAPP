package com.example.dongwei.testapp;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.aztec.encoder.Encoder;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;

/**
 * Created by dongwei on 2017/3/16.
 */

public class DmpTestActivity extends AppCompatActivity{

    private Button newTestBtn;
    private String address;
    private ImageView codeImg;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dmp_test);

        newTestBtn = (Button)findViewById(R.id.newTestBtn);
        codeImg = (ImageView)findViewById(R.id.codeImg);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        address = bundle.getString("address");


        if (address!=null && !address.equals("")){
            try {
                bitmap = CreateCodeImg("MacAddress:"+address);
            } catch (WriterException e) {
                e.printStackTrace();
            }
        }

        if (bitmap != null){
            codeImg.setVisibility(View.VISIBLE);
            codeImg.setImageBitmap(bitmap);
        }

        newTestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DmpTestActivity.this,MainActivity.class);
                DeviceMsgActivity.stopBleConnect();
                finish();
                startActivity(intent);
            }
        });

    }

    public Bitmap CreateCodeImg(String string) throws WriterException{
        BitMatrix matrix = new MultiFormatWriter().encode(string, BarcodeFormat.CODE_128,1200,800);
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
