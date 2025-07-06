package com.lksnext.parkingplantilla;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;


import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.lksnext.parkingplantilla.view.fragment.RegisterFragment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class RegisterUITest {

    @Before
    public void launchFragment() {

        FragmentScenario.launchInContainer(
                RegisterFragment.class,
                null,
                R.style.Theme_ParkingLKS
        );
    }

    @Test
    public void testElementsAreVisible() {
        onView(withId(R.id.emailEditText)).check(matches(isDisplayed()));
        onView(withId(R.id.usernameEditText)).check(matches(isDisplayed()));
        onView(withId(R.id.phoneEditText)).check(matches(isDisplayed()));
        onView(withId(R.id.passwordEditText)).check(matches(isDisplayed()));
        onView(withId(R.id.confirmPasswordEditText)).check(matches(isDisplayed()));
        onView(withId(R.id.registerButton)).check(matches(isDisplayed()));
        onView(withId(R.id.backButton)).check(matches(isDisplayed()));
    }

    @Test
    public void testInvalidEmailShowsError() {
        onView(withId(R.id.emailEditText)).perform(typeText("invalid-email"), closeSoftKeyboard());
        onView(withId(R.id.usernameEditText)).perform(typeText("TestUser"), closeSoftKeyboard());
        onView(withId(R.id.phoneEditText)).perform(typeText("123456789"), closeSoftKeyboard());
        onView(withId(R.id.passwordEditText)).perform(typeText("password123"), closeSoftKeyboard());
        onView(withId(R.id.confirmPasswordEditText)).perform(typeText("password123"), closeSoftKeyboard());
        onView(withId(R.id.registerButton)).perform(click());

    }

    @Test
    public void testPasswordsDoNotMatchShowsError() {
        onView(withId(R.id.emailEditText)).perform(typeText("test@example.com"), closeSoftKeyboard());
        onView(withId(R.id.usernameEditText)).perform(typeText("TestUser"), closeSoftKeyboard());
        onView(withId(R.id.phoneEditText)).perform(typeText("123456789"), closeSoftKeyboard());
        onView(withId(R.id.passwordEditText)).perform(typeText("password123"), closeSoftKeyboard());
        onView(withId(R.id.confirmPasswordEditText)).perform(typeText("differentPass"), closeSoftKeyboard());
        onView(withId(R.id.registerButton)).perform(click());

    }

    @Test
    public void testClickBackButton() {
        onView(withId(R.id.backButton)).perform(click());

    }
}
