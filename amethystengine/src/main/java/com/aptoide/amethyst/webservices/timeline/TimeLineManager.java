package com.aptoide.amethyst.webservices.timeline;

/**
 * Created by fabio on 14-10-2015.
 */
public interface TimeLineManager {

    public void hidePost(long id);
    public void unHidePost(long id);
    public void likePost(long id);
    public void unlikePost(long id);
    public void commentPost(long id,String comment, int position);
    public void getComments(long id);
    public void getWhoLiked(long id);
    public void openCommentsDialog(long id, int position);
    public void openWhoLikesDialog(long id,int likes, int position);
}
