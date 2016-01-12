package com.aptoide.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by rmateus on 29-07-2014.
 */
public class ApkSuggestionJson {
    public Options options = new Options();

    public List<Ads> ads;
    public String status;

    public List<Ads> getAds(){
        return this.ads;
    }
    public void setAds(List<Ads> ads){
        this.ads = ads;
    }
    public String getStatus(){
        return this.status;
    }
    public void setStatus(String status){
        this.status = status;
    }


    public static class Options{
        public Boolean mediation = true;

        public Boolean getMediation(){
            return mediation;
        }

    }


    public static class Data{
        public String description;
        public Number downloads;
        public String icon;
        public Number id;
        public String md5sum;
        public String name;
        @JsonProperty("package") public String packageName;
        public String repo;
        public Number size;
        public Number stars;
        public Number vercode;
        public String vername;

        public String developer;

        public String url;
        public String image;

        public String getDescription(){
            return this.description;
        }
        public void setDescription(String description){
            this.description = description;
        }
        public Number getDownloads(){
            return this.downloads;
        }
        public void setDownloads(Number downloads){
            this.downloads = downloads;
        }
        public String getIcon(){
            return this.icon;
        }
        public void setIcon(String icon){
            this.icon = icon;
        }
        public Number getId(){
            return this.id;
        }
        public void setId(Number id){
            this.id = id;
        }
        public String getMd5sum(){
            return this.md5sum;
        }
        public void setMd5sum(String md5sum){
            this.md5sum = md5sum;
        }
        public String getName(){
            return this.name;
        }
        public void setName(String name){
            this.name = name;
        }
        public String getPackageName() {
            return packageName;
        }
        public void setPackage(String packageName){
            this.packageName = packageName;
        }
        public String getRepo(){
            return this.repo;
        }
        public void setRepo(String repo){
            this.repo = repo;
        }
        public Number getSize(){
            return this.size;
        }
        public void setSize(Number size){
            this.size = size;
        }
        public Number getStars(){
            return this.stars;
        }
        public void setStars(Number stars){
            this.stars = stars;
        }
        public Number getVercode(){
            return this.vercode;
        }
        public void setVercode(Number vercode){
            this.vercode = vercode;
        }
        public String getVername(){
            return this.vername;
        }
        public void setVername(String vername){
            this.vername = vername;
        }

        public String getUrl(){
            return this.url;
        }

        public void setUrl(String url){
            this.url = url;
        }
        public String getImage(){
            return this.image;
        }
        public void setImage(String image){
            this.image = image;
        }
    }


    public static class Ads{

        public Data data;
        public Info info;
        public Partner partner;
        public Partner tracker;

        public Partner getTracker() {
            return tracker;
        }

        public void setTracker(Partner tracker) {
            this.tracker = tracker;
        }

        public Data getData(){
            return this.data;
        }
        public void setData(Data data){
            this.data = data;
        }
        public Info getInfo(){
            return this.info;
        }
        public void setInfo(Info info){
            this.info = info;
        }

        public Partner getPartner() {
            return partner;
        }

        public void setPartner(Partner partner) {
            this.partner = partner;
        }
    }

    public static class Info{

        public long ad_id;
        public String ad_type;
        public String cpc_url;
        public String cpi_url;
        public String cpd_url;

        public long getAd_id() {
            return ad_id;
        }
        public void setAd_id(long ad_id) {
            this.ad_id = ad_id;
        }
        public String getAd_type(){
            return this.ad_type;
        }
        public void setAd_type(String ad_type){
            this.ad_type = ad_type;
        }
        public String getCpc_url(){
            return this.cpc_url;
        }
        public void setCpc_url(String cpc_url){
            this.cpc_url = cpc_url;
        }
        public String getCpi_url(){
            return this.cpi_url;
        }
        public void setCpi_url(String cpi_url){
            this.cpi_url = cpi_url;
        }
        public String getCpd_url() {
            return cpd_url;
        }
        public void setCpd_url(String cpd_url) {
            this.cpd_url = cpd_url;
        }
    }

    public static class Partner{

        public Info getPartnerInfo() {
            return partnerInfo;
        }

        public Data getPartnerData() {
            return partnerData;
        }

        @JsonProperty("info") public Info partnerInfo;
        @JsonProperty("data")public Data partnerData;

        public static class Info{

            public Number getId() {
                return id;
            }

            public String getName() {
                return name;
            }

            public Number id;
            public String name;
        }

        public static class Data{

            public String getClick_url() {
                return click_url;
            }

            public String getImpression_url() {
                return impression_url;
            }

            public String click_url;
            public String impression_url;
        }


    }

}
