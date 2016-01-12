package com.aptoide.amethyst.downloadmanager.model;

import android.util.Log;

import com.aptoide.amethyst.downloadmanager.exception.CompletedDownloadException;
import com.aptoide.amethyst.downloadmanager.exception.Md5FailedException;
import com.aptoide.amethyst.utils.AptoideUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: rmateus
 * Date: 02-07-2013
 * Time: 11:17
 * To change this template use File | Settings | File Templates.
 */
public class DownloadFile implements Serializable{

    private File file;

    public RandomAccessFile getmFile() throws FileNotFoundException {
        return new RandomAccessFile(file, "rw");
    }

    public void delete(){

        new File(mDestination).delete();

    }


    private String mDestination;
    private String md5;



    public DownloadFile(String destination, String md5) throws FileNotFoundException, CompletedDownloadException {
        this.md5 = md5;
        this.mDestination = destination;
        file = new File(this.mDestination);

        if(file.exists()){

            try {
                checkMd5();
                throw new CompletedDownloadException();
            } catch (Md5FailedException e) {

                if (!file.delete()) {
                    throw new FileNotFoundException();
                }

                e.printStackTrace();
            }

        }else{
            this.mDestination = this.mDestination +"--downloading";
            file = new File(this.mDestination);
        }

        File dir = file.getParentFile();
        if ((dir != null) && (!dir.isDirectory())) {
            if(!dir.mkdirs()){
                throw new FileNotFoundException();
            }
        }

    }

    public static long getFileLength(String path)
    {
        File f = new File(path);
        if (f.exists()) {
            return f.length();
        }

        return 0L;
    }

    public String getDestination()
    {
        return this.mDestination;
    }


    public void close(RandomAccessFile file) {
        try {
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void setDownloadedSize(RandomAccessFile file, long downloadedSize) throws IOException {

        Log.d("DownloadFile", "Position is: " + downloadedSize);
        file.getChannel().position(downloadedSize);

    }

    public String getMd5() {
        return md5;
    }

    public synchronized void checkMd5() throws Md5FailedException {

        String md5 = getMd5();

        if(md5.length()>0){

            String calculatedMd5 = AptoideUtils.Algorithms.md5Calc(new File(mDestination));

            if(!calculatedMd5.equals(md5)){

                Log.d("DownloadFile", "Failed Md5: " + mDestination + "   calculated " + calculatedMd5 + " vs " + md5);
                throw new Md5FailedException();

            }

        }


    }

    public void rename() throws FileNotFoundException {

        if(!file.renameTo(new File(this.mDestination.replaceAll("--downloading", "")))){
            throw new FileNotFoundException();
        };

    }
}
