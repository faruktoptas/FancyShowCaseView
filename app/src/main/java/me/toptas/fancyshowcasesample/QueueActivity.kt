package me.toptas.fancyshowcasesample

import android.os.Bundle
import android.widget.Toast

import kotlinx.android.synthetic.main.activity_queue.*
import me.toptas.fancyshowcase.FancyShowCaseQueue
import me.toptas.fancyshowcase.FancyShowCaseView
import me.toptas.fancyshowcase.listener.OnCompleteListener


class QueueActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_queue)

        val fancyShowCaseView1 = FancyShowCaseView.Builder(this)
                .title("First Queue Item")
                .focusOn(btn_queue_1)
                .build()

        val fancyShowCaseView2 = FancyShowCaseView.Builder(this)
                .title("Second Queue Item")
                .focusOn(btn_queue_2)
                .build()

        val fancyShowCaseView3 = FancyShowCaseView.Builder(this)
                .title("Third Queue Item")
                .focusOn(btn_queue_3)
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
