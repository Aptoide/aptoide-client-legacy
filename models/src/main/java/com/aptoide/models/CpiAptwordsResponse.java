package com.aptoide.models;

/**
 * Created by neuro on 03-08-2015.
 */
public class CpiAptwordsResponse {
    private String status;
    private Info info;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public String getRevenue() {

        String url = info.conversion.url;
        int i = url.lastIndexOf('/');
        String subString = url.substring(i + 1, url.length());

        return subString;
    }

    private class Info {
        private Conversion conversion;

        public Conversion getConversion() {
            return conversion;
        }

        public void setConversion(Conversion conversion) {
            this.conversion = conversion;
        }

        private class Conversion {
            private String url;
            private String chash;

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }

            public String getChash() {
                return chash;
            }

            public void setChash(String chash) {
                this.chash = chash;
            }
        }
    }
}
