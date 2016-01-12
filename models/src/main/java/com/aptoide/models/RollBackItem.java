package com.aptoide.models;

/**
 * Created with IntelliJ IDEA.
 * User: tdeus
 * Date: 10/18/13
 * Time: 2:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class RollBackItem {

    private final String repoName;

    public String getRepoName() {
        return repoName;
    }

    public enum Action {
        INSTALLING("Installing"),
        UNINSTALLING("Uninstalling"),
        UPDATING("Updating"),
        INSTALLED("Installed"),
        UNINSTALLED("Uninstalled"),
        UPDATED("Updated"),
        DOWNGRADING("Downgrading"),
        DOWNGRADED("Downgraded");

        private String referrer;
        private String action;

        Action(String action) {
            this.action = action;
        }

        @Override
        public String toString() {
            return action;
        }

        public String getReferrer() {
            return referrer;
        }

        public Action setReferrer(String referrer) {
            this.referrer = referrer;
            return this;
        }
    }

    private final String md5;

    private String name;

    private String pathIcon;

    private String timestamp;

    private String version;

    private String previousVersion;

    private String packageName;

    private Action action;


    public RollBackItem(String name, String packageName, String version, String previousVersion, String pathIcon, String timestamp, String md5, Action action, String repoName){
        this.name = name;
        this.packageName = packageName;
        this.version = version;
        this.previousVersion = previousVersion;
        this.pathIcon = pathIcon;
        this.timestamp = timestamp;
        this.md5 = md5;
        this.action = action;
        this.repoName = repoName;
    }

    public String getName() {
        return name;
    }

    public String getIconPath() { return pathIcon; }

    public String getTimestamp() {
        return timestamp;
    }

    public String getVersion() {
        return version;
    }

    public String getPreviousVersion() { return previousVersion; }

    public String getMd5() { return md5; }

    public String getPackageName() {
        return packageName;
    }

    public Action getAction() { return action; }
}