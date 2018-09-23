package com.ysk.turingv2;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.util.Log;

import com.ysk.turingv2.adapter.RecyclerViewAdapter;
import com.ysk.turingv2.bean.Chat;

import java.util.List;

/**
 * 根据app名称打开对应的app
 */

public class OpenAppAction {

    MainActivity mActivity;
    String appName;

    public OpenAppAction(MainActivity mActivity, String appName) {
        this.mActivity = mActivity;
        this.appName = appName;
    }

    public void start(){
        if ((appName != null) && (appName.length() > 0)){
            launchAppByName();
        }else {
            mActivity.addData("至少应该告诉我应用名称吧^_^", Chat.TYPE_RECEIVED,mActivity.getCurrentTime());//机器人说的话添加到接收文本框里
            //mActivity.answerText.setText("至少应该告诉我应用名称吧^_^");
        }
    }

    private void launchAppByName(){


        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");

        PackageManager pm = mActivity.getPackageManager();
        List<ResolveInfo> installAppList = pm.queryIntentActivities(intent, 0);

        for (ResolveInfo info : installAppList){
            String name = info.loadLabel(pm).toString();

            Log.e("tag","name:"+name);

            if (name.equalsIgnoreCase(appName)){

                String pkgName = info.activityInfo.packageName;
                if ("com.android.contacts".equals(pkgName) ){
                    Uri uri = Uri.parse("content://contacts/people");
                    Intent i = new Intent("android.intent.action.VIEW", uri);
                    mActivity.startActivity(i);

                }else {
//                    intent = pm.getLaunchIntentForPackage(pkgName);
//                    intent.addCategory("android.intent.category.LAUNCHER");

                    intent=mActivity.getPackageManager().getLaunchIntentForPackage(pkgName);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    // mActivity.answerText.setText("已打开" + appName + "...");
                    mActivity.addData("已打开" + appName + "...", Chat.TYPE_RECEIVED,mActivity.getCurrentTime());//机器人说的话添加到接收文本框里
                    mActivity.startActivity(intent);

                }
                mActivity.speakText("已经打开"+appName);
                return;
            }
//            mActivity.answerText.setText("没有找到你所说的应用哦^_^");
            // mAdapter.addItem(new Msg("没有找到你所说的应用哦^_^", true));//装配消息
            mActivity.speakText("没有找到你所说的应用哦^_^");
        }

    }
}

