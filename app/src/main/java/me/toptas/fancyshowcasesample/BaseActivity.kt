package me.toptas.fancyshowcasesample

import android.support.v7.app.AppCompatActivity

import me.toptas.fancyshowcase.FancyShowCaseView

/**
 * Created by yeldos on 3/29/17.
 */

abstract class BaseActivity : AppCompatActivity() {

    override fun onBackPressed() {
        if (FancyShowCaseView.isVisible(this)) {
            FancyShowCaseView.hideCurrent(this)
        } else {
            super.onBackPressed()
        }
    }
}
