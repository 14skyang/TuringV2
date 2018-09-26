package com.ysk.turingv2.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ysk.turingv2.R;
import com.ysk.turingv2.bean.User;

import org.litepal.LitePal;
import org.litepal.tablemanager.Connector;

import java.util.List;

public class RegistActivity extends AppCompatActivity {

    private static final String TAG = "RegistActivity";

    private String strusername;
    private String strpassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.regist_layout);
        Button yes = (Button) findViewById(R.id.YES);
        Button no = (Button) findViewById(R.id.NO);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText username = (EditText) findViewById(R.id.username);
                EditText password = (EditText) findViewById(R.id.password);
                strusername = username.getText().toString();
                strpassword = password.getText().toString();
                Log.e(TAG, "用户名：" + strusername);
                Log.e(TAG, "密码：" + strpassword);
                //判断用户注册是否输入了密码和用户名
                //首先学会如何判断EditText中内容为空，要包含trim（去掉首部空格）
                if (TextUtils.isEmpty(strusername)) {
                    Toast.makeText(RegistActivity.this, "用户名和密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(strpassword)) {
                    Toast.makeText(RegistActivity.this, "用户名和密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                //连接LitePal数据库
                Connector.getDatabase();
                List<User> users = LitePal.select("username").find(User.class);
                Log.e(TAG, "onClick: " + users);
                if (users != null && users.size() != 0) {//如果列表(users这个list)不为空，并且列表的size大于0，则可以用foreach遍历
                    for (User user : users) {
                        if (strusername.equals(user.getUsername())) {
                            Toast.makeText(RegistActivity.this, "该用户名已经注册", Toast.LENGTH_SHORT).show();
                            return;//退出遍历
                        }
                    }
                }
                //users数据为空或者用户名没有注册的情况,存数据
                Log.e(TAG, "数据库为空");
                User user = new User();//如果不新建一个对象，则会覆盖掉原来的数据
                user.setUsername(strusername);
                user.setPassword(strpassword);
                user.save();
                Toast.makeText(RegistActivity.this, "创建用户成功", Toast.LENGTH_SHORT).show();
                finish();//结束本活动，就会回到登陆活动

            }


        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();//结束本活动，就会回到登陆活动
            }
        });
    }


}





