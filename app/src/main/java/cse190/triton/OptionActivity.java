package cse190.triton;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;



public class OptionActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        setContentView(R.layout.activity_option);

        //selection for how many players
        final Spinner numPlayers = (Spinner)findViewById(R.id.spinner1);
        String[] items = new String[]{"2"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
        numPlayers.setAdapter(adapter);

        //value of money
        final EditText startingMoney = (EditText)findViewById(R.id.startingMoney);
        final EditText userID = (EditText)findViewById(R.id.userID);

        final Button play = (Button) findViewById(R.id.play);

        play.setOnClickListener(new View.OnClickListener() {
            int tempPlayers = Integer.parseInt(String.valueOf(numPlayers.getSelectedItem()));

            public void onClick(View v) {
                String tempMoney = startingMoney.getText().toString();
                String tempID = userID.getText().toString();

                if(tempID.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please state a username",
                            Toast.LENGTH_SHORT).show();
                }
                else if(tempMoney.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Starting money is too low. Please pick a number greater than 1000.",
                            Toast.LENGTH_SHORT).show();
                }

                else if(Integer.parseInt(tempMoney) < 1000) {
                    Toast.makeText(getApplicationContext(), "Starting money is too low. Please pick a number greater than 1000.",
                            Toast.LENGTH_SHORT).show();
                }

                else {
                    Settings.setNumPlayers(tempPlayers);
                    Settings.setStartingMoney(tempMoney);
                    Settings.setUserID(tempID);
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
        Intent intent = new Intent(this, GamePlay.class);
        startActivity(intent);
    }
}
