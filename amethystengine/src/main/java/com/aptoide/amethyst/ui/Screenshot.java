package com.aptoide.amethyst.ui;

/**
 * Created by tdeus on 2/12/14.
 */
public class Screenshot implements IMediaObject {

    private String url;
    private String orient;

    public Screenshot(String path, String orient) {
        this.url = path;
        this.orient = orient;
    }

    public String getImageUrl() {
        return url;
    }

    public String getOrient() {
        return orient;
    }

}
