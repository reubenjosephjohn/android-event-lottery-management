package com.example.eventlotterysystem;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
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

public class OrganizerTest {
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
     * Test Facility Activity
     * Fulfill US:
     *  US 02.01.03 As an organizer, I want to create and manage my facility profile.
     *  US 02.01.01 As an organizer I want to create a new event and generate a unique promotional QR code that links to the event description and event poster in the app
     *  US 02.03.01 As an organizer I want to OPTIONALLY limit the number of entrants who can join my waiting list
     *  US 02.02.03 As an organizer I want to enable or disable the geolocation requirement for my event.
     *  US 02.01.02 As an organizer I want to store the generated QR code in my database
     * @throws InterruptedException
     */
    @Test
    public void testOrganizerActivity() throws InterruptedException {
        // Wait for 12 seconds to ensure the delay has passed
        Thread.sleep(12000);
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

        onView(withId(R.id.return_button)).perform(click());

        // Jump to Facility page
        onView(withId(R.id.facilitiesIcon)).perform(click());
        Thread.sleep(1000);

        // Create facility with personal information
        onView(withId(R.id.facility_name_edit)).perform(typeText("Test Facility"), closeSoftKeyboard());
        onView(withId(R.id.facility_description_edit)).perform(typeText("Test Facility Description"), closeSoftKeyboard());
        onView(withId(R.id.finish_button)).perform(click());
        onView(withId(R.id.name)).check(matches(withText("Test Facility")));
        onView(withId(R.id.email)).check(matches(withText("Test Facility Description")));

        // Create Events
        onView(withId(R.id.return_button)).perform(click());
        onView(withId(R.id.eventsIcon)).perform(click());
        onView(withId(R.id.create_button)).perform(click());
        onView(withId(R.id.firstName)).perform(typeText("Test Event"), closeSoftKeyboard());
        onView(withId(R.id.title_edit5)).perform(typeText("Test Event Description"), closeSoftKeyboard());
        onView(withId(R.id.editTextNumber)).perform(typeText("3"), closeSoftKeyboard());
        onView(withId(R.id.editTextNumber2)).perform(typeText("2"), closeSoftKeyboard());
        onView(withId(R.id.registration_start)).perform(typeText("2023-12-01"), closeSoftKeyboard());
        onView(withId(R.id.registration_end)).perform(typeText("2023-12-31"), closeSoftKeyboard());
        onView(withId(R.id.event_start)).perform(typeText("2024-12-01"), closeSoftKeyboard());
        onView(withId(R.id.event_end)).perform(typeText("2024-12-31"), closeSoftKeyboard());
        onView(withId(R.id.location_loc)).perform(click());
        onView(withId(R.id.finish_button)).perform(click());
        onView(withId(R.id.imageViewQRCode)).check(matches(isDisplayed()));
        pressBack();
        onView(withId(R.id.ownedEvents)).check(matches(isDisplayed()));
        onView(withText("Test Event")).perform(click());

        pressBack();
        pressBack();
        onView(withId(R.id.facilitiesIcon)).perform(click());
        Thread.sleep(1000);

        // Delete facility
        onView(withId(R.id.del_button)).perform(click());
        onView(withText("Delete")).perform(click());
        onView(withId(R.id.landing_page)).check(matches(isDisplayed()));

        // Delete Profile
        onView(withId(R.id.profileIcon)).perform(click());
        onView(withId(R.id.del_button)).perform(click());
        onView(withText("Profile")).perform(click());
        onView(withText("Delete")).perform(click());
        onView(withId(R.id.name)).check(matches(withText("Default Name")));
        onView(withId(R.id.email)).check(matches(withText("Email: user@example.com")));
        onView(withId(R.id.contact)).check(matches(withText("Contact: 000-000-0000")));
    }
}
