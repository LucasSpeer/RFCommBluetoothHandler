package speer.lucas.rfcommbluetoohhandler;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Lucas Speer on 1/2/18.
 * This thread handles the BT socket after a connection is established
 */

public class ConnectedThread extends Thread {
    private static final String TAG = "MY_APP_DEBUG_TAG";
    private Handler mHandler; // handler that gets info from Bluetooth service

    // Defines several constants used when transmitting messages between the
    // service and the UI.
    private interface MessageConstants {
        int MESSAGE_READ = 0;
        int MESSAGE_WRITE = 1;
        int MESSAGE_TOAST = 2;
    }
    // ... (Add other message types here as needed.)
    private final BluetoothSocket mmSocket;
    private byte[] mmBuffer; // mmBuffer store for the stream
    private Boolean isConnected;
    public ConnectedThread(BluetoothSocket socket) {
        mmSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        // Get the input and output streams; using temp objects because
        // member streams are final.
        if(socket != null) {
            try {
                tmpIn = socket.getInputStream();

            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating input stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating output stream", e);
            }
            MainActivity.mmInStream = tmpIn;
            MainActivity.mmOutStream = tmpOut;
            MainActivity.BTFound = true;
            isConnected = true;
            MainActivity.BTStatus = "connected";       //Update the status for the main menu text
            MainActivity.handler.sendEmptyMessage(0);
        }
        else{
            MainActivity.BTFound = false;
            isConnected = false;
            MainActivity.BTStatus = "paired";    //Update the status for the main menu text
            cancel();
        }
    }

    public void run() {
        Backgrounder task = new Backgrounder();
        task.doInBackground();
    }

    // Call this method from the main activity to shut down the connection.
    public void cancel() {
        try {
            if(mmSocket != null){
                mmSocket.close();
                MainActivity.mmInStream = null;
                MainActivity.mmOutStream = null;
            }
        } catch (IOException e) {
            Log.e(TAG, "Could not close the connect socket", e);
        }
    }
    static class Backgrounder extends AsyncTask<Void, Void, Void> {
        /*
            this function creates a background task for the bluetooth connection to keep the UI responsive to activate call:
                task = new bluetoothTask();
                task.execute();

            make sure to have task.cancel(false); in the cancel() function
         */
        private byte[] mmBuffer; // mmBuffer store for the stream
        private InputStream mmInStream = MainActivity.mmInStream;
        private OutputStream mmOutStream = MainActivity.mmOutStream;
        @Override
        protected Void doInBackground(Void... voids) {

            mmBuffer = new byte[1024];
            int numBytes = 0; // bytes returned from read()
            String tmp = "";
            // Keep listening to the InputStream until an exception occurs.
            while(MainActivity.mmInStream != null){
                try {
                   // Read from the InputStream.
                   numBytes = mmInStream.read(mmBuffer);
                   char finalByteArr[] = new char[numBytes];
                   for(int i = 0; i < numBytes; i++){
                       finalByteArr[i] = ((char) mmBuffer[i]);
                       tmp += finalByteArr[i];
                   }
                } catch (IOException e) {
                    Log.d(TAG, "Input stream was disconnected", e);
                    break;
                }
            }
            return null;
        }
    }


}
