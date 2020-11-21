package com.neusoft.mc2.setting.ui.fragmentSystemView

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.neusoft.mc2.setting.R
import com.neusoft.mc2.setting.base.BaseRecyclerAdapter
import com.neusoft.mc2.setting.base.BaseViewHolder
import com.neusoft.mc2.setting.data.bean.AboutEntity
import com.neusoft.mc2.setting.ui.custom.FullyLinearLayoutManager
import java.lang.Exception

private const val TAG = "AboutFragment"

class AboutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dataList = ArrayList<AboutEntity>()
        dataList.add(AboutEntity("Hardware Version", "123"))
        dataList.add(AboutEntity("Software Version", "123"))
        dataList.add(AboutEntity("Bluetooth Version", "123"))
        dataList.add(AboutEntity("Bluetooth Address", "123"))
        dataList.add(AboutEntity("WLAN NAC", "123"))
        dataList.add(AboutEntity("XDSN ICCID", "123"))
        dataList.add(AboutEntity("VR Version", "123"))
        dataList.add(AboutEntity("VIN Version", "123"))

        view.findViewById<RecyclerView>(R.id.recyclerViewAbout).run {
            Log.d(TAG, "onViewCreated: ${dataList.size}")
            val aboutRecyclerView =
                AboutRecyclerView(requireContext(), R.layout.item_system_about, dataList)
            layoutManager = FullyLinearLayoutManager(requireContext())
            adapter = aboutRecyclerView
        }
    }

    class AboutRecyclerView(
        val context: Context,
        layoutID: Int,
        data: ArrayList<AboutEntity>
    ) : BaseRecyclerAdapter<AboutEntity>(context, layoutID, data) {

        private val COUNTS = 5 // 点击次数
        private val DURATION: Long = 1000 // 规定有效时间
        var mHits = LongArray(COUNTS)

        override fun bindData(holder: BaseViewHolder, data: AboutEntity, position: Int) {
            Log.d(TAG, "bindData: ${data.title} $position")
            holder.setText(R.id.tvAboutTitle, data.title)
            holder.setText(R.id.tvAboutInformation, data.information)

            holder.setOnItemClickListener(View.OnClickListener { continuousFiveClick() })
        }

        private fun continuousFiveClick() {
            try {
                // 每次点击时，数组向前移动一位
                System.arraycopy(mHits, 1, mHits, 0, mHits.size - 1)
                // 为数组最后一位赋值
                mHits[mHits.size - 1] = SystemClock.uptimeMillis()
                if (mHits[0] >= SystemClock.uptimeMillis() - DURATION) {
                    mHits = LongArray(COUNTS) // 重新初始化数组
                    Intent().run {
                        setClassName(
                            "com.igentai.autodevelopermode",
                            "com.igentai.autodevelopermode.ui.activity.MainActivity"
                        )
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        context.startActivity(this)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "continuousFiveClick: ${e.message} ")
            }
        }
    }
}