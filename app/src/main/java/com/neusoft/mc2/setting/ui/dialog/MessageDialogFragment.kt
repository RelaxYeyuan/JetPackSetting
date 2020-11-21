package com.neusoft.mc2.setting.ui.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentTransaction
import com.neusoft.mc2.setting.R
import java.util.*

/**
 * Created by chenhongrui on 2018/11/27
 *
 *
 * 内容摘要:
 * 版权所有：Semisky
 * 修改内容：onStart 固定了宽度和高度
 * 修改日期
 */
class MessageDialogFragment : DialogFragment() {
    private var viewGroup: ViewGroup? = null
    var layoutStyle: LayoutStyle? = null
    var showTime = 1500
    private var isConfirm = false
    private var mTimer: Timer = Timer()
    private var mTimerTask: TimerTask? = null

    companion object {
        private const val TAG = "MessageDialogFragment"
        fun newInstance(message: String, title: String): MessageDialogFragment {
            val dialogFragment = MessageDialogFragment()
            val args = Bundle()
            args.putString("message", message)
            args.putString("title", title)
            dialogFragment.arguments = args
            return dialogFragment
        }
    }

    init {
        timerTaskDismissDialog()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val message = requireArguments().getString("message")
        val title = requireArguments().getString("title")
        Log.d(TAG, "onCreateDialog: $message")
        return when (layoutStyle) {
            LayoutStyle.ONLY_MESSAGE -> createOnlyMessageDialog(message)
            LayoutStyle.IMAGE_MESSAGE -> createImageMessageDialog(message)
            LayoutStyle.CANCEL_CONFIRM_BUTTON -> createCancelConfirmDialog(message)
            LayoutStyle.TITLE_MESSAGE_AND_BUTTON -> createTitleMessageButtonDialog(title, message)
            LayoutStyle.CANCEL_BUTTON -> createCancelDialog(message)
            else -> createOnlyMessageDialog(message)
        }
    }

    var confirmListener: ClickConfirmListener? = null

    interface ClickConfirmListener {
        /**
         * 当点击了确认
         */
        fun clickConfirm()

        /**
         * 当点击了取消
         */
        fun clickCancel()

        /**
         * 当dialog消失
         */
        fun dialogDisMiss()
    }

    /**
     * 只显示消息
     */
    private fun createOnlyMessageDialog(message: String?): Dialog {
        val builder = AlertDialog.Builder(activity)
        return builder.create()
    }

    /**
     * 显示图标和信息
     */
    private fun createImageMessageDialog(message: String?): Dialog {
        val builder = AlertDialog.Builder(activity)

        return builder.create()
    }

    /**
     * 显示消息
     * 以及确认、取消按钮
     */
    private fun createCancelConfirmDialog(message: String?): AlertDialog {
        val builder = AlertDialog.Builder(activity)

        return builder.create()
    }

    /**
     * 显示标题和信息
     * 以及确认、取消按钮
     */
    private fun createTitleMessageButtonDialog(
        title: String?,
        message: String?
    ): AlertDialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = requireActivity().layoutInflater
        val view: View = inflater.inflate(
            R.layout.title_message_button_dialog,
            viewGroup,
            false
        )
        val btnCancel = view.findViewById<Button>(R.id.btn_dialog_cancel)
        val btnConfirm = view.findViewById<Button>(R.id.btn_dialog_confirm)
        val dialogMessage = view.findViewById<TextView>(R.id.tv_dialog_message)
        dialogMessage.text = message
        btnConfirm.setOnClickListener {
            isConfirm = true
            if (confirmListener != null) {
                confirmListener!!.clickConfirm()
            }
            dismiss()
        }
        btnCancel.setOnClickListener { dismiss() }
        builder.setView(view)
        return builder.create()
    }

    /**
     * 带title和cancel的弹出框
     */
    private fun createCancelDialog(message: String?): AlertDialog {
        val builder = AlertDialog.Builder(activity)
//        val inflater = requireActivity().layoutInflater
//        val view: View = inflater.inflate(R.layout.cancel_button_dialog, viewGroup, false)
//        val btnCancel = view.findViewById<Button>(R.id.btn_cancel)
//        val tvMessage = view.findViewById<TextView>(R.id.tv_dialog_message)
//        tvMessage.text = message
//        btnCancel.setOnClickListener {
//            isConfirm = true
//            if (confirmListener != null) {
//                confirmListener!!.clickCancel()
//            }
//            dismiss()
//        }
//        builder.setView(view)
        return builder.create()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (confirmListener != null && !isConfirm) {
            confirmListener!!.dialogDisMiss()
        } else {
            isConfirm = false
        }
        cancelTimer()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewGroup = container
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog ?: return super.onStart()
        activity?.windowManager?.defaultDisplay?.getMetrics(DisplayMetrics())
        val window = dialog.window ?: return
        val attributes = window.attributes
        Log.d(TAG, "onStart: $layoutStyle")
        when (layoutStyle) {
            LayoutStyle.ONLY_MESSAGE -> {
                attributes.gravity = Gravity.TOP
                attributes.verticalMargin = 0.4f
                attributes.height = 100
                attributes.width = 530
                dialog.window!!.attributes = attributes
            }
            LayoutStyle.IMAGE_MESSAGE -> {
                attributes.gravity = Gravity.TOP
                attributes.verticalMargin = 0.3f
                attributes.height = 250
                attributes.width = 200
                dialog.window!!.attributes = attributes
            }
            else -> {
                attributes.gravity = Gravity.CENTER
                attributes.width = 500
                attributes.height = 260
                dialog.window!!.attributes = attributes
            }
        }
    }

    override fun show(
        transaction: FragmentTransaction,
        tag: String?
    ): Int {
        if (showTime != 0) {
            dismissTimerDialog()
        }
        try {
            val c = Class.forName("androidx.fragment.app.DialogFragment")
            val dismissed = c.getDeclaredField("mDismissed")
            dismissed.isAccessible = true
            val shownByMe = c.getDeclaredField("mShownByMe")
            shownByMe.isAccessible = true
            dismissed[this] = false
            shownByMe[this] = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        transaction.add(this, tag)
        var backStackId = -1
        try {
            val c =
                Class.forName("androidx.fragment.app.DialogFragment")
            val mViewDestroyed = c.getDeclaredField("mViewDestroyed")
            mViewDestroyed.isAccessible = true
            mViewDestroyed[this] = false
            //commit 替换 commitAllowingStateLoss
            backStackId = transaction.commitAllowingStateLoss()
            val mBackStackId = c.getDeclaredField("mBackStackId")
            mBackStackId.isAccessible = true
            mBackStackId[this] = backStackId
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //mDismissed = false;
        //mShownByMe = true;
        //transaction.add(this, tag);
        //mViewDestroyed = false;
        //mBackStackId = transaction.commit();
        //return mBackStackId;
        return backStackId
    }

    private fun dismissTimerDialog() {
        mTimer.schedule(mTimerTask, showTime.toLong())
    }

    private fun timerTaskDismissDialog() {
        mTimerTask = object : TimerTask() {
            override fun run() {
                if (dialog != null && dialog!!.isShowing) {
                    dismissAllowingStateLoss()
                }
            }
        }
    }

    fun cancelTimer() {
        mTimer.cancel()
        mTimer.purge()
        mTimerTask?.cancel()
    }

    enum class LayoutStyle {
        ONLY_MESSAGE,  //只显示信息
        IMAGE_MESSAGE,//图标和信息
        TITLE_MESSAGE_AND_BUTTON,  //显示标题和信息和按钮
        CANCEL_CONFIRM_BUTTON,  //带有确认和取消
        CANCEL_BUTTON,  //只有取消
    }
}