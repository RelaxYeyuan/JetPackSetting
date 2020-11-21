package com.neusoft.mc2.setting.ui.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.neusoft.mc2.setting.R
import com.neusoft.mc2.setting.ui.custom.MyEditText
import com.neusoft.mc2.setting.utils.KeyboardUtils
import java.io.UnsupportedEncodingException
import java.util.*

/**
 * Created by chenhongrui on 2018/11/27
 *
 *
 * 内容摘要:
 * 版权所有：Semisky
 * 修改内容：
 * 修改日期
 */
class EditInputDialogFragment : DialogFragment() {

    companion object {
        fun newInstance(message: String?): EditInputDialogFragment {
            val dialogFragment = EditInputDialogFragment()
            val args = Bundle()
            args.putString("message", message)
            dialogFragment.arguments = args
            return dialogFragment
        }
    }

    var layoutStyle: LayoutStyle? =
        null
    var showTime = 1500

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val message = arguments!!.getString("message")
        return when (layoutStyle) {
            LayoutStyle.EDITV_BUTTON -> SetEdiCancelConfirmListener(
                message,
                normalType
            )
            LayoutStyle.EDITV_BUTTON_WIFI -> SetEdiCancelConfirmListener(
                message,
                wifiType
            )
            else -> SetEdiCancelConfirmListener(message, normalType)
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

        /**
         * 确定修改的名字
         */
        fun clicknameConfirm(dvname: String)
    }

    private val normalType = "normal"
    private val wifiType = "wifi"

    private fun SetEdiCancelConfirmListener(
        message: String,
        type: String
    ): AlertDialog {
        val builder =
            AlertDialog.Builder(
                Objects.requireNonNull(context)!!
            )
        val view = LayoutInflater.from(context)
            .inflate(R.layout.cancel_confirm_editext_dialog, null, false)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        val btnConfirm = view.findViewById<Button>(R.id.btnConfirm)
        val edittv: MyEditText = view.findViewById(R.id.et_dialog_message)
        edittv.requestFocus()
        edittv.setText(message)
        edittv.setSelection(message.length)
        edittv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun onTextChanged(
                charSequence: CharSequence,
                i: Int,
                i1: Int,
                i2: Int
            ) {
            }

            override fun afterTextChanged(editable: Editable) {
                if (type == normalType) {
                    //大于最大长度
                    val inputMsg = editable.toString().trim { it <= ' ' }
                    if (!TextUtils.isEmpty(inputMsg)) {
                        btnConfirm.alpha = 1f //恢复改变颜色
                        btnConfirm.isEnabled = true
                        val limitMsg = getLimitInput(inputMsg)
                        if (!TextUtils.isEmpty(limitMsg)) {
                            if (limitMsg != inputMsg) {
                                edittv.setText(limitMsg)
                                //新字符串长度
                                //旧光标位置超过字符串长度
                                edittv.setSelection(limitMsg.length)
                            }
                        }
                    } else {
                        btnConfirm.alpha = 0.3f
                        btnConfirm.isEnabled = false
                    }
                } else if (type == wifiType) {
                    val inputMsg = editable.toString().trim { it <= ' ' }
                    if (inputMsg.length >= 8) {
                        btnConfirm.alpha = 1f //恢复改变颜色
                        btnConfirm.isEnabled = true
                    } else {
                        btnConfirm.alpha = 0.3f
                        btnConfirm.isEnabled = false
                    }
                }
            }
        })
        btnConfirm.setOnClickListener {
            KeyboardUtils.hideSoftInput(edittv, requireContext())
            if (confirmListener != null) {
                confirmListener!!.clicknameConfirm(edittv.text.toString())
            }
        }
        btnCancel.setOnClickListener {
            KeyboardUtils.hideSoftInput(edittv, requireContext())
            dismiss()
        }
        builder.setView(view)
        return builder.create()
    }

    private fun getLimitInput(inputStr: String): String {
        val orignLen = inputStr.length
        var resultLen = 0
        var temp: String? = null
        for (i in 0 until orignLen) {
            temp = inputStr.substring(i, i + 1)
            try { // 3 bytes to indicate chinese word,1 byte to indicate english
                if (temp.toByteArray(charset("utf-8")).size == 3) {
                    resultLen += 2
                } else {
                    resultLen++
                }
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }
            if (resultLen > 32) {
                return inputStr.substring(0, i)
            }
        }
        return inputStr
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (confirmListener != null) {
            confirmListener!!.dialogDisMiss()
        }
    }

    override fun dismiss() {
        dismissAllowingStateLoss()
    }

    fun dismissDialog() {
        Handler().postDelayed(
            { dismissAllowingStateLoss() },
            showTime.toLong()
        )
    }

    enum class LayoutStyle {
        EDITV_BUTTON,  //输入
        EDITV_BUTTON_WIFI //wifi
    }

}