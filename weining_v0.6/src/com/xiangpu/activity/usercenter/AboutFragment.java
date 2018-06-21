package com.xiangpu.activity.usercenter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.konecty.rocket.chat.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiangpu.appversion.CheckVersionService;
import com.xiangpu.bean.AuthenticationBean;
import com.xiangpu.common.Constants;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/12/3 0003.
 * Info：
 */

public class AboutFragment extends Fragment {

    private View rootView;
    private ImageView iv_check_version;
    private ImageView ivLogo;
    private TextView tvVersion;

    public static AboutFragment newInstance() {
        AboutFragment fragment = new AboutFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_about, container, false);
        initView();
        initListener();
        return rootView;
    }

    private void initView() {
        iv_check_version = (ImageView) rootView.findViewById(R.id.iv_check_version);
        ivLogo = (ImageView) rootView.findViewById(R.id.iv_logo);
        tvVersion = (TextView) rootView.findViewById(R.id.tv_app_version);
        final AuthenticationBean bean = DataSupport.where("orgName = ? and version = ? and env = ? ", getString(R.string.api_compcode), getString(R.string.api_version), getString(R.string.api_env)).findFirst(AuthenticationBean.class);
        if (bean != null) {
            tvVersion.setText("版本：" + bean.getVersionAbout());
            ImageLoader.getInstance().displayImage(Constants.getConfigLogoPath(bean.getAboutLogoDir()), ivLogo);
        }
    }

    private void initListener() {
        iv_check_version.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckVersionService versionService = new CheckVersionService(getActivity());
                versionService.setShowTag(true);
                versionService.checkVersion();
            }
        });
    }

}
