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
import android.view.View
import java.util.ArrayList
import me.toptas.fancyshowcase.FancyShowCaseView
import android.os.Build
import android.view.ViewTreeObserver
import androidx.recyclerview.widget.LinearLayoutManager
import me.toptas.fancyshowcasesample.databinding.ActivityRecyclerViewBinding


class RecyclerViewActivity : BaseActivity() {

    private lateinit var binding: ActivityRecyclerViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecyclerViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val modelList = ArrayList<MyModel>()
        for (i in 0..24) {
            modelList.add(MyModel("Item $i"))
        }

        val layoutManager = LinearLayoutManager(this)
        val adapter = MyRecyclerViewAdapter(modelList)
        adapter.setClickListener(View.OnClickListener { v ->
            focus(v)
        })

        binding.recyclerView.adapter = adapter

        binding.recyclerView.layoutManager = layoutManager

        binding.recyclerView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val width = binding.recyclerView.width
                val height = binding.recyclerView.height
                if (width > 0 && height > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        binding.recyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    } else {
                        binding.recyclerView.viewTreeObserver.removeGlobalOnLayoutListener(this)
                    }
                }

                focus(layoutManager.findViewByPosition(2)!!.findViewById(R.id.ivIcon))
            }
        })
    }

    private fun focus(v: View) {
        FancyShowCaseView.Builder(this@RecyclerViewActivity)
                .focusOn(v)
                .title("Focus RecyclerView Items")
                .enableAutoTextPosition()
                .build()
                .show()
    }
}
