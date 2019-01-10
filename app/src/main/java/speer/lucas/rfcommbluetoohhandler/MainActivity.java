package speer.lucas.rfcommbluetoohhandler;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.InputStream;
import java.io.OutputStream;
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

    public static String[] nameList;
    public static String[] MACList;


    public static Handler handler;
    private SharedPreferences prefs = null;      //create a shared preference for storing settings
    private SharedPreferences.Editor editor;

    DeviceFragment mainListFrag;

    String sppUUID = "00001101-0000-1000-8000-00805f9b34fb";
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
        uuid = UUID.fromString(sppUUID);
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

        final Button refresh = findViewById(R.id.mainRefreshButton);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findDevices();
                mainListFrag.refreshList();
            }
        });

        //Fragment Initialize

        FragmentManager fragmentManager = getSupportFragmentManager();
        mainListFrag = new DeviceFragment();
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