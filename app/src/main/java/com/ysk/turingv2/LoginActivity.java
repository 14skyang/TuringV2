package com.ysk.turingv2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ysk.turingv2.bean.ChatHistory;
import com.ysk.turingv2.bean.User;

import org.litepal.LitePal;

import java.util.List;

/*
程序启动时显示的第一个活动界面,即为登陆页面
 */
public class LoginActivity extends AppCompatActivity {


    //用户名文本编辑框
    private EditText username;
    //密码文本编辑框
    private EditText password;
    //登录按钮

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置布局
        setContentView(R.layout.login_layout);

        //得到登录按钮对象
        Button login = (Button) findViewById(R.id.login);
        Button regist = (Button) findViewById(R.id.regist);
        //给登录按钮设置监听器
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //得到用户名和密码的编辑框
                username = (EditText) findViewById(R.id.username);
                password = (EditText) findViewById(R.id.password);

                //判断是否成功匹配的标志
                boolean flag = false;
                //LitePal里遍历查询所有数据的方法
                List<User> users = LitePal.findAll(User.class);
                for (User user : users) {
                    //判断用户输入的用户名和密码是否与数据库中相同
                    if (user.getUsername().equals(username.getText().toString()) &&
                            user.getPassword().equals(password.getText().toString())) {

                        flag = true;
                    }
                }

                if (flag) {
                    //创建Intent对象，传入源Activity和目的Activity的类对象
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                    //传递用户名到主活动
                    String USERNAME = username.getText().toString();
                    intent.putExtra("userName", USERNAME);
                    //启动Activity
                    startActivity(intent);
                    finish();//结束掉本活动
                } else {
                    //登录信息错误，通过Toast显示提示信息
                    Toast.makeText(LoginActivity.this, "用户登录信息错误", Toast.LENGTH_SHORT).show();
                }
            }
        });
        regist.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegistActivity.class);
                startActivity(intent);//此处不要结束本活动，注册完还返回到本活动
            }
        });

    }


}



