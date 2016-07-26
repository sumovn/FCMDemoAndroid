package com.n2t.gcmdemo;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.provider.FirebaseInitProvider;

public class MainActivity extends AppCompatActivity {

    /**
     * SERVER KEY: AIzaSyDciCmbFSwWfhYgWKCClzYi0tALFOZbpZI
     * */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tvMsg = (TextView) findViewById(R.id.textView1);

        /*SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String token = preferences.getString("fcm_token", "");*/

        String token = FirebaseInstanceId.getInstance().getToken();

        if( token!=null && token.length()>0){
            tvMsg.setText(token);
            System.out.println("Token:" + token);
        }

    }
}
