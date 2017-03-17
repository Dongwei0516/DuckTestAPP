package com.example.dongwei.testapp;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import java.util.UUID;

/**
 * Created by dongwei on 2017/3/16.
 */

public class Utils {

    public static void getTestChar(){
        BluetoothGattService messageService;
        BluetoothGattCharacteristic messageCharacteristic;
        BluetoothGatt mBluetoothGatt = null;

        messageService = mBluetoothGatt.getService(UUID.fromString("226c0000-6476-4566-7562-66734470666d"));
        messageCharacteristic = messageService.getCharacteristic(UUID.fromString("226cbb55-6476-4566-7562-66734470666d"));


    }

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

    public static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }
}
