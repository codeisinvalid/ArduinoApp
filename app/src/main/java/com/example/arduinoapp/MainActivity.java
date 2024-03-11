package com.example.arduinoapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;


import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice bluetoothDevice;
    BluetoothSocket bluetoothSocket;
    OutputStream outputStream;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT > 31) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.BLUETOOTH_CONNECT}, 100);
                return;
            }
        }


        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        System.out.println(bluetoothAdapter.getBondedDevices());


        BluetoothDevice hc05 = bluetoothAdapter.getRemoteDevice("00:22:03:01:01:52");
        System.out.println(hc05.getName());

        bluetoothSocket = null;
        int counter = 0;
        do {
            try {
                bluetoothSocket = hc05.createRfcommSocketToServiceRecord(mUUID);
                System.out.println(bluetoothSocket);
                bluetoothSocket.connect();
                System.out.println(bluetoothSocket.isConnected());
            } catch (IOException e) {
                e.printStackTrace();
            }
            counter++;
        }

        while (!bluetoothSocket.isConnected() && counter < 3);
        try {
            outputStream = bluetoothSocket.getOutputStream();
            outputStream.write(48);
        } catch (IOException e) {
            e.printStackTrace();
        }

        InputStream inputStream = null;
        try {
            inputStream = bluetoothSocket.getInputStream();
            inputStream.skip(inputStream.available());

            for (int i = 0; i < 26; i++) {

                byte b = (byte) inputStream.read();
                System.out.println((char) b);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            bluetoothSocket.close();
            System.out.println(bluetoothSocket.isConnected());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}