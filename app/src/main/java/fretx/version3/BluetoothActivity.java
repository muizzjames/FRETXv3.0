package fretx.version3;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class BluetoothActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    ArrayAdapter<String> listAdapter;
    ListView listView;
    BluetoothAdapter btAdapter;
    Set<BluetoothDevice> devicesArray;
    ArrayList<String> pairedDevices;
    ArrayList<BluetoothDevice> devices;
    public static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private String FILE = Environment.getExternalStorageDirectory()+"/userinfo.csv";
    IntentFilter filter;
    BroadcastReceiver receiver;

    Button m_btnGoBack;

    String macAddress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_activity);

        m_btnGoBack = (Button)findViewById(R.id.btGoBack);
        m_btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
        if(btAdapter == null){
            Toast.makeText(getApplicationContext(), "No Bluetooth on your phone!", Toast.LENGTH_SHORT).show();
        }
        else{
            if(!btAdapter.isEnabled()){
                turnOnBT();
            }
            getPairedDevices();
            startDiscovery();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
        Log.v("debug", "================================================================");
    }

    private void startDiscovery() {
        btAdapter.cancelDiscovery();
        btAdapter.startDiscovery();
    }

    private void turnOnBT() {
        Intent intent =new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent, 0);
    }

    private void getPairedDevices() {
        devicesArray = btAdapter.getBondedDevices();
        if(devicesArray.size()>0){
            for(BluetoothDevice device:devicesArray){
                pairedDevices.add(device.getName());
            }
        }
    }

    private void init() {
        listView=(ListView)findViewById(R.id.listView);
        listView.setOnItemClickListener(this);
        listAdapter= new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,0);
        listView.setAdapter(listAdapter);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        pairedDevices = new ArrayList<>();
        filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        devices = new ArrayList<>();
        receiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if(BluetoothDevice.ACTION_FOUND.equals(action)){
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    devices.add(device);
                    String s = "";
                    String name = device.getName();

                    if (name == null) {
                        Toast.makeText(getApplicationContext(), "Bluetooth without name found", Toast.LENGTH_SHORT).show();
                        listAdapter.add(device.getAddress());
                        return;
                    }
                    for(int a = 0; a < pairedDevices.size(); a++){
                        if(name.equals(pairedDevices.get(a))){
                            //append
                            s = " (Paired)";
                            break;
                        }
                    }
                    listAdapter.add(device.getName() + " " + s);
                }
                else if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
                    if(btAdapter.getState() == BluetoothAdapter.STATE_OFF){
                        turnOnBT();
                    }
                }
            }
        };

        registerReceiver(receiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        registerReceiver(receiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(receiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(receiver, filter);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_CANCELED){
            Toast.makeText(getApplicationContext(), "Bluetooth must be enabled to continue", Toast.LENGTH_SHORT).show();
//            finish();
        }
        else{
            while (!btAdapter.isEnabled());
            Log.v("debug", "bluetooth is now enabled");
        }
    }

    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        boolean isPaired = true;

        if(btAdapter.isDiscovering()){
            btAdapter.cancelDiscovery();
        }
        BluetoothDevice selectedDevice = devices.get(arg2);
        if(!listAdapter.getItem(arg2).contains("Paired")) {
            isPaired = pairDevice(selectedDevice);
        }
        if (!isPaired) {
            Toast.makeText(getApplicationContext(), "pairing failed", Toast.LENGTH_LONG).show();
            return;
        }
        ConnectThread connect = new ConnectThread(selectedDevice);
        connect.start();
        Log.i(BluetoothClass.tag, "in click listener");
    }

    private class ConnectThread extends Thread {

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            Log.i(BluetoothClass.tag, "construct");
            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MY_UUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
                macAddress = device.getAddress();
//                exportEmailInCSV(Config.strUserID, macAddress);
            } catch (IOException e) {
                Log.i(BluetoothClass.tag, "get socket failed");
            }
            BluetoothClass.mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            btAdapter.cancelDiscovery();
            Log.i(BluetoothClass.tag, "connect - run");
            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                BluetoothClass.mmSocket.connect();

                Log.i(BluetoothClass.tag, "connect - succeeded");
            } catch (IOException connectException) {
                Log.i(BluetoothClass.tag, "connect failed");
                // Unable to connect; close the socket and get out
                try {
                    BluetoothClass.mmSocket.close();
                } catch (IOException closeException) {
                    Log.e(BluetoothClass.tag, "mmSocket.close");
                }
                return;
            }
            // Do work to manage the connection (in a separate thread)
            if (BluetoothClass.mHandler == null)
                Log.v("debug", "mHandler is null @ obtain message");
            else
                Log.v("debug", "mHandler is not null @ obtain message");
            BluetoothClass.mHandler.obtainMessage(BluetoothClass.SUCCESS_CONNECT, BluetoothClass.mmSocket).sendToTarget();
            Log.v(BluetoothClass.tag, "out of handler after connection");
            try {
                exportEmailInCSV(Config.strUserID, macAddress);
            } catch (IOException closeException) {
                Log.e(BluetoothClass.tag, "file save fail");
            }

            Config.bBlueToothActive = true;
            finish();
        }
    }

    public boolean pairDevice(BluetoothDevice btDevice) {
        try {
            Log.d(BluetoothClass.tag, "Start Pairing...");
            Method m = btDevice.getClass().getMethod("createBond", (Class[]) null);
            Boolean returnValue = (Boolean) m.invoke(btDevice, (Object[]) null);
            Log.d(BluetoothClass.tag, "Pairing finished.");
            return returnValue;

        } catch (Exception e) {
            Log.e(BluetoothClass.tag, "pairDevice()");
        }
        return false;
    }

    @Override
    public void onBackPressed() {
    }

    public void exportEmailInCSV(final String user, final String mac) throws IOException {
        new Thread() {
            public void run() {
                try {
                    FileWriter fw = new FileWriter(FILE);

                    // Creating a title in CVS file
                    fw.append("UserID");
                    fw.append(',');

                    fw.append("BT MAC ADRESS");
                    fw.append(',');

                    fw.append('\n');


                    // Creating a title in CVS file
                    fw.append(user);
                    fw.append(',');

                    fw.append(mac);
                    fw.append(',');

                    fw.append('\n');
                    // fw.flush();
                    fw.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

}

