package com.storn.freechat.me.ui

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.v4.content.ContextCompat
import com.common.common.Constants
import com.common.util.CommonUtil
import com.common.util.GlideHelper
import com.common.util.PreferenceTool
import com.common.widget.toast.LoadToast
import com.jaeger.library.StatusBarUtil
import com.jude.beam.bijection.RequiresPresenter
import com.jude.beam.expansion.BeamBaseActivity
import com.storn.freechat.R
import com.storn.freechat.main.ui.HomeActivity
import com.storn.freechat.manager.UserManager
import com.storn.freechat.me.presenter.ProfileContract
import com.storn.freechat.me.presenter.ProfilePresenter
import kotlinx.android.synthetic.main.common_yellow_tool_bar_layout.*
import kotlinx.android.synthetic.main.profile_detail_layout.*

/**
 * 个人信息
 * Created by tianshutong on 2017/6/27.
 */
@RequiresPresenter(ProfilePresenter::class)
class ProfileDetailAct : BeamBaseActivity<ProfilePresenter>(), ProfileContract.View {

    val mHandler = Handler(Looper.myLooper())
    var mLoadToast: LoadToast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_detail_layout)
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_y_e2), 0)
        fillData()
        initToast()
        initListener()
    }

    fun initToolbar() {
        mainToolBar.setNavigationIcon(R.mipmap.white_arrow_left)
        mainToolBarTitle.text = resources.getString(R.string.profile_title)
    }

    private fun initListener() {
        mainToolBar.setNavigationOnClickListener { _ -> onBackPressed() }
        headLayout.setOnClickListener { presenter.showCameraDialog() }
        signatureLayout.setOnClickListener { presenter.goEdit(signatureView.text.toString(), signatureText.text.toString()) }
        nickLayout.setOnClickListener { presenter.goEdit(nickView.text.toString(), nickText.text.toString()) }
        accountLayout.setOnClickListener { }
        phoneLayout.setOnClickListener { presenter.goEdit(telephoneView.text.toString(), telephoneText.text.toString()) }
        qqLayout.setOnClickListener { presenter.goEdit(qqView.text.toString(), qqText.text.toString()) }
        emailLayout.setOnClickListener { presenter.goEdit(emailView.text.toString(), mailText.text.toString()) }
    }

    fun initToast() {
        mLoadToast = LoadToast(this)
        mLoadToast!!.setBackgroundColor(resources.getColor(R.color.dialog_bg))
        mLoadToast!!.setProgressColor(resources.getColor(R.color.black_19))
    }

    override fun fillData() {
        val userVo = presenter.getProfileData()
        if (userVo.nickName == null || userVo.nickName.isEmpty()) mainToolBarTitle.text = "" else mainToolBarTitle.text = userVo.nickName
        if (userVo.img != null && userVo.img.isNotEmpty()) GlideHelper.showHeadViewWithNoAnim(this, userVo.img, headView) else setHeadView()
        if (userVo.signature == null || userVo.signature.isEmpty()) signatureView.text = "" else signatureView.text = userVo.signature
        if (userVo.nickName == null || userVo.nickName.isEmpty()) nickView.text = "" else nickView.text = userVo.nickName
        if (userVo.name == null || userVo.name.isEmpty()) accountView.text = "" else accountView.text = userVo.name
        if (userVo.telephone == null || userVo.telephone.isEmpty()) telephoneView.text = "" else telephoneView.text = userVo.telephone
        if (userVo.qq == null || userVo.qq.isEmpty()) qqView.text = "" else qqView.text = userVo.qq
        if (userVo.email == null || userVo.email.isEmpty()) emailView.text = "" else emailView.text = userVo.email
    }

    fun setHeadView() {
        val inputStream = UserManager.getInstance().getUserHead(PreferenceTool.getString(Constants.LOGIN_JID))
        if (inputStream != null) {
            headView.setImageBitmap(BitmapFactory.decodeStream(inputStream))
        }
    }

    fun updateHeadView(url: String) {
        GlideHelper.showHeadViewWithNoAnim(this, url, headView)
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == presenter.REQUEST_CODE_CAMERA) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                presenter.goCamera()
            } else {
                CommonUtil.showToast(this, resources.getString(R.string.camera_no_permission))
            }
        }
        if (requestCode == presenter.REQUEST_CODE_ALBUM) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                presenter.goAlbum()
            } else {
                CommonUtil.showToast(this, resources.getString(R.string.storage_no_permission))
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            presenter.REQUEST_CODE_EDIT -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    when (data.getStringExtra(Constants.PROFILE_TEXT)) {
                        signatureText.text.toString() -> {
                            signatureView.text = data.getStringExtra(Constants.PROFILE_INFO)
                        }
                        nickText.text.toString() -> {
                            nickView.text = data.getStringExtra(Constants.PROFILE_INFO)
                            if (HomeActivity.homeHandler != null) {
                                val message = Message.obtain()
                                message.what = Constants.UPDATE_NICKNAME
                                message.obj = data.getStringExtra(Constants.PROFILE_INFO)
                                HomeActivity.homeHandler?.sendMessage(message)
                            }
                        }
                        telephoneText.text.toString() -> {
                            telephoneView.text = data.getStringExtra(Constants.PROFILE_INFO)
                        }
                        qqText.text.toString() -> {
                            qqView.text = data.getStringExtra(Constants.PROFILE_INFO)
                        }
                        mailText.text.toString() -> {
                            emailView.text = data.getStringExtra(Constants.PROFILE_INFO)
                        }
                    }
                }
            }
            presenter.REQUEST_CODE_CAMERA -> {
                if (resultCode == Activity.RESULT_OK) {
                    presenter.doCameraResult()
                }
            }
            presenter.REQUEST_CODE_ALBUM -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    presenter.doAlbumCropResult(data)
                }
            }
            presenter.REQUEST_CODE_CROP -> {
                if (resultCode == Activity.RESULT_OK) {
                    presenter.doCropResult()
                }
            }
            else -> {
            }
        }
    }
}