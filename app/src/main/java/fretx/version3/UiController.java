package fretx.version3;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

import fretx.version3.SoundAnalyzer.*;
import fretx.version3.Tuning.GuitarString;

import java.util.Observable;
import java.util.Observer;


public class UiController implements Observer, OnItemSelectedListener {
	public static final String TAG = "GuitarTuner";

	public static double test = 0f;

	private TunerFragment ui;
	private double frequency;
	private Tuning tuning = new Tuning(0);
	
	private enum MessageClass {
		TUNING_IN_PROGRESS,
		WEIRD_FREQUENCY,
		TOO_QUIET,
		TOO_NOISY,
	}
	
	MessageClass message;
	MessageClass previouslyProposedMessage;
	MessageClass proposedMessage; // needs to get X consecutive votes.
	private int numberOfVotes;
	private final int minNumberOfVotes = 3; // X.
	
	public UiController(TunerFragment u) {
		ui = u;
	}
	
	@Override
	public void update(Observable who, Object obj) {
		if(who instanceof SoundAnalyzer) {
			if(obj instanceof AnalyzedSound) {
				AnalyzedSound result = (AnalyzedSound)obj;
				// result.getDebug();
				frequency = FrequencySmoothener.getSmoothFrequency(result);
				if(result.error==AnalyzedSound.ReadingType.BIG_FREQUENCY ||
						result.error==AnalyzedSound.ReadingType.BIG_VARIANCE ||
						result.error==AnalyzedSound.ReadingType.ZERO_SAMPLES)
					proposedMessage = MessageClass.TOO_NOISY;
				else if(result.error==AnalyzedSound.ReadingType.TOO_QUIET)
					proposedMessage = MessageClass.TOO_QUIET;
				else if(result.error==AnalyzedSound.ReadingType.NO_PROBLEMS)
					proposedMessage = MessageClass.TUNING_IN_PROGRESS;
				else {
					Log.e(TAG, "UiController: Unknown class of message.");
					proposedMessage=null;
				}
				if(ConfigFlags.uiControlerInformsWhatItKnowsAboutSound)
					result.getDebug();
				//Log.e(TAG,"Frequency: " + frequency);
				updateUi();
			} else if(obj instanceof ArrayToDump) {
				ArrayToDump a = (ArrayToDump)obj;
				ui.dumpArray(a.arr, a.elements);
			}
		}
	}
	
	public void updateUi() {
		GuitarString current = tuning.getString(frequency);
		// Mark a string in red on a big picture of guitar.
		ui.changeString(current.stringId);
		String stringName ="";
		switch (current.stringId){
			case 1: stringName = (7 - current.stringId) + "-st String " + current.name + " " + String.format("%.2f", frequency) + " Hz"; break;
			case 2: stringName = (7 - current.stringId) + "-nd String " + current.name + " " + String.format("%.2f", frequency) + " Hz"; break;
			case 3: stringName = (7 - current.stringId) + "-rd String " + current.name + " " + String.format("%.2f", frequency) + " Hz"; break;
			case 4: stringName = (7 - current.stringId) + "-th String " + current.name + " " + String.format("%.2f", frequency) + " Hz"; break;
			case 5: stringName = (7 - current.stringId) + "-th String " + current.name + " " + String.format("%.2f", frequency) + " Hz"; break;
			case 6: stringName = (7 - current.stringId) + "-th String " + current.name + " " + String.format("%.2f", frequency) + " Hz"; break;
			default: stringName =""; break;
		}

		// Change color of your guitar.

		double match = 0.0; // How close is current frequency to the desired

		                    // frequency in 0..1 scale.
		if(current.stringId == 0) {
			match = 0.0;
			test = 0.0;
		} else {
			if(frequency<current.freq) {
				match = (frequency-current.minFreq)/(current.freq-current.minFreq);
				//test = Math.pow(match, 1.5);
				test = (100-Math.round(100.0*match))/2/(-10f);
				/*Log.d(TAG,"frequencyMin " + frequency);
				Log.d(TAG,"current.freq " + current.freq);
				Log.d(TAG,"current.minFreq " + current.minFreq);
				Log.d(TAG,"current.maxFreq " + current.maxFreq);*/
			} else {
				match = (current.maxFreq - frequency )/(current.maxFreq-current.freq);
				//test = Math.pow(match, 1.5);

				test = (100-Math.round(100.0*match))/2/10f;
				/*Log.d(TAG,"frequencyMax " + frequency);
				Log.d(TAG,"current.freq " + current.freq);
				Log.d(TAG,"current.maxFreq " + current.maxFreq);
				Log.d(TAG,"current.minFreq " + current.minFreq);*/

			}
		}
		ui.coloredGuitarMatch(Math.pow(match, 1.5));
		//test = Math.pow(match, 1.5);
		//test = Math.round(100.0*match)/2/10;
		Log.d(TAG, "match " + match);
		
		// Update message. 
		// If cannot decide on a string
		if(proposedMessage == MessageClass.TUNING_IN_PROGRESS && current.stringId == 0)
			proposedMessage = MessageClass.WEIRD_FREQUENCY;
		if(message == null) {
			message = previouslyProposedMessage = proposedMessage;
		} if(message == proposedMessage) {
			// do nothing.
		} else {
			if(previouslyProposedMessage != proposedMessage) {
				previouslyProposedMessage = proposedMessage;
				numberOfVotes = 1;
			} else if(previouslyProposedMessage == proposedMessage) {
				numberOfVotes++;
			}
			if(numberOfVotes >= minNumberOfVotes) {
				message = proposedMessage;
			}
		}
		switch(message) {
			case TUNING_IN_PROGRESS:
				ui.displayMessage("Currently tuning string " + current.name +
						" from " + tuning.getName() + " tuning, matched in " + 
						Math.round(100.0*match) + "%.", stringName, true);
				Log.d(TAG, "current.name " + current.name);
				Log.d(TAG, "tuning.getName " + tuning.getName());
				break;
			case TOO_NOISY:
				ui.displayMessage("Please reduce background noise (or play louder).", "", false);
				break;
			case TOO_QUIET:
				ui.displayMessage("Please play louder!","",false);
				break;
			case WEIRD_FREQUENCY:
				ui.displayMessage("Are you sure instrument you are playing is guitar? :)", "", false);
			default:
				Log.d(TAG, "No message");
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View thing, int itemno,
			long rowno) {
		if(tuning.getTuningId() != itemno)
			tuning = new Tuning(itemno);
		Log.d(TAG,"Changed tuning to " + tuning.getName());
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// do nothing
	}
	


}
