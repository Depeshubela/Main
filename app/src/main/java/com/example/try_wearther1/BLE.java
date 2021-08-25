package com.example.letgo;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Binder;

import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.Vector;

public class BLE extends Service {
    private String name, address;
    private BluetoothAdapter bluetoothAdapter;
    private final UUID serialPortUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothSocket socket;
    private OutputStream outputStream;




    public void onCreate() {
        super.onCreate();

    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final String deviceName = intent.getStringExtra("DeviceName");
        final String deviceAddress = intent.getStringExtra("DeviceAddress");

        name = deviceName != null ? deviceName : "裝置名稱未顯示";
        address = deviceAddress;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        final BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);

        try {
            socket = device.createRfcommSocketToServiceRecord(serialPortUUID);
            socket.connect();
            //inputStream = socket.getInputStream();
            //outputStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return START_STICKY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //mHandler = getApplication().getHandler();
        return null;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

    }
    public void sendWeather() {
        //if (outputStream == null) return;

        try {
            outputStream.write("weather".getBytes());
            //outputStream.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
