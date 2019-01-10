package speer.lucas.rfcommbluetoohhandler;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.SupportActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SendIntActivity extends SupportActivity {
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_int);

        final EditText fileName = findViewById(R.id.sendIntBox);
        final Resources res = getResources();

        Button confirm = findViewById(R.id.sendIntSendButton);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int toSend = Integer.valueOf(fileName.getText().toString());
                ConnectedThread.sendInt(toSend);
            }
        });

        Button back = findViewById(R.id.sendIntBackButton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.BTFound = false;
                Intent intent = new Intent(SendIntActivity.this, ConnectedActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

    }
}
