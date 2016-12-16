package fretx.version3;



import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class LearnFragmentButton extends Fragment {

    MainActivity mActivity;
    View rootView = null;

    Button btExerciseOne;
    Button btExerciseTwo;

    public LearnFragmentButton(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mActivity = (MainActivity)getActivity();
        rootView = inflater.inflate(R.layout.learn_fragment_buttons, container, false);
        btExerciseOne = (Button)rootView.findViewById(R.id.btExerciseOne);
        btExerciseOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.learn_container, new LearnFragmentOne());
                fragmentTransaction.commit();

            }
        });
        btExerciseTwo = (Button)rootView.findViewById(R.id.btExerciseTwo);
        btExerciseTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.learn_container, new LearnFragmentTwo());
                fragmentTransaction.commit();

            }
        });
        return rootView;
    }
}