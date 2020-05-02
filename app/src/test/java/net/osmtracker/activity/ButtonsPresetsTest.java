package net.osmtracker.activity;

import android.widget.LinearLayout;

import net.osmtracker.R;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.Hashtable;

import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.mock;


import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith( PowerMockRunner.class )
public class ButtonsPresetsTest {

    @Test
    @PrepareForTest(ButtonsPresets.class)
    public void refreshActivityTest() {
        try {
            ButtonsPresets mockActivity = mock(ButtonsPresets.class);

            Hashtable internalHash = new Hashtable<String,String>();
            internalHash.put("foo", "bar");

            Whitebox.setInternalState(mockActivity.getClass(), "layoutsFileNames", internalHash);


            LinearLayout downloadedLayouts = mock(LinearLayout.class);
            LinearLayout defaultSection = mock(LinearLayout.class);


            when(mockActivity,"refreshActivity").thenCallRealMethod();


            when(mockActivity,"findViewById",R.id.list_layouts).thenReturn(downloadedLayouts);
            when(mockActivity,"findViewById",R.id.buttons_presets).thenReturn(defaultSection);

            // Downgrade mockito dependency to 1.6.2 for this to work
            doNothing().when(mockActivity, "listLayouts", downloadedLayouts);

            mockActivity.refreshActivity();

            internalHash = Whitebox.getInternalState(mockActivity.getClass(), "layoutsFileNames");

            // Check the hashtable is reset
            assertEquals(0, internalHash.size());

            // Check calls listLayouts
            verifyPrivate(mockActivity).invoke("listLayouts", downloadedLayouts);

            // Check calls checkCurrentLayout
            verifyPrivate(mockActivity).invoke("checkCurrentLayout", downloadedLayouts, defaultSection);

        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Error testing refresh activity");
            fail();
        }

    }
}