package fretx.version3;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class LearnFragmentDialog extends DialogFragment {
	public Button btnYes,btnNo;
	static String DialogBoxTitle;
	MainActivity mActivity;

	//---empty constructor required
	public LearnFragmentDialog(){
		
	}
	//---set the title of the dialog window---
	public void setDialogTitle(String title) {
		DialogBoxTitle= title;
	}
	
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState ) {

		mActivity = (MainActivity)getActivity();
		View view= inflater.inflate(R.layout.learn_fragment_dialog, container);
		//---get the Button views---
		btnYes = (Button) view.findViewById(R.id.btnYes);
		btnNo = (Button) view.findViewById(R.id.btnNo);
		
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

				FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
				android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
				fragmentTransaction.replace(R.id.learn_container, new LearnFragmentButton());
				fragmentTransaction.commit();

			}else if(((Button) v).getText().toString().equals("Next Exercise")) {

				FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
				android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
				fragmentTransaction.replace(R.id.learn_container, new LearnFragmentTwo());
				fragmentTransaction.commit();
			}
			dismiss();
		}
	};
}
