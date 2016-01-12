package cm.aptoide.pt.adapter.main;

import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aptoide.amethyst.adapters.SpannableRecyclerAdapter;
import com.aptoide.amethyst.models.EnumStoreTheme;
import com.aptoide.models.Displayable;

import java.util.List;

import cm.aptoide.pt.R;
import cm.aptoide.pt.adapter.BaseAdapter;
import cm.aptoide.pt.viewholders.BaseViewHolder;
import cm.aptoide.pt.viewholders.HomeBrickItemViewHolder;
import cm.aptoide.pt.viewholders.HomeGridItemViewHolder;
import cm.aptoide.pt.viewholders.ProgressBarRowViewHolder;
import cm.aptoide.pt.viewholders.main.AdultRowViewHolder;
import cm.aptoide.pt.viewholders.main.EditorsChoiceViewHolder;
import cm.aptoide.pt.viewholders.main.EmptyViewHolder;
import cm.aptoide.pt.viewholders.main.HeaderViewHolder;
import cm.aptoide.pt.viewholders.main.HomeCategoryViewHolder;
import cm.aptoide.pt.viewholders.main.ReviewViewHolder;
import cm.aptoide.pt.viewholders.main.StoreItemRowViewHolder;
import cm.aptoide.pt.viewholders.main.TimelineViewHolder;
import cm.aptoide.pt.viewholders.store.StoreHeaderViewHolder;

/**
 * Created by rmateus on 02/06/15.
 */
public class HomeTabAdapter extends BaseAdapter implements SpannableRecyclerAdapter {

    /**
     * flag used to change the Subscribe button. since that info comes from SQLite
     */
    private boolean subscribed;
    private String storeName;
    private long storeId;

    /**
     * Necessary to show the AdultDialog
     */
    private FragmentManager fragmentManager;

    /**
     * Used on the Store's fragments. We'll also use to tint the review rating button
     */
    private EnumStoreTheme theme;


    public HomeTabAdapter(List<Displayable> displayableList, EnumStoreTheme theme, boolean subscribed,String storeName) {
        super(displayableList);
        this.theme = theme;
        this.subscribed = subscribed;
        this.storeName = storeName;
    }

    public HomeTabAdapter(List<Displayable> displayableList, FragmentManager fragmentManager, EnumStoreTheme theme) {
        super(displayableList);
        this.fragmentManager = fragmentManager;
        this.theme = theme;
    }

    public HomeTabAdapter(List<Displayable> displayableList, FragmentManager fragmentManager, EnumStoreTheme theme, String storeName) {
        super(displayableList);
        this.fragmentManager = fragmentManager;
        this.theme = theme;
        this.storeName = storeName;
    }

    public HomeTabAdapter(List<Displayable> displayableList, FragmentManager fragmentManager, EnumStoreTheme theme, String storeName, long storeId) {
        super(displayableList);
        this.fragmentManager = fragmentManager;
        this.theme = theme;
        this.storeName = storeName;
        this.storeId = storeId;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(viewType, viewGroup, false);

        BaseViewHolder holder;
        switch (viewType) {
            case R.layout.layout_header: {
                holder = new HeaderViewHolder(view, viewType, theme, storeName,storeId);
                break;
            }
            case R.layout.grid_item: {
                holder = new HomeGridItemViewHolder(view, viewType);
                break;
            }
            case R.layout.row_store_header: {
                holder = new StoreHeaderViewHolder(view, viewType, subscribed, theme);
                break;
            }
            case R.layout.editors_choice_row: {
                holder = new EditorsChoiceViewHolder(view, viewType);
                break;
            }
            case R.layout.row_review: {
                holder = new ReviewViewHolder(view, viewType, theme);
                break;
            }
            case R.layout.row_empty: {
                holder = new EmptyViewHolder(view, viewType);
                break;
            }
            case R.layout.row_category_home_item: {
                holder = new HomeCategoryViewHolder(view, viewType, theme);
                break;
            }
            case R.layout.timeline_item: {
                holder = new TimelineViewHolder(view, viewType);
                break;
            }
            case R.layout.row_store_item: {
                holder = new StoreItemRowViewHolder(view, viewType);
                break;
            }
            case R.layout.row_adult_switch: {
                holder = new AdultRowViewHolder(view, viewType, fragmentManager);
                break;
            }
            case R.layout.brick_app_item: {
                holder = new HomeBrickItemViewHolder(view, viewType);
                break;
            }
            case R.layout.row_progress_bar: {
                holder = new ProgressBarRowViewHolder(view, viewType);
                break;
            }
            default:
                throw new IllegalStateException("HomeTabAdapter with unknown viewtype");
        }

        return holder;
    }

    @Override
    public int getSpanSize(int position) {
        if (position >= displayableList.size() || position < 0) {
            return 1;
        } else {
            return displayableList.get(position).getSpanSize();
        }
    }
}
