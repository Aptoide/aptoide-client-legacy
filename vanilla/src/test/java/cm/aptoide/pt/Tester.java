package cm.aptoide.pt;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by rmateus on 28/05/15.
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)

public class Tester {


    @Test
    public void test3() throws Exception{

        assertThat(1).isEqualTo(1);
    }
}
