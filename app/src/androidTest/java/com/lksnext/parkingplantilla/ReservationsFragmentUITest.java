package com.lksnext.parkingplantilla;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.mockito.Mockito.verify;

import android.os.Bundle;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.lksnext.parkingplantilla.view.fragment.ReservationsFragment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ReservationsFragmentUITest {

    @Before
    public void launchFragment() {
        FragmentScenario.launchInContainer(
                ReservationsFragment.class,
                null,
                R.style.Theme_ParkingLKS
        );
    }

    @Test
    public void testRecyclerViewIsDisplayed() {
        onView(withId(R.id.recycler_view_reservations)).check(matches(isDisplayed()));
    }

    @Test
    public void testFilterButtonsAreDisplayedAndClickable() {
        onView(withId(R.id.btn_filter_terminadas)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_filter_siguientes)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_filter_encurso)).check(matches(isDisplayed()));

        onView(withId(R.id.btn_filter_terminadas)).perform(click());
        onView(withId(R.id.btn_filter_siguientes)).perform(click());
        onView(withId(R.id.btn_filter_encurso)).perform(click());
    }
    public void testButtonDeleteReservationIsDisplayedAndClickable() {

        onView(withId(R.id.recycler_view_reservations))
                .perform(click());
    }


}
