package com.aptoide.amethyst.events;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.aptoide.amethyst.downloadmanager.model.Download;
import com.aptoide.models.stores.Login;
import com.aptoide.models.stores.Store;
import com.aptoide.models.displayables.UpdateRow;

import com.aptoide.amethyst.downloadmanager.state.StatusState;

import java.util.List;

import lombok.Getter;

/**
 * Created by hsousa on 29-06-2015.
 */
public class OttoEvents {

    public static class ExcludedUpdateAddedEvent {

        private final int position;

        public ExcludedUpdateAddedEvent(int position) {
            this.position = position;
        }

        public int getPosition() {
            return position;
        }
    }

    public static class ExcludedUpdateRemovedEvent {
    }

    //

    /**
     * Renamed from UnInstalledApkEvent

     * {@link GetUpdatesFinishedEvent#numUpdates} (
     * -1   Error getting updates
     * 0    No updates available
     * >=1  Number of updates
     * )}
     */
    public static class GetUpdatesFinishedEvent {

        public final int numUpdates;

        public GetUpdatesFinishedEvent(int numUpdates) {
            this.numUpdates = numUpdates;
        }
    }

    /** ******************  Repo-related Events *******************************/
    public static class RepoAddedEvent {
    }

    public static class RepoDeletedEvent {
        public List<Store> stores;

        public RepoDeletedEvent(List<Store> stores) {
            this.stores = stores;
        }
    }

    public static class RepoCompleteEvent {
        public long getRepoId() {
            return repoId;
        }

        private final long repoId;

        public RepoCompleteEvent(long repoId) {
            this.repoId = repoId;
        }
    }

    /**
     * Used to Subscribe a repo
     */
    public static class RepoSubscribeEvent {

        private final String storeName;

        public RepoSubscribeEvent(String storeName) {
            this.storeName = storeName;
        }

        public String getStoreName() {
            return storeName;
        }
    }

    /** ******************  Download-related Events *******************************/
    public static class DownloadEvent {

        public long getId() {
            return id;
        }

        private final long id;
        private StatusState mStatusState;

        public DownloadEvent(long id, StatusState mStatusState){
            this.id = id;
            this.mStatusState = mStatusState;
        }

        public StatusState getmStatusState() {
            return mStatusState;
        }
    }

    public static class DownloadServiceConnected {
    }

    public static class DownloadInProgress {

        private final Download download;

        public DownloadInProgress(Download download) {
            this.download = download;
        }

        public Download getDownload() {
            return download;
        }
    }

    public static class StartDownload {
        private final List <UpdateRow> row;

        public StartDownload(List<UpdateRow> row) {
            this.row = row;
        }

        public List <UpdateRow> getRow() {
            return row;
        }
    }

    public static class InstalledApkEvent {
    }

    public static class UnInstalledApkEvent {

        private String packageName;

        public UnInstalledApkEvent(String packageName) {
            this.packageName = packageName;
        }

        public String getPackageName() {
            return packageName;
        }
    }

    public static class MatureEvent {

        private boolean mature;

        public MatureEvent(boolean mature) {
            this.mature = mature;
        }

        public boolean isMature() {
            return mature;
        }
    }
    public static class AppViewRefresh {}

    public static class SocialTimelineInitEvent {

        private boolean isRefresh;

        public SocialTimelineInitEvent(boolean isRefresh) {
            this.isRefresh = isRefresh;
        }

        public boolean isRefresh() {
            return isRefresh;
        }

    }

    public class SocialTimelineEvent {

        private boolean isRefresh;

        public SocialTimelineEvent(boolean isRefresh) {
            this.isRefresh = isRefresh;
        }


        public boolean isRefresh() {
            return isRefresh;
        }
    }

    public static class RedrawNavigationDrawer {
    }

    public static class InstallAppFromManager {
        private long id;
        public long getId() {
            return id;
        }

        public InstallAppFromManager(long id) {
            this.id = id;
        }
    }

    public static class StoreAuthorizationEvent{

        private final long id;
        @NonNull
        private final Login login;

        public StoreAuthorizationEvent(final long id, final Login login) {
            this.id = id;
            this.login = login;
        }

        public long getId() {
            return id;
        }

        @NonNull
        public Login getLogin() {
            return login;
        }
    }

    public static class ActivityLifeCycleEvent {
        @Getter LifeCycle state;
        @Getter Activity activity;

        public ActivityLifeCycleEvent(Activity activity, LifeCycle state) {
            this.state = state;
            this.activity = activity;
        }

        public enum LifeCycle {
            CREATE,
            START,
            RESUME,
            PAUSE,
            STOP,
            DESTROY
        }
    }
}