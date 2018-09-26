package me.toptas.fancyshowcasesample

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View

import java.util.ArrayList

import butterknife.BindView
import butterknife.ButterKnife
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
