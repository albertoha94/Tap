package com.albertoha94.games.Tap;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

/**
 * Class: EndGameDialog.java
 * Summary:         This class is used to show the game of the app. In here, the blocks are resized,
 *                  drawn, switched and animated. It's the only class that is used as a "game screen"
 *                  and it can work in both game modes, but only 1 at the time.
 * @author Albertoha94
 * @version 2
 * Changes------------------------------------------------------------------------------------------
 * 16/Mar/16:                   -Modified the declare method so it styles the dialog.
 * 19/Mar/16:                   -Added the interstitial ads.
 */
public class EndGameDialog extends AlertDialog.Builder {

    /*--VARIABLES---------------------------------------------------------------------------------*/

    public static final int TITLE = 0, MESSAGE = 1, COMPLETION = 2;
    Context  _c   ;
    Activity _a   ;
    String[] _text;

    private InterstitialAd _ad;                                          //Ad used in this activity.

    /*--METHODS-----------------------------------------------------------------------------------*/
    public EndGameDialog(Context context, String[] OText, Activity OAct, InterstitialAd OAd) {

        super(context, R.style.apptheme_Dialog) ;
        _c    = context;
        _a    = OAct   ;
        _text = OText  ;
        _ad   = OAd    ;
        declare();

    }

    public void declare() {

        setCancelable(false);
        Typeface font = Typeface.createFromAsset(_c.getAssets(), "gamefont.otf");

        /*--Custom title--------------------------------------------------------------------------*/
        TextView t = new TextView(_c);
        t.setTypeface(font);
        t.setText(_text[TITLE]);
        t.setGravity(Gravity.CENTER);
        t.setTextSize(_c.getResources().getDimension(R.dimen.tv_title_dif_tvsize));
        setCustomTitle(t);
        String d = _c.getResources().getString(R.string.nohits);
        setMessage(_text[MESSAGE] + "\n" + d + " " + _text[COMPLETION]);
        LayoutInflater inflater = LayoutInflater.from(_c        );
        View View = inflater.inflate(R.layout.dialog_share, null);
        //setView(View);

        /*--FACEBOOK CONTENT----------------------------------------------------------------------*/

        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.albertoha94.games.Tap"))
                .build();

        /*--Admob ad------------------------------------------------------------------------------*/
        _ad.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                restartGame();
            }
        });

        /*--LAYOUT ITEMS--------------------------------------------------------------------------*/
        ShareButton sb_facebook = (ShareButton)View.findViewById(R.id.b_face);
        sb_facebook.setShareContent(content);
        Button btn_twitter = (Button)View.findViewById(R.id.b_twitter);

        btn_twitter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                /*--SESSION FOR TWITTER-----------------------------------------------------------*/

                final TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();

                /*--PLACEHOLDER-------------------------------------------------------------------*/

                TweetComposer.Builder builder = new TweetComposer.Builder(_c)
                        .text("COLOR BLOCK TAP ALPHA")
                        .image(Uri.parse("android.resource://com.albertoha94.games.Tap/" + R.drawable.appcard));
                builder.show();

                /*--APPCARD-----------------------------------------------------------------------*/

//                final Card card = new Card.AppCardBuilder(_a).imageUri(
//                        Uri.parse("android.resource://com.albertoha94.games.Tap/" + R.drawable.appcard))
//                        .googlePlayId("")
//                        .build();

//                final Intent intent = new ComposerActivity.Builder(_a)
//                        .session(session)
//                        //.card(card)
//                        .createIntent();
//                _a.startActivity(intent);

            }

        });

        String Pos = _c.getResources().getString(R.string.play_again);
        String Neg = _c.getResources().getString(R.string.return_menu);
        final Activity a = (Activity)_c;

        /*--BUTTONS-------------------------------------------------------------------------------*/
        setNegativeButton(Neg, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                showInterstitial(false);
            }
        });
        setPositiveButton(Pos, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                showInterstitial(true);
            }
        });

    }

    /**
     * Summary:             Destroys the current game activity and creates a new one.
     */
    public void restartGame(){

        Intent x = new Intent(_a, A_Game.class);
        _a.finish();
        _a.startActivity(x);

    }

    /**
     * Summary:             Loads the ad requested.
     */
    private void showInterstitial(boolean ORestart) {

        if (_ad != null && _ad.isLoaded())
            _ad.show();
         else
            if(ORestart)                                                 //We couldnt load the ad :(
                restartGame();
            else {
                Intent x = new Intent(_a, A_Menu.class);
                _a.startActivity(x);
                _a.finish();
            }

    }

}
