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

package me.toptas.fancyshowcasesample

import android.graphics.Color
import androidx.test.rule.ActivityTestRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import android.view.Gravity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.LargeTest
import com.example.espressorobot.robot
import me.toptas.fancyshowcase.FancyShowCaseView
import me.toptas.fancyshowcase.internal.FancyImageView.Companion.DISABLE_ANIMATIONS_FOR_TESTING
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class FancyShowCaseViewTest {

    @get:Rule
    val rule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    @Before
    fun before() {
        DISABLE_ANIMATIONS_FOR_TESTING = true
    }

    @Test
    fun noFocus() {
        robot {
            clickButton(R.id.btn_simple)
            checkFancyShowCase()
            matchText(R.id.fscv_title, "No Focus")
        }
    }

    @Test
    fun focus() {
        robot {
            resetAllShowOnce(rule.activity)
            clickButton(R.id.btn_focus)
            checkFancyShowCase()
            matchText(R.id.fscv_title, "Focus on View only once")
        }
    }

    @Test
    fun titleSpanned() {
        robot {
            clickButton(R.id.btn_spanned)
            checkFancyShowCase()
            matchText(R.id.fscv_title, "Spanned")
        }
    }

    @Test
    fun titleSize() {
        robot {
            clickButton(R.id.btn_title_size)
            checkFancyShowCase()
            checkTextSize(rule.activity, 48)
        }
    }


    @Test
    fun titleGravity() {
        robot {
            clickButton(R.id.btn_focus2)
            checkFancyShowCase()
            checkTextGravity(rule.activity, Gravity.BOTTOM or Gravity.CENTER)
        }
    }


    @Test
    fun focusBorderSize() {
        robot {
            clickButton(R.id.btn_focus_rect_color)
            checkFancyShowCase()
        }
    }

    @Test
    fun showOnce() {
        robot {
            resetAllShowOnce(rule.activity)
            clickButton(R.id.btn_focus)
            checkFancyShowCase()
            matchText(R.id.fscv_title, "Focus on View only once")
            clickButton(R.id.btn_focus)
            sleep()
            checkFancyShowCaseNotVisible(rule.activity)
            Assert.assertTrue(FancyShowCaseView.isShownBefore(rule.activity, "id0"))
        }
    }

    @Test
    fun backgroundAndTitleColor() {
        robot {
            clickButton(R.id.btn_background_color)
            checkFancyShowCase()
            checkTextColor(rule.activity, Color.parseColor("#00ff00"))
            checkTextSize(rule.activity, 24)
        }
    }


    @Test
    fun customView() {
        robot {
            swipeUp(R.id.scrollView)
            sleep()
            clickButton(R.id.btn_custom_view)
            isVisible(R.id.iv_custom_view)
            clickButton(R.id.btn_action_1)
        }
    }

    fun sleep(millis: Long = 500) = apply {
        Thread.sleep(millis)
    }

    private fun swipeDown(id: Int) {
        onView(withId(id)).perform(ViewActions.swipeDown())
    }

    private fun swipeUp(id: Int) {
        onView(withId(id)).perform(ViewActions.swipeUp())
    }


/*    @Test
    fun enterAnimation() {
    }

    @Test
    fun exitAnimation() {
    }

    @Test
    fun animationListener() {
    }

    @Test
    fun closeOnTouch() {
    }

    @Test
    fun enableTouchOnFocusedView() {
    }

    @Test
    fun fitSystemWindows() {
    }

    @Test
    fun focusShape() {
    }

    @Test
    fun focusRectAtPosition() {
    }

    @Test
    fun focusCircleAtPosition() {
    }

    @Test
    fun dismissListener() {
    }

    @Test
    fun roundRectRadius() {
    }

    @Test
    fun disableFocusAnimation() {
    }

    @Test
    fun focusAnimationMaxValue() {
    }

    @Test
    fun focusAnimationStep() {
    }

    @Test
    fun delay() {
    }*/


}