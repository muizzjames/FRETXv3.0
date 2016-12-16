package fretx.version3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;


/**
 * The class Login is an Activity class that is launched after Splash screen at
 * shows the interface for Login to the app with the options for Login using
 * Facebook and Twitter and also includes the options for Register and recover
 * password.
 */
public class Login extends Activity
{


	private Button btnFb;
	private CallbackManager mCallbackManager;

	String userId;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
		btnFb = (Button)findViewById(R.id.btnFb);



		FacebookSdk.sdkInitialize(getApplicationContext());

		mCallbackManager = CallbackManager.Factory.create();
		btnFb.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				LoginManager.getInstance().logInWithReadPermissions(Login.this, Arrays.asList("public_profile", "user_friends", "user_location", "user_birthday"));
			}
		});
		if (AccessToken.getCurrentAccessToken() != null){
			Intent intent = new Intent(Login.this, PresentationActivity.class);
			startActivity(intent);
		}
		LoginManager.getInstance().registerCallback(mCallbackManager,
				new FacebookCallback<LoginResult>() {
					@Override
					public void onSuccess(LoginResult loginResult) {
						final AccessToken accessToken = loginResult.getAccessToken();
						GraphRequestAsyncTask request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
							@Override
							public void onCompleted(JSONObject user, GraphResponse graphResponse) {
								Log.d("FB", user.optString("email"));
								Log.d("FB", user.optString("name"));
								Log.d("FB", user.optString("id"));
								Config.strUserID = user.optString("id");

							}
						}).executeAsync();
						Intent intent = new Intent(Login.this, PresentationActivity.class);
						startActivity(intent);
					}

					@Override
					public void onCancel() {
						Toast.makeText(Login.this, "Login Cancel", Toast.LENGTH_LONG).show();
					}

					@Override
					public void onError(FacebookException exception) {
						Toast.makeText(Login.this, exception.getMessage(), Toast.LENGTH_LONG).show();
					}
				});

	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		mCallbackManager.onActivityResult(requestCode, resultCode, data);
	}

    @Override
    public void onBackPressed() {
    }
}
