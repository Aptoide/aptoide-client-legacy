package com.aptoide.amethyst;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.aptoide.amethyst.dialogs.AptoideDialog;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.dataprovider.AptoideSpiceHttpService;
import com.aptoide.dataprovider.webservices.json.GenericResponseV2;
import com.bumptech.glide.Glide;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;
import java.util.List;

import com.aptoide.amethyst.adapter.DividerItemDecoration;
import com.aptoide.amethyst.webservices.MakeReviewRequest;

/**
 * Created by fabio on 22-10-2015.
 */
public class MakeReviewActivity extends AptoideBaseActivity {
    private static final int RATINGS_COUNT = 4;
    private static final int RATINGS_SPEED = 0;
    private static final int RATINGS_USABILITY = 1;
    private static final int RATINGS_ADDICTIVE = 2;
    private static final int RATINGS_STABILITY = 3;
    private static final String RATINGS_VALUES = "RV";
    private static final String RATINGS_AVG = "AVG";

    private static final int[] PRO_IDS = {R.id.Pro1, R.id.Pro2,R.id.Pro3};
    private static final int[] CON_IDS = {R.id.Con1,R.id.Con2,R.id.Con3};

    public static final String EXTRA_PACKAGE = "PACKAGE";
    public static final String EXTRA_SCREENSHOTS_URL = "SCREENSHOTS";
    public static final String EXTRA_REPO = "ERS";
    public static final String EXTRA_ICON = "EICON";
    public static final String EXTRA_APP_NAME = "EAPPNAME";
    public static final String EXTRA_SIZE = "ESIZE";
    public static final String EXTRA_DOWNLOADS = "EDOWNLOADS";
    public static final String EXTRA_STARS = "ESTARS";

    int[] ratingValues = new int[RATINGS_COUNT];
    double avg;

    private TextView scoreTV;
    private String scoreString;


    private SpiceManager spiceManager = new SpiceManager(AptoideSpiceHttpService.class);
    Toolbar mToolbar;

    @Override
    protected void onPause() {
        super.onPause();
        spiceManager.shouldStop();
    }

//    @Override

    @Override
    protected void onResume() {
        super.onResume();
        spiceManager.start(this);
    }

    RecyclerView screenshots;


    public static class ScreenshotsViewHolder extends RecyclerView.ViewHolder{

        public ImageView screenshot;


        public ScreenshotsViewHolder(View itemView) {
            super(itemView);

            screenshot = (ImageView) itemView.findViewById(R.id.screenshot_image_item);

        }

    }
    public static class ScreenshotsAdapter extends RecyclerView.Adapter<ScreenshotsViewHolder>{

        private ArrayList<String> screenshotsUrls = new ArrayList<>();
        public ScreenshotsAdapter(ArrayList<String> screenshotsUrls) {

            this.screenshotsUrls = screenshotsUrls;

        }

        @Override
        public ScreenshotsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            Context context = parent.getContext();

            View inflate = LayoutInflater.from(context).inflate(R.layout.row_item_screenshots_gallery, parent, false);

            return new ScreenshotsViewHolder(inflate);
        }

        @Override
        public void onBindViewHolder(ScreenshotsViewHolder holder, int position) {
            Glide.with(holder.itemView.getContext()).load(screenshotsUrls.get(position)).crossFade().into(holder.screenshot);
        }

        @Override
        public int getItemCount() {
            return screenshotsUrls.size();
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Aptoide.getThemePicker().setAptoideTheme(this);
        super.onCreate(savedInstanceState);

        setContentView(getContentView());
        bindViews();

        mToolbar.setCollapsible(false);

        setSupportActionBar(mToolbar);
        scoreTV = (TextView) findViewById(R.id.finalScore);
        scoreString = getString(R.string.review_final_score);

        screenshots = (RecyclerView) findViewById(R.id.layout_screenshots);


        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(R.string.make_review_title);

        int pixels = AptoideUtils.getPixels(this, 5);

        screenshots.addItemDecoration(new DividerItemDecoration(pixels));
        screenshots.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        screenshots.setAdapter(new ScreenshotsAdapter(getIntent().getStringArrayListExtra(EXTRA_SCREENSHOTS_URL)));

        Glide.with(this).load(getIntent().getStringExtra(EXTRA_ICON)).into((ImageView) findViewById(R.id.icon));

        ((TextView) findViewById(R.id.name)).setText(getIntent().getStringExtra(EXTRA_APP_NAME));
        String text = getString(R.string.size) + ": " + getIntent().getLongExtra(EXTRA_SIZE,0);
        ((TextView) findViewById(R.id.text1)).setText(text);
        text = getString(R.string.downloads) + ": " + getIntent().getIntExtra(EXTRA_DOWNLOADS,0);
        ((TextView) findViewById(R.id.text2)).setText(text);

        ((RatingBar) findViewById(R.id.app_rating)).setRating(getIntent().getFloatExtra(EXTRA_STARS,0.0f));

        if(savedInstanceState != null ) {
            ratingValues=savedInstanceState.getIntArray(RATINGS_VALUES);
            avg=savedInstanceState.getDouble(RATINGS_AVG);
            updateScoreUI();
        }else{
            for (int i = 0; i < RATINGS_COUNT; i++) {
                ratingValues[i]=0;
            }
        }
        setupSeekBar(RATINGS_SPEED,R.string.review_speed,R.id.Seek_Bar_Speed);
        setupSeekBar(RATINGS_USABILITY,R.string.review_usability,R.id.Seek_Bar_Usability);
        setupSeekBar(RATINGS_ADDICTIVE,R.string.review_addictive,R.id.Seek_Bar_Funny);
        setupSeekBar(RATINGS_STABILITY,R.string.review_stability,R.id.Seek_Bar_Stability);
    }

    protected int getContentView() {
        return R.layout.activity_make_review;
    }

    protected void bindViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray(RATINGS_VALUES,ratingValues);
        outState.putDouble(RATINGS_AVG,avg);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if(item.getItemId() == android.R.id.home || item.getItemId() == R.id.home){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupSeekBar(final int valuesPos,int title, int id){
        View v = findViewById(id);
        SeekBar seekbar = (SeekBar) v.findViewById(R.id.seek_bar_on_row);
        TextView titleTV = (TextView) v.findViewById(R.id.seek_bar_name);
        final TextView seek_bar_value = (TextView) v.findViewById(R.id.seek_bar_value);
        titleTV.setText(title);

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress+=1;
                if(fromUser){
                    seek_bar_value.setText(String.valueOf(progress));
                    ratingValues[valuesPos]=progress;
                    updateScore();
                }else if(ratingValues[valuesPos] != 0){
                    seekBar.setProgress(ratingValues[valuesPos]);
                }
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        seekbar.setProgress(ratingValues[valuesPos]);
        seek_bar_value.setText(String.valueOf(ratingValues[valuesPos]));
    }

    private void updateScore(){
        int sum = 0;
        for (int i = 0; i < RATINGS_COUNT; i++) {
            sum+= ratingValues[i];
        }
        avg = (double) sum/RATINGS_COUNT;
        updateScoreUI();
    }

    private void updateScoreUI(){
        if(scoreTV==null){
            scoreTV = (TextView) findViewById(R.id.finalScore);
        }
        scoreTV.setText(String.valueOf(avg));
    }

    private List<String> getStrings(int [] ids){
        ArrayList<String> ret = new ArrayList<>(ids.length);
        for (int i = 0; i < ids.length; i++) {
            EditText et = (EditText) findViewById(ids[i]);
            String s = et.getText().toString();
            if(s.length()>0)ret.add(s);
        }
        return ret;
    }

    public void finishButtonClick(View view){
        //TODO Strings
        if(avg<1) {
            /*If true the sliders were never moved...*/
            AptoideUtils.UI.toast(getString(R.string.set_sliders));
            return;
        }

        List<String> pros = getStrings(PRO_IDS);
        if(pros.size()==0){
            AptoideUtils.UI.toast(getString(R.string.write_one_pro));
            return;
        }
        List<String> cons = getStrings(CON_IDS);
        if(cons.size()==0){
            AptoideUtils.UI.toast(getString(R.string.write_one_con));
            return;
        }
        String final_verdict = ((EditText) findViewById(R.id.make_review_final_verdict)).getText().toString();
        if(final_verdict.length()<=1){
            AptoideUtils.UI.toast(getString(R.string.write_your_final_verdict));
            return;
        }



        for(String pro : pros){
            Log.d("pois", "pro: "+pro );
        }
        for(String pro : cons){
            Log.d("pois", "con: "+pro );
        }


        for (int i = 0; i < ratingValues.length; i++) {
            Log.d("pois", "values ["+i+"] =   "+ ratingValues[i] );
        }

        MakeReviewRequest request = new MakeReviewRequest();
        request.setPackage_name(getIntent().getStringExtra(EXTRA_PACKAGE));
        request.setRepoName( getIntent().getStringExtra(EXTRA_REPO) );
        request.setPerformance(ratingValues[RATINGS_SPEED]);
        request.setUsability(ratingValues[RATINGS_USABILITY]);
        request.setAddiction(ratingValues[RATINGS_ADDICTIVE]);
        request.setStability(ratingValues[RATINGS_STABILITY]);

        MakeReviewRequest.ReviewPost.Locale en_GB = new MakeReviewRequest.ReviewPost.Locale("en_GB");
        en_GB.setCons(cons);
        en_GB.setPros(pros);
        en_GB.setFinalVerdict(final_verdict);
        request.addLocale(en_GB);


        AptoideDialog.pleaseWaitDialog().show(getSupportFragmentManager(), "pleaseWaitDialog");

        spiceManager.execute(request,new RequestListener<GenericResponseV2>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                AptoideUtils.UI.toast(getString(R.string.error_occured));

            }

            @Override
            public void onRequestSuccess(GenericResponseV2 genericResponseV2) {
                DialogFragment pleaseWaitDialog = (DialogFragment) getSupportFragmentManager().findFragmentByTag("pleaseWaitDialog");
                if(pleaseWaitDialog!=null){
                    pleaseWaitDialog.dismiss();
                }
                AptoideUtils.UI.toast(getString(R.string.review_success));
                finish();
            }
        });
    }

    //    }
//        return "Make Review";
//    protected String getScreenName() {

    @Override
    protected String getScreenName() {
        return "Make Review";
    }
}
