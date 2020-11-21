package com.neusoft.mc2.setting.base

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * @author by chenhongrui on 2019/3/22
 *
 * 内容摘要: RecyclerView.Adapter的封装
 * 需要在构造方法中传入参数，复写bindData方法实现item即可
 *
 * 版权所有：Semisky
 * 修改内容：
 * 修改日期
 */
abstract class BaseRecyclerAdapter<T>(
    protected var mContext: Context,
    private val mLayoutID: Int,
    var adapterData: List<T>
) : RecyclerView.Adapter<BaseViewHolder>() {

    protected var itemClickListener: IItemClickListener? = null
    protected var itemLongClickListener: IItemLongClickListener? = null

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        position: Int
    ): BaseViewHolder {
        val inflate = LayoutInflater.from(mContext).inflate(mLayoutID, viewGroup, false)
        return BaseViewHolder(inflate)
    }

    override fun onBindViewHolder(
        baseViewHolder: BaseViewHolder,
        position: Int
    ) {
        bindData(baseViewHolder, adapterData[position], position)
    }

    override fun getItemCount(): Int {
        return adapterData.size
    }

    /**
     * 将必要参数传递出去
     *
     * @param holder
     * @param data
     * @param position
     */
    protected abstract fun bindData(holder: BaseViewHolder, data: T, position: Int)

    fun setOnItemClickListener(listener: IItemClickListener) {
        itemClickListener = listener
    }

    fun setOnItemLongClickListener(listener: IItemLongClickListener) {
        itemLongClickListener = listener
    }

    interface IItemClickListener {
        fun itemClickListener(position: Int, data: Any)
    }

    interface IItemLongClickListener {
        fun itemLongClickListener(position: Int, data: Any)
    }
}