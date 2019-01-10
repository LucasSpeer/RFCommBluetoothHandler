package speer.lucas.rfcommbluetoohhandler;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Lucas Speer on 1/2/18.
 * This thread handles the BT socket after a connection is established
 * Modified from my SmartMirror app (github.com/lucasspeer/smartmirror)
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
    public static String readData;
    private Boolean isConnected;
    public ConnectedThread(BluetoothSocket socket) {
        mmSocket = socket;
        waitForConnection.run();
    }

    int timeout = 10;
    int cnt = 0;
    private Runnable waitForConnection = new Runnable() {
        @Override
        public void run() {
            MainActivity.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    InputStream tmpIn = null;
                    OutputStream tmpOut = null;

                    // Get the input and output streams; using temp objects because
                    // member streams are final.
                    if(mmSocket != null) {
                        try {
                            tmpIn = mmSocket.getInputStream();

                        } catch (IOException e) {
                            Log.e(TAG, "Error occurred when creating input stream", e);
                        }
                        try {
                            tmpOut = mmSocket.getOutputStream();
                        } catch (IOException e) {
                            Log.e(TAG, "Error occurred when creating output stream", e);
                        }
                        MainActivity.mmInStream = tmpIn;
                        MainActivity.mmOutStream = tmpOut;
                        MainActivity.BTFound = true;
                        isConnected = true;
                        MainActivity.BTStatus = "connected";       //Update the status for the main menu text
                        MainActivity.handler.sendEmptyMessage(0);
                    } else if (cnt < timeout){
                        cnt++;
                        MainActivity.handler.postDelayed(this, 500);
                    } else{
                        MainActivity.BTFound = false;
                        isConnected = false;
                        MainActivity.BTStatus = "paired";    //Update the status for the main menu text
                        cancel();
                    }
                }
            }, 500);
        }
    };

    public static void executeCommand(String command, String data){
        //This is the main function to send data over the server (although MainActivity.mmOutStream can be accessed anywhere)
        //The server side script must be anticipating "command" + "\n" + "data..."
        //All available commands are given in strings.xml (and new ones should be added there)
        if(MainActivity.mmOutStream != null){
            String toWrite = command + "\n" + data;
            try {
                MainActivity.mmOutStream.write(toWrite.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void sendInt(int toWrite){
        String str = String.valueOf(toWrite) + "#";
        if(MainActivity.mmOutStream != null){
            try {
                MainActivity.mmOutStream.write(str.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
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

            // Keep listening to the InputStream until an exception occurs.
            while(MainActivity.mmInStream != null){
                try {
                   // Read from the InputStream
                   numBytes = mmInStream.read(mmBuffer);
                   char finalByteArr[] = new char[numBytes];
                   String tmp = "";
                   for(int i = 0; i < numBytes; i++){
                       finalByteArr[i] = ((char) mmBuffer[i]);
                       tmp += finalByteArr[i];
                   }
                   readData = tmp;
                } catch (IOException e) {
                    Log.d(TAG, "Input stream was disconnected", e);
                    break;
                }
            }
            return null;
        }
    }


}
