package speer.lucas.rfcommbluetoohhandler;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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
    private BluetoothDevice BTdevice;
    public static InputStream mmInStream = null;     //Initialize IO streams
    public static OutputStream mmOutStream = null;
    public static Boolean BTFound = false;
    public static String BTStatus;
    public BluetoothHandler BTHandler;
    public static String rememberedDevice;

    public static final Handler handler = new Handler();
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
        editor.apply();
    
    }

}
