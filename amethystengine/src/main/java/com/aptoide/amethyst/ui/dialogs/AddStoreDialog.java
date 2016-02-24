package com.aptoide.amethyst.ui.dialogs;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.R;
import com.aptoide.amethyst.analytics.Analytics;
import com.aptoide.amethyst.database.AptoideDatabase;
import com.aptoide.amethyst.dialogs.ProgressDialogFragment;
import com.aptoide.amethyst.events.BusProvider;
import com.aptoide.amethyst.events.OttoEvents;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.amethyst.utils.IconSizeUtils;
import com.aptoide.amethyst.webservices.ChangeUserRepoSubscription;
import com.aptoide.dataprovider.AptoideSpiceHttpService;
import com.aptoide.dataprovider.webservices.GetSimpleStoreRequest;
import com.aptoide.dataprovider.webservices.models.BulkResponse;
import com.aptoide.models.stores.Login;
import com.aptoide.models.stores.Store;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import com.aptoide.amethyst.requests.CheckServerRequest;

//TODO BusProvider, onStart
/**
 * Created with IntelliJ IDEA.
 * User: rmateus
 * Date: 18-10-2013
 * Time: 17:27
 * To change this template use File | Settings | File Templates.
 */
public class AddStoreDialog extends DialogFragment {
    private SpiceManager spiceManager = new SpiceManager(AptoideSpiceHttpService.class);

    private Callback callback;
    public Callback dummyCallback = new Callback() {
        @Override
        public void startParse(Store s) {

        }
    };
    private String repoName;
    private String url;

    private CheckServerRequest checkServerRequest;
    private Login login;


    public interface Callback{
        public void startParse(Store store);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        callback = (Callback) activity;

    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = dummyCallback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (getDialog() != null) {
//            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getDialog().getWindow().setTitle(getString(R.string.subscribe_store));

        }
        return inflater.inflate(R.layout.dialog_add_store, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        spiceManager.start(getActivity());

        if(url!=null){
            //spiceManager.addListenerIfPending(ResponseCode.class, (url+"rc"),new CheckSimpleStoreListener(login));
            //spiceManager.getFromCache(ResponseCode.class, (url+"rc"), DurationInMillis.ONE_MINUTE, new CheckSimpleStoreListener(login));
        }
    }


    public final class CheckStoreListener implements RequestListener<BulkResponse.GetStore> {


        private final Login login;

        public CheckStoreListener(Login login) {
            this.login = login;

        }

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            dismissDialog();
            Toast.makeText(Aptoide.getContext(), R.string.error_occured, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onRequestSuccess(BulkResponse.GetStore response) {

            try{

                if(response.errors != null){
                    dismissDialog();

                    if(response.errors.get(0).code.equals("STORE-3")){
                        DialogFragment fragment = new PasswordDialog();
                        fragment.setTargetFragment(AddStoreDialog.this, 20);
                        fragment.show(getFragmentManager(), PasswordDialog.FRAGMENT_TAG);

                    }else{
                        Toast.makeText(Aptoide.getContext(), R.string.error_occured, Toast.LENGTH_LONG).show();

                    }

                } else {

                    final Store store = new Store();
                    BulkResponse.GetStore.StoreMetaData data = response.datasets.meta.data;
                    store.setId(data.id.longValue());
                    store.setName(response.datasets.meta.data.name);
                    store.setDownloads(response.datasets.meta.data.downloads.intValue() + "");


                    String sizeString = IconSizeUtils.generateSizeStringAvatar(getActivity());


                    String avatar = data.avatar;

                    if (avatar != null) {
                        String[] splittedUrl = avatar.split("\\.(?=[^\\.]+$)");
                        avatar = splittedUrl[0] + "_" + sizeString + "." + splittedUrl[1];
                    }

                    store.setAvatar(avatar);
                    store.setDescription(data.description);
                    store.setTheme(data.theme);
                    store.setView(data.view);
                    store.setBaseUrl(data.name);

                    if(login!=null) {
                        store.setLogin(login);
                    }

                    AptoideDatabase database = new AptoideDatabase(Aptoide.getDb());

                    long l = database.insertStore(store);
                    database.updateStore(store, l);

                    addStoreOnCloud(store);

                    try {
                        Analytics.Stores.subscribe(response.datasets.meta.data.name);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(Aptoide.getContext(),
                            AptoideUtils.StringUtils.getFormattedString(getContext(), R.string.store_subscribed,
                                    store.getName()),
                            Toast.LENGTH_LONG).show();

                    BusProvider.getInstance().post(new OttoEvents.RepoAddedEvent());
                    dismissDialog();
                    dismiss();
                }

            } catch (Exception e){
                Toast.makeText(Aptoide.getContext(), R.string.error_occured, Toast.LENGTH_LONG).show();
                dismissDialog();
            }
        }
    }

    private void addStoreOnCloud(Store store) {

        if(AccountManager.get(getActivity()).getAccountsByType(Aptoide.getConfiguration().getAccountType()).length > 0) {
            ChangeUserRepoSubscription changeUserRepoSubscription = new ChangeUserRepoSubscription();
            ChangeUserRepoSubscription.RepoSubscription repoSubscription = new ChangeUserRepoSubscription.RepoSubscription(store.getName(), true);
            changeUserRepoSubscription.setRepoSubscription(repoSubscription);
            spiceManager.execute(changeUserRepoSubscription, null);
        }
    }

    @Override
    public void onStop() {
        if(spiceManager.isStarted()){
            spiceManager.shouldStop();
        }
        super.onStop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case 20:
                String username = data.getStringExtra("username");
                String password = data.getStringExtra("password");
                Login login = new Login();
                login.setUsername(username.trim());
                try {
                    login.setPassword(AptoideUtils.Algorithms.computeSHA1sum(password.trim()));
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                get(url, login);
                showDialog();
                break;
        }
    }


    void dismissDialog(){
        setRetainInstance(false);
        DialogFragment pd = (DialogFragment) getFragmentManager().findFragmentByTag("addStoreProgress");
        if(pd!=null)
            pd.dismissAllowingStateLoss();

    }

    void dismissDialog(String message){
        if(message!=null){
            //Toast.makeText(getActivity(),message, Toast.LENGTH_LONG).show();
        }

        dismissDialog();
    }


    public void get(String s, final Login login) {
        url = AptoideUtils.RepoUtils.checkStoreUrl(s);
        repoName = AptoideUtils.RepoUtils.split(url);

        final GetSimpleStoreRequest request = AptoideUtils.RepoUtils.buildSimpleStoreRequest(repoName);
        request.login = login;
        CheckStoreListener checkStoreListener = new CheckStoreListener(login);

        spiceManager.execute(request, checkStoreListener);

        Log.i("Aptoide-", "Request:" +(url+"rc") );
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.button_dialog_add_store).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FlurryAgent.logEvent("Store_View_Dialog_Clicked_Add_Store");
                String url = ((EditText)view.findViewById(R.id.edit_store_uri)).getText().toString();
                if(url!=null&&url.length()>0){
                    get(url, null);
                    showDialog();
                }
            }
        });

        view.findViewById(R.id.button_top_stores).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                FlurryAgent.logEvent("Store_View_Dialog_Clicked_See_Top_Stores");
                Uri uri = Uri.parse("http://m.aptoide.com/more/toprepos/q=" + Aptoide.filters);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                if(isAdded())dismiss();
                startActivity(intent);
            }
        });
    }

    private void showDialog() {

        ProgressDialogFragment pd = new ProgressDialogFragment();
        pd.show(getFragmentManager(), "addStoreProgress");
    }

    public ProgressDialogFragment.OnCancelListener cancelListener = new ProgressDialogFragment.OnCancelListener() {

        @Override
        public void onCancel() {

            Log.i("Aptoide-", "Canceling:" +(url+"rc") );
            Log.i("Aptoide-", "Canceling:" + (url + "repositoryInfo"));

            if(checkServerRequest!=null){
                checkServerRequest.cancel();
            }

            //Toast.makeText(getActivity(), "Canceled", Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState!=null){
            url = savedInstanceState.getString("url");

            ProgressDialogFragment pd = (ProgressDialogFragment) getFragmentManager().findFragmentByTag("addStoreProgress");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("url", url);
    }
}