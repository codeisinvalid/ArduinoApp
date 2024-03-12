package com.example.arduinoapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice bluetoothDevice;
    BluetoothSocket bluetoothSocket;
    OutputStream outputStream;
    Button bluetoothButton;
    ImageButton upButton, downButton, leftButton, rightButton, stopButton;
    private boolean isConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bluetoothButton = findViewById(R.id.bluetoothBtn);
        upButton = findViewById(R.id.upButton);
        downButton = findViewById(R.id.downButton);
        rightButton = findViewById(R.id.rightButton);
        leftButton = findViewById(R.id.leftButton);
        stopButton = findViewById(R.id.stopButton);

        bluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectBluetooth();
            }
        });

        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData("U");
            }
        });

        downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData("D");
            }
        });

        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData("L");
            }
        });

        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData("R");
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendData("S");
            }
        });
    }

    private void connectBluetooth() {
        if (!isConnected){

            if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.BLUETOOTH, android.Manifest.permission.BLUETOOTH_ADMIN}, 100);
                return;
            }

            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter == null) {
                showToast("Bluetooth not supported");
                return;
            }else System.out.println(bluetoothAdapter.getBondedDevices());

            if (!bluetoothAdapter.isEnabled()) {
                showToast("Bluetooth is not enabled");
                return;
            }

            bluetoothDevice = bluetoothAdapter.getRemoteDevice("00:22:03:01:01:52"); // Replace with your HC-05 MAC address
            if (bluetoothDevice == null) {
                showToast("Bluetooth device not found");
                return;
            } else System.out.println(bluetoothDevice.getName());

            try {
                bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(mUUID);
                bluetoothSocket.connect();
                outputStream = bluetoothSocket.getOutputStream();
                showToast(bluetoothDevice.getName()+" connected");
                bluetoothButton.setText("Disconnect");
                isConnected = true;
            } catch (IOException e) {
                e.printStackTrace();
                showToast("Failed to connect Bluetooth");
            }

        } else {
            try {
                if (bluetoothSocket != null) {
                    bluetoothSocket.close();
                    outputStream = null;
                    showToast("Bluetooth disconnected");
                    bluetoothButton.setText("Connect"); // Change button text to "Connect"
                    isConnected = false; // Update connection status
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    private void sendData(String data) {
        if (outputStream != null) {
            try {
                outputStream.write(data.getBytes());
                showToast("Data sent: " + data);
            } catch (IOException e) {
                e.printStackTrace();
                showToast("Failed to send data");
            }
        } else {
            showToast("Bluetooth not connected");
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
