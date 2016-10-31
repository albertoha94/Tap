package com.albertoha94.games.Tap;

import android.os.Bundle;
import android.view.View;
import android.os.Message;
import android.os.Handler;
import android.view.Gravity;
import android.app.Activity;
import android.widget.Button;
import android.view.ViewGroup;
import android.content.Intent;
import android.content.Context;
import android.widget.TextView;
import android.app.AlertDialog;
import android.graphics.Typeface;
import android.widget.LinearLayout;
import android.content.DialogInterface;
import android.view.animation.Animation;
import android.support.v7.app.ActionBar;
import android.view.animation.AnimationUtils;
import android.support.v7.app.AppCompatActivity;

import java.util.Random;

/**
 * Class: A_Game.java
 * Summary:         This class is used to show the game of the app. In here, the blocks are resized,
 *                  drawn, switched and animated. It's the only class that is used as a "game screen"
 *                  and it can work in both game modes, but only 1 at the time.
 * @author Albertoha94
 * @version 4
 * Changes------------------------------------------------------------------------------------------
 * 14/Mar/16:                   -Updated the whole class with its respective documentation.
 *                              -Added a code to fix the double tap exploit.
 * 16/Mar/16:                   -Added the methods showPauseDialog and showEndConfirmDialog, and renamed to showPauseDialog
 *                              and showExitConfirmDialog.
 * 18/Mar/16:                   -Logs removed.
 *                              -Edited the setCombo method so it doesnt work in tap mode.
 */
@SuppressWarnings("deprecation")
public class A_Game extends AppCompatActivity {

    /*--VARIABLES---------------------------------------------------------------------------------*/
	private Activity _a;
    private Boolean _tapcheck;
    private Button[] _b_use  ;
    private Button  _b_indicator, _b_pause;
	private Context  _c;
	private Handler _h_clock, _h_endgame;
    private LinearLayout _ll_container;
	private TextView _tv_score, _tv_timer, _tv_game_ready, _tv_combo_1, _tv_combo_2;
    private Typeface _gamefont;
    private View _v_bar;

    /*--STYLING VARIABLES-------------------------------------------------------------------------*/
    /*@Note: This variables are separated from the rest because they're used to enhance the app
             experience with the use of animations.                                               */
    private Animation
            _a_tv_game_ready_fadein, _a_tv_game_ready_pulse, _a_tv_score_pulse      ,
            _a_b_indicator_fadein  , _a_b_indicator_fadeout, _a_b_pause_fadein      ,
            _a_v_bar_slide         , _a_ll_container_fadein, _a_ll_container_fadeout,
            _a_tv_combo_1_fadeout  , _a_tv_combo_2_fadein  , _a_tv_combo_2_fadeout;
    private Boolean _fill, _first_appearance;
    private int _fill_value;
    private Handler _h_change, _h_getbsize, _h_filltime, _h_scorecolor, _h_hide;

	/*--STAT VARIABLES----------------------------------------------------------------------------*/
	private int _s_hits, _s_total, _s_streak, _s_lstreak;

	/*--GAME VARIABLES----------------------------------------------------------------------------*/
    /*@Note: This variables are used by the game.                                                 */
	private boolean _g_game_started, _g_game_available;
	private int _g_time, _g_time_ready, _g_score, _g_points_per_hit, _g_combo, _g_difficulty,
			_g_correct_id, _g_gamemode, _g_correct_color, _g_cutsec;

	/*--RUNNABLES---------------------------------------------------------------------------------*/
    /*
     * Summary: This runnable get the height of the screen to be used later on the showPauseDialog button and
     *          the indicator.
     */
    private Runnable _r_setsize = new Runnable() {

        @Override
        public void run() {

            if(GM.readConf(_a, GM.C_BISIZE) == 0) {
                int d = _b_indicator.getHeight();               //Since we dont have a height, we
                GM.writeConf(_a, GM.C_BISIZE, d);               //need to get it.
                _h_getbsize.post(_r_setsize);                   //Keep trying until it changes-
            } else {
                int size = GM.readConf(_a, GM.C_BISIZE);
                ViewGroup.LayoutParams params = _b_indicator.getLayoutParams(),
                                       params2 = _b_pause.getLayoutParams();
                params2.width  = size / 4 * 3;
                params2.height = size / 4 * 3;                  //We got the size now, so we just
                _b_indicator.setLayoutParams(params);           //adjust the buttons to the size we
                _b_pause.setLayoutParams(params2);              //want.
            }

        }
    };

    /*
     * Summary: This one creates the animation of the fade in and fade out of the animation of the
     *          indicator.
     */
    private Runnable _r_change = new Runnable() {

        @Override
        public void run() {
            setBlocksLayout();
            setCorrectColor();
            _b_indicator.setBackgroundResource(_g_correct_color);
            _tapcheck = false;
            if(!_first_appearance) {
                if(_g_gamemode != GM.GM_CTAP || _g_gamemode != GM.GM_CMIN) {
                    _b_indicator.clearAnimation();                      //This animation happens
                    _b_indicator.setAnimation(_a_b_indicator_fadein);   //only when the its not the
                    _a_b_indicator_fadein.start();                      //first block shown AND we
                }                                                       //are not playing single
                _ll_container.clearAnimation();                         //color modes.
                _ll_container.setAnimation(_a_ll_container_fadein);
                _a_ll_container_fadein.start();                     //Animation used in the container.
            }
        }

    };

    /**
     * Summary: Creates the animation that fills the textview that represents the time.
     *          This one happens only at the beginning of the game.
     */
    private Runnable _r_fill = new Runnable() {
        @Override
        public void run() {

            if(_fill_value < _g_time) {

                _tv_timer.setText(String.valueOf(_fill_value));
                _fill_value++;
                _h_filltime.postDelayed(_r_fill, 10);              //Repeats until we beat the time.

            }

        }
    };

    /*
     *Summary:      Main runnable of the game, in here is where the goes and where it makes a
     *              "countdown" before it starts.
     */
    private Runnable _r_tick = new Runnable() {

        public void run() {
            /*--COUNTDOWN-------------------------------------------------------------------------*/
            if (_g_time_ready > 0) {
                _g_time_ready--;
                if(_tv_game_ready.getVisibility() != View.VISIBLE)
                    _tv_game_ready.setVisibility(View.VISIBLE);       //Show tv_game_ready.
                _tv_game_ready.setText(String.valueOf(_g_time_ready));
                _tv_game_ready.setAnimation(_a_tv_game_ready_fadein); //Update _tv_game_ready.
                if(!_a_tv_game_ready_fadein.isInitialized()) {
                    _a_tv_game_ready_fadein.start();                  //Do it only once.

                    /*--Also, GET BLOCK SIZES FOR THE GAME if we dont have them-------------------*/
                    for(int i = 0; i < GM.S_6 + 1; i++)
                        while(GM.readConf(_a, GM.C_SIZESX[i]) == 0) {
                            setBlockSize(i, false);
                        }
                }

                if(!_a_v_bar_slide.isInitialized())
                    _a_v_bar_slide.start();                           //Animate the fill bar.

                /*--FILL THE TIMER IF MODE IS 1MIN------------------------------------------------*/
                if(_g_gamemode == GM.GM_CMIN || _g_gamemode == GM.GM_MIN) {
                    if(!_fill) {                                      //Do it only once too.
                        _h_filltime.postDelayed(_r_fill, 10);         //Fill the time textview
                        _fill = true;                                 //animation.
                    }
                }
            } else {

				/*--GAME HAS STARTED--------------------------------------------------------------*/
                _tv_game_ready.clearAnimation();
                if(_tv_game_ready.getVisibility() != View.GONE)
                    _tv_game_ready.setVisibility(View.GONE);          //Hide _tv_game_ready

                if(_g_gamemode == GM.GM_100T || _g_gamemode == GM.GM_CTAP)
                    timeTic_Tap();
                else                                                  //Tic according to game mode
                    timeTic_Min();
            }

			/*--WHEN _g_time_ready IS 0, START GAME AND SHOW GO!----------------------------------*/
            if (_g_time_ready == 0 && !_g_game_started) {
                _tv_game_ready.setText(getResources( ).getString( R.string.Go));
                _g_game_started = true;
                setFullLayout();

                /*--DISPLAY THE PAUSE BUTTON------------------------------------------------------*/
                if(_b_pause.getVisibility() == View.INVISIBLE) {
                    _b_pause.startAnimation(_a_b_pause_fadein);
                    _b_pause.setVisibility(View.VISIBLE);
                }
            }
            _h_clock.postDelayed(_r_tick, 1000);                      //Restart thread.
        }

    };

    /*
     * Summary: This runnables switches the timer color back to normal.
     */
    Runnable _r_score_color = new Runnable() {

        @Override
        public void run() {
            int y = getResources().getColor(R.color.white);
            _tv_score.setTextColor(y);
        }

    };

    /*
     *Summary:      Hides the combo textview after a certain time.
     */
    Runnable _r_hide = new Runnable() {

        @Override
        public void run() {
            _tv_combo_2.clearAnimation();
            _tv_combo_2.startAnimation(_a_tv_combo_2_fadeout);
        }

    };

/*--METHODS---------------------------------------------------------------------------------------*/

    /*
     *Summary:       Mandatory method for Android. Shows the layout defined(activity_game) and
     *               executes the methods declare(), setListeners() and setGame().
     *Parameters:    A bundle generated by default.
     */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState    );
		setContentView(R.layout.activity_game);
		declare(     );
        setListeners();
		setGame(     );
	}

    /*
     *Summary:       Another default method from Android, in here we just showPauseDialog the game with the
     *               method pauseGame().
     */
    @Override
    protected void onStop() {
        super.onStop();
        pauseGame();
    }

    /*
     *Summary:       When the user presses the back button, instead of returning to the previous
     *               activity, it calls the method pauseGame().
     */
    @Override
	public void onBackPressed() {
		pauseGame();
	}

    /*
     *Summary:       One of the main methods, it initializes most of the variables used for the game
     *               and styling, it configures the game according to the settings, hides the action
     *               bar and initializes a handler that allows us to get the width of the screen.
     */
	public void declare() {

		_a 				  = A_Game.this;
		_c				  = A_Game.this;
		_g_score      	  = 0;
		_g_time_ready 	  = 4;                          //1 second extra because of the handler.
		_g_game_started   = false;                      //To avoid the user clicks before he should.
		_g_game_available = true ;                      //To avoid the double showPauseDialog menu.
        _tapcheck         = false;                      //Variable used to check the user doesnt
                                                        //more points if the button is pressed more
                                                        //than once.
		_g_gamemode       = GM.readGStat(_a, GM.GS_MODE);
		_g_difficulty     = GM.readGStat(_a, GM.GS_DIF );
        _fill_value       = 0;       //Used in a handler to "fill" the timer before the game starts.
        _fill             = false;   //Makes sure the handler that "fills" the timer is used only
                                     // once.
        _first_appearance = true;   //Used to switch the animation of the container and the layout
                                     //the first time they're created.

        /*--STATS---------------------------------------------------------------------------------*/
        /*Description:      These variables are used only to keep track of the user statistics in
         *                  current game, theyre used only on this activity and are sent to the main
         *                  game data once the game is over.
         */

        _s_hits    = 0;         //Correct taps.
        _s_total   = 0;         //Total blocks created.
        _s_streak  = 0;         //Current streak in this game.
        _s_lstreak = 0;         //Longest streak made in this game.

        /*--ACTIVITY ITEMS-----------------------------------------1-------------------------------*/
        _v_bar         = findViewById(R.id.v_fill);
        _tv_score      = (TextView)findViewById(R.id.tv_score   );
        _tv_timer      = (TextView)findViewById(R.id.tv_timer   );
        _b_indicator   = (Button)findViewById(R.id.b_indicator  );
        _b_pause       = (Button)findViewById(R.id.b_pause      );
        _tv_game_ready = (TextView)findViewById(R.id.tv_readyup );
        _tv_combo_1    = (TextView)findViewById(R.id.tv_combo1  );
        _tv_combo_2    = (TextView)findViewById(R.id.tv_combo2  );
        _ll_container  = (LinearLayout)findViewById(R.id.llh_maincontainer);

        /*NOTE:         The buttons inside the container are not initialized here because they
         *              dinamically change. Instead they're initialized inside setFullLayout().
         */

        /*--CONFIGURING GAME----------------------------------------------------------------------*/
        /*Description:      Defines how to set the timer and the combo according to the game mode
         *                  we are currently on.
         */

        if(_g_gamemode == GM.GM_CMIN || _g_gamemode == GM.GM_MIN) {
            _g_time  = 61;       //Time's set to 60 seconds, 1 extra because of the handler.
            _g_combo =  1;       //Combo is exclusive for this mode.
            _g_points_per_hit = getResources().getIntArray(R.array.pph)[_g_difficulty];
                                //defines how many points you get for 1 correct tap.
        } else {
            _g_time   = -1;                       //It starts in 0 but we go to -1 because of the
                                                  // handler.
            _g_cutsec =  0;                       //Removes a second from your timer, a bonus.
            _v_bar.setVisibility(View.INVISIBLE); //Remove the bar, this one is used in the game
                                                  //mode 1 minute.
        }

        /*--ANIMATIONS----------------------------------------------------------------------------*/
        /*NOTE:         In order to identify more easily to what view the animation belongs, they
         *              were written in the following format.
         *              Format:
         *              _a_[view name]_[animation]
         */

        _a_tv_game_ready_fadein = AnimationUtils.loadAnimation(_c, R.anim.tv_fadein  );
        _a_tv_game_ready_pulse  = AnimationUtils.loadAnimation(_c, R.anim.tv_pulse   );
        _a_tv_score_pulse       = AnimationUtils.loadAnimation(_c, R.anim.tv_pulse_2 );
        _a_b_pause_fadein       = AnimationUtils.loadAnimation(_c, R.anim.btn_fadein );
        _a_b_indicator_fadein   = AnimationUtils.loadAnimation(_c, R.anim.btn_fadein );
        _a_b_indicator_fadeout  = AnimationUtils.loadAnimation(_c, R.anim.fadeout    );
        _a_ll_container_fadein  = AnimationUtils.loadAnimation(_c, R.anim.btn_fadein );
        _a_ll_container_fadeout = AnimationUtils.loadAnimation(_c, R.anim.fadeout    );
        _a_v_bar_slide          = AnimationUtils.loadAnimation(_c, R.anim.vw_slide   );
        _a_tv_combo_1_fadeout   = AnimationUtils.loadAnimation(_c, R.anim.tv_combo_fadeout);
        _a_tv_combo_2_fadein    = AnimationUtils.loadAnimation(_c, R.anim.tv_combo_fadein );
        _a_tv_combo_2_fadeout   = AnimationUtils.loadAnimation(_c, R.anim.tv_combo_fadeout);

		/*--HANDLER-------------------------------------------------------------------------------*/
        _h_getbsize    = new Handler(); //Gets and sets the size of the indicator and showPauseDialog buttons.
		_h_clock       = new Handler(); //Main handler, moves the time of the game.
        _h_change      = new Handler(); //Changes the indicator and container.
        _h_filltime    = new Handler(); //Fills the time before the game starts.
        _h_scorecolor  = new Handler(); //Changes the color when the user taps a button.
        _h_hide        = new Handler(); //Hides a textview after a certain time.

        /*Used when the game is over. When it is it stops the _h_clock handler, updates the
         *game information according to the stats made this session and shows a Dialog that ends the
         * game.
         */
		_h_endgame  = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
					case 0:
						_h_clock.removeCallbacks(_r_tick);
                        updateGameData();
                        showEndGameDialog();
						break;
				}
			}
		};


        /*--STYLLING ACTIVITY ITEMS---------------------------------------------------------------*/
        _gamefont = Typeface.createFromAsset(getAssets(), "gamefont.otf");
        _tv_game_ready.setTypeface(_gamefont);
        _tv_combo_1.setTypeface(_gamefont);
        _tv_combo_2.setTypeface(_gamefont);
        _tv_score.setTypeface(_gamefont);
        _tv_timer.setTypeface(_gamefont);
        _b_pause.setTypeface(_gamefont);

        /*--SETTING UP ANIMATIONS-----------------------------------------------------------------*/
        _v_bar.setAnimation(_a_v_bar_slide);        //Animation to "fill" the bar in countdown.
        _tv_score.setAnimation(_a_tv_score_pulse);  //A "bump" in tv_score when user taps a color.

        /*--HIDE TEXTVIEWS------------------------------------------------------------------------*/
        _tv_combo_1.setVisibility(View.INVISIBLE);
        _tv_combo_2.setVisibility(View.INVISIBLE);

		/*--HIDE ACTION BAR-----------------------------------------------------------------------*/
		ActionBar ActBar = getSupportActionBar();
        assert ActBar != null;
        ActBar.hide();

        /*--GET SIZE OF THE BUTTON----------------------------------------------------------------*/
        _h_getbsize.post(_r_setsize);              /*We get how to display the button showPauseDialog and the
                                                     * indicator
                                                     */

	}

    /*
     *Summary:      Creates all the buttons listeners used in the activity. This time its just the
     *              showPauseDialog button.
     */
    public void setListeners(){

        /*--PAUSE BUTTON----------------------------------------------------------------------------
         *Description:          When pressed, pauses the game.
         */

        _b_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseGame();
            }
        });

    }

    /*--Dialogs-----------------------------------------------------------------------------------*/
    /*
     *Summary:      Executes a "showPauseDialog", this only means that it'll stop the main handler(_h_clock),
     *              "stops" the game by making _g_game_started false, sets a time for _g_time_ready
     *              required to start the countdown again and calls a dialog that is the "showPauseDialog menu".
     */
    public void pauseGame() {

        if(_g_game_available) {
            _g_time_ready   = 4;
            _g_game_started = false;
            showPauseDialog();
            _h_clock.removeCallbacks(_r_tick);
        }

    }

    /*
     *Summary:      Displays an AlertDialog that is treated as the end menu screen of the game.
     *              In here the user can see the total blocks created in this session and the total
     *              he has correct. It also shows the current highest score and compares if you beat
     *              it.
     *              Comes with 2 buttons, first button is play again which restarts teh activity and
     *              the second is return to the menu, which sends the user to the A_Menu activity.
     */
    public void showEndGameDialog() {

        /*--GET TOP SCORES------------------------------------------------------------------------*/
        Row[] leaders = GM.readLeaders(_c, A_Leaderboards.DATAFILES[_g_gamemode]);
        boolean bind;
        int draw_res;

        if (_g_gamemode == GM.GM_CMIN || _g_gamemode == GM.GM_CTAP) {   //Set values to the insert
            bind     = true;                                            //row according to the game
            draw_res = _g_correct_color;                                //mode.
        }
        else {
            bind     = false;
            draw_res = Row.EMPTY;
        }
        Row insertion = new Row(bind, 0, draw_res, _g_difficulty); //The new row we will try to add.

        /* Dialog title and content---------------------------------------------------------------*/
        String[] d    = new String[3];
        d[EndGameDialog.TITLE]      = getResources().getString(R.string.Game_finished);
        d[EndGameDialog.COMPLETION] = _s_hits + "/" + (_s_total - 1);
        if(_g_gamemode == GM.GM_MIN || _g_gamemode == GM.GM_CMIN) { //Content according
            // to the high score.
            if (_g_score > leaders[0].get_score())
                d[EndGameDialog.MESSAGE] = getResources().getString(R.string.Your_score)
                        + " " + _g_score + "\n" + getResources().getString(R.string.
                        new_highest_score);
            else
                d[EndGameDialog.MESSAGE] = getResources().getString(R.string.Your_score)
                        + " " + _g_score + "\n" + getResources().getString(R.string.
                        Best_score) + " " + leaders[0].get_score();
            insertion.set_score(_g_score);
        } else {
            if (_g_time < leaders[0].get_score() || leaders[0].get_score() == Row.EMPTY) {
                //Lowest time!
                d[1] = getResources().getString(R.string.Your_time) + " " + _g_time + "\n" +
                        getResources().getString(R.string.new_highest_time);
            } else
                d[EndGameDialog.MESSAGE] = getResources().getString(R.string.Your_time) + " " +
                        _g_time + "\n" + getResources().getString(R.string.Best_time) + " " +
                        leaders[0].get_score();
            insertion.set_score(_g_time);
        }

        GM.insertRow(_c, A_Leaderboards.DATAFILES[_g_gamemode], insertion); //Will try to add the new row.
        EndGameDialog dialog = new EndGameDialog(_c, d, _a);
        dialog.show();                                                      //Show our new dialog
                                                                            // with the content we
                                                                            // created.

    }

    /*
     * Summary:                 Displays a pause menu when called. Here the user will have 3 options.
     *                          Restart, continue or exit the game.
     */
    public void showPauseDialog() {

        AlertDialog.Builder dlg = new AlertDialog.Builder(_c, R.style.apptheme_Dialog);
        String Pos = getResources().getString(R.string.Continue);
        String Neg = getResources().getString(R.string.QuitGame);
        String Neu = getResources().getString(R.string.restart );
        TextView t = new TextView(_c);                                          //Set custom title.
        t.setTypeface(_gamefont);
        t.setGravity(Gravity.CENTER);
        t.setText(getResources().getString(R.string.Pause));
        t.setTextSize(getResources().getDimension(R.dimen.tv_title_dif_tvsize));
        dlg.setCustomTitle(t);
        dlg.setCancelable(false);

		/*--END GAME BUTTON-----------------------------------------------------------------------*/
        dlg.setNegativeButton(Neg, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface ODialog, int OWhich) {
                // TODO Auto-generated method stub
                showEndConfirmDialog();
            }

        });

        /*--CONTINUE------------------------------------------------------------------------------*/
        dlg.setPositiveButton(Pos, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface ODialog, int OWhich) {
                // TODO Auto-generated method stub
                setGame();
            }

        });

        /*--Restart-------------------------------------------------------------------------------*/
        dlg.setNeutralButton(Neu, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent x = new Intent(_a, A_Game.class);
                _a.finish();
                _a.startActivity(x);

            }

        });
        dlg.create().show();

    }

    /*
     * Summary:                 Displays a confirmation menu when called. It's just to make sure the
     *                          user really ewants to get out of the game.
     */
    public void showEndConfirmDialog() {

        String Pos = getResources().getString(android.R.string.yes);
        String Neg = getResources().getString(android.R.string.no );
        AlertDialog.Builder dlg = new AlertDialog.Builder(_c, R.style.apptheme_Dialog );    //Dialog
        dlg.setMessage(getResources().getString(R.string.Exit_game_confirm));
        dlg.setCancelable(false);

        /*--END GAME------------------------------------------------------------------------------*/
        dlg.setPositiveButton(Pos, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                Intent x = new Intent(_c, A_Menu.class);
                x.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(x);
                finish();
            }

        });

        /*--CANCEL AND CONTINUE PLAYING-----------------------------------------------------------*/
        dlg.setNegativeButton(Neg, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                showPauseDialog();
            }

        });
        dlg.create().show( );

    }

    /*--------------------------------------------------------------------------------------------*/
    /*
     *Summary:      Starts the handler _h_clock with the timer _r_tick. With this, the game starts.
     */
	public void setGame() {
		_h_clock.postDelayed(_r_tick, 1000);
	}



    /*
     *Summary:      This methods happens every second if the game mode is 1 minute.
     */
    public void timeTic_Min() {

        if (_g_time > 1) {
            /*--GAME IS STILL GOING---------------------------------------------------------------*/
            _g_time--;

            /*
             *Bar's width is resized every second according to the current time.
             */
            ViewGroup.LayoutParams param = _v_bar.getLayoutParams();
            param.width = GM.readConf(_a, GM.C_WIDTH) / 60 * _g_time;
            _v_bar.setLayoutParams(param);

            /*
             *Between 11 and 20 seconds lapse the timer and bar will be yellow.
             */
            if (_g_time > 10 && _g_time <= 20) {
                int y = getResources().getColor(R.color.amber);
                _tv_timer.setTextColor(y);
                _v_bar.setBackgroundColor(y);
            }

            /*
             *In the final 10 seconds, the timer and the bar will be red.
             */
            if (_g_time <= 10) {
                int y = getResources().getColor(R.color.auburn);
                _tv_timer.setTextColor(y);
                _v_bar.setBackgroundColor(y);
            }

            _tv_timer.setText(String.valueOf(_g_time));         //Update the textview to current time.
            _tv_timer.startAnimation(_a_tv_game_ready_pulse);   //A small animation that "bumps" the
                                                                //timer every second.
        } else {
            /*--GAME IS FINISHED------------------------------------------------------------------*/
            _tv_timer.setText(String.valueOf(_g_time));       //Update the textview to current time.
            _g_game_available = false;                        //You can't showPauseDialog the game now.
            _h_endgame.sendEmptyMessage(0);                   //Calls the end game menu.
            GM.writeGStat(_a, GM.GS_SCORE, _g_score);         //Writes our score in this session.
        }

    }

    /*
     *Summary:      Same as timeTic_Min but with the game mode 25 Taps.
     */
    public void timeTic_Tap(){

        if (_g_score < 25) {
            /*--WE HAVENT REACHED 25 TAPS, SO GAME GOES ON.---------------------------------------*/
            _g_time++;
            _tv_timer.setText(String.valueOf(_g_time));         //Update the textview to current time.
        } else {
            /*--GAME IS FINISHED------------------------------------------------------------------*/
            _g_game_available = false;                      //You cant showPauseDialog now.
            _h_endgame.sendEmptyMessage(0);                 //Calls the end game menu.
            GM.writeGStat(_a, GM.GS_TIME, _g_time);         //Writes the time done in this session.
        }

    }

    /*
     *Summary:      This method checks what button was pressed and what button is the correct one.
     *              If both are the same then the user gets points according to the game mode.
     *Parameters:   A view that is recieved automatically when the user taps on a button, in this
     *              case we recieve a button.
     */
	public void checkTap(View v) {

		if (_g_game_started && !_tapcheck) {
            boolean wascorrect = v.getId() == _g_correct_id;
			if (wascorrect) {
                if(_g_gamemode == GM.GM_CMIN || _g_gamemode == GM.GM_MIN) {
                    _g_score += (_g_points_per_hit * _g_combo);
                }
                else {
                    _g_score++;
                    _g_cutsec++;

                    /*--CUTSEC BONUS--------------------------------------------------------------*/
                    if(_g_cutsec == 5) {
                        _g_time  -= 2;
                        _g_cutsec = 0;
                        _tv_timer.setText(String.valueOf(_g_time));
                    }
                }

				/*--INCREASE STAT-----------------------------------------------------------------*/
				_s_hits++;
				_s_streak++;
                setCombo();
                GM.writeGStat(_a, GM.GS_HITS, _s_hits);
                GM.writeGStat(_a, GM.GS_STREAK, _s_streak);
                updateStatsColors();

                /*--CHECK FOR STREAK--------------------------------------------------------------*/
				if(_s_streak > _s_lstreak){
					_s_lstreak = _s_streak;
					GM.writeGStat(_a, GM.GS_LSTREAK, _s_lstreak);
				}
			} else {
				/*--NOT CORRECT-------------------------------------------------------------------*/
				if(_g_difficulty == GM.DIF_EX){
					/*--END GAME IF IN EXTREME DIFFICULTY FOR ANY MODE----------------------------*/
					_g_time  = 0;
					_g_score = 0;
                    _g_game_available = false;
                    _h_endgame.sendEmptyMessage(0);
					_tv_score.setText(String.valueOf(_g_score));
					_tv_timer.setText(String.valueOf(_g_time));
				} else {
                    if(_g_gamemode == GM.GM_CMIN || _g_gamemode == GM.GM_MIN) {
                        _g_score -= _g_points_per_hit;              //Lose points according to pph.
                        if (_g_score <= 0)
                            _g_score = 0;
                    }
                    else {
                        _g_time += 5;                               //Increase time by 5.
                        _tv_timer.setText(String.valueOf(_g_time));
                    }
					/*--STATS---------------------------------------------------------------------*/
					_s_streak = 0;
                    setCombo();
				}
            }
            _tapcheck = true;
            setFullLayout();
            /*--SHOW SCORE ANIMATION--------------------------------------------------------------*/
            _a_tv_score_pulse.start();
            int color;
            if (wascorrect)
                color = getResources().getColor(R.color.ao);
            else
                color = getResources().getColor(R.color.auburn);
            _tv_score.setTextColor(color);
            _h_scorecolor.postDelayed(_r_score_color, 500);

			/*--UPDATE SCORE----------------------------------------------------------------------*/
			_tv_score.setText(String.valueOf(_g_score));
		}

	}


    /*
     *Summary:      Increase the combo value according to the streak the user currently has. The max
     *              the combo can get is 5 and it's lost if he chooses a wrong color than shown.
     *              This combo only happens in the mode 1 minute.
     *              The combo is increased every 5.
     */
	public void setCombo(){

        if(_g_gamemode == GM.GM_MIN || _g_gamemode == GM.GM_CMIN) {
            int _p_combo = _g_combo;            //Save old combo value.
            if (_s_streak >= 20)                 //Then we get a new one.
                _g_combo = 5;
            else if (_s_streak >= 15)
                _g_combo = 4;
            else if (_s_streak >= 10)
                _g_combo = 3;
            else if (_s_streak >= 5)
                _g_combo = 2;
            else
                _g_combo = 1;
            if (_p_combo != _g_combo) {                      //If true, we have a new combo!!
                _tv_combo_1.setText(_p_combo + "X");
                _tv_combo_2.setText(_g_combo + "X");
                int h = GM.readConf(_a, GM.C_HEIGHT);
                _tv_combo_1.setY(h / 3 * 2);
                _tv_combo_2.setY(h / 3 * 2);
                _tv_combo_1.startAnimation(_a_tv_combo_1_fadeout);
                _tv_combo_2.startAnimation(_a_tv_combo_2_fadein);
                if (_g_combo == 1)
                    _h_hide.postDelayed(_r_hide, 1500);     //call the handler.
            }

		/*--STAT----------------------------------------------------------------------------------*/
            GM.writeGStat(_a, GM.GS_COMBO, _g_combo);
        }

	}



    /*
     *Summary:      Adds the data obtained from the game session to the overall game data, this
     *              increases the statistics and achievements the user has.
     */
    public void updateGameData(){

        /*--TOTAL GAMES---------------------------------------------------------------------------*/
        int nogames = GM.readGData(_a, GM.GD_TOTAL_GAMES);
        nogames++;
        GM.writeGData(_a, GM.GD_TOTAL_GAMES, nogames);

        /*--PERFECT GAMES-------------------------------------------------------------------------*/
        if(_s_hits == (_s_total - 1) && _s_total > 1) {
            int perfectg = GM.readGData(_a, GM.GD_PERFECT_G);
            perfectg++;
            GM.writeGData(_a, GM.GD_PERFECT_G, perfectg);
        }

        /*--LONGEST STREAK------------------------------------------------------------------------*/
        int datastreak = GM.readGData(_a, GM.GD_LONGSTREAK);
        if(_s_lstreak > datastreak) {
            GM.writeGData(_a, GM.GD_LONGSTREAK, _s_lstreak);
        }

        /*--GAMEMODES-----------------------------------------------------------------------------*/
        int x;
        String d = "";
        switch(_g_gamemode){
            case GM.GM_MIN:
                d = GM.GD_MING;
                break;
            case GM.GM_100T:
                d = GM.GD_TAPGAMES;
                break;
            case GM.GM_CMIN:
                d = GM.GD_CMING;
                break;
            case GM.GM_CTAP:
                d = GM.GD_CTAPG;
                break;
        }
        x = GM.readGData(_a, d);
        x++;
        GM.writeGData(_a, d, x);

        /*--HITS----------------------------------------------------------------------------------*/
        int nohits = _s_hits + GM.readGData(_a, GM.GD_TOTALBLOCK);
        GM.writeGData(_a, GM.GD_TOTALBLOCK, nohits);

        /*--COLORS--------------------------------------------------------------------------------*/
        int[] values = new int[GM.GS_COLORS.length];
        for(int i = 0; i < values.length; i++) {
            values[i] = GM.readGStat(_a, GM.GS_COLORS[i]) + GM.readGStat(_a, GM.GD_COLORS[i]);
            GM.writeGData(_a, GM.GD_COLORS[i], values[i]);
        }

    }

    /*
     *Summary:      Updates the counter of the colors that have been used in the current session.
     *              This information is passed on later to the overall game data.
     */
    public void updateStatsColors(){

        int pos = 0, value;
        for(int i = 0; i < GM.COLORS.length; i++)
            if(_g_correct_color == GM.COLORS[i]) {             //Find what color are we looking for.
                pos = i;
                break;
            }
        value = GM.readGStat(_a, GM.GS_COLORS[pos]);
        value++;
        GM.writeGStat(_a, GM.GS_COLORS[pos], value);

    }

    /*
     *Summary:      Another main method of the app. it first animates both the indicator and the
     *              container when the game first starts. This two have a different animation later
     *              on.
     *
     */
    public void setFullLayout() {

        /*--FIRST ANIMATION, MAKE EVERYTHING VISIBLE----------------------------------------------*/
        if(_first_appearance) {

            /*--USE THE FADE IN ANIMATION FOR BOTH------------------------------------------------*/
            _b_indicator.setAnimation(_a_b_indicator_fadein);
            _b_indicator.setVisibility(View.VISIBLE);
            _a_b_indicator_fadein.start();

            _ll_container.setAnimation(_a_ll_container_fadein);
            _ll_container.setVisibility(View.VISIBLE);
            _a_ll_container_fadein.start();

            _h_change.post(_r_change);
            _first_appearance = false;                                     //This happens only once.

        } else {
            /*
             *Note:         The fadeout animation only happens to the indicator IF the game mode is
             *              NOT single color. Since in single color it always remains the same,
             *              there's no need to switch.
             */

            /*--After this its the fadeout animation----------------------------------------------*/
            if(_g_gamemode == GM.GM_MIN || _g_gamemode == GM.GM_100T) {
                _b_indicator.setAnimation(_a_b_indicator_fadeout);
                _a_b_indicator_fadeout.start();
            }

            /*--Container always animates---------------------------------------------------------*/
            _ll_container.setAnimation(_a_ll_container_fadeout);
            _a_ll_container_fadeout.start();
            _h_change.postDelayed(_r_change, 250);
        }

		/*--Total blocks stat---------------------------------------------------------------------*/
        _s_total++;
        GM.writeGStat(_a, GM.GS_TOTAL, _s_total);

    }

    /*
     *Summary:      Sets how many blocks are meant to be shown on screen. This varies on the
     *              difficulty and a random value.
     */
    public void setBlocksLayout() {

        Button[] b_unused;
        int[] used = null, unused = null;
        Random R = new Random();
        int D    = R.nextInt(9);
        int size = 0;

        /*--ASSIGN BLOCKS ACCORDIGN TO DIFFICULTY-------------------------------------------------*/
        switch(_g_difficulty) {
            case GM.DIF_EZ:
                /*--20% CHANCE YOU GET 3X3, 80% YOU GET 4X4---------------------------------------*/
                if (D >= 8) {
                    used   = GM.S_3X3;
                    unused = GM.S_3X3_EMPTY;
                    size = GM.S_3;
                } else {
                    used   = GM.S_4X4;
                    unused = GM.S_4X4_EMPTY;
                    size = GM.S_4;
                }
                break;
            case GM.DIF_MED:
                /*--50% YOU GET 5X5, 50% YOU GET 4X4----------------------------------------------*/
                if ( D >= 5 ) {
                    used   = GM.S_5X5;
                    unused = GM.S_5X5_EMPTY;
                    size = GM.S_5;
                } else {
                    used   = GM.S_4X4;
                    unused = GM.S_4X4_EMPTY;
                    size = GM.S_4;
                }
                break;
            case GM.DIF_HARD:
                /*--30% YOU GET 6X6, 70% GET 5X5--------------------------------------------------*/
                if ( D >= 7 ) {
                    used = GM.S_6X6;
                    size = GM.S_6;
                } else {
                    used   = GM.S_5X5;
                    unused = GM.S_5X5_EMPTY;
                    size = GM.S_5;
                }
                break;
            case GM.DIF_EX:
                /*--ALWAYS 6X6--------------------------------------------------------------------*/
                used = GM.S_6X6;
                size = GM.S_6;
                break;
        }
        /*--We got the quantity, now it's time to show them---------------------------------------*/
        assert used != null;
        _b_use = new Button[used.length];
        for (int i = 0; i < used.length; i++) {
            _b_use[i] = (Button)findViewById(used[i]);
            _b_use[i].setVisibility(View.VISIBLE);
        }
        if(unused != null) {
            b_unused = new Button[unused.length];
            for (int i = 0; i < unused.length; i++) {
                b_unused[i] = (Button) findViewById(unused[i]);
                b_unused[i].setVisibility(View.GONE);
            }
        }
        /*--SIZE THE BLOCKS TO THE CORRECT DIMENSION----------------------------------------------*/
        setBlockSize(size, true);

    }

    /*
     *Summary:      Sets the size the blocks will have according to the layout. These blocks will
     *              be of the same size and with an extra margin in their height to keep them
     *              separated. This method has 2 purposes:
     *              -Get the dimensions of ALL the block layouts at the same time. It is used only
     *               the first game after installation.
     *              -Draw the blocks with the new size-
     *Parameters:   oSize: recieves an integer that is related to the size of the block. It is used
     *                     to access a file that has the corresponding size.
     *              oResize: Decides if we resize the blocks now or not. It changes only the first
     *                       game so it can get the dimensions that will be used without drawing
     *                       anything on screen.
     */
    public void setBlockSize(int oSize, boolean oResize) {

        /*--GET BLOCK SIZE FOR CURRENT LAYOUT-----------------------------------------------------*/
        int sizex = GM.readConf(_a, GM.C_SIZESX[oSize  ]);
        int sizey = GM.readConf(_a, GM.C_SIZESY[oSize]);
        int margx = GM.readConf(_a, GM.C_BMARGINX[oSize]);

        /*--IF WE DONT HAVE SIZES, GET THEM-------------------------------------------------------*/
        if(sizex == 0 || sizey == 0) {
            int w = _ll_container.getWidth( );
            int h = _ll_container.getHeight();

            /*--WIDTH-----------------------------------------------------------------------------*/
            int nspaces = oSize + 4;
            int nblocks = oSize + 3;
            int space_left_x = w % nspaces;
            sizex = w / nblocks;
            margx = space_left_x / nspaces;

            /*--MARGIN FOR Y----------------------------------------------------------------------*/
            sizey = h / nblocks;

            /*--SAVE THE DATA ACQUIRED------------------------------------------------------------*/
            GM.writeConf(_a, GM.C_SIZESX[oSize], sizex);
            GM.writeConf(_a, GM.C_SIZESY[oSize], sizey);
            GM.writeConf(_a, GM.C_BMARGINX[oSize], margx);
        }
        else {

            /*--We got the sizes!-----------------------------------------------------------------*/
            if (oResize) {

                /*--Draw the BLOCKS---------------------------------------------------------------*/
                int[] bleft = {R.id.b_tap1, R.id.b_tap7, R.id.b_tap13, R.id.b_tap19, R.id.b_tap25, R.id.b_tap31};
                for (Button a_b_use : _b_use) {
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) a_b_use.getLayoutParams();
                    params.width = sizex;
                    params.height = sizey;
                    params.rightMargin = margx;

                    /*--Add an extra margin to the blocks on left---------------------------------*/
                    for (int aBleft : bleft)
                        if (a_b_use.getId() == aBleft) {
                            params.leftMargin = margx;
                            break;
                        }

                    /*--Set it!-------------------------------------------------------------------*/
                    a_b_use.setLayoutParams(params);
                }
            }
        }
    }

    /*
     *Summary:      Sets which color and which button is the correct one, then it creates the rest
     *              of the block at random.
     */
    public void setCorrectColor(){

        /*--IF WE ARE PLAYING SINGLE COLOR--------------------------------------------------------*/
        Random r = new Random();
        if(_g_gamemode == GM.GM_CMIN || _g_gamemode == GM.GM_CTAP) {
            _g_correct_color  = GM.readGStat(_a, GM.GS_CCOLOR_ID);
            int b_correct_pos = r.nextInt( _b_use.length - 1    );
            _g_correct_id = _b_use[b_correct_pos].getId();

			/*--WE HAVE CORRECT COLORS, NOW LETS MAKE INCORRECT ONES------------------------------*/
            setRandomBlocks();

			/*--FILL THE CORRECT BLOCK------------------------------------------------------------*/
            _b_use[ b_correct_pos ].setBackgroundResource(_g_correct_color);
        } else {

            /*--IF WE ARE NOT PLAYING IN BIND COLOR-----------------------------------------------*/
            int b_correct_pos = r.nextInt(_b_use.length - 1);
            int c_correct_pos = r.nextInt(GM.COLORS.length - 1);
            _g_correct_color = GM.COLORS[c_correct_pos];
            _g_correct_id = _b_use[b_correct_pos].getId();

			/*--FILL BLOCKS WITH RANDOM COLORS----------------------------------------------------*/
            setRandomBlocks();

			/*--FILL THE CORRECT BLOCK------------------------------------------------------------*/
            _b_use[b_correct_pos].setBackgroundResource(_g_correct_color);
        }

    }

    /*
     *Summary:      Draws blacks at random with random values. It makes sure none of them are the
     *              same as the correct color.
     */

    public void setRandomBlocks(){

        Random r = new Random();
        int c_pos = 0;
        for(int i = 0; i < GM.COLORS.length; i++)
            if(_g_correct_color == GM.COLORS[i]) {
                c_pos = i;               //We have which color is the correct, now we can ignore it.
                break;
            }

        /*--Random blocks time--------------------------------------------------------------------*/
        /*-IF PLAYING IN SINGLE COLOR-------------------------------------------------------------*/
        if(_g_gamemode == GM.GM_CMIN || _g_gamemode == GM.GM_CTAP){
            Boolean use_random = true;
            int v = r.nextInt(9), i = 0;
            switch(_g_difficulty){
                case GM.DIF_EZ:

                /*--40% RANDOM COLOR, 60% SOFT COLOR----------------------------------------------*/
                    use_random = v > 5;
                    break;
                case GM.DIF_MED:

                /*--70% RANDOM COLOR, 30% SOFT COLOR----------------------------------------------*/
                    use_random = v > 2;
                    break;
                case GM.DIF_HARD:

                /*--90% RANDOM COLOR, 10% SOFT COLOR----------------------------------------------*/
                    use_random = v > 0;
                    break;
                case GM.DIF_EX:

                /*--100% RANDOM COLOR, 0% SOFT COLOR----------------------------------------------*/
                    use_random = true;
                    break;
            }

            /*--DO ACTION ACCORDING TO use_random-------------------------------------------------*/
            if(use_random){
                while (i <= _b_use.length - 1) {
                    int c = r.nextInt(GM.COLORS.length - 1);
                    if (GM.COLORS[c] != _g_correct_color)
                        _b_use[i++].setBackgroundResource(GM.COLORS[c]);
                }
            } else {

                /*--Soft Colors-------------------------------------------------------------------*/
                while (i <= _b_use.length - 1)
                    _b_use[i++].setBackgroundResource(GM.COLORS_S[c_pos]);
            }
        } else {

            /*--NOT SINGLE COLOR, RANDOM COLORS COMPLETELY----------------------------------------*/
            int i = 0;
            while (i <= _b_use.length - 1) {
                int c = r.nextInt(GM.COLORS.length - 1);
                if (c != c_pos)
                    _b_use[i++].setBackgroundResource(GM.COLORS[c]);
            }
        }

    }

}
