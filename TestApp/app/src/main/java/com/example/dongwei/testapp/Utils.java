package com.example.dongwei.testapp;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * Created by dongwei on 2017/3/16.
 */

public class Utils {

    public static byte[] hexStringToBytes(String hexString){
        if (hexString == null || hexString.equals("")){
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length()/2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i =0;i<length;i++){
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos+1]));
        }

        return d;
    }

    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <=0){
            return null;
        }
        for (int i = 0; i<src.length; i++){
            int v = src[i] &0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() <2){
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
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


}
