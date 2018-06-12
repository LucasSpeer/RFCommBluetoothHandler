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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class FileChooseActivity extends SupportActivity {
    //List Variables
    public static String fileListSelection = "none";
    public static String chosenFileText = "";
    public static int selectionPosition;
    public static String[] fileList;
    protected RecyclerView mRecyclerView;
    protected RecyclerView.Adapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    private JSONObject readJSONdata;
    private JSONObject fileContents;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_chooser);
        Resources resources = getResources();
        String rawFiles = "";
        if(ConnectedThread.readData != null){
            String rawData = ConnectedThread.readData;
            try {
                readJSONdata = new JSONObject(rawData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(readJSONdata != null) {
                try {

                    fileContents = readJSONdata.getJSONObject("files");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    rawFiles = readJSONdata.getString("fileNames");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            rawFiles = "None_Found-Create_New";
        }

        fileList = rawFiles.split(" ");

        //Setup Buttons
        Button open = findViewById(R.id.fileListOpenButton);
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!fileListSelection.equals("none")) {
                    String selection = fileListSelection;
                    try {
                        chosenFileText = fileContents.getString(fileList[selectionPosition]);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Intent intent = new Intent(FileChooseActivity.this, TextEditorActivity.class);
                intent.putExtra("fileName", fileListSelection);
                intent.putExtra("fileContents", chosenFileText);
                startActivity(intent);
            }
        });

        Button back = findViewById(R.id.fileListBackButton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FileChooseActivity.this, ConnectedActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        mRecyclerView = findViewById(R.id.fileListRecycler);    //find the recyclerView
        mLayoutManager = new LinearLayoutManager(this);     //Get and set a new layout manager
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new FileListAdapter(fileList);            //Get and set the adapter for String[] -> RecyclerView as defined in DeviceListAdapter
        mRecyclerView.setAdapter(mAdapter);
    }
}
