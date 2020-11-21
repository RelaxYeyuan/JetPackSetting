package com.neusoft.mc2.setting.adapter

import android.content.Context
import android.util.Log
import android.view.View
import com.neusoft.mc2.setting.R
import com.neusoft.mc2.setting.base.BaseRecyclerAdapter
import com.neusoft.mc2.setting.base.BaseViewHolder
import com.neusoft.mc2.setting.data.bean.BluetoothEntity
import kotlin.collections.ArrayList

private const val TAG = "BtSearchAdapter"

class BtSearchAdapter(
    context: Context,
    id: Int,
    dataList: ArrayList<BluetoothEntity>,
    private val listener: IItemBtClickListener
) : BaseRecyclerAdapter<BluetoothEntity>(context, id, dataList) {

    override fun bindData(holder: BaseViewHolder, data: BluetoothEntity, position: Int) {
        Log.d(TAG, "bindData: ${data.name} ${data.address}")
        holder.setText(R.id.tvItemBtName, data.name)
        holder.setViewVisibility(R.id.ivBtOk, View.GONE)
        holder.setViewVisibility(R.id.ivBtInformation, View.GONE)

        holder.setOnItemClickListener(View.OnClickListener { listener.itemClickListener(data) })
    }

    interface IItemBtClickListener {
        fun itemClickListener(bluetoothEntity: BluetoothEntity)
    }
}
