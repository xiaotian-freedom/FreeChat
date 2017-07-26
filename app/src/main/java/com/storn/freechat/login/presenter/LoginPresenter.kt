package com.storn.freechat.login.presenter

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.ActivityOptionsCompat
import android.text.TextUtils
import android.transition.Explode
import com.common.common.Constants
import com.common.util.CommonUtil
import com.common.util.PreferenceTool
import com.jude.beam.expansion.BeamBasePresenter
import com.storn.freechat.common.ChatApplication
import com.storn.freechat.login.model.LoginRepository
import com.storn.freechat.login.ui.LoginActivity
import com.storn.freechat.login.ui.RegisterActivity
import com.storn.freechat.main.ui.HomeActivity
import com.storn.freechat.manager.XMPPConnectionManager
import com.storn.freechat.vo.UserVo
import org.jivesoftware.smack.AbstractXMPPConnection
import org.jivesoftware.smack.SmackException
import org.jivesoftware.smack.XMPPException
import org.jivesoftware.smackx.vcardtemp.VCardManager
import java.io.IOException
import java.util.*

/**
 * 登录控制器
 * Created by tianshutong on 2017/4/7.
 */

class LoginPresenter : BeamBasePresenter<LoginActivity>(), LoginContract.Presenter {

    val REQUEST_REGISTER = 200
    var loginRepository: LoginRepository? = null
    val mAccountList = ArrayList<UserVo>()
    val mHandler = Handler(Looper.getMainLooper())

    override fun onCreateView(view: LoginActivity) {
        super.onCreateView(view)
        getAccounts()
        initData()
    }

    override fun onCreate(view: LoginActivity, savedState: Bundle?) {
        super.onCreate(view, savedState)
        loginRepository = LoginRepository.getInstance()
    }

    private fun initData() {
        if (mAccountList.size == 0) {
            return
        }
        val userVo = mAccountList[mAccountList.size - 1]
        val userPwd = CommonUtil.AESDecrypt(view, userVo.password)
        view.fillLastAccount(userVo.name, userPwd)

        val mTempList = ArrayList<String>()
        for (u in mAccountList) {
            val userName = u.name
            mTempList.add(userName)
        }
        view.addAccountsToAutoComplete(mTempList)
    }

    override fun goToMainAct() {
        val explode = Explode()
        explode.duration = 500

        view.window.exitTransition = explode
        view.window.enterTransition = explode
        val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(view)
        view.startActivity(Intent(view, HomeActivity::class.java), optionsCompat.toBundle())
        view.finish()
    }

    override fun goToRegAct(view: android.view.View, s: String) {
        getView().window.exitTransition = null
        getView().window.enterTransition = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val options = ActivityOptions.makeSceneTransitionAnimation(getView(), view, s)
            getView().startActivityForResult(Intent(getView(), RegisterActivity::class.java), REQUEST_REGISTER, options.toBundle())
        } else {
            getView().startActivityForResult(Intent(getView(), RegisterActivity::class.java), REQUEST_REGISTER)
        }
    }

    override fun getAccounts() {
        val userInfo = PreferenceTool.getString(Constants.APP_ACCOUNT_INFO)
        if (!TextUtils.isEmpty(userInfo)) {
            if (userInfo.contains(",")) {
                val userArray = userInfo.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                for (users in userArray) {
                    val user = users.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val userVo = UserVo()
                    userVo.name = user[0]
                    userVo.password = user[1]
                    mAccountList.add(userVo)
                }
            } else {
                val user = userInfo.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val userVo = UserVo()
                userVo.name = user[0]
                if (user.size == 2) {
                    userVo.password = user[1]
                }
                mAccountList.add(userVo)
            }
        }
    }

    override fun saveAccount(userName: String, password: String) {
        val encryptPwd = CommonUtil.AESEncrypt(view, password)
        checkAccount(userName, password)
        val userVo = UserVo()
        userVo.name = userName
        userVo.password = password
        mAccountList.add(userVo)
        var userInfos = ""
        for (mUserVo in mAccountList) {
            val uName = mUserVo.name
            val uPwd = mUserVo.password
            val userInfo = uName + "/" + uPwd
            if (TextUtils.isEmpty(userInfos)) {
                userInfos = userInfo
            } else {
                userInfos += "," + userInfo
            }
        }
        PreferenceTool.putString(Constants.APP_ACCOUNT_INFO, userInfos)
        PreferenceTool.commit()
    }

    override fun checkAccount(uName: String, pwd: String) {
        if (mAccountList.size == 0) {
            return
        }
        var position = 0

        for (i in mAccountList.indices) {
            val userVo = mAccountList[i]
            val userName = userVo.name
            val password = userVo.password
            val decryptPwd = CommonUtil.AESEncrypt(view, pwd)
            if (!TextUtils.isEmpty(uName)
                    && !TextUtils.isEmpty(pwd)
                    && !TextUtils.isEmpty(password)
                    && userName == uName && password == pwd) {
                position = i
                break
            }
        }
        mAccountList.removeAt(position)
    }

    override fun attemptLogin(userName: String, password: String) {
        view.showProgress(true)

        Thread {
            var isLogin: Boolean
            val connection: AbstractXMPPConnection
            val manager = XMPPConnectionManager.getInstance()
            manager.initConnection(userName, password)
            connection = manager.connection
            try {
                connection.connect()
                if (connection.isConnected) {
                    connection.login(userName, password)
                }
                isLogin = connection.isAuthenticated
            } catch (e: IOException) {
                e.printStackTrace()
                isLogin = false
            } catch (e: XMPPException) {
                e.printStackTrace()
                isLogin = false
            } catch (e: SmackException) {
                e.printStackTrace()
                isLogin = false
            }

            mHandler.postDelayed({
                dealResult(isLogin, userName, password, connection)
            }, Constants.DELAY_2000.toLong())

        }.start()
    }

    private fun dealResult(login: Boolean, userName: String, password: String, conn: AbstractXMPPConnection) {

        if (login) {
            view.runOnUiThread {
                view.loadSuccess()
            }

            saveAccount(userName, password)
            val userVo = UserVo()

            try {
                val vCardManager = VCardManager.getInstanceFor(conn)
                val vCard = vCardManager.loadVCard()
                var jid = vCard.jabberId
                if (TextUtils.isEmpty(jid)) {
                    jid = vCard.to
                }
                if (!TextUtils.isEmpty(vCard.nickName)) {
                    userVo.nickName = vCard.nickName
                } else if (!TextUtils.isEmpty(vCard.firstName)) {
                    userVo.nickName = vCard.firstName
                } else {
                    userVo.nickName = userName
                }
                userVo.jid = jid
                userVo.name = userName
                userVo.password = password
                userVo.telephone = vCard.getPhoneWork(Constants.PHONE_TYPE_5)
                userVo.email = vCard.emailWork

                ChatApplication.setUserVo(userVo)
                loginRepository!!.insertOrUpdateAccount(view, userVo)
                PreferenceTool.putBoolean(Constants.LOGIN_STATUS, true)
                PreferenceTool.putString(Constants.LOGIN_JID, jid)
                PreferenceTool.putString(Constants.LOGIN_UNAME, userName)
                PreferenceTool.putString(Constants.LOGIN_UPASS, password)
                PreferenceTool.putInt(Constants.CHAT_BACKGROUND, 0)
                PreferenceTool.commit()

                mHandler.postDelayed({ goToMainAct() }, Constants.DELAY_2000.toLong())
            } catch (e: SmackException.NoResponseException) {
                e.printStackTrace()
                view.runOnUiThread {
                    view.loadFailed()
                    mHandler.postDelayed({
                        view.resetLoad()
                        view.showProgress(false)
                    }, Constants.DELAY_1000.toLong())
                }
            } catch (e: XMPPException.XMPPErrorException) {
                e.printStackTrace()
                view.runOnUiThread {
                    view.loadFailed()
                    mHandler.postDelayed({
                        view.resetLoad()
                        view.showProgress(false)
                    }, Constants.DELAY_1000.toLong())
                }
            } catch (e: SmackException.NotConnectedException) {
                e.printStackTrace()
                view.runOnUiThread {
                    view.loadFailed()
                    mHandler.postDelayed({
                        view.resetLoad()
                        view.showProgress(false)
                    }, Constants.DELAY_1000.toLong())
                }
            }
        } else {
            view.runOnUiThread {
                view.loadFailed()
                mHandler.postDelayed({
                    view.resetLoad()
                    view.showProgress(false)
                }, Constants.DELAY_1000.toLong())
            }
        }
    }
}
