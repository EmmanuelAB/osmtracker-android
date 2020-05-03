package net.osmtracker.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.osmtracker.R;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.PowerMockUtils;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Hashtable;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;


import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith( PowerMockRunner.class )
public class ButtonsPresetsTest {

    @Test
    @PrepareForTest(ButtonsPresets.class)
    public void refreshActivityTest() {
        try {
            ButtonsPresets mockActivity = mock(ButtonsPresets.class);

            injectMockHashtable(mockActivity, 1); // to check it's reset actually

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
    public void initializeAttributesTest() {
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

    @Test
    @PrepareForTest({File.class, Preferences.class, Environment.class})
    public void listLayoutsTest(){
        for (int i = 0; i <= 1 ; i++) {
            try {
                doTestListLayouts(i);
            }
            catch (Exception e){
                e.printStackTrace();
                fail();
            }
        }
    }

    // TODO: make this test complete after refactoring the method,
    //  now tests only a small part because couldn't be tested deeply
    //  without refactoring or making it public
    private void doTestListLayouts(int numberOfDownloadedLayouts) throws Exception {
        ButtonsPresets mockActivity = mock(ButtonsPresets.class);

        mockStatic(Environment.class); // avoid getExternal call

        TextView mockEmpyText = mock(TextView.class);
        when(mockActivity.findViewById(R.id.btnpre_empty)).thenReturn(mockEmpyText);

        CheckBox mockDefaultcheckBox = mock(CheckBox.class);
        when(mockDefaultcheckBox.getText()).thenReturn("foo");
        when(mockActivity.findViewById(R.id.def_layout)).thenReturn(mockDefaultcheckBox);

        injectMockHashtable(mockActivity, numberOfDownloadedLayouts);

        callListLayouts(mockActivity, null);

        int expectedVisibility = (numberOfDownloadedLayouts > 1) ? View.INVISIBLE : View.VISIBLE;
        verify(mockEmpyText).setVisibility(expectedVisibility);




    }

    private void callListLayouts(ButtonsPresets mockActivity, LinearLayout mockRootLayout) {
        try {
            Method m = ButtonsPresets.class.getDeclaredMethod("listLayouts", LinearLayout.class);
            m.setAccessible(true);
            m.invoke(mockActivity, mockRootLayout);
        }catch (Exception e){
            e.printStackTrace();
            fail();
        }
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

    private void injectMockHashtable(ButtonsPresets mockActivity, int numberOfEntries) {
        Hashtable internalHash = new Hashtable<String,String>();
        for (int i = 0; i < numberOfEntries; i++) {
            internalHash.put("foo", "bar");
        }
        Whitebox.setInternalState(mockActivity.getClass(), "layoutsFileNames", internalHash);
    }
}