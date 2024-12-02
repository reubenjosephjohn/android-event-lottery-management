package com.example.eventlotterysystem;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


import android.Manifest;
import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.filters.LargeTest;
import androidx.test.rule.GrantPermissionRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;


@RunWith(AndroidJUnit4.class)
@LargeTest

public class EntrantTest {

    @Rule
    public GrantPermissionRule permissionRule =
            GrantPermissionRule.grant(
                    Manifest.permission.CAMERA,
                    Manifest.permission.POST_NOTIFICATIONS,
                    Manifest.permission.INTERNET
            );

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    private UiDevice device;
    private Context context;
    private static final String FIXED_FID = "testfid";

    @Before
    public void setUp() throws Exception {
        // Initialize UiDevice instance
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        context = ApplicationProvider.getApplicationContext();
        // Set the local FID to the fixed value
        Control.setLocalFID(FIXED_FID);
    }

    @After
    public void tearDown() throws Exception {

    }

    /**
     * Test Profile Activity
     * Fulfill US:
     *  US 01.07.01 As an entrant, I want to be identified by my device, so that I don't have to use a username and password
     *  US 01.02.01 As an entrant, I want to provide my personal information such as name, email and optional phone number in the app
     *  US 01.02.02 As an entrant I want to update information such as name, email and contact information on my profile
     *  US 01.03.02 As an entrant I want remove profile picture if need be
     *  US 01.03.03 As an entrant I want my profile picture to be deterministically generated from my profile name if I haven't uploaded a profile image yet.
     * @throws InterruptedException
     * @throws IOException
     **/
    @Test
    public void testEntrantActivity() throws InterruptedException, IOException, UiObjectNotFoundException {
        // Wait for 12 seconds to ensure the delay has passed
        Thread.sleep(12000);
        // Reset current user
        User curUser = Control.getCurrentUser();
        curUser.setName("Default Name");
        curUser.setEmail("user@example.com");
        curUser.setContact("000-000-0000");
        curUser.setPicture(null);
        Control.getInstance().saveUser(curUser);

        // Check if Landing_page activity was launched
        onView(withId(R.id.landing_page))
                .check(matches(isDisplayed()));

        // Jump to profile page
        onView(withId(R.id.profileIcon)).perform(click());

        Thread.sleep(1000);
        // Create profile with personal information
        onView(withId(R.id.name)).perform(typeText("EntrantTest User"), closeSoftKeyboard());
        onView(withId(R.id.email)).perform(typeText("testuser@example.com"), closeSoftKeyboard());
        onView(withId(R.id.user_contact)).perform(typeText("123-456-7890"), closeSoftKeyboard());
        onView(withId(R.id.finish_button)).perform(click());
        onView(withId(R.id.name)).check(matches(withText("EntrantTest User")));
        onView(withId(R.id.email)).check(matches(withText("Email: testuser@example.com")));
        onView(withId(R.id.contact)).check(matches(withText("Contact: 123-456-7890")));

        // Edit profile with new information
        onView(withId(R.id.edit_button)).perform(click());
        onView(withId(R.id.name)).perform(clearText(),typeText("UpdatedName"), closeSoftKeyboard());
        onView(withId(R.id.email)).perform(clearText(),typeText("updateduser@example.com"), closeSoftKeyboard());
        onView(withId(R.id.user_contact)).perform(clearText(),typeText("098-765-4321"), closeSoftKeyboard());
        onView(withId(R.id.finish_button)).perform(click());
        onView(withId(R.id.name)).check(matches(withText("UpdatedName")));
        onView(withId(R.id.email)).check(matches(withText("Email: updateduser@example.com")));
        onView(withId(R.id.contact)).check(matches(withText("Contact: 098-765-4321")));

        // Generate profile picture
        onView(withId(R.id.poster)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.generate_button)).perform(click());
        onView(withId(R.id.poster)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        // Delete image
        onView(withId(R.id.del_button)).perform(click());
        onView(withText("Picture")).perform(click());
        onView(withText("Delete")).perform(click());
        onView(withId(R.id.poster)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

        // Delete Profile
        onView(withId(R.id.del_button)).perform(click());
        onView(withText("Profile")).perform(click());
        onView(withText("Delete")).perform(click());
        onView(withId(R.id.name)).check(matches(withText("Default Name")));
        onView(withId(R.id.email)).check(matches(withText("Email: user@example.com")));
        onView(withId(R.id.contact)).check(matches(withText("Contact: 000-000-0000")));

    }

    /**
     * Test Notification Toggle
     * Fulfill US:
     *  US 01.04.03 As an entrant I want to opt out of receiving notifications from organizers and admin
     **/
    @Test
    public void testOptNotifications() throws InterruptedException {
        // Wait for 12 seconds to ensure the delay has passed
        Thread.sleep(12000);
        // Reset Notification Setting
        User curUser = Control.getCurrentUser();
        curUser.setNotificationSetting(true);
        Control.getInstance().saveUser(curUser);
        // Check if Landing_page activity was launched
        onView(withId(R.id.landing_page))
                .check(matches(isDisplayed()));
        onView(withId(R.id.settingsIcon)).perform(scrollTo());
        onView(withId(R.id.settingsIcon)).perform(click());

        // Check if the switch is initially on
        Espresso.onView(ViewMatchers.withId(R.id.noti_switch))
                .check(ViewAssertions.matches(ViewMatchers.isChecked()));
        // Toggle the switch
        Espresso.onView(ViewMatchers.withId(R.id.noti_switch))
                .perform(ViewActions.click());
        // Check if the switch is now off
        Espresso.onView(ViewMatchers.withId(R.id.noti_switch))
                .check(ViewAssertions.matches(ViewMatchers.isNotChecked()));
        // Toggle the switch again
        Espresso.onView(ViewMatchers.withId(R.id.noti_switch))
                .perform(ViewActions.click());
    }

}
