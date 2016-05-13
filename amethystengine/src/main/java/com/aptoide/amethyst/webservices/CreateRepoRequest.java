package com.aptoide.amethyst.webservices;

import android.accounts.AccountManager;
import android.text.TextUtils;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.dataprovider.webservices.json.GenericResponseV2;
import com.octo.android.robospice.request.retrofit.RetrofitSpiceRequest;

import java.util.HashMap;

import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by fabio on 22-10-2015.
 */
public class CreateRepoRequest extends RetrofitSpiceRequest<GenericResponseV2, CreateRepoRequest.CreateRepoRequestInterface> {
    private final String repository;
    private final String username;
    private final String password;

    public CreateRepoRequest(String repository, String username, String password) {
        super(GenericResponseV2.class, CreateRepoRequestInterface.class);


        this.repository = repository;
        this.username = username;
        this.password = password;
    }

    @Override
    public GenericResponseV2 loadDataFromNetwork() throws Exception {

        HashMap<String, String> args = new HashMap<>();


        StringBuilder hmacmessage = new StringBuilder();

        String name = AccountManager.get(Aptoide.getContext()).getAccountsByType(Aptoide.getConfiguration().getAccountType())[0].name;

        String dummy = AptoideUtils.Algorithms.computeSHA1sum("dummy");
        args.put("email", name);
        args.put("passhash", dummy);

        hmacmessage.append(name);
        hmacmessage.append(dummy);
        args.put("repo", repository);
        hmacmessage.append(repository);

        if(!TextUtils.isEmpty(username)){
            args.put("privacy", "true");
            hmacmessage.append("true");
            args.put("privacy_user", username);
            hmacmessage.append(username);
            args.put("privacy_pass", password);
            hmacmessage.append(password);

        }

        String bazaar_hmac = AptoideUtils.Algorithms.computeHmacSha1(hmacmessage.toString(), "bazaar_hmac");

        args.put("hmac", bazaar_hmac);
        args.put("mode", "json");

        return getService().createRepo(args);
    }

    public interface CreateRepoRequestInterface{

        @POST("/webservices.aptoide.com/webservices/2/createUser")
        @FormUrlEncoded
        GenericResponseV2 createRepo(@FieldMap HashMap<String, String> args);

    }

}
