package com.niupule.www.selfui_demo.BottomNavigations.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.niupule.www.selfui_demo.R;

/**
 * Created: niupule
 * Date: 2018/5/23  下午1:39
 * E-mail:niupuyue@aliyun.com
 * des:
 */

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile,container,false);
        return view;
    }
}
