package com.aptoide.amethyst.downloadmanager;

import android.os.StatFs;
import android.util.Log;

import com.aptoide.amethyst.downloadmanager.exception.IPBlackListedException;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.aptoide.amethyst.downloadmanager.exception.CompletedDownloadException;
import com.aptoide.amethyst.downloadmanager.exception.ContentTypeNotApkException;
import com.aptoide.amethyst.downloadmanager.exception.DownloadNotFoundException;
import com.aptoide.amethyst.downloadmanager.exception.Md5FailedException;
import com.aptoide.amethyst.downloadmanager.model.DownloadFile;
import com.aptoide.amethyst.downloadmanager.model.DownloadModel;
import com.aptoide.amethyst.downloadmanager.state.ActiveState;
import com.aptoide.amethyst.downloadmanager.state.ErrorState;

/**
 * Created with IntelliJ IDEA.
 * User: rmateus
 * Date: 02-07-2013
 * Time: 15:22
 * To change this template use File | Settings | File Templates.
 */
public class DownloadThread implements Runnable, Serializable {

    DownloadConnection mConnection = null;
    DownloadFile mDownloadFile = null;
    transient RandomAccessFile file = null;
    private long mFullSize;
    private long mProgress;
    private DownloadModel download;
    private long fileSize;
    private long mDownloadedSize = 0;
    private long mRemainingSize;
    private DownloadInfoRunnable parent;

    public DownloadThread(DownloadModel download, DownloadInfoRunnable parent) throws IOException {

        this.download = download;
        this.parent = parent;
        this.mConnection = download.createConnection();
        this.mProgress = DownloadFile.getFileLength(download.getDestination());
        this.mFullSize = download.getSize();
        this.mRemainingSize = mFullSize;
    }

    public long getmDownloadedSize() {
        return mDownloadedSize;
    }

    public long getmProgress() {
        return mProgress > 0 ? mProgress : 0;
    }

    public long getmFullSize() {
        return mFullSize;
    }

    public long getmRemainingSize() {
        return mRemainingSize;
    }

    @Override
    public void run() {
        try {
            if (!(parent.getStatusState() instanceof ActiveState)) {
                return;
            }
            mDownloadFile = download.createFile();
            file = mDownloadFile.getmFile();

            this.mConnection = download.createConnection();

            mConnection.setPaidApp(parent.isPaid());

            this.mDownloadedSize = 0;
            fileSize = DownloadFile.getFileLength(download.getDestination());
            mDownloadFile.setDownloadedSize(file, fileSize);
            this.mRemainingSize = mFullSize - fileSize;

            download();

        } catch (DownloadNotFoundException exception) {
            exception.printStackTrace();
            parent.changeStatusState(new ErrorState(parent, EnumDownloadFailReason.NOT_FOUND));
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
            parent.changeStatusState(new ErrorState(parent, EnumDownloadFailReason.SD_ERROR));
        } catch (ContentTypeNotApkException e) {
            parent.changeStatusState(new ErrorState(parent, EnumDownloadFailReason.PAIDAPP_NOTFOUND));
        } catch (IPBlackListedException e) {

            if (mConnection != null) {
                mConnection.close();
            }

            try {
                mConnection = download.createFallbackConnection();
                download();
            } catch (DownloadNotFoundException exception) {
                exception.printStackTrace();
                parent.changeStatusState(new ErrorState(parent, EnumDownloadFailReason.NOT_FOUND));
            } catch (FileNotFoundException exception) {
                exception.printStackTrace();
                parent.changeStatusState(new ErrorState(parent, EnumDownloadFailReason.SD_ERROR));
            } catch (ContentTypeNotApkException e1) {
                parent.changeStatusState(new ErrorState(parent, EnumDownloadFailReason.PAIDAPP_NOTFOUND));
            } catch (IPBlackListedException e1) {

                try {
                    InetAddress.getByName(mConnection.getURL().getHost());
                } catch (UnknownHostException e2) {
                    e2.printStackTrace();
                }

                parent.changeStatusState(new ErrorState(parent, EnumDownloadFailReason.IP_BLACKLISTED));
            } catch (Md5FailedException e1) {
                e1.printStackTrace();
                mDownloadFile.delete();
                parent.changeStatusState(new ErrorState(parent, EnumDownloadFailReason.MD5_CHECK_FAILED));
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
                parent.changeStatusState(new ErrorState(parent, EnumDownloadFailReason.CONNECTION_ERROR));
            } catch (CompletedDownloadException e1) {
                mFullSize = mProgress = fileSize;
                mRemainingSize = 0;
                e1.printStackTrace();
            } catch (Exception e1) {
                e1.printStackTrace();
                parent.changeStatusState(new ErrorState(parent, EnumDownloadFailReason.CONNECTION_ERROR));
            }

        } catch (Md5FailedException e) {
            e.printStackTrace();
            mDownloadFile.delete();
            parent.changeStatusState(new ErrorState(parent, EnumDownloadFailReason.MD5_CHECK_FAILED));
        } catch (UnknownHostException e) {
            e.printStackTrace();
            parent.changeStatusState(new ErrorState(parent, EnumDownloadFailReason.CONNECTION_ERROR));
        } catch (IOException e) {
            e.printStackTrace();

            parent.changeStatusState(new ErrorState(parent, EnumDownloadFailReason.CONNECTION_ERROR));
        } catch (CompletedDownloadException e) {

            mFullSize = mProgress = fileSize;
            mRemainingSize = 0;

            e.printStackTrace();


        } catch (Exception e) {
            e.printStackTrace();
            parent.changeStatusState(new ErrorState(parent, EnumDownloadFailReason.CONNECTION_ERROR));
        }

        if (mDownloadFile != null && file != null) {
            mDownloadFile.close(file);
        }

        if (mConnection != null) {
            mConnection.close();
        }
    }

    /**
     * Actual method that performs the download.
     *
     * @throws IOException
     * @throws CompletedDownloadException
     * @throws DownloadNotFoundException
     * @throws IPBlackListedException
     * @throws ContentTypeNotApkException
     * @throws Md5FailedException
     */
    private void download() throws IOException, CompletedDownloadException, DownloadNotFoundException, IPBlackListedException, ContentTypeNotApkException, Md5FailedException {
        mConnection.connect(fileSize, parent.isUpdate());

        Log.d("DownloadManager", "Starting Download " + (parent.getStatusState() instanceof ActiveState) + " " + this.mDownloadedSize + fileSize + " " + this.mRemainingSize);
        byte[] bytes = new byte[1024];
        int bytesRead;
        BufferedInputStream mStream = mConnection.getStream();

        if (parent.getStatusState() instanceof ActiveState) {
            StatFs stat = new StatFs(download.getDestination());

            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();

            long avail = (blockSize * availableBlocks);

            if (mRemainingSize > avail) {
                parent.changeStatusState(new ErrorState(parent, EnumDownloadFailReason.NO_FREE_SPACE));
            }
        }

        while ((bytesRead = mStream.read(bytes)) != -1 && parent.getStatusState() instanceof ActiveState) {
            file.write(bytes, 0, bytesRead);
            this.mDownloadedSize += bytesRead;
            this.mProgress += bytesRead;
        }

        if (parent.getStatusState() instanceof ActiveState) {
            mDownloadFile.checkMd5();
            mDownloadFile.rename();
        }
    }

}