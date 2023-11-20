package com.fppreactnativemodule.utils;

import android.os.Build;

import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Method;

public class Helper {
    public static String getSerialNumber() {
JSONObject serialNumbers = new JSONObject();

   try {
        Class<?> c = Class.forName("android.os.SystemProperties");
        Method get = c.getMethod("get", String.class);

        String gsmSn1 = (String) get.invoke(c, "gsm.sn1");
        serialNumbers.put("gsm.sn1", gsmSn1);

        String persistRoSerialNo = (String) get.invoke(c, "persist.ro.serialno");
        serialNumbers.put("persist.ro.serialno", persistRoSerialNo);

        String rilSerialNumber = (String) get.invoke(c, "ril.serialnumber");
        serialNumbers.put("ril.serialnumber", rilSerialNumber);

        String roSerialNo = (String) get.invoke(c, "ro.serialno");
        serialNumbers.put("ro.serialno", roSerialNo);

        String sysSerialNumber = (String) get.invoke(c, "sys.serialnumber");
        serialNumbers.put("sys.serialnumber", sysSerialNumber);

        String buildSerial = Build.SERIAL;
        serialNumbers.put("Build.SERIAL", buildSerial);

    } catch (Exception e) {
        e.printStackTrace();
    }

    return serialNumbers.toString();
    }

}
