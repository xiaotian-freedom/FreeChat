package com.storn.freechat.me.presenter

import com.storn.freechat.vo.UserVo

/**
 * Created by tianshutong on 2017/6/27.
 */
interface ProfileContract {

    interface View {

        fun fillData()

    }

    interface Presenter {

        fun getProfileData(): UserVo

        fun showCameraDialog()

        fun goCamera()

        fun goAlbum()

        fun goEdit(info: String, tip: String)
    }
}