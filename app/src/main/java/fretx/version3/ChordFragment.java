package fretx.version3;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Misho on 2/4/2016.
 */

public class ChordFragment extends Fragment
{
    //the images to display
    Integer[] imageIDs = {
            R.drawable.dor,
            R.drawable.re,
            R.drawable.mi,
            R.drawable.fa,
            R.drawable.so,
            R.drawable.la,
            R.drawable.si
    };
    Integer[] imageBackgroundIDs = {
            R.drawable.backone,
            R.drawable.backtwo,
            R.drawable.backthree,
            R.drawable.backfour,
            R.drawable.backfive,
            R.drawable.backsix,
            R.drawable.backseven
    };
    ObservableVideoView vvMain;
    Uri[] videoUri = new Uri[7];
    ArrayList<byte[]> musicArray = new ArrayList<>(7);
    ImageView imgBack;

    MainActivity mActivity;

    View rootView;


    public ChordFragment(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mActivity = (MainActivity) getActivity();

        rootView = inflater.inflate(R.layout.chord_fragment, container, false);

        vvMain = (ObservableVideoView)rootView.findViewById(R.id.vvMain);
        imgBack = (ImageView)rootView.findViewById(R.id.imgBackground);
        imgBack.setImageResource(imageBackgroundIDs[0]);

        // input data file
        videoUri[0] = Uri.parse("android.resource://" + mActivity.getPackageName() + "/" + R.raw.cmajor);
        videoUri[1] = Uri.parse("android.resource://" + mActivity.getPackageName() + "/" + R.raw.dmajor);
        videoUri[2] = Uri.parse("android.resource://" + mActivity.getPackageName() + "/" + R.raw.emajor);
        videoUri[3] = Uri.parse("android.resource://" + mActivity.getPackageName() + "/" + R.raw.fmajor);
        videoUri[4] = Uri.parse("android.resource://" + mActivity.getPackageName() + "/" + R.raw.gmajor);
        videoUri[5] = Uri.parse("android.resource://" + mActivity.getPackageName() + "/" + R.raw.amajor);
        videoUri[6] = Uri.parse("android.resource://" + mActivity.getPackageName() + "/" + R.raw.bmajor);


        // input text file

        byte[] array1 = data2array(R.raw.cmajortxt);
        byte[] array2 = data2array(R.raw.dmajortxt);
        byte[] array3 = data2array(R.raw.emajortxt);
        byte[] array4 = data2array(R.raw.fmajortxt);
        byte[] array5 = data2array(R.raw.gmajortxt);
        byte[] array6 = data2array(R.raw.amajortxt);
        byte[] array7 = data2array(R.raw.bmajortxt);

        musicArray.add(array1);
        musicArray.add(array2);
        musicArray.add(array3);
        musicArray.add(array4);
        musicArray.add(array5);
        musicArray.add(array6);
        musicArray.add(array7);

        // init gallery
        Gallery gallery = (Gallery) rootView.findViewById(R.id.gallery1);
        gallery.setAdapter(new ImageAdapter(mActivity));
        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Toast.makeText(mActivity.getBaseContext(), "pic" + (position + 1) + " selected", Toast.LENGTH_SHORT).show();
                playChord(position);
            }
        });
        return  rootView;
    }
    // data convert to array
    public byte[] data2array(int resID){
        String str= readRawTextFile(mActivity.getBaseContext(), resID);
        String[] strArrTemp = str.split(" ");
        String strText = strArrTemp[1].replaceAll("\n", "");     // This is text of that strTime.
        return str2array(strText);
    }
    // string convert to array
    public byte[] str2array(String string){
        String strSub = string.replaceAll("[{}]", "");
        String[] parts = strSub.split(",");
        byte[] array = new byte[parts.length];
        for (int i = 0; i < parts.length; i ++)
        {
            array[i] = Byte.valueOf(parts[i]);
        }
        return array;
    }
    // this function read form raw data.
    public static String readRawTextFile(Context ctx, int resId) {
        InputStream inputStream = ctx.getResources().openRawResource(resId);

        InputStreamReader inputreader = new InputStreamReader(inputStream);
        BufferedReader buffreader = new BufferedReader(inputreader);
        String line;
        StringBuilder text = new StringBuilder();

        try {
            while ((line = buffreader.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
        } catch (IOException e) {
            return null;
        }
        return text.toString();
    }
    public void playChord(int i){
        vvMain.setVideoURI(videoUri[i]);
        vvMain.start();
        imgBack.setImageResource(imageBackgroundIDs[i]);
        startViaData(i);

    }
    public class ImageAdapter extends BaseAdapter {
        private Context context;
        private int itemBackground;
        public ImageAdapter(Context c)
        {
            context = c;
            // sets a grey background; wraps around the images
            TypedArray a =mActivity.obtainStyledAttributes(R.styleable.MyGallery);
            itemBackground = a.getResourceId(R.styleable.MyGallery_android_galleryItemBackground, 0);
            a.recycle();
        }
        // returns the number of images
        public int getCount() {
            return imageIDs.length;
        }
        // returns the ID of an item
        public Object getItem(int position) {
            return position;
        }
        // returns the ID of an item
        public long getItemId(int position) {
            return position;
        }
        // returns an ImageView view
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView = new ImageView(context);
            imageView.setImageResource(imageIDs[position]);
            imageView.setLayoutParams(new Gallery.LayoutParams(300, 450));
            imageView.setBackgroundResource(itemBackground);
            return imageView;
        }
    }
    // to send byte array data for playing
    public void startViaData(int index) {
        BluetoothClass.mHandler.obtainMessage(BluetoothClass.ARDUINO, musicArray.get(index)).sendToTarget();
    }
    // to turn off light
    public void stopViaData() {
        byte[] array = new byte[]{0};
        BluetoothClass.mHandler.obtainMessage(BluetoothClass.ARDUINO, array).sendToTarget();
    }

}
