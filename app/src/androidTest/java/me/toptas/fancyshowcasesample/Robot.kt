/*
 * Copyright (c) 2018. Faruk Toptaş
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.espressorobot

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.support.test.espresso.Espresso.onData
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.ViewInteraction
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.view.Gravity
import android.widget.TextView
import me.toptas.fancyshowcase.FancyShowCaseView
import me.toptas.fancyshowcasesample.R
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.anything
import org.junit.Assert

/**
 * Created by ftoptas on 21/12/17.
 */

fun robot(func: Robot.() -> Unit) = Robot().apply { func() }

class Robot {

    fun fillEditText(resId: Int, text: String): ViewInteraction =
            onView(withId(resId)).perform(ViewActions.replaceText(text), ViewActions.closeSoftKeyboard())

    fun clickButton(resId: Int): ViewInteraction = onView((withId(resId))).perform(ViewActions.click())

    fun view(resId: Int): ViewInteraction = onView(withId(resId))

    fun matchText(viewInteraction: ViewInteraction, text: String): ViewInteraction = viewInteraction
            .check(ViewAssertions.matches(ViewMatchers.withText(text)))

    fun matchText(resId: Int, text: String): ViewInteraction = matchText(view(resId), text)

    fun clickListItem(listRes: Int, position: Int) {
        onData(anything())
                .inAdapterView(allOf(withId(listRes)))
                .atPosition(position).perform(ViewActions.click())
    }

    fun isVisible(resId: Int) {
        view(resId).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    fun checkFancyShowCase() {
        isVisible(R.id.fscv_id)
    }

    fun checkTextGravity(activity: Activity, gravity: Int) {
        Assert.assertTrue(activity.findViewById<TextView>(R.id.fscv_title).gravity == gravity)
    }

    fun checkTextSize(activity: Activity, size: Int) {
        Assert.assertTrue(activity.findViewById<TextView>(R.id.fscv_title).textSize == size * Resources.getSystem().displayMetrics.density)
    }

    fun checkTextColor(activity: Activity, color: Int) {
        Assert.assertTrue(activity.findViewById<TextView>(R.id.fscv_title).currentTextColor == color)
    }

    fun checkFancyShowCaseNotVisible(activity: Activity) {
        Assert.assertTrue(activity.findViewById<FancyShowCaseView>(R.id.fscv_id) == null)
    }

    fun resetAllShowOnce(context: Context) {
        FancyShowCaseView.resetAllShowOnce(context)
    }

    fun sleep() = apply {
        Thread.sleep(500)
    }


}