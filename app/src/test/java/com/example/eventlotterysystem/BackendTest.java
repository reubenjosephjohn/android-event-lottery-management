package com.example.eventlotterysystem;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class BackendTest {

    private Control control;

    @Before
    public void setUp() {
        // Use reflection to inject the mock Firestore instance
        control = Control.getTestInstance();

    }

    @Test
    public void testGetInstance_ReturnsSingletonInstance() {
        Control firstInstance = Control.getInstance();
        Control secondInstance = Control.getInstance();
        assertSame("Control should return the same singleton instance", firstInstance, secondInstance);
    }

    @Test
    public void testGetCurrentUserID_InitialValue() {
        assertEquals("Initial currentUserID should be 0", 0, control.getCurrentUserID());
    }

    @Test
    public void testGetCurrentEventID_InitialValue() {
        assertEquals("Initial currentEventID should be 0", 0, control.getCurrentEventID());
    }

    @Test
    public void testFindUserByID_UserExists() {
        User user = new User(0, "Test user");
        control.getUserList().add(user);

        User result = control.findUserByID(0);

        assertNotNull("User should be found", result);
        assertEquals("User ID should match", 0, result.getUserID());
    }

    @Test
    public void testFindUserByID_UserDoesNotExist() {
        User result = control.findUserByID(99);

        assertNull("User should not be found", result);
    }

    @Test
    public void testFindEventByID_EventExists() {
        Event event = new Event(0, "Test event", "Description", 15,15, false);
        control.getEventList().add(event);

        Event result = control.findEventByID(0);

        assertNotNull("Event should be found", result);
        assertEquals("Event ID should match", 0, result.getEventID());
    }

    @Test
    public void testFindEventByID_EventDoesNotExist() {
        Event result = control.findEventByID(99);

        assertNull("Event should not be found", result);
    }

}
