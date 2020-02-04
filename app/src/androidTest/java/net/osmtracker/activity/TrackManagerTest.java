package net.osmtracker.activity;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.ViewAssertion;
import android.support.test.rule.ActivityTestRule;

import net.osmtracker.R;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.*;


import static org.junit.Assert.*;

public class TrackManagerTest {

    @Rule
    public ActivityTestRule<TrackManager> mRule = new ActivityTestRule<>(TrackManager.class);

    @Test
    public void test(){
        onView(withText("footrack")).perform(click());

        onView(withId(R.id.trackdetail_item_description)).perform(typeText("foo bar text"));

        onView(withText("SAVE")).perform(click());

        onView(withText("footrack")).perform(click());

        onView(withId(R.id.trackdetail_item_description)).check(matches(withText("foo bar text")));




    }

}