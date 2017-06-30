package com.storn.freechat.me.presenter

import android.app.Activity
import android.content.Intent
import android.text.Editable
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import com.common.common.Constants
import com.common.util.CommonUtil
import com.common.util.PreferenceTool
import com.common.util.RegexUtil
import com.common.util.SoftKeyBoardUtil
import com.common.widget.ClearableEditText
import com.jude.beam.expansion.BeamBasePresenter
import com.storn.freechat.R
import com.storn.freechat.manager.XMPPConnectionManager
import com.storn.freechat.me.ui.ModifyInfoAct
import com.storn.freechat.util.DBHelper
import kotlinx.android.synthetic.main.activity_modify_user_info.*
import org.jivesoftware.smackx.vcardtemp.VCardManager

/**
 * 修改信息控制器
 * Created by tianshutong on 2017/6/28.
 */
class ModifyPresenter : BeamBasePresenter<ModifyInfoAct>(), ModifyContract.Presenter,
        ClearableEditText.OnTextWatcher, TextView.OnEditorActionListener {

    override fun onCreateView(view: ModifyInfoAct) {
        super.onCreateView(view)
        initListener()
        getTitle()
        getInfo()
    }

    override fun getTitle(): String {
        return view.intent.getStringExtra(Constants.PROFILE_TEXT)
    }

    override fun getInfo(): String {
        return view.intent.getStringExtra(Constants.PROFILE_INFO)
    }

    override fun saveInfo() {
        SoftKeyBoardUtil.hideSoftKeyboard(view)

        val info = view.editInfo.text.toString()
        if (info.isEmpty())
            view.editInfo.setShakeAnimation()
        else {
            val userVo = DBHelper.getInstance().queryProfileInfo(view, PreferenceTool.getString(Constants.LOGIN_JID))
            view.showLoadingView()
            when (getTitle()) {
                view.getString(R.string.profile_signature) -> {
                    val connection = XMPPConnectionManager.getInstance().connection
                    if (!connection.isConnected || !connection.isAuthenticated) {
                        view.error()
                        CommonUtil.showToast(view, view.getString(R.string.error_network))

                    } else {
                        userVo.signature = info
                        DBHelper.getInstance().insertOrUpdateAccount(view, userVo)
                        complete()
                    }
                }
                view.getString(R.string.profile_nick) -> {
                    val connection = XMPPConnectionManager.getInstance().connection
                    if (!connection.isConnected || !connection.isAuthenticated) {
                        view.error()
                        CommonUtil.showToast(view, view.getString(R.string.error_network))

                    } else {
                        val vCardManager = VCardManager.getInstanceFor(connection)
                        val vCard = vCardManager.loadVCard()
                        vCard.nickName = info
                        vCardManager.saveVCard(vCard)
                        userVo.nickName = info
                        DBHelper.getInstance().insertOrUpdateAccount(view, userVo)
                        complete()
                    }
                }
                view.getString(R.string.profile_telephone) -> {
                    if (RegexUtil.checkMobile(info)) {
                        val connection = XMPPConnectionManager.getInstance().connection
                        if (!connection.isConnected || !connection.isAuthenticated) {
                            view.error()
                            CommonUtil.showToast(view, view.getString(R.string.error_network))

                        } else {
                            val vCardManager = VCardManager.getInstanceFor(connection)
                            val vCard = vCardManager.loadVCard()
                            vCard.setPhoneWork(Constants.PHONE_TYPE_5, info)
                            vCardManager.saveVCard(vCard)
                            userVo.telephone = info
                            DBHelper.getInstance().insertOrUpdateAccount(view, userVo)
                            complete()
                        }
                    } else {
                        view.error()
                        CommonUtil.showToast(view, view.getString(R.string.profile_modify_telephone_error))
                    }
                }
                view.getString(R.string.profile_email) -> {
                    if (RegexUtil.checkEmail(info)) {
                        val connection = XMPPConnectionManager.getInstance().connection
                        if (!connection.isConnected || !connection.isAuthenticated) {
                            view.error()
                            CommonUtil.showToast(view, view.getString(R.string.error_network))

                        } else {
                            val vCardManager = VCardManager.getInstanceFor(connection)
                            val vCard = vCardManager.loadVCard()
                            vCard.emailWork = info
                            vCardManager.saveVCard(vCard)
                            userVo.email = info
                            DBHelper.getInstance().insertOrUpdateAccount(view, userVo)
                            complete()
                        }
                    } else {
                        view.error()
                        CommonUtil.showToast(view, view.getString(R.string.profile_modify_email_error))
                    }

                }
                view.getString(R.string.profile_qq) -> {
                    val connection = XMPPConnectionManager.getInstance().connection
                    if (!connection.isConnected || !connection.isAuthenticated) {
                        view.error()
                        CommonUtil.showToast(view, view.getString(R.string.error_network))

                    } else {
                        userVo.qq = info
                        DBHelper.getInstance().insertOrUpdateAccount(view, userVo)
                        complete()
                    }
                }
            }
        }
    }

    fun complete() {
        view.mHandler.postDelayed({
            view.success()
        }, Constants.DELAY_1000.toLong())
        view.mHandler.postDelayed({
            val data = Intent()
            data.putExtra(Constants.PROFILE_TEXT, getTitle())
            data.putExtra(Constants.PROFILE_INFO, view.editInfo.text.toString())
            view.setResult(Activity.RESULT_OK, data)
            view.finish()
        }, Constants.DELAY_2000.toLong())
    }

    override fun initListener() {
        view.editInfo.setOnTextWatcher(this)
        view.editInfo.setOnEditorActionListener(this)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
        val text = s.toString().trim()
        when (getTitle()) {
            view.getString(R.string.profile_nick) -> {
                view.updateCount(String.format(view.getString(R.string.profile_modify_edit_nick), text.length.toString()))

                if (text.isNotEmpty() && text.length > 24) {
                    Toast.makeText(view, "昵称太长不好记哟😆", Toast.LENGTH_SHORT).show()
                }
            }
            view.getString(R.string.profile_telephone) -> {
                view.updateCount(String.format(view.getString(R.string.profile_modify_edit_telephone), text.length.toString()))

                if (text.isNotEmpty() && text.length > 11) {
                    Toast.makeText(view, "确定是中国的电话号码吗😂", Toast.LENGTH_SHORT).show()
                }
            }
            view.getString(R.string.profile_qq) -> {
                view.updateCount(String.format(view.getString(R.string.profile_modify_edit_qq), text.length.toString()))

                if (text.isNotEmpty() && text.length > 10) {
                    Toast.makeText(view, "朋友，你的QQ号有点长啊😂", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            saveInfo()
            return true
        }
        return false
    }
}