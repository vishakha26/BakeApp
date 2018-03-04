package com.udacitypro.bakeapp;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.udacitypro.bakeapp.R.id.recipe_detail_list_container;

;

@RunWith(AndroidJUnit4.class)
public class DisplayMainListTest {

    private static final String TAG = "DisplayMainListTest";

    @Rule
    public ActivityTestRule<DisplayMainList> mDisplayMainListActivityTestRule
            = new ActivityTestRule<>(DisplayMainList.class);

    @Test
    public void clicksMainList_ChecksIfMainActivityOpens() {


        onView(withId(R.id.main_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(2, click()));

        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            Log.e(TAG, "clicksMainList_ChecksIfMainActivityOpens: ", e);
        }

        onView(withId(recipe_detail_list_container)).check(matches(isDisplayed()));
    }
}
