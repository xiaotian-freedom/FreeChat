package com.storn.freechat.interfac;

/**
 * 聊天刷新接口
 * Created by tianshutong on 2016/12/10.
 */

public interface OnChatRefreshListener {
    void onLoadMoreFooter();

    void onLoadMoreHeader();

    void onRefreshing();
}
