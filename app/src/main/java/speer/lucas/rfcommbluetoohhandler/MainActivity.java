package speer.lucas.rfcommbluetoohhandler;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {


    //Bluetooth Variables
    public static String MAC; //Address of connected device
    public static UUID uuid = null;
    public static BluetoothAdapter mBluetoothAdapter;
    public static InputStream mmInStream = null;     //Initialize IO streams
    public static OutputStream mmOutStream = null;
    public static Boolean BTFound = false;
    public static String BTStatus;
    public static BluetoothHandler BTHandler;
    public static String rememberedDevice;
    public static Set<BluetoothDevice> pairedDevices;
    private BluetoothDevice BTdevice;

    //List Variables
    public static String currentSelection;
    public static int selectionPosition;
    protected RecyclerView mRecyclerView;
    protected RecyclerView.Adapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    public static String[] nameList;
    public static String[] MACList;


    public static Handler handler;
    private SharedPreferences prefs = null;      //create a shared preference for storing settings
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = this.getPreferences(Context.MODE_PRIVATE);    //retrieve default preference file for storing layout as key value pairs {(string) "L1", (int)1}
        editor = prefs.edit();
        MAC = prefs.getString("MAC","00:00:00:00:00");
        rememberedDevice = prefs.getString("deviceName", "None");
        //Initialize Bluetooth
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();      //get bluetooth adapter
        if (!mBluetoothAdapter.isEnabled()) {                          //If bluetooth is not enabled, enable it
            mBluetoothAdapter.enable();
        }
        uuid = UUID.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ee");
        findDevices();

        //Setup for Button
        final Button connectButton = findViewById(R.id.mainAttemptButton);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tmpName;
                if(currentSelection != null) {
                    rememberedDevice = currentSelection;
                }
                for (BluetoothDevice device : pairedDevices) {
                    tmpName = device.getName();
                    if (tmpName.equals(rememberedDevice)) {
                        BTdevice = device;
                    }
                }
                BTHandler = new BluetoothHandler(BTdevice);
                BTHandler.run();

            }
        });

        //Fragment Initialize
        Fragment mainListFrag = new deviceFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.mainListFrag, mainListFrag).commit();
        //View fragView = mainListFrag.getView();
        /*
            @Override
            public void onClick(View v) {
                if(currentSelection != null) {
                    String toSet = getString(R.string.mainStatus) + currentSelection;
                    deviceStatus.setText(toSet);
                }
            }
        });
        */
        mRecyclerView = findViewById(R.id.mainListRecycler);    //find the recyclerView
        mLayoutManager = new LinearLayoutManager(this);     //Get and set a new layout manager
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new DeviceListAdapter(MainActivity.nameList);            //Get and set the adapter for String[] -> RecyclerView as defined in DeviceListAdapter
        mRecyclerView.setAdapter(mAdapter);

        //setup Handler
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                startActivity(new Intent(MainActivity.this, ConnectedActivity.class));
            }
        };
        editor.apply();
    }


    private void findDevices() {
        /*
        findDevices() first gets the list of devices paired
        If none are found an error is shown
     */
        pairedDevices = MainActivity.mBluetoothAdapter.getBondedDevices();   //check if already paired
        String deviceNames[]= new String[pairedDevices.size()];
        String mac[]= new String[pairedDevices.size()];
        int i = 0;
        if (pairedDevices.size() > 0) {       // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                deviceNames[i] = device.getName();
                mac[i] = device.getAddress();
                i++;
            }
        } else {
            //There are no paired devices
        }
        nameList = deviceNames;
        MACList = mac;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        editor.putString("deviceName", rememberedDevice);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!BTFound && mmInStream != null){     //If 'Disconnect' was pushed in the connected activity (i.e. a connection exists that is not needed)
            BTHandler.cancel();                 //Close the connection (this will set mmIn/OutStream to null)
        }
    }

}