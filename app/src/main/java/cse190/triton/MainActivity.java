package cse190.triton;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.facebook.AppEventsLogger;
import com.facebook.widget.LoginButton;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class MainActivity extends FragmentActivity {
    ImageButton volumeOptions;
    LinearLayout layout;
    LinearLayout mainLayout;
    private MainFragment mainFragment;

    Intent music;
    ServiceConnectionBinder service = new ServiceConnectionBinder(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            // Add the fragment on initial activity setup
            mainFragment = new MainFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, mainFragment)
                    .commit();
        } else {
            // Or set the fragment from restored state info
            mainFragment = (MainFragment) getSupportFragmentManager()
                    .findFragmentById(android.R.id.content);
        }
        setContentView(R.layout.activity_main);

        //music service
        service.doBindService();
        music = new Intent();
        music.setClass(this,MusicService.class);
        startService(music);

        //setting up popup window
        layout = new LinearLayout(this);
        mainLayout = new LinearLayout(this);
        volumeOptions = (ImageButton) findViewById(R.id.volumeOptions);

        volumeOptions.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                SoundOptions.doPopUp(getBaseContext(), v, layout, mainLayout);
            }
        });

        LoginButton authButton = (LoginButton) findViewById(R.id.authButton);
        authButton.setReadPermissions(Arrays.asList("public_profile"));
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void goOptions(View view) {
        service.doUnbindService();
        service.getConnectionService().onDestroy();
        Intent intent = new Intent(this, OptionActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        service.doUnbindService();
        service.getConnectionService().onDestroy();
        stopService(music);
        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }


}