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
import android.support.v7.widget.LinearLayoutManager
import android.view.View

import java.util.ArrayList

import kotlinx.android.synthetic.main.activity_recycler_view.*
import me.toptas.fancyshowcase.FancyShowCaseView


class RecyclerViewActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_view)

        val modelList = ArrayList<MyModel>()
        for (i in 0..24) {
            modelList.add(MyModel("Item $i"))
        }


        val adapter = MyRecyclerViewAdapter(modelList)
        adapter.setClickListener(View.OnClickListener { v ->
            FancyShowCaseView.Builder(this@RecyclerViewActivity)
                    .focusOn(v)
                    .title("Focus RecyclerView Items")
                    .build()
                    .show()
        })

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

    }
}
