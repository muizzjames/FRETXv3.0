package fretx.version3;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Misho on 2/21/2016.
 */
public final class Util {
    private Util() {
    }
    public static SongItem setSongItem(String songName, String songUrl, int sontText, Bitmap bitmap){
        SongItem itemData = new SongItem();
        itemData.songName = songName;
        itemData.songURl = songUrl;
        itemData.songTxt = sontText;
        itemData.image = bitmap;

        return itemData;
    }
    ///Read the text file from resource(Raw) and divide by end line mark('\n")
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
    public static void setDefaultValues(Boolean[] bArray)
    {
        for (int i = 0; i < bArray.length; i ++){
            bArray[i] = false;
        }
    }
    public static byte[] str2array(String string){
        String strSub = string.replaceAll("[{}]", "");
        String[] parts = strSub.split(",");
        byte[] array = new byte[parts.length];
        for (int i = 0; i < parts.length; i ++)
        {
            array[i] = Byte.valueOf(parts[i]);
        }
        return array;
    }
    public static void startViaData(byte[] array) {
        BluetoothClass.mHandler.obtainMessage(BluetoothClass.ARDUINO, array).sendToTarget();
    }

    public static void stopViaData() {
        byte[] array = new byte[]{0};
        BluetoothClass.mHandler.obtainMessage(BluetoothClass.ARDUINO, array).sendToTarget();
    }

}
