package com.aptoide.amethyst.fragments;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.utils.AptoideUtils;

import java.io.File;

/**
 * Created by fabio on 22-10-2015.
 */
public class Md5CalculatorFragmentTask extends Fragment {
    public interface Callback {

        void onPostExecute(String md5Sum);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String package_name = getArguments().getString("package_name");
        setRetainInstance(true);
        new Md5Calculator(callback).execute(package_name);
    }

    Callback callback;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.callback = (Callback) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.callback = null;
    }

    private static class Md5Calculator extends AsyncTask<String, Void, String> {


        private Callback callback;

        public Md5Calculator(Callback callback) {

            this.callback = callback;
        }

        @Override
        protected String doInBackground(String... params) {

            String package_name = params[0];

            PackageManager packageManager = Aptoide.getContext().getPackageManager();
            try {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(package_name, 0);
                String dataDir = applicationInfo.sourceDir;

                return AptoideUtils.Algorithms.md5Calc(new File(dataDir));

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String md5Sum) {
            this.callback.onPostExecute(md5Sum);
        }

    }




}
