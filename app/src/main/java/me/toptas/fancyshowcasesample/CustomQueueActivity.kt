package me.toptas.fancyshowcasesample

import android.os.Bundle
import android.util.Log
import android.view.View

import butterknife.ButterKnife
import kotlinx.android.synthetic.main.activity_queue.*
import me.toptas.fancyshowcase.FancyShowCaseQueue
import me.toptas.fancyshowcase.FancyShowCaseView
import me.toptas.fancyshowcase.OnViewInflateListener

class CustomQueueActivity : BaseActivity() {

    private lateinit var queue: FancyShowCaseQueue

    private var mClickListener: View.OnClickListener = View.OnClickListener {
        Log.d("", "onClick: ")
        queue.current?.hide()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_queue)

        val fancyShowCaseView1 = FancyShowCaseView.Builder(this)
                .title("First Queue Item")
                .focusOn(btn_queue_1)
                .customView(R.layout.layout_my_custom_view, object : OnViewInflateListener {
                    override fun onViewInflated(view: View) {
                        view.findViewById<View>(R.id.btn_action_1).setOnClickListener(mClickListener)
                    }
                })
                .closeOnTouch(false)
                .build()

        val fancyShowCaseView2 = FancyShowCaseView.Builder(this)
                .title("Second Queue Item")
                .focusOn(btn_queue_2)
                .customView(R.layout.layout_my_custom_view, object : OnViewInflateListener {
                    override fun onViewInflated(view: View) {
                        view.findViewById<View>(R.id.btn_action_1).setOnClickListener(mClickListener)
                    }
                })
                .closeOnTouch(false)
                .build()

        val fancyShowCaseView3 = FancyShowCaseView.Builder(this)
                .title("Third Queue Item")
                .focusOn(btn_queue_3!!)
                .customView(R.layout.layout_my_custom_view, object : OnViewInflateListener {
                    override fun onViewInflated(view: View) {
                        view.findViewById<View>(R.id.btn_action_1).setOnClickListener(mClickListener)
                    }
                })
                .closeOnTouch(false)
                .build()

        queue = FancyShowCaseQueue()
                .add(fancyShowCaseView1)
                .add(fancyShowCaseView2)
                .add(fancyShowCaseView3)

        queue.show()
    }

}
