package com.suneee.sepay;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.suneee.sepay.R;
import com.suneee.sepay.core.sepay.bean.PayType;
import com.suneee.sepay.core.sepay.bean.PayTypeItem;

import java.util.ArrayList;
import java.util.List;


/**
 * 支付方式列表adapter
 * Created by suneee on 2016/6/27.
 */
public class PayTypeAdapter extends RecyclerView.Adapter {

    private LayoutInflater mInflater;
    private List<PayTypeItem> datas;
    private Context context;
    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener clickListener){
        this.itemClickListener = clickListener;
    }

    public List<PayTypeItem> getDatas() {
        return datas;
    }
    public void setDatas(List<PayTypeItem> datas) {
        this.datas = datas;
    }

    public PayTypeAdapter(Context context){
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayout = mInflater.inflate(R.layout.adapter_paytype, parent, false);
        return new ViewHolder(itemLayout);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        PayTypeItem itemData = datas.get(position);
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.nameTv.setText(itemData.name);
        if(String.valueOf(PayType.ALI).equals(itemData.payment_type_id)){
            viewHolder.typeLogoIv.setImageResource(R.drawable.alipay);
        }else if(String.valueOf(PayType.WX).equals(itemData.payment_type_id)){
            viewHolder.typeLogoIv.setImageResource(R.drawable.wechat);
        }/*else if(PayType.YL.equals(itemData.payment_type_id)){
            viewHolder.typeLogoIv.setImageResource(R.mipmap.wechat);
        }*/

        if(itemClickListener!=null){
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClickListener.onItemClick(position);
                }
            });
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        if (datas == null)
            datas = new ArrayList<>();
        return datas.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        public View itemView;
        public ImageView typeLogoIv;
        public TextView nameTv;

        public ViewHolder(View itemView) {
            super(itemView);
            typeLogoIv = (ImageView) itemView.findViewById(R.id.iv_typelogo);
            nameTv = (TextView) itemView.findViewById(R.id.tv_typename);
            this.itemView = itemView;
        }
    }

    public static interface ItemClickListener{

        public void onItemClick(int position);
    }
}
