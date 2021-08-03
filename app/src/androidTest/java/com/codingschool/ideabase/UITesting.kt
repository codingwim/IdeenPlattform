package com.codingschool.ideabase

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.core.IsNot

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class UITesting {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.codingschool.ideabase", appContext.packageName)
    }
    @Test
    fun loginValid() {

        Espresso.onView(ViewMatchers.withId(R.id.etUsername))
            .perform(ViewActions.typeText("nk@gmail.com"))

        Espresso.onView(ViewMatchers.withId(R.id.etPassword))
            .perform(ViewActions.typeText("123456"))

        Espresso.onView(withId(R.id.btLogin))
            .perform(ViewActions.click())




    }
    @Test
    fun loginNotValid(){
        Espresso.onView(ViewMatchers.withId(R.id.etUsername))
            .perform(ViewActions.typeText("niko123"))
        Espresso.onView(ViewMatchers.withId(R.id.etPassword))
            .perform(ViewActions.typeText("12345"))



    }

    @Test
    fun RegisterValid(){
        Espresso.onView(ViewMatchers.withId(R.id.etFirstname))
            .perform(ViewActions.typeText("Niko"))
        Espresso.onView(ViewMatchers.withId(R.id.etLastname))
            .perform(ViewActions.typeText("Kuschnig"))

        Espresso.onView(ViewMatchers.withId(R.id.etEmail))
            .perform(ViewActions.typeText("nk@gmx.at"))

        Espresso.onView(ViewMatchers.withId(R.id.etPassword))
            .perform(ViewActions.typeText("123456"))




        Espresso.onView(ViewMatchers.withId(R.id.etPasswordagain))
            .perform(ViewActions.typeText("123456"))


    }
    @Test
    fun RegisterNotValid(){
        Espresso.onView(ViewMatchers.withId(R.id.etFirstname))
            .perform(ViewActions.typeText("Niko"))
        Espresso.onView(ViewMatchers.withId(R.id.etLastname))
            .perform(ViewActions.typeText("Kuschnig"))
        Espresso.onView(ViewMatchers.withId(R.id.etEmail))
            .perform(ViewActions.typeText("nk.gmx.at"))

        Espresso.onView(ViewMatchers.withId(R.id.etPassword))
            .perform(ViewActions.typeText("123456"))




        Espresso.onView(ViewMatchers.withId(R.id.etPasswordagain))
            .perform(ViewActions.typeText("12345"))

    }
    @Test
    fun editProfValid(){
        Espresso.onView(ViewMatchers.withId(R.id.etFirstname))
            .perform(ViewActions.typeText("Niko"))

        Espresso.onView(ViewMatchers.withId(R.id.etLastname))
            .perform(ViewActions.typeText("Kuschnig"))
        Espresso.onView(ViewMatchers.withId(R.id.etEmail))
            .perform(ViewActions.typeText("nk@gmx.at"))

        Espresso.onView(ViewMatchers.withId(R.id.etPassword))
            .perform(ViewActions.typeText("123456"))




        Espresso.onView(ViewMatchers.withId(R.id.etPasswordagain))
            .perform(ViewActions.typeText("123456"))
    }


    @Test
    fun editProfNotvalid(){

        Espresso.onView(ViewMatchers.withId(R.id.etFirstname))
            .perform(ViewActions.typeText("Niko"))
        Espresso.onView(ViewMatchers.withId(R.id.etLastname))
            .perform(ViewActions.typeText("Kuschnig"))
        Espresso.onView(ViewMatchers.withId(R.id.etEmail))
            .perform(ViewActions.typeText("nk.gmx.at"))

        Espresso.onView(ViewMatchers.withId(R.id.etPassword))
            .perform(ViewActions.typeText("123456"))




        Espresso.onView(ViewMatchers.withId(R.id.etPasswordagain))
            .perform(ViewActions.typeText("12345"))


    }

}