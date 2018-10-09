package com.ysk.turingv2.Action;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ysk.turingv2.Activity.MainActivity;
import com.ysk.turingv2.bean.Chat;
import com.ysk.turingv2.bean.GeocodesBean;
import com.ysk.turingv2.bean.Location;
import com.ysk.turingv2.util.HttpUtil;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

import static android.support.v4.content.ContextCompat.startActivity;

public class NavigationAction {
    private static final String TAG = "NavigationAction";
    //经纬度
    private String location;
    private String latitude;//纬度
    private  String longitude;//经度
    String dizhi;
    MainActivity mActivity;

    public  NavigationAction(MainActivity mActivity,String dizhi){
        this.mActivity=mActivity;
        this.dizhi=dizhi;
    }
    public void start(){
        if ((dizhi != null) && (dizhi.length() > 0)){
            setNavigation();
            mActivity.addData("即将为您打开高德导航", Chat.TYPE_RECEIVED,mActivity.getCurrentTime());//机器人说的话添加到接收文本框里
            mActivity.speakText("即将为您打开高德导航");
        }else {
            mActivity.addData("至少应该告诉导航到哪里吧^_^", Chat.TYPE_RECEIVED,mActivity.getCurrentTime());//机器人说的话添加到接收文本框里
            mActivity.speakText("至少应该告诉导航到哪里吧");
        }
    }
    private void setNavigation() {
            HttpUtil.sendOkHttpRequest("https://restapi.amap.com/v3/geocode/geo?address=" + dizhi + "&key=2d7ad20513168e8d162b70eee75de8b5", new okhttp3.Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    //得到服务器返回的具体内容
                    String responseData = response.body().string();
                    Log.e(TAG, "responseData=======" + responseData);
                    parseJSONWithGSON(responseData);
                    setUpGaodeAppByMine();
                }
                @Override
                public void onFailure(Call call, IOException e) {

                }
            });

    }

    private void parseJSONWithGSON(String jsonData){
        Gson gson =new Gson();
        Location jdata=gson.fromJson(jsonData,Location .class);//解析掉第一层
        List<GeocodesBean> beanList= jdata .getGeocodes();//getGeocodes()得到的就是一个数组[]，封装为list
        Log.e(TAG,"location:"+beanList.get(0).getLocation());//list的第一个（即序号0）的数据中就含有location
        location=beanList.get(0).getLocation();
        longitude=location.substring(0,location.indexOf(","));//截取","前的字符串，经度
        latitude=location.substring(location.indexOf(",")+1,location.length());//截取","后的字符串，纬度
    }

    void setUpGaodeAppByMine(){
        try {
            Intent intent = Intent.getIntentOld("androidamap://route?sourceApplication=softname&sname=我的位置&dlat="+latitude+"&dlon="+longitude+"&dname="+dizhi+"&dev=0&m=0&t=0");//其中t=0是驾车，
            //直接写入参数 我的位置 就可以了，这样进入高德或者百度地图app中直接就给定位了获取到当前位置了。不用在自己的项目中通过sdk获取到当前位置的坐标然后传入参数
            if(isInstallByread("com.autonavi.minimap")){
                mActivity.startActivity(intent);
                Log.e(TAG, "高德地图客户端已经安装") ;
            }else {
                Log.e(TAG, "没有安装高德地图客户端") ;
                Toast.makeText(mActivity, "请安装高德地图APP", Toast.LENGTH_SHORT).show();
                mActivity.addData("没有安装高德地图APP，请先下载安装", Chat.TYPE_RECEIVED,mActivity.getCurrentTime());
                mActivity.speakText("没有安装高德地图APP，请先下载安装");
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
    /**
     * 判断是否安装目标应用
     * @param packageName 目标应用安装后的包名
     * @return 是否已安装目标应用
     */
    private boolean isInstallByread(String packageName) {
        return new File("/data/data/" + packageName).exists();
    }

}
