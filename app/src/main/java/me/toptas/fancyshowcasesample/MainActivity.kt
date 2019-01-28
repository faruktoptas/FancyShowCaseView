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

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.util.TypedValue
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import me.toptas.fancyshowcase.FancyShowCaseView
import me.toptas.fancyshowcase.FocusShape
import me.toptas.fancyshowcase.listener.DismissListener
import me.toptas.fancyshowcase.listener.OnViewInflateListener

class MainActivity : BaseActivity() {

    private var mFancyShowCaseView: FancyShowCaseView? = null

    private var mClickListener: View.OnClickListener = View.OnClickListener {
        mFancyShowCaseView?.hide()
        mFancyShowCaseView = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_simple.setOnClickListener {
            FancyShowCaseView.Builder(this)
                    .title("No Focus")
                    .build()
                    .show()
        }

        //Shows a FancyShowCaseView that focus on a vie
        btn_focus.setOnClickListener {
            FancyShowCaseView.Builder(this)
                    .focusOn(it)
                    .title("Focus on View only once")
                    .showOnce("id0")
                    .build()
                    .show()
        }

        // Set title with spanned
        val spanned: Spanned = Html.fromHtml("<font color='#ff0000'>Spanned</font>")
        btn_spanned.setOnClickListener {
            FancyShowCaseView.Builder(this)
                    .focusOn(it)
                    .title(spanned)
                    .enableAutoTextPosition()
                    .build()
                    .show()
        }

        // Set title size
        btn_title_size.setOnClickListener {
            FancyShowCaseView.Builder(this)
                    .focusOn(it)
                    .title("Title size")
                    .titleSize(48, TypedValue.COMPLEX_UNIT_SP)
                    .build()
                    .show()
        }

        //Shows a FancyShowCaseView with rounded rect focus shape
        btn_rounded_rect.setOnClickListener {
            FancyShowCaseView.Builder(this)
                    .focusOn(it)
                    .focusShape(FocusShape.ROUNDED_RECTANGLE)
                    .roundRectRadius(90)
                    .title("Focus on View")
                    .build()
                    .show()
        }


        //Shows a FancyShowCaseView that focus on a view
        btn_focus_dismiss_on_focus_area.setOnClickListener {
            if (FancyShowCaseView.isVisible(this)) {
                Toast.makeText(this, "Clickable button", Toast.LENGTH_SHORT).show()
                FancyShowCaseView.hideCurrent(this)
            } else {
                FancyShowCaseView.Builder(this)
                        .focusOn(findViewById(R.id.btn_focus_dismiss_on_focus_area))
                        .enableTouchOnFocusedView(true)
                        .title("Focus on View \n(dismiss on focus area)")
                        .build()
                        .show()
            }
        }

        //Shows a FancyShowCaseView with rounded rect focus shape
        btn_rounded_rect_dismiss_on_focus_area.setOnClickListener {
            if (FancyShowCaseView.isVisible(this)) {
                Toast.makeText(this, "Clickable button", Toast.LENGTH_SHORT).show()
                FancyShowCaseView.hideCurrent(this)
            } else {
                FancyShowCaseView.Builder(this)
                        .focusOn(it)
                        .focusShape(FocusShape.ROUNDED_RECTANGLE)
                        .roundRectRadius(90)
                        .enableTouchOnFocusedView(true)
                        .title("Focus on View \n(dismiss on focus area)")
                        .build()
                        .show()
            }
        }

        //Shows FancyShowCaseView with focusCircleRadiusFactor 1.5 and title gravity
        btn_focus2.setOnClickListener {
            FancyShowCaseView.Builder(this)
                    .focusOn(it)
                    .focusCircleRadiusFactor(1.5)
                    .title("Focus on View with larger circle")
                    .focusBorderColor(Color.GREEN)
                    .titleStyle(0, Gravity.BOTTOM or Gravity.CENTER)
                    .build()
                    .show()
        }

        //Shows FancyShowCaseView at specific position (round rectangle shape)
        btn_rect_position.setOnClickListener {
            FancyShowCaseView.Builder(this)
                    .title("Focus on larger view")
                    .focusRectAtPosition(260, 85, 480, 80)
                    .roundRectRadius(60)
                    .dismissListener(object : DismissListener {
                        override fun onDismiss(id: String?) {

                        }

                        override fun onSkipped(id: String?) {

                        }
                    })
                    .build()
                    .show()
        }

        //Shows a FancyShowCaseView that focuses on a larger view
        btn_focus_rect_color.setOnClickListener {
            FancyShowCaseView.Builder(this)
                    .focusOn(it)
                    .title("Focus on larger view")
                    .focusShape(FocusShape.ROUNDED_RECTANGLE)
                    .roundRectRadius(50)
                    .focusBorderSize(5)
                    .focusBorderColor(Color.RED)
                    .titleStyle(0, Gravity.TOP)
                    .build()
                    .show()
        }

        //Shows a FancyShowCaseView with background color and title style
        btn_background_color.setOnClickListener {
            FancyShowCaseView.Builder(this)
                    .focusOn(it)
                    .backgroundColor(Color.parseColor("#AAff0000"))
                    .title("Background color and title style can be changed")
                    .titleStyle(R.style.MyTitleStyle, Gravity.TOP or Gravity.CENTER)
                    .build()
                    .show()
        }

        //Shows a FancyShowCaseView with border color
        btn_border_color.setOnClickListener {
            FancyShowCaseView.Builder(this)
                    .focusOn(it)
                    .title("Focus border color can be changed")
                    .titleStyle(R.style.MyTitleStyle, Gravity.TOP or Gravity.CENTER)
                    .focusBorderColor(Color.GREEN)
                    .focusBorderSize(10)
                    .build()
                    .show()
        }

        //Shows a FancyShowCaseView with custom enter, exit animations
        btn_anim.setOnClickListener {
            val enterAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_in_top)
            val exitAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_out_bottom)

            val fancyShowCaseView = FancyShowCaseView.Builder(this)
                    .focusOn(it)
                    .title("Custom enter and exit animations.")
                    .enterAnimation(enterAnimation)
                    .exitAnimation(exitAnimation)
                    .build()
            fancyShowCaseView.show()
            exitAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {

                }

                override fun onAnimationEnd(animation: Animation) {
                    fancyShowCaseView.removeView()
                }

                override fun onAnimationRepeat(animation: Animation) {

                }
            })
        }

        //Shows a FancyShowCaseView view custom view inflation
        btn_custom_view.setOnClickListener {
            mFancyShowCaseView = FancyShowCaseView.Builder(this)
                    .focusOn(it)
                    .enableTouchOnFocusedView(true)
                    .customView(R.layout.layout_my_custom_view_arrow, object : OnViewInflateListener {
                        override fun onViewInflated(view: View) {
                            val image = (view as RelativeLayout).findViewById<ImageView>(R.id.iv_custom_view)
                            val params = image.layoutParams as RelativeLayout.LayoutParams
                            val calculator = mFancyShowCaseView!!.focusCalculator!!

                            image.post {
                                params.leftMargin = calculator.circleCenterX - image.width / 2
                                params.topMargin = calculator.circleCenterY - calculator.focusHeight - image.height
                                image.layoutParams = params
                            }

                            view.findViewById<View>(R.id.btn_action_1).setOnClickListener(mClickListener)
                        }
                    })
                    .closeOnTouch(false)
                    .build()
            mFancyShowCaseView?.show()

        }

        btn_custom_view2.setOnClickListener {
            startActivity(Intent(this, AnimatedActivity::class.java))
        }

        btn_no_anim.setOnClickListener {
            mFancyShowCaseView = FancyShowCaseView.Builder(this)
                    .focusOn(it)
                    .disableFocusAnimation()
                    .build()
            mFancyShowCaseView?.show()

        }

        btn_queue.setOnClickListener {
            startActivity(Intent(this, QueueActivity::class.java))
        }

        btn_custom_queue.setOnClickListener {
            startActivity(Intent(this, CustomQueueActivity::class.java))
        }

        btn_another_activity.setOnClickListener {
            startActivity(Intent(this, SecondActivity::class.java))
        }

        btn_recycler_view.setOnClickListener {
            startActivity(Intent(this, RecyclerViewActivity::class.java))
        }

        btn_focus_delay.setOnClickListener {
            FancyShowCaseView.Builder(this)
                    .title("Focus with delay")
                    .focusOn(it)
                    .delay(1000)
                    .build()
                    .show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }


    /**
     * Shows a FancyShowCaseView that focuses to ActionBar items
     *
     * @param item actionbar item to focus
     * @return true
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        FancyShowCaseView.Builder(this)
                .focusOn(findViewById(item.itemId))
                .title("Focus on Actionbar items")
                .build()
                .show()
        return true
    }
}
