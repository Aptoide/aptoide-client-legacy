package com.aptoide.amethyst.viewholders.main;

import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.R;
import com.aptoide.amethyst.dialogs.AdultDialog;
import com.aptoide.amethyst.events.BusProvider;
import com.aptoide.amethyst.events.OttoEvents;
import com.aptoide.dataprovider.webservices.models.Constants;
import com.aptoide.models.displayables.Displayable;


import com.aptoide.amethyst.viewholders.BaseViewHolder;

/**
 * Created by hsousa on 23/06/15.
 */
public class AdultRowViewHolder extends BaseViewHolder {

	public SwitchCompat adultSwitch;
	FragmentManager fragmentManager;

	public AdultRowViewHolder(View itemView, int viewType, FragmentManager fragmentManager) {
		super(itemView, viewType);
		this.fragmentManager = fragmentManager;
	}

	@Override
	public void populateView(Displayable displayable) {
//        final AdultRowViewHolder holder = (AdultRowViewHolder) viewHolder;
		adultSwitch.setOnCheckedChangeListener(null);
		adultSwitch.setChecked(PreferenceManager.getDefaultSharedPreferences(Aptoide.getContext()).getBoolean(Constants.MATURE_CHECK_BOX, false));
		adultSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				if (isChecked) {
					new AdultDialog().show(fragmentManager, "adultDialog");
				} else {
					BusProvider.getInstance().post(new OttoEvents.MatureEvent(false));
				}
			}
		});
	}

	@Override
	protected void bindViews(View itemView) {
		adultSwitch = (SwitchCompat) itemView.findViewById(R.id.adult_content);
	}
}
