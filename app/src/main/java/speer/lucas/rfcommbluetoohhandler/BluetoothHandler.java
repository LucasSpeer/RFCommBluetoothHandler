package speer.lucas.rfcommbluetoohhandler;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/*
 * Created by Lucas on 11/19/17.
 * handles the bluetooth connection process. Once connected it hands the Bluetooth Socket to ConnectedThread, which handles the data transfer
 */

public class BluetoothHandler extends Thread {

    private static final String TAG = "MY_APP_DEBUG_TAG";
    private static final int PERIOD = 5000;
    final private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private static BluetoothSocket mmSocket;
    private static BluetoothDevice BTdevice = null;
    private ConnectedThread BTthread;
    private Backgrounder task = null;

    BluetoothHandler(BluetoothDevice device) {
        task = new Backgrounder();
        task.doInBackground();
        BTdevice = device;
    }

    private int timeout = 20;
    private int cnt = 0;
    public void run() {
        mBluetoothAdapter.cancelDiscovery();    //Cancel discovery because it otherwise slows down the connection.
        MainActivity.handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                try {
                    if(mmSocket != null){
                        if(!mmSocket.isConnected()) {
                            mmSocket.connect();   //Connect to the remote device through the socket. This call blocks until it succeeds or throws an exception
                            BTthread = new ConnectedThread(mmSocket);     //Create a new thread to handle the connection.
                            BTthread.start();
                        }
                    }
                } catch (IOException connectException) {
                    connectException.printStackTrace();
                }
                if (cnt < timeout){
                    if(mmSocket == null) {
                        cnt++;
                        MainActivity.handler.postDelayed(this, 500);
                    }
                }


            }
        }, 500);

    }

    void cancel() {
        try {
            mmSocket.close();    // Closes the client socket and causes the thread to finish.
        } catch (IOException e) {
            Log.e(TAG, "Could not close the client socket", e);
        }
        if(BTthread != null) {
            BTthread.cancel();
        }
        if (task != null) {
            task.cancel(false);
        }
    }

    static class Backgrounder extends AsyncTask<Void, Void, Void> {
        /*
            this function creates a background task for the bluetooth connection to keep the UI responsive to activate call:
                task = new bluetoothTask();
                task.execute();

            make sure to have task.cancel(false); in the cancel() function
         */

        @Override
        protected Void doInBackground(Void... voids) {
            BluetoothSocket tmp = null;   // Use a temporary object that is later assigned to mmSocket because mmSocket is final.
            UUID uuid = MainActivity.uuid;
            try {
                if(BTdevice != null){
                    tmp = BTdevice.createRfcommSocketToServiceRecord(uuid);    //Get a BluetoothSocket to connect with the given BluetoothDevice. uuid must match rfcomm-server.py on the Rpi
                }
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            mmSocket = tmp;

            return null;
        }
    }
}
