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

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView

import me.toptas.fancyshowcase.FancyShowCaseQueue
import me.toptas.fancyshowcase.FancyShowCaseView
import me.toptas.fancyshowcase.listener.OnViewInflateListener
import me.toptas.fancyshowcasesample.databinding.ActivityAnimatedBinding


class AnimatedActivity : BaseActivity() {

    private lateinit var binding: ActivityAnimatedBinding
    private lateinit var queue: FancyShowCaseQueue
    private lateinit var fancyView: FancyShowCaseView
    private lateinit var fancyView2: FancyShowCaseView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnimatedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fancyView = FancyShowCaseView.Builder(this)
                .focusOn(binding.btnFocus)
                .customView(R.layout.layout_animated_view, object : OnViewInflateListener {
                    override fun onViewInflated(view: View) {
                        setAnimatedContent(view, fancyView)
                    }
                })
                .build()

        fancyView2 = FancyShowCaseView.Builder(this)
                .focusOn(binding.btnFocus2)
                .customView(R.layout.layout_animated_view, object : OnViewInflateListener {
                    override fun onViewInflated(view: View) {
                        setAnimatedContent(view, fancyView2)
                    }
                })
                .build()

        binding.btnFocus.setOnClickListener {
            queue = FancyShowCaseQueue().apply {
                add(fancyView)
                add(fancyView2)
                show()
            }
        }


    }

    private fun setAnimatedContent(view: View, fancyShowCaseView: FancyShowCaseView) {
        Handler().postDelayed({
            val tvMain = view.findViewById<View>(R.id.tvMain) as TextView
            val tvSub = view.findViewById<View>(R.id.tvSub) as TextView
            val tvNext = view.findViewById<View>(R.id.btn_next) as TextView
            val tvDismiss = view.findViewById<View>(R.id.btn_dismiss) as TextView

            if (fancyShowCaseView == fancyView2) {
                tvMain.text = "My Fancy Title 2"
                tvSub.text = "My fancy description can be a longer text.2"
                tvNext.text = "Close"
            }

            tvNext.setOnClickListener { fancyShowCaseView.hide() }

            tvDismiss.setOnClickListener { queue.cancel(true) }

            val mainAnimation = AnimationUtils.loadAnimation(this@AnimatedActivity, R.anim.slide_in_left)
            mainAnimation.fillAfter = true

            val subAnimation = AnimationUtils.loadAnimation(this@AnimatedActivity, R.anim.slide_in_left)
            subAnimation.fillAfter = true
            tvMain.startAnimation(mainAnimation)
            Handler().postDelayed({ tvSub.startAnimation(subAnimation) }, 80)
        }, 200)
    }
}
