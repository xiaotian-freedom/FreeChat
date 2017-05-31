package com.storn.freechat.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.storn.freechat.R;

/**
 * 右侧菜单
 * Created by tianshutong on 2016/12/8.
 */

public class MenuRightFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.activity_right_panel, container, false);
        return contentView;
    }
}
