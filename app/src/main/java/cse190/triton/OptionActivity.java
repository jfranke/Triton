package cse190.triton;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;



public class OptionActivity extends ActionBarActivity {
    ImageButton volumeOptions;
    LinearLayout layout;
    LinearLayout mainLayout;
    Intent music;
    ServiceConnectionBinder service = new ServiceConnectionBinder(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);

        //music service
        service.doBindService();
        music = new Intent();
        music.setClass(this,MusicService.class);
        startService(music);

        layout = new LinearLayout(this);
        mainLayout = new LinearLayout(this);
        volumeOptions = (ImageButton) findViewById(R.id.volumeOptions);
        volumeOptions.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                SoundOptions.doPopUp(getBaseContext(), v, layout, mainLayout);
            }
        });

        final EditText aiName = (EditText)findViewById(R.id.aiName);
        final EditText aiName2 = (EditText)findViewById(R.id.aiName2);
        final EditText aiName3 = (EditText)findViewById(R.id.aiName3);
        final CheckBox anteOnBox = (CheckBox) findViewById(R.id.anteOnBox);
        //aiName setup


        //selection for how many players
        final Spinner numPlayers = (Spinner)findViewById(R.id.spinner1);
        String[] items = new String[]{"1", "2", "3"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
        numPlayers.setAdapter(adapter);

        numPlayers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if((position > 0)) {
                    aiName2.setVisibility(View.VISIBLE);
                }

                else {
                    aiName2.setVisibility(View.GONE);
                }

                if((position > 1)) {
                    aiName3.setVisibility(View.VISIBLE);
                }

                else {
                    aiName3.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        //value of money
        final EditText userID = (EditText)findViewById(R.id.userID);
        final Button play = (Button) findViewById(R.id.play);

        play.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int tempPlayers = Integer.parseInt(String.valueOf(numPlayers.getSelectedItem()));
                String tempID = userID.getText().toString();

                //ai names
                String aiID = aiName.getText().toString();
                String aiID2 = aiName2.getText().toString();
                String aiID3 = aiName3.getText().toString();
                if(aiID.isEmpty()) {
                    aiID = "Minh";
                }

                if(aiID2.isEmpty()) {
                    aiID2 = "John";
                }

                if(aiID3.isEmpty()) {
                    aiID3 = "Albert";
                }

                if(tempID.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please state a username",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    if(anteOnBox.isChecked()) {
                        Settings.anteOn = true;
                    }
                    Settings.setNumPlayers(tempPlayers + 1);
                    Settings.setUserID(tempID, aiID, aiID2, aiID3);
                    System.out.println(tempPlayers + 1);
                    startGame(v);
                }
            }


        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_option, menu);
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

    public void startGame(View view) {
        service.doUnbindService();
        service.getConnectionService().onDestroy();
        Intent intent = new Intent(this, GamePlay.class);
        startActivity(intent);
    }

    public void goBack(View view) {
        service.doUnbindService();
        service.getConnectionService().onDestroy();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    @Override
    public void onPause() {
        service.doUnbindService();
        service.getConnectionService().onDestroy();
        stopService(music);
        super.onPause();
    }
}
