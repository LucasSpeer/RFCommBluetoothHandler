package speer.lucas.rfcommbluetoohhandler;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.util.Set;

public class ConfActivity extends AppCompatActivity {
    public static String[] nameList;
    public static String[] MACList;
    public static Set<BluetoothDevice> pairedDevices;
    public static String currentSelection;
    protected RecyclerView mRecyclerView;
    protected RecyclerView.Adapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure);
        findDevices();
        currentSelection = MainActivity.rememberedDevice;
        mRecyclerView = findViewById(R.id.BluetoothList);
        mLayoutManager = new LinearLayoutManager(ConfActivity.this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyAdapter(nameList);            //Get the adapter for String[] -> RecyclerView as defined in MyAdapter
        mRecyclerView.setAdapter(mAdapter);
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
}
