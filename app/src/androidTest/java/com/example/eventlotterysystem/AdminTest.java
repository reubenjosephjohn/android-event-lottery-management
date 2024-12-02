package com.example.eventlotterysystem;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSubstring;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.is;

import android.Manifest;
import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.matcher.ViewMatchers;
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
     * Test the admin Facility Management
     * Fulfill US:
     * US 03.07.01 As an administrator I want to remove facilities that violate app policy
     *
     * @throws InterruptedException
     */
    @Test
    public void testFacilityManagement() throws InterruptedException {
        // Wait for 12 seconds to ensure the delay has passed
        Thread.sleep(12000);
        // set that the user is an admin
        User curUser = Control.getCurrentUser();
        curUser.setAdmin(true);
        Control.getInstance().saveUser(curUser);
        // Create a mock facility
        String uniqueFacilityName = "Mock Facility " + System.currentTimeMillis();
        Facility mockFacility = new Facility(uniqueFacilityName, "Mock Description", curUser.getUserID());
        Control.getInstance().saveFacility(mockFacility);
        // Check if Landing_page activity was launched
        onView(withId(R.id.landing_page))
                .check(matches(isDisplayed()));
        // Long click on the Facility button
        onView(withId(R.id.facilitiesIcon))
                .perform(longClick());

        onView(withId(R.id.facilities_list_view)).check(matches(isDisplayed()));
        onData(hasToString(is(uniqueFacilityName)))
                .inAdapterView(withId(R.id.facilities_list_view))
                .perform(scrollTo(), click());
        Thread.sleep(1000);
        onView(withSubstring(uniqueFacilityName)).check(matches(isDisplayed()));
        onView(withId(R.id.del_button)).perform(click());
        onView(withText("Delete")).perform(click());
        onView(withText(uniqueFacilityName)).check(doesNotExist());
    }

    /**
     * Test the admin User Management
     * Fulfill US:
     *  US 03.05.01 As an administrator, I want to be able to browse profiles.
     *  US 03.06.01 As an administrator, I want to be able to browse images.
     *  US 03.03.01 As an administrator, I want to be able to remove images.
     *  US 03.02.01 As an administrator, I want to be able to remove profiles.
     **/
    @Test
    public void testUserManagement() throws InterruptedException {
        // Wait for 12 seconds to ensure the delay has passed
        Thread.sleep(12000);
        // set that the user is an admin
        User curUser = Control.getCurrentUser();
        curUser.setAdmin(true);
        Control.getInstance().saveUser(curUser);
        // Set a mock user
        String uniqueUserName = "Mock User " + System.currentTimeMillis();
        curUser.setName(uniqueUserName);
        curUser.setEmail("mockuser@example.com");
        curUser.setContact("123-456-7890");
        curUser.generate_picture();
        Control.getInstance().saveUser(curUser);
        // Check if Landing_page activity was launched
        onView(withId(R.id.landing_page))
                .check(matches(isDisplayed()));
        // Long click on the Profile button
        onView(withId(R.id.profileIcon))
                .perform(scrollTo(),longClick());

        onView(withId(R.id.users_list_view)).check(matches(isDisplayed()));

        onData(hasToString(is(uniqueUserName)))
                .inAdapterView(withId(R.id.users_list_view))
                .perform(scrollTo(), click());

        Thread.sleep(1000);
        onView(withSubstring(uniqueUserName)).check(matches(isDisplayed()));
        onView(withId(R.id.imageView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.del_button)).perform(click());
        onView(withText("PICTURE")).perform(click());
        onView(withText("DELETE")).perform(click());
        onView(withId(R.id.imageView)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.del_button)).perform(click());
        onView(withText("PROFILE")).perform(click());
        onView(withText("DELETE")).perform(click());
        onView(withText("Mock User")).check(doesNotExist());
    }

    /**
     * Test the admin Event Management
     * Fulfill US:
     *  US 03.04.01 As an administrator, I want to be able to browse events.
     *  US 03.03.02 As an administrator, I want to be able to remove hashed QR code data
     *  US 03.01.01 As an administrator, I want to be able to remove events.
     * @throws InterruptedException
     */
    @Test
    public void testEventManagement() throws InterruptedException {
        // Wait for 12 seconds to ensure the delay has passed
        Thread.sleep(12000);
        // set that the user is an admin
        User curUser = Control.getCurrentUser();
        curUser.setAdmin(true);
        curUser.generate_picture();
        Control.getInstance().saveUser(curUser);
        String mockPoster = curUser.getPicture();
        // Create a mock event
        String uniqueEventName = "Admin Test Event " + System.currentTimeMillis();
        Event newEvent = new Event(Control.getInstance().getCurrentEventIDForEventCreation(), uniqueEventName, "Mock Description", 3, 2, true);
        newEvent.setCreatorRef(-1);
        newEvent.generateQR();
        newEvent.setPoster(mockPoster);
        Control.getInstance().getEventList().add(newEvent);
        Control.getInstance().saveEvent(newEvent);
        // Check if Landing_page activity was launched
        onView(withId(R.id.landing_page))
                .check(matches(isDisplayed()));
        onView(withId(R.id.eventsIcon))
                .perform(click());
        onView(withText(uniqueEventName)).perform(scrollTo(), click());
        onView(withSubstring(uniqueEventName)).check(matches(isDisplayed()));
        onView(withId(R.id.del_button)).perform(click());
        onView(withText("Poster")).perform(click());
        onView(withId(R.id.del_button)).perform(click());
        onView(withText("QR code")).perform(click());
        onView(withId(R.id.del_button)).perform(click());
        onView(withText("Event")).perform(click());
        onView(withText("Delete")).perform(click());
        onView(withSubstring(uniqueEventName)).check(doesNotExist());




    }
}
