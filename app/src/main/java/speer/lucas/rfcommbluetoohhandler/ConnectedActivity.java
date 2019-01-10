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
        commandList = resources.getStringArray(R.array.commandsToDisplay);   //Get list of possible commands from resources

        //Setup Buttons
        Button confirm = findViewById(R.id.connectedConfirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = 0;
                for (int i = 0; i < commandList.length; i++) {
                    if(connCurrentSelection.equals(commandList[i])){
                        pos = i;
                    }
                }
                switch (pos){  //Each case is a string from @strings/commands[]
                    case 0:
                        Intent editorIntent = new Intent(ConnectedActivity.this, TextEditorActivity.class);
                        startActivity(editorIntent);
                        break;
                    case 1:
                        Intent fileIntent = new Intent(ConnectedActivity.this, FileChooseActivity.class);
                        startActivity(fileIntent);
                        break;
                    case 2:
                        Intent intIntent = new Intent(ConnectedActivity.this, SendIntActivity.class);
                        startActivity(intIntent);
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
