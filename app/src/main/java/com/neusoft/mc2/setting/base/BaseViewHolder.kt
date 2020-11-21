package com.neusoft.mc2.setting.base

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.util.SparseArray
import android.view.View
import android.view.View.OnLongClickListener
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * @author by chenhongrui on 2019/3/22
 *
 * 内容摘要: recycler Holder类
 * 1.[.getView] 获取指定ID的view
 * 2.可以通过[.setText] [.setImage]
 * 直接设置数据
 * 3.可以通过{[.setOnItemClickListener]
 * {[.setOnItemLongClickListener]}
 * 设置监听
 *
 * 版权所有：Semisky
 * 修改内容：
 * 修改日期
 */
class BaseViewHolder internal constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    private val mViews = SparseArray<View>()
    fun <T : View> getView(viewID: Int): T {
        var view = mViews[viewID]
        if (view == null) {
            view = itemView.findViewById(viewID)
            mViews.put(viewID, view)
        }
        return view as T
    }

    /**
     * 设置TextView数据
     * @param viewID view id
     * @param text 数据
     */
    @SuppressLint("SetTextI18n")
    fun setText(viewID: Int, text: CharSequence?) {
        val view = getView<TextView>(viewID)!!
        view.text = text
    }

    /**
     * 设置ImageView数据
     * @param viewID view id
     * @param drawable 数据
     */
    fun setImage(viewID: Int, drawable: Drawable?) {
        val imageView = getView<ImageView>(viewID)!!
        imageView.background = drawable
    }

    /**
     * 设置view隐藏
     * @param viewID view id
     * @param visibility  View.VISIBLE INVISIBLE GONE
     */
    fun setViewVisibility(viewID: Int, visibility: Int) {
        getView<View>(viewID)!!.visibility = visibility
    }

    /**
     * view点击事件
     * @param viewID view id
     * @param listener listener
     */
    fun setOnItemClickListener(
        viewID: Int,
        listener: View.OnClickListener?
    ) {
        getView<View>(viewID)!!.setOnClickListener(listener)
    }

    /**
     * view长按事件
     * @param viewID view id
     * @param listener listener
     */
    fun setOnItemLongClickListener(viewID: Int, listener: OnLongClickListener?) {
        getView<View>(viewID)!!.setOnLongClickListener(listener)
    }

    /**
     * itemView点击事件
     * @param listener listener
     */
    fun setOnItemClickListener(listener: View.OnClickListener?) {
        itemView.setOnClickListener(listener)
    }

    /**
     * itemView长按事件
     * @param listener listener
     */
    fun setOnItemLongClickListener(listener: OnLongClickListener?) {
        itemView.setOnLongClickListener(listener)
    }
}