package com.example.videostream

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.videostream.presentation.ui.EnterNameActivity
import org.hamcrest.Matchers
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class SignInTest {
    @get:Rule
    var activityScenarioRule = ActivityScenarioRule(EnterNameActivity::class.java)

    @Test
    fun setDisplayNameTest() {
        onView(withId(R.id.enterNameEditText)).perform(
            typeText("UserA"),
            closeSoftKeyboard()
        )

        onView(withId(R.id.enterNameBtn)).perform(
            click()
        )

        onView(withId(R.id.myToolbar)).check(
            ViewAssertions.matches(
                ViewMatchers.hasDescendant(
                    ViewMatchers.withText(Matchers.containsString("Hi UserA"))
                )
            )
        )
    }
}