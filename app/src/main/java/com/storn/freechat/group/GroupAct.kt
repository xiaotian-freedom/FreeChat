package com.storn.freechat.group

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import com.common.util.AnimationUtil
import com.common.widget.toast.LoadToast
import com.jaeger.library.StatusBarUtil
import com.jude.beam.bijection.RequiresPresenter
import com.jude.beam.expansion.BeamBaseActivity
import com.storn.freechat.R
import com.storn.freechat.adapter.MainGroupAdapter
import com.storn.freechat.interfac.OnItemClickListener
import com.storn.freechat.vo.GroupEntityVo
import kotlinx.android.synthetic.main.activity_group_layout.*
import kotlinx.android.synthetic.main.tool_bar_layout.*

/**
 * 群列表
 * Created by tianshutong on 2017/7/14.
 */
@RequiresPresenter(GroupPresenter::class)
class GroupAct : BeamBaseActivity<GroupPresenter>(), GroupContract.View {

    var groupAdapter: MainGroupAdapter? = null

    var mHandler: Handler? = null

    var mLoadToast: LoadToast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_layout)
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.colorPrimary))
        initToolbar()
        initToast()
        initGroupRecyclerView()
    }

    override fun onDestroy() {
        super.onDestroy()
        groupAdapter = null
        mLoadToast = null
        mHandler = null
    }

    override fun onBackPressed() {
        super.onBackPressed()
        this.finish()
    }

    override fun initToolbar() {
        toolbar.title = ""
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.mipmap.white_arrow_left)
        mainToolBarTitle!!.text = "群"
        mHandler = Handler(Looper.myLooper())
    }

    override fun initToast() {
        mLoadToast = LoadToast(this)
        mLoadToast!!.setBackgroundColor(ContextCompat.getColor(this, R.color.dialog_bg))
        mLoadToast!!.setProgressColor(ContextCompat.getColor(this, R.color.black_19))
    }

    /**
     * 初始化群列表
     */
    private fun initGroupRecyclerView() {
        mainGroupRecyclerView.setHasFixedSize(true)
        mainGroupRecyclerView.itemAnimator = DefaultItemAnimator()
        mainGroupRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun showLoadToast(text: String) {
        mLoadToast!!.setText(text).setTranslationY(100).show()
    }

    override fun toastSuccess() {
        mLoadToast!!.success()
    }

    override fun toastError() {
        mLoadToast!!.error()
    }

    override fun startRotation() {
        AnimationUtil.rotationAnim(fab)
    }

    override fun endRotation() {
        AnimationUtil.reverseRotation(fab)
    }

    override fun finishGroupRefresh() {
        groupRefreshLayout.finishRefreshing()
    }

    fun setGroupAdapter(list: MutableList<GroupEntityVo>) {
        if (groupAdapter == null) {
            groupAdapter = MainGroupAdapter(this, list, true)
            groupAdapter?.setOnItemClickListener(mOnGroupItemClickListener)
            mainGroupRecyclerView.adapter = groupAdapter
        } else {
            groupAdapter?.setRefreshData(list, false)
        }
    }

    private val mOnGroupItemClickListener = OnItemClickListener { position: Int -> presenter.goMultiChat(position) }

}