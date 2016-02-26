package com.aptoide.amethyst.webservices.json;

import com.aptoide.dataprovider.webservices.json.GenericResponseV2;

/**
 * Created by fabio on 26-10-2015.
 */
public class GetUserSettingsJson extends GenericResponseV2{
    public Setting settings;

    public static class Setting {

        public String timeline;

        public String getTimeline() {	return timeline;	}
    }

    public Setting getResults() {
        return settings;
    }
}
