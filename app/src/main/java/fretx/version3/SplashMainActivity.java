package fretx.version3;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ypyproductions.task.IDBCallback;

/**
 * 
 * 
 * @author:YPY Productions
 * @Skype: baopfiev_k50
 * @Mobile : +84 983 028 786
 * @Email: dotrungbao@gmail.com
 * @Website: www.ypyproductions.com
 * @Project:CloundMusicDownloader
 * @Date:Nov 13, 2014
 * 
 */
public class SplashMainActivity extends Activity {


	private ProgressBar mProgressBar;
	private boolean isPressBack;

	private Handler mHandler = new Handler();
	private TextView mTvCopyright;

	private TextView mTvVersion;
	private boolean isLoading;
	private TextView mTvAppName;
	private boolean isStartAnimation;
	private ImageView mImgLogo;
	protected boolean isShowingDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(null);

		this.setContentView(R.layout.splash_activity);
		this.mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
		this.mTvCopyright = (TextView) findViewById(R.id.tv_copyright);
		this.mTvVersion = (TextView) findViewById(R.id.tv_version);
		this.mTvAppName = (TextView) findViewById(R.id.tv_app_name);

		mImgLogo = (ImageView) findViewById(R.id.img_logo);


		mProgressBar.setVisibility(View.INVISIBLE);
		mTvAppName.setVisibility(View.INVISIBLE);

	}

	@Override
	protected void onResume() {
		super.onResume();


			if (!isLoading) {
				isLoading = true;
				startAnimationLogo(new IDBCallback() {
					@Override
					public void onAction() {
						mProgressBar.setVisibility(View.VISIBLE);
						mTvAppName.setVisibility(View.VISIBLE);

						mHandler.postDelayed(new Runnable() {
							@Override
							public void run() {
								mProgressBar.setVisibility(View.INVISIBLE);
								Intent mIntent = new Intent(SplashMainActivity.this, Login.class);
								startActivity(mIntent);
								finish();
							}
						}, 3000);
					}
				});
			}


	}

	private void startAnimationLogo(final IDBCallback mCallback) {
		if (!isStartAnimation) {
			isStartAnimation = true;
			mProgressBar.setVisibility(View.INVISIBLE);
			mImgLogo.setRotationY(-180);

			AccelerateDecelerateInterpolator mInterpolator = new AccelerateDecelerateInterpolator();
			final ViewPropertyAnimator localViewPropertyAnimator = mImgLogo.animate().rotationY(0).setDuration(1000).setInterpolator(mInterpolator);

			localViewPropertyAnimator.setListener(new AnimatorListener() {
				@Override
				public void onAnimationStart(Animator animation) {

				}

				@Override
				public void onAnimationRepeat(Animator animation) {

				}

				@Override
				public void onAnimationEnd(Animator animation) {
					if (mCallback != null) {
						mCallback.onAction();
					}
				}

				@Override
				public void onAnimationCancel(Animator animation) {
					if (mCallback != null) {
						mCallback.onAction();
					}
				}
			});
			localViewPropertyAnimator.start();
		}
		else {
			if (mCallback != null) {
				mCallback.onAction();
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mHandler.removeCallbacksAndMessages(null);
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isPressBack) {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
