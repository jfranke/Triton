
package cse190.triton;

        import android.content.Intent;
        import android.support.v7.app.ActionBarActivity;
        import android.os.Bundle;
        import android.view.Gravity;
        import android.view.LayoutInflater;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.ImageButton;
        import android.widget.LinearLayout;
        import android.widget.PopupWindow;
        import android.widget.SeekBar;
        import android.widget.TextView;

        import android.content.res.AssetManager;

        import java.io.InputStream;
        import java.io.IOException;

        import java.util.List;
        import java.util.ArrayList;
        import java.util.Collections;
        import java.util.Random;

        import android.os.Handler;

        import android.widget.Button;

        import android.widget.ImageView;
        import android.view.ViewGroup.LayoutParams;
        import android.widget.VerticalSeekBar;


        import static cse190.triton.NikiConstants.*;



public class GamePlay extends ActionBarActivity {
    final String userID = Settings.getUserID();
    final int numPlayers = Settings.getNumPlayers();

    //global textview for moneys
    TextView playerID;
    TextView aiName;
    TextView aiName2;
    TextView aiName3;
    TextView userBet;
    TextView aiBet;
    TextView aiBet2;
    TextView aiBet3;
    TextView[] allBets  = new TextView[3];

    TextView moreInfo;
    TextView gradePercent;

    int potValue = 0;
    final int ante = ((Integer.parseInt(Settings.getMoney("User"))) / 100);
    int minRaise = 0;


    //other textviews
    TextView winner;
    TextView pot;
    TextView aiCommands;
    TextView aiCommands2;
    TextView aiCommands3;
    TextView grade;
    TextView raiseNum;
    TextView[] allCommands = new TextView[3];


    //pictures setup for players
    ImageView p1c1;
    ImageView p1c2;
    ImageView p2c1;
    ImageView p2c2;
    ImageView p3c1;
    ImageView p3c2;
    ImageView p4c1;
    ImageView p4c2;

    //pictures for flop,turn,river
    ImageView flop1;
    ImageView flop2;
    ImageView flop3;
    ImageView turn;
    ImageView river;
    ImageView[] picHands = new ImageView[8];

    ImageView dealer;
    ImageView dealer2;
    ImageView dealer3;
    ImageView dealer4;
    ImageView[] dealerPic = new ImageView[4];

    List<Integer> deck = shuffleCards();
    Deck bitDeck = new Deck();
    Hand[] allHands =  new Hand[numPlayers];

    Handler myHandler;

    Button testButton;
    Button foldButton;
    Button raiseButton;
    ImageButton infoButton;
    ImageButton volumeOptions;

    int raisePot;
    int aiRaisePot;
    int numCallers = 0;
    String[] flopper;
    int userCurrentBet = 0;

    int bigBlindNum = 200;
    int smallBlindNum = 100;

    LinearLayout layout;
    LinearLayout mainLayout;

    AiRate ai;
    AiRate ai2;
    AiRate ai3;
    AiRate[] allAi = new AiRate[numPlayers - 1];

    Boolean userAllIn;
    Boolean raiseFlag;
    Boolean foldFlag;
    Boolean demoOn = Settings.demoOn;
    int demoCount = 0;


    VerticalSeekBar raiseControl;

    ArrayList<Integer>  tieWinners;
    String[] tieWinnersNames;

    int dealerNum = 0;
    int startBetter;

    //music stuff
    public Intent music;
    public ServiceConnectionBinder service = new ServiceConnectionBinder(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);

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

        winner = (TextView) findViewById(R.id.winner);
        testButton = (Button) findViewById(R.id.deal);
        foldButton = (Button) findViewById(R.id.fold);
        raiseButton = (Button) findViewById(R.id.raise);
        infoButton = (ImageButton) findViewById(R.id.questionmark);

        //setting up player id and money
        playerID = (TextView) findViewById(R.id.playerID);
        aiName = (TextView) findViewById(R.id.aiName);
        aiName2 = (TextView) findViewById(R.id.aiName2);
        aiName3 = (TextView) findViewById(R.id.aiName3);

        userBet = (TextView) findViewById(R.id.userBet);
        aiBet = (TextView) findViewById(R.id.aiBet);
        aiBet2 = (TextView) findViewById(R.id.aiBet2);
        aiBet3 = (TextView) findViewById(R.id.aiBet3);
        if(numPlayers == 2) {
            allBets[0] = aiBet2;
            allBets[1] = aiBet;
        }
        else {
            allBets[0] = aiBet;
            allBets[1] = aiBet2;
        }
        allBets[2] = aiBet3;

        pot = (TextView) findViewById(R.id.pot);
        aiCommands = (TextView) findViewById(R.id.aiCommands);
        aiCommands2 = (TextView) findViewById(R.id.aiCommands2);
        aiCommands3 = (TextView) findViewById(R.id.aiCommands3);

        if(numPlayers == 2) {
            allCommands[1] = aiCommands;
            allCommands[0] = aiCommands2;
        }

        else {
            allCommands[0] = aiCommands;
            allCommands[1] = aiCommands2;
        }
        allCommands[2] = aiCommands3;
        grade = (TextView) findViewById(R.id.grade);

        aiName.setText("");
        aiName2.setText("");
        aiName3.setText("");
        userBet.setText("");
        allBets[0].setText("");
        allBets[1].setText("");
        allBets[2].setText("");

        moreInfo = (TextView) findViewById(R.id.moreInfo);

        moreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMoreInfo(v);
            }
        });

        updateMoneyUI();

        gradePercent = (TextView) findViewById(R.id.gradePercent);
        gradePercent.setText("");
        //setting up the pot

        //ante is starting money / 100

        //pictures setup for players
        p1c1 = (ImageView) findViewById(R.id.p1c1);
        p1c2 = (ImageView) findViewById(R.id.p1c2);
        p2c1 = (ImageView) findViewById(R.id.p2c1);
        p2c2 = (ImageView) findViewById(R.id.p2c2);
        p3c1 = (ImageView) findViewById(R.id.p3c1);
        p3c2 = (ImageView) findViewById(R.id.p3c2);
        p4c1 = (ImageView) findViewById(R.id.p4c1);
        p4c2 = (ImageView) findViewById(R.id.p4c2);

        picHands[0] = p1c1;
        picHands[1] = p1c2;
        picHands[2] = p3c1;
        picHands[3] = p3c2;
        picHands[4] = p2c1;
        picHands[5] = p2c2;
        picHands[6] = p4c1;
        picHands[7] = p4c2;

        dealer = (ImageView) findViewById(R.id.dealer);
        dealer2 = (ImageView) findViewById(R.id.dealer2);
        dealer3 = (ImageView) findViewById(R.id.dealer3);
        dealer4 = (ImageView) findViewById(R.id.dealer4);


        dealerPic[0] = dealer;
        if(numPlayers == 2) {
            dealerPic[1] = dealer3;
        }

        else {
            dealerPic[1] = dealer2;
            dealerPic[2] = dealer3;
        }
        dealerPic[3] = dealer4;

        if(numPlayers < 4 ) {
            p4c1.setImageDrawable(null);
            p4c2.setImageDrawable(null);
        }

        if(numPlayers < 3 ) {
            p3c1.setImageDrawable(null);
            p3c2.setImageDrawable(null);
        }

        //pictures for flop,turn,river
        flop1 = (ImageView) findViewById(R.id.flop1);
        flop2 = (ImageView) findViewById(R.id.flop2);
        flop3 = (ImageView) findViewById(R.id.flop3);
        turn = (ImageView) findViewById(R.id.turn);
        river = (ImageView) findViewById(R.id.river);

        myHandler = new Handler();


        //load all possible outcomes
        final AssetManager assetManager = getAssets();
        loader(assetManager);

        winner.setText("");
        aiCommands.setText("");
        aiCommands2.setText("");
        aiCommands3.setText("");


        testButton.setTag(0);
        startOver();
        infoButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                    popUpInfo(v);
            }
        });

        raiseNum = (TextView) findViewById(R.id.raiseNum);
        raiseNum.setText("0");
        raiseControl = (VerticalSeekBar) findViewById(R.id.vertSeekBar);
        updateRaiseSeek();

        foldButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                foldFlag = true;
                while(!figureAi(0, 0)) {
                    numCallers++;
                }
                subCurrentBet();
                doEverything();
                startOver();
            }
        });

        testButton.setOnClickListener(new View.OnClickListener() {

            public void onClick( View v) {
                final int status = (int) v.getTag();
                if (status == 0) {
                    numCallers++;
                    doCall();
                    if (numCallers < numPlayers) {
                        //if ai checks then continue
                        if (figureAi(userCurrentBet, 0)) {
                            //set text that ai checked
                            flop1.setImageResource(findPic(flopper[0]));
                            flop2.setImageResource(findPic(flopper[1]));
                            flop3.setImageResource(findPic(flopper[2]));
                            v.setTag(1);
                            testButton.setText("CHECK");
                            resetValues();
                        }
                    }
                    //if ai calls already then you just show
                    else {
                        flop1.setImageResource(findPic(flopper[0]));
                        flop2.setImageResource(findPic(flopper[1]));
                        flop3.setImageResource(findPic(flopper[2]));
                        v.setTag(1);
                        testButton.setText("CHECK");
                        resetValues();
                    }

                } else if (status == 1) {
                    numCallers++;
                    doCall();
                    if (numCallers < numPlayers) {
                        //if ai checks then continue
                        if (figureAi(userCurrentBet, 0)) {
                            //set text that ai checked
                            turn.setImageResource(findPic(flopper[3]));
                            v.setTag(2);
                            testButton.setText("CHECK");
                            resetValues();
                        }
                    }
                    //if ai calls already then you just show
                    else {
                        turn.setImageResource(findPic(flopper[3]));
                        v.setTag(2);
                        testButton.setText("CHECK");
                        resetValues();
                    }


                } else if (status == 2) {
                    numCallers++;
                    doCall();
                    if (numCallers < numPlayers) {
                        //if ai checks then continue
                        if (figureAi(userCurrentBet, 0)) {
                            //set text that ai checked
                            river.setImageResource(findPic(flopper[4]));
                            v.setTag(3);
                            testButton.setText("CHECK");
                            resetValues();
                            showCards();
                        }
                    }
                    //if ai calls already then you just show
                    else {
                        river.setImageResource(findPic(flopper[4]));
                        v.setTag(3);
                        testButton.setText("CHECK");
                        resetValues();
                        showCards();
                    }
                } else {
                    numCallers++;
                    doCall();
                    if (numCallers < numPlayers) {
                        //if ai checks then continue
                        if (figureAi(0, 0)) {
                            resetValues();
                            figureOutWinner();
                            if (Settings.getIntMoney("User") == 0) {
                                popUp(v);
                            } else {
                                startOver();
                            }
                            numCallers = 0;
                            testButton.setText("CHECK");
                        }

                    }
                    //if ai calls already then you just show
                    else {
                        resetValues();
                        figureOutWinner();
                        if (Settings.getIntMoney("User") <= 0) {
                            popUp(v);
                        } else {
                            startOver();
                        }
                    }

                }

                if(noMoreBetting()) {
                    doEverything();
                    if(Settings.getIntMoney("User") <= 0) {
                        popUp(v);
                    }
                    startOver();
                }

            }
        });


        raiseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final int status = (int) testButton.getTag();

                if(getRaise() >= Settings.getIntMoney("User")) {
                    userAllIn = true;
                }

                if(figureAi(getRaise(), 0)) {
                    numCallers = 1;
                    userCurrentBet = getRaise();
                    testButton.setText("CHECK");
                    doRaise();
                    resetValues();

                    if (status == 0) {
                        flop1.setImageResource(findPic(flopper[0]));
                        flop2.setImageResource(findPic(flopper[1]));
                        flop3.setImageResource(findPic(flopper[2]));
                        testButton.setTag(1);

                    } else if (status == 1) {
                        turn.setImageResource(findPic(flopper[3]));
                        testButton.setTag(2);

                    } else if (status == 2) {
                        river.setImageResource(findPic(flopper[4]));
                        testButton.setTag(3);
                        showCards();

                    } else {
                        figureOutWinner();
                        if (Settings.getIntMoney("User") == 0) {
                            popUp(v);
                        } else {
                            startOver();
                        }
                    }
                }
                if (noMoreBetting()) {
                    doEverything();
                    if (Settings.getIntMoney("User") <= 0) {
                        popUp(v);
                    }

                    startOver();

                }
                updateRaiseSeek();
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


    public void setHands(int player) {
        long tempLong = 0;

        if(demoOn && numPlayers == 2) {
            switch (demoCount) {
                case 0: if(player == 0) {allHands[0] = new Hand("Ad,Ah");
                    picHands[0].setImageResource(findPic("Ad"));
                    picHands[1].setImageResource(findPic("Ah"));}
                    else {allHands[1] = new Hand("9s,2s");}
                    break;

                case 1: if(player == 0) {allHands[0] = new Hand("Qs,Js");
                    picHands[0].setImageResource(findPic("Qs"));
                    picHands[1].setImageResource(findPic("Js"));}
                    else {allHands[1] = new Hand("9s,2s");}
                    break;

                case 2: if(player == 0) {allHands[0] = new Hand("9h,7h");
                    picHands[0].setImageResource(findPic("9h"));
                    picHands[1].setImageResource(findPic("7h"));}
                    else {allHands[1] = new Hand("9s,2s");}
                    break;

                case 3: if(player == 0) {allHands[0] = new Hand("8d,4s");
                    picHands[0].setImageResource(findPic("8d"));
                    picHands[1].setImageResource(findPic("4s"));}
                    else {allHands[1] = new Hand("9s,2s");}
                    break;

                case 4: if(player == 0) {allHands[0] = new Hand("7h,2d");
                    picHands[0].setImageResource(findPic("7h"));
                    picHands[1].setImageResource(findPic("2d"));}
                    else {allHands[1] = new Hand("9s,2s");}
                    break;
            }
            bitDeck.removeCards(allHands[player].hCards);
            allHands[player].fold = false;
            picHands[2].setImageResource(findPic("9s"));
            picHands[3].setImageResource(findPic("2s"));
        }
        else {
            //finds bit mask for cards
            for (int m = player * 2; m < (player * 2) + 2; m++) {
                int temp = 0;
                for (int i = 0; i < DECK_STRINGS.length - 1; i++) {
                    if (DECK_STRINGS[deck.get(m)].equals(DECK_STRINGS[i])) {
                        temp = i;
                        picHands[m].setImageResource(findPic(DECK_STRINGS[i]));
                        break;
                    }
                }

                tempLong = tempLong | DECK_BIT_MASKS[temp];

            }


            //set cards to array and remove cards from deck
            allHands[player] = new Hand(tempLong);
            bitDeck.removeCards(allHands[player].hCards);
            allHands[player].fold = false;
        }

    }

    public void loader(AssetManager asset) {
        try {

            InputStream input = asset.open("nonflush.ser");
            Eva.loadEnumHands(input);

        } catch (IOException e) {
            e.printStackTrace();

        }

    }

    public void startOver() {
        testButton.setTag(0);
        testButton.setEnabled(false);
        foldButton.setEnabled(false);
        raiseButton.setEnabled(false);
        showCards();


            myHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    userCurrentBet = 0;
                    userBet.setText("");
                    winner.setText("");
                    aiCommands.setText("");
                    aiCommands2.setText("");
                    aiCommands3.setText("");
                    deck = shuffleCards();
                    bitDeck = new Deck();
                    userAllIn = false;
                    raiseFlag = false;
                    foldFlag = false;
                    numCallers = 0;
                    minRaise = 0;
                    aiRaisePot = 0;
                    testButton.setText("CHECK");
                    updateRaiseSeek();

                    //sets all hands and their pictures
                    for (int n = 0; n < numPlayers; n++) {
                        setHands(n);
                    }

                    if(demoCount > 4) {
                        demoCount = 0;
                    }
                    else {
                        demoCount++;
                    }

                    ai = new AiRate(allHands[1].getStrHand(), Settings.aiName);
                    ai.fold = false;
                    allAi[0] = ai;
                    if (numPlayers > 2) {
                        ai2 = new AiRate(allHands[2].getStrHand(), Settings.aiName2);
                        ai2.fold = false;
                        allAi[1] = ai2;
                    }
                    if(numPlayers > 3) {
                        ai3 = new AiRate(allHands[3].getStrHand(), Settings.aiName3);
                        ai3.fold = false;
                        allAi[2] = ai3;
                    }
                    figureOutBlinds();

                    Grader teacher = new Grader(allHands[0].getStrHand());
                    grade.setText(teacher.returnGrade());
                    if(demoOn) {
                        gradePercent.setText(String.valueOf(teacher.winRate) + "%");
                    }

                    flop1.setImageResource(R.drawable.b1fv);
                    flop2.setImageResource(R.drawable.b1fv);
                    flop3.setImageResource(R.drawable.b1fv);
                    turn.setImageResource(R.drawable.b1fv);
                    river.setImageResource(R.drawable.b1fv);

                    //setting up ante for pot and subtracting from both players
                    if(Settings.anteOn) {
                        subAnte(ante);
                        potValue = ante * numPlayers;
                    }
                    else {
                        potValue = 0;
                    }
                    pot.setText("pot: " + potValue);

                    bitDeck.enumRandom();
                    long flopCards = bitDeck.getBoard();
                    flopper = CPlot.getCards(flopCards, 5);
                    testButton.setEnabled(true);
                    foldButton.setEnabled(true);
                    raiseButton.setEnabled(true);

                    //checks to see if money = 0
                    for (int l = 0; l < numPlayers - 1; l++) {
                        if (Settings.getIntMoney(allAi[l].aiName) <= 0) {
                            Settings.addMoney(allAi[l].aiName, 10000);
                            updateMoneyUI();
                        }
                    }

                    Settings.setHandStartingMoney();
                    if(startBetter != 0) {
                        figureAi(bigBlindNum, startBetter - 1);
                    }

                    if(!allHands[0].bigBlind) {
                        testButton.setText("CALL");
                    }
                    hideCards();
                    if(demoOn) {
                        p3c1.setVisibility(View.VISIBLE);
                        p3c2.setVisibility(View.VISIBLE);
                    }

                }
            }, 5000);


    }

    public void updateMoneyUI() {
        playerID.setText(userID + ": " + Settings.getMoney("User"));
        if(numPlayers == 2) {
            aiName2.setText(Settings.aiName + ": " + Settings.getMoney(Settings.aiName));
        }
        else {
            aiName.setText(Settings.aiName + ": " + Settings.getMoney(Settings.aiName));
            aiName2.setText(Settings.aiName2 + ": " + Settings.getMoney(Settings.aiName2));
        }

        if(numPlayers > 3) {
            aiName3.setText(Settings.aiName3 + ": " + Settings.getMoney(Settings.aiName3));
        }
        pot.setText("pot: " + potValue);
    }

    public void subAnte( int ante) {
        Settings.subMoney(Settings.aiName, ante);
        Settings.subMoney("User", ante);

        if(numPlayers > 2) {
            Settings.subMoney(Settings.aiName2, ante);
        }

        if(numPlayers > 3) {
            Settings.subMoney(Settings.aiName3, ante);
        }
        updateMoneyUI();
    }

    public void subMoney(String id, int amount) {
        Settings.subMoney(id, amount);
        potValue = potValue + amount;
    }

    public void addWinner(int winner) {
        if(winner == 0) {
            Settings.addMoney("User", potValue);
        }

        else if (winner == 1){
            Settings.addMoney(ai.aiName, potValue);
        }

        else if(winner == 2) {
            Settings.addMoney(ai2.aiName, potValue);
        }

        else if (winner == 3) {
            Settings.addMoney(ai3.aiName, potValue);
        }

        else {

            for (int y = 0; y < tieWinnersNames.length; y++) {
                Settings.addMoney(tieWinnersNames[y], potValue/tieWinnersNames.length);
            }
        }
        updateMoneyUI();
    }

    public void doRaise() {
        subMoney("User", userCurrentBet);
        raisePot = 0;
        userCurrentBet = 0;

        if(noMoreBetting()) {
            testButton.setEnabled(false);
            foldButton.setEnabled(false);
            raiseButton.setEnabled(false);
        }
        updateMoneyUI();

    }

    public void doEverything() {
        flop1.setImageResource(findPic(flopper[0]));
        flop2.setImageResource(findPic(flopper[1]));
        flop3.setImageResource(findPic(flopper[2]));
        turn.setImageResource(findPic(flopper[3]));
        river.setImageResource(findPic(flopper[4]));

        figureOutWinner();
        testButton.setTag(0);
    }

    public void figureOutWinner() {
        allHands[0].fold = foldFlag;

        for(int k = 0; k < numPlayers - 1; k++) {
            allHands[k+1].fold = allAi[k].fold;
        }

        int[] hValues = new int[numPlayers];

        for (int j = 0; j < numPlayers; j++) {
            hValues[j] = Eva.getValue(bitDeck.keysBoard[0] * allHands[j].hKey,
                    bitDeck.handsBoard[0] | allHands[j].hCards);
        }

        int winningHand = 0;
        int winningValue = 0;
        for (int k = 0; k < numPlayers; k++) {
            if (hValues[k] >= winningValue && !allHands[k].fold) {
                if (winningValue == hValues[k]) {
                    winningHand = -1;
                    tieWinners.add(k);
                } else {
                    winningValue = hValues[k];
                    winningHand = k;
                    tieWinners = new ArrayList<Integer>();
                    tieWinners.add(k);
                }
            }
        }
        if (winningHand >= 0) {
            if (winningHand == 0) {
                winner.setText("You Win!");
            }
            else if(winningHand == 1){
                winner.setText(Settings.aiName + " Wins!");
            }
            else if(winningHand == 2) {
                winner.setText(Settings.aiName2 + " Wins!");
            }

            else {
                winner.setText(Settings.aiName3 + " Wins!");
            }
        }
        //work on it's a tie
        else {
            String tieText = "It's a tie between ";
            tieWinnersNames = new String[tieWinners.size()];
            for (int w = 0; w < tieWinners.size(); w++) {
                int winningNum = tieWinners.get(w);

                if(winningNum == 0) {
                    tieWinnersNames[w] = "you";
                }

                else if(winningNum == 1) {
                    tieWinnersNames[w] = Settings.aiName;
                }

                else if(winningNum == 2) {
                    tieWinnersNames[w] =  Settings.aiName2;
                }

                else {
                    tieWinnersNames[w] =  Settings.aiName3;
                }
            }

            if(tieWinners.size() == 4) {
                winner.setText("It's a tie between " + tieWinnersNames[0] + ", " + tieWinnersNames[1]
                        + ", " + tieWinnersNames[2] + " and " + tieWinnersNames[3]);
            }
            else if(tieWinners.size() == 3) {
                winner.setText("It's a tie between " + tieWinnersNames[0] + ", " + tieWinnersNames[1]
                        + " and " + tieWinnersNames[2]);
            }
            else  {
                winner.setText("It's a tie between " + tieWinnersNames[0] + " and " + tieWinnersNames[1]);
            }
        }

        aiCommands.setText("");
        aiCommands2.setText("");
        aiCommands3.setText("");
        addWinner(winningHand);
    }

    public void popUp(View oldView) {
        LayoutInflater layoutInflater
                = (LayoutInflater)getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.playagainwindow, null);
        final PopupWindow popupWindow = new PopupWindow(
                popupView,
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);

        Button yesButton = (Button)popupView.findViewById(R.id.yesButton);
        Button noButton = (Button)popupView.findViewById(R.id.noButton);
        yesButton.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                Settings.resetMonies();
                updateMoneyUI();
                startOver();
            }});

        noButton.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                goHome(v);
            }});
        popupWindow.showAtLocation(oldView, Gravity.CENTER, 0, 0);
    }

    public void popUpInfo(View oldView) {
        LayoutInflater layoutInflater
                = (LayoutInflater)getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.grade_info, null);
        final PopupWindow popupInfo = new PopupWindow(
                popupView,
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);

        Button okButton = (Button)popupView.findViewById(R.id.exitInfo);
        okButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                popupInfo.dismiss();
            }});
        popupInfo.showAtLocation(oldView, Gravity.CENTER, 0, 0);
    }

    public void showMoreInfo(View oldView) {
        LayoutInflater layoutInflater
                = (LayoutInflater)getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.more_info, null);
        final PopupWindow popupInfo = new PopupWindow(
                popupView,
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);

        Button okButton = (Button)popupView.findViewById(R.id.exitmoreInfo);
        okButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                popupInfo.dismiss();
            }});
        popupInfo.showAtLocation(oldView, Gravity.CENTER, 0, 0);
    }

    public void goHome(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public boolean figureAi(int playerRaise, int starter) {
        int value;
        for (int i = starter; i < numPlayers - 1; i++) {
            if (playerRaise < aiRaisePot) {
                value = aiRaisePot;
            }
            else {
                value = playerRaise;
            }

            if(numCallers >= numPlayers) {
                break;
            }
            if(!allAi[i].fold) {
                String command = allAi[i].whatToDo(value);
                if (command.equals("RAISE") && !userAllIn) {
                    numCallers = 1;
                    doAiRaise(value);
                    allAi[i].currentBet = aiRaisePot;
                    allCommands[i].setText("has raised to");
                    allBets[i].setText(Integer.toString(allAi[i].currentBet));

                    if (aiRaisePot == Settings.getIntMoney("User")) {
                        testButton.setText("All in");
                        raiseButton.setEnabled(false);
                    } else {
                        minRaise = aiRaisePot;
                        updateRaiseSeek();
                        testButton.setText("CALL");
                    }
                    raiseFlag = true;
                } else if (command.equals("CHECK") || (command.equals("RAISE") && !userAllIn)) {
                    numCallers++;
                    allAi[i].currentBet = value;
                    aiRaisePot = value;
                    if (value > 0) {
                        allCommands[i].setText("has called");
                        allAi[i].currentBet = value;
                    } else {
                        allCommands[i].setText("has checked");
                    }
                    allBets[i].setText(Integer.toString(allAi[i].currentBet));
                } else {
                    numCallers++;
                    allAi[i].fold = true;
                    allCommands[i].setText("has fold");
                    if (checkFolds()) {
                        addWinner(0);
                        pot.setText("pot: " + potValue);
                        startOver();
                    }
                }
            }

            else {
                allCommands[i].setText("");
                numCallers++;
            }
        }

        if(numCallers == numPlayers) {
            raiseFlag = false;
        }

        return !raiseFlag;
    }

    public void doAiRaise(int value) {
        int lowestNum = getLowest();

        Random rand = new Random();
        int percent = rand.nextInt(10);
        if (percent == 0) {
            percent = 5;
        }
        int raiseAmount = Settings.getIntMoney("User")  * percent/100;
        aiRaisePot = raiseAmount + value;

        if(aiRaisePot > lowestNum) {
            aiRaisePot = lowestNum;
        }

        updateMoneyUI();
    }

    public void doCall() {
        if(testButton.getText().equals("CALL") || testButton.getText().equals("ALL IN")) {
            userCurrentBet = aiRaisePot;
            userBet.setText(Integer.toString(userCurrentBet));
        }
    }

    public void subCurrentBet() {
        subMoney("User", userCurrentBet);
        userBet.setText("");
        updateMoneyUI();
        updateRaiseSeek();
        userCurrentBet = 0;
    }

    public boolean checkFolds() {
        for (int m = 0; m < numPlayers - 1; m++) {
            if(!allAi[m].fold) {
                return false;
            }
        }
        return true;
    }

    public int getLowest() {
        int lowest = Settings.getIntMoney("User");
        for (int n = 0; n < numPlayers - 1; n++) {
            if(Settings.getIntMoney(allAi[n].aiName) < lowest) {
                lowest = Settings.getIntMoney(allAi[n].aiName);
            }
        }
        return lowest;
    }

    public void subAiBets() {
        for (int w = 0; w < numPlayers - 1; w++) {
            subMoney(allAi[w].aiName, allAi[w].currentBet);
            allAi[w].currentBet = 0;
            allBets[w].setText("");
        }
        updateMoneyUI();
    }

    public boolean noMoreBetting() {
        if(Settings.getIntMoney("User") <= 0) {
            return true;
        }
        for (int n = 0; n < numPlayers - 1; n++) {
            if(Settings.getIntMoney(allAi[n].aiName)  <= 0) {
                return true;
            }
        }
        return false;
    }

    public void resetValues() {
        raiseFlag = false;
        numCallers = 0;
        aiRaisePot = 0;
        subCurrentBet();
        subAiBets();
        /*for (int g = 0; g < numPlayers - 1; g++) {
            allCommands[g].setText("");
        }*/
        if (startBetter != 0) {
            figureAi(0, startBetter - 1);
        }
        Settings.setHandStartingMoney();

    }

    public void updateRaiseSeek() {

        raiseControl.setMax(Settings.getIntMoney("User") - minRaise - 1);
        raiseControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
                raiseNum.setText(Integer.toString(arg1 + minRaise + 1));

                if ((arg1 + minRaise + 1) == Settings.getIntMoney("User")) {
                    raiseButton.setText("ALL IN");
                } else {
                    raiseButton.setText("RAISE");
                }
            }
        });
    }

    public int getRaise() {
        return Integer.parseInt(raiseNum.getText().toString());
    }

    public void figureOutBlinds() {
        for(int q = 0; q < numPlayers - 1; q++) {
            allHands[q].smallBlind = false;
            allHands[q].bigBlind = false;
            allHands[q].startBetting = false;
        }

        if(dealerNum + 1 > numPlayers - 1) {
            allHands[(dealerNum + 1) % numPlayers].smallBlind = true;
        }
        else {
            allHands[dealerNum + 1].smallBlind = true;
        }

        if(dealerNum + 2 > numPlayers - 1) {
            allHands[(dealerNum + 2) % numPlayers].bigBlind = true;
        }
        else {
            allHands[dealerNum + 2].bigBlind = true;
        }
        if(dealerNum + 3 > numPlayers - 1) {
            allHands[(dealerNum + 3) % numPlayers].startBetting = true;
        }
        else {
            allHands[dealerNum + 3].startBetting = true;
        }

        for(int d = 0; d < numPlayers; d++) {
            if(allHands[d].bigBlind) {
                if(d == 0) {
                    userCurrentBet = bigBlindNum;
                }
                else {
                    allAi[d-1].currentBet = bigBlindNum;
                }
            }

            if(allHands[d].smallBlind) {
                if(d == 0) {
                    userCurrentBet = smallBlindNum;
                }
                else {
                    allAi[d-1].currentBet = smallBlindNum;
                }
            }
            if(allHands[d].startBetting) {
                startBetter = d;
            }
            dealerPic[d].setVisibility(View.GONE);
        }

        dealerPic[dealerNum].setVisibility(View.VISIBLE);
        setAiCurrentBet();
        dealerNum++;
        if(dealerNum > numPlayers - 1) {
            dealerNum = 0;
        }
        aiRaisePot = bigBlindNum;
    }

    public void setAiCurrentBet() {
        if(userCurrentBet > 0) {
            userBet.setText(String.valueOf(userCurrentBet));
        }
        for (int w = 0; w < numPlayers - 1; w++) {
            if(allAi[w].currentBet > 0) {
                allBets[w].setText(String.valueOf(allAi[w].currentBet));
            }
        }
    }

    public void showCards() {
        p3c1.setVisibility(View.VISIBLE);
        p3c2.setVisibility(View.VISIBLE);

        if(numPlayers > 2) {
            p2c1.setVisibility(View.VISIBLE);
            p2c2.setVisibility(View.VISIBLE);
        }

        if(numPlayers > 3) {
            p4c1.setVisibility(View.VISIBLE);
            p4c2.setVisibility(View.VISIBLE);
        }
    }

    public void hideCards() {
        p3c1.setVisibility(View.GONE);
        p3c2.setVisibility(View.GONE);

        if(numPlayers > 2) {
            p2c1.setVisibility(View.GONE);
            p2c2.setVisibility(View.GONE);
        }

        if(numPlayers > 3) {
            p4c1.setVisibility(View.GONE);
            p4c2.setVisibility(View.GONE);
        }
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
            default: return R.drawable.spades3;
        }

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

