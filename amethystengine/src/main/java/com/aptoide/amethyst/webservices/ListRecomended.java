package com.aptoide.amethyst.webservices;

import java.util.List;

/**
 * Created by fabio on 15-10-2015.
 */
public class ListRecomended{


    public Number productscount;

    public List<Repository> repository;


    public String status;

    public Number getProductscount(){
        return this.productscount;
    }
    public void setProductscount(Number productscount){
        this.productscount = productscount;
    }
    public List<Repository> getRepository(){
        return this.repository;
    }
    public void setRepository(List<Repository> repository){
        this.repository = repository;
    }
    public String getStatus(){
        return this.status;
    }
    public void setStatus(String status){
        this.status = status;
    }


    public static class Repository{


        public String apkpath;
        public Number appscount;
        public String basepath;
        public String featuregraphicpath;
        public String hash;


        public String iconspath;


        public String name;


        public List<Package> thePackages;
        public String screenspath;

        public String getApkpath(){
            return this.apkpath;
        }
        public void setApkpath(String apkpath){
            this.apkpath = apkpath;
        }
        public Number getAppscount(){
            return this.appscount;
        }
        public void setAppscount(Number appscount){
            this.appscount = appscount;
        }
        public String getBasepath(){
            return this.basepath;
        }
        public void setBasepath(String basepath){
            this.basepath = basepath;
        }

        public String getFeaturegraphicpath(){
            return this.featuregraphicpath;
        }
        public void setFeaturegraphicpath(String featuregraphicpath){
            this.featuregraphicpath = featuregraphicpath;
        }
        public String getHash(){
            return this.hash;
        }
        public void setHash(String hash){
            this.hash = hash;
        }
        public String getIconspath(){
            return this.iconspath;
        }
        public void setIconspath(String iconspath){
            this.iconspath = iconspath;
        }
        public String getName(){
            return this.name;
        }
        public void setName(String name){
            this.name = name;
        }
        public List<Package> getPackage(){
            return this.thePackages;
        }
        public void setPackage(List<Package> thePackages){
            this.thePackages = thePackages;
        }
        public String getScreenspath(){
            return this.screenspath;
        }
        public void setScreenspath(String screenspath){
            this.screenspath = screenspath;
        }


        public static class Package{


            public String age;


            public String apkid;


            public String catg;


            public String catg2;


            public Number catids;


            public String cpu;


            public Number dwn;


            public String icon;


            public String icon_hd;


            public String md5h;


            public String minGles;


            public String minScreen;


            public Number minSdk;




            public String name;


            public String path;


            public Number rat;


            public String signature;


            public Number sz;


            public String timestamp;


            public String ver;


            public Number vercode;

            public String getAge(){
                return this.age;
            }
            public void setAge(String age){
                this.age = age;
            }
            public String getApkid(){
                return this.apkid;
            }
            public void setApkid(String apkid){
                this.apkid = apkid;
            }
            public String getCatg(){
                return this.catg;
            }
            public void setCatg(String catg){
                this.catg = catg;
            }
            public String getCatg2(){
                return this.catg2;
            }
            public void setCatg2(String catg2){
                this.catg2 = catg2;
            }
            public Number getCatids(){
                return this.catids;
            }
            public void setCatids(Number catids){
                this.catids = catids;
            }
            public String getCpu(){
                return this.cpu;
            }
            public void setCpu(String cpu){
                this.cpu = cpu;
            }
            public Number getDwn(){
                return this.dwn;
            }
            public void setDwn(Number dwn){
                this.dwn = dwn;
            }

            public String getIcon(){
                return this.icon;
            }

            public void setIcon(String icon){
                this.icon = icon;
            }
            public String getMd5h(){
                return this.md5h;
            }
            public void setMd5h(String md5h){
                this.md5h = md5h;
            }
            public String getMinGles(){
                return this.minGles;
            }
            public void setMinGles(String minGles){
                this.minGles = minGles;
            }
            public String getMinScreen(){
                return this.minScreen;
            }
            public void setMinScreen(String minScreen){
                this.minScreen = minScreen;
            }
            public Number getMinSdk(){
                return this.minSdk;
            }
            public void setMinSdk(Number minSdk){
                this.minSdk = minSdk;
            }
            public String getName(){
                return this.name;
            }
            public void setName(String name){
                this.name = name;
            }
            public String getPath(){
                return this.path;
            }
            public void setPath(String path){
                this.path = path;
            }
            public Number getRat(){
                return this.rat;
            }
            public void setRat(Number rat){
                this.rat = rat;
            }

            public String getSignature(){
                return this.signature;
            }
            public void setSignature(String signature){
                this.signature = signature;
            }
            public Number getSz(){
                return this.sz;
            }
            public void setSz(Number sz){
                this.sz = sz;
            }
            public String getTimestamp(){
                return this.timestamp;
            }
            public void setTimestamp(String timestamp){
                this.timestamp = timestamp;
            }
            public String getVer(){
                return this.ver;
            }
            public void setVer(String ver){
                this.ver = ver;
            }
            public Number getVercode(){
                return this.vercode;
            }
            public void setVercode(Number vercode){
                this.vercode = vercode;
            }

            public String getIcon_hd() {
                return icon_hd;
            }
        }


    }



}
