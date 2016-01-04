package com.example.andylab.service_bt;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;


import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.UUID;

public class BTService extends Service  implements SensorEventListener {
    //=========Acceleration=======================
    String ss;
    SensorManager sm;
    Sensor sr;
    float x,y,z;



    //=========BLUETOOTH===================
    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter btAdapter = null;//控制手機藍芽裝置功能，取得手機系統上的藍芽配接器
    private BluetoothSocket btSocket = null;//網路傳輸介面
    private OutputStream outStream = null;//資料傳輸
    // Well known SPP UUID
    private static final UUID MY_UUID =UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    //UUID:通用唯一識別碼，表示特定服務類別。
    // Insert your server's MAC address
    private static String address = "00:14:02:11:18:85";//20:13:12:09:51:37


    private Handler handler = new Handler();

    int t = 0;

    @Override
    public void onCreate() {
        Log.d("PrinterService", "Service started");
        super.onCreate();
        sm = (SensorManager)getSystemService(SENSOR_SERVICE);
        sr = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //btAdapter = BluetoothAdapter.getDefaultAdapter();

        /*if (btAdapter.isEnabled()) {
            // Set up a pointer to the remote node using it's address.
            BluetoothDevice device = btAdapter.getRemoteDevice(address);//��o���ݸ˸m��������T

            // Two things are needed to make a connection:
            //   A MAC address, which we got above.
            //   A Service ID or UUID.  In this case we are using the
            //     UUID for SPP.

            try {
                btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                //errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
            }


            // Discovery is resource intensive.  Make sure it isn't going on
            // when you attempt to connect and pass your message.
            btAdapter.cancelDiscovery();

            // Establish the connection.  This will block until it connects.
            //Log.d(TAG, "...Connecting to Remote...");

            try {
                btSocket.connect();
                //Log.d(TAG, "...Connection established and data link opened...");
            } catch (IOException e) {
                try {
                    btSocket.close();
                } catch (IOException e2) {
                    //errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
                }
            }
            // Create a data stream so we can talk to server.
            //Log.d(TAG, "...Creating Socket...");
            try {
                outStream = btSocket.getOutputStream();
            } catch (IOException e) {
                //errorExit("Fatal Error", "In onResume() and output stream creation failed:" + e.getMessage() + ".");
            }
        }*/



    }

    public BTService() {
    }
    @Override
    public void onStart(Intent intent, int startId) {
        handler.postDelayed(showTime, 2000);
        sm.registerListener(this, sr, SensorManager.SENSOR_DELAY_NORMAL);
        super.onStart(intent, startId);

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        if (btAdapter.isEnabled()) {
            // Set up a pointer to the remote node using it's address.
            BluetoothDevice device = btAdapter.getRemoteDevice(address);//��o���ݸ˸m��������T

            // Two things are needed to make a connection:
            //   A MAC address, which we got above.
            //   A Service ID or UUID.  In this case we are using the
            //     UUID for SPP.

            try {
                btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                //errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
            }


            // Discovery is resource intensive.  Make sure it isn't going on
            // when you attempt to connect and pass your message.
            btAdapter.cancelDiscovery();

            // Establish the connection.  This will block until it connects.
            //Log.d(TAG, "...Connecting to Remote...");

            try {
                btSocket.connect();
                //Log.d(TAG, "...Connection established and data link opened...");
            } catch (IOException e) {
                try {
                    btSocket.close();
                } catch (IOException e2) {
                    //errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
                }
            }
            // Create a data stream so we can talk to server.
            //Log.d(TAG, "...Creating Socket...");
            try {
                outStream = btSocket.getOutputStream();
            } catch (IOException e) {
                //errorExit("Fatal Error", "In onResume() and output stream creation failed:" + e.getMessage() + ".");
            }
        }





    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(showTime);
        super.onDestroy();
        sm.unregisterListener(this);

        if (btAdapter.isEnabled()) {

            if (outStream != null) {
                try {
                    outStream.flush();
                } catch (IOException e) {
                    //errorExit("Fatal Error", "In onPause() and failed to flush output stream: " + e.getMessage() + ".");
                }


                try {
                    btSocket.close();
                } catch (IOException e2) {
                    //errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
                }
            }
        }
    }

    private Runnable showTime = new Runnable() {
        public void run() {
            //log目前時間
            Log.i("time:", new Date().toString());
            handler.postDelayed(this, 500);

            //t = t + 1;
            if(x>3){
                sendData((byte)4);
            }
            else if(x<-3){
                sendData((byte)6);
            }
            else{
                sendData((byte)5);
            }
            //sendData((byte)4);
            //Toast msg = Toast.makeText(getBaseContext(), ss, Toast.LENGTH_SHORT);
            //msg.show();


        }
    };

    private void sendData(byte message) {
        //byte[] msgBuffer = message.getBytes();

        //Log.d(TAG, "...Sending data: " + message + "...");

        try {
            outStream.write(message);
        } catch (IOException e) {
            Toast msg1 = Toast.makeText(getBaseContext(), "nosend", Toast.LENGTH_SHORT);
            msg1.show();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //ss =String.format("X軸: %1.2f\nY軸: %1.2f\nZ軸: %1.2f\n",event.values[0], event.values[1],event.values[2]);
        ss =String.format("X軸: %1.2f\nY軸: %1.2f\nZ軸: %1.2f\n",x,y,z);
        x = event.values[0];
        y = event.values[1];
        z = event.values[2];

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
