package speer.lucas.rfcommbluetoohhandler;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.SupportActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;



public class ConnectedActivity extends SupportActivity {
    //List Variables
    public static String connCurrentSelection = "none";
    public static int selectionPosition;
    public static String[] commandList;
    protected RecyclerView mRecyclerView;
    protected RecyclerView.Adapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected);
        Resources resources = getResources();
        commandList = resources.getStringArray(R.array.commands);   //Get list of possible commands from resources

        //Setup Buttons
        Button confirm = findViewById(R.id.connectedConfirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!connCurrentSelection.equals("none")) {
                    String stringToSend = connCurrentSelection;
                    try {
                        MainActivity.mmOutStream.write(stringToSend.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                switch (connCurrentSelection){  //Each case is a string from @strings/commands[]
                    case "New File":
                        Intent editorIntent = new Intent(ConnectedActivity.this, TextEditorActivity.class);
                        startActivity(editorIntent);
                        break;
                    case "Open a File":
                        Intent fileIntent = new Intent(ConnectedActivity.this, FileChooseActivity.class);
                        startActivity(fileIntent);
                        break;
                }
            }
        });

        Button disconnect = findViewById(R.id.disconnectButton);
        disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.BTFound = false;
                Intent intent = new Intent(ConnectedActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        mRecyclerView = findViewById(R.id.connectedRecycler);    //find the recyclerView
        mLayoutManager = new LinearLayoutManager(this);     //Get and set a new layout manager
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new CommandListAdapter(commandList);            //Get and set the adapter for String[] -> RecyclerView as defined in DeviceListAdapter
        mRecyclerView.setAdapter(mAdapter);
    }
}
