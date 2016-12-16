package fretx.version3;



import android.content.Intent;
import android.graphics.Color;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;

import de.hdodenhof.circleimageview.CircleImageView;
import fretx.version3.ShareMe.ShareMeActivity;


public class MainActivity extends ActionBarActivity
{

    ViewPager pager;

    private TextView userNameView;
    private CircleImageView profilePictureView;

    private Profile pendingUpdateForUser;
    private CallbackManager callbackManager;
    private ProfileTracker profileTracker;
    private AccessTokenTracker accessTokenTracker;

    private String titles[] = new String[]{ "    play  ",
                                            "    Learn    ",
                                            "    Chords   ",
                                            " Tuner   "};

    SlidingTabLayout slidingTabLayout;

    public TextView m_tvConnectionState;

    Button btn;

    private LeftNavAdapter adapter;

    private int mCurrentPosition = 0;
    private int mPreviousPosition = 0;

    String myUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_back);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), titles);
        pager = (ViewPager) findViewById(R.id.viewpager);
        pager.setAdapter(viewPagerAdapter);

        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        slidingTabLayout.setViewPager(pager);
        slidingTabLayout.setBackgroundColor(Color.argb(255, 240, 240, 240));
        slidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                mPreviousPosition = mCurrentPosition;
                mCurrentPosition = position;
                if (mPreviousPosition ==  1)
                    changeFragments(position);
                Util.stopViaData();
                return Color.BLUE;

            }
        });



        m_tvConnectionState = (TextView) findViewById(R.id.tvConnectionState);
        m_tvConnectionState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Config.bBlueToothActive == false) {
                    Intent intent = new Intent(MainActivity.this, BluetoothActivity.class);
                    startActivity(intent);
                } else {
                    try {
                        Util.stopViaData();
                        BluetoothClass.mmSocket.close();
                        Config.bBlueToothActive = false;
                        showConnectionState();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });
        adapter = new LeftNavAdapter(this, getResources().getStringArray(
                R.array.arr_left_nav_list));




        final DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        final ListView navList = (ListView) findViewById(R.id.drawer);
        View header = getLayoutInflater().inflate(R.layout.left_nav_header_one,
                null);
        userNameView = (TextView)header.findViewById(R.id.tvYourName);
        profilePictureView = (CircleImageView) header.findViewById(R.id.imageCommonFriends);

        callbackManager = CallbackManager.Factory.create();
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                setProfile(currentProfile);
            }
        };

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                // On AccessToken changes fetch the new profile which fires the event on
                // the ProfileTracker if the profile is different
                Profile.fetchProfileForCurrentAccessToken();
            }
        };

        // Ensure that our profile is up to date
        Profile.fetchProfileForCurrentAccessToken();
        setProfile(Profile.getCurrentProfile());


        navList.addHeaderView(header);
        navList.setAdapter(adapter);
        navList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                                    long arg3)
            {
                drawer.closeDrawers();
                if (pos != 0)
                    launchFragment(pos - 1);
                else
                    launchFragment(-2);

            }
        });
        btn = (Button)findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(navList);
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        profileTracker.stopTracking();
        accessTokenTracker.startTracking();
    }
    public void launchFragment(int pos)
    {

        String title = null;
        if (pos == -1)
        {
            title = "Your Match";
            Toast.makeText(getApplicationContext(),title, Toast.LENGTH_LONG).show();
        }
        else if (pos == -2)
        {
            title = "Profile";
            Toast.makeText(getApplicationContext(),title, Toast.LENGTH_LONG).show();
        }
        else if (pos == 0)
        {
            title = "Home";
            Toast.makeText(getApplicationContext(),title, Toast.LENGTH_LONG).show();
        }
        else if (pos == 1)
        {
            title = "Find Match";
            Intent intent = new Intent(MainActivity.this, PresentationActivity.class);
            startActivity(intent);
            finish();
        }
        else if (pos == 2)
        {
            title = "Chat with Nikita";
            Intent intent  = new Intent(MainActivity.this, CommentMainActivity.class);
            startActivity(intent);
//            Intent intent = new Intent(MainActivity.this, ShareMeActivity.class);
//            startActivity(intent);
        }
        else if (pos == 3)
        {
            title = "Video Chat with Nikita";
            Intent intent = new Intent(MainActivity.this, ShareMeActivity.class);
            startActivity(intent);

        }
        else if (pos == 4)
        {
            title = "Liked You";
            LoginManager.getInstance().logOut();
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
            finish();
//            Toast.makeText(getApplicationContext(),title, Toast.LENGTH_LONG).show();
        }
        else if (pos == 5)
        {
            title = "Favorites";
            Toast.makeText(getApplicationContext(),title, Toast.LENGTH_LONG).show();
        }
        else if (pos == 6)
        {
            title = "Visitores";
            Toast.makeText(getApplicationContext(),title, Toast.LENGTH_LONG).show();
        }


        if (adapter != null && pos >= 0)
            adapter.setSelection(pos);
    }
    public void showConnectionState(){
        if (Config.bBlueToothActive == true){
            m_tvConnectionState.setText(R.string.connect);
            m_tvConnectionState.setBackgroundColor(Color.GREEN);
        }else{
            m_tvConnectionState.setText(R.string.disconnect);
            m_tvConnectionState.setBackgroundColor(Color.RED);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        showConnectionState();
    }

    @Override
    public void onBackPressed() {
        if (mCurrentPosition == 0){
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.play_container, new PlayFragmentSearchList());
            fragmentTransaction.commit();
        }else if (mCurrentPosition == 1){
            FragmentManager fragmentManager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.learn_container, new LearnFragmentButton());
            fragmentTransaction.commit();
        }

    }
    public void changeFragments(int position){
        if (position == 2 || position == 0){
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.learn_container, new LearnFragmentButton());
            fragmentTransaction.commit();
        }   
    }
    private void setProfile(Profile profile) {
        if (userNameView == null || profilePictureView == null) {
            // Fragment not yet added to the view. So let's store which user was intended
            // for display.
            pendingUpdateForUser = profile;
            return;
        }

        if (profile == null) {

            userNameView.setText("No user");
        } else {
            userNameView.setText(profile.getName());
            myUserId = profile.getId();
            try{
//                Bitmap bmp = null;
//                bmp = Glide.with(this).load("http://graph.facebook.com/" + myUserId + "/picture?type=large").asBitmap().into(-1, -1).get();
                Glide.with(this).load("http://graph.facebook.com/" + myUserId + "/picture?type=large").into(profilePictureView);
            }catch (Exception e){
                e.printStackTrace();
            }

//            AsyncTask<Void, Void, Bitmap> t = new AsyncTask<Void, Void, Bitmap>() {
//                protected Bitmap doInBackground(Void... p) {
//                    Bitmap bmp = null;
//                    bmp = Glide.with(getActivigetApplicationContext()).
//                            load("your image url here").
//                            asBitmap().
//                            into(-1, -1).
//                            get();
//                    try {
//                        URL aURL = new URL("http://graph.facebook.com/" + myUserId + "/picture?type=large");
//                        URLConnection conn = aURL.openConnection();
//                        conn.setUseCaches(true);
//                        conn.connect();
//                        InputStream is = conn.getInputStream();
//                        BufferedInputStream bis = new BufferedInputStream(is);
//                        bmp = BitmapFactory.decodeStream(bis);
//                        bis.close();
//                        is.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    return bmp;
//                }
//
//                protected void onPostExecute(Bitmap bmp) {
//
//                    profilePictureView.setImageBitmap(bmp);
//                }
//            };
//            t.execute();
        }
    }
}
