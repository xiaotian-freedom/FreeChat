package com.storn.freechat.service

import android.content.Context
import com.common.common.Constants
import com.common.util.NetworkUtil
import com.storn.freechat.main.ui.HomeActivity
import com.storn.freechat.manager.XMPPConnectionManager
import org.jivesoftware.smack.ConnectionListener
import org.jivesoftware.smack.XMPPConnection
import java.util.*

/**
 * 连接监听类
 * Created by tianshutong on 2017/6/26.
 */

class TaxiConnectionListener(val context: Context) : ConnectionListener {

    private var tExit: Timer? = null

    override fun connected(connection: XMPPConnection) {

    }

    override fun authenticated(connection: XMPPConnection, resumed: Boolean) {

    }

    override fun connectionClosed() {
        XMPPConnectionManager.getInstance().disconnect()
        // 重连服务器
        tExit = Timer()
        tExit!!.schedule(MyTask(), Constants.DELAY_3000.toLong())
    }

    override fun connectionClosedOnError(e: Exception) {
        // 判断账号是否已登录
        val error = e.message == "stream:error (conflict)"
        if (!error) {
            // 關閉連接
            XMPPConnectionManager.getInstance().disconnect()
            // 重连服务器
            tExit = Timer()
            tExit!!.schedule(MyTask(), Constants.DELAY_3000.toLong())
        }
    }

    override fun reconnectionSuccessful() {

    }

    override fun reconnectingIn(seconds: Int) {

    }

    override fun reconnectionFailed(e: Exception) {

    }

    internal inner class MyTask : TimerTask() {
        override fun run() {
            if (NetworkUtil.isNetworkConnected(context)) {
                if (HomeActivity.homeHandler != null) {
                    val homeMessage = android.os.Message.obtain()
                    homeMessage.what = Constants.CONNECT_SERVICE
                    HomeActivity.homeHandler?.sendMessage(homeMessage)
                }
            }
        }
    }
}
