package com.aptoide.amethyst;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aptoide.amethyst.preferences.SecurePreferences;
import com.aptoide.dataprovider.AptoideSpiceHttpService;
import com.aptoide.dataprovider.webservices.json.GenericResponseV2;
import com.bumptech.glide.Glide;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;

import java.util.List;

import com.aptoide.amethyst.adapter.timeline.TimeLineFriendsCheckableListAdapter;
import com.aptoide.amethyst.ui.widget.CircleTransform;
import com.aptoide.amethyst.webservices.timeline.ListUserFriendsRequest;
import com.aptoide.amethyst.webservices.timeline.RegisterUserFriendsInviteRequest;
import com.aptoide.amethyst.webservices.timeline.TimelineRequestListener;
import com.aptoide.amethyst.webservices.timeline.json.Friend;
import com.aptoide.amethyst.webservices.timeline.json.ListUserFriendsJson;

/**
 * Created by fabio on 14-10-2015.
 */
public class TimeLineFriendsInviteActivity extends AptoideBaseActivity {
    private TimeLineFriendsCheckableListAdapter adapter;
    private TextView friends_using_timeline;
    private TextView friends_to_invite;
    private LinearLayout friends_list;
    private ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        //Aptoide.getThemePicker().setAptoideTheme(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.page_timeline_invite_friends);
        listView = getListView();

        friends_list = (LinearLayout) findViewById(R.id.friends_list);
        friends_using_timeline = (TextView) findViewById(R.id.friends_using_timeline);
        friends_to_invite = (TextView) findViewById(R.id.friends_to_invite);

        View footer_friends_to_invite = LayoutInflater.from(this).inflate(R.layout.footer_invite_friends, null);
        listView.addFooterView(footer_friends_to_invite);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        rebuildList(savedInstanceState);
        final Button invite = (Button) footer_friends_to_invite.findViewById(R.id.timeline_invite);
        final Activity c = this;
        invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FlurryAgent.logEvent("Social_Timeline_Clicked_On_Invite_Friends");
                RegisterUserFriendsInviteRequest request = new RegisterUserFriendsInviteRequest();
                long[] ids = listView.getCheckItemIds();
                if(ids.length>0) {
                    for (long id : ids) {
                        request.addEmail(adapter.getItem((int) id).getEmail());
                    }
                    manager.execute(request, new TimelineRequestListener<GenericResponseV2>() {
                        @Override
                        protected void caseFAIL() {
                            c.findViewById(R.id.friends_to_invite_layout).setVisibility(View.VISIBLE);
                            c.findViewById(android.R.id.empty).setVisibility(View.GONE);
                        }
                        @Override
                        protected void caseOK(GenericResponseV2 response) {
                            Toast.makeText(c, c.getString(R.string.facebook_timeline_friends_invited), Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });
                    c.findViewById(R.id.friends_to_invite_layout).setVisibility(View.GONE);
                    c.findViewById(android.R.id.empty).setVisibility(View.VISIBLE);
                }
                else{
                    Toast.makeText(c, c.getString(R.string.select_friends_to_invite), Toast.LENGTH_LONG).show();
                }
            }
        });

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(R.string.invite_friends);

    }

    @Override
    protected void onStart() {
        super.onStart();
//        FlurryAgent.onStartSession(this, getResources().getString(R.string.FLURRY_KEY));
        manager.start(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        FlurryAgent.onEndSession(this);
        manager.shouldStop();
    }

//    @Override
//    protected String getScreenName() {
//        return "Timeline Friends Invite";
//    }

    SpiceManager manager = new SpiceManager(AptoideSpiceHttpService.class);

    private void rebuildList(final Bundle savedInstanceState) {
        final TimeLineFriendsInviteActivity c = this;
        ListUserFriendsRequest request = new ListUserFriendsRequest();
        request.setOffset(0);
        request.setLimit(150);
        manager.execute(request,"friendslist" + SecurePreferences.getInstance().getString("access_token", "") , DurationInMillis.ONE_HOUR / 2, new TimelineRequestListener<ListUserFriendsJson>(){
            @Override
            protected void caseFAIL() {
                Toast.makeText(c,R.string.error_occured,Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            protected void caseOK(ListUserFriendsJson response) {
                adapter = new TimeLineFriendsCheckableListAdapter(c, response.getInactiveFriends());
                //adapter.setOnItemClickListener(this);
                //adapter.setAdapterView(listView);

                listView.setAdapter(adapter);
                setFriends(response.getActiveFriends());
                c.findViewById(android.R.id.empty).setVisibility(View.GONE);

            }
        });

    }
    private ListView getListView() {
        return (ListView) findViewById(android.R.id.list);
    }

    public void setFriends(List<Friend> activeFriendsList){

        StringBuilder friendsString;
        int i = 0;

        if(activeFriendsList !=null && !activeFriendsList.isEmpty()){

            int j = i;


            do {
                friendsString = new StringBuilder(activeFriendsList.get(j).getUsername());
                j++;
            }while (friendsString.length() == 0);


            for(i = j; i<activeFriendsList.size() && i < 3 + j; i++){
                String friendName = activeFriendsList.get(i).getUsername();
                if(!TextUtils.isEmpty(friendName)){
                    friendsString.append(", ").append(friendName);
                }
            }

            String text;
            text = getString(R.string.facebook_friends_list_using_timeline);

            if ( activeFriendsList.size() - i <= 0 ){
                text = friendsString.toString() + " " +text;

            }else{
                text=friendsString.toString()
                        +" "+ getString(R.string.and)
                        +" "+ String.valueOf(activeFriendsList.size() - i)
                        +" "+ getString(R.string.more_friends)
                        +" "+ text;
            }

            friends_using_timeline.setText(text);


            for(Friend friend : activeFriendsList){
                String avatar = friend.getAvatar();
                final View v = LayoutInflater.from(this).inflate(R.layout.row_facebook_friends_on_timeline, friends_list, false);
                final ImageView avatarIv = (ImageView) v.findViewById(R.id.user_avatar);
                Glide.with(this).load(avatar).transform(new CircleTransform(this)).into(avatarIv);
                friends_list.addView(v);
            }

            friends_list.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
            friends_using_timeline.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
            friends_to_invite.setVisibility(View.VISIBLE);
        }else{
            friends_using_timeline.setText(getString(R.string.facebook_friends_list_using_timeline_empty));
            friends_to_invite.setVisibility(View.GONE);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }

    @Override
    protected String getScreenName() {
        return "Timeline Friends Invite";
    }

}