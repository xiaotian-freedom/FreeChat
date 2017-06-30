package com.storn.freechat.me.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.content.ContextCompat
import android.text.InputFilter
import android.view.View
import android.view.inputmethod.EditorInfo
import com.common.widget.toast.LoadToast
import com.jaeger.library.StatusBarUtil
import com.jude.beam.bijection.RequiresPresenter
import com.jude.beam.expansion.BeamBaseActivity
import com.storn.freechat.R
import com.storn.freechat.me.presenter.ModifyContract
import com.storn.freechat.me.presenter.ModifyPresenter
import kotlinx.android.synthetic.main.activity_modify_user_info.*
import kotlinx.android.synthetic.main.common_yellow_tool_bar_layout.*

/**
 * 修改信息
 * Created by tianshutong on 2017/6/28.
 */
@RequiresPresenter(ModifyPresenter::class)
class ModifyInfoAct : BeamBaseActivity<ModifyPresenter>(), ModifyContract.View {

    val mHandler = Handler(Looper.myLooper())
    var mLoadToast: LoadToast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_user_info)
        initToolBar()
        initToast()
        fillData()
    }

    override fun initToolBar() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_y_e2), 0)
        mainToolBar.setNavigationIcon(R.mipmap.white_arrow_left)
        mainToolBar.setNavigationOnClickListener { _ -> onBackPressed() }
        mainToolBarRightText.visibility = View.VISIBLE
        mainToolBarRightText.text = getString(R.string.complete)
        mainToolBarRightText.setOnClickListener { presenter.saveInfo() }
        mainToolBarTitle.text = String.format(getString(R.string.profile_modify_title), presenter.getTitle())
    }

    fun initToast() {
        mLoadToast = LoadToast(this)
        mLoadToast!!.setBackgroundColor(resources.getColor(R.color.dialog_bg))
        mLoadToast!!.setProgressColor(resources.getColor(R.color.black_19))
    }

    override fun fillData() {
        editInfo.setText(presenter.getInfo())
        when (presenter.getTitle()) {
            getString(R.string.profile_signature) -> {
                tvCount.visibility = View.GONE
                editInfo.inputType = EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE
                editInfo.imeOptions = EditorInfo.IME_ACTION_NONE
            }
            getString(R.string.profile_nick) -> {
                tvCount.visibility = View.VISIBLE
                editInfo.inputType = EditorInfo.TYPE_CLASS_TEXT
                editInfo.imeOptions = EditorInfo.IME_ACTION_DONE
                editInfo.maxLines = 1
                editInfo.filters = arrayOf(InputFilter.LengthFilter(24))
            }
            getString(R.string.profile_telephone) -> {
                tvCount.visibility = View.VISIBLE
                editInfo.inputType = EditorInfo.TYPE_CLASS_PHONE
                editInfo.imeOptions = EditorInfo.IME_ACTION_DONE
                editInfo.maxLines = 1
                editInfo.filters = arrayOf(InputFilter.LengthFilter(11))
            }
            getString(R.string.profile_qq) -> {
                tvCount.visibility = View.VISIBLE
                editInfo.inputType = EditorInfo.TYPE_CLASS_PHONE
                editInfo.imeOptions = EditorInfo.IME_ACTION_DONE
                editInfo.maxLines = 1
                editInfo.filters = arrayOf(InputFilter.LengthFilter(10))
            }
            getString(R.string.profile_email) -> {
                tvCount.visibility = View.GONE
                editInfo.inputType = EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                editInfo.imeOptions = EditorInfo.IME_ACTION_DONE
                editInfo.maxLines = 1
            }
        }
    }

    override fun updateCount(text: String) {
        tvCount.text = text
    }

    fun showLoadingView() {
        mLoadToast!!.setText(resources.getString(R.string.tip_modify_ing)).setTranslationY(100).show()
    }

    fun success() {
        mLoadToast!!.success()
    }

    fun error() {
        mLoadToast!!.error()
    }
}