package cm.aptoide.pt.viewholders.main;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import cm.aptoide.pt.R;

/**
 * Created by gmartinsribeiro on 01/12/15.
 */
public class ScreenshotsViewHolder extends RecyclerView.ViewHolder{

    @Bind(R.id.screenshot_image_item)     public ImageView screenshot;
    @Bind(R.id.play_button)               public ImageView play_button;
    @Bind(R.id.media_layout)              public FrameLayout media_layout;

    public ScreenshotsViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

}
