package com.xiangpu.activity.usercenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.konecty.rocket.chat.R;
import com.lssl.activity.SuneeeApplication;
import com.lssl.activity.getui.SEPushIntentService;
import com.xiangpu.activity.BaseActivity;
import com.xiangpu.activity.UcpWebViewActivity;
import com.xiangpu.bean.PersonalMessage;
import com.xiangpu.common.Constants;
import com.xiangpu.utils.LogUtil;
import com.xiangpu.utils.SharedPrefUtils;
import com.xiangpu.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.Collections;
import java.util.List;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by fangfumin on 2017/5/14.
 */

public class MessageActivity extends BaseActivity {
    private static final String TAG = "MessageActivity";
    private List<PersonalMessage> messages;
    private TextView tv_notice;
    private ListView lv_personal_message;
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.i(TAG, "-------MessageActivity onCreate-------------");
        setContentView(R.layout.activity_personal_message);
        initView();
        initData();
        registerBoradcastReceiver();
//        hideSmallRedPoint();
        SuneeeApplication.user.isHavMessage = false;

        loadPoint("", "我的消息");
        ShortcutBadger.removeCount(this);//清除图标右上角未读消息数
        SEPushIntentService.badgeCount = 0;
    }

    private void initView() {
        findViewById(R.id.layout_back_id).setOnClickListener(this);
        TextView tv_title_center = (TextView) findViewById(R.id.tv_title_center);
        tv_title_center.setText(this.getResources().getText(R.string.person_app_set3).toString());
        lv_personal_message = (ListView) this.findViewById(R.id.lv_personal_message);
        lv_personal_message.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PersonalMessage message = messages.get(position);
                LogUtil.i(TAG, message.getModule() + "---" + position);
                if (Constants.MODULE_UCP.equals(message.getModule())) {
                    Utils.startActivity(MessageActivity.this, UcpWebViewActivity.class);
                } else if (Constants.MODULE_HNQ.equals(message.getModule())) {
                    setFunctionUrl("hnq", "", "汇能器", Constants.HUINENGQI_URL);
                }
            }
        });
        tv_notice = (TextView) findViewById(R.id.tv_notice);
    }

    private void setFunctionUrl(String key, String tag, String defuatTitle, String defautUrl) {
        if (SuneeeApplication.configManage != null) {

            String link = SuneeeApplication.configManage.getFunctionUrl(key);
            String title = SuneeeApplication.configManage.getFunctionName(key);

            if (link != null && title != null) {
                goWebMainActivity(link, title, tag);
            } else if (link == null) {
                goWebMainActivity(defautUrl, defuatTitle, tag);
            }
        }
    }

    private void initData() {
        messages = DataSupport.select("module", "messageTitle", "messageContent")
                .where("userId = ?", SharedPrefUtils.getUserId(this))
                .find(PersonalMessage.class);
        if (messages != null && messages.size() > 0) {
            Collections.reverse(messages);//倒序排列
            for (PersonalMessage message : messages) {
                LogUtil.i(TAG, "personalMessage ReceiveTime is:" + message.getReceiveTime());
                LogUtil.i(TAG, "personalMessage title is:" + message.getMessageTitle());
                LogUtil.i(TAG, "personalMessage content is:" + message.getMessageContent());
            }
            adapter = new MyAdapter(this, messages);
            lv_personal_message.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            lv_personal_message.setVisibility(View.GONE);
            tv_notice.setVisibility(View.VISIBLE);
        }
//        messages = new ArrayList<>();
//        ArrayList<String> messageStrs = SuneeeApplication.getInstance().messages;
//
//        for (int i = 0; i < messageStrs.size(); i++) {
//            PersonalMessage message = new PersonalMessage();
//            // String messageData = SharedPrefUtils.getStringData(this,
//            // Constants.MESSAGE, "");
//            message.setMessageTitle(messageStrs.get(i));
//
//            message.setMessageContent(messageStrs.get(i));
//            messages.add(message);
//        }
//        if (messages.isEmpty()) {
//            PersonalMessage message1 = new PersonalMessage();
//            // String messageData = SharedPrefUtils.getStringData(this,
//            // Constants.MESSAGE, "");
//            message1.setMessageTitle("5.1放假通知");
//            message1.setIcon(R.drawable.message_ucp);
//            message1.setMessageContent("根据国家相关规定，结合公司实际情况，现将2015年五一劳动节放假事项通知如下：一、放假时间：2015年5月1日放假1天，5月2日正常上班。");
//            messages.add(message1);
//
//            PersonalMessage message2 = new PersonalMessage();
//            // String messageData = SharedPrefUtils.getStringData(this,
//            // Constants.MESSAGE, "");
//            message2.setMessageTitle("尚隆促销来了！~");
//            message2.setIcon(R.drawable.message_ucp);
//            message2.setMessageContent("全体商品、电器、家电8折");
//            messages.add(message2);
//
//            PersonalMessage message3 = new PersonalMessage();
//            // String messageData = SharedPrefUtils.getStringData(this,
//            // Constants.MESSAGE, "");
//            message3.setMessageTitle("慎思");
//            message3.setIcon(R.drawable.message_ucp);
//            message3.setMessageContent("明天晚上加班，记得把象谱0.5版本的原型图给外包那边。");
//            messages.add(message3);
//        }

    }

    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constants.REFRESH_MESSAGE_LIST);
        //注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtil.i(TAG, "Receive Broadcast to refresh_message_list");
            String action = intent.getAction();
            if (action.equals(Constants.REFRESH_MESSAGE_LIST)) {
                initData();
            }
        }

    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

    private void hideSmallRedPoint() {
        Intent intent = new Intent();
        intent.setAction("hideSmallRedPoint");
        sendBroadcast(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_back_id:
                super.onClick(v);
                break;

            default:
                super.onClick(v);
                break;
        }

    }

    private class MyAdapter extends BaseAdapter {
        private Context mContext;
        private List<PersonalMessage> msgs;

        public MyAdapter(Context context, List<PersonalMessage> msggess) {
            mContext = context;
            msgs = msggess;
        }

        @Override
        public int getCount() {
            if (msgs.isEmpty())
                return 0;
            return msgs.size();
        }

        @Override
        public Object getItem(int position) {
            if (msgs.isEmpty())
                return null;
            return msgs.get(position);
        }

        @Override
        public long getItemId(int position) {

            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;

            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.personal_message_list_item, null);
                holder = new ViewHolder();
                /* 得到各个控件的对象 */
                holder.tv_message_icon = (ImageView) convertView.findViewById(R.id.tv_message_icon);
                holder.tv_message_title = (TextView) convertView.findViewById(R.id.tv_message_title);
                holder.tv_message_content = (TextView) convertView.findViewById(R.id.tv_message_content);

                convertView.setTag(holder); // 绑定ViewHolder对象
            } else {
                holder = (ViewHolder) convertView.getTag(); // 取出ViewHolder对象
            }

			/* 设置TextView显示的内容，即我们存放在动态数组中的数据 */
            if ("ucp".equals(msgs.get(position).getModule())) {
                holder.tv_message_icon.setBackgroundResource(R.drawable.message_ucp);
            } else if ("hnq".equals(msgs.get(position).getModule())) {
                holder.tv_message_icon.setBackgroundResource(R.drawable.message_hnq);
            }
            holder.tv_message_title.setText(msgs.get(position).getMessageTitle());
            holder.tv_message_content.setText(msgs.get(position).getMessageContent());
            return convertView;
        }

    }

    private class ViewHolder {
        ImageView tv_message_icon;
        TextView tv_message_title;
        TextView tv_message_content;
    }

}

