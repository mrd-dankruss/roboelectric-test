import com.mrdexpress.paperless.LoginActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.fest.assertions.api.ANDROID.assertThat;

/**
 * Created by dan on 2014/06/09.
 */
@RunWith(RobolectricTestRunner.class)
public class AuthProcessTest {

    private LoginActivity loginActivity

    @Before
    public void setup(){
        loginActivity = Robolectric.buildActivity(LoginActivity.class).get();
    }

    @Test
    public void UserInterfaceLoadTest(){
        assertThat(loginActivity).isNotNull();
    }

}
