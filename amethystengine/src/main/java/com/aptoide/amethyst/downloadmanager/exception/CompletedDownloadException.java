package com.aptoide.amethyst.downloadmanager.exception;

/**
 * Created with IntelliJ IDEA.
 * User: rmateus
 * Date: 03-07-2013
 * Time: 11:23
 * To change this template use File | Settings | File Templates.
 */
public class CompletedDownloadException extends Throwable {

    public CompletedDownloadException() {

    }

    private long mSize;

    public long getSize() {
        return mSize;
    }
}
