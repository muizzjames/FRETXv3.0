package fretx.version3;


import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.VideoView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;

public class LearnFragmentTwo extends Fragment {
    //    MediaController mc;
    Button   btLearned;
    Button   btPlayReplay;
    TextView tvTitle;


    private static ObservableVideoView vvMain;
    RelativeLayout llMain;
    MainActivity mActivity;
    View rootView = null;
    MediaController mc;
    ToggleButton tgSwitch;
    Button   btStartLoop;
    Button   btEndLoop;
    TextView tvStartTime;
    TextView tvEndTime;
    boolean bStartCheckFlag = false;        ///Flag that current time is passed start time.
    boolean bEndCheckFlag = false;          ///Flag that current time is passed end time.

    Hashtable lstTimeText = new Hashtable();
    int[] arrayKeys;
    Boolean[] arrayCallStatus;

    int m_currentTime = 0;                ////Now playing time.
    int durationTime = 0;               ///Video duration
    int startPos = 0;                   ///start point of loop
    int endPos = 0;                     ///end point of loop
    boolean  mbLoopable = false;        ///flag of checking loop
    boolean mbPlaying = true;           ///Flag of now playing.
    Uri videoUri;
    private Handler mCurTimeShowHandler = new Handler();

    boolean mbSendingFlag = false;


    public LearnFragmentTwo(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mActivity = (MainActivity)getActivity();
        rootView = inflater.inflate(R.layout.learn_fragment_play, container, false);
        initTxt();
        initUI();       ///Init UI(textView, VideoView....)
        return rootView;
    }


    public void startButton(){
        btLearned.setVisibility(View.VISIBLE);
        btPlayReplay.setVisibility(View.VISIBLE);
        btPlayReplay.setText("Replay");
    }
    private void initUI() {
        tvTitle = (TextView)rootView.findViewById(R.id.tvTitle);
        tvTitle.setText("Exercise2 - Em Chord");
        btLearned = (Button)rootView.findViewById(R.id.btLearned);
        btLearned.setVisibility(View.INVISIBLE);
        btLearned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.learn_container, new LearnFragmentTuner());
                fragmentTransaction.commit();
            }
        });
        btPlayReplay = (Button)rootView.findViewById(R.id.btPlayReplay);
        btPlayReplay.setVisibility(View.INVISIBLE);
        btPlayReplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btPlayReplay.setVisibility(View.INVISIBLE);
                vvMain.start();
            }
        });

        llMain = (RelativeLayout)rootView.findViewById(R.id.llVideoView);
        tgSwitch = (ToggleButton)rootView.findViewById(R.id.tgSwitch);   ///ToggleButton that sets loop.
        btStartLoop = (Button)rootView.findViewById(R.id.btnStartLoop);  ///Button that sets startTime while playing video.
        btEndLoop = (Button)rootView.findViewById(R.id.btnEndLoop);      ///Button that sets endTime while playing video.
        tvStartTime = (TextView)rootView.findViewById(R.id.tvStartTime);
        tvEndTime = (TextView)rootView.findViewById(R.id.tvEndTime);
        tgSwitch.setChecked(false);
        tvStartTime.setText("0");
        tvEndTime.setText("0");
        videoUri = Uri.parse("android.resource://" + mActivity.getPackageName() + "/" + R.raw.learn_ex2);
        vvMain = (ObservableVideoView)rootView.findViewById(R.id.vvMain);
        vvMain.setVideoURI(videoUri);
        ////New MediaController including VideoView. It shows timeline and play and forward,
        // backward button on the VideoView.
        mc = new MediaController(vvMain.getContext());
        mc.setMediaPlayer(vvMain);
        mc.setAnchorView(llMain);
        vvMain.setMediaController(mc);
        vvMain.start(); ///Play Video
        durationTime = vvMain.getDuration(); //Get video duration.

        ////This is runnable thread that sets currentTime to tvCurTime TextView and check loop
        // available through startPos and endPos

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                {
                    ///Set currentTime to current time textview.
                    m_currentTime = vvMain.getCurrentPosition();
                    ///Set the current title of current time.
                    changeText(m_currentTime);
                    if (!vvMain.isPlaying()){
                        startButton();
                    }else{
                        btPlayReplay.setVisibility(View.INVISIBLE);
                    }
                    mCurTimeShowHandler.postDelayed(this, 1);
                }
            }
        };
        mCurTimeShowHandler.post(runnable);
        /////This is videoview listener.
        vvMain.setVideoViewListener(new ObservableVideoView.IVideoViewActionListener() {
            @Override
            public void onPause() {
                mbPlaying = false;
                ////if press pause button then stop loop.
//                stopViaData();
                mCurTimeShowHandler.removeCallbacks(runnable);
            }

            @Override
            public void onResume() {
                mbPlaying = true;
                ////if press resume button then start loop
                mCurTimeShowHandler.post(runnable);
            }

            @Override
            public void onTimeBarSeekChanged(int currentTime) {
                //Set the current time of textview and change the text of current timee while
                // seeking the timeline.
                m_currentTime = currentTime;
                changeText(currentTime);
            }

        });

    }

    public static VideoView getVideoView(){
        return vvMain;
    }

    ///Read from text source and get the array of time and its text.
    public void initTxt()
    {
        String str= readRawTextFile(mActivity.getBaseContext(), R.raw.learn_two);
        String[] strArray = str.split( "\n" );
        for( int nIndex= 0; nIndex < strArray.length; nIndex++ )
        {
            ///Split the every line of source text to two parts.
            // Every line is splited by ' ',
            String[] strArrTemp = strArray[nIndex].split(" ");
            String strTime = strArrTemp[0];     ///This is time
            String strText = strArrTemp[1];     // This is text of that strTime.
            ///If ther's same time, then add two text to hashtable.
            // else add one text of the time to hashtable.
            if(lstTimeText.containsKey(Integer.parseInt(strTime)))
            // if there's same key in the
            // hashtable then add other text of same time.
            {
                String strTemp = (String)lstTimeText.get(Integer.parseInt(strTime));
                lstTimeText.put(Integer.parseInt(strTime), strTemp + ":" + strText);
            }else
                lstTimeText.put(Integer.parseInt(strTime), strText);

        }
        ///save the key array of hashtable to int array.
        arrayKeys = new int[lstTimeText.size()];
        arrayCallStatus = new Boolean[lstTimeText.size()];

        int i = 0;
        for ( Enumeration e = lstTimeText.keys(); e.hasMoreElements(); ) {
            arrayKeys[i] = (int) e.nextElement();
            arrayCallStatus[i] = false;
            i++;
        }
        Arrays.sort(arrayKeys);
    }
    ///Read the text file from resource(Raw) and divide by end line mark('\n")
    public static String readRawTextFile(Context ctx, int resId) {
        InputStream inputStream = ctx.getResources().openRawResource(resId);

        InputStreamReader inputreader = new InputStreamReader(inputStream);
        BufferedReader buffreader = new BufferedReader(inputreader);
        String line;
        StringBuilder text = new StringBuilder();

        try {
            while ((line = buffreader.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
        } catch (IOException e) {
            return null;
        }
        return text.toString();
    }
    ///changeText(int currentTime)
    ///Set the text of currentTime.
    public void changeText(int currentTime)
    {
        //From the first to number of hashtable keys, Search index that its value is bigger than
        // current time. Then sets the text that was finded in hashtable keys.
        for ( int nIndex = 0; nIndex < arrayKeys.length -1; nIndex++ )
        {
            if ( arrayKeys[nIndex] <= currentTime && arrayKeys[nIndex + 1] > currentTime )
            {
                if( arrayCallStatus[nIndex] )
                    return;

                arrayCallStatus[nIndex] = true;
                ConnectThread connectThread = new ConnectThread(str2array((String) lstTimeText.get(arrayKeys[nIndex])));
                connectThread.run();
                setDefaultValues(arrayCallStatus);
                arrayCallStatus[nIndex] = true;

            }
        }

        if ( arrayKeys[arrayKeys.length -1] <= currentTime )
        {
            if( arrayCallStatus[arrayKeys.length -1] )
                return;

            arrayCallStatus[arrayKeys.length -1] = true;
            //tvNotice.setText((String) lstTimeText.get(arrayKeys[arrayKeys.length -1]));
            ConnectThread connectThread = new ConnectThread(str2array((String) lstTimeText.get(arrayKeys[arrayKeys.length -1])));
            connectThread.run();
            setDefaultValues(arrayCallStatus);
            arrayCallStatus[arrayKeys.length -1] = true;
        }


    }
    public void setDefaultValues(Boolean[] bArray)
    {
        for (int i = 0; i < bArray.length; i ++){
            bArray[i] = false;
        }
    }
    public void startViaData(byte[] array) {
        BluetoothClass.mHandler.obtainMessage(BluetoothClass.ARDUINO, array).sendToTarget();
    }

    public byte[] str2array(String string){
        String strSub = string.replaceAll("[{}]", "");
        String[] parts = strSub.split(",");
        byte[] array = new byte[parts.length];
        for (int i = 0; i < parts.length; i ++)
        {
            array[i] = Byte.valueOf(parts[i]);
        }
        return array;
    }
    private class ConnectThread extends Thread {
        byte[] array;
        public ConnectThread(byte[] tmp) {
            array = tmp;
        }

        public void run() {
            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                startViaData(array);
            } catch (Exception connectException) {
                Log.i(BluetoothClass.tag, "connect failed");
                // Unable to connect; close the socket and get out
                try {
                    BluetoothClass.mmSocket.close();
                } catch (IOException closeException) {
                    Log.e(BluetoothClass.tag, "mmSocket.close");
                }
                return;
            }
            // Do work to manage the connection (in a separate thread)
            if (BluetoothClass.mHandler == null)
                Log.v("debug", "mHandler is null @ obtain message");
            else
                Log.v("debug", "mHandler is not null @ obtain message");
            mbSendingFlag = false;
        }
    }
}
