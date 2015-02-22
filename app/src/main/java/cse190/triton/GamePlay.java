
package cse190.triton;

        import android.support.v7.app.ActionBarActivity;
        import android.os.Bundle;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.TextView;

        import android.content.res.AssetManager;

        import java.io.InputStream;
        import java.io.IOException;

        import java.util.List;
        import java.util.ArrayList;
        import java.util.Collections;
        import android.widget.Button;
        import android.content.pm.ActivityInfo;

        import android.widget.ImageView;


        import static cse190.triton.NikiConstants.*;



public class GamePlay extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        final TextView winner = (TextView) findViewById(R.id.winner);
        final Button testButton = (Button) findViewById(R.id.deal);

        //setting up player id and money
        final TextView playerID = (TextView) findViewById(R.id.playerID);
        final TextView playerMoney = (TextView) findViewById(R.id.playerMoney);
        final TextView aiMoney = (TextView) findViewById(R.id.aiMoney);
        playerID.setText("Player 1");
        updateMoneyUI(playerMoney, aiMoney);

        //setting up the pot
        final TextView pot = (TextView) findViewById(R.id.pot);
        //ante is starting money / 100
        final int ante = (Integer.parseInt(Settings.getMoney("ai")) / 100);

        //pictures setup for players
        final ImageView p1c1 = (ImageView) findViewById(R.id.p1c1);
        final ImageView p1c2 = (ImageView) findViewById(R.id.p1c2);
        final ImageView p2c1 = (ImageView) findViewById(R.id.p2c1);
        final ImageView p2c2 = (ImageView) findViewById(R.id.p2c2);

        final ImageView[] picHands = {p1c1,p1c2,p2c1,p2c2};

        //pictures for flop,turn,river
        final ImageView flop1 = (ImageView) findViewById(R.id.flop1);
        final ImageView flop2 = (ImageView) findViewById(R.id.flop2);
        final ImageView flop3 = (ImageView) findViewById(R.id.flop3);
        final ImageView turn = (ImageView) findViewById(R.id.turn);
        final ImageView river = (ImageView) findViewById(R.id.river);

        //load all possible outcomes
        final AssetManager assetManager = getAssets();
        loader(assetManager);

        winner.setText("");


        testButton.setTag(0);
        testButton.setOnClickListener(new View.OnClickListener() {
            //variables
            int numPlayers = Settings.getNumPlayers();
            int potValue = 0;

            //random numbers for the decks
            List<Integer> deck = shuffleCards();

            //init all the classes.
            Deck bitDeck = new Deck();
            //CPlot cplot = new CPlot();
            Hand[] allHands = new Hand[numPlayers];

            String[] flopper;


            public void onClick( View v) {
                final int status = (int) v.getTag();

                //status for dealing everyone hand
                if(status == 0) {

                    winner.setText("");
                    testButton.setText("Show Flop");
                    v.setTag(1);

                    //sets all hands and their pictures
                    for (int n = 0; n < numPlayers; n++) {
                        allHands = setHands(n, deck,bitDeck, allHands, picHands);
                    }

                    //get all 5 cards for holdem
                    bitDeck.enumRandom();
                    long flopCards = bitDeck.getBoard();
                    flopper = CPlot.getCards(flopCards);

                    flop1.setImageResource(R.drawable.b1fv);
                    flop2.setImageResource(R.drawable.b1fv);
                    flop3.setImageResource(R.drawable.b1fv);
                    turn.setImageResource(R.drawable.b1fv);
                    river.setImageResource(R.drawable.b1fv);

                    //setting up ante for pot and subtracting from both players
                    subAnte(playerMoney, aiMoney, ante);

                    pot.setText(String.valueOf(ante * Settings.getNumPlayers()));
                    potValue = ante * Settings.getNumPlayers();


                }

                else if(status == 1) {

                    testButton.setText("Show Turn");
                    flop1.setImageResource(findPic(flopper[0]));
                    flop2.setImageResource(findPic(flopper[1]));
                    flop3.setImageResource(findPic(flopper[2]));

                    v.setTag(2);
                }

                else if(status == 2) {

                    testButton.setText("Show River");
                    turn.setImageResource(findPic(flopper[3]));
                    v.setTag(3);
                }

                else {

                    river.setImageResource(findPic(flopper[4]));
                    testButton.setText("Deal");
                    v.setTag(0);

                    int[] hValues = new int[numPlayers];
                    for(int j = 0; j < numPlayers; j++) {
                        hValues[j] = Eva.getValue(bitDeck.keysBoard[0] * allHands[j].hKey,
                                bitDeck.handsBoard[0] | allHands[j].hCards);
                    }

                    int winningHand = 0;
                    int winningValue = 0;
                    for(int k = 0; k < numPlayers; k++) {
                        if(hValues[k] >= winningValue) {
                            //check for tie
                            if(winningValue == hValues[k]) {
                                winningHand = -1;
                            } else {
                                winningValue = hValues[k];
                                winningHand = k;
                            }
                        }
                    }
                    if(winningHand >= 0){
                        winner.setText("Player " + Integer.toString(winningHand+1) + " Wins!");
                    }
                    else {
                        winner.setText("It's a tie!");
                    }

                    addWinner(playerMoney, aiMoney, winningHand, potValue);

                    //resets everything for next game
                    deck = shuffleCards();
                    bitDeck = new Deck();
                    allHands = new Hand[numPlayers];
                    pot.setText("0");
                    potValue = 0;



                }

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game_play, menu);
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





    public List<Integer> shuffleCards() {
        ArrayList<Integer> deck = new ArrayList<>();
        for (int i = 0; i <52; i++) {
            deck.add(i);
        }
        Collections.shuffle(deck);
        return deck;

    }


    public Hand[] setHands(int player, List<Integer> intDeck, Deck bitDeck,  Hand[] allHands, ImageView[] pics) {
        long tempLong = 0;

        //finds bit mask for cards
        for(int m = player * 2; m < (player * 2) + 2; m++) {
            int temp = 0;
            for (int i = 0; i < DECK_STRINGS.length - 1; i++) {
                if (DECK_STRINGS[intDeck.get(m)].equals(DECK_STRINGS[i])) {
                    temp = i;
                    pics[m].setImageResource(findPic(DECK_STRINGS[i]));
                    break;
                }
            }

            tempLong = tempLong | DECK_BIT_MASKS[temp];

        }

        //set cards to array and remove cards from deck
        allHands[player] = new Hand(tempLong);
        bitDeck.removeCards(allHands[player].hCards);



        return allHands;
    }

    public void loader(AssetManager asset) {
        try {

            InputStream input = asset.open("nonflush.ser");
            Eva.loadEnumHands(input);

        } catch (IOException e) {
            e.printStackTrace();

        }

    }

    public void updateMoneyUI(TextView player, TextView ai) {
          player.setText(Settings.getMoney("Player 1"));
          ai.setText(Settings.getMoney("ai"));
    }

    public void subAnte(TextView player, TextView ai, int ante) {
        Settings.subMoney("ai", ante);
        Settings.subMoney("Player 1", ante);
        updateMoneyUI(player, ai);
    }

    public void addWinner(TextView player, TextView ai, int winner, int pot) {
        if(winner == 0) {
            Settings.addMoney("Player1", pot);
        }

        else if (winner == -1) {
            Settings.addMoney("Player1", pot/2);
            Settings.addMoney("ai", pot/2);
        }
        else {
            Settings.addMoney("ai", pot);
        }

        updateMoneyUI(player, ai);
    }

    public int findPic(String cardStr) {

        switch(cardStr) {
            case "Ac":  return R.drawable.clubs1;
            case "Kc":  return R.drawable.clubs13;
            case "Qc":  return R.drawable.clubs12;
            case "Jc":  return R.drawable.clubs11;
            case "Tc":  return R.drawable.clubs10;
            case "9c":  return R.drawable.clubs9;
            case "8c":  return R.drawable.clubs8;
            case "7c":  return R.drawable.clubs7;
            case "6c":  return R.drawable.clubs6;
            case "5c":  return R.drawable.clubs5;
            case "4c":  return R.drawable.clubs4;
            case "3c":  return R.drawable.clubs3;
            case "2c":  return R.drawable.clubs2;

            case "Ad":  return R.drawable.diamonds1;
            case "Kd":  return R.drawable.diamonds13;
            case "Qd":  return R.drawable.diamonds12;
            case "Jd":  return R.drawable.diamonds11;
            case "Td":  return R.drawable.diamonds10;
            case "9d":  return R.drawable.diamonds9;
            case "8d":  return R.drawable.diamonds8;
            case "7d":  return R.drawable.diamonds7;
            case "6d":  return R.drawable.diamonds6;
            case "5d":  return R.drawable.diamonds5;
            case "4d":  return R.drawable.diamonds4;
            case "3d":  return R.drawable.diamonds3;
            case "2d":  return R.drawable.diamonds2;

            case "Ah":  return R.drawable.hearts1;
            case "Kh":  return R.drawable.hearts13;
            case "Qh":  return R.drawable.hearts12;
            case "Jh":  return R.drawable.hearts11;
            case "Th":  return R.drawable.hearts10;
            case "9h":  return R.drawable.hearts9;
            case "8h":  return R.drawable.hearts8;
            case "7h":  return R.drawable.hearts7;
            case "6h":  return R.drawable.hearts6;
            case "5h":  return R.drawable.hearts5;
            case "4h":  return R.drawable.hearts4;
            case "3h":  return R.drawable.hearts3;
            case "2h":  return R.drawable.hearts2;

            case "As":  return R.drawable.spades1;
            case "Ks":  return R.drawable.spades13;
            case "Qs":  return R.drawable.spades12;
            case "Js":  return R.drawable.spades11;
            case "Ts":  return R.drawable.spades10;
            case "9s":  return R.drawable.spades9;
            case "8s":  return R.drawable.spades8;
            case "7s":  return R.drawable.spades7;
            case "6s":  return R.drawable.spades6;
            case "5s":  return R.drawable.spades5;
            case "4s":  return R.drawable.spades4;
            case "3s":  return R.drawable.spades3;
            case "2s":  return R.drawable.spades2;
            default: return R.drawable.b1fv;
        }
    }


}

