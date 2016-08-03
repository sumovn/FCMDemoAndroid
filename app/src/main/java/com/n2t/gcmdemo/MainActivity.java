package com.n2t.gcmdemo;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.n2t.gcmdemo.model.FcmMessage;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {

    /**
     * SERVER KEY: AIzaSyDciCmbFSwWfhYgWKCClzYi0tALFOZbpZI
     * */
    /**
     * Setup environment test
     * Tool: postman.
     * Url: https://fcm.googleapis.com/fcm/send
     * Header:
     *    + Content-Type : application/json
     *    + Authorization : key=SERVERKEY_HERE
     *
     * Data: raw json
     * {
          "to": "YOUR TOKEN HERE",
          "data": {
                      "message": "This is a Firebase Cloud Messaging Topic Message!",
                  }
       }
     * */

    TextView tvToken, tvMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvToken = (TextView) findViewById(R.id.textView1);
        tvMsg = (TextView) findViewById(R.id.tvMessage);

        /*SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String token = preferences.getString("fcm_token", "");*/

        String token = FirebaseInstanceId.getInstance().getToken();

        if( token!=null && token.length()>0){
            tvToken.setText("Token: " + token);
            saveTokenSdcard(token);
            System.out.println("Token: " + token);
        }
        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void onReceiverFcmToken(final String token){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvToken.setText("Token: " + token);
                saveTokenSdcard(token);
            }
        });
        System.out.println("Token: " + token);
    }

    @Subscribe
    public void onReceiverPushMessage(final FcmMessage msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvMsg.setText(msg.message);

                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.layoutMain), msg.message, Snackbar.LENGTH_LONG);

                snackbar.show();

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void saveTokenSdcard(String token){
        try {

            if(!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
                System.out.println("SDCard not mounted");
                return;
            }

            //check permission
            if(!storagePermitted(this))
                return;

            File myFile = new File(Environment.getExternalStorageDirectory(), "FcmDemoToken.txt");
            myFile.createNewFile();

            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(token);
            myOutWriter.close();
            fOut.close();

            Toast.makeText(getBaseContext(),
                    "Done writing token to SDcard 'FcmDemoToken.txt'",
                    Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private static final int REQUESTCODE_STORAGE_PERMISSION = 2001;
    private static boolean storagePermitted(Activity activity) {

        if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return true;

        ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUESTCODE_STORAGE_PERMISSION);
        return false;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUESTCODE_STORAGE_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    String token = FirebaseInstanceId.getInstance().getToken();
                    saveTokenSdcard(token);
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}
