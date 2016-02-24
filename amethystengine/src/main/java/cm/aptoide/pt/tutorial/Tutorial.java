package cm.aptoide.pt.tutorial;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.AptoideBaseActivity;
import com.aptoide.amethyst.R;
import com.aptoide.amethyst.analytics.Analytics;
import com.flurry.android.FlurryAgent;

import java.util.ArrayList;
import java.util.Iterator;



/**
 * Created with IntelliJ IDEA.
 * User: brutus
 * Date: 28-10-2013
 * Time: 13:11
 * To change this template use File | Settings | File Templates.
 */
public class Tutorial extends AptoideBaseActivity {

    private int currentFragment;
    private int lastFragment;
    private ArrayList<Fragment> wizard_fragments;
    private ArrayList<Action> actionsToExecute = new ArrayList<Action>();
    private Button next, back;
    private boolean addDefaultRepo = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Aptoide.getThemePicker().setAptoideTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_tutorial);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar_login);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        /*if(getIntent().hasExtra("isUpdate")){
            //getSupportActionBar().setDisplayShowTitleEnabled(true);
            //getSupportActionBar().setTitle(R.string.whats_new);
            wizard_fragments = Wizard.getWizardUpdate();
            addDefaultRepo = false;
        }else{*/
            //getSupportActionBar().setDisplayShowTitleEnabled(true);
            //getSupportActionBar().setTitle(R.string.app_name);
            //getSupportActionBar().setLogo(R.drawable.ic_aptoide_toolbar);
            wizard_fragments = Wizard.getWizardNewToAptoide();
        //}

        if (wizard_fragments.isEmpty()) {
            Log.e("Wizard", "The wizard doesn't have fragments");
            finish();
        }


        lastFragment = wizard_fragments.size() - 1;

        /*wAdapter = new WizardAdapter(getSupportFragmentManager(), wizard_fragments);

        wPager = (ViewPager) findViewById(R.id.pager);

        wPager.setAdapter(wAdapter);
        */

        next = (Button) findViewById(R.id.next);
        next.setOnClickListener(getNextListener());

        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(getBackListener());

//        ((Button) findViewById(R.id.finish)).setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                if (currentFragment == lastFragment) {
//                    getFragmentsActions();
//
//                    runFragmentsActions();
//                }
//
//                finish();
//            }
//        });


        if (savedInstanceState == null) {
            Fragment firstFragment = wizard_fragments.get(0);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.wizard_fragment, firstFragment);
            ft.commit();

        }else{
            currentFragment = savedInstanceState.getInt("currentFragment");
        }


    }



    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void finish() {

        if(addDefaultRepo){
            FlurryAgent.logEvent("Wizard_Added_Apps_As_Default_Store");
            Intent data = new Intent();
            data.putExtra("addDefaultRepo", true);
            setResult(RESULT_OK, data);
            Log.d("Tutorial-addDefaultRepo", "true");
        }else{
            FlurryAgent.logEvent("Wizard_Did_Not_Add_Apps_As_Default_Store");
        }

        super.finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected String getScreenName() {
        return "Tutorial";
    }

    private View.OnClickListener getBackListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FlurryAgent.logEvent("Wizard_Clicked_On_Back_Button");
                if (currentFragment != 0) {
                    changeFragment(--currentFragment);
                }
            }
        };
    }

    private View.OnClickListener getNextListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FlurryAgent.logEvent("Wizard_Clicked_On_Next_Button");
                if (currentFragment != lastFragment) {
                    changeFragment(++currentFragment);
                }else{
                    finish();
                }
            }
        };
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentFragment", currentFragment);
    }

    private void changeFragment(int toPage) {
        if (back != null) {
            if (toPage == 0) {
                back.setVisibility(View.GONE);
            } else {
                back.setVisibility(View.VISIBLE);
            }
        }
        Fragment fragment = wizard_fragments.get(toPage);
        if (!fragment.isAdded()) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.wizard_fragment, fragment);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            //ft.addToBackStack(null);
            ft.commit();
        }
    }

    private void getFragmentsActions() {
        Iterator<Fragment> iterator = wizard_fragments.iterator();
        Wizard.WizardCallback wizardCallback;
        while (iterator.hasNext()) {
            wizardCallback = (Wizard.WizardCallback) iterator.next();
            wizardCallback.getActions(actionsToExecute);
        }
    }

    private void runFragmentsActions() {

        Iterator<Action> iterator = actionsToExecute.iterator();
        Action action;
        while (iterator.hasNext()) {
            action = iterator.next();
            action.run();
        }

    }


    public void setAddDefaultRepo(boolean addDefaultRepo){
        this.addDefaultRepo = addDefaultRepo;
    }

    /*
    public class WizardAdapter extends FragmentPagerAdapter {

        ArrayList<SherlockFragment> fragments;

        public WizardAdapter(FragmentManager fm, ArrayList<SherlockFragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int i) {
            return fragments.get(i);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

    } */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_wizard, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.menu_skip) {
            FlurryAgent.logEvent("Wizard_Skipped_Initial_Wizard");

            if (currentFragment == lastFragment) {
                getFragmentsActions();
                runFragmentsActions();
            }
            Analytics.Tutorial.finishedTutorial(currentFragment + 1);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
