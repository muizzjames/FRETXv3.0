package fretx.version3;


import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

public class LearnFragmentOne extends Fragment {

    static ObservableVideoView vvMain1;
    private Handler mCurTimeShowHandler = new Handler();

    RelativeLayout llMain;

    MainActivity mActivity;

    View rootView = null;

    Button   btLearned;
    Button   btPlayReplay;
    TextView tvTitle;

    Uri videoUri;

    int durationTime = 0;               ///Video duration

    int nReplay = 0;

    public LearnFragmentOne(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mActivity = (MainActivity)getActivity();
        rootView = inflater.inflate(R.layout.learn_fragment_play, container, false);
        initUI();       ///Init UI(textView, VideoView....)
        return rootView;
    }

    private void initUI() {


        llMain = (RelativeLayout)rootView.findViewById(R.id.llVideoView);
        tvTitle = (TextView)rootView.findViewById(R.id.tvTitle);
        videoUri = Uri.parse("android.resource://" + mActivity.getPackageName() + "/" + R.raw.learn_ex1);
        vvMain1 = (ObservableVideoView)rootView.findViewById(R.id.vvMain);
        vvMain1.setVideoURI(videoUri);

        vvMain1.start();
        durationTime = vvMain1.getDuration(); //Get video duration.
//        vvMain1.pause();

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
            {
                if (!vvMain1.isPlaying()){
                    startButton();
                }
                mCurTimeShowHandler.postDelayed(this, 1);
            }
            }
        };
        mCurTimeShowHandler.post(runnable);

        btLearned = (Button)rootView.findViewById(R.id.btLearned);
        btLearned.setVisibility(View.INVISIBLE);
        btLearned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v4.app.FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
                LearnFragmentDialog yesnoDialog = new LearnFragmentDialog();
                yesnoDialog.setCancelable(true);
                yesnoDialog.setDialogTitle("Congrats");
                yesnoDialog.show(fragmentManager, "Yes/No Dialog");
            }
        });
        btPlayReplay = (Button)rootView.findViewById(R.id.btPlayReplay);
        btPlayReplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btPlayReplay.setVisibility(View.INVISIBLE);
                vvMain1.start();
            }
        });
    }
    public void startButton(){
        btLearned.setVisibility(View.VISIBLE);
        btPlayReplay.setVisibility(View.VISIBLE);
        if (nReplay != 0)
            btPlayReplay.setText("Replay");
        nReplay = 1;
    }
    public static VideoView getVideoView(){
        return vvMain1;
    }
}
