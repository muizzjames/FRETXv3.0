package fretx.version3;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubePlayerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;


public class PlayFragmentYoutubeFragment extends Fragment{

    MainActivity mActivity;
    private static final String API_KEY = "AIzaSyBVpnU2xvoUvbVVFw9u8xgSMYdkr4uGRbk";

    private String VIDEO_ID = "";
    private int SONG_TXT;




    static Hashtable lstTimeText;
    static int[]                               arrayKeys;
    static Boolean[]                           arrayCallStatus;

    private static final int            RECOVERY_REQUEST = 1;


    private    YouTubePlayer               m_player = null;
    private MyPlayerStateChangeListener playerStateChangeListener;
    private MyPlaybackEventListener     playbackEventListener;
    public String                       videoUri;
    public int                          resourceId;


    //add kys 0220
    static private ToggleButton tgSwitch;
    private Button btStartLoop;
    private Button                      btEndLoop;
    private TextView                    tvStartTime;
    private TextView                    tvEndTime;

    static boolean                             bStartCheckFlag = false;        ///Flag that current time is passed start time.
    static boolean                             bEndCheckFlag = false;          ///Flag that current time is passed end time.

    static int                                 m_currentTime = 0;                ////Now playing time.

    static int                                 startPos = 0;                   ///start point of loop
    static int                                 endPos = 0;                     ///end point of loop

    static boolean                             mbLoopable = false;        ///flag of checking loop
    static boolean                             mbPlaying = true;           ///Flag of now playing.


    static private Handler mCurTimeShowHandler = new Handler();

    static boolean                             mbSendingFlag = false;


    public View rootView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mActivity = (MainActivity)getActivity();

        rootView = inflater.inflate(R.layout.play_fragment_youtube_fragment, container, false);

        VIDEO_ID = getArguments().getString("URL");
        SONG_TXT = getArguments().getInt("RAW");
        initTxt( SONG_TXT );

        YouTubePlayerSupportFragment youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.youtube_view, youTubePlayerFragment).commit();



        youTubePlayerFragment.initialize(API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
                if (!wasRestored) {
                    m_player = player;
                    m_player.setFullscreen(false);

                    m_player.setPlayerStateChangeListener(playerStateChangeListener);
                    m_player.setPlaybackEventListener(playbackEventListener);
                    m_player.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
                    m_player.setShowFullscreenButton(false);

                    m_player.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                    m_player.loadVideo(VIDEO_ID);
                    m_player.play();
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult error) {
                // YouTube error
                String errorMessage = error.toString();
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
                Log.d("errorMessage:", errorMessage);
            }
        });
        initUI();
        return rootView;
    }

    public void initUI(){

        playerStateChangeListener = new MyPlayerStateChangeListener();
        playbackEventListener = new MyPlaybackEventListener();

        tvStartTime = (TextView)rootView.findViewById(R.id.tvStartTime);
        tvEndTime = (TextView)rootView.findViewById(R.id.tvEndTime);

        tvStartTime.setText("0");
        tvEndTime.setText("0");

        btStartLoop = (Button)rootView.findViewById(R.id.btnStartLoop);  ///Button that sets startTime while playing video.
        ///Set startPosition of video by pressing "START LOOP" Button
        btStartLoop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPos = m_currentTime;
                showMessage("Button Start");
                tvStartTime.setText(String.format("%d", startPos));
            }
        });
        btEndLoop = (Button)rootView.findViewById(R.id.btnEndLoop);      ///Button that sets endTime while playing video.
        ///Set endPosition of video by pressing "END LOOP" Button
        btEndLoop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endPos = m_currentTime;
                showMessage("Button End");
                tvEndTime.setText(String.format("%d", endPos));
            }
        });

        tgSwitch = (ToggleButton)rootView.findViewById(R.id.tgSwitch);   ///ToggleButton that sets loop.
        tgSwitch.setChecked(false);
        tgSwitch.setOnClickListener(new View.OnClickListener() {   /////Set loopable flag.
            @Override
            public void onClick(View v) {
                if (mbLoopable) {
                    tvStartTime.setTextColor(Color.parseColor("#000000"));
                    tvEndTime.setTextColor(Color.parseColor("#000000"));
                    mbLoopable = false;
                } else {
                    ///check current time is in duration of startPosition and endPosition.
                    String strStartPos = tvStartTime.getText().toString();
                    String strEndPos = tvEndTime.getText().toString();
                    int nTmpStartPos, nTmpEndPos;
                    if (strStartPos.length() != 0)
                        nTmpStartPos = Integer.parseInt(strStartPos);
                    else {
                        nTmpStartPos = 0;
                        tvStartTime.setText("0");
                    }
                    if (strEndPos.length() != 0)
                        nTmpEndPos = Integer.parseInt(strEndPos);
                    else {
                        nTmpEndPos = 0;
                        tvEndTime.setText("0");
                    }
                    ///if start position is bigger than end position then can not loop.
                    if (nTmpStartPos >= nTmpEndPos) {
                        tvStartTime.setTextColor(Color.parseColor("#000000"));
                        tvEndTime.setTextColor(Color.parseColor("#000000"));
                        mbLoopable = false;
                        Toast.makeText(mActivity, "Start time is bigger than End time.", Toast.LENGTH_LONG).show();
                        tgSwitch.setChecked(false);
                    } else {
                        tvStartTime.setTextColor(Color.parseColor("#FF0000"));
                        tvEndTime.setTextColor(Color.parseColor("#0000FF"));
                        getStartEndTime();
                        if ((m_currentTime < startPos) || (m_currentTime > endPos)) {
                            m_currentTime = startPos;
                            m_player.seekToMillis(startPos);
                        }
                        bStartCheckFlag = false;
                        bEndCheckFlag = false;
                        mbLoopable = true;
                    }
                }
            }
        });
    }


    //From the first to number of hashtable keys, Search index that its value is bigger than
    // current time. Then sets the text that was finded in hashtable keys.
    public  void changeText(int currentTime) {
        for ( int nIndex = 0; nIndex < arrayKeys.length -1; nIndex++ )
        {
            if ( arrayKeys[nIndex] <= currentTime && arrayKeys[nIndex + 1] > currentTime )
            {
                if( arrayCallStatus[nIndex] )
                    return;

                arrayCallStatus[nIndex] = true;
                ConnectThread connectThread = new ConnectThread(Util.str2array((String) lstTimeText.get(arrayKeys[nIndex])));
                connectThread.run();
                Util.setDefaultValues(arrayCallStatus);
                arrayCallStatus[nIndex] = true;

            }
        }

        if ( arrayKeys[arrayKeys.length -1] <= currentTime )
        {
            if( arrayCallStatus[arrayKeys.length -1] )
                return;

            arrayCallStatus[arrayKeys.length -1] = true;
            ConnectThread connectThread = new ConnectThread(Util.str2array((String) lstTimeText.get(arrayKeys[arrayKeys.length - 1])));
            connectThread.run();
            Util.setDefaultValues(arrayCallStatus);
            arrayCallStatus[arrayKeys.length -1] = true;
        }
    }

    public void initTxt(int resId) {
        String str= Util.readRawTextFile(mActivity, resId);
        String[] strArray = str.split( "\n" );
        lstTimeText = new Hashtable();
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

    ///Set startPos and endPos from TextView of tvStartTime and tvEndTime
    ///Convert String data of TextView to Integer data.
    void getStartEndTime() {
        if(tvStartTime.getText().toString().length() != 0)
            startPos = Integer.parseInt(tvStartTime.getText().toString());
        else
            startPos = 0;
        if(tvEndTime.getText().toString().length() != 0)
            endPos = Integer.parseInt(tvEndTime.getText().toString());
        else
            endPos = 0;
    }


    private final class MyPlaybackEventListener implements YouTubePlayer.PlaybackEventListener {

        Runnable runnable1 = new Runnable() {
            @Override
            public void run() {
                {
                    try{
                        ///Set currentTime to current time textview.
                        if (m_player == null){
                            return;
                        }
                        if (!m_player.isPlaying()){
                            return;
                        }
                        m_currentTime = m_player.getCurrentTimeMillis();
                        showMessage("CurrnetTime==="+ m_currentTime);

                        ///Set the current title of current time.
                        changeText(m_currentTime);
                        if(mbLoopable){
                            if(startPos >= endPos) {
                                mbLoopable = false;
                                tgSwitch.setChecked(false);
                            }else{
                                ///if currentTime is smaller than startPos then set bStartCheckFlag
                                // true.
                                //and set endChekFlag to false. Set current pos to startPos. and loop
                                // video.
                                if((m_currentTime + 500 < startPos) && (!bStartCheckFlag)){
                                    m_player.seekToMillis(startPos);
                                    bStartCheckFlag = true;
                                    bEndCheckFlag = false;
                                }

                                ///if currentTime is bigger than startPos then set bEndCheckFlag
                                // true.
                                //and set startChekFlag to false.
                                ///Set current pos to startPos. and loop video.
                                if((m_currentTime > endPos) && (!bEndCheckFlag)){
                                    bEndCheckFlag = false;
                                    bStartCheckFlag = true;
                                    showMessage("Big Case why : begin");

                                    m_player.seekToMillis(startPos);

                                    showMessage("Current Time =====" + startPos);
                                    showMessage("Current Time =====" + m_currentTime);
                                    showMessage("Big Case why : end");
                                }
                            }
                        }
                        mCurTimeShowHandler.postDelayed(this, 1000);
                    }catch (IllegalStateException e){
                        mCurTimeShowHandler.removeCallbacks(this);
                    }
                }
            }
        };
        @Override
        public void onPlaying() {
            // Called when playback starts, either due to user action or call to play().

            bStartCheckFlag = false;        ///Flag that current time is passed start time.
            bEndCheckFlag = false;          ///Flag that current time is passed end time.

            showMessage("Playing");
            mbPlaying = true;

            getStartEndTime();
            //  This is runnable thread that sets currentTime to tvCurTime TextView and check loop
            //  available through startPos and endPos

            mCurTimeShowHandler.post(runnable1);
        }

        @Override
        public void onPaused() {
            // Called when playback is paused, either due to user action or call to pause().
            showMessage("Paused");

            mbPlaying = false;

            Util.stopViaData();

            bStartCheckFlag = false;        ///Flag that current time is passed start time.

            bEndCheckFlag = false;          ///Flag that current time is passed end time.

        }

        @Override
        public void onStopped() {
            // Called when playback stops for a reason other than being paused.
            mbPlaying = false;
        }

        @Override
        public void onBuffering(boolean b) {
            // Called when buffering starts or ends.
        }

        @Override
        public void onSeekTo(int currentTime) {
            // Called when a jump in playback position occurs, either
            // due to user scrubbing or call to seekRelativeMillis() or seekToMillis()


            if (mbLoopable) {
                ///When current time is setted from seeking timeline,
                ///If current time is smaller than start position then set bStartChekFlag false.
                ///current time is smaller than start position, compare start position and
                // current time in thread.it must check in thread.
                ///Also sets bEndCheckFlag false when current time is bigger than end position.
                ///So the thread have to compare between end position and current time.
                if (currentTime < startPos) {
                    bStartCheckFlag = false;
                } else if (currentTime > endPos){
                    bEndCheckFlag = false;
                } else {
                    ///When current time is in the duration of loop.
                    bStartCheckFlag = true;
                    bEndCheckFlag = false;
                }
            }
        }
    }
    ////////////////////////////////StateChangeListener//////////////
    private final class MyPlayerStateChangeListener implements YouTubePlayer.PlayerStateChangeListener {

        @Override
        public void onLoading() {
            // Called when the player is loading a video
            // At this point, it's not ready to accept commands affecting playback such as play() or pause()
            showMessage("Loading!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }

        @Override
        public void onLoaded(String s) {
            // Called when a video is done loading.
            // Playback methods such as play(), pause() or seekToMillis(int) may be called after this callback.
            showMessage("loaded!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }

        @Override
        public void onAdStarted() {
            // Called when playback of an advertisement starts.
            showMessage("AdStarted!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }

        @Override
        public void onVideoStarted() {
            // Called when playback of the video starts.
            showMessage("VideoStarted!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }

        @Override
        public void onVideoEnded() {
            // Called when the video reaches its end.
            showMessage("VideoEnded!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }

        @Override
        public void onError(YouTubePlayer.ErrorReason errorReason) {
            // Called when an error occurs.
        }
    }

    /////////////////////////////////BlueToothConnection/////////////////////////
    static private class ConnectThread extends Thread {
        byte[] array;
        public ConnectThread(byte[] tmp) {
            array = tmp;
        }

        public void run() {
            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                Util.startViaData(array);
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
    private static void showMessage(String message) {
        Log.d("+++", message);
    }
}