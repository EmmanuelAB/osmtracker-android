package net.osmtracker.Layouts;

import android.Manifest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.GrantPermissionRule;

import net.osmtracker.activity.TrackManager;

import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.PreferenceMatchers.withTitleText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static net.osmtracker.Layouts.TestUtils.*;
import static org.junit.Assert.assertFalse;

public class UpdateLayoutTest {

    // Must start in TrackManager and navigate to ButtonsPresets because the files
    // of the layout need to be installed before ButtonsPresets loads
    @Rule
    public ActivityTestRule<TrackManager> mRule = new ActivityTestRule<>(TrackManager.class);

    // Storage permissions are required
    @Rule
    public GrantPermissionRule readPermission = GrantPermissionRule.grant(Manifest.permission.READ_EXTERNAL_STORAGE);
    @Rule
    public GrantPermissionRule writePermission = GrantPermissionRule.grant(Manifest.permission.WRITE_EXTERNAL_STORAGE);


    /**
     * Assumes being at TrackManager Activity
     */
    private void navigateToButtonsPresets(){
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Settings")).perform(click());
        onData(withTitleText("Buttons presets")).perform(scrollTo(), click());
    }

    /**
     * Assumes being in the ButtonsPresets activity
     * Deletes the layout with the received name
     */
    private void updateLayout(String layoutName){
        onView(withText(layoutName)).perform(longClick());
        onView(withText("Update & Install")).perform(click());
    }


    /**
     * Injects a mock layout and deletes it
     * Checks that:
     *  - The UI option doesn't appear anymore
     *  - The XML file is deleted
     *  - A Toast is shown to inform about what happened
     *  - The icons directory is deleted
     */
    @Test
    public void layoutUpdateTest(){

        // Make sure there is at least one layout installed
        String layoutName = "maxspeed";
        injectMockLayout(layoutName, "en");

        navigateToButtonsPresets();

        updateLayout(layoutName);

        // Check the spinner is shown
        onView(withText("Updating...")).check(doesNotExist());

        // Check the successful Toast is shown
        checkToastIsShownWith("Layout was updated successfully");

        // Check the layout still shows in the UI
        onView(withText(layoutName)).check(matches(isDisplayed()));
    }


    /**
     * Install a mock layout in the phone
     *  - Creates the xml, the icons directory and some empty png files inside
     */
    public void injectMockLayout(String layoutName, String iso) {
        File layoutsDir = getLayoutsDirectory();

        // Create a mock layout file
        File newLayout = createFile(layoutsDir,layoutName+"_"+iso+".xml");
        writeToFile(newLayout, MockData.MOCK_LAYOUT_CONTENT);

        // Create the icons directory
        File iconsDir = createDirectory(layoutsDir, layoutName+"_icons");

        // And put some mock files inside
        int pngsToCreate = 4;
        File png;
        for (int i = 1; i <= pngsToCreate; i++) {
            png = createFile(iconsDir, i+".png");
            writeToFile(png, "foo");
        }
    }
}
