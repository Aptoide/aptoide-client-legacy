package com.aptoide.amethyst.ui;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.AptoideBaseActivity;
import com.aptoide.amethyst.R;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.amethyst.utils.Logger;



import com.aptoide.amethyst.events.BusProvider;
import com.aptoide.amethyst.events.OttoEvents;

import com.aptoide.amethyst.websockets.WebSocketSingleton;
import com.aptoide.amethyst.services.DownloadService;


/**
 * Created with IntelliJ IDEA.
 * User: rmateus
 * Date: 04-12-2013
 * Time: 15:54
 * To change this template use File | Settings | File Templates.
 */
public class SearchManager extends AptoideBaseActivity implements SearchQueryCallback {

    private DownloadService downloadService;

    String query;
    private boolean isDisconnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Aptoide.getThemePicker().setAptoideTheme(this);
        super.onCreate(savedInstanceState);

//        setContentView(R.layout.page_search);

        Bundle args = new Bundle();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        if (savedInstanceState != null) {
            query = savedInstanceState.getString("query");
            getSupportActionBar().setTitle("'" + query + "'");

            Log.d("SearchManager", "Read From savedInstanceState");
            return;
        }
        if (query == null) {
            Log.d("SearchManager", "Query has null");
            if (getIntent().hasExtra("search")) {
                query = getIntent().getExtras().getString("search");
            } else {
                query = getIntent().getExtras().getString(android.app.SearchManager.QUERY).replaceAll("\\s{2,}|\\W", " ").trim();
                query = query.replaceAll("\\s{2,}", " ");
            }
        }
        Log.d("SearchManager", "Query:" + query);

//        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this, Aptoide.getConfiguration().getSearchAuthority(), 1);
//        suggestions.saveRecentQuery(query, null);

        args.putString("query", query);

        getSupportActionBar().setTitle("'" + query + "'");
        args.putBoolean("searchmorevisible", isSearchMoreVisible());

        Fragment fragment = new SearchFragment();
        fragment.setArguments(args);

//        getSupportFragmentManager().beginTransaction().replace(R.id.fragContainer, fragment, "search").commit();

        bindService(new Intent(this, DownloadService.class), conn2, BIND_AUTO_CREATE);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("query", query);
        Log.d("SearchManager", "onSaveInstanceState:" + query);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();

        if (i == android.R.id.home) {
            finish();
        } else if (i == R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


//        getMenuInflater().inflate(R.menu.menu_search, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        final android.app.SearchManager searchManager = (android.app.SearchManager) getSystemService(Context.SEARCH_SERVICE);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                MenuItemCompat.collapseActionView(searchItem);
                searchView.setQuery(query, false);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus) {
                    isDisconnect = true;

                    if (Build.VERSION.SDK_INT > 7) {

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                if (isDisconnect) {
//                                    WebSocketSingleton.getInstance().disconnect();
                                }

                            }
                        }, 10000);

                    }
                }

            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDisconnect = false;

//                FlurryAgent.logEvent("Clicked_On_Search_Button");

                if (Build.VERSION.SDK_INT > 7) {
//                    WebSocketSingleton.getInstance().connect();
                } else {
                    onSearchRequested();
                    MenuItemCompat.collapseActionView(searchItem);
                }
            }
        });


        if (Build.VERSION.SDK_INT > 7) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void setQuery(String query) {
        this.query = query;
    }


    public static class SearchFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {


//        SpiceManager manager = new SpiceManager(HttpClientSpiceService.class);
//        private List<SearchJson.Results.Apks> items = new ArrayList<SearchJson.Results.Apks>();
//        private List<SearchJson.Results.Apks> items2 = new ArrayList<SearchJson.Results.Apks>();

        private SearchQueryCallback callback;
        private boolean loading;
        private View v2;
        private View searchLayout;
        private boolean hasUapks;
        private View sponsoredApp;
        private View sponsoredAdApp, sponsoredAdLabel;
        private View searchResultsLabel;

        @Override
        public void onStart() {
            super.onStart();
//            manager.start(getActivity());
//            FlurryAgent.onStartSession(getActivity(), "59W5PVRJJ2956RV4RT5Z");
        }

        @Override
        public void onStop() {
            super.onStop();
//            if (manager.isStarted()) manager.shouldStop();
//            FlurryAgent.onEndSession(getActivity());
        }

        //        private MergeAdapter adapter;
//        private String query;
//        int positionsub = 0;
//        private SearchAdapter2 searchAdapterapks;
//        private SearchAdapter2 searchAdapterapks2;
//
//        private SearchAdapter cursorAdapter;
//        private StoreActivity.Sort sort = StoreActivity.Sort.DOWNLOADS;
        private View v;
        TextView more;
//        private Class appViewClass = Aptoide.getConfiguration().getAppViewActivityClass();


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
//            adapter = new MergeAdapter();
//            setRetainInstance(true);
//            searchAdapterapks = new SearchAdapter2(getActivity(), items);
//            searchAdapterapks2 = new SearchAdapter2(getActivity(), items2);
//
//            query = getArguments().getString("query");
//
//            searchLayout = LayoutInflater.from(getActivity()).inflate(R.layout.didyoumean_and_uapks_search_layout, null);
//            v = ((SearchManager) getActivity()).getFooterView(R.layout.footer_search);
//            v2 = ((SearchManager) getActivity()).getFooterView(R.layout.footer_search);
//
//            View inflater = View.inflate(getActivity(), R.layout.progress_bar, null);
//            pb = (ProgressBar) inflater.findViewById(R.id.progressBar);
//
//            AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
//            pb.setLayoutParams(params);
//
//
//            adapter.addView(searchLayout);
//
//            sponsoredApp = LayoutInflater.from(getActivity()).inflate(R.layout.row_app_suggested, null);
//
//            adapter.addView(sponsoredApp);
//            adapter.setActive(sponsoredApp, false);
//
//            sponsoredAdLabel = LayoutInflater.from(getActivity()).inflate(R.layout.separator_ad_banner, null);
//            adapter.addView(sponsoredAdLabel);
//            adapter.setActive(sponsoredAdLabel, false);
//
//            sponsoredAdApp = LayoutInflater.from(getActivity()).inflate(R.layout.row_app_ad_banner, null);
//            adapter.addView(sponsoredAdApp);
//            adapter.setActive(sponsoredAdApp, false);
//
//            searchResultsLabel = LayoutInflater.from(getActivity()).inflate(R.layout.separator_searchu, null);
//            adapter.addView(searchResultsLabel);
//            adapter.addAdapter(searchAdapterapks);
//            adapter.setActive(searchResultsLabel, false);
//
//            if (getArguments().getBoolean("searchmorevisible", true)) {
//                adapter.addView(v);
//                adapter.setActive(v, false);
//            }
//
//
//            adapter.addAdapter(searchAdapterapks2);
//
//            adapter.addView(pb);
//            adapter.setActive(pb, false);
//            if (getArguments().getBoolean("searchmorevisible", true)) {
//                adapter.addView(v2);
//                adapter.setActive(v2, false);
//            }


        }

//        private int getItemPosition(String md5sum) {
//            for (int i = 0; i < items.size(); i++) {
//                if (items.get(i).getMd5sum().equals(md5sum))
//                    return i;
//            }
//            return -1;
//        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
//            position += positionsub;
//            Intent intent = new Intent(getActivity(), appViewClass);
//
//
//            // Hack para analytics (posição seleccionada)
//            if (adapter.getItem(position) instanceof SearchJson.Results.Apks) {
//                SearchJson.Results.Apks tmp = (SearchJson.Results.Apks) adapter.getItem(position);
//                int pos = getItemPosition(tmp.getMd5sum());
//
//                if (pos != -1)
//                    Analytics.Search.searchPosition(pos + 1);
//            }
//
//            if (adapter.getItem(position) instanceof Cursor) {
//                intent.putExtra("id", id);
//
//            } else {
//
//                intent.putExtra("fromRelated", true);
//                intent.putExtra("repoName", ((SearchJson.Results.Apks) adapter.getItem(position)).getRepo());
//                intent.putExtra("packageName", ((SearchJson.Results.Apks) adapter.getItem(position)).getPackage());
//                intent.putExtra("md5sum", ((SearchJson.Results.Apks) adapter.getItem(position)).getMd5sum());
//                intent.putExtra("download_from", "search_result");
//
//            }
//            startActivity(intent);
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            this.callback = (SearchQueryCallback) activity;
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, final Bundle args) {
//            setListAdapter(null);
//            cursorAdapter = new SearchAdapter(getActivity());
//            adapter = new MergeAdapter();
//            return new SimpleCursorLoader(getActivity()) {
//                @Override
//                public Cursor loadInBackground() {
//                    return new Database(Aptoide.getDb()).getSearchResults(args.getString("query"), sort);
//                }
//            };
            return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, final Cursor data) {
//            cursorAdapter.swapCursor(data);
//            v = LayoutInflater.from(getActivity()).inflate(R.layout.separator_search, null);
//            adapter.addView(v);
//            adapter.addAdapter(cursorAdapter);

            if (isAdded()) {

//                TextView foundResults = (TextView) v.findViewById(R.id.results);
//                more = (TextView) v.findViewById(R.id.more);
//                if (data.getCount() > 0) {
//                    foundResults.setText(getString(R.string.found_results, data.getCount()));
//                } else {
//                    foundResults.setText(getString(R.string.no_search_result_subscribed, query));
//                }
//                setListAdapter(adapter);
//                setListShown(true);
//                setEmptyText(getString(R.string.no_search_result_subscribed, query));
//
//                Map<String, String> searchParams = new HashMap<String, String>();
//                searchParams.put("Search_Query", query);
//                FlurryAgent.logEvent("Search_Results_Searched_For", searchParams);

                Handler handler = new Handler();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            int visibleItems = getListView().getLastVisiblePosition() - getListView().getFirstVisiblePosition();
                            if (((SearchManager) getActivity()).isSearchMoreVisible() && visibleItems < data.getCount()) {
                                more.setVisibility(View.VISIBLE);
                                more.setOnClickListener(((SearchManager) getActivity()).getSearchListener());
                            } else {
                                more.setVisibility(View.GONE);
                            }
                        } catch (IllegalStateException e) {
                            Logger.printException(e);
                        }
                    }
                });
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
//            cursorAdapter.swapCursor(null);
        }


        ProgressBar pb;
        StringBuilder sb = new StringBuilder();

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            getListView().setItemsCanFocus(true);
            getListView().setDivider(null);
            getListView().setCacheColorHint(getResources().getColor(android.R.color.transparent));
//            final ListSearchApkRequest request = new ListSearchApkRequest();

//            request.setSearchString(query);

            if (!AptoideUtils.NetworkUtils.isNetworkAvailable(getActivity())) {
                Bundle bundle = new Bundle();
//                bundle.putString("query", query);
                getLoaderManager().initLoader(60, bundle, SearchFragment.this);
            }


//            GetAdsRequest getAdsRequest = new GetAdsRequest(getActivity());
//
//            getAdsRequest.setLocation("search");
//            getAdsRequest.setKeyword(query);
//            getAdsRequest.setLimit(1);
//
//            manager.execute(getAdsRequest, new RequestListener<ApkSuggestionJson>() {
//                @Override
//                public void onRequestFailure(SpiceException spiceException) {
//                    Logger.d("SearchManager", "onRequestFailure");
//
//                }
//
//                @Override
//                public void onRequestSuccess(ApkSuggestionJson apkSuggestionJson) {
//
//                    if (apkSuggestionJson != null && apkSuggestionJson.getAds() != null && apkSuggestionJson.getAds().size() > 0) {
//
//                        final ApkSuggestionJson.Ads appSuggested = (ApkSuggestionJson.Ads) apkSuggestionJson.getAds().get(0);
//
//                        if (appSuggested.getInfo().getAd_type().equals("app:suggested")) {
////                            Log.d("SearchManager", "onRequestSuccess; app:suggested");
//
//                            ImageView icon = (ImageView) sponsoredApp.findViewById(R.id.app_icon);
//                            TextView name = (TextView) sponsoredApp.findViewById(R.id.app_name);
//                            TextView description = (TextView) sponsoredApp.findViewById(R.id.app_description);
//                            RatingBar rating = (RatingBar) sponsoredApp.findViewById(R.id.app_rating);
//                            TextView downloads = (TextView) sponsoredApp.findViewById(R.id.app_downloads);
//
//                            ImageLoader.getInstance().displayImage(appSuggested.getData().getIcon(), icon);
//
//                            name.setText(appSuggested.getData().getName());
//                            description.setText(Html.fromHtml(appSuggested.getData().getDescription()).toString());
//                            rating.setRating(appSuggested.getData().getStars().floatValue());
//                            String down = String.valueOf(appSuggested.getData().getDownloads().intValue());
//                            downloads.setText(getString(R.string.X_download_number, withSuffix(down)));
//                            sponsoredApp.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    FlurryAgent.logEvent("Home_Page_Clicked_On_Sponsored_App");
//                                    Intent i = new Intent(getActivity(), appViewClass);
//                                    long id = appSuggested.getData().getId().longValue();
//                                    i.putExtra("id", id);
//                                    i.putExtra("packageName", appSuggested.getData().getPackageName());
//                                    i.putExtra("repoName", appSuggested.getData().getRepo());
//                                    i.putExtra("fromSponsored", true);
//                                    i.putExtra("location", "homepage");
//                                    i.putExtra("keyword", "__NULL__");
//                                    i.putExtra("cpc", appSuggested.getInfo().getCpc_url());
//                                    i.putExtra("cpi", appSuggested.getInfo().getCpi_url());
//                                    i.putExtra("whereFrom", "sponsored");
//                                    i.putExtra("download_from", "sponsored");
//
//                                    if (appSuggested.getPartner() != null) {
//                                        Bundle bundle = new Bundle();
//
//                                        bundle.putString("partnerType", appSuggested.getPartner().getPartnerInfo().getName());
//                                        bundle.putString("partnerClickUrl", appSuggested.getPartner().getPartnerData().getClick_url());
//
//                                        i.putExtra("partnerExtra", bundle);
//                                    }
//
//                                    startActivity(i);
//                                }
//                            });
//
//                            adapter.setActive(sponsoredApp, true);
//                        } else if (appSuggested.getInfo().getAd_type().equals("url:googleplay")) {
////                            Log.d("SearchManager", "onRequestSuccess; url:googleplay");
//
//                            ImageView banner = (ImageView) sponsoredAdApp.findViewById(R.id.app_ad_banner);
//                            ImageLoader.getInstance().displayImage(appSuggested.getData().getImage(), banner);
//
//                            banner.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    FlurryAgent.logEvent("Search_Results_Clicked_On_Sponsored_Google_Play_Link");
//                                    try {
//                                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(appSuggested.getData().getUrl()));
//                                        List<ResolveInfo> resolveInfos = getActivity().getPackageManager().queryIntentActivities(i, 0);
//                                        String activityToOpen = "";
//                                        for (ResolveInfo resolveInfo : resolveInfos) {
//                                            if (resolveInfo.activityInfo.packageName.equals("com.android.vending")) {
//                                                activityToOpen = resolveInfo.activityInfo.name;
//                                            }
//                                        }
//                                        i.setClassName("com.android.vending", activityToOpen);
//                                        startActivity(i);
//                                    } catch (ActivityNotFoundException e) {
//                                        e.printStackTrace();
//
//                                        Intent i = new Intent(getActivity(), Aptoide.getConfiguration().getSearchActivityClass());
//                                        String param = appSuggested.getData().getUrl().split("=")[1];
//                                        i.putExtra(android.app.SearchManager.QUERY, param);
//                                        startActivity(i);
//                                    }
//                                }
//                            });
//
//                            adapter.setActive(sponsoredAdLabel, true);
//                            adapter.setActive(sponsoredAdApp, true);
//
//                        } else if (appSuggested.getInfo().getAd_type().equals("url:banner")) {
//                            Logger.d("SearchManager", "onRequestSuccess; url:banner");
//
//                            ImageView banner = (ImageView) sponsoredAdApp.findViewById(R.id.app_ad_banner);
//                            ImageLoader.getInstance().displayImage(appSuggested.getData().getImage(), banner);
//
//                            banner.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    FlurryAgent.logEvent("Search_Results_Clicked_On_Sponsored_Banner_Link");
//                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(appSuggested.getData().getUrl()));
//                                    startActivity(intent);
//                                }
//                            });
//
//                            adapter.setActive(sponsoredAdLabel, true);
//                            adapter.setActive(sponsoredAdApp, true);
//                        }
//
//
//                    }
//                }
//            });
//
//
//            sb.setLength(0);
//            Cursor c = new Database(Aptoide.getDb()).getServers();
//            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
//                sb.append(c.getString(c.getColumnIndex("name")));
//            }
//            c.close();
//
//            boolean matureCheck = PreferenceManager.getDefaultSharedPreferences(Aptoide.getContext()).getBoolean("matureChkBox", true);
//
//            manager.execute(request, query + sb.toString().hashCode() + matureCheck, DurationInMillis.ONE_HOUR, new RequestListener<SearchJson>() {
//                @Override
//                public void onRequestFailure(SpiceException spiceException) {
//                    Bundle bundle = new Bundle();
//                    bundle.putString("query", query);
//                    getLoaderManager().initLoader(60, bundle, SearchFragment.this);
//                }
//
//                @Override
//                public void onRequestSuccess(SearchJson searchJson) {
//                    if (searchJson == null) {
//                        return;
//                    }
//
//                    if ("FAIL".equals(searchJson.getStatus())) {
//                        for (cm.aptoide.ptdev.model.Error error : searchJson.getErrors()) {
//                            Integer errorCode = Errors.getErrorsMap().get(error.getCode());
//                            String errorMsg;
//                            if (errorCode != null) {
//                                errorMsg = getString(errorCode);
//                            } else {
//                                errorMsg = error.getMsg();
//                            }
//                            if (getActivity() != null) {
//                                getActivity().finish();
//                            }
//
//                            Toast.makeText(Aptoide.getContext(), errorMsg, Toast.LENGTH_LONG).show();
//                        }
//                        return;
//                    }
//
//                    /*u_items.clear();
//                    u_items.addAll(searchJson.getResults().getU_Apks());
//                    if(u_items.size()>0) {
//                        adapter.addAdapter(searchAdapteruapks);
//                        TextView foundUResults = (TextView) v.findViewById(R.id.resultsU);
//                        foundUResults.setVisibility(View.VISIBLE);
//                    }*/
//
//
//                    LinearLayout didyoumeanContainer = (LinearLayout) searchLayout.findViewById(R.id.didyoumeancontainer);
//                    LinearLayout usearchContainer = (LinearLayout) v.findViewById(R.id.container);
//                    LinearLayout usearchContainer2 = (LinearLayout) v2.findViewById(R.id.container);
//
//
//                    adapter.setActive(searchResultsLabel, true);
//
//                    if (searchResultsLabel != null && searchJson.getResults().getApks().isEmpty()) {
//                        ((TextView) searchResultsLabel.findViewById(R.id.results)).setText(getString(R.string.no_search_result_subscribed, query));
//                    }
//
//
//                    didyoumeanContainer.removeAllViews();
//
//                    final String sizeString = IconSizes.generateSizeString(getActivity());
//
//                    for (final String s : searchJson.getResults().getDidyoumean()) {
//                        //Log.d("didyou", s);
//                        TextView tv = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.row_app_search_did_you_mean, null);
//
//                        SpannableString content = new SpannableString(s);
//                        content.setSpan(new UnderlineSpan(), 0, s.length(), 0);
//                        tv.setText(content);
//                        tv.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                FlurryAgent.logEvent("Search_Results_Clicked_On_Did_You_Mean_Recommendation");
//
//                                Bundle args = new Bundle();
//
//                                SearchRecentSuggestions suggestions = new SearchRecentSuggestions(getActivity(), Aptoide.getConfiguration().getSearchAuthority(), 1);
//                                suggestions.saveRecentQuery(s, null);
//
//                                args.putString("query", s);
//
//                                ((SearchManager) getActivity()).getSupportActionBar().setTitle("'" + s + "'");
//                                callback.setQuery(s);
//                                Fragment fragment = new SearchFragment();
//                                fragment.setArguments(args);
//                                getFragmentManager()
//                                        .beginTransaction()
//                                        .addToBackStack(null)
//                                        .replace(R.id.fragContainer, fragment)
//                                        .commit();
//                            }
//                        });
//                        didyoumeanContainer.addView(tv);
//                    }
//
//                    fillUApks(searchJson, usearchContainer, sizeString);
//                    fillUApks(searchJson, usearchContainer2, sizeString);
//
//
//                    if (!items.isEmpty()) {
//                        items.clear();
//                    }
//
//                    if (!items2.isEmpty()) {
//                        items2.clear();
//                    }
//
//
//                    for (int i = 0; i < searchJson.getResults().getApks().size(); i++) {
//
//                        if (i < 10) {
//                            items.add(searchJson.getResults().getApks().get(i));
//                        } else {
//                            items2.add(searchJson.getResults().getApks().get(i));
//                        }
//
//                    }
//
//                    LinkedList<String> tmp = new LinkedList<>();
//                    for (SearchJson.Results.Apks item : items) {
//                        tmp.add(item.getPackage());
//                    }
//
////                    Analytics.SearchOld.topSearchList(query);
//                    Analytics.Search.searchTerm(query);
//
//
//                    int getDidyoumeanSize = searchJson.getResults().getDidyoumean().size();
//                    int uapksSize = searchJson.getResults().getU_Apks().size();
//
//
//                    if (uapksSize > 0 && ((SearchManager) getActivity()).isSearchMoreVisible()) {
//                        hasUapks = true;
//                        adapter.setActive(v, true);
//                    }
//
//
//                    //adapter.notifyDataSetChanged();
//
//
//                    if (getDidyoumeanSize > 0) {
//                        adapter.setActive(searchLayout, true);
//                    } else {
//                        adapter.setActive(searchLayout, false);
//                    }
//
//                    //positionsub = -1;
//                    //Log.d("SearchManager", "Adding Header View");
//
//
//                    //getListView().addHeaderView(searchLayout, null, false);
//
//
//                    if (isAdded()) {
//
//                        more = (TextView) v.findViewById(R.id.more);
//                        //foundResults.setText(getString(R.string.found_results, searchAdapterapks.getCount()));
//
//                        setListShown(true);
//                        setEmptyText(getString(R.string.no_search_result_subscribed, query));
//                        adapter.setActive(v, true);
//
//                    }
//
//                    getListView().setOnScrollListener(new AbsListView.OnScrollListener() {
//                        @Override
//                        public void onScrollStateChanged(AbsListView view, int scrollState) {
//
//                        }
//
//                        @Override
//                        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//
//                            int lastInScreen = firstVisibleItem + visibleItemCount;
//                            if ((lastInScreen + 5 >= totalItemCount && !loading)) {
//
//                                loading = true;
//                                ListSearchApkRequest request1 = new ListSearchApkRequest();
//                                request1.setSearchString(query);
//                                request1.setOffset(items.size() + items2.size());
//                                adapter.setActive(pb, true);
//
//                                manager.execute(request1, query + sb.toString().hashCode() + items.size() + items2.size(), DurationInMillis.ONE_HOUR, new RequestListener<SearchJson>() {
//
//                                    @Override
//                                    public void onRequestFailure(SpiceException spiceException) {
//                                        loading = false;
//                                        adapter.setActive(pb, false);
//                                    }
//
//                                    @Override
//                                    public void onRequestSuccess(SearchJson searchJson) {
//
//                                        if (!"FAIL".equals(searchJson.getStatus())) {
//                                            items2.addAll(searchJson.getResults().getApks());
//                                            if (!searchJson.getResults().getApks().isEmpty()) {
//                                                adapter.notifyDataSetChanged();
//                                                loading = false;
//                                            } else if (items2.size() > 9 && hasUapks) {
//                                                adapter.setActive(v2, true);
//                                            }
//
//                                            adapter.setActive(pb, false);
//                                        } else {
//                                            loading = false;
//                                            adapter.setActive(pb, false);
//                                        }
//
//
//                                    }
//                                });
//                            }
//
//                        }
//                    });
//
//
//                    if (getListView().getAdapter() == null) {
//                        setListAdapter(adapter);
//                    }
//
//
//                }
//
//
//            });

        }
//
//        private void fillUApks(SearchJson searchJson, LinearLayout usearchContainer, String sizeString) {
//
//            if (usearchContainer != null && searchJson != null) {
//
//
//                usearchContainer.removeAllViews();
//                for (SearchJson.Results.Apks apk : searchJson.getResults().getU_Apks()) {
//                    View element = LayoutInflater.from(getActivity()).inflate(R.layout.row_app_search_result_other, usearchContainer, false);
//                    ImageView app_icon = (ImageView) element.findViewById(R.id.app_icon);
//
//                    String iconUrl = apk.getIcon();
//
//                    if (iconUrl.contains("_icon")) {
//                        String[] splittedUrl = iconUrl.split("\\.(?=[^\\.]+$)");
//                        iconUrl = splittedUrl[0] + "_" + sizeString + "." + splittedUrl[1];
//                    }
//                    ImageLoader.getInstance().displayImage(iconUrl, app_icon);
//
//                    TextView app_name = (TextView) element.findViewById(R.id.app_name);
//                    app_name.setText(apk.getName() + " - " + apk.getVername());
//
//                    usearchContainer.addView(element);
//
//                }
//            }
//        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
//            outState.putInt("offset", items.size());
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            getLoaderManager().destroyLoader(60);
        }

        @Override
        public void onResume() {
            super.onResume();
//            ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle("'" + query + "'");
        }

        @Override
        public void onDetach() {
            super.onDetach();
        }

    }

    public boolean isSearchMoreVisible() {
        return true;
    }

    private View.OnClickListener getSearchListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
//                    FlurryAgent.logEvent("Search_Results_Clicked_On_Search_More_Button");
//                    String url = Aptoide.getConfiguration().getUriSearch() + query + "&q=" + Utils.filters(SearchManager.this);
//                    Intent i = new Intent(Intent.ACTION_VIEW);
//                    url = url.replaceAll(" ", "%20");
//                    i.setData(Uri.parse(url));
//                    startActivity(i);

                } catch (ActivityNotFoundException e) {
                    Toast.makeText(Aptoide.getContext(), getString(R.string.error_occured), Toast.LENGTH_LONG).show();
                }

            }
        };
    }

    public View getFooterView(int res) {
        View footer = LayoutInflater.from(this).inflate(res, null);
//        TextView search = (TextView) footer.findViewById(R.id.search);
//        search.setOnClickListener(getSearchListener());
        return footer;
    }

    public void installApp(long id) {
        downloadService.startDownloadFromAppId(id);
    }

    private ServiceConnection conn2 = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            downloadService = ((DownloadService.LocalBinder) binder).getService();
            BusProvider.getInstance().post(new OttoEvents.DownloadServiceConnected());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onDestroy() {

        if (downloadService != null) {
            unbindService(conn2);
        }
        super.onDestroy();
    }

    @Override
    protected String getScreenName() {
        return "Search Manager";
    }

    private static boolean isSocketDisconnect;

    public static void setupSearch(Menu menu, final Activity activity) {
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        final android.app.SearchManager searchManager = (android.app.SearchManager) activity.getSystemService(Context.SEARCH_SERVICE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                MenuItemCompat.collapseActionView(searchItem);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus) {
                    MenuItemCompat.collapseActionView(searchItem);
                    isSocketDisconnect = true;

                    if (Build.VERSION.SDK_INT > 7) {

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                if (isSocketDisconnect) {
                                    WebSocketSingleton.getInstance().disconnect();
                                }

                            }
                        }, 10000);
                    }
                }
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSocketDisconnect = false;

//                FlurryAgent.logEvent("Clicked_On_Search_Button");

                if (Build.VERSION.SDK_INT > 7) {
                    WebSocketSingleton.getInstance().connect();
                } else {
                    activity.onSearchRequested();
                    MenuItemCompat.collapseActionView(searchItem);
                }
            }
        });


        if (Build.VERSION.SDK_INT > 7) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(activity.getComponentName()));
        }
    }

}

interface SearchQueryCallback {
    void setQuery(String query);
}
