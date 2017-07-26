package com.storn.freechat.adapter

import am.widget.shapeimageview.ShapeImageView
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import com.common.common.Constants
import com.common.util.AnimationUtil
import com.common.util.BitmapUtil
import com.common.util.PreferenceTool
import com.common.widget.XCRoundImageViewByXfermode
import com.storn.freechat.R

/**
 * 更换背景适配器
 * Created by tianshutong on 2017/7/5.
 */
class ChatBgAdapter(val mContext: Context, val mList: MutableList<Int>) : BaseAdapter() {

    fun changeUi() {
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        val mHolder: ImageHolder
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.setting_change_chat_background_item, parent, false)
            mHolder = ImageHolder(view)
            view!!.tag = mHolder
        } else {
            mHolder = view.tag as ImageHolder
        }

        mHolder.shapeImageView.setImageResource(mList[position])

        mHolder.reflectImageView.type = XCRoundImageViewByXfermode.TYPE_OVAL

        val resBmp = mContext.resources.getDrawable(mList[position]) as BitmapDrawable
        val finalBmp = BitmapUtil.createReflectedImage(resBmp.bitmap)
        mHolder.reflectImageView.setImageBitmap(finalBmp)

        mHolder.shapeImageView.clearAnimation()

        if (position == PreferenceTool.getInt(Constants.CHAT_BACKGROUND, 0)) {
            AnimationUtil.rotationY(mHolder.shapeImageView)
            AnimationUtil.startAlphaAnim(mHolder.selectedView)
        } else {
            mHolder.selectedView.visibility = View.GONE
        }
        return view
    }

    override fun getItem(position: Int): Any {
        return mList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return mList.size
    }

    class ImageHolder constructor(itemView: View) {
        val shapeLayout = itemView.findViewById(R.id.shape_layout) as LinearLayout
        val shapeImageView = itemView.findViewById(R.id.shape_view) as ShapeImageView
        val selectedView = itemView.findViewById(R.id.selected) as ImageView
        val reflectImageView = itemView.findViewById(R.id.reflect_view) as XCRoundImageViewByXfermode
    }

}