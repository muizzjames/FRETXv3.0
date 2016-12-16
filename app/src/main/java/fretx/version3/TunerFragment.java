package fretx.version3;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;


public class TunerFragment extends Fragment {
	// switch off gc logs.
	// System.setProperty("log.tag.falvikvm", "SUPPRESS"); 
	public static final String TAG = "AndroidTuner";
	
	private final boolean LAUNCHANALYZER = true;
	
	private ImageView guitar = null;
	private ImageView tune = null;
	private Spinner tuningSelector = null;

	private UiController uiController = null;
	private TextView mainMessage = null;
	private TextView stringMessage = null;
	private Meter meter;
	private Toast toast;

	private static final String notes[] =
			{"C", "C", "D", "E", "E", "F",
					"F", "G", "A", "A", "B", "B"};

	private static final String sharps[] =
			{"", "\u266F", "", "\u266D", "", "",
					"\u266F", "", "\u266D", "", "\u266D", ""};

	MainActivity mActivity;

	View rootView;


	public TunerFragment(){

	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		mActivity = (MainActivity) getActivity();

		rootView = inflater.inflate(R.layout.tuner_fragment, container, false);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				mActivity);

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();

        uiController = new UiController(this);
        if(LAUNCHANALYZER) {
	        try {
				if (Config.soundAnalyzer == null){
					Config.soundAnalyzer = new SoundAnalyzer();
				}

	        } catch(Exception e) {
	        	Toast.makeText(mActivity, "The are problems with your microphone :(", Toast.LENGTH_LONG ).show();
	        	Log.e(TAG, "Exception when instantiating SoundAnalyzer: " + e.getMessage());
	        }
			Config.soundAnalyzer.addObserver(uiController);
        }
        guitar = (ImageView)rootView.findViewById(R.id.guitar);
        tune = (ImageView)rootView.findViewById(R.id.tune);
        mainMessage = (TextView)rootView.findViewById(R.id.mainMessage);
		stringMessage = (TextView)rootView.findViewById(R.id.nameString);
        tuningSelector = (Spinner)rootView.findViewById(R.id.tuningSelector);
        Tuning.populateSpinner(mActivity, tuningSelector);
        tuningSelector.setOnItemSelectedListener(uiController);
		meter = (Meter)rootView.findViewById(R.id.meter);

		return  rootView;
    }


	private Map<Integer, Bitmap> preloadedImages;
	private BitmapFactory.Options bitmapOptions;

	private Bitmap getAndCacheBitmap(int id) {
		if(preloadedImages == null)
			preloadedImages = new HashMap<Integer,Bitmap>();
		Bitmap ret = preloadedImages.get(id);
		if(ret == null) {
			if(bitmapOptions == null) {
				BitmapFactory.Options options=new BitmapFactory.Options();
				options.inSampleSize = 4; // The higher it goes, the smaller the image.
			}
			ret = BitmapFactory.decodeResource(getResources(), id, bitmapOptions);
			preloadedImages.put(id, ret);
		}
		return ret;
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
	
	private int [] stringNumberToImageId = new int[]{
			R.drawable.strings0,
			R.drawable.strings1,
			R.drawable.strings2,
			R.drawable.strings3,
			R.drawable.strings4,
			R.drawable.strings5,
			R.drawable.strings6
	};

    int oldString = 0;
    // 1-6 strings (ascending frequency), 0 - no string.
    public void changeString(int stringId) {
    	if(oldString!=stringId) {
    		guitar.setImageBitmap(getAndCacheBitmap(stringNumberToImageId[stringId]));
        	oldString=stringId;
    	}
    }
    
	int [] targetColor =         new int[]{160,80,40};
	int [] awayFromTargetColor = new int[]{160,160,160};

    
    public void coloredGuitarMatch(double match) {
        tune.setBackgroundColor(
        		Color.rgb((int)(match*targetColor[0]+ (1-match)*awayFromTargetColor[0]),
        				  (int)(match*targetColor[1]+ (1-match)*awayFromTargetColor[1]),
        				  (int)(match*targetColor[2]+ (1-match)*awayFromTargetColor[2])));
        
    }
    
    public void displayMessage(String msg, String stringName, boolean positiveFeedback) {
    	int textColor = positiveFeedback ? Color.rgb(34,139,34) : Color.rgb(255,36,0);
    	mainMessage.setText(msg);
		stringMessage.setText(stringName);
    	mainMessage.setTextColor(textColor);
    }
    
    
    @Override
	public  void onDestroy() {
		super.onDestroy();
        Log.d(TAG, "onDestroy()");

		// Get rid of all those pesky objects
		meter = null;
		toast = null;

		// Hint that it might be a good idea
		System.runFinalization();

	}

	@Override
	public void onPause() {
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
	public void onStop() {
		super.onStop();
        Log.d(TAG,"onStop()");
        if(Config.soundAnalyzer!=null)
			Config.soundAnalyzer.stop();
	}

}