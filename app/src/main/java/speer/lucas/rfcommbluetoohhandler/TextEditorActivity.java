package speer.lucas.rfcommbluetoohhandler;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.SupportActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;

public class TextEditorActivity extends SupportActivity {
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_editor);

        final EditText fileName = findViewById(R.id.editorFileName);
        final EditText fileContents = findViewById(R.id.editorTextBox);
        final Resources res = getResources();
        String commandList[] = res.getStringArray(R.array.commandsToSend);
        final String sendCommand = commandList[0];    //Get the command to send "saveFile"

        Button confirm = findViewById(R.id.editorSendButton);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stringToSend = fileName.getText().toString() + "\n" + fileContents.getText().toString();     //The python script needs to know the format fileName\nFileContents... in it's textEditorHandler
                ConnectedThread.executeCommand(sendCommand, stringToSend);
            }
        });

        Button back = findViewById(R.id.editorBackButton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.BTFound = false;
                Intent intent = new Intent(TextEditorActivity.this, ConnectedActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        String toOpen = getIntent().getStringExtra("fileName");
        String openedContents = getIntent().getStringExtra("fileContents");
        if(toOpen != null){
            fileName.setText(toOpen);
            fileContents.setText(openedContents);
        }
    }
}
