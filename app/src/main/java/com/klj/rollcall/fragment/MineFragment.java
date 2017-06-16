package com.klj.rollcall.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.klj.rollcall.R;
import com.klj.rollcall.activity.LoginActivity;
import com.klj.rollcall.activity.MineActivity;
import com.klj.rollcall.activity.UpdatePwdActivity;
import com.klj.rollcall.base.BaseFragment;
import com.klj.rollcall.utils.ConstantUtil;
import com.klj.rollcall.utils.UrlUtil;
import com.klj.rollcall.utils.Utils;
import com.klj.rollcall.view.CircleImageView;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class MineFragment extends BaseFragment {
    @BindView(R.id.iv_mime_portrait)
    CircleImageView ivMimePortrait;
    @BindView(R.id.tv_mine_name)
    TextView tvMineName;
    @BindView(R.id.rl_mine)
    RelativeLayout rlMine;
    @BindView(R.id.tv_mine_updatePwd)
    TextView tvMineUpdatePwd;
    @BindView(R.id.tv_mine_exit)
    TextView tvMineExit;
    Unbinder unbinder;
    private String userRole;

    @Override
    public int getContentViewId() {
        return R.layout.fragment_mine;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    @Override
    protected void init() {
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        userRole = (String) Utils.getSPFile(getActivity(), ConstantUtil.LOGIN_FILE_NAME, ConstantUtil.LOGIN_USER_ROLE, "");
        String portrait="";
        String name = "";
        if (ConstantUtil.STUDENT.equals(userRole)) {
             portrait = (String) Utils.getSPFile(getActivity(), ConstantUtil.STUDENT_FILE_NAME, "sPortrait", "");
             name = (String) Utils.getSPFile(getActivity(), ConstantUtil.STUDENT_FILE_NAME, "sName", "");
        }
        if (ConstantUtil.TEACHER.equals(userRole)) {
             portrait = (String) Utils.getSPFile(getActivity(), ConstantUtil.Teacher_FILE_NAME, "tPortrait", "");
             name = (String) Utils.getSPFile(getActivity(), ConstantUtil.Teacher_FILE_NAME, "tName", "");
        }
        if (!TextUtils.isEmpty(portrait)) {
            Picasso.with(getActivity()).load(UrlUtil.ROOT_PATH + portrait).placeholder(R.mipmap.icon_portrait).into(ivMimePortrait);
        }
        tvMineName.setText(name);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @OnClick({R.id.rl_mine, R.id.tv_mine_updatePwd, R.id.tv_mine_exit})
    public void onViewClicked(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.rl_mine:
                intent = new Intent(getActivity(), MineActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_mine_updatePwd:
                intent = new Intent(getActivity(), UpdatePwdActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_mine_exit:
                intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
                break;
        }
    }
}
