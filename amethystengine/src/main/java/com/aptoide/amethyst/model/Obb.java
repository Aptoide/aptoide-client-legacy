package com.aptoide.amethyst.model;


import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * User: rmateus
 * Date: 06-11-2013
 * Time: 12:24
 * To change this template use File | Settings | File Templates.
 */
@Data
public class Obb {



    private String filename;


    private Number filesize;


    private String md5sum;


    private String path;

    public String getFilename(){
        return this.filename;
    }
    public void setFilename(String filename){
        this.filename = filename;
    }
    public Number getFilesize(){
        return this.filesize;
    }
    public void setFilesize(Number filesize){
        this.filesize = filesize;
    }
    public String getMd5sum(){
        return this.md5sum;
    }
    public void setMd5sum(String md5sum){
        this.md5sum = md5sum;
    }
    public String getPath(){
        return this.path;
    }
    public void setPath(String path){
        this.path = path;
    }
}
