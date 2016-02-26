package com.aptoide.amethyst.callbacks;


import com.aptoide.amethyst.webservices.v2.AddApkCommentVoteRequest;

/**
 * Created by jcosta on 04-07-2014.
 */
public interface AddCommentVoteCallback {
    void voteComment(int commentId, AddApkCommentVoteRequest.CommentVote vote);
}
