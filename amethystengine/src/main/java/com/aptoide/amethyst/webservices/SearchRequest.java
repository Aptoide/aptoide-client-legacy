package com.aptoide.amethyst.webservices;

import android.text.TextUtils;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.models.displayables.SearchApk;
import com.aptoide.amethyst.models.search.SearchResults;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.dataprovider.webservices.models.Constants;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.util.HashMap;
import java.util.List;

import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;


/**
 * Created by rmateus on 12/06/15.
 */
public class SearchRequest extends RetrofitSpiceRequest<SearchResults, SearchRequest.Webservice> {

    public interface Webservice {
        @POST("/webservices.aptoide.com/webservices/3/listSearchApks")
        @FormUrlEncoded
        SearchJson searchApks(@FieldMap HashMap<String, String> args);
    }

    private static final String PACKAGE = "package";
    private static final String MODE = "mode";
    private static final String TYPE = "type";
    private static final String MATURE = "mature";
    private static final String VERCODE = "vercode";
    private static final String REPO = "repos";
    private static final String LIMIT = "limit";
    private static final String OFFSET = "offset";
    private static final String U_LIMIT = "u_limit";
    private static final String U_OFFSET = "u_offset";
    private static final String LANG = "lang";
    private static final String FILTERS = "q";
    private static final String SEARCH = "search";
    public static final int SEARCH_LIMIT = 7;
    public static final int OTHER_REPOS_SEARCH_LIMIT = 0;
    private String[] repos = {};
    private String search;
    private int limit;
    private int offset;
    private int u_offset;

    public void setOtherReposLimit(int otherReposLimit) {
        this.otherReposLimit = otherReposLimit;
    }

    private int otherReposLimit;

    public SearchRequest() {
        super(SearchResults.class, SearchRequest.Webservice.class);
    }

    public void setU_offset(int u_offset) {
        this.u_offset = u_offset;
    }

    @Override
    public SearchResults loadDataFromNetwork() throws Exception {


        HashMap<String, String> args = new HashMap<>();

        args.put(MODE, "json");
        args.put(SEARCH, search);
        args.put(FILTERS, Aptoide.filters);

        //build options
        StringBuilder optionsString = new StringBuilder();
        optionsString.append("(");
        optionsString.append(putIntOptionString("limit", limit));
        optionsString.append(putIntOptionString("u_limit", otherReposLimit));
        if (repos.length > 0) {
            optionsString.append("repos=" + TextUtils.join(",", repos) + ";");
        }
        optionsString.append(putIntOptionString("offset", offset));
        optionsString.append(putIntOptionString("u_offset", u_offset));
        optionsString.append(putIntOptionString("mature", AptoideUtils.getSharedPreferences().getBoolean(Constants.MATURE_CHECK_BOX, false)));
        optionsString.append(")");

        args.put("options", optionsString.toString());


        SearchJson json = getService().searchApks(args);
        SearchResults results = new SearchResults();

        for (SearchJson.Apk apk : json.results.apks) {
            results.apkList.add(apk.toSearchApk());
        }

        for (SearchJson.Apk apk : json.results.uApks) {
            results.uApkList.add(apk.toSearchApk());
        }

        results.didyoumean.addAll(json.results.didyoumean);

		setFromSubscribedStores(results.apkList);
		setPositions(results.apkList, results.uApkList);

        return results;
    }

	private void setFromSubscribedStores(List<SearchApk> apkList) {
		for (SearchApk searchApk : apkList) {
			searchApk.fromSubscribedStore = true;
		}
	}

	/**
     * Define search items position for Analytics purpose.
     */
    private void setPositions(List<SearchApk> apkList, List<SearchApk> uApkList) {
		int i = offset + u_offset + 1;

		if (!apkList.isEmpty()) {
			for (SearchApk apk : apkList) {
				apk.position = i++;
			}
		}

		if (!uApkList.isEmpty()) {
			for (SearchApk apk : uApkList) {
				apk.position = i++;
			}
		}
	}

    public void setRepos(String... repos) {
        this.repos = repos;
    }

    public void setSearchQuery(String search) {
        this.search = search;
    }

    private String putIntOptionString(String argName, Boolean value) {
        return putIntOptionString(argName, String.valueOf(value));
    }

    private String putIntOptionString(String argName, int value) {
        if (value > 0) {
            return putIntOptionString(argName, String.valueOf(value));
        }else return "";
    }

    private String putIntOptionString(String argName, String value) {
        return argName + "=" + value + ";";
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
