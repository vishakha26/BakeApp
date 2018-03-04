package com.udacitypro.bakeapp;


import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class CheckIfIngredientListExistTablet {

    @Rule
    public ActivityTestRule<DisplayMainList> mActivityTestRule = new ActivityTestRule<>(DisplayMainList.class);

    @Test
    public void checkIfIngredientListExistTablet() {

        onView(withId(R.id.main_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));


        ViewInteraction textView = onView(
                allOf(withId(R.id.ingr_items), withText("2  CUP  ~  Graham Cracker crumbs\n6  TBLSP  ~  unsalted butter, melted\n0.5  CUP  ~  granulated sugar\n1.5  TSP  ~  salt\n5  TBLSP  ~  vanilla\n1  K  ~  Nutella or other chocolate-hazelnut spread\n500  G  ~  Mascapone Cheese(room temperature)\n1  CUP  ~  heavy cream(cold)\n4  OZ  ~  cream cheese(softened)\n"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.recipe_detail_recycler_view),
                                        0),
                                2),
                        isDisplayed()));
        textView.check(matches(isDisplayed()));

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
