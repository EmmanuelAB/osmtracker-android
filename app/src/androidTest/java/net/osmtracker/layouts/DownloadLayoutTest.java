package net.osmtracker.layouts;

import android.Manifest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.GrantPermissionRule;

import net.osmtracker.R;
import net.osmtracker.activity.AvailableLayouts;
import net.osmtracker.util.TestUtils;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;
import static net.osmtracker.util.TestUtils.getLayoutsDirectory;
import static net.osmtracker.util.TestUtils.injectMockLayout;
import static org.apache.commons.io.FileUtils.deleteDirectory;

public class DownloadLayoutTest {

    @Rule
    public GrantPermissionRule writePermission = GrantPermissionRule.grant(Manifest.permission.WRITE_EXTERNAL_STORAGE);

    @Rule
    public ActivityTestRule<AvailableLayouts> mRule = new ActivityTestRule(AvailableLayouts.class);

    //    @Rule
//    public ActivityTestRule<AvailableLayouts> mRule = new ActivityTestRule(AvailableLayouts.class){
//        @Override
//        protected void beforeActivityLaunched() {
//            //Makes sure that only the mock layout exists
//            deleteLayoutsDirectory();
//            TestUtils.setLayoutsTestingRepository();
//        }
//    };

    String layoutWithoutIcons = "abc";

    String layoutWithIcons = "xyz";
    List<String> expectedIcons = Arrays.asList("x.png", "y.png", "z.png");

    String locale = "en";

    String successfulMessage = TestUtils.getStringResource(R.string.available_layouts_successful_download);


    @Before
    public void setUp(){
        deleteLayoutsDirectory();
        TestUtils.setLayoutsTestingRepository();
    }

    /**
     * Download a layout and check:
     *      - the xml file is created
     *      - success message is shown
     */
    @Test
    public void downloadLayoutWithoutIconsTest() {

        clickButtonsToDownloadLayout(layoutWithoutIcons);

        TestUtils.checkToastIsShownWith(successfulMessage);

        assertTrue(layoutFileIsDownloaded(layoutWithoutIcons, locale));

    }

    /**
     * Download a layout and check:
     *     - the xml is created
     *     - the icons folder is created
     *     - the icons are downloaded
     *     - success message is shown
     */
    @Test
    public void downloadLayoutWithIconsTest() {

        clickButtonsToDownloadLayout(layoutWithIcons);

        TestUtils.checkToastIsShownWith(successfulMessage);

        assertTrue(layoutFileIsDownloaded(layoutWithIcons, locale));

        File iconsDirectory = TestUtils.getLayoutIconsDirectoryFor(layoutWithIcons);
        assertTrue(iconsDirectory.exists());

        List<String> icons = TestUtils.listFiles(iconsDirectory);
        assertTrue(icons.containsAll(expectedIcons));

    }


    public boolean layoutFileIsDownloaded(String layoutName, String locale) {
        File layoutFile = TestUtils.getLayoutFileFor(layoutName, locale);
        return layoutFile.exists();
    }


    public void deleteLayoutsDirectory(){
        try {
            FileUtils.deleteDirectory(TestUtils.getLayoutsDirectory());
        }catch (Exception e){
            e.printStackTrace();
            fail();
        }
    }


    private void clickButtonsToDownloadLayout(String layoutName) {
        onView(withText(layoutName)).perform(click());

        // TODO: catch the languages available dialog that shows up when the cell phone is not in EN.

        onView(withText(TestUtils.getStringResource(R.string.available_layouts_description_dialog_positive_confirmation))).
                perform(click());

        TestUtils.checkToastIsShownWith(TestUtils.getStringResource(R.string.available_layouts_successful_download));
    }
}