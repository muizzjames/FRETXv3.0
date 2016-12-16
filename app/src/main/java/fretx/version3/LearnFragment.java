package fretx.version3;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class LearnFragment extends Fragment {

    private MainActivity mActivity;

    private View rootView = null;

    public LearnFragment(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mActivity = (MainActivity) getActivity();

        rootView = inflater.inflate(R.layout.learn_fragment, container, false);

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.learn_container, new LearnFragmentButton());
                fragmentTransaction.commit();

            }
        });
        return rootView;
    }
}