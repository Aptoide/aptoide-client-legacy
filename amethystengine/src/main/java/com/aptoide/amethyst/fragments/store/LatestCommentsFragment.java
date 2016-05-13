package com.aptoide.amethyst.fragments.store;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.R;
import com.aptoide.amethyst.adapters.SpannableRecyclerAdapter;
import com.aptoide.amethyst.dialogs.AptoideDialog;
import com.aptoide.amethyst.ui.callbacks.AddCommentCallback;
import com.aptoide.amethyst.ui.listeners.EndlessRecyclerOnScrollListener;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.amethyst.webservices.v2.AddCommentRequest;
import com.aptoide.amethyst.webservices.v2.AlmostGenericResponseV2RequestListener;
import com.aptoide.dataprovider.webservices.AllCommentsRequest;
import com.aptoide.dataprovider.webservices.json.GenericResponseV2;
import com.aptoide.dataprovider.webservices.models.Constants;
import com.aptoide.dataprovider.webservices.models.v2.Comment;
import com.aptoide.dataprovider.webservices.models.v2.GetComments;
import com.aptoide.models.displayables.CommentItem;
import com.aptoide.models.displayables.Displayable;
import com.aptoide.models.displayables.ProgressBarRow;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;
import java.util.List;


import com.aptoide.amethyst.adapter.BaseAdapter;
import com.aptoide.amethyst.adapter.store.CommentsStoreAdapter;

/**
 * Created by rmateus on 23/06/15.
 */
public class LatestCommentsFragment extends BaseWebserviceFragment {

    private CommentsStoreAdapter adapter;
    private boolean appView;

    String eventActionUrl;
    boolean mLoading = false;
    protected int offset = 0, limit = 30;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();
        eventActionUrl = args.getString("eventActionUrl");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bucketSize = AptoideUtils.UI.getEditorChoiceBucketSize();
        BUCKET_SIZE = bucketSize;
        getRecyclerView().addOnScrollListener(new EndlessRecyclerOnScrollListener((LinearLayoutManager) getRecyclerView().getLayoutManager()) {
            @Override
            public int getOffset() {
                return offset;
            }

            @Override
            public boolean isLoading() {
                return mLoading;
            }

            @Override
            public void onLoadMore() {
                mLoading = true;
                displayableList.add(new ProgressBarRow(BUCKET_SIZE));
                adapter.notifyItemInserted(adapter.getItemCount());
                executeEndlessSpiceRequest();
            }
        });
    }

    RequestListener<GetComments> listener = new RequestListener<GetComments>() {
        @Override
        public void onRequestFailure(SpiceException spiceException) {
            handleErrorCondition(spiceException);
        }

        @Override
        public void onRequestSuccess(GetComments getComments) {
            handleSuccessCondition();
            displayableList.addAll(createCommentItemList(getComments.list));
            if (appView) {
                sortComments(displayableList);
            }
            getRecyclerView().setAdapter(getAdapter());

            /*if(!getComments.list.isEmpty()) {
                displayableList.addAll(createCommentItemList(getComments.list));
            }*/
            offset += getComments.list.size();
            getAdapter().notifyDataSetChanged();
            swipeContainer.setEnabled(false);
            mLoading = false;
        }
    };

    public static LatestCommentsFragment newInstance(Bundle args) {
        LatestCommentsFragment fragment = new LatestCommentsFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // hack: differentiate between coming from the storeActivity or from the AppviewActivity
        if (getArguments() != null) {
            String versionName = (String) getArguments().get(Constants.VERSIONNAME_KEY);
            String packageName = (String) getArguments().get(Constants.PACKAGENAME_KEY);
            if (versionName == null) {
                versionName = "";
            }
            if (packageName == null) {
                packageName = "";
            }
            appView = !(TextUtils.isEmpty(versionName) || TextUtils.isEmpty(packageName));
        }
    }

    @Override
    protected void executeSpiceRequest(boolean useCache) {
        mLoading = true;
        long cacheExpiryDuration = useCache ? DurationInMillis.ONE_HOUR * 6 : DurationInMillis.ALWAYS_EXPIRED;


        // hack: differentiate between coming from the storeActivity or from the AppviewActivity
        if (appView) {
            spiceManager.execute(buildAppRequest(), getBaseContext() + getPackageName() + BUCKET_SIZE, cacheExpiryDuration, listener);
        } else {
            // storeactivity
            spiceManager.execute(buildStoreRequest(), getBaseContext() + getStoreId() + BUCKET_SIZE, cacheExpiryDuration, listener);
        }
    }

    protected void executeEndlessSpiceRequest() {
        long cacheExpiryDuration = useCache ? DurationInMillis.ONE_HOUR * 6 : DurationInMillis.ALWAYS_EXPIRED;

        if (TextUtils.isEmpty(getVersionName()) || TextUtils.isEmpty(getPackageName())) {
            spiceManager.execute(buildStoreRequest(), getBaseContext() + getStoreId() + BUCKET_SIZE + offset, cacheExpiryDuration, new RequestListener<GetComments>() {
                @Override
                public void onRequestFailure(SpiceException spiceException) {
                    if (mLoading && !displayableList.isEmpty()) {
                        displayableList.remove(displayableList.size() - 1);
                        adapter.notifyItemRemoved(displayableList.size());
                    }
                }

                @Override
                public void onRequestSuccess(GetComments getComments) {

                    if (mLoading && !displayableList.isEmpty()) {
                        displayableList.remove(displayableList.size() - 1);
                        adapter.notifyItemRemoved(displayableList.size());
                    }

                    displayableList.addAll(createCommentItemList(getComments.list));
                    adapter.notifyItemRangeInserted(offset, getComments.list.size());

                    offset += getComments.list.size();
                    mLoading = false;

                }
            });
        }// No endless for when a user comes from appView
        else {
            // appviewActivity
            spiceManager.execute(buildAppRequest(), getBaseContext() + getPackageName() + BUCKET_SIZE + offset, cacheExpiryDuration, new RequestListener<GetComments>() {
                @Override
                public void onRequestFailure(SpiceException spiceException) {
                    if (mLoading && !displayableList.isEmpty()) {
                        displayableList.remove(displayableList.size() - 1);
                        adapter.notifyItemRemoved(displayableList.size());
                    }
                }

                @Override
                public void onRequestSuccess(GetComments getComments) {

                    if (mLoading && !displayableList.isEmpty()) {
                        displayableList.remove(displayableList.size() - 1);
                        adapter.notifyItemRemoved(displayableList.size());
                    }

                    displayableList.addAll(createCommentItemList(getComments.list));
                    adapter.notifyItemRangeInserted(offset, getComments.list.size());

                    offset += getComments.list.size();
                    mLoading = false;

                }
            });
        }

    }

    @Override
    protected BaseAdapter getAdapter() {
        if (adapter == null) {
            adapter = new CommentsStoreAdapter(displayableList, getActivity(), getResources().getColor(getStoreTheme().getStoreHeader()), appView, addCommentCallback);
        }
        return adapter;
    }

    AddCommentCallback addCommentCallback = new AddCommentCallback() {
        @Override
        public void addComment(String comment, String answerTo) {
            if (comment != null && comment.length() < Constants.MIN_COMMENT_CHARS) {
                Toast.makeText(getContext(), R.string.error_IARG_100, Toast.LENGTH_LONG).show();
                return;
            }

            AddCommentRequest request = new AddCommentRequest(getContext());
            request.setApkversion(versionName);
            request.setPackageName(packageName);
            request.setRepo(storeName);
            request.setText(comment);

            if (answerTo != null) {
                request.setAnswearTo(answerTo);
            }

            spiceManager.execute(request, addCommentRequestListener);
            AptoideDialog.pleaseWaitDialog().show(getActivity().getSupportFragmentManager(), "pleaseWaitDialog");
        }
    };

    RequestListener<GenericResponseV2> addCommentRequestListener = new AlmostGenericResponseV2RequestListener() {
        @Override
        public void CaseOK() {
            Toast.makeText(Aptoide.getContext(), getString(R.string.comment_submitted), Toast.LENGTH_LONG).show();
            swipeContainer.setRefreshing(true);
            executeSpiceRequest(false);
            dismissDialog();
        }

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            super.onRequestFailure(spiceException);
            dismissDialog();
        }

        @Override
        public void onRequestSuccess(GenericResponseV2 genericResponse) {
            super.onRequestSuccess(genericResponse);
            dismissDialog();
        }

        protected void dismissDialog() {
            DialogFragment pd = (DialogFragment) getActivity().getSupportFragmentManager().findFragmentByTag("pleaseWaitDialog");
            if (pd != null) {
                pd.dismissAllowingStateLoss();
            }
        }
    };
    @Override
    protected String getBaseContext() {
        return "LatestComments";
    }

    private AllCommentsRequest buildStoreRequest() {
        AllCommentsRequest request = new AllCommentsRequest();
        request.storeName = getStoreName();
        request.filters = Aptoide.filters;
        request.lang = AptoideUtils.StringUtils.getMyCountryCode(getContext());
        request.offset = offset;
        return request;
    }

    private AllCommentsRequest buildAppRequest() {
        AllCommentsRequest request = new AllCommentsRequest();
        request.storeName = getStoreName();
        request.versionName = getVersionName();
        request.packageName = getPackageName();
        request.filters = Aptoide.filters;
        request.lang = AptoideUtils.StringUtils.getMyCountryCode(getContext());
        request.offset = offset;
        request.limit = limit;
        return request;
    }

    private List<CommentItem> createCommentItemList(List<Comment> comments) {
        List<CommentItem> itemList = new ArrayList<>();

        if (comments != null) {

            for (Comment comment : comments) {
                itemList.add(createComment(comment));
            }
        }
        return itemList;
    }
    public static int bucketSize = AptoideUtils.UI.getEditorChoiceBucketSize();

    public static CommentItem createComment(Comment comment) {
        CommentItem item = new CommentItem(bucketSize);
        item.setSpanSize(1);
        item.appname = comment.getAppname();
        item.id = comment.getId();
        item.lang = comment.getLang();
        item.text = comment.getText();
        item.timestamp = comment.getTimestamp();
        item.useravatar = comment.getUseravatar();
        item.appid = comment.getAppid();
        item.username = comment.getUsername();
        item.answerto = comment.getAnswerto();

        return item;
    }

    public static List<Displayable> sortComments(List<Displayable> list) {
        List<Displayable> auxList = new ArrayList<>();

        for (Displayable displayable : list) {
            if (displayable instanceof CommentItem) {
                CommentItem comment = (CommentItem) displayable;
                if (comment.answerto != null && comment.answerto.longValue() > 0) {
                    int fatherIndex = getCommentParentLocation(comment.answerto, list);
                    if (fatherIndex >= 0) {
                        if (fatherIndex < auxList.size()) {
                            auxList.add(fatherIndex + 1, comment);
                        } else {
                            auxList.add(comment);
                        }
                        comment.commentLevel = 1;
                    } else {
                        auxList.add(comment);
                        comment.commentLevel = 0;
                    }
                } else {
                    auxList.add(comment);
                    comment.commentLevel = 0;
                }
            } else {
                auxList.add(displayable);
            }
        }

        return auxList;
    }

    public static int getCommentParentLocation(Number answerTo, List<Displayable> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) instanceof CommentItem) {
                CommentItem comment = (CommentItem) list.get(i);
                if (comment.id.longValue() == answerTo.longValue()) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public void setLayoutManager(final RecyclerView recyclerView) {
        if (appView) {
            recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        } else {
            bucketSize = AptoideUtils.UI.getEditorChoiceBucketSize();
            final GridLayoutManager gridLayoutManager = new GridLayoutManager(recyclerView.getContext(), bucketSize);
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {

                    if (!(recyclerView.getAdapter() instanceof SpannableRecyclerAdapter)) {
                        throw new IllegalStateException("RecyclerView adapter must extend SpannableRecyclerAdapter");
                    }

                    int spanSize = ((SpannableRecyclerAdapter) recyclerView.getAdapter()).getSpanSize(position);
                    if (spanSize >= ((GridLayoutManager) recyclerView.getLayoutManager()).getSpanCount()) {
                        return ((GridLayoutManager) recyclerView.getLayoutManager()).getSpanCount();
                    } else {
                        return spanSize;
                    }
                }
            });

            // we need to force the spanCount, or it will crash.
            // https://code.google.com/p/android/issues/detail?id=182400
            gridLayoutManager.setSpanCount(bucketSize);
            recyclerView.setLayoutManager(gridLayoutManager);
        }
    }
}
