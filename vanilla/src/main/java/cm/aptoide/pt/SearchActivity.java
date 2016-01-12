package cm.aptoide.pt;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.analytics.Analytics;
import com.aptoide.amethyst.models.EnumStoreTheme;
import com.aptoide.amethyst.utils.AptoideUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import cm.aptoide.pt.fragments.SearchFragment;

/**
 * Created by rmateus on 12/06/15.
 */
public class SearchActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)    Toolbar mToolbar;
    public static final String CONTEXT = "Search";
    public static final String SEARCH_SOURCE = "search_source";
    public static final String SEARCH_THEME = "search_theme";

    private static final String ARG_SECONDARY = "arg_secondary";


    /**
     * Indicates whether this search activity is secondary. If it is, it should be replaced with
     * the next search takes place.
     */
    private boolean secondary;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Aptoide.getThemePicker().setAptoideTheme(this);
        super.onCreate(savedInstanceState);
        EnumStoreTheme storeTheme = null;
        try {
            storeTheme = (EnumStoreTheme) getIntent().getExtras().get(SEARCH_THEME);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        String storeName = getIntent().getStringExtra(SEARCH_SOURCE);
		String query = getIntent().getExtras().getString(SearchManager.QUERY);

		Analytics.Search.searchTerm(query, storeName);

		if (storeName != null && !TextUtils.isEmpty(storeName)) {
			Intent intent = new Intent(SearchActivity.this, MoreSearchActivity.class);
			intent.putExtra(MoreSearchActivity.QUERY_BUNDLE_KEY, query);
			intent.putExtra(SEARCH_SOURCE, storeName);
			intent.putExtra(SEARCH_THEME, storeTheme);
			startActivity(intent);
			finish();
			return;
		}
		setContentView(R.layout.activity_fragment_layout);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        ActionBar supportActBar = getSupportActionBar();
        if (supportActBar != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setLogo(R.drawable.ic_aptoide_toolbar);
        }

        setTitle(AptoideUtils.StringUtils.getFormattedString(this, R.string.search_activity_title, query));

        if(savedInstanceState==null) {
            getSupportFragmentManager().beginTransaction().add(R.id.content, SearchFragment.newInstance(getIntent().getStringExtra(SearchManager.QUERY)), "").commit();
        }

		secondary = getIntent().getBooleanExtra(ARG_SECONDARY, false);
	}

    @Override
    public void startActivity(final Intent intent) {
        final ComponentName name = intent.getComponent();
        if (name != null && SearchActivity.class.getName().equals(name.getClassName())) {
            intent.putExtra(ARG_SECONDARY, true);
            if (secondary) {
                finish();
            }
        }
        super.startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.simple_search_menu, menu);
        cm.aptoide.pt.ui.SearchManager.setupSearch(menu, this);
//        setupSearch(menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if(item.getItemId() == R.id.home || item.getItemId() == android.R.id.home){
            finish();
        }


        return super.onOptionsItemSelected(item);
    }
}
