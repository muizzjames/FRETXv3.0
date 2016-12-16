package fretx.version3;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Misho on 2/4/2016.
 */

public class PresentationActivity extends Activity
{

    ObservableVideoView vvMain;
    MediaController mc;
    Uri videoUri;
    Button btGoMenu;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.presentation_activity);


        videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video_intro);



        vvMain = (ObservableVideoView)findViewById(R.id.vvMain);
        vvMain.setVideoURI(videoUri);
        mc = new MediaController(vvMain.getContext());
        mc.setMediaPlayer(vvMain);
//        mc.setAnchorView(llMain);

        vvMain.setMediaController(mc);

        vvMain.start();

        btGoMenu = (Button)findViewById(R.id.btGoMenu);
        btGoMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent  = new Intent(PresentationActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    @Override
    public void onBackPressed() {
    }
}
