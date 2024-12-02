package com.example.eventlotterysystem;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.Manifest;
import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.UiDevice;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class AdminTest {
    @Rule
    public GrantPermissionRule permissionRule =
            GrantPermissionRule.grant(
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.POST_NOTIFICATIONS,
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

    /**
     * Test the admin activity
     * Fulfill US:
     *  US 03.07.01 As an administrator I want to remove facilities that violate app policy
     * @throws InterruptedException
     */
    @Test
    public void testAdminActivity() throws InterruptedException {
        // Wait for 12 seconds to ensure the delay has passed
        Thread.sleep(12000);
        // set that the user is an admin
        User curUser = Control.getCurrentUser();
        curUser.setAdmin(true);
        Control.getInstance().saveUser(curUser);
        // Create a mock facility
        Facility mockFacility = new Facility("Mock Facility", "Mock Description", curUser.getUserID());
        Control.getInstance().saveFacility(mockFacility);
        // Check if Landing_page activity was launched
        onView(withId(R.id.landing_page))
                .check(matches(isDisplayed()));
        // Long click on the Facility button
        onView(withId(R.id.facilitiesIcon))
                .perform(longClick());
        onView(withId(R.id.facilities_list_view)).check(matches(isDisplayed()));
        onView(withText("Mock Facility")).check(matches(isDisplayed()));
        onView(withText("Mock Facility")).perform(click());
        onView(withId(R.id.del_button)).perform(click());
        onView(withText("Delete")).perform(click());
        onView(withText("Mock Facility")).check(doesNotExist());


    }



}
