package net.osmtracker.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.widget.LinearLayout;

import net.osmtracker.R;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.lang.reflect.Method;
import java.util.Hashtable;

import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.mock;


import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith( PowerMockRunner.class )
public class ButtonsPresetsTest {

    @Test
    @PrepareForTest(ButtonsPresets.class)
    public void refreshActivityTest() {
        try {
            ButtonsPresets mockActivity = mock(ButtonsPresets.class);

            injectOneEntryHashtable(mockActivity); // to check it's reset actually

            LinearLayout downloadedLayouts = mock(LinearLayout.class);
            LinearLayout defaultSection = mock(LinearLayout.class);
            when(mockActivity,"findViewById",R.id.list_layouts).thenReturn(downloadedLayouts);
            when(mockActivity,"findViewById",R.id.buttons_presets).thenReturn(defaultSection);

            // Actual method call
            when(mockActivity,"refreshActivity").thenCallRealMethod();
            mockActivity.refreshActivity();

            Hashtable internalHash = Whitebox.getInternalState(mockActivity.getClass(), "layoutsFileNames");
            assertEquals(0, internalHash.size());

            // Check internal method calls happen
            verifyPrivate(mockActivity).invoke("listLayouts", downloadedLayouts);
            verifyPrivate(mockActivity).invoke("checkCurrentLayout", downloadedLayouts, defaultSection);

        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Error testing refresh activity");
            fail();
        }

    }

    @Test
    @PrepareForTest(PreferenceManager.class)
    public void testInitializeAttributes() {
        ButtonsPresets mockActivity = mock(ButtonsPresets.class);

        // Setup the mock resources
        Resources mockResources = mock(Resources.class);
        when(mockResources.getString(R.string.prefs_ui_buttons_layout)).thenReturn("fooTitle");
        when(mockActivity.getResources()).thenReturn(mockResources);

        // Mock the shared preferences
        mockDefaultSharedPreferences(mockActivity);

        // Call actual method
        callInitializeAttributes(mockActivity);

        // Check internal methods calls
        verify(mockActivity).setTitle("fooTitle");
        verify(mockActivity).setContentView(R.layout.buttons_presets);

        // Check attributes are set
        checkAttributesAfterInitialization(mockActivity);
    }

    private void mockDefaultSharedPreferences(Context context) {
        SharedPreferences mockPrefs = mock(SharedPreferences.class);
        mockStatic(PreferenceManager.class);
        when(PreferenceManager.getDefaultSharedPreferences(context)).thenReturn(mockPrefs);
    }

    private void checkAttributesAfterInitialization(ButtonsPresets mockActivity) {
        Hashtable hashtable = Whitebox.getInternalState(mockActivity.getClass(), "layoutsFileNames");
        Object listener = Whitebox.getInternalState(mockActivity, "listener");
        Object sharedPrefs = Whitebox.getInternalState(mockActivity, "prefs");
        String storageDir = Whitebox.getInternalState(mockActivity.getClass(),"storageDir");

        assertTrue(sharedPrefs instanceof SharedPreferences);
        assertEquals(0, hashtable.size());
        assertNotNull(listener);
        assertEquals("//osmtracker", storageDir);
    }

    private void callInitializeAttributes(ButtonsPresets mockActivity) {
        try {
            Method m = ButtonsPresets.class.getDeclaredMethod("initializeAttributes");
            m.setAccessible(true);
            m.invoke(mockActivity);
        }catch (Exception e){
            e.printStackTrace();
            fail();
        }
    }

    private void injectOneEntryHashtable(ButtonsPresets mockActivity) {
        Hashtable internalHash = new Hashtable<String,String>();
        internalHash.put("foo", "bar");
        Whitebox.setInternalState(mockActivity.getClass(), "layoutsFileNames", internalHash);
    }
}