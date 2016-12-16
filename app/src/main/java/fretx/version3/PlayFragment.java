package fretx.version3;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class PlayFragment extends Fragment {

    private MainActivity mActivity;

    private View rootView = null;

    public PlayFragment(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mActivity = (MainActivity) getActivity();

        rootView = inflater.inflate(R.layout.play_fragment, container, false);

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {


                FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.play_container, new PlayFragmentSearchList());
                fragmentTransaction.commit();
            }
        });
        return rootView;
    }
}