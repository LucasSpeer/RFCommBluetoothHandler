package speer.lucas.rfcommbluetoohhandler;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.SupportActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

public class SendIntActivity extends SupportActivity {

    String titleText;
    TextView title;
    SeekBar seekBar;
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_int);

        title = findViewById(R.id.sendIntTitle);
        titleText = title.getText().toString();
        final EditText intEnterBox = findViewById(R.id.sendIntBox);
        final Resources res = getResources();

        Button confirm = findViewById(R.id.sendIntSendButton);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    send(Integer.valueOf(intEnterBox.getText().toString()));
                } catch (NumberFormatException e){
                    //do nothing
                }
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

        seekBar = findViewById(R.id.sendIntSeekBar);

        final CheckBox center = findViewById(R.id.sendIntCenter);
        center.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked && seekBar.getProgress() != 90){
                    seekBar.setProgress(90);
                }
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(final SeekBar seekBar, final int progress, boolean fromUser) {
                if(canSend){
                    send(progress);
                    canSend = false;
                    MainActivity.handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            canSend = true;
                        }
                    }, 50);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //int prog = seekBar.getProgress();
                if(center.isChecked()) {
                    canSend = true;
                    seekBar.setProgress(90);
                }
            }
        });

    }

    Boolean canSend = true;
    private void send(final int toSend){
        ConnectedThread.sendInt(toSend);
        title.setText(titleText + " - " + toSend);
    }
}
