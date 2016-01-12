package cm.aptoide.pt;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import org.apache.tools.ant.taskdefs.Sleep;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.manifest.AndroidManifest;
import org.robolectric.res.builder.RobolectricPackageManager;

import butterknife.ButterKnife;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class ApplicationTest{



    @Test
    public void test1() throws Exception {
        MainActivity activity = Robolectric.setupActivity(MainActivity.class);


        ViewPager viewById = ButterKnife.findById(activity, R.id.pager);
        viewById.setVisibility(View.INVISIBLE);
        Thread.sleep(10000);
        assertThat(viewById.getVisibility()).isEqualTo(View.INVISIBLE);
    }

    @Test
    public void test2() throws Exception {
        assertThat("test").isEqualTo("test");
    }

    }