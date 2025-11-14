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
import android.widget.Toast

import me.toptas.fancyshowcase.FancyShowCaseQueue
import me.toptas.fancyshowcase.FancyShowCaseView
import me.toptas.fancyshowcase.listener.OnCompleteListener
import me.toptas.fancyshowcasesample.databinding.ActivityQueueBinding


class QueueActivity : BaseActivity() {

    private lateinit var binding: ActivityQueueBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQueueBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fancyShowCaseView1 = FancyShowCaseView.Builder(this)
                .title("First Queue Item")
                .focusOn(binding.btnQueue1)
                .build()

        val fancyShowCaseView2 = FancyShowCaseView.Builder(this)
                .title("Second Queue Item")
                .focusOn(binding.btnQueue2)
                .build()

        val fancyShowCaseView3 = FancyShowCaseView.Builder(this)
                .title("Third Queue Item")
                .focusOn(binding.btnQueue3)
                .build()

        val queue = FancyShowCaseQueue()
                .add(fancyShowCaseView1)
                .add(fancyShowCaseView2)
                .add(fancyShowCaseView3)
        queue.show()
        queue.completeListener = object : OnCompleteListener {
            override fun onComplete() {
                Toast.makeText(this@QueueActivity, "Finished", Toast.LENGTH_SHORT).show()
            }
        }

    }
}
