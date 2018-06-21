package com.xiangpu.adapter;

import java.util.List;

import com.konecty.rocket.chat.R;
import com.xiangpu.bean.AudioSearchBean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class VoiceSearchResultAdapter extends BaseAdapter{

	private  List<AudioSearchBean> audioList;
	private Context mContext;
	
	public VoiceSearchResultAdapter(Context context,List<AudioSearchBean> list){
		this.audioList = list;
		this.mContext = context;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return audioList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return audioList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		AudioSearchBean bean = audioList.get(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.voice_result_items, null);

			viewHolder = new ViewHolder();
			viewHolder.tvId = (TextView) convertView.findViewById(R.id.column_tv_id);
			viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_name_id);
			viewHolder.tvRemark= (TextView) convertView.findViewById(R.id.tv_remark_id);
		
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.tvName.setText(bean.name);
		viewHolder.tvRemark.setText(bean.remark);
		
		return convertView;
	}
	
	class ViewHolder {
		public TextView tvId;
		public TextView tvName;
		public TextView tvRemark;
	}

}
