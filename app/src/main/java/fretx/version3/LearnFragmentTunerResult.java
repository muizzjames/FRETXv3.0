package fretx.version3;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class LearnFragmentTunerResult extends DialogFragment {
	public Button btnYes,btnNo;
	static String DialogBoxTitle;

	public TextView tvText;


	//---empty constructor required
	public LearnFragmentTunerResult(){
		
	}
	//---set the title of the dialog window---
	public void setDialogTitle(String title) {
		DialogBoxTitle= title;
	}
	
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState ) {
		
		View view= inflater.inflate(R.layout.learn_fragment_dialog, container);
		//---get the Button views---
		btnYes = (Button) view.findViewById(R.id.btnYes);
		btnNo = (Button) view.findViewById(R.id.btnNo);
		btnNo.setVisibility(View.INVISIBLE);

		tvText = (TextView) view.findViewById(R.id.tvTextView);
		tvText.setText("You just learned how to play an Em/Asus2 Chord!");

		// Button listener
		btnYes.setOnClickListener(btnListener);
		btnNo.setOnClickListener(btnListener);
		
		//---set the title for the dialog
		getDialog().setTitle(DialogBoxTitle);
		
		return view;
	}
	
	//---create an anonymous class to act as a button click listener
	private OnClickListener btnListener = new OnClickListener()
	{
		public void onClick(View v)
		{
			if (((Button) v).getText().toString().equals("Go to Learn Menu")){

			}
			//---dismiss the alert---
			dismiss();
		}
	};
}
