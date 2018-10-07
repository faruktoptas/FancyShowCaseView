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

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView


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