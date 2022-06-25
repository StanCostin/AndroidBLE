package com.example.android.bluetoothlegatt;

import static com.example.android.bluetoothlegatt.BluetoothLeService.UUID_BLE;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.speech.RecognizerIntent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class VCBLEController extends AppCompatActivity {
    private final static String TAG = VCBLEController.class.getSimpleName();
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    private ImageButton recVCBtn;
    private ImageButton discVCBtn;
    private EditText textSpeech;
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
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    private BluetoothGattCharacteristic bluetoothGattCharacteristicBLE;
    private final static int RECORD_AUDIO_REQUEST_CODE = 10;

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
        setContentView(R.layout.activity_vcblecontroller);

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        textSpeech = findViewById(R.id.editVCTextId);
        recVCBtn = findViewById(R.id.micVCID);
        discVCBtn = findViewById(R.id.backVCID);


        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        textSpeech.setEnabled(false);

        recVCBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSpeachInput();
            }
        });

        discVCBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBackVCMC();
            }
        });

    }


    public void getSpeachInput() {

        Intent voiceControlIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        voiceControlIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        voiceControlIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        voiceControlIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say Something....");

        startActivityForResult(voiceControlIntent, RECORD_AUDIO_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RECORD_AUDIO_REQUEST_CODE:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> dataVoice = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    textSpeech.setText(dataVoice.get(0));
                    String cmd = dataVoice.get(0).toUpperCase();
                    String flo = "FRONT LIGHTS ON";
                    String flf = "FRONT LIGHTS OFF";
                    String blo = "BACK LIGHTS ON";
                    String blf = "BACK LIGHTS OFF";
                    String stop = "STOP";
                    String elo = "EMERGENCY LIGHTS ON";
                    String elf = "EMERGENCY LIGHTS OFF";
                    String ho = "SOUND ON";
                    String hf = "SOUND OFF";
                    String mf = "MOVE FORWARD";
                    String mb = "MOVE BACKWARD";
                    String tl = "TURN LEFT";
                    String tr = "TURN RIGHT";

                    if (cmd.equals(flo)) {
                        String value = "F";
                        mBluetoothLeService.writeCharacteristic(bluetoothGattCharacteristicBLE, value);
                    } else if(cmd.equals(flf)) {
                        String value = "f";
                        mBluetoothLeService.writeCharacteristic(bluetoothGattCharacteristicBLE, value);
                    } else if(cmd.equals(stop)) {
                        String value = "X";
                        mBluetoothLeService.writeCharacteristic(bluetoothGattCharacteristicBLE, value);
                    } else if(cmd.equals(elo)) {
                        String value = "E";
                        mBluetoothLeService.writeCharacteristic(bluetoothGattCharacteristicBLE, value);
                    } else if(cmd.equals(elf)) {
                        String value = "e";
                        mBluetoothLeService.writeCharacteristic(bluetoothGattCharacteristicBLE, value);
                    } else if(cmd.equals(blo)) {
                        String value = "B";
                        mBluetoothLeService.writeCharacteristic(bluetoothGattCharacteristicBLE, value);
                    } else if(cmd.equals(blf)) {
                        String value = "b";
                        mBluetoothLeService.writeCharacteristic(bluetoothGattCharacteristicBLE, value);
                    } else if(cmd.equals(ho)) {
                        String value = "H";
                        mBluetoothLeService.writeCharacteristic(bluetoothGattCharacteristicBLE, value);
                    } else if(cmd.equals(hf)) {
                        String value = "h";
                        mBluetoothLeService.writeCharacteristic(bluetoothGattCharacteristicBLE, value);
                    } else if(cmd.equals(mf)) {
                        String value = "L";
                        mBluetoothLeService.writeCharacteristic(bluetoothGattCharacteristicBLE, value);
                    } else if(cmd.equals(mb)) {
                        String value = "M";
                        mBluetoothLeService.writeCharacteristic(bluetoothGattCharacteristicBLE, value);
                    } else if(cmd.equals(tl)) {
                        String value = "N";
                        mBluetoothLeService.writeCharacteristic(bluetoothGattCharacteristicBLE, value);
                    } else if(cmd.equals(tr)) {
                        String value = "K";
                        mBluetoothLeService.writeCharacteristic(bluetoothGattCharacteristicBLE, value);
                    } else if(cmd.equals(stop)) {
                        String value = "X";
                        mBluetoothLeService.writeCharacteristic(bluetoothGattCharacteristicBLE, value);
                    }


                }
                break;
        }
    }

    private void goBackVCMC() {
        mBluetoothLeService.disconnect();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
        Intent ovmc = new Intent(this, MenuController.class);
        ovmc.putExtra(EXTRAS_DEVICE_NAME, mDeviceName);
        ovmc.putExtra(EXTRAS_DEVICE_ADDRESS, mDeviceAddress);
        startActivity(ovmc);
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