package speer.lucas.rfcommbluetoohhandler;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.Set;

public class ConfActivity extends AppCompatActivity {
    public static String[] nameList;
    public static String[] MACList;
    public static Set<BluetoothDevice> pairedDevices;
    public static String currentSelection;
    public static int selectionPosition;
    private BluetoothDevice BTdevice;
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
        mAdapter = new DeviceListAdapter(nameList);            //Get the adapter for String[] -> RecyclerView as defined in DeviceListAdapter
        mRecyclerView.setAdapter(mAdapter);

        //button setup

        Button back = findViewById(R.id.confBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ConfActivity.this, MainActivity.class);  //when back is clicked return to main menu
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);  //clear the ConfActivity of the top of the stack before starting MainActivity
                startActivity(intent);
            }
        });

        Button confirm = findViewById(R.id.confConfirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tmpName;
                if(currentSelection != null){
                    MainActivity.rememberedDevice = currentSelection;
                    for (BluetoothDevice device : pairedDevices) {
                        tmpName = device.getName();
                        if(tmpName.equals(currentSelection)){
                            BTdevice = device;
                        }
                    }
                    MainActivity.BTHandler =  new BluetoothHandler(BTdevice);
                    Intent intent = new Intent(ConfActivity.this, MainActivity.class);  //when back is clicked return to main menu
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);  //clear the ConfActivity of the top of the stack before starting MainActivity
                    startActivity(intent);
                }
            }
        });
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