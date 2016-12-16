package fretx.version3;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import fretx.version3.audio.CaptureThread;
import fretx.version3.graphics.DialView;


public class PTuneFragment extends Fragment {
	private DialView dial;
	private TextView t;
	private TextView tvSelectView;
	//private CaptureTask capture;
	private float targetFrequency;
	private CaptureThread mCapture;
	private Handler mHandler;

	MainActivity mActivity;

	View rootView;

	public PTuneFragment(){

	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		mActivity = (MainActivity) getActivity();

		rootView = inflater.inflate(R.layout.main_game, container, false);

		tvSelectView = (TextView) rootView.findViewById(R.id.tvSelectItemView);

        dial = (DialView) rootView.findViewById(R.id.dial);
        t = (TextView) rootView.findViewById(R.id.textView1);

		init();



        mHandler = new Handler() {
        	@Override
        	public void handleMessage(Message m) {
        		updateDisplay(m.getData().getFloat("Freq"));
        	}
        };
        
        mCapture = new CaptureThread(mHandler);
        mCapture.setRunning(true);
        mCapture.start();

		return rootView;
        //Log.d("PTuneActivity", "onCreate called.");
    }
    public void init(){
		RadioGroup radioGroup = (RadioGroup)rootView.findViewById(R.id.radioGroup1);
		RadioButton rb;
		rb = (RadioButton) rootView.findViewById(R.id.radio0);
		rb.setText("E4");
		rb.setTag("329.628");
		rb = (RadioButton) rootView.findViewById(R.id.radio1);
		rb.setText("B3");
		rb.setTag("246.942");
		rb = (RadioButton) rootView.findViewById(R.id.radio2);
		rb.setText("G3");
		rb.setTag("195.998");
		rb = (RadioButton) rootView.findViewById(R.id.radio3);
		rb.setText("D3");
		rb.setTag("146.832");
		rb = (RadioButton) rootView.findViewById(R.id.radio4);
		rb.setText("A2");
		rb.setTag("110.000");
		rb = (RadioButton) rootView.findViewById(R.id.radio5);
		rb.setText("E2");
		rb.setTag("82.4069");
		radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				updateTargetFrequency();
			}
		});
		updateTargetFrequency();
	}
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	
    	if (mCapture != null) {
    		mCapture.setRunning(false);
    		mCapture = null;
    	}
    	
    	//Log.d("PTuneActivity", "onDestroy called.");
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	
    	mCapture.setRunning(false);
    	
    	//Log.d("PTuneActivity", "onPause called.");
    }
    
    @Override
    public void onResume() {
    	super.onResume();  

    	updateTargetFrequency(); // Get radio button selection
        
        //Log.d("PTuneActivity", "onResume called.");
    }
    
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu, menu);
//        return true;
//    }
    
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//    	RadioButton rb;
//        // Handle item selection
//        switch (item.getItemId()) {
//            case R.id.b_std:
//            	rb = (RadioButton) findViewById(R.id.radio0);
//            	rb.setText("B3");
//            	rb.setTag("246.942");
//            	rb = (RadioButton) findViewById(R.id.radio1);
//            	rb.setText("G3");
//            	rb.setTag("195.998");
//            	rb = (RadioButton) findViewById(R.id.radio2);
//            	rb.setText("D3");
//            	rb.setTag("146.832");
//            	rb = (RadioButton) findViewById(R.id.radio3);
//            	rb.setText("A2");
//            	rb.setTag("110.000");
//            	rb = (RadioButton) findViewById(R.id.radio4);
//            	rb.setText("E2");
//            	rb.setTag("82.4069");
//            	rb = (RadioButton) findViewById(R.id.radio5);
//            	rb.setText("B1");
//            	rb.setTag("61.7354");
//            	updateTargetFrequency();
////            	Toast.makeText(PTuneActivity.this, R.string.b_std, Toast.LENGTH_SHORT).show();
//                return true;
//            case R.id.e_std:
//            	rb = (RadioButton) findViewById(R.id.radio0);
//            	rb.setText("E4");
//            	rb.setTag("329.628");
//            	rb = (RadioButton) findViewById(R.id.radio1);
//            	rb.setText("B3");
//            	rb.setTag("246.942");
//            	rb = (RadioButton) findViewById(R.id.radio2);
//            	rb.setText("G3");
//            	rb.setTag("195.998");
//            	rb = (RadioButton) findViewById(R.id.radio3);
//            	rb.setText("D3");
//            	rb.setTag("146.832");
//            	rb = (RadioButton) findViewById(R.id.radio4);
//            	rb.setText("A2");
//            	rb.setTag("110.000");
//            	rb = (RadioButton) findViewById(R.id.radio5);
//            	rb.setText("E2");
//            	rb.setTag("82.4069");
//            	updateTargetFrequency();
////            	Toast.makeText(PTuneActivity.this, R.string.e_std, Toast.LENGTH_SHORT).show();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }
//
    private void updateTargetFrequency() {
    	// Grab the selected radio button tag.
        RadioGroup rg = (RadioGroup) rootView.findViewById(R.id.radioGroup1);
        int selected = rg.getCheckedRadioButtonId();
        RadioButton rb = (RadioButton) rootView.findViewById(selected);
		tvSelectView.setText((String) rb.getTag() + "Hz");
        targetFrequency = Float.parseFloat((String) rb.getTag());
    }
    
    public void updateDisplay(float frequency) {
    	// Calculate difference between target and measured frequency,
    	// given that the measured frequency can be a factor of target.
    	float difference = 0;
    	if (frequency > targetFrequency) {
    		int divisions = (int) (frequency / targetFrequency);
    		float modified = targetFrequency * (float) divisions;
    		if (frequency - modified > targetFrequency / 2) {
    			modified += targetFrequency;
    			divisions++;
    		}
    		difference = (frequency - modified) / (float) divisions;
    	} else {
    		// If target is greater than measured, just use difference.
    		difference = frequency - targetFrequency;
    	}
    	
    	float relativeFrequency = targetFrequency + difference;
    	
    	// Update TextView
    	if (relativeFrequency < 1000f)
			t.setText(String.format("%.1f Hz", relativeFrequency));
		else
			t.setText(String.format("%.2f kHz", relativeFrequency / 1000));

    	// Update DialView
    	float value = difference / (targetFrequency / 2) * 90;
		dial.update(value);
    }
    public void onRadioButtonClicked(View v) {
    	// Perform action on clicks
    	RadioButton rb = (RadioButton) v;
//    	Toast.makeText(PTuneActivity.this, rb.getText(), Toast.LENGTH_SHORT).show();
    	targetFrequency = Float.parseFloat((String) rb.getTag());
    }
}