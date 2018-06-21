package com.suneee.sepay;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.suneee.sepay.core.exception.PayTypeException;
import com.suneee.sepay.core.sepay.bean.PayType;
import com.suneee.sepay.core.sepay.bean.PayTypeItem;
import com.suneee.sepay.core.sepay.config.WXConfig;
import com.suneee.sepay.core.sepay.contract.PayContract;
import com.suneee.sepay.core.sepay.model.WXModel;
import com.suneee.sepay.core.sepay.presenter.PayPresenter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


/**
 * Created by suneee on 2016/6/22.
 */
public class SEPayMainActivity extends Activity implements PayContract.View{

    private static final String TAG = SEPayMainActivity.class.getSimpleName();

    public static final String INTENT_CREATE_TS = "created_ts";
    public static final String INTENT_PAY_NO = "pay_no";
    public static final String INTENT_SE_SIGN = "se_sign";
    public static final String INTENT_ORDERMONEY = "orderMoney";

    private ProgressDialog loadingDialog;

    private PayPresenter payPresenter;
    private TextView orderMoneyTv;
    private RecyclerView payTypeRv;
    private PayTypeAdapter adapter;
    private TextView emptyMessageTv;
    private TextView regetTv;
    private RelativeLayout emptyRl;
    private LinearLayout typeListLl;
    private String orderMoney;
    private String created_ts;
    private String pay_no;
    private String se_sign;

    private PayResultCallback payResultCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sepay_main);

        initIntent();
        payPresenter = new PayPresenter(this);
        initView();

        /*created_ts = "1473407173";
        pay_no = "P160909001000869";
        se_sign = "da8fa523854d1f7a91fa83e8e8002e94";*/

        payPresenter.requestPayTypeList(created_ts, pay_no, se_sign);
        //old
//        payPresenter.requestPayTypeList();

        payResultCallback = SEPayManager.getInstance().getPayResultCallback();
    }

    private void initIntent() {
        created_ts = getIntent().getStringExtra(INTENT_CREATE_TS);
        pay_no = getIntent().getStringExtra(INTENT_PAY_NO);
        se_sign = getIntent().getStringExtra(INTENT_SE_SIGN);
        orderMoney = getIntent().getStringExtra(INTENT_ORDERMONEY);
    }

    private void initView() {

        ImageView backIv = (ImageView) findViewById(R.id.iv_back);
        backIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(payResultCallback != null){
                    payResultCallback.onFaile(PayResultCallback.ERROR_RETURN);
                }
                finish();
            }
        });

        typeListLl = (LinearLayout) findViewById(R.id.ll_typelist);
        emptyRl = (RelativeLayout) findViewById(R.id.rl_empty);
        emptyMessageTv = (TextView) findViewById(R.id.tv_message);
        regetTv = (TextView) findViewById(R.id.tv_reget);
        regetTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payPresenter.requestPayTypeList(created_ts, pay_no, se_sign);
                //old
//                payPresenter.requestPayTypeList();
            }
        });


        orderMoneyTv = (TextView) findViewById(R.id.tv_orderMoney);
        orderMoneyTv.setText(orderMoney);

        payTypeRv = (RecyclerView) findViewById(R.id.rv_payType);
        payTypeRv.setLayoutManager(new LinearLayoutManager(this));
        DividerLine dividerLine = new DividerLine(DividerLine.VERTICAL);
        dividerLine.setSize(1);
        dividerLine.setColor(R.color.bg);
        
        if(!"".equals(WXConfig.APP_ID)){
        	payPresenter.initWXPay((Activity)this, WXConfig.APP_ID);
        }
        
        payTypeRv.addItemDecoration(dividerLine);
        adapter =  new PayTypeAdapter(this);
        adapter.setItemClickListener(new PayTypeAdapter.ItemClickListener() {
            @Override
            public void onItemClick(int position) {
                {
                    if(TextUtils.isEmpty(pay_no)){
                        Toast.makeText(SEPayMainActivity.this, "没有订单参数...", Toast.LENGTH_LONG).show();
                        return;
                    }
                    PayTypeItem item = adapter.getDatas().get(position);
                    if(String.valueOf(PayType.ALI).equals(item.payment_type_id)){
                        try {
                            payPresenter.requestPayParam(PayType.ALI, pay_no);
                            //old
//                            payPresenter.requestPayParam(PayType.ALI);
                        } catch (PayTypeException e) {
                            e.printStackTrace();
                            Toast.makeText(SEPayMainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }else if((String.valueOf(PayType.WX).equals(item.payment_type_id))){
                        if (WXModel.getInstance().isWXAppInstalledAndSupported() && WXModel.getInstance().isWXPaySupported()) {
                            //对于微信支付, 手机内存太小会有OutOfResourcesException造成的卡顿, 以致无法完成支付
                            //这个是微信自身存在的问题
                            try {
                                payPresenter.requestPayParam(PayType.WX, pay_no);
                                //old
//                            payPresenter.requestPayParam(PayType.WX);
                            } catch (PayTypeException e) {
                                e.printStackTrace();
                                Toast.makeText(SEPayMainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(SEPayMainActivity.this, "您尚未安装微信或者安装的微信版本不支持", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }else if((String.valueOf(PayType.GS).equals(item.payment_type_id))){
                    	  Toast.makeText(SEPayMainActivity.this,"不支持", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        payTypeRv.setAdapter(adapter);

        initLoadingDialog();
    }

    private void initLoadingDialog() {
        // 如果调起支付太慢, 可以在这里开启动画, 以progressdialog为例
        loadingDialog = new ProgressDialog(SEPayMainActivity.this);
        loadingDialog.setMessage("处理中，请稍候...");
        loadingDialog.setIndeterminate(true);
        loadingDialog.setCancelable(true);
    }

    @Override
    public void showDialog() {
        loadingDialog.show();
    }

    @Override
    public void hideDialog() {
        loadingDialog.dismiss();
    }

    @Override
    public void showErrorMsg(final String errorMsg) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!TextUtils.isEmpty(errorMsg)){
                    //Toast.makeText(SEPayMainActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    Log.e(TAG, errorMsg);
                }

                if(SEPayManager.getInstance().getPayResultCallback()!=null){
                    SEPayManager.getInstance().getPayResultCallback().onFaile(errorMsg);
                }
            }
        });

    }

    @Override
    public void showPayList(List<PayTypeItem> payTYpeList) {

        if(payTYpeList == null){
            typeListLl.setVisibility(View.GONE);
            emptyRl.setVisibility(View.VISIBLE);
            emptyMessageTv.setText("未获取到支付列表，请重试！");
            regetTv.setVisibility(View.VISIBLE);
            return;
        }

        if(payTYpeList.size() == 0){
            typeListLl.setVisibility(View.GONE);
            emptyRl.setVisibility(View.VISIBLE);
            emptyMessageTv.setText("没有支付列表");
            regetTv.setVisibility(View.GONE);
            return;
        }else{

            typeListLl.setVisibility(View.VISIBLE);
            emptyRl.setVisibility(View.GONE);

            adapter.setDatas(payTYpeList);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showPayResult() {
        //支付完成,该方法在子线程中
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //回到APp中
                //Toast.makeText(SEPayMainActivity.this, "支付成功...", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "支付成功...");
                if(SEPayManager.getInstance().getPayResultCallback()!=null){
                    SEPayManager.getInstance().getPayResultCallback().onSuccess();
                    finish();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        payPresenter.detachView();
        SEPayManager.getInstance().destory();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            if(payResultCallback != null){
                payResultCallback.onFaile(PayResultCallback.ERROR_RETURN);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        handleUPPay(data);
    }

    /**
     * 银联结果处理
     * @param data
     */
    private void handleUPPay(Intent data) {
        if (data == null) {
            return;
        }

        String msg = "";
        /*
         * 支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
         */
        String str = data.getExtras().getString("pay_result");
        if (str.equalsIgnoreCase("success")) {
            // 支付成功后，extra中如果存在result_data，取出校验
            // result_data结构见c）result_data参数说明
            if (data.hasExtra("result_data")) {
                String result = data.getExtras().getString("result_data");
                try {
                    JSONObject resultJson = new JSONObject(result);
                    String sign = resultJson.getString("sign");
                    String dataOrg = resultJson.getString("data");
                    // 验签证书同后台验签证书
                    // 此处的verify，商户需送去商户后台做验签
                    String model = "";

                    boolean ret = verify(dataOrg, sign, model);
                    if (ret) {
                        // 验证通过后，显示支付结果
                        msg = "支付成功！";
                    } else {
                        // 验证不通过后的处理
                        // 建议通过商户后台查询支付结果
                        msg = "支付失败！";
                    }
                } catch (JSONException e) {
                }
            } else {
                // 未收到签名信息
                // 建议通过商户后台查询支付结果
                msg = "支付成功！";
            }
        } else if (str.equalsIgnoreCase("fail")) {
            msg = "支付失败！";
        } else if (str.equalsIgnoreCase("cancel")) {
            msg = "用户取消了支付";
        }
        showErrorMsg(msg);
    }

    private boolean verify(String msg, String sign64, String mode) {
        // 此处的verify，商户需送去商户后台做验签
        return true;
    }
}
