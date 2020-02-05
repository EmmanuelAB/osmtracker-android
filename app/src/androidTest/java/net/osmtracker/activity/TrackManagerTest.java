package net.osmtracker.activity;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.ViewAssertion;
import android.support.test.espresso.matcher.PreferenceMatchers;
import android.support.test.rule.ActivityTestRule;
import android.util.Log;

import net.osmtracker.R;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.PreferenceMatchers.withSummary;
import static android.support.test.espresso.matcher.PreferenceMatchers.withSummaryText;
import static android.support.test.espresso.matcher.ViewMatchers.*;


import static org.junit.Assert.*;

public class TrackManagerTest {

    private void log(String s){Log.e("#",">>> "+s);}

    @Rule
    public ActivityTestRule<TrackManager> mRule = new ActivityTestRule<>(TrackManager.class);

    @Test
    public void test(){

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        onView(withText("Settings")).perform(click());

        onView((withText("Voice record duration"))).perform(click());

        onView((withText("10"))).perform(click());

        Espresso.pressBack();

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        onView(withText("Settings")).perform(click());

        PreferenceMatchers.withTitleText("Voice record duration").matches(withSummaryText("10 seconds"));







    }

}