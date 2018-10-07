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
import android.view.Menu
import android.view.MenuItem
import android.view.View

import kotlinx.android.synthetic.main.activity_second.*
import me.toptas.fancyshowcase.FancyShowCaseView

class SecondActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        setSupportActionBar(toolbar)

        focusOnButton()

        button1.setOnClickListener {
            focusOnButton()
        }

        button2.setOnClickListener {
            if (toolbar.visibility == View.VISIBLE) {
                toolbar.visibility = View.GONE
            } else {
                toolbar.visibility = View.VISIBLE
            }
        }
    }


    private fun focusOnButton() {
        FancyShowCaseView.Builder(this@SecondActivity)
                .focusOn(button1)
                .title("Focus a view")
                .fitSystemWindows(true)
                .build()
                .show()
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
                .fitSystemWindows(true)
                .build()
                .show()
        return true
    }
}
