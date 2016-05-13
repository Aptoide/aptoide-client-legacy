package com.aptoide.amethyst.fragments.timeline;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.R;
import com.aptoide.amethyst.preferences.SecurePreferences;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.dataprovider.AptoideSpiceHttpService;
import com.aptoide.dataprovider.webservices.json.GenericResponseV2;
import com.bumptech.glide.Glide;
import com.facebook.FacebookException;
import com.facebook.widget.LoginButton;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;

import java.util.Arrays;
import java.util.List;


import com.aptoide.amethyst.TimeLineNoFriendsInviteActivity;
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
public class FragmentSocialTimelineLayouts extends Fragment {


    public static final String LOGOUT_FIRST_ARG = "logoutFirst";
    public static final String LOGGED_IN_ARG = "loggedIn";
    public static final java.lang.String STATE_ARG = "state";
    private View timeline_empty_start_invite;
    private View email_friends;
    private ListView listView;
    private View timeline_empty;
    private TimeLineFriendsCheckableListAdapter adapter;
    private View layout;
    private View layout_with_friends;
    private View loading;


    public enum State{
        NONE, LOGGED_IN, LOGOUT_FIRST, FRIENDS_INVITE
    }

//    private Runnable analyticsRunnable;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        State state;
        if(getArguments()!=null){
            state = State.values()[getArguments().getInt(STATE_ARG, 0)];
        }else{
            state = State.NONE;
        }


        switch (state){
            case LOGGED_IN:
//                analyticsRunnable = analyticsJoinEvent();
                return inflater.inflate(R.layout.page_timeline_logged_in, container, false);
            case LOGOUT_FIRST:
//                analyticsRunnable = analyticsLoginEvent();
                return inflater.inflate(R.layout.page_timeline_logout_and_login, container, false);
            case FRIENDS_INVITE:
                return inflater.inflate(R.layout.page_timeline_empty, container, false);
            default:
//                analyticsRunnable = analyticsJoinEvent();
                return inflater.inflate(R.layout.page_timeline_not_logged_in, container, false);

        }
    }

//    private Runnable analyticsJoinEvent() {
//        return new Runnable() {
//            @Override
//            public void run() {
//                Analytics.Facebook.join();
//            }
//        };
//    }
//
//    private Runnable analyticsLoginEvent() {
//        return new Runnable() {
//            @Override
//            public void run() {
//                Analytics.Facebook.Login();
//            }
//        };
//    }

    SpiceManager manager = new SpiceManager(AptoideSpiceHttpService.class);
    private TextView friends_using_timeline;
    private View join_friends;
    private LinearLayout friends_list;


    public void setFriends(ListUserFriendsJson friends){
        List<Friend> friendsList = friends.getActiveFriends() ;

        StringBuilder friendsString;
        int i = 0;

        if(friendsList !=null && !friendsList.isEmpty()){

            int j = i;


            do {
                friendsString = new StringBuilder(friendsList.get(j).getUsername());
                j++;
            }while (friendsString.length() == 0);


            for(i = j; i<friendsList.size() && i < 3 + j; i++){
                String friendName = friendsList.get(i).getUsername();
                if(!TextUtils.isEmpty(friendName)){
                    friendsString.append(", ").append(friendName);
                }
            }

            String text;
            text = getString(R.string.facebook_friends_list_using_timeline);

            if ( friendsList.size() - i <= 0 ){
                text = friendsString.toString() + " " +text;

            }else{
                text=friendsString.toString()
                        +" "+ getString(R.string.and)
                        +" "+ String.valueOf(friendsList.size() - i)
                        +" "+ getString(R.string.more_friends)
                        +" "+ text;
            }

            friends_using_timeline.setText(text);

            for(Friend friend : friendsList){
                String avatar = friend.getAvatar();
                final View v = LayoutInflater.from(getActivity()).inflate(R.layout.row_facebook_friends_on_timeline, friends_list, false);
                final ImageView avatarIv = (ImageView) v.findViewById(R.id.user_avatar);
                Glide.with(this).load(avatar).transform(new CircleTransform(getActivity())).into(avatarIv);
                friends_list.addView(v);

            }

            friends_list.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
            friends_using_timeline.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));
            join_friends.startAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.fade_in));

            join_friends.setVisibility(View.VISIBLE);
        }else{
            friends_using_timeline.setText(getString(R.string.facebook_friends_list_using_timeline_empty));
            join_friends.setVisibility(View.GONE);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        manager.start(getActivity());
    }

    @Override
    public void onStop() {
        super.onStop();
        manager.shouldStop();
    }

    public void getFriends(){
        ListUserFriendsRequest request = new ListUserFriendsRequest();
        String username = SecurePreferences.getInstance().getString("access_token", "");
        manager.execute(request, "facebook-friends-" + username, DurationInMillis.ONE_HOUR ,new TimelineRequestListener<ListUserFriendsJson>() {
            @Override
            protected void caseOK(ListUserFriendsJson response) {
                setFriends((response));
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        State state;
        if(getArguments()!=null){
            state = State.values()[getArguments().getInt(STATE_ARG, 0)];
        }else{
            state = State.NONE;
        }

        switch (state){
            case LOGGED_IN:
                showFriends(view);
                break;
            case FRIENDS_INVITE:
                showInviteFriends(view);
                break;
            default:
            case LOGOUT_FIRST:
                LoginButton fb_login_button = (LoginButton) view.findViewById(R.id.fb_login_button);
                fb_login_button.setReadPermissions(Arrays.asList("email", "user_friends"));
                fb_login_button.setOnErrorListener(new LoginButton.OnErrorListener() {
                    @Override
                    public void onError(FacebookException error) {
                        error.printStackTrace();
                        Toast.makeText(Aptoide.getContext(), R.string.error_occured, Toast.LENGTH_LONG).show();
                    }
                });
                fb_login_button.setFragment(getParentFragment());
                fb_login_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        FlurryAgent.logEvent("Social_Timeline_Clicked_On_Login_With_Facebook");
                    }
                });
                break;
        }

    }

    private void rebuildList() {

        ListUserFriendsRequest request = new ListUserFriendsRequest();
        request.setOffset(0);
        request.setLimit(150);

        manager.execute(request, "friendslist" + SecurePreferences.getInstance().getString("access_token", "") , DurationInMillis.ONE_HOUR ,new TimelineRequestListener<ListUserFriendsJson>() {
            @Override
            protected void caseOK(ListUserFriendsJson response) {

                loading.setVisibility(View.GONE);
                adapter = new TimeLineFriendsCheckableListAdapter(getActivity(), response.getInactiveFriends());
                //adapter.setOnItemClickListener(this);
                //adapter.setAdapterView(listView);


                if(response.getInactiveFriends().isEmpty()){
                    layout.setVisibility(View.VISIBLE);
                    email_friends.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TimeLineNoFriendsInviteActivity.sendMail(getActivity());
                        }
                    });
                }else{
                    layout_with_friends.setVisibility(View.VISIBLE);
                    listView.setAdapter(adapter);
                }

            }
        });

    }

    private void showInviteFriends(final View view) {
        loading = view.findViewById(android.R.id.empty);
        email_friends = view.findViewById(R.id.email_friends);
        listView = (ListView) view.findViewById(android.R.id.list);
        layout = view.findViewById(R.id.layout_no_friends);
        layout_with_friends = view.findViewById(R.id.layout_with_friends);
        View footer_friends_to_invite = LayoutInflater.from(getActivity()).inflate(R.layout.footer_invite_friends, null);
        listView.addFooterView(footer_friends_to_invite);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        rebuildList();
        Button invite = (Button) footer_friends_to_invite.findViewById(R.id.timeline_invite);
        final Context c = getActivity();

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
                        private void cleanUI(){
                            view.findViewById(R.id.layout_with_friends).setVisibility(View.VISIBLE);
                            view.findViewById(android.R.id.empty).setVisibility(View.GONE);
                        }

                        @Override
                        protected void caseFAIL() {
                            cleanUI();
                        }
                        @Override
                        protected void caseOK(GenericResponseV2 response) {
                            cleanUI();
                            Toast.makeText(c, c.getString(R.string.facebook_timeline_friends_invited), Toast.LENGTH_LONG).show();
                        }
                    });
                    view.findViewById(R.id.layout_with_friends).setVisibility(View.GONE);
                    view.findViewById(android.R.id.empty).setVisibility(View.VISIBLE);
                }
                else{
                    Toast.makeText(c, c.getString(R.string.select_friends_to_invite), Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public interface Callback {
        void onStartTimeline();
    }


    public void showFriends(View view){
        friends_using_timeline = (TextView) view.findViewById(R.id.friends_using_timeline);
        join_friends = view.findViewById(R.id.join_friends);
        getFriends();

        friends_list = (LinearLayout) view.findViewById(R.id.friends_list);

        Button start_timeline = (Button) view.findViewById(R.id.start_timeline);

        start_timeline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FlurryAgent.logEvent("Social_Timeline_Clicked_On_Join_Social_Timeline");
                AptoideUtils.SocialMedia.acceptTimeline();
                ((Callback) getParentFragment()).onStartTimeline();
            }
        });
    }


}

