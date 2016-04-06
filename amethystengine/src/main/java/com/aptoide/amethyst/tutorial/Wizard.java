package com.aptoide.amethyst.tutorial;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aptoide.amethyst.R;

import java.util.ArrayList;



/**
 * Created with IntelliJ IDEA.
 * User: brutus
 * Date: 30-10-2013
 * Time: 12:06
 * To change this template use File | Settings | File Templates.
 */
public class Wizard {

    public static ArrayList<Fragment> getWizardNewToAptoide() {
        ArrayList<Fragment> wizard = new ArrayList<Fragment>();
        wizard.add(NewToAptoide1.newInstace());
        wizard.add(NewToAptoide2.newInstace());
        wizard.add(NewToAptoide3.newInstace());
        return wizard;
    }

    public static ArrayList<Fragment> getWizardUpdate() {
        ArrayList<Fragment> wizard = new ArrayList<Fragment>();
        wizard.add(NewFeature4.newInstace());
//        wizard.add(NewFeature4.newInstace());
        return wizard;
    }


    public static class NewToAptoide1 extends Fragment {

        public static NewToAptoide1 newInstace() {
            NewToAptoide1 fragment = new NewToAptoide1();
            Bundle args = new Bundle();
            args.putString("name", "NewToAptoide1");
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_tutorial, container, false);
            return view;
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            TextView title = (TextView) view.findViewById(R.id.title);
            title.setText(getString(R.string.wizard_title_01));
            TextView description = (TextView) view.findViewById(R.id.description);
            description.setText(getString(R.string.wizard_description_01));
            ImageView image = (ImageView) view.findViewById(R.id.image);
            image.setImageResource(R.drawable.wizard_01);
        }

    }

    public static class NewFeature4 extends Fragment {

        public static NewFeature4 newInstace() {
            NewFeature4 fragment = new NewFeature4();
            Bundle args = new Bundle();
            args.putString("name", "NewFeature1");
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_wizard_new_reviews, container, false);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

        }

    }

    public static class NewToAptoide2 extends Fragment {

        TextView title;
        TextView description;
        ImageView image;
        ImageView arrow;
        TextView add_more_stores;

        public static NewToAptoide2 newInstace() {
            NewToAptoide2 fragment = new NewToAptoide2();

            Bundle args = new Bundle();
            args.putString("name", "NewToAptoide2");
            fragment.setArguments(args);

            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_tutorial, container, false);
            return view;
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            title = (TextView) view.findViewById(R.id.title);
            title.setText(getString(R.string.wizard_title_02));
            description = (TextView) view.findViewById(R.id.description);
            description.setText(getString(R.string.wizard_description_02));
            image = (ImageView) view.findViewById(R.id.image);
            image.setImageResource(R.drawable.wizard_02);
        }

    }

    public static class NewToAptoide3 extends Fragment {

        public static NewToAptoide3 newInstace() {
            NewToAptoide3 fragment = new NewToAptoide3();

            Bundle args = new Bundle();
            args.putString("name", "NewToAptoide3");
            fragment.setArguments(args);

            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_tutorial_3, container, false);
            return view;
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            TextView mainTitle = (TextView) view.findViewById(R.id.mainTitle);
            mainTitle.setText(getString(R.string.wizard_title_03));
            TextView title = (TextView) view.findViewById(R.id.title);
            title.setText(getString(R.string.wizard_subtitle_03_1));
            TextView description = (TextView) view.findViewById(R.id.description);
            description.setText(getString(R.string.wizard_description_03_1));
            ImageView image = (ImageView) view.findViewById(R.id.image);
            image.setImageResource(R.drawable.wizard_03_1);
            TextView title2 = (TextView) view.findViewById(R.id.title2);
            title2.setText(getString(R.string.wizard_subtitle_03_2));
            TextView description2 = (TextView) view.findViewById(R.id.description2);
            description2.setText(getString(R.string.wizard_description_03_2));
            ImageView image2 = (ImageView) view.findViewById(R.id.image2);
            image2.setImageResource(R.drawable.wizard_03_2);
        }
    }

    public static class NewFeature1 extends Fragment {

        public static NewFeature1 newInstace() {
            NewFeature1 fragment = new NewFeature1();
            Bundle args = new Bundle();
            args.putString("name", "NewFeature1");
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_wizard_new_improvements, container, false);
            return view;
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

        }
    }

    public static class NewFeature2 extends Fragment {

        public static NewFeature2 newInstace() {
            NewFeature2 fragment = new NewFeature2();
            Bundle args = new Bundle();
            args.putString("name", "NewFeature2");
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_wizard_new_homepage, container, false);
            return view;
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
        }
    }

    public static class NewFeature3 extends Fragment {

        public static NewFeature3 newInstace() {
            NewFeature3 fragment = new NewFeature3();
            Bundle args = new Bundle();
            args.putString("name", "NewFeature3");
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_wizard_new_top, container, false);
            return view;
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
        }
    }

    public static class OneClickInstallWizard extends Fragment {

        public static Fragment newInstance() {
            return new OneClickInstallWizard();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_wizard_oneclickinstall, container, false);
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

        }
    }


}
