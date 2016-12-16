package fretx.version3;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Timer;
import java.util.TimerTask;


public class LearnFragmentTuner extends Fragment{
	public static final String TAG = "AndroidTuner";
	
	private final boolean LAUNCHANALYZER = true;

	private int nFlag = 0;

	Timer timer;
	MyTimerTask myTimerTask;
	int nCounter = 0;

	private NewUiController uiController = null;

	private Button button1;
	private Button button2;
	private Button button3;
	private Button button4;
	private Button button5;
	private Button button6;


	private TextView tvTimeLapse;
	int oldString = 1;

	private static final String notes[] =
			{"C", "C", "D", "E", "E", "F",
					"F", "G", "A", "A", "B", "B"};

	private static final String sharps[] =
			{"", "\u266F", "", "\u266D", "", "",
					"\u266F", "", "\u266D", "", "\u266D", ""};

	private MainActivity mActivity;
	private View rootView;

	public LearnFragmentTuner(){

	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		mActivity = (MainActivity)getActivity();
		rootView = inflater.inflate(R.layout.learn_fragment_tuner, container, false);

		button1 = (Button)rootView.findViewById(R.id.button1);
		button1.setBackgroundColor(Color.GREEN);
		button2 = (Button)rootView.findViewById(R.id.button2);
		button2.setBackgroundColor(Color.BLUE);
		button3 = (Button)rootView.findViewById(R.id.button3);
		button3.setBackgroundColor(Color.BLUE);
		button4 = (Button)rootView.findViewById(R.id.button4);
		button4.setBackgroundColor(Color.BLUE);
		button5 = (Button)rootView.findViewById(R.id.button5);
		button5.setBackgroundColor(Color.BLUE);
		button6 = (Button)rootView.findViewById(R.id.button6);
		button6.setBackgroundColor(Color.BLUE);


		tvTimeLapse = (TextView)rootView.findViewById(R.id.tvTimeLapse);

		startTimer();

		uiController = new NewUiController(this);

		if(LAUNCHANALYZER) {
			try {
				if (Config.soundAnalyzer == null )
					Config.soundAnalyzer = new SoundAnalyzer();
			} catch(Exception e) {
				Toast.makeText(mActivity, "The are problems with your microphone :(", Toast.LENGTH_LONG ).show();
				Log.e(TAG, "Exception when instantiating SoundAnalyzer: " + e.getMessage());
			}
			Config.soundAnalyzer.addObserver(uiController);
		}
		return rootView;

    }
	public void startTimer(){
		if (timer != null){
			timer.cancel();
		}
		timer = new Timer();

		myTimerTask = new MyTimerTask();

		timer.schedule(myTimerTask, 1000, 1000);
	}
	public void successPlayer(){
		MediaPlayer mediaPlayer= MediaPlayer.create(mActivity, R.raw.success_sound);
		mediaPlayer.start();
	}

	class MyTimerTask extends TimerTask {

		@Override
		public void run() {

			nCounter ++;

			mActivity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					tvTimeLapse.setText("" + nCounter / 60 + " : " + nCounter % 60);
				}
			});
		}

	}

	// 1-6 strings (ascending frequency), 0 - no string.
	public void changeString(int stringId) {
		if(oldString ==stringId) {
			Log.d("+++", "changString" + stringId + "oldString" + oldString);

			if (oldString == 1){

				successPlayer();

				ConnectThread connectThread = new ConnectThread(Util.str2array("{6,0}"));
				connectThread.run();

				button1.setBackgroundColor(Color.BLUE);
				button2.setBackgroundColor(Color.GREEN);
				oldString = 2;
				return;
			}
			if (oldString == 2){
				successPlayer();

				ConnectThread connectThread = new ConnectThread(Util.str2array("{5,0}"));
				connectThread.run();

				button2.setBackgroundColor(Color.BLUE);
				button3.setBackgroundColor(Color.GREEN);
				oldString = 3;
				return;
			}
			if (oldString == 3){
				successPlayer();
				ConnectThread connectThread = new ConnectThread(Util.str2array("{4,0}"));
				connectThread.run();
				oldString = 4;
				button3.setBackgroundColor(Color.BLUE);
				button4.setBackgroundColor(Color.GREEN);
				return;
			}
			if (oldString == 4){
				successPlayer();
				ConnectThread connectThread = new ConnectThread(Util.str2array("{3,0}"));
				connectThread.run();
				oldString = 5;
				button4.setBackgroundColor(Color.BLUE);
				button5.setBackgroundColor(Color.GREEN);
				return;
			}
			if (oldString == 5){
				successPlayer();
				ConnectThread connectThread = new ConnectThread(Util.str2array("{2,0}"));
				connectThread.run();
				oldString = 6;
				button5.setBackgroundColor(Color.BLUE);
				button6.setBackgroundColor(Color.GREEN);
				return;
			}
			if (oldString == 6){
				successPlayer();
				ConnectThread connectThread = new ConnectThread(Util.str2array("{1,0}"));
				connectThread.run();
				oldString = 1;
				button6.setBackgroundColor(Color.BLUE);
				button1.setBackgroundColor(Color.GREEN);
				if (nFlag == 1){
					nFlag = 0;
					timer.cancel();
					if (nCounter <= 40){
						Config.nPoints = 5;
					}
					if (nCounter>40 && nCounter <=60){
						Config.nPoints = 4;
					}
					if (nCounter>60 && nCounter<=80){
						Config.nPoints = 3;
					}
					if (nCounter>80 && nCounter <=100){
						Config.nPoints = 2;
					}
					if (nCounter >100){
						Config.nPoints = 1;
					}
					Util.stopViaData();

					android.support.v4.app.FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
					LearnFragmentTunerResult yesnoDialog = new LearnFragmentTunerResult();
					yesnoDialog.setCancelable(true);
					yesnoDialog.setDialogTitle("Result");
					yesnoDialog.show(fragmentManager, "Yes/No Dialog");
				}else{
					nFlag = 1;
				}
				return;
			}
		}
	}


	public void coloredGuitarMatch(double match) {
	}

	String oldmsg = "";

	public void displayMessage(String msg, String stringName, boolean positiveFeedback) {
		int textColor = positiveFeedback ? Color.rgb(34,139,34) : Color.rgb(255,36,0);
		Log.d("***", "displayMessage:"+msg+":"+stringName+":"+textColor);
		if (msg.equals(oldmsg)){
			return;
		}

//		tvMessage.setText(msg +"\n" + stringName);
//		Toast.makeText(this, msg +"\n"+stringName, Toast.LENGTH_LONG ).show();
		oldmsg = msg;
	}


	public void dumpArray(final double [] inputArray, final int elements) {
		Log.d(TAG, "Starting File writer thread...");
		final double [] array = new double[elements];
		for(int i=0; i<elements; ++i)
			array[i] = inputArray[i];
		new Thread(new Runnable() {
			@Override
			public void run() {
				try { // catches IOException below
					// Location: /data/data/your_project_package_structure/files/samplefile.txt
					String name = "Chart_" + (int)(Math.random()*1000) + ".data";
					FileOutputStream fOut = mActivity.openFileOutput(name,
							Context.MODE_WORLD_READABLE);
					OutputStreamWriter osw = new OutputStreamWriter(fOut);

					// Write the string to the file
					for(int i=0; i<elements; ++i)
						osw.write("" + array[i] + "\n");
					/* ensure that everything is
					 * really written out and close */
					osw.flush();
					osw.close();
					Log.d(TAG, "Successfully dumped array in file " + name);
				} catch(Exception e) {
					Log.e(TAG,e.getMessage());
				}
			}
		}).start();
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if(ConfigFlags.menuKeyCausesAudioDataDump) {
			if (keyCode == KeyEvent.KEYCODE_MENU) {
				Log.d(TAG,"Menu button pressed");
				Log.d(TAG,"Requesting audio data dump");
				Config.soundAnalyzer.dumpAudioDataRequest();
				return true;
			}
		}
		return false;
	}
    @Override
	public void onDestroy() {
		super.onDestroy();
        Log.d(TAG, "onDestroy()");
		// Hint that it might be a good idea
		System.runFinalization();

	}

	@Override
	public  void onPause() {
		super.onPause();
        Log.d(TAG, "onPause()");
	}



	@Override
	public void onResume() {
		super.onResume();
        Log.d(TAG,"onResume()");
        if(Config.soundAnalyzer!=null)
			Config.soundAnalyzer.ensureStarted();
	}

	@Override
	public void onStart() {
		super.onStart();
        Log.d(TAG,"onStart()");
        if(Config.soundAnalyzer!=null)
			Config.soundAnalyzer.start();
	}

	@Override
	public  void onStop() {
		super.onStop();
        Log.d(TAG,"onStop()");
        if(Config.soundAnalyzer!=null)
			Config.soundAnalyzer.stop();
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
		}
	}
}