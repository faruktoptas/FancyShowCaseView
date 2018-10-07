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

import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.test.suitebuilder.annotation.LargeTest
import com.example.espressorobot.robot
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class FancyShowCaseViewTest {

    @get:Rule
    val mActivityTestRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

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

    }

    @Test
    fun titleSpanned() {

    }

    @Test
    fun titleSize() {
    }

    @Test
    fun titleStyle() {
    }

    @Test
    fun titleGravity() {
    }

    @Test
    fun focusBorderColor() {
    }

    @Test
    fun focusBorderSize() {
    }

    @Test
    fun showOnce() {
    }

    @Test
    fun backgroundColor() {
    }

    @Test
    fun focusCircleRadiusFactor() {
    }

    @Test
    fun customView() {
    }

    @Test
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
    }


}