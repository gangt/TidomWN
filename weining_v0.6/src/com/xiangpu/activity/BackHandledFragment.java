package com.xiangpu.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.xiangpu.interfaces.BackHandledInterface;

/**
 * Created by Administrator on 2017/12/14 0014.
 * Info：自己监听返回键的fragment
 */

public abstract class BackHandledFragment extends Fragment {
    protected BackHandledInterface backHandledInterface;

    /**
     * 所有继承BackHandledFragment的子类都将在这个方法中实现物理Back键按下后的逻辑
     * FragmentActivity捕捉到物理返回键点击事件后会首先询问Fragment是否消费该事件
     * 如果没有Fragment消息时FragmentActivity自己才会消费该事件
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!(getActivity() instanceof BackHandledInterface)) {
            throw new ClassCastException("Hosting Activity must implement BackHandledInterface");
        } else {
            this.backHandledInterface = (BackHandledInterface) getActivity();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // 告诉FragmentActivity，当前Fragment在栈顶
        backHandledInterface.setSelectedFragment(this);
    }

    protected abstract boolean onBackPressed();

}
