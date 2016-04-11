package com.aptoide.amethyst;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.AptoideBaseActivity;
import com.aptoide.amethyst.R;

/**
 * Created by fabio on 14-10-2015.
 */
public class TimeLineNoFriendsInviteActivity extends AptoideBaseActivity {
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Aptoide.getThemePicker().setAptoideTheme(this);
        super.onCreate(savedInstanceState);

        setContentView(getContentView());
        bindViews();

        mToolbar.setCollapsible(false);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.invite_friends));


    }

    protected int getContentView() {
        return R.layout.page_timeline_no_activity;
    }

    protected void bindViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    public static void sendMail(Context c){
//        FlurryAgent.logEvent("Social_Timeline_Clicked_On_Invite_Friends_By_Email");

        String subject = c.getString(R.string.aptoide_timeline);
        String html =
                "   <p><strong>%s</strong></p>\n" +
                        "   <p>%s</p>\n" +
                        "   <p>%s</p>\n" +
                        "   <p>%s</p>\n" +
                        "   <p>%s</p>\n" +
                        "   <p>%s<a href=\"http://m.aptoide.com/install\">%s</a></p>\n" +
                        "   <p>%s</p>\n";

        String username = PreferenceManager.getDefaultSharedPreferences(Aptoide.getContext()).getString("username", null);
        String Invitation= c.getString(R.string.timeline_email_invitation);
        String whatIs= c.getString(R.string.whats_timeline);
        String TOS= c.getString(R.string.facebook_tos).replace("\n\n","<br>");

        String howTo= c.getString(R.string.timeline_email_how_to_join);
        String step1= c.getString(R.string.timeline_email_step1);
        String step2= c.getString(R.string.timeline_email_step2);
        String install= c.getString(R.string.install) + " Aptoide";

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/html");

        emailIntent.putExtra(Intent.EXTRA_SUBJECT,subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT,
                Html.fromHtml(String.format(html, username, Invitation, whatIs, TOS, howTo, step1, install, step2)));
        try {
            c.startActivity(emailIntent);

        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(c, R.string.feedback_no_email, Toast.LENGTH_LONG).show();
        }
    }

    public void SendMail(View view) {
        sendMail(this);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == android.R.id.home || i == R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        FlurryAgent.onStartSession(this, getResources().getString(R.string.FLURRY_KEY));
    }

    @Override
    protected void onStop() {
        super.onStop();
//        FlurryAgent.onEndSession(this);
    }

    @Override
    protected String getScreenName() {
        return "Timeline No Friends Invite";
    }}

