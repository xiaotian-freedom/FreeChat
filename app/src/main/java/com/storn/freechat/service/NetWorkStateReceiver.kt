package com.storn.freechat.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import com.common.common.Constants
import com.storn.freechat.main.ui.HomeActivity

/**
 * 网络广播
 * Created by tianshutong on 2017/6/13.
 */

class NetWorkStateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val manager = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = manager.activeNetworkInfo
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.isConnected) {
                if (HomeActivity.homeHandler != null) {
                    val homeMessage = android.os.Message.obtain()
                    homeMessage.what = Constants.CONNECT_SERVICE
                    HomeActivity.homeHandler?.sendMessage(homeMessage)
                }
            } else {
//                Toast.makeText(context, "网络连接异常", Toast.LENGTH_SHORT).show()
            }
        } else {   // not connected to the internet
//            Toast.makeText(context, "网络连接异常", Toast.LENGTH_SHORT).show()
        }
    }
}
