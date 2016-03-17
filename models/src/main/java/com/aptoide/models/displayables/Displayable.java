package com.aptoide.models.displayables;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Created by rmateus on 04/06/15.
 */

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.WRAPPER_OBJECT,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = AppItem.class, name = "app"),
        @JsonSubTypes.Type(value = HeaderRow.class, name = "header"),
        @JsonSubTypes.Type(value = UpdateRow.class, name = "update"),
        @JsonSubTypes.Type(value = InstalledHeader.class, name = "installed_header"),
        @JsonSubTypes.Type(value = UpdatesHeader.class, name = "updates_header"),
        @JsonSubTypes.Type(value = EditorsChoiceRow.class, name = "editors_choice"),
        @JsonSubTypes.Type(value = InstallRow.class, name = "installed"),
        @JsonSubTypes.Type(value = CommentItem.class, name = "comment"),
        @JsonSubTypes.Type(value = ReviewRowItem.class, name = "review"),
        @JsonSubTypes.Type(value = ReviewPlaceHolderRow.class, name = "review_placeholder"),
        @JsonSubTypes.Type(value = AdPlaceHolderRow.class, name = "ad_placeholder"),
        @JsonSubTypes.Type(value = AdItem.class, name = "ad_item"),
        @JsonSubTypes.Type(value = CategoryRow.class, name = "category_row"),
        @JsonSubTypes.Type(value = TimeLinePlaceHolderRow.class, name = "timeline_placeholder"),
        @JsonSubTypes.Type(value = CommentPlaceHolderRow.class, name = "comment_placeholder"),
        @JsonSubTypes.Type(value = TimelineRow.class, name = "timeline_row"),
        @JsonSubTypes.Type(value = HomeStoreItem.class, name = "store_item"),
        @JsonSubTypes.Type(value = AdultItem.class, name = "adult_item"),
        @JsonSubTypes.Type(value = BrickAppItem.class, name = "brick_item"),
        @JsonSubTypes.Type(value = ProgressBarRow.class, name = "progress_bar_row"),
        @JsonSubTypes.Type(value = MoreVersionsItem.class, name = "more_versions_item"),
        @JsonSubTypes.Type(value = MoreVersionsAppViewItem.class, name = "more_versions_app_view_item"),
})
public abstract class Displayable {

    public int BUCKETSIZE;
    public int FULL_ROW;
    int spanSize;

    public Displayable(int bucketSize) {
        BUCKETSIZE = bucketSize;
        FULL_ROW = BUCKETSIZE * 2;
    }

    public void setSpanSize(int spanSize) {
        this.spanSize = spanSize;
    }

    public int getSpanSize() {
        return spanSize;
    }

}
