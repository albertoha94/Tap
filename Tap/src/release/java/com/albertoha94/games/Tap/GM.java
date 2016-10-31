package com.albertoha94.games.Tap;

import java.io.InputStream;
import android.app.Activity;
import java.io.BufferedReader;
import android.content.Context;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import android.content.SharedPreferences;

/*
 * Class: GM.java
 * Summary:         Used to save all the values and different methods that are used in the game.
 *                  Static variables are saved here and write/read methods too.
 * @author Albertoha94
 * @version 3
 * Changes------------------------------------------------------------------------------------------
 * 16/Mar/16:                   -Methods showPauseDialog and showEndConfirmDialog removed from this class and moved to class
 *                              A_Game.java,
 * 18/Mar/16:                   -Removed logs.
 * 19/Mar/16:                   -Added a new array of the mipmap icons.
 */
public class GM {

	/*--VARIABLES---------------------------------------------------------------------------------*/

	/*--COLOR BIND ATTRIBUTE, GAME MODE-----------------------------------------------------------*/

	static final int GM_MIN = 0, GM_100T = 1, GM_CMIN = 2, GM_CTAP = 3;

	/*--DIFFICULTIES------------------------------------------------------------------------------*/

	static final int DIF_EZ = 0, DIF_MED = 1, DIF_HARD = 2, DIF_EX = 3;

	/*--COLOR PALETTE-----------------------------------------------------------------------------*/
	static final int[ ] COLORS = { R.drawable.b_auburn, R.drawable.b_air_force_blue,
            R.drawable.b_amber, R.drawable.b_amethyst, R.drawable.b_ao, R.drawable.b_turquoise,
            R.drawable.b_bronze, R.drawable.b_grey, R.drawable.b_cgreen, R.drawable.b_awesome };

    static final int[ ] COLORS_S = { R.drawable.b_auburn_s, R.drawable.b_air_force_blue_s,
            R.drawable.b_amber_s, R.drawable.b_amethyst_s, R.drawable.b_ao_s,
            R.drawable.b_turquoise_s, R.drawable.b_bronze_s, R.drawable.b_grey_s,
            R.drawable.b_cgreen_s, R.drawable.b_awesome_s };

    /*--SIZE IDENTIFIERS--------------------------------------------------------------------------*/
    static final int S_3 = 0, S_4 = 1, S_5 = 2, S_6 = 3;

	/*--SIZE 3X3----------------------------------------------------------------------------------*/
	static final int[] S_3X3 = { R.id.b_tap1, R.id.b_tap2, R.id.b_tap3, R.id.b_tap7,
            R.id.b_tap8, R.id.b_tap9, R.id.b_tap13, R.id.b_tap14, R.id.b_tap15};

	static final int[] S_3X3_EMPTY = { R.id.b_tap4, R.id.b_tap5, R.id.b_tap6,
            R.id.b_tap10, R.id.b_tap11, R.id.b_tap12, R.id.b_tap16, R.id.b_tap17,
            R.id.b_tap18, R.id.b_tap19, R.id.b_tap20, R.id.b_tap21, R.id.b_tap22,
            R.id.b_tap23, R.id.b_tap24, R.id.b_tap25, R.id.b_tap26, R.id.b_tap27,
            R.id.b_tap28, R.id.b_tap29, R.id.b_tap30, R.id.b_tap31, R.id.b_tap32,
            R.id.b_tap33, R.id.b_tap34, R.id.b_tap35, R.id.b_tap36};

	/*--SIZE 4X4----------------------------------------------------------------------------------*/
	static final int[] S_4X4 = { R.id.b_tap1, R.id.b_tap2, R.id.b_tap3, R.id.b_tap4,
            R.id.b_tap7, R.id.b_tap8, R.id.b_tap9, R.id.b_tap10, R.id.b_tap13,
            R.id.b_tap14, R.id.b_tap15, R.id.b_tap16, R.id.b_tap19, R.id.b_tap20,
            R.id.b_tap21, R.id.b_tap22};

	static final int[] S_4X4_EMPTY = { R.id.b_tap5, R.id.b_tap6, R.id.b_tap11,
            R.id.b_tap12, R.id.b_tap17, R.id.b_tap18, R.id.b_tap23, R.id.b_tap24,
            R.id.b_tap25, R.id.b_tap26, R.id.b_tap27, R.id.b_tap28, R.id.b_tap29,
            R.id.b_tap30, R.id.b_tap31, R.id.b_tap32, R.id.b_tap33, R.id.b_tap34,
            R.id.b_tap35, R.id.b_tap36};

	/*--SIZE 5X5----------------------------------------------------------------------------------*/
	static final int[] S_5X5 = { R.id.b_tap1, R.id.b_tap2, R.id.b_tap3, R.id.b_tap4,
            R.id.b_tap5, R.id.b_tap7, R.id.b_tap8, R.id.b_tap9, R.id.b_tap10,
            R.id.b_tap11, R.id.b_tap13, R.id.b_tap14, R.id.b_tap15, R.id.b_tap16,
            R.id.b_tap17, R.id.b_tap19, R.id.b_tap20, R.id.b_tap21, R.id.b_tap22,
            R.id.b_tap23, R.id.b_tap25, R.id.b_tap26, R.id.b_tap27, R.id.b_tap28,
            R.id.b_tap29};

	static final int[] S_5X5_EMPTY = { R.id.b_tap6, R.id.b_tap12, R.id.b_tap18,
            R.id.b_tap24, R.id.b_tap30, R.id.b_tap31, R.id.b_tap32, R.id.b_tap33,
            R.id.b_tap34, R.id.b_tap35, R.id.b_tap36};

	/*--SIZE 6X6----------------------------------------------------------------------------------*/
	static final int[] S_6X6 = { R.id.b_tap1, R.id.b_tap2, R.id.b_tap3, R.id.b_tap4,
            R.id.b_tap5, R.id.b_tap6, R.id.b_tap7, R.id.b_tap8, R.id.b_tap9,
            R.id.b_tap10, R.id.b_tap11, R.id.b_tap12, R.id.b_tap13, R.id.b_tap14,
            R.id.b_tap15, R.id.b_tap16, R.id.b_tap17, R.id.b_tap18, R.id.b_tap19,
            R.id.b_tap20, R.id.b_tap21, R.id.b_tap22, R.id.b_tap23, R.id.b_tap24,
            R.id.b_tap25, R.id.b_tap26, R.id.b_tap27, R.id.b_tap28, R.id.b_tap29,
            R.id.b_tap30, R.id.b_tap31, R.id.b_tap32, R.id.b_tap33, R.id.b_tap34,
            R.id.b_tap35, R.id.b_tap36};

    /*--Achievements icons------------------------------------------------------------------------*/
    public static final int[] ICONS = {
            R.mipmap.a_tap1, R.mipmap.a_tap2, R.mipmap.a_tap3, R.mipmap.a_tap4,
            R.mipmap.a_red1, R.mipmap.a_red2, R.mipmap.a_red3,
            R.mipmap.a_blue1, R.mipmap.a_blue2, R.mipmap.a_blue3,
            R.mipmap.a_amber1, R.mipmap.a_amber2, R.mipmap.a_amber3,
            R.mipmap.a_amethyst1, R.mipmap.a_amethyst2, R.mipmap.a_amethyst3,
            R.mipmap.a_ao1, R.mipmap.a_ao2, R.mipmap.a_ao3,
            R.mipmap.a_cyan1, R.mipmap.a_cyan2, R.mipmap.a_cyan3,
            R.mipmap.a_bronze1, R.mipmap.a_bronze2, R.mipmap.a_bronze3,
            R.mipmap.a_grey1, R.mipmap.a_grey2, R.mipmap.a_grey3,
            R.mipmap.a_cadnium1, R.mipmap.a_cadnium2, R.mipmap.a_cadnium3,
            R.mipmap.a_pink1, R.mipmap.a_pink2, R.mipmap.a_pink3 };

    public static final int ICON_DEFAULT = R.mipmap.a_default;

	/*--KEYS--------------------------------------------------------------------------------------*/

    /*--CONFIGURATION-----------------------------------------------------------------------------*/

	public static final String C_CONFIGURATION = "CONFIGURATION",
            C_WIDTH  = "C_WIDTH", C_HEIGHT = "C_HEIGHT",
            C_BISIZE = "C_BISIZE";

    public static final String[] C_SIZESX = { "C_B3SIZE"  , "C_B4SIZE" , "C_B5SIZE" , "C_B6SIZE"  };
    public static final String[] C_SIZESY = { "C_M3SIZE"  , "C_M4SIZE" , "C_M5SIZE" , "C_M6SIZE"  };
    public static final String[] C_BMARGINX = { "C_MX3SIZE" , "C_MX4SIZE", "C_MX5SIZE", "C_MX6SIZE" };

    /*--CURRENT GAME STATS------------------------------------------------------------------------*/

    public static final String GS_STATS   = "GS_STATS"  , GS_HITS    = "GS_HITS"   ,
                               GS_TOTAL   = "GS_TOTAL"  , GS_STREAK  = "GS_STREAK" ,
                               GS_COMBO   = "GS_COMBO"  , GS_TIME    = "GS_TIME"   ,
                               GS_LSTREAK = "GS_LSTREAK",
                               GS_SCORE   = "SCORE"     ,
                               GS_CCOLOR_ID = "COLORID" , GS_DIF     = "DIFFICULTY",
			 				   GS_MODE      = "GAMEMODE";

	public static final String[] GS_COLORS = {"GS_COLOR1", "GS_COLOR2", "GS_COLOR3", "GS_COLOR4",
			"GS_COLOR5", "GS_COLOR6", "GS_COLOR7", "GS_COLOR8", "GS_COLOR9", "GS_COLOR10"};


    /*--OVERALL GAME DATA-------------------------------------------------------------------------*/

    public static final String GD_TOTAL_GAMES = "GD_TOTAL_GAMES",  GD_DATA       = "GD_DATA"    ,
							   GD_PERFECT_G   = "PERFECT GAMES" , GD_LONGSTREAK = "LONG_STREAK",
                               GD_MING        = "MINUTEGAMES"   , GD_TAPGAMES   = "TAPGAMES"   ,
                               GD_CMING       = "CMINUTEGAMES"  , GD_CTAPG      = "CTAPGAMES"  ,
                               GD_TOTALBLOCK  = "TOTALBLOCKS"   ;

	public static final String[] GD_COLORS = {"GD_COLOR1", "GD_COLOR2", "GD_COLOR3", "GD_COLOR4",
			"GD_COLOR5", "GD_COLOR6", "GD_COLOR7", "GD_COLOR8", "GD_COLOR9", "GD_COLOR10"};

	/*--METHODS-----------------------------------------------------------------------------------*/

	/*--LEADERBOARDS1------------------------------------------------------------------------------*/

	public static Row[] readLeaders(Context OCon, String OFile){

		Row[] r = new Row[5];
		try {

			InputStream inputStream = OCon.openFileInput(OFile);

            /*--LOG-------------------------------------------------------------------------------*/


			if (inputStream != null) {

				InputStreamReader inputStreamReader = new InputStreamReader(inputStream   );
				BufferedReader    bufferedReader    = new BufferedReader(inputStreamReader);
				String receiveString;
				StringBuilder stringBuilder = new StringBuilder();

				while ((receiveString = bufferedReader.readLine()) != null) {

					stringBuilder.append(receiveString);

				}

				inputStream.close();

				/*--FILL THE ROWS-----------------------------------------------------------------*/

				String[] d = stringBuilder.toString().split("@");
				for(int i = 0; i < r.length; i++) {

                    r[i] = new Row(d[i]);

                    /*--LOG-----------------------------------------------------------------------*/


                }

                /*--LOG---------------------------------------------------------------------------*/


			}

		} catch (Exception e) {

			e.printStackTrace();
            for(int i = 0; i < r.length; i++)
                r[i] = new Row();

            /*--LOG-------------------------------------------------------------------------------*/


		}

		return r;

	}

	public static void writeLeaders(Context OCon, String OFile, Row[] ORows){

		try {

			FileOutputStream   fileout = OCon.openFileOutput(OFile, OCon.MODE_PRIVATE);
            OutputStreamWriter outputWriter = new OutputStreamWriter(fileout              );
            int i = 0;

            /*--LOG-------------------------------------------------------------------------------*/


            /*------------------------------------------------------------------------------------*/

			while(i < ORows.length) {

				outputWriter.write(ORows[i].toString());

                /*--LOG---------------------------------------------------------------------------*/


                /*--------------------------------------------------------------------------------*/

				i++;

            }

			outputWriter.close();

            /*--LOG-------------------------------------------------------------------------------*/


		} catch (Exception e) {

			e.printStackTrace();

		}

	}

    public static void insertRow(Context OCon, String OFile, Row ORow) {


        /*--LOG-----------------------------------------------------------------------------------*/


        /*----------------------------------------------------------------------------------------*/

        Row[] data   = GM.readLeaders(OCon, OFile             );
        int gamemode = GM.readGStat((Activity)OCon, GM.GS_MODE);


        /*--IF GAME MODE IS 1 MINUTE--------------------------------------------------------------*/

        if(gamemode == GM_MIN || gamemode == GM_CMIN) {

            /*--COMPARE UNTIL YOU FIND A PLACE----------------------------------------------------*/

            for (int i = 0; i < data.length; i++)
                if (ORow.get_score() > data[i].get_score()) {

                    /*--MOVE BY 1 EVERYTHING------------------------------------------------------*/

                    System.arraycopy(data, i, data, i + 1, data.length - 1 - i);

                    /*--ADD THE NEW SCORE---------------------------------------------------------*/

                    data[i] = ORow;

                    /*--LOG-----------------------------------------------------------------------*/


                    /*----------------------------------------------------------------------------*/

                    break;

                }

        }

        /*--GAME MODE IS TAP----------------------------------------------------------------------*/

        else {

            for (int i = 0; i < data.length; i++)
                if (ORow.get_score() < data[i].get_score() || data[i].get_score() == Row.EMPTY) {

                    /*--MOVE BY 1 EVERYTHING------------------------------------------------------*/

                    System.arraycopy(data, i, data, i + 1, data.length - 1 - i);

                    /*--ADD THE NEW SCORE---------------------------------------------------------*/

                    data[i] = ORow;

                    /*--LOG-----------------------------------------------------------------------*/


                    /*----------------------------------------------------------------------------*/

                    break;

                }

        }

        writeLeaders(OCon, OFile, data);

        /*--LOG-----------------------------------------------------------------------------------*/


        /*----------------------------------------------------------------------------------------*/

    }

	/*--CONFIGURATION-----------------------------------------------------------------------------*/

	public static boolean writeConf( Activity OActivity, String OKey, int OValue ) {

		boolean Result;
		try {

			SharedPreferences SharedPref = OActivity.getSharedPreferences(C_CONFIGURATION,
                    Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = SharedPref.edit( );
			editor.putInt(OKey, OValue);
			editor.apply();
			Result = true;

		} catch ( Exception e ) {

			Result = false;

		}
		return Result;
	}

	public static int readConf( Activity OActivity, String OKey ) {

		SharedPreferences SharedPref = OActivity.getSharedPreferences( C_CONFIGURATION,
				Context.MODE_PRIVATE );
        return SharedPref.getInt(OKey, 0);

	}

	/*--GAME STAT---------------------------------------------------------------------------------*/

    public static boolean writeGStat(Activity OActivity, String OKey, int OValue) {

        boolean Result;
        try {

            SharedPreferences SharedPref = OActivity.getSharedPreferences(GS_STATS, Context.MODE_PRIVATE );
            SharedPreferences.Editor editor = SharedPref.edit( );
            editor.putInt(OKey, OValue);
            editor.apply( );
            Result = true;

        } catch ( Exception e ) {

            Result = false;

        }
        return Result;
    }

    public static int readGStat(Activity OActivity, String OKey) {

        SharedPreferences SharedPref = OActivity.getSharedPreferences(GS_STATS, Context.MODE_PRIVATE );
        return SharedPref.getInt(OKey, 0);

    }

	/*--OVERALL GAME DATA ACHIEVEMENT-------------------------------------------------------------*/

    public static void writeAchievementData(Activity OActivity, String OKey, boolean OValue) {

        try {

            SharedPreferences SharedPref = OActivity.getSharedPreferences(GD_DATA, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = SharedPref.edit();
            editor.putBoolean(OKey, OValue);
            editor.apply();

        } catch(Exception ignored) { }

    }

    public static boolean readAchievementData(Activity OActivity, String OKey) {

        SharedPreferences SharedPref = OActivity.getSharedPreferences(GD_DATA, Context.MODE_PRIVATE);
        return SharedPref.getBoolean(OKey, false);

    }

    /*--OVERALL GAME DATA-------------------------------------------------------------------------*/

    public static void writeGData(Activity OActivity, String OKey, int OValue) {

        try {

            SharedPreferences SharedPref = OActivity.getSharedPreferences(GD_DATA, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = SharedPref.edit();
            editor.putInt(OKey, OValue);
            editor.apply();

        } catch(Exception ignored) { }

    }

    public static int readGData(Activity OActivity, String OKey) {

        SharedPreferences SharedPref = OActivity.getSharedPreferences(GD_DATA, Context.MODE_PRIVATE);
        return SharedPref.getInt(OKey, 0);

    }

}
