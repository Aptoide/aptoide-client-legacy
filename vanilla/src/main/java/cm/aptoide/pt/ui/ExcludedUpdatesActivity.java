package cm.aptoide.pt.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;

import com.aptoide.amethyst.AptoideBaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import cm.aptoide.pt.R;
import cm.aptoide.pt.fragments.ExcludedUpdatesFragment;

public class ExcludedUpdatesActivity extends AptoideBaseActivity {

    @Bind(R.id.toolbar)    Toolbar mToolbar;
    @Bind(R.id.content)    FrameLayout content;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Aptoide.getThemePicker().setAptoideTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_layout);

        ButterKnife.bind(this);

        mToolbar.setCollapsible(false);


        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        // AppBarLayout$ScrollingViewBehavior issue
//        content.setVisibility(View.GONE);
//        contentNoBehaviour.setVisibility(View.VISIBLE);

        // Apenas se nao for uma orientation change
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.content, ExcludedUpdatesFragment.newInstance(), "").commit();
        }




    }

    @Override
    protected String getScreenName() {
        return "Excluded Updates";
    }
}