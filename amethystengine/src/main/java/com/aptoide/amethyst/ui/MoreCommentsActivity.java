package com.aptoide.amethyst.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.aptoide.amethyst.R;
import com.aptoide.amethyst.preferences.SecurePreferences;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.amethyst.webservices.v2.AddApkCommentVoteRequest;
import com.aptoide.amethyst.webservices.v2.AlmostGenericResponseV2RequestListener;
import com.aptoide.dataprovider.webservices.json.GenericResponseV2;
import com.aptoide.dataprovider.webservices.models.Defaults;
import com.octo.android.robospice.request.listener.RequestListener;


import com.aptoide.amethyst.callbacks.AddCommentVoteCallback;
import com.aptoide.amethyst.fragments.store.LatestCommentsFragment;

public class MoreCommentsActivity extends MoreActivity implements AddCommentVoteCallback {

    @Override
    protected Fragment getFragment(Bundle args) {
        Fragment fragment = LatestCommentsFragment.newInstance(args);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected String getScreenName() {
        return "All Comments";
    }

    @Override
    public void voteComment(int commentId, AddApkCommentVoteRequest.CommentVote vote) {
        RequestListener<GenericResponseV2> commentRequestListener = new AlmostGenericResponseV2RequestListener() {
            @Override
            public void CaseOK() {
                Toast.makeText(MoreCommentsActivity.this, getString(R.string.vote_submitted), Toast.LENGTH_LONG).show();
            }
        };

        AptoideUtils.VoteUtils.voteComment(
                spiceManager,
                commentId,
                Defaults.DEFAULT_STORE_NAME,
                SecurePreferences.getInstance().getString("token", "empty"),
                commentRequestListener,
                vote);
    }
}
