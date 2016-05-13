package com.aptoide.amethyst.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.AptoideBaseActivity;
import com.aptoide.amethyst.R;
import com.aptoide.amethyst.analytics.Analytics;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.amethyst.utils.Configs;
import com.aptoide.amethyst.utils.Logger;
import com.aptoide.dataprovider.AptoideSpiceHttpService;
import com.aptoide.dataprovider.webservices.GetReviews;
import com.aptoide.dataprovider.webservices.json.review.Review;
import com.aptoide.dataprovider.webservices.json.review.ReviewJson;
import com.aptoide.dataprovider.webservices.models.Constants;
import com.aptoide.dataprovider.webservices.models.ErrorResponse;
import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.aptoide.amethyst.AppViewActivity;

import com.aptoide.amethyst.ui.widget.CircleTransform;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

/**
 * Created by fabio on 21-10-2015.
 */
public class ReviewActivity extends AptoideBaseActivity {
    private static final String TAG = ReviewActivity.class.getSimpleName();
    SpiceManager manager = new SpiceManager(AptoideSpiceHttpService.class);
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM y", Locale.getDefault());

    Toolbar mToolbar;

    private PieChartData speedData;
    private PieChartView speedChart;

    private PieChartData usabilityData;
    private PieChartView usabilityChart;

    private PieChartData addictiveData;
    private PieChartView addictiveChart;

    private PieChartData stabilityData;
    private PieChartView stabilityChart;

    private TextView title;
    private TextView finalVeredict;
    private TextView reviewer;
    private TextView rating;
    private ImageView appIcon;
    private ImageView avatar;
    private LinearLayout consContainer;
    private LinearLayout prosContainer;
    private ImageView bigImage;

    ArrayList<ImageView> screenshots;

    private TextView speedLabel;
    private TextView usabilityLabel;
    private TextView addictiveLabel;
    private TextView stabilityLabel;
    private TextView consLabel;
    private TextView prosLabel;
    private TextView vername_date;


    private void showLoading(){
        findViewById(android.R.id.empty).setVisibility(View.VISIBLE);
        findViewById(R.id.content).setVisibility(View.GONE);
    }

    private void showContent(){
        findViewById(android.R.id.empty).setVisibility(View.GONE);
        findViewById(R.id.content).setVisibility(View.VISIBLE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Aptoide.getThemePicker().setAptoideTheme(this);
        super.onCreate(savedInstanceState);

        setContentView(getContentView());
        bindViews();

        mToolbar.setCollapsible(false);

        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.review_title));
        init();

        int aptoideTheme = Aptoide.getThemePicker().getAptoideTheme(this);

        if (aptoideTheme == R.style.AptoideThemeDefault) {
            getWindow().getDecorView().setBackgroundColor(Color.WHITE);
        } else {
            getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.transparent_black));
            consLabel.setBackgroundColor(getResources().getColor(R.color.dark_gray));
            prosLabel.setBackgroundColor(getResources().getColor(R.color.dark_gray));
            vername_date.setBackgroundColor(getResources().getColor(R.color.dark_gray));
        }

        GetReviews.GetReview reviewsRequest = new GetReviews.GetReview();
        final int id = getIntent().getIntExtra("review_id", 0);
        reviewsRequest.setId(id);
        reviewsRequest.setDensity(AptoideUtils.HWSpecifications.getScreenDensity());
        showLoading();
        manager.execute(reviewsRequest, "review-id-" + id, DurationInMillis.ONE_HOUR, new RequestListener<ReviewJson>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {

            }

            @Override
            public void onRequestSuccess(final ReviewJson reviewListJson) {
                if (reviewListJson.errors != null && reviewListJson.errors.size() > 0) {
                    Toast.makeText(ReviewActivity.this, getResources().getString(R.string.simple_error_occured), Toast.LENGTH_SHORT).show();
                    Crashlytics.setLong("ReviewID", id);
                    String response = "";
                    for (ErrorResponse error : reviewListJson.errors) {
                        response += "code: " + error.code + "\tmsg: " + error.msg+"\n";
                    }
                    Crashlytics.setString("Errors", response);
                    Logger.e(TAG, new NullPointerException(response).toString());
                    Crashlytics.logException(new NullPointerException());
                    finish();
                    return;
                }
                Log.d("AptoideReview", reviewListJson.toString());
                int addiction = reviewListJson.getReview().getAddiction();
                int speed = reviewListJson.getReview().getPerformance();
                int stability = reviewListJson.getReview().getStability();
                int usability = reviewListJson.getReview().getUsability();

                TextView verName = (TextView) findViewById(R.id.vername_date);
                showContent();
                Date date;
                try {
                    date = Configs.TIME_STAMP_FORMAT.parse(reviewListJson.getReview().getAddedTimestamp());
                    String dateText = dateFormatter.format(date);
                    verName.setText(getString(R.string.version) + ": " + reviewListJson.getReview().getApk().getVername() + " - " + dateText);
                } catch (ParseException e) {
                    e.printStackTrace();
                    verName.setText(reviewListJson.getReview().getApk().getVername());
                }

                findViewById(R.id.getapp).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), AppViewActivity.class);
                        intent.putExtra("fromApkInstaller", true);
                        intent.putExtra(Constants.APP_ID_KEY, reviewListJson.getReview().getApk().getId().longValue());
                        intent.putExtra(Constants.APPNAME_KEY, reviewListJson.getReview().getApk().getTitle());
                        intent.putExtra(Constants.PACKAGENAME_KEY, reviewListJson.review.getApk().packageName);

                        startActivity(intent);
                    }
                });

                setValue(speedData, speed, speedLabel, speedChart);
                setValue(addictiveData, addiction, addictiveLabel, addictiveChart);
                setValue(stabilityData, stability, stabilityLabel, stabilityChart);
                setValue(usabilityData, usability, usabilityLabel, usabilityChart);

                rating.setText(AptoideUtils.StringUtils.getRoundedValueFromDouble(reviewListJson.getReview().getRating()));
                title.setText(reviewListJson.getReview().getApk().getTitle());

                finalVeredict.setText(reviewListJson.getReview().getFinalVerdict());

                List<Review.ReviewApk.Screenshots> reviewScreenshots = reviewListJson.getReview().getApk().getScreenshots();
                int i = 0;
                final Context context = ReviewActivity.this;
                if (i < reviewScreenshots.size()) {
                    final String url = reviewScreenshots.get(i++).getUrl();
                    Glide.with(context).load(url).crossFade().into(bigImage);
                }

                for (ImageView screenshot : screenshots) {
                    if (i < reviewScreenshots.size()) {
                        final String url = reviewScreenshots.get(i++).getUrl();
                        Glide.with(context).load(url).crossFade().into(screenshot);
                    }
                }

                Glide.with(context).load(reviewListJson.getReview().getApk().getIcon()).transform(new CircleTransform(context)).crossFade().into(appIcon);
                Glide.with(context).load(reviewListJson.getReview().getUser().getAvatar()).transform(new CircleTransform(context)).into(avatar);

                reviewer.setText(AptoideUtils.StringUtils.getFormattedString(context, R.string.review_by, reviewListJson.getReview().getUser().getName()));

                LayoutInflater layoutInflater = LayoutInflater.from(context);
                for (String pro : reviewListJson.getReview().getPros()) {
                    TextView proTv = (TextView) layoutInflater.inflate(R.layout.review_pro, prosContainer, false);
                    proTv.setText(pro);
                    prosContainer.addView(proTv);
                }

                for (String con : reviewListJson.getReview().getCons()) {
                    TextView conTv = (TextView) layoutInflater.inflate(R.layout.review_con, consContainer, false);
                    conTv.setText(con);
                    consContainer.addView(conTv);
                }

                speedChart.startDataAnimation();
                usabilityChart.startDataAnimation();
                addictiveChart.startDataAnimation();
                stabilityChart.startDataAnimation();
            }
        });

        Analytics.Home.clickOnReviewsMore();
    }

    protected int getContentView() {
        return R.layout.app_review;
    }

    protected void bindViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home || item.getItemId() == R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void setValue(PieChartData data, int score, TextView label,PieChartView chartView){
        int color = getColorBasedOnScore(score);
        setGraph(chartView, data,color);
        label.setBackgroundColor(color);
        data.getValues().get(0).setTarget(score);
        data.getValues().get(1).setTarget(10-score);
        data.setCenterText1(score + "/10");
    }

    private int getColorBasedOnScore(int score){
        String hexColor;
        if(score >=9){
            hexColor = "#00c81b";
        }else if(score >= 7 ){
            hexColor= "#d9d31a";
        }else if(score>=5){
            hexColor = "#ff6600";
        }else{
            hexColor= "#ff3037";
        }
        return Color.parseColor(hexColor);
    }

    private void init() {
        final View speed_chart = findViewById(R.id.speed_chart);
        final View usability_chart = findViewById(R.id.usability_chart);
        final View addictive_chart = findViewById(R.id.addictive_chart);
        final View stability_chart = findViewById(R.id.stability_chart);

        speedChart = (PieChartView) speed_chart.findViewById(R.id.chart);
        usabilityChart = (PieChartView) usability_chart.findViewById(R.id.chart);
        addictiveChart = (PieChartView) addictive_chart.findViewById(R.id.chart);
        stabilityChart = (PieChartView) stability_chart.findViewById(R.id.chart);

        screenshots = new ArrayList<>();
        screenshots.add((ImageView) speed_chart.findViewById(R.id.screenshot));
        screenshots.add((ImageView) usability_chart.findViewById(R.id.screenshot));
        screenshots.add((ImageView) addictive_chart.findViewById(R.id.screenshot));
        screenshots.add((ImageView) stability_chart.findViewById(R.id.screenshot));

        bigImage = (ImageView) findViewById(R.id.bigImage);

        speedLabel = (TextView) speed_chart.findViewById(R.id.designation);
        usabilityLabel = (TextView) usability_chart.findViewById(R.id.designation);
        addictiveLabel = (TextView) addictive_chart.findViewById(R.id.designation);
        stabilityLabel = (TextView) stability_chart.findViewById(R.id.designation);

        speedLabel.setText(R.string.review_speed);
        usabilityLabel.setText(R.string.review_usability);
        addictiveLabel.setText(R.string.review_addictive);
        stabilityLabel.setText(R.string.review_stability);

        title = (TextView) findViewById(R.id.app_name);
        finalVeredict = (TextView) findViewById(R.id.final_veredict);
        reviewer = (TextView) findViewById(R.id.reviewer);
        rating = (TextView) findViewById(R.id.rating);

        appIcon = (ImageView) findViewById(R.id.app_icon);
        avatar = (ImageView) findViewById(R.id.avatar);

        speedData = new PieChartData();
        usabilityData = new PieChartData();
        addictiveData = new PieChartData();
        stabilityData = new PieChartData();

        prosLabel = (TextView) findViewById(R.id.pros_label);
        consLabel = (TextView) findViewById(R.id.cons_label);
        vername_date = (TextView) findViewById(R.id.vername_date);

        consContainer = (LinearLayout) findViewById(R.id.cons_container);
        prosContainer = (LinearLayout) findViewById(R.id.pros_container);
    }

    @Override
    protected void onStart() {
        super.onStart();
        manager.start(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        manager.shouldStop();
    }


    private void setGraph(PieChartView graph, PieChartData data,int color){
        ArrayList<SliceValue> sliceValues = new ArrayList<>();
        SliceValue sliceValue2 = new SliceValue(1);
        SliceValue sliceValue = new SliceValue(0);

        sliceValues.add(sliceValue);
        sliceValues.add(sliceValue2);

        sliceValue.setColor(color);
        sliceValue2.setColor(getResources().getColor(R.color.dark_custom_gray));
        data.setHasCenterCircle(true);
        data.setCenterCircleColor(Color.BLACK);
        data.setValues(sliceValues);
        data.setCenterText1FontSize(12);
        data.setCenterText1Typeface(Typeface.DEFAULT_BOLD);
        data.setCenterText1Color(Color.WHITE);
        graph.setPieChartData(data);
        graph.setChartRotationEnabled(false);
    }

    @Override
    protected String getScreenName() {
        return "Review";
    }
}
