package com.example.dongwei.testapp;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by dongwei on 2017/2/23.
 */

public class ClsUtils {

    public static boolean createBond(Class btClass, BluetoothDevice btDevice) throws Exception{
        Method createBondMethod = btClass.getMethod("createBond");
        Boolean returnValue = (Boolean)createBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }

    public static boolean removeBond(Class<?> btClass, BluetoothDevice btDevice) throws Exception{
        Method removeBondMethod = btClass.getMethod("removeBond");
        Boolean returnValue = (Boolean)removeBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }

    public static boolean setPin(Class<? extends BluetoothDevice> btClass, BluetoothDevice btDevice, String str) throws Exception{
        try{
            Method removeBondMethod = btClass.getDeclaredMethod("setPin", new Class[]{byte[].class});
            Boolean returnValue = (Boolean)removeBondMethod.invoke(btDevice, new Object[]{str.getBytes()});
            Log.d("returnValue ", String.valueOf(returnValue));
        }catch (SecurityException e){
            e.printStackTrace();
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

    public static boolean cancelPairingUserInput(Class<?> btClass, BluetoothDevice device)throws Exception{
        Method createBondMethod = btClass.getMethod("cancelPairingUserInput");
        Boolean returnValue = (Boolean)createBondMethod.invoke(device);
        return returnValue.booleanValue();
    }

    public static boolean cancelBondProcress(Class<?> btClass,BluetoothDevice device)throws Exception{
        Method createBondMethod = btClass.getMethod("cancelBondProcress");
        Boolean returnValue = (Boolean)createBondMethod.invoke(device);
        return returnValue.booleanValue();
    }

    public static void setPairingConfirmation(Class<?> btClass, BluetoothDevice device,boolean isConfirm)throws Exception{
        Method setPairingConfirmation = btClass.getDeclaredMethod("setPairingConfirmation", boolean.class);
        setPairingConfirmation.invoke(device,isConfirm);
    }

    public static void printAllInform(Class clsShow){
        try {
            Method[] hideMethod = clsShow.getMethods();
            int i = 0;
            for (;i<hideMethod.length;i++){
                Log.d("method name", hideMethod[i].getName()+";i: "+i);
            }
            Field[] allFields = clsShow.getFields();
            for (i=0;i<allFields.length;i++){
                Log.d("field name",allFields[i].getName());
            }
        }catch (SecurityException e){
            e.printStackTrace();
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
