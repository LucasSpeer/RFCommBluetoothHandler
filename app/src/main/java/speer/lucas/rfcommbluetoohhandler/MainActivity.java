package speer.lucas.rfcommbluetoohhandler;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

        //Setup for Buttons and TextViews

        Button chooseDeviceButton = findViewById(R.id.mainConfButton);
        chooseDeviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ConfActivity.class);
                startActivity(intent);
            }
        });

        Button connectButton = findViewById(R.id.mainAttemptButton);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(BTHandler == null) {
                    Toast.makeText(getApplicationContext(), R.string.noDevice, Toast.LENGTH_SHORT).show();
                }
                else {
                    BTHandler.run();
                    int i = 0;
                    while (!BTStatus.equals("connected") && i < 20) {
                        try {
                            wait(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        i++;
                    }
                    if (BTStatus.equals("connected")) {
                        Toast.makeText(getApplicationContext(), R.string.connected, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.timeOut, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        if( rememberedDevice.equals("none")) {
            connectButton.setVisibility(View.INVISIBLE);
        }
        editor.apply();
        updateStatus();
    }

    //A function to update the status text
    private void updateStatus(){
        String deviceStr;
        TextView deviceText = findViewById(R.id.mainStatusText);
        if(!rememberedDevice.equals("None")) {
            deviceStr = getString(R.string.mainStatus) + rememberedDevice;
        }
        else{
            deviceStr = getString(R.string.noDevice);
        }
        deviceText.setText(deviceStr);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStatus();
    }

}