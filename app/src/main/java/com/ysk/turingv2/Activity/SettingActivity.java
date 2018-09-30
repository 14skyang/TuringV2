package com.ysk.turingv2.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ysk.turingv2.R;
import com.ysk.turingv2.animation.ItemView;
import com.ysk.turingv2.bean.Custom;

import org.litepal.LitePal;
import org.litepal.tablemanager.Connector;

import java.util.List;
import java.util.Set;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SettingActivity";
    private ImageView mBackground;
    private ImageView mHead;
    private TextView mUserName;

    private ItemView mNickName;

    private ItemView allUserName;
    private ItemView allRobotCustom;
    private ItemView mPass;
    private ItemView robotCustom;
    private ItemView robotName;
    private ItemView mAbout;
    private ItemView voice;

    //用户名
    private String data;
    //用户设置的机器人姓名
    private String mRobotName;
    //用户自定义的语料库
    private String rQuestion;
    private String rAnswer;

    //用于修改密码

    private String rNewPass;
    private String rConfirmPass;

    //用于存储用户名等

    //设置发音人
    private String speaker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);//默认左上角生成返回按钮
        toolbar.setTitle("应用设置");
        /*//获取传递过来的用户名
        Intent intent = getIntent();
        data = intent.getStringExtra("data");*/
        //连接数据库
        Connector.getDatabase();
        initLayout();
        mUserName.setText("张三丰");
        mNickName.setRightDesc("张三丰");//右侧描述文字
        mNickName.setOnClickListener(this);
        allRobotCustom.setOnClickListener(this);
        mPass.setOnClickListener(this);
        mAbout.setOnClickListener(this);
        robotCustom.setOnClickListener(this);
        voice.setOnClickListener(this);
        mHead.setOnClickListener(this);


    }
    /**
     * 初始化布局
     */
    private void initLayout() {

        //顶部头像控件
        mBackground = (ImageView) findViewById(R.id.h_background);
        mHead = (ImageView) findViewById(R.id.h_head);
        mUserName = (TextView) findViewById(R.id.user_name);

        //下面item控件
        mNickName = (ItemView) findViewById(R.id.nickName);
        allRobotCustom = (ItemView) findViewById(R.id.allcustom);
        mPass = (ItemView) findViewById(R.id.pass);
        robotCustom = (ItemView) findViewById(R.id.robot_custom);
        mAbout = (ItemView) findViewById(R.id.about);
        voice = (ItemView) findViewById(R.id.voice);
    }

    /**
     * 实现点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.h_head:
                Toast.makeText(SettingActivity.this, "更换头像功能敬请期待...", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nickName:
                Toast.makeText(SettingActivity.this, "更改用户名功能敬请期待", Toast.LENGTH_SHORT).show();
                break;
            case R.id.voice:
                selectVoiceDialog();
                break;
            case R.id.pass:
                changePassDialog();
                break;
            case R.id.robot_custom:
                robotCustomDialog();
                break;
            default:
                break;
        }
    }
    /**
     * 弹出设置发音人的选择框
     */
    private void selectVoiceDialog(){

        final String[] speakers = {"小琪(青年女声，普通话)", "小宇(青年男声，普通话)", "小梅(青年女声，粤语)",
                "小芸(青年女声，东北话)", "小蓉(青年女声，四川话)", "晓琳(青年女声，台湾普通话)", "小莹(青年女声，陕西话)"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择发音人");
        builder.setIcon(R.drawable.icon);
        builder.setItems(speakers, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        speaker = "xiaoqi";
                        break;
                    case 1:
                        speaker = "xiaoyu";
                        break;
                    case 2:
                        speaker = "xiaomei";
                        break;
                    case 3:
                        speaker = "vixyun";
                        break;
                    case 4:
                        speaker = "vixr";
                        break;
                    case 5:
                        speaker = "xiaolin";
                        break;
                    case 6:
                        speaker = "vixying";
                        break;
                    default:
                        break;
                }

                Toast.makeText(SettingActivity.this, "发音人设置成功,点击返回生效", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setCancelable(true).show();
    }


    /**
     * 弹出修改语料库对话框
     */
    private void robotCustomDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("请输入自定义问答");
        //通过LayoutInflater来加载一个xml的布局文件作为一个View对象
        View view = LayoutInflater.from(SettingActivity.this).inflate(R.layout.dialog_layout, null);
        //设置自己定义的布局文件作为弹出框的Content
        builder.setView(view);

        final EditText question = (EditText)view.findViewById(R.id.question);
        final EditText answer = (EditText)view.findViewById(R.id.answer);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                rQuestion = question.getText().toString().trim();
                rAnswer = answer.getText().toString().trim();

                if (TextUtils.isEmpty(rQuestion)){
                    Toast.makeText(SettingActivity.this, "请输入您要设置的问题", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(rAnswer)){
                    Toast.makeText(SettingActivity.this, "请输入您希望得到的回答", Toast.LENGTH_SHORT).show();
                    return;
                }else {

                    //检测是否存在该问题
                    if (checkQuestion(rQuestion)){
                        dialog();

                    }else {
                        //保存问题和回复
                        SaveCustom(rQuestion,rAnswer);
                        Toast.makeText(SettingActivity.this, "自定义语料库成功", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
            }
        });
        builder.show();
    }

    /**
     * 弹出修改密码对话框
     */
    private void changePassDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("修改密码");
        View view = LayoutInflater.from(SettingActivity.this).inflate(R.layout.editpass_layout, null);
        builder.setView(view);

        final EditText newPass = (EditText) view.findViewById(R.id.newpass);
        final EditText confirmPass = (EditText) view.findViewById(R.id.surepass);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                rNewPass = newPass.getText().toString().trim();
                rConfirmPass = confirmPass.getText().toString().trim();

                if(TextUtils.isEmpty(rNewPass)){
                    Toast.makeText(SettingActivity.this, "请输入要修改的密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(rConfirmPass)){
                    Toast.makeText(SettingActivity.this, "请再次输入密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(rNewPass.equals(rConfirmPass)){
                    updatePass();
                    Toast.makeText(SettingActivity.this, "密码修改成功,请重新登陆", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(SettingActivity.this, "两次输入密码不一致，请重新输入",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    /**
     * 更新数据库中的密码
     */
    private void updatePass(){

     //
    }



    /**
     * 添加语料库
     */
    public void SaveCustom(String question,String answer){ //发送的消息/问题
        Custom custom=new Custom();
        custom.setSendtext(question);
        custom.setReceivetext(answer);
        custom.save();
        //custom.setUsername();
        //保存到数据库
    }


    /**
     * 检测数据库中是否存在该问题
     */
    public boolean checkQuestion(String question){ //有则返回true,没有就返回false
        List<Custom>customList= LitePal.where("sendtext=?",question).find(Custom.class);//只查询设置的发的消息
        for(Custom custom:customList){
            if (question.equals(custom.getSendtext())){
                return true;
            }
        }
        return false;
    }

    /**
     * 更新数据库中相应问题的答案
     */
    private void updateAnswer(String answer){ //在找到相同问题的前提下
        Custom custom=new Custom();
        custom.setReceivetext(answer);
        custom.updateAll("sendtext",rQuestion);//更新sendtext=rQuestion的那一行
    }
    /**
     * 返回按钮在ToolBar中的点击事件
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home){//点击返回键，则回到主活动
            Log.e(TAG, "点击了返回键 " );
            //返回这里获取的数据给上一个活动，即主活动
            Intent intent1 = new Intent(SettingActivity.this,MainActivity.class);
            //intent1.putExtra("mRobotName", mRobotName);
            // intent1.putExtra("rQuestion", rQuestion);
            //intent1.putExtra("rAnswer", rAnswer);
            intent1.putExtra("speaker", speaker);//发音人
            Log.e(TAG, "发音人: "+speaker);
            setResult(RESULT_OK, intent1);

            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     * 如果点击返回按钮，将数据传递到上一个活动
     */
    @Override
    public void onBackPressed() {

        Intent intent=new Intent(SettingActivity.this,MainActivity.class);
        /*intent.putExtra("mRobotName",mRobotName);
        intent.putExtra("rQuestion", rQuestion);
        intent.putExtra("rAnswer", rAnswer);*/
        intent.putExtra("speaker", speaker);
        Log.e(TAG, "发音人："+speaker);
        setResult(RESULT_OK,intent);
        finish();//销毁当前活动
    }

    /**
     * 设置弹出警告窗口提示
     */
    private void dialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("已添加过该问题，继续将覆盖之前的回答，是否继续？");
        builder.setTitle("提示");
        builder.setPositiveButton("继续", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                updateAnswer(rAnswer);
                Toast.makeText(SettingActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}


