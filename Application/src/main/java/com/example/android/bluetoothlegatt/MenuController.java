package com.example.android.bluetoothlegatt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MenuController extends AppCompatActivity {

    private final static String TAG = MenuController.class.getSimpleName();
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    private String mDeviceName;
    private String mDeviceAddress;
    private Button rcController;
    private Button obstacleController;
    private Button vocalController;
    private Button danceController;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_controller);

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        rcController = findViewById(R.id.btnRC);
        obstacleController = findViewById(R.id.btnOVC);
        vocalController = findViewById(R.id.btnVC);
        danceController = findViewById(R.id.btnDC);

        rcController.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent rci = new Intent(MenuController.this, RCBLEController.class);
                rci.putExtra(EXTRAS_DEVICE_NAME, mDeviceName);
                rci.putExtra(EXTRAS_DEVICE_ADDRESS, mDeviceAddress);
                startActivity(rci);

            }
        });

        obstacleController.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent oai = new Intent(MenuController.this, OVBLEController.class);
                oai.putExtra(EXTRAS_DEVICE_NAME, mDeviceName);
                oai.putExtra(EXTRAS_DEVICE_ADDRESS, mDeviceAddress);
                startActivity(oai);
            }
        });

        vocalController.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent vci = new Intent(MenuController.this, VCBLEController.class);
                vci.putExtra(EXTRAS_DEVICE_NAME, mDeviceName);
                vci.putExtra(EXTRAS_DEVICE_ADDRESS, mDeviceAddress);
                startActivity(vci);
            }
        });

        danceController.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent dci = new Intent(MenuController.this, DCBLEController.class);
                dci.putExtra(EXTRAS_DEVICE_NAME, mDeviceName);
                dci.putExtra(EXTRAS_DEVICE_ADDRESS, mDeviceAddress);
                startActivity(dci);
            }
        });

    }
}