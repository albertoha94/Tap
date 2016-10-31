package com.albertoha94.games.Tap;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.os.Handler;
import android.app.Activity;
import android.widget.Button;
import android.content.Intent;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.content.DialogInterface;
import android.view.animation.Animation;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.animation.AnimationUtils;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import java.util.Random;

import io.fabric.sdk.android.Fabric;

/**
 * Class: A_Menu.java
 * Summary:         This class is used to show the menu of the app. In here the user can access all
 *                  the game modes by clicking on them plus some option like about, statistics,
 *                  achievements and leaderboards. The last three sent you to a different activity
 *                  while the game buttons trigger dialogs to configure the game mode before it
 *                  redirects you to the game activity.
 * @author Albertoha94
 * @version 2
 * Changes------------------------------------------------------------------------------------------
 * 16/Mar/16:                   -Added the method createShowInfoDialog for the button ib_info.
 *                              -Modified the method createDlgDif to have a style.
 *                              -Modified the method createDlgMode to have a style.
 *                              -Modified the method createShowInfoDialog to have a style.
 * 18/Mar/16:                   -Edited updateAchievements so it shows a different string with
 *                              different length.
 *                              -Removed logs.
 */
public class A_Menu extends AppCompatActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "Squ3SQjLD4Cayns8NFJyljLZu";
    private static final String TWITTER_SECRET = "3BneNXvJeiX17HN4NMIR25kt979OISvr8R9axkpsEu9GjYaZ13";

    /*--VARIABLES---------------------------------------------------------------------------------*/

	private Activity _a;
    private Animation[] _animation;
    private Button _b_min, _b_tap, _b_single;
    private Button[] _b;
	private Context  _c;
//	private AdView _AdMenu;
//	private AdRequest _AdRequest;
	private Handler _h_dims,_h_main;
    private int _i;
    private Typeface _gamefont;
//	private MediaPlayer _Snd_Click;

    /*--RUNNABLES---------------------------------------------------------------------------------*/

    private  Runnable _r_getdims = new Runnable() {
        @Override
        public void run() {

            /*--CHECK IF THEYRE NOT EMPTY---------------------------------------------------------*/
            int dw = GM.readConf(_a, GM.C_WIDTH );
            int dh = GM.readConf(_a, GM.C_HEIGHT);
            if(dw == 0 || dh == 0) {

                /*--SIZE IS NOT OBTAINED YET------------------------------------------------------*/


                /*--------------------------------------------------------------------------------*/

                DisplayMetrics display = getResources().getDisplayMetrics();
                int w = display.widthPixels ;
                int h = display.heightPixels;
                if (w != 0 && h != 0) {

                    GM.writeConf(_a, GM.C_WIDTH, w );
                    GM.writeConf(_a, GM.C_HEIGHT, h);
                    _h_dims.sendEmptyMessage(0);

                } else {

                    _h_dims.post(_r_getdims);


                }
            }
            else {

                _h_dims.sendEmptyMessage(0);

                /*--SIZE IS ALREADY OBTAINED------------------------------------------------------*/


                /*--------------------------------------------------------------------------------*/

            }

        }
    };

    private Runnable _r_animate = new Runnable() {

        @Override
        public void run() {

            if(_i < _b.length) {

                _b[_i].startAnimation(_animation[_i]);
                _i++;
                Random r = new Random();
                int wait = (r.nextInt(5) + 1) * 1000;
                _h_main.postDelayed(_r_animate, wait);
            }
        }

    };

    /*--METHODS-----------------------------------------------------------------------------------*/

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

        /*--TWITTER INITIALIZE--------------------------------------------------------------------*/

		TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
		Fabric.with(this, new Twitter(authConfig));

        /*----------------------------------------------------------------------------------------*/

		setContentView(R.layout.activity_mainmenu);
		declare();
        setListeners();

	}

    @Override
    protected void onPause() {

        super.onPause();

        /*--FACEBOOK------------------------------------------------------------------------------*/

        AppEventsLogger.deactivateApp(this);

        /*----------------------------------------------------------------------------------------*/

    }

    @Override
    protected void onStop() {

        super.onStop();
        clearAnimations();

    }

    @Override
    protected void onResume() {

        super.onResume();

        /*--FACEBOOK------------------------------------------------------------------------------*/

        AppEventsLogger.activateApp(this);

        /*--CALL HANDLER--------------------------------------------------------------------------*/

        _h_dims.post(_r_getdims);

        /*-ACHIEVEMENTS---------------------------------------------------------------------------*/

        updateAchievements();

    }

    //	public void createAd() {
//		_AdMenu    = (AdView)findViewById(R.id.adMenu);
//		_AdRequest = new AdRequest.Builder().build();
//		_AdMenu.loadAd(_AdRequest);
//	}

    public void declare() {

		_a = A_Menu.this;
		_c = A_Menu.this;

        /*--HANDLER-------------------------------------------------------------------------------*/

        _h_main  = new Handler();
        _h_dims = new Handler() {

            public void handleMessage(android.os.Message msg) {

                switch (msg.what) {

                    case 0:

                        /*--ANIMATON STARTS UNTIL WE GET DIMENSIONS-------------------------------*/

                        setButtonsX();
                        setButtonsY();
                        _h_main.postDelayed(_r_animate, 1000);
                        break;

                }

            }

        };

        /*-ACTIVITY ITEMS-------------------------------------------------------------------------*/

        _b_min = (Button)findViewById(R.id.b_min );
        _b_tap = (Button)findViewById(R.id.b_100t);
        _b_single = (Button)findViewById(R.id.b_single);
        TextView _t_title = (TextView) findViewById(R.id.tv_menutitle);

        /*--DECORATIVE ITEMS----------------------------------------------------------------------*/

        _b = new Button[10];
        _b[0] = (Button) findViewById(R.id.b_animation1);
        _b[1] = (Button) findViewById(R.id.b_animation2);
        _b[2] = (Button) findViewById(R.id.b_animation3);
        _b[3] = (Button) findViewById(R.id.b_animation4);
        _b[4] = (Button) findViewById(R.id.b_animation5);
        _b[5] = (Button) findViewById(R.id.b_animation6);
        _b[6] = (Button) findViewById(R.id.b_animation7);
        _b[7] = (Button) findViewById(R.id.b_animation8);
        _b[8] = (Button) findViewById(R.id.b_animation9);
        _b[9] = (Button) findViewById(R.id.b_animation10);

        for(int i = 0; i < GM.COLORS.length; i++)
            _b[i].setBackgroundResource(GM.COLORS[i]);

        /*--ANIMATIONS----------------------------------------------------------------------------*/

        _animation = new Animation[10];

        for(int i = 0; i < _animation.length; i++)
            _animation[i] = AnimationUtils.loadAnimation(_c, R.anim.btn_movement);

		/*--HIDING THE TOOLBAR--------------------------------------------------------------------*/

		ActionBar ActBar = getSupportActionBar();
		assert ActBar != null;
		ActBar.hide();

        /*--USE THE TYPEFACE ON BUTTONS AND TITLE-------------------------------------------------*/
        _gamefont = Typeface.createFromAsset(getAssets(), "gamefont.otf");
        _b_single.setTypeface(_gamefont);
        _t_title.setTypeface(_gamefont);
        _b_tap.setTypeface(_gamefont);
        _b_min.setTypeface(_gamefont);

        /*--CREATE ADS----------------------------------------------------------------------------*/

        //createAd();

        /*--INITIALIZE FACEBOOK SERVICE-----------------------------------------------------------*/

        FacebookSdk.sdkInitialize(getApplicationContext());

        /*--FACEBOOK HASH KEY---------------------------------------------------------------------*/

//        try {
//
//            PackageInfo info = getPackageManager().getPackageInfo("com.albertoha94.games.Tap",
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//
//            }
//
//        } catch (PackageManager.NameNotFoundException e) { }
//
//        catch (NoSuchAlgorithmException e) { }

	}

    public void setListeners(){

        /*--MINUTE--------------------------------------------------------------------------------*/

        _b_min.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // TODO Auto-generated method stub
                GM.writeGStat(_a, GM.GS_MODE, GM.GM_MIN);
                createDlgDif();

                /*--LOG---------------------------------------------------------------------------*/


				/*--------------------------------------------------------------------------------*/

            }

        });

        /*--TAP-----------------------------------------------------------------------------------*/

        _b_tap.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // TODO Auto-generated method stub
                GM.writeGStat(_a, GM.GS_MODE, GM.GM_100T);
                createDlgDif();

                /*--LOG---------------------------------------------------------------------------*/


				/*--------------------------------------------------------------------------------*/

            }

        });

        /*--SINGLE COLOR--------------------------------------------------------------------------*/

        _b_single.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // TODO Auto-generated method stub
                createDlgMode();

                /*--LOG---------------------------------------------------------------------------*/


				/*--------------------------------------------------------------------------------*/

            }

        });

        /*--STATS---------------------------------------------------------------------------------*/

        findViewById(R.id.ib_stats).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent d = new Intent(_a, A_Statistics.class);
                startActivity(d );
                clearAnimations();

				/*--LOG---------------------------------------------------------------------------*/


				/*--------------------------------------------------------------------------------*/

            }

        });

        /*--ACHIEVEMENTS--------------------------------------------------------------------------*/

        findViewById(R.id.ib_achievements).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent d = new Intent(_a, A_Achievements.class);
                startActivity(d );
                clearAnimations();

				/*--LOG---------------------------------------------------------------------------*/


				/*--------------------------------------------------------------------------------*/

            }

        });

        /*--LEADERBOARDS--------------------------------------------------------------------------*/

        findViewById(R.id.ib_leaderboards).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent d = new Intent(_a, A_Leaderboards.class);
                startActivity(d);
                clearAnimations();

				/*--LOG---------------------------------------------------------------------------*/


				/*--------------------------------------------------------------------------------*/

            }

        });

        /*--SETTINGS------------------------------------------------------------------------------*/

        findViewById(R.id.ib_info).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                createShowInfoDialog();
            }

        });

    }

    public void updateAchievements(){

        String[] value = new String[11];
        String[] names = getResources().getStringArray(R.array.achievement_name    );
        int   [] max   = getResources().getIntArray(R.array.achievement_values_max );
        String[] state = getResources().getStringArray(R.array.GD_ACHIEVEMENT_STATE);

        /*--FILL VALUES---------------------------------------------------------------------------*/

        value[0] = GM.GD_TOTAL_GAMES;
        System.arraycopy(GM.GD_COLORS, 0, value, 1, value.length - 1);

        /*----------------------------------------------------------------------------------------*/

        for(int i = 0; i < names.length; i++) {

            int current;
            if(i > 3)
                current = GM.readGData(_a, value[((i - 4) / 3) > 0 ? (i - 4) / 3 + 1:1]);
            else
                current = GM.readGData(_a,value[0]);

            /*--LOG-------------------------------------------------------------------------------*/


            /*------------------------------------------------------------------------------------*/

            if(current > max[i] && !GM.readAchievementData(_a, state[i])){

				/*--UNLOCK ACHIEVEMENT------------------------------------------------------------*/

                GM.writeAchievementData(_a, state[i], true);
                String achievement = getResources().getString(R.string.achievement);
                String unlock = getResources().getString(R.string.unlocked);
                Snackbar.make(findViewById(R.id.cl_menu), achievement + " '" + names[i] + "' " +
                                unlock, Snackbar.LENGTH_LONG).show();

            }

        }

    }


    /*
     * Summary:                 Creates an alertdialog that contains a list with the different
     *                          difficulties available for the game. This value is stored and
     *                          later used in the game.
     */
    public void createDlgDif() {

        try {

            final String[] dif_array = getResources().getStringArray(R.array.difficulty_s);
            String title  = getResources().getString(R.string.select_dif);
            String cancel = getResources().getString(android.R.string.cancel  );
            ArrayAdapter<String> adapter = new ArrayAdapter<>(_c, R.layout.row_dialogs, dif_array);
            AlertDialog.Builder dlg_dif = new AlertDialog.Builder(_c, R.style.apptheme_Dialog);

            TextView t = new TextView(_c);                                       //Set custom title.
            t.setText(title);
            t.setTypeface(_gamefont);
            t.setGravity(Gravity.CENTER);
            t.setTextSize(getResources().getDimension(R.dimen.tv_title_dif_tvsize));
            dlg_dif.setCustomTitle(t);

			/*--WHAT TO DO WHEN OPTION SELECTED---------------------------------------------------*/
            dlg_dif.setAdapter(adapter, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    clearAnimations();
                    GM.writeGStat(_a, GM.GS_DIF, which);
                    Intent in = new Intent(_a, A_Game.class);
                    startActivity(in);
                    finish();

                }

            });

			/*--CANCEL BUTTON---------------------------------------------------------------------*/
            dlg_dif.setNegativeButton(cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

			/*--SHOW DIALOG-----------------------------------------------------------------------*/
            dlg_dif.create().show();

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    public void createDlgMode() {

        try {

            final String[] mode_array = getResources().getStringArray(R.array.mode_s);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(_c, R.layout.row_dialogs, mode_array);
            final AlertDialog.Builder dlg_mode = new AlertDialog.Builder(_c, R.style.apptheme_Dialog);
            String cancel = getResources().getString( android.R.string.cancel);
            String title  = getResources().getString( R.string.select_mode	 );

            TextView t = new TextView(_c);                                       //Set custom title.
            t.setText(title);
            t.setTypeface(_gamefont);
            t.setGravity(Gravity.CENTER);
            t.setTextSize(getResources().getDimension(R.dimen.tv_title_dif_tvsize));
            dlg_mode.setCustomTitle(t);

			/*--WHAT TO DO WHEN OPTION SELECTED---------------------------------------------------*/

            dlg_mode.setAdapter(adapter, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    GM.writeGStat(_a, GM.GS_MODE, (which + 2));
                    createDlgColor();
                    dialog.dismiss();

                }

            });

			/*--CANCEL BUTTON---------------------------------------------------------------------*/

            dlg_mode.setNegativeButton(cancel, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) { }

            });

			/*--SHOW DIALOG-----------------------------------------------------------------------*/

            dlg_mode.create().show();

            /*--LOG-------------------------------------------------------------------------------*/


            /*------------------------------------------------------------------------------------*/

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    public void createDlgColor() {

        try {

            ColorDialog dlg_c = new ColorDialog(_c, _a);
            dlg_c.show();

            /*--LOG-------------------------------------------------------------------------------*/


            /*------------------------------------------------------------------------------------*/

        }

        catch (Exception e) {

            e.printStackTrace();

        }

    }

    /*
     * Summary:             Used to show a dialog that displays a text only. This text tells
     *                      everything about the developers of the app.
     */
    public void createShowInfoDialog(){

        AlertDialog.Builder dlg = new AlertDialog.Builder(this);
        TextView t = new TextView(_c);                                          //Set custom title.
        t.setText(getResources().getString(R.string.info_title));
        t.setTypeface(_gamefont);
        t.setGravity(Gravity.CENTER);
        t.setTextSize(getResources().getDimension(R.dimen.tv_title_dif_tvsize));
        dlg.setCustomTitle(t);
        dlg.setMessage(getResources().getString(R.string.info));

        dlg.setNegativeButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dlg.show();

    }

    public void clearAnimations(){

        _h_main.removeCallbacks(_r_animate);

        /*--LOG-----------------------------------------------------------------------------------*/


        /*----------------------------------------------------------------------------------------*/

    }

    public void setButtonsX(){

        Random r = new Random();

        /*--WIDTH SHOULDNT BE 0-------------------------------------------------------------------*/

        int w = GM.readConf(_a, GM.C_WIDTH) + 200;
        int extra = 0;


        for (Button a_b : _b) {

            if (r.nextBoolean())
                extra += r.nextInt(300);
            else
                extra -= r.nextInt(300);
            a_b.setX(r.nextInt(w) + extra);

        }

    }

    public void setButtonsY(){

        float size = getResources().getDimension(R.dimen.b_animation_size);
        for (Button a_b : _b)
            a_b.setY(0 - size);

    }

    /*
     * Class: ColorDialog
     * Summary:             This class is used for the custom dialog we show in Single color mode.
     *                      We save the value that the user selects for later use.
     */
    private class ColorDialog extends AlertDialog implements View.OnClickListener {

    /*--VARIABLES---------------------------------------------------------------------------------*/

        Button[]  _btns;
        Button    _exit;
        Context   _c   ;
        Activity  _a   ;

    /*--METHODS-----------------------------------------------------------------------------------*/

        public ColorDialog(Context context, Activity OAct) {

            super(context);
            _c = context;
            _a =    OAct;
            declare();

        }

        public void declare(){

            TextView t = new TextView(_c);                                       //Set custom title.
            t.setText(_c.getResources().getString(R.string.select_color));
            t.setTypeface(_gamefont);
            t.setGravity(Gravity.CENTER);
            t.setTextSize(getResources().getDimension(R.dimen.tv_title_dif_tvsize));
            setCustomTitle(t);

            setCancelable(false);
            LayoutInflater inflater = LayoutInflater.from(_c          );
            View View = inflater.inflate(R.layout.dialog_buttons, null);
            setView(View);

        /*--LAYOUT ITEMS--------------------------------------------------------------------------*/

            _btns    = new Button[10];
            _btns[0] = (Button)View.findViewById(R.id.b_c1  );
            _btns[1] = (Button)View.findViewById(R.id.b_c2  );
            _btns[2] = (Button)View.findViewById(R.id.b_c3  );
            _btns[3] = (Button)View.findViewById(R.id.b_c4  );
            _btns[4] = (Button)View.findViewById(R.id.b_c5  );
            _btns[5] = (Button)View.findViewById(R.id.b_c6  );
            _btns[6] = (Button)View.findViewById(R.id.b_c7  );
            _btns[7] = (Button)View.findViewById(R.id.b_c8  );
            _btns[8] = (Button)View.findViewById(R.id.b_c9  );
            _btns[9] = (Button)View.findViewById(R.id.b_c10 );
            _exit    = (Button)View.findViewById(R.id.b_exit);

        /*-- SETCOLORS----------------------------------------------------------------------------*/

            for(int i = 0; i < GM.COLORS.length; i++)
                _btns[i].setBackgroundResource(GM.COLORS[i]);

        /*----------------------------------------------------------------------------------------*/

            setListeners();

        }

        @Override
        public void onClick(View OView) {

            Button clicked = (Button)OView;
            int p = 0;
            switch (clicked.getId()){

                case R.id.b_c1 : p = 0; break;
                case R.id.b_c2 : p = 1; break;
                case R.id.b_c3 : p = 2; break;
                case R.id.b_c4 : p = 3; break;
                case R.id.b_c5 : p = 4; break;
                case R.id.b_c6 : p = 5; break;
                case R.id.b_c7 : p = 6; break;
                case R.id.b_c8 : p = 7; break;
                case R.id.b_c9 : p = 8; break;
                case R.id.b_c10: p = 9; break;

            }

            GM.writeGStat(_a, GM.GS_CCOLOR_ID, GM.COLORS[p]);
            createDlgDif();

        /*--LOG-----------------------------------------------------------------------------------*/


        /*----------------------------------------------------------------------------------------*/

            dismiss();

        }

        public void setListeners(){

            _btns[0].setOnClickListener(this);
            _btns[1].setOnClickListener(this);
            _btns[2].setOnClickListener(this);
            _btns[3].setOnClickListener(this);
            _btns[4].setOnClickListener(this);
            _btns[5].setOnClickListener(this);
            _btns[6].setOnClickListener(this);
            _btns[7].setOnClickListener(this);
            _btns[8].setOnClickListener(this);
            _btns[9].setOnClickListener(this);
            _exit   .setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                /*--LOG---------------------------------------------------------------------------*/


                /*--------------------------------------------------------------------------------*/

                    dismiss();

                }

            });

        }

    }

}
