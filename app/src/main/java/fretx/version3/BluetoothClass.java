package fretx.version3;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by AsmodeusStudio on 29/12/2015.
 */
public class BluetoothClass {

    static String tag = "debug";
    static BluetoothSocket mmSocket;
    static final int SUCCESS_CONNECT = 0;
    static final int MESSAGE_READ = 1;
    static final int ARDUINO = 2;
    static ConnectedThread connectedThread = null;
    static Handler.Callback callback = new Handler.Callback() {
        public boolean handleMessage(Message msg) {
            Log.i("debug", "in handler");
            switch (msg.what) {
                case BluetoothClass.SUCCESS_CONNECT:
                    BluetoothClass.connectedThread = new BluetoothClass.ConnectedThread((BluetoothSocket) msg.obj);
                    break;
                case BluetoothClass.ARDUINO:
                    if (Config.bBlueToothActive == true){
                        BluetoothClass.connectedThread.write((byte[])msg.obj);
                    }
                    Log.d("BT",msg.toString());
                    break;
            }
            return false;
        }
    };
    static Handler mHandler = new Handler(callback);

    static public class ConnectedThread extends Thread {
        //private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            //mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(tag, "tmpIn or tmpOut");
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer;  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    buffer = new byte[1024];
                    bytes = mmInStream.read(buffer);
                    // Send the obtained bytes to the UI activity
                    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Log.e(tag, "mmOutStream");
            }
        }
    }
}