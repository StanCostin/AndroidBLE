package com.example.android.bluetoothlegatt;

import android.Manifest;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static com.example.android.bluetoothlegatt.BluetoothLeService.UUID_BLE;

public class RCBLEController extends AppCompatActivity {

    private final static String TAG = RCBLEController.class.getSimpleName();
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    private ImageButton upBtn;
    private ImageButton dwnBtn;
    private ImageButton lBtn;
    private ImageButton rBtn;
    private ImageButton flBtn;
    private ImageButton blBtn;
    private ImageButton hBtn;
    private ImageButton eBtn;
    private ImageButton discBtn;
    private ImageButton backBtn;
    private TextView mConnectionState;
    private TextView mDataField;
    private EditText mEditField;
    private String mDeviceName;
    private String mDeviceAddress;
    private ExpandableListView mGattServicesList;
    private BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false;
    private int counter = 0;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    private BluetoothGattCharacteristic bluetoothGattCharacteristicBLE;
    private boolean isClickedfl, isClickedbl, isClickedec = false;

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            } else {
                Log.e(TAG, "Muie ba!merge!");
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }

    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                //clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rcblecontroller);

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        upBtn = findViewById(R.id.upArrowId);
        dwnBtn = findViewById(R.id.downArrowId);
        lBtn = findViewById(R.id.leftArrowId);
        rBtn = findViewById(R.id.rightArrowId);
        flBtn = findViewById(R.id.frontLightsId);
        blBtn = findViewById(R.id.backLightsId);
        hBtn = findViewById(R.id.hornId);
        eBtn = findViewById(R.id.emergencyId);
        discBtn = findViewById(R.id.disconnectId);
        backBtn = findViewById(R.id.backButtonId);


        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        upBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                {
                    String value = "W";
                    mBluetoothLeService.writeCharacteristic(bluetoothGattCharacteristicBLE, value);
                    return false;
                }
                else if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    String value = "w";
                    mBluetoothLeService.writeCharacteristic(bluetoothGattCharacteristicBLE, value);
                }

                return false;
            }



        });

        dwnBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                {
                    String value = "S";
                    mBluetoothLeService.writeCharacteristic(bluetoothGattCharacteristicBLE, value);
                    return false;
                }
                else if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    String value = "s";
                    mBluetoothLeService.writeCharacteristic(bluetoothGattCharacteristicBLE, value);
                }

                return false;
            }
        });

        lBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                {
                    String value = "A";
                    mBluetoothLeService.writeCharacteristic(bluetoothGattCharacteristicBLE, value);
                    return false;
                }
                else if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    String value = "l";
                    mBluetoothLeService.writeCharacteristic(bluetoothGattCharacteristicBLE, value);
                }

                return false;
            }
        });

        rBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                {
                    String value = "D";
                    mBluetoothLeService.writeCharacteristic(bluetoothGattCharacteristicBLE, value);
                    return false;
                }
                else if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    String value = "r";
                    mBluetoothLeService.writeCharacteristic(bluetoothGattCharacteristicBLE, value);
                }

                return false;
            }
        });

        hBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                {
                    String value = "H";
                    mBluetoothLeService.writeCharacteristic(bluetoothGattCharacteristicBLE, value);
                    return false;
                }
                else if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    String value = "h";
                    mBluetoothLeService.writeCharacteristic(bluetoothGattCharacteristicBLE, value);
                }

                return false;
            }
        });

        hBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counter++;
                if(counter >= 1) {
                    String value = "h";
                    mBluetoothLeService.writeCharacteristic(bluetoothGattCharacteristicBLE, value);
                }
            }
        });

        flBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isClickedfl == false) {
                    counter = 0;
                    String value = "F";
                    mBluetoothLeService.writeCharacteristic(bluetoothGattCharacteristicBLE, value);
                    isClickedfl = true;
                }else {
                    String value = "f";
                    mBluetoothLeService.writeCharacteristic(bluetoothGattCharacteristicBLE, value);
                    isClickedfl = false;
                }
            }
        });

        blBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isClickedbl == false) {
                    String value = "B";
                    mBluetoothLeService.writeCharacteristic(bluetoothGattCharacteristicBLE, value);
                    isClickedbl = true;
                }else {
                    String value = "b";
                    mBluetoothLeService.writeCharacteristic(bluetoothGattCharacteristicBLE, value);
                    isClickedbl = false;
                }
            }
        });

        eBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isClickedec == false) {
                    String value = "E";
                    mBluetoothLeService.writeCharacteristic(bluetoothGattCharacteristicBLE, value);
                    isClickedec = true;
                }else {
                    String value = "e";
                    mBluetoothLeService.writeCharacteristic(bluetoothGattCharacteristicBLE, value);
                    isClickedec = false;
                }
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBackRCMC();
            }
        });

    }

    private void goBackRCMC() {
        mBluetoothLeService.disconnect();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
        Intent rcmc = new Intent(this, MenuController.class);
        rcmc.putExtra(EXTRAS_DEVICE_NAME, mDeviceName);
        rcmc.putExtra(EXTRAS_DEVICE_ADDRESS, mDeviceAddress);
        startActivity(rcmc);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_connect:
                mBluetoothLeService.connect(mDeviceAddress);
                return true;
            case R.id.menu_disconnect:
                mBluetoothLeService.disconnect();
                checkAddresses();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkAddresses() {

        Intent si = new Intent(this, DeviceScanActivity.class);
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
        startActivity(si);
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //mConnectionState.setText(resourceId);
            }
        });
    }
//
    private void displayData(String data) {
        if (data != null) {
            //mDataField.setText(data);
        }
    }
//
//    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
//    // In this sample, we populate the data structure that is bound to the ExpandableListView
//    // on the UI.
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(
                    LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(
                        LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);

                //Check if it is "BLE"
                Log.e("UID",uuid);


                if(uuid.equals(SampleGattAttributes.L)){
                    Log.e("OK","YES");
                    bluetoothGattCharacteristicBLE = gattService.getCharacteristic(UUID_BLE);
                    Log.e("s",bluetoothGattCharacteristicBLE.toString());
                }
                else{
                    Log.e("OK","NO");
                }
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }

    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }


}