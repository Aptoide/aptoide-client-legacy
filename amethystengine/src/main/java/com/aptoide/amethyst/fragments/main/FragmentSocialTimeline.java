package com.aptoide.amethyst.fragments.main;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.LoginActivity;
import com.aptoide.amethyst.R;
import com.aptoide.amethyst.analytics.Analytics;
import com.aptoide.amethyst.events.BusProvider;
import com.aptoide.amethyst.events.OttoEvents;
import com.aptoide.amethyst.preferences.Preferences;
import com.aptoide.amethyst.preferences.SecurePreferences;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.amethyst.utils.Logger;
import com.aptoide.amethyst.webservices.ListApksInstallsRequest;
import com.aptoide.amethyst.webservices.json.TimelineListAPKsJson;
import com.aptoide.dataprovider.AptoideSpiceHttpService;
import com.aptoide.dataprovider.webservices.json.GenericResponseV2;
import com.aptoide.dataprovider.webservices.models.Constants;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.NativeAd;
import com.facebook.model.GraphUser;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;


import com.aptoide.amethyst.TimeLineFriendsInviteActivity;
import com.aptoide.amethyst.TimeLineNoFriendsInviteActivity;
import com.aptoide.amethyst.adapter.timeline.EndlessWrapperAdapter;
import com.aptoide.amethyst.adapter.timeline.TimelineAdapter;
import com.aptoide.amethyst.dialogs.TimeLineCommentsDialog;
import com.aptoide.amethyst.dialogs.TimeLineWhoLikesDialog;
import com.aptoide.amethyst.fragments.FragmentSdkNotCompatible;
import com.aptoide.amethyst.fragments.timeline.FragmentSignIn;
import com.aptoide.amethyst.fragments.timeline.FragmentSocialTimelineLayouts;
import com.aptoide.amethyst.webservices.timeline.AddUserApkInstallCommentRequest;
import com.aptoide.amethyst.webservices.timeline.AddUserApkInstallLikeRequest;
import com.aptoide.amethyst.webservices.timeline.GetUserApkInstallCommentsRequest;
import com.aptoide.amethyst.webservices.timeline.ListUserFriendsRequest;
import com.aptoide.amethyst.webservices.timeline.ListUserapklikesRequest;
import com.aptoide.amethyst.webservices.timeline.TimeLineManager;
import com.aptoide.amethyst.webservices.timeline.TimelineRequestListener;
import com.aptoide.amethyst.webservices.timeline.json.ApkInstallComments;
import com.aptoide.amethyst.webservices.timeline.json.ChangeUserApkInstallStatusRequest;
import com.aptoide.amethyst.webservices.timeline.json.ListUserFriendsJson;
import com.aptoide.amethyst.webservices.timeline.json.ListapklikesJson;

/**
 * Created by fabio on 13-10-2015.
 */
public class FragmentSocialTimeline extends Fragment implements FragmentSignIn.SignInCallback, FragmentSocialTimelineLayouts.Callback {

    private UiLifecycleHelper fbhelper;
    private boolean loginMode;
    private boolean removeAccount;

    @Subscribe
    public void forceRefresh(OttoEvents.SocialTimelineEvent event) {

        if (event != null && event.isRefresh()) {
            Fragment fragmentById = getChildFragmentManager().findFragmentByTag("tag");
            if (fragmentById != null && fragmentById instanceof SubFragmentSocialTimeline) {
                ((SubFragmentSocialTimeline) fragmentById).forceRefresh();
            }
        }

    }

    @Subscribe
    public void forceInit(OttoEvents.SocialTimelineInitEvent event) {
        init();
    }

    public void loginError() {
        Session session = Session.getActiveSession();

        if (session != null && session.isOpened()) {
            session.closeAndClearTokenInformation();
        }
        loginMode = false;
        init();
    }

    public void loginEnded() {
        if (removeAccount) {
            removeAccount = false;
        }

        loginMode = false;
        startTimeline(true);
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    private void startTimeline(boolean force) {
        if (force) {
            AptoideUtils.SocialMedia.acceptTimeline();
        }

        if (Preferences.getBoolean(Preferences.TIMELINE_ACEPTED_BOOL, false) || force) {

            Account account = AccountManager.get(getActivity()).getAccountsByType(Aptoide.getConfiguration().getAccountType())[0];

            if (Preferences.getBoolean("socialtimelinenotifications", true)) {

                String timelineActivitySyncAdapterAuthority = Aptoide.getConfiguration().getTimelineActivitySyncAdapterAuthority();
                String timeLinePostsSyncAdapterAuthority = Aptoide.getConfiguration().getTimeLinePostsSyncAdapterAuthority();

                ContentResolver.setSyncAutomatically(account, timelineActivitySyncAdapterAuthority, true);
                ContentResolver.addPeriodicSync(account, timelineActivitySyncAdapterAuthority, new Bundle(), 7200);

                ContentResolver.setSyncAutomatically(account, timeLinePostsSyncAdapterAuthority, true);
                ContentResolver.addPeriodicSync(account, timeLinePostsSyncAdapterAuthority, new Bundle(), 86400);
            }

            if (!PreferenceManager.getDefaultSharedPreferences(Aptoide.getContext()).contains(Preferences.SHARE_TIMELINE_DOWNLOAD_BOOL)) {
                Preferences.putBooleanAndCommit(Preferences.SHARE_TIMELINE_DOWNLOAD_BOOL, true);
            }


            SubFragmentSocialTimeline fragment = new SubFragmentSocialTimeline();
            getChildFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment, "tag").setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();

        } else {

            Fragment fragment = new FragmentSocialTimelineLayouts();
            Bundle args = new Bundle();
            args.putInt(FragmentSocialTimelineLayouts.STATE_ARG, FragmentSocialTimelineLayouts.State.LOGGED_IN.ordinal());
            fragment.setArguments(args);
            getChildFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment, "tag").commit();
        }

    }


    @Override
    public void onStartTimeline() {
        startTimeline(false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Logger.d("FragmentSocialTimeline", " onCreate");
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            init();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {

            fbhelper = new UiLifecycleHelper(getActivity(), new Session.StatusCallback() {
                @Override
                public void call(final Session session, SessionState state, Exception exception) {
                    if (!AptoideUtils.AccountUtils.isLoggedIn(Aptoide.getContext()) || removeAccount) {
                        try {
                            final AccountManager mAccountManager = AccountManager.get(getActivity());

                            if (session.isOpened()) {
                                Request.newMeRequest(session, new Request.GraphUserCallback() {
                                    @Override
                                    public void onCompleted(final GraphUser user, Response response) {

                                        if (removeAccount && mAccountManager.getAccountsByType(Aptoide.getConfiguration().getAccountType()).length > 0) {
                                            mAccountManager.removeAccount(mAccountManager.getAccountsByType(Aptoide.getConfiguration().getAccountType())[0], new AccountManagerCallback<Boolean>() {
                                                @Override
                                                public void run(AccountManagerFuture<Boolean> future) {
                                                    startLogin(user, session);
                                                }
                                            }, new Handler(Looper.getMainLooper()));
                                        } else {

                                            startLogin(user, session);
                                        }
                                    }
                                }).executeAsync();

                            }
                        } catch (Exception e) {
                            Toast.makeText(Aptoide.getContext(), R.string.error_occured, Toast.LENGTH_LONG).show();
                            loginError();
                        }
                    }
                }
            });

            fbhelper.onCreate(savedInstanceState);
        }
    }

    private void startLogin(GraphUser user, Session session) {
        try {
            loginMode = true;
            Fragment fragment = new FragmentSignIn();
            Bundle args = new Bundle();
            args.putInt(FragmentSignIn.LOGIN_MODE_ARG, LoginActivity.Mode.FACEBOOK.ordinal());
            args.putString(FragmentSignIn.LOGIN_PASSWORD_OR_TOKEN_ARG, session.getAccessToken());
            args.putString(FragmentSignIn.LOGIN_USERNAME_ARG, (String) user.getProperty("email"));
            fragment.setArguments(args);
            getChildFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment, "tag").setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();

            Analytics.SocialTimeline.login();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() {
        Fragment fragment;

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
            fragment = new FragmentSdkNotCompatible();
            getChildFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment, "tag").commit();
            return;
        }

        if (AptoideUtils.AccountUtils.isLoggedIn(Aptoide.getContext())) {
            if ("FACEBOOK".equals(PreferenceManager.getDefaultSharedPreferences(Aptoide.getContext()).getString("loginType", null))) {
                startTimeline(false);
            } else {
                fragment = new FragmentSocialTimelineLayouts();
                Bundle args = new Bundle();
                args.putInt(FragmentSocialTimelineLayouts.STATE_ARG, FragmentSocialTimelineLayouts.State.LOGOUT_FIRST.ordinal());
                fragment.setArguments(args);
                getChildFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment, "tag").commit();
                removeAccount = true;
            }

        } else {
            fragment = new FragmentSocialTimelineLayouts();
            getChildFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment, "tag").commit();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.page_store, container, false);
    }


    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            fbhelper.onResume();
        }

        init();
    }

    @Override
    public void onPause() {
        BusProvider.getInstance().unregister(this);
        super.onPause();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            fbhelper.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            fbhelper.onDestroy();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            fbhelper.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            fbhelper.onStop();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            fbhelper.onSaveInstanceState(outState);
        }
        outState.putBoolean("loginMode", loginMode);
    }

    public static Fragment newInstance() {
        return new FragmentSocialTimeline();
    }

    public static class SubFragmentSocialTimeline extends ListFragment implements SwipeRefreshLayout.OnRefreshListener, TimeLineManager, EndlessWrapperAdapter.Callback, AdListener {

        private static final int COMMENTSLIMIT = 10;
        private static final String COMMENTSDIALOGTAG = "CD";
        private static final String LIKESDIALOGTAG = "LD";
        private static final String TAG = SubFragmentSocialTimeline.class.getSimpleName();

        private ArrayList<TimelineListAPKsJson.UserApk> apks = new ArrayList<TimelineListAPKsJson.UserApk>();
        private EndlessWrapperAdapter adapter;
        private Number lastId;
        private Number firstId;
        private SwipeRefreshLayout mSwipeRefreshLayout;
        private boolean mListShown = false;

        private boolean forceRefresh;
        private View inviteFriends;

        public void forceRefresh() {
            forceRefresh = true;
        }

        private class ListFragmentSwipeRefreshLayout extends SwipeRefreshLayout {

            public ListFragmentSwipeRefreshLayout(Context context) {
                super(context);
            }

            /**
             * As mentioned above, we need to override this method to properly signal when a
             * 'swipe-to-refresh' is possible.
             *
             * @return true if the {@link android.widget.ListView} is visible and can scroll up.
             */
            @Override
            public boolean canChildScrollUp() {
                final ListView listView = getListView();
                if (listView.getVisibility() == View.VISIBLE) {
                    return canListViewScrollUp(listView);
                } else {
                    return false;
                }
            }

        }


        private static boolean canListViewScrollUp(ListView listView) {
            if (android.os.Build.VERSION.SDK_INT >= 14) {
                // For ICS and above we can call canScrollVertically() to determine this
                return ViewCompat.canScrollVertically(listView, -1);
            } else {
                // Pre-ICS we need to manually check the first visible item and the child view's top
                // value
                return listView.getChildCount() > 0 &&
                        (listView.getFirstVisiblePosition() > 0
                                || listView.getChildAt(0).getTop() < listView.getPaddingTop());
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            // Create the list fragment's content view by calling the super method

            final FrameLayout listFragmentView = (FrameLayout) super.onCreateView(inflater, container, savedInstanceState);

            // Now create a SwipeRefreshLayout to wrap the fragment's content view
            mSwipeRefreshLayout = new ListFragmentSwipeRefreshLayout(container.getContext());


            mSwipeRefreshLayout.setOnRefreshListener(this);

            // Add the list fragment's content view to the SwipeRefreshLayout, making sure that it fills
            // the SwipeRefreshLayout
            mSwipeRefreshLayout.addView(listFragmentView,
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            // Make sure that the SwipeRefreshLayout will fill the fragment
            mSwipeRefreshLayout.setLayoutParams(
                    new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));

            // Now return the SwipeRefreshLayout as this fragment's content view
            return mSwipeRefreshLayout;

        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            init();
            mSwipeRefreshLayout.setColorSchemeResources(R.color.default_progress_bar_color,
                    R.color.default_color, R.color.default_progress_bar_color, R.color.default_color);
        }

        public void onItemsReady(ArrayList<TimelineListAPKsJson.UserApk> data) {
            Logger.d("FragmentSocialTimeline", " onItemsReady ");
            try {
                if (data.isEmpty()) {
                    if (apks.isEmpty()) {

                        FragmentSocialTimelineLayouts fragmentSocialTimelineLayouts = new FragmentSocialTimelineLayouts();
                        Bundle args = new Bundle();
                        args.putInt(FragmentSocialTimelineLayouts.STATE_ARG, FragmentSocialTimelineLayouts.State.FRIENDS_INVITE.ordinal());
                        fragmentSocialTimelineLayouts.setArguments(args);
                        getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragmentSocialTimelineLayouts).commitAllowingStateLoss();
                    }
                    adapter.stopAppending();
                } else {
                    if (firstId == null) {
                        firstId = data.get(0).info.id;
                    }
                    data.get(0).animate = true;

                    if (AptoideUtils.getSharedPreferences().getBoolean(Constants.MATURE_CHECK_BOX, false)) {
                        apks.addAll(data);
                    } else {
                        // filter mature apps

                        for (TimelineListAPKsJson.UserApk apk : data) {
                            if (!apk.apk.age.equals("Mature")) {
                                apks.add(apk);
                            }
                        }

                    }

                    lastId = apks.get(apks.size() - 1).info.id;

                }   // Tell the EndlessAdapter to
                // remove it's pending
                // view and call
                // notifyDataSetChanged()
                adapter.onDataReady();
//                initFACEBOOKNativeAd();
            } catch (Exception e) {
                Logger.printException(e);
            }

        }

        public void onItemsReadyRefresh(ArrayList<TimelineListAPKsJson.UserApk> data) {
            Logger.d("FragmentSocialTimeline", " onItemsReadyRefresh ");
            if (AptoideUtils.getSharedPreferences().getBoolean(Constants.MATURE_CHECK_BOX, false)) {
                apks.addAll(0, data);
            } else {

                for (TimelineListAPKsJson.UserApk apk : data) {
                    if (!apk.apk.age.equals("Mature")) {
                        apks.add(apk);
                    }
                }

            }


            if (apks.size() > 0) {
                firstId = apks.get(0).info.id;
                lastId = apks.get(apks.size() - 1).info.id;
                adapter.onDataReady();
            }
            // remove it's pending
            // view and call
            // notifyDataSetChanged()
        }


        SpiceManager manager = new SpiceManager(AptoideSpiceHttpService.class);

        private ListApksInstallsRequest listAPKsInstallsRequest;


        private RequestListener<TimelineListAPKsJson> listener = new RequestListener<TimelineListAPKsJson>() {

            @Override
            public void onRequestFailure(SpiceException spiceException) {

            }

            @Override
            public void onRequestSuccess(TimelineListAPKsJson timelineListAPKsJson) {

                if (timelineListAPKsJson != null && timelineListAPKsJson.usersapks != null) {
                    onItemsReady(new ArrayList<TimelineListAPKsJson.UserApk>(timelineListAPKsJson.usersapks));
                    mSwipeRefreshLayout.setRefreshing(false);
                    if (!mListShown) setListShown(true);
                }

            }

        };

        private RequestListener<TimelineListAPKsJson> listenerRefresh = new RequestListener<TimelineListAPKsJson>() {

            @Override
            public void onRequestFailure(SpiceException spiceException) {
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onRequestSuccess(TimelineListAPKsJson timelineListAPKsJson) {
                apks.clear();
                onItemsReadyRefresh(new ArrayList<TimelineListAPKsJson.UserApk>(timelineListAPKsJson.usersapks));
                mSwipeRefreshLayout.setRefreshing(false);
                if (!mListShown) setListShown(true);
            }
        };

        public void runRequest() {
            listAPKsInstallsRequest = new ListApksInstallsRequest();

            if (lastId != null) {
                listAPKsInstallsRequest.setOffset_id(String.valueOf(lastId.intValue()));
                listAPKsInstallsRequest.setDownwardsDirection();
            }

            manager.execute(listAPKsInstallsRequest, "timeline-posts-id" + (lastId != null ? lastId.intValue() : "") + username, DurationInMillis.ONE_HOUR / 2, listener);
        }

        public void refreshRequest() {
            listAPKsInstallsRequest = new ListApksInstallsRequest();

            //listAPKsInstallsRequest.setOffset(String.valueOf(firstId.intValue()));
            //listAPKsInstallsRequest.setUpwardsDirection();
            Logger.d("FragmentSocialTimeline", "notifydatasetchanged");
            adapter.notifyDataSetChanged();
            Logger.d("FragmentSocialTimeline", "restartAppending");
            adapter.restartAppending();


            Executors.newSingleThreadExecutor().execute(new Runnable() {
                @Override
                public void run() {

                    try {
                        Logger.d("FragmentSocialTimeline", "RemovingData from Cache");
                        manager.removeDataFromCache(TimelineListAPKsJson.class, "timeline-posts-id" + username).get();
                    } catch (InterruptedException | ExecutionException e) {
                        Logger.printException(e);
                    }

                    Logger.d("FragmentSocialTimeline", "Executing request");
                    manager.execute(listAPKsInstallsRequest, "timeline-posts-id" + username, DurationInMillis.ONE_HOUR / 2, listenerRefresh);
                }
            });

        }


        @Override
        public void onCreate(Bundle savedInstanceState) {
            //Aptoide.getThemePicker().setAptoideTheme(this);
            super.onCreate(savedInstanceState);

        }

        String username = "";


        private void init() {
            adapter = new EndlessWrapperAdapter(new TimelineAdapter(this, getActivity(), apks), this, getActivity());
            adapter.setRunInBackground(false);
            username = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("username", "");

            mSwipeRefreshLayout.setRefreshing(true);
            inviteFriends = LayoutInflater.from(getActivity()).inflate(R.layout.separator_invite_friends, null);
            getListView().addHeaderView(inviteFriends);

            final View invite = inviteFriends.findViewById(R.id.timeline_invite);
            final Context c = getActivity();
            ListUserFriendsRequest request = new ListUserFriendsRequest();

            request.setOffset(0);
            request.setLimit(150);

            manager.execute(request, "friendslist" + SecurePreferences.getInstance().getString("access_token", ""), DurationInMillis.ONE_HOUR / 2, new RequestListener<ListUserFriendsJson>() {

                @Override
                public void onRequestFailure(SpiceException spiceException) {
                    Logger.e(TAG, spiceException.getMessage());
                }

                @Override
                public void onRequestSuccess(ListUserFriendsJson listUserFriendsJson) {
                    View.OnClickListener onClickListener;

                    if (listUserFriendsJson.getInactiveFriends().isEmpty()) {

                        onClickListener = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                FlurryAgent.logEvent("Social_Timeline_Clicked_On_Invite_Friends");
                                startActivity(new Intent(c, TimeLineNoFriendsInviteActivity.class));
                            }
                        };

                    } else {

                        onClickListener = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                FlurryAgent.logEvent("Social_Timeline_Clicked_On_Invite_Friends");
                                startActivity(new Intent(c, TimeLineFriendsInviteActivity.class));
                            }
                        };
                    }

                    invite.setOnClickListener(onClickListener);

                }
            });

            getListView().setItemsCanFocus(true);
            setListAdapter(adapter);
            setListShown(false);
            //force loading
            adapter.getView(0, null, null);

        }

        @Override
        public void onResume() {
            super.onResume();
            if (forceRefresh) {
                refreshRequest();
                forceRefresh = false;
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

        @Override
        public void onRefresh() {
            refreshRequest();
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int i = item.getItemId();
            if (i == android.R.id.home) {
//        } else if (i == R.id.menu_invite_friends) {

            }
            return super.onOptionsItemSelected(item);
        }

           /*FACEBOOK ads stuff*/


        private NativeAd listNativeAd;

        private void initFACEBOOKNativeAd(){
            Logger.d("FragmentSocialTimeline"," initFACEBOOKNativeAd ");
            if(listNativeAd!=null)
                return;
            Logger.d("FragmentSocialTimeline"," initFACEBOOKNativeAd new ad");
            listNativeAd = new NativeAd(getActivity(), "381014172054741_426066357549522");
            listNativeAd.setAdListener(this);
            listNativeAd.loadAd();
        }

        @Override
        public void onAdClicked(Ad ad) {
            Logger.d("FragmentSocialTimeline","onAdClicked , ad: "+ad.toString());
        }

        @Override
        public void onAdLoaded(Ad ad) {
            Logger.d("FragmentSocialTimeline","onAdLoaded , ad: "+ad.toString());
            if(getActivity() != null){
                adapter.addNativeAd((NativeAd) ad,getActivity(),apks);
            }

        }

        @Override
        public void onError(Ad ad, AdError error) {
            Logger.d("FragmentSocialTimeline"," onError "+error.getErrorMessage());
            Logger.d("FragmentSocialTimeline"," onError code:"+error.getErrorCode());
        }


    /* *************** Methods of the TimeLineManager Interface *************** */

        @Override
        public void hidePost(long id) {
            changeUserApkInstallStatusPost(id, ChangeUserApkInstallStatusRequest.STATUSHIDDEN);
        }

        @Override
        public void unHidePost(long id) {
            changeUserApkInstallStatusPost(id, ChangeUserApkInstallStatusRequest.STATUSACTIVE);
        }

        private void changeUserApkInstallStatusPost(long id, String status) {
            ChangeUserApkInstallStatusRequest request = new ChangeUserApkInstallStatusRequest();
            request.setPostStatus(status);
            request.setPostId(id);
            manager.removeDataFromCache(TimelineListAPKsJson.class);
            manager.execute(request, new TimelineRequestListener<GenericResponseV2>());
        }

        @Override
        public void likePost(long id) {
            likeRequestPost(id, AddUserApkInstallLikeRequest.LIKE);
        }

        @Override
        public void unlikePost(long id) {
            likeRequestPost(id, AddUserApkInstallLikeRequest.UNLIKE);
        }

        private void likeRequestPost(long id, String like) {
            AddUserApkInstallLikeRequest request = new AddUserApkInstallLikeRequest();
            request.setLike(like);
            request.setPostId(id);

//            manager.execute(request, new TimelineRequestListener<GenericResponse>());
            manager.execute(request, new LikeRequestListner());
        }

        @Override
        public void commentPost(long id, String comment, int position) {
            AddUserApkInstallCommentRequest request = new AddUserApkInstallCommentRequest();
            request.setPostID(id);
            request.setComment(comment);
            manager.execute(request, new SetUserApkInstallCommentsRequestListener(id, position));
        }

        @Override
        public void getComments(long id) {
            GetUserApkInstallCommentsRequest request = new GetUserApkInstallCommentsRequest();
            request.setPostID(id);
            request.setPostLimit(COMMENTSLIMIT);
            manager.execute(request, new GetUserApkInstallCommentsRequestListener());
        }

        @Override
        public void getWhoLiked(long id) {
            ListUserapklikesRequest request = new ListUserapklikesRequest();
            request.setPostID(id);
            manager.execute(request, new GetUserApklikesRequestListener());
        }

        @Override
        public void openCommentsDialog(long id, int position) {
            Bundle args = new Bundle();
            args.putString(TimeLineCommentsDialog.LIKES, String.valueOf(((TimelineListAPKsJson.UserApk) adapter.getItem(position)).info.likes));
            args.putLong(TimeLineCommentsDialog.POSTID, id);
            args.putInt(TimeLineCommentsDialog.POSITION, position);

            TimeLineCommentsDialog commentsDialog = new TimeLineCommentsDialog();
            commentsDialog.setArguments(args);
            FragmentTransaction backstack = getChildFragmentManager().beginTransaction().addToBackStack("1");

            commentsDialog.show(backstack, COMMENTSDIALOGTAG);
        }

        @Override
        public void openWhoLikesDialog(long id, int likes, int position) {
            Bundle args = new Bundle();
            args.putString(TimeLineWhoLikesDialog.LIKES, String.valueOf(likes));
            args.putLong(TimeLineWhoLikesDialog.POSTID, id);
            args.putInt(TimeLineCommentsDialog.POSITION, position);

            TimeLineWhoLikesDialog wholikesDialog = new TimeLineWhoLikesDialog();
            wholikesDialog.setArguments(args);
            FragmentTransaction backstack = getChildFragmentManager().beginTransaction().addToBackStack("1");
            wholikesDialog.show(backstack, LIKESDIALOGTAG);
        }

    /* *************** End of Methods of the TimeLineManager Interface *************** */

        // new LikeRequestListener
        public class LikeRequestListner extends TimelineRequestListener<GenericResponseV2> {

            @Override
            protected void caseOK(GenericResponseV2 response) {
                super.caseOK(response);
            }
        }

        public class SetUserApkInstallCommentsRequestListener extends TimelineRequestListener<GenericResponseV2> {
            private final long postid;
            private final int position;

            public SetUserApkInstallCommentsRequestListener(long postid, int position) {
                this.postid = postid;
                this.position = position;
            }

            @Override
            protected void caseFAIL() {
                Toast.makeText(Aptoide.getContext(), R.string.error_occured, Toast.LENGTH_LONG).show();
                dismissFrag();
            }

            private void dismissFrag() {
                TimeLineCommentsDialog fragmentByTag = (TimeLineCommentsDialog) getChildFragmentManager().findFragmentByTag(COMMENTSDIALOGTAG);
                if (fragmentByTag != null) {
                    fragmentByTag.dismiss();
                }
            }

            @Override
            protected void caseOK(GenericResponseV2 response) {
                listAPKsInstallsRequest = new ListApksInstallsRequest();
                listAPKsInstallsRequest.setPostId(postid);
                manager.execute(listAPKsInstallsRequest, new TimelineRequestListener<TimelineListAPKsJson>() {
                    @Override
                    protected void caseOK(TimelineListAPKsJson response) {
                        if (!response.usersapks.isEmpty()) {
                            TimelineListAPKsJson.UserApk apk = response.usersapks.get(0);
                            apks.set(position, apk);
                        }
                        adapter.onDataReady();
                    }
                });
                dismissFrag();
            }
        }

        public class GetUserApkInstallCommentsRequestListener extends TimelineRequestListener<ApkInstallComments> {
            @Override
            protected void caseOK(ApkInstallComments response) {
                if ((response).getComments() != null) {
                    TimeLineCommentsDialog timeLineCommentsDialog = (TimeLineCommentsDialog) getChildFragmentManager().findFragmentByTag(COMMENTSDIALOGTAG);
                    if (timeLineCommentsDialog != null) {
                        timeLineCommentsDialog.setComments((response).getComments());
                    }
                }
            }
        }

        public class GetUserApklikesRequestListener extends TimelineRequestListener<ListapklikesJson> {
            @Override
            protected void caseOK(ListapklikesJson response) {
                if ((response).getUsersapks_likes() != null) {
                    TimeLineWhoLikesDialog timeLineWhoLikesDialog = (TimeLineWhoLikesDialog) getChildFragmentManager().findFragmentByTag(LIKESDIALOGTAG);
                    if (timeLineWhoLikesDialog != null) {
                        timeLineWhoLikesDialog.setFriends((response).getUsersapks_likes());
                    }
                }
            }
        }

    }
}
