package me.toptas.fancyshowcasesample

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import butterknife.BindView
import butterknife.ButterKnife

/**
 * Activity for RecyclerView sample
 */
class MyRecyclerViewAdapter(private val mMyModelList: List<MyModel>) : RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder>() {
    private var mClickListener: View.OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_recycler, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val myModel = mMyModelList[position]
        holder.title!!.text = myModel.title
        holder.title!!.setOnClickListener { v ->
            if (mClickListener != null) {
                mClickListener!!.onClick(v)
            }
        }
        holder.imageView!!.setOnClickListener { v ->
            if (mClickListener != null) {
                mClickListener!!.onClick(v)
            }
        }
        holder.imageView!!.visibility = if (position % 5 == 2) View.VISIBLE else View.GONE

    }

    override fun getItemCount(): Int {
        return mMyModelList.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var title: TextView = view.findViewById(R.id.tvMain)
        var imageView: ImageView = view.findViewById(R.id.ivIcon)
    }

    fun setClickListener(clickListener: View.OnClickListener) {
        mClickListener = clickListener
    }
}

data class MyModel(val title: String)