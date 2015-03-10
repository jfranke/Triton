/**
 * Copyright 2010-present Facebook.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cse190.triton;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import com.facebook.*;

public class MainActivity extends FragmentActivity {

    private MainFragment mainFragment;
    private SoundFragment soundFragment;
    Intent music;
    ServiceConnectionBinder service = new ServiceConnectionBinder(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        restoreFragments(savedInstanceState);
        service.doBindService();
        music = new Intent();
        music.setClass(this,MusicService.class);
        startService(music);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        FragmentManager manager = getSupportFragmentManager();
        manager.putFragment(outState, mainFragment.TAG, mainFragment);
        manager.putFragment(outState, soundFragment.TAG, soundFragment);

        service.doUnbindService();
        stopService(music);

    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void restoreFragments(Bundle savedInstanceState) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        if (savedInstanceState != null) {
            mainFragment = (MainFragment)manager.getFragment(savedInstanceState, MainFragment.TAG);
            soundFragment = (SoundFragment)manager.getFragment(savedInstanceState, SoundFragment.TAG);
        }

        if (mainFragment == null) {
            mainFragment = new MainFragment();
            transaction.add(R.id.fragmentContainer, mainFragment, MainFragment.TAG);
        }

        if (soundFragment == null) {
            soundFragment = new SoundFragment();
            transaction.add(R.id.fragmentContainer, soundFragment, SoundFragment.TAG);
        }

        transaction.commit();
    }

    public void goOptions(View view) {
        service.doUnbindService();
        service.getConnectionService().onDestroy();
        stopService(music);
        Intent intent = new Intent(this, OptionActivity.class);
        startActivity(intent);
    }

}
