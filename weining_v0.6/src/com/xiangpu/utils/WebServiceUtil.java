package com.xiangpu.utils;

import android.app.Application;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.lssl.activity.SuneeeApplication;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class WebServiceUtil {
    private static final String TAG = "WebServiceUtil";
    private static final String TAG_RESULT_CODE = "errorCode";
    private static final String TAG_RESULT_REMARK = "msg";

    public static interface OnDataListener {
        public void onReceivedData(String mode, JSONObject result);

        public List<NameValuePair> onGetParamData(String mode);

        public String onGetParamDataString(String mode);
    }


    public static String pay(String out_trade_no, String subject, String total_fee, int business_type, String url) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("out_trade_no", out_trade_no));
        params.add(new BasicNameValuePair("subject", subject));
        params.add(new BasicNameValuePair("total_fee", "0.01"));
        params.add(new BasicNameValuePair("business_type", String.valueOf(business_type)));

        return doPost(url, params);
    }

    public static String doPost(String url, List<NameValuePair> params) {
        String strResult = "doPostError";

        try {
            HttpPost httpRequest = new HttpPost(url.trim());
            HttpClient httpClient = getHttpClient();

            if (params != null) {
                /* 添加请求参数到请求对象 */
                httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            }

			 /* 发送请求并等待响应 */
            HttpResponse httpResponse = null;
            try {
                // 请求超时
                httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
                // 读取超时
                httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 20000);
                httpResponse = httpClient.execute(httpRequest);

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (httpResponse == null) {
                return strResult;
            }

           /* 若状态码为200 ok */
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
               /* 读返回数据 */
                try {
                    strResult = EntityUtils.toString(httpResponse.getEntity());
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {

            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return strResult;
    }

    public static String doPost(String url, String paramsJson) {
        String strResult = "doPostError";

        try {
            Log.e("url", url + "");
            HttpPost httpRequest = new HttpPost(url.trim());

            HttpClient httpClient = getHttpClient();
            httpRequest.addHeader("Content-Type", "application/json; charset=utf-8");

            if (url.contains("json/mobile/filelist")) {
                httpRequest.addHeader("Content-Token", paramsJson);
                httpRequest.setEntity(new StringEntity("", HTTP.UTF_8));
            } else {
                httpRequest.setEntity(new StringEntity(paramsJson, HTTP.UTF_8));
            }

            //StringEntity就是以字符串输出到流
            HttpResponse httpResponse;
            // 请求超时
            httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
            // 读取超时
            httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
            httpResponse = httpClient.execute(httpRequest);

            if (httpResponse == null) {
                return strResult;
            }

            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                strResult = EntityUtils.toString(httpResponse.getEntity());
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return strResult;
    }

    public static HttpClient getHttpClient() {
        return new DefaultHttpClient(new BasicHttpParams());
    }

    public static class RequestResult {
        public Exception exception;
        public OnDataListener listener;
        public JSONObject data;
        public String url;
    }

//	public static String pay(String out_trade_no,String subject,float total_fee,int business_type,String url){
//		List<NameValuePair> params = new ArrayList<NameValuePair>();
//		
//		  params.add(new BasicNameValuePair("out_trade_no", out_trade_no));
//		  params.add(new BasicNameValuePair("subject", subject));
//		  params.add(new BasicNameValuePair("total_fee", String.valueOf(total_fee)));
//		  params.add(new BasicNameValuePair("business_type", String.valueOf(business_type)));
//			  
//		 return  doPost(url, params);
//	}

    public static JSONObject requestJson(final String url, final OnDataListener listener) {
        RequestResult rr = new RequestResult();
        rr.listener = listener;
        rr.url = url;

        JSONObject json = null;
        try {

            List<NameValuePair> params = null;

            if (listener != null) {
                params = listener.onGetParamData(url);
                if (params == null) {
                    //return ;
                }
            }

            String result = doPost(url, params);
            json = new JSONObject(result);
            rr.data = json;

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {

        }

        return json;
    }

    //请求
    public static void request(final String url, final String type, final OnDataListener listener) {

        new Thread() {
            @Override
            public void run() {

                RequestResult rr = new RequestResult();
                rr.listener = listener;
                rr.url = url;

                JSONObject json;
                try {

                    String result = "";

                    if (listener != null) {
                        if (type.equals("json")) {
                            String params = listener.onGetParamDataString(url);
                            Log.e("params", params);
                            if (params == null) {
                                //return ;
                            }
                            result = doPost(url, params);
                            LogUtil.i(TAG, "Http Respond----->" + result);
                        } else {
                            List<NameValuePair> params = listener.onGetParamData(url);
                            if (params == null) {
                                //return ;
                            }
                            result = doPost(url, params);
                        }

                    }
                    if (result.startsWith("[")) {
                        result = result.substring(1, result.length() - 1);
                    }
                    json = new JSONObject(result);
                    rr.data = json;

                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = rr;
                    mRequestHandler.sendMessage(msg);

                } catch (JSONException e) {
                    e.printStackTrace();

                    rr.data = null;

                    Message msg = new Message();
                    msg.what = 2;
                    msg.obj = rr;
                    mRequestHandler.sendMessage(msg);

                }
//                finally {
//                    Message msg = new Message();
//                    msg.what = 1;
//                    msg.obj = rr;
//                    mRequestHandler.sendMessage(msg);
//                }
            }
        }.start();
    }

    private static Handler mRequestHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            Application app = SuneeeApplication.getInstance();

            RequestResult rr = (RequestResult) msg.obj;

//			if(rr.exception != null)
//			{
//				
//				if(rr.exception.getClass() == ConnectException.class){
//					Toast.makeText(app, "网络连接失败", Toast.LENGTH_LONG).show();
//				} else if(rr.exception.getClass() == IOException.class){
//					Toast.makeText(app, "网络异常", Toast.LENGTH_LONG).show();
//				}
//			}else{
//				Toast.makeText(app, "连接异常", Toast.LENGTH_LONG).show();
//				return ;
//			}

            switch (msg.what) {
                case 0:

                    try {
                        if (rr.data != null) {

                            Integer rs = rr.data.getInt(TAG_RESULT_CODE);

                            if (rs != null && rs != 1) {
                                String remark = rr.data.getString(TAG_RESULT_REMARK);
                                if (remark != null)
                                    Toast.makeText(app, remark, Toast.LENGTH_LONG).show();

                                rr.data = null;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (rr.listener != null) {
                        rr.listener.onReceivedData(rr.url, rr.data);
                    }
                    break;

                case 1: {
//				if(rr.data != null){
//					
//					try {
//						String errcode = rr.data.getString(TAG_RESULT_CODE);
//						String errcodeMsg = rr.data.getString(TAG_RESULT_REMARK);
//						if(errcode.equals("0000")){
//							
//						}else{
//							//rr.data = null;
//							//Toast.makeText(app,errcodeMsg, Toast.LENGTH_LONG).show();
//						}
//						
//					} catch (JSONException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}

                    if (rr.listener != null) {
                        rr.listener.onReceivedData(rr.url, rr.data);
                    }

                }
                break;

                case 2:
                    if (rr.listener != null) {
                        rr.listener.onReceivedData(rr.url, rr.data);
                    }
                    break;

                default:
                    break;
            }
        }
    };
}
