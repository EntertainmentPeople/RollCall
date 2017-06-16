package com.klj.rollcall.utils;

import android.support.v4.app.Fragment;

import com.klj.rollcall.fragment.MineFragment;
import com.klj.rollcall.fragment.SignInFragment;
import com.klj.rollcall.fragment.SyllabusFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment帮助类
 */

public class FragmentUtil {
    private static MineFragment mineFragment = null;        //我的
    private static SignInFragment signInFragment = null;    //签到
    private static SyllabusFragment syllabusFragment = null;    //课表
    private static List<Fragment> fragments =new ArrayList<>();//fragment集

    /**
     *静态代码块，用于初始化fragment
     */
    static {
        fragments.add(new SyllabusFragment());
        fragments.add(new SignInFragment());
        fragments.add(new MineFragment());

    }

    /**
     * 返回学生页面所有的fragment
     * @return
     */
    public static List<Fragment> getFragments(){
        return fragments;
    }

}
