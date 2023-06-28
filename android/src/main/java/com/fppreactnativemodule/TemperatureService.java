
package com.fppreactnativemodule;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Debug;
import android.os.SystemClock;
import android.serialport.SerialPort;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class TemperatureService extends IntentService {

    private static final String TAG = "TemperatureService";
    private static float temperature = 0f;
    int temperatureServiceInterval = 120;
    int temperatureServiceCounter = temperatureServiceInterval;

    SerialPort serialPort;

    TemperatureCameraThread mTemperatureCameraThread;

    InputStream temperatureInput = null;
    OutputStream temperatureOutput = null;

    SharedPreferences sharedPref;
    String version;

    public TemperatureService() {
        super(TAG);
    }

    public static void startService(Context context) {
        context.startService(new Intent(context, TemperatureService.class));
    }

    public static float getTemperature() {
        return temperature;
    }

    public static void stopService(Context context) {
        context.stopService(new Intent(context, TemperatureService.class));
    }

    public void onHandleIntent(@Nullable Intent intent) {
        init();
    }

    void init() {

        sharedPref = getSharedPreferences("user", Context.MODE_PRIVATE);
        version = sharedPref.getString("version", "v2");

        try {
            SerialPort.setSuPath("/system/xbin/su");

            if(version.equals("v2")) {
                serialPort = new SerialPort(new File("/dev/ttyS9"), 115200);
            } else {
                serialPort = new SerialPort(new File("/dev/ttyS1"), 115200);
            }

            temperatureOutput = serialPort.getOutputStream();
            temperatureInput = serialPort.getInputStream();

            mTemperatureCameraThread = new TemperatureCameraThread();
            mTemperatureCameraThread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private class TemperatureCameraThread extends Thread {
        boolean isInterrupt;

        @Override
        public void run() {
            while (!isInterrupt) {
                try {

                    if(version.equals("v2")) {
                        byte[] buffer = new byte[32];
                        int size = temperatureInput.read(buffer);
                        String raw = new String(buffer, 0, size);
                        String str = raw.substring(raw.indexOf("{")+1, raw.indexOf("}"));
                        temperature = Float.parseFloat(str);
                    } else {
                        byte[] READ_TEMPERATURE_CMD = {-91, 85, 1, -5};
                        temperatureOutput.write(READ_TEMPERATURE_CMD);

                        byte[] buffer = new byte[32];
                        int size = temperatureInput.read(buffer);

                        String hexString = bytesToHex(buffer, 0, size);
                        String a = hexString.substring(4, 6);
                        String b = hexString.substring(6, 8);

                        temperature = convertTemperature(a, b);
                    }

                    //Log.d(TAG, "received " + temperature);
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void interrupt() {
            isInterrupt = true;
            super.interrupt();
        }

    }

    public static String byte2Hex(Byte inByte) {
        return String.format("%02x", new Object[]{inByte}).toUpperCase();
    }

    public static String bytesToHex(byte[] inBytArr, int offset, int byteCount) {
        StringBuilder strBuilder = new StringBuilder();
        int j = byteCount;
        for (int i = offset; i < j; i++) {
            strBuilder.append(byte2Hex(Byte.valueOf(inBytArr[i])));
        }
        return strBuilder.toString();
    }

    private float convertTemperature(String a, String b) {
        return ((float) ((Integer.parseInt(b, 16) * 256) + Integer.parseInt(a, 16))) / 100.0f;
    }

}
