package com.ysk.turingv2.Activity;

import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.ysk.turingv2.R;
import com.ysk.turingv2.adapter.CustomInfoAdapter;
import com.ysk.turingv2.bean.Custom;

import org.litepal.LitePal;
import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.List;

public class ManageCustomActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener {
    private ListView customListView;
    private Custom customInfo;
    private List<Custom> list = new ArrayList<>();
    private CustomInfoAdapter customAdapter;
    private String question;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cusview_layout);

        //连接数据库
        Connector.getDatabase();
        //初始化布局
        initLayout();
        readCustom();
    }
    /**
     * 初始化布局
     */
    private void initLayout() {

        customListView = (ListView) findViewById(R.id.all_custom);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);//默认左上角生成返回按钮
        toolbar.setTitle("管理所有自定义话术");
        customListView.setOnItemLongClickListener(this);
    }

    /*
    **从数据库中取出所有问答
     */
    private void readCustom(){
        List<Custom>customList= LitePal.findAll(Custom.class);
        if(customList!=null&&customList.size()!=0) {
            for (Custom custom : customList) {
               customInfo=new Custom();
               customInfo.setId(custom.getId());
               customInfo.setSendtext(custom.getSendtext());
               customInfo.setReceivetext(custom.getReceivetext());
               list.add(customInfo);
            }
        }
        setAdapter();
    }
    /**
     * 返回按钮监听
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     * 实现长按listview的功能
     */
    @Override
    public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {

        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("确认删除吗？删除后数据不可恢复！")
                .setPositiveButton("确认删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Custom customInfo = list.get(position);
                        question = customInfo.getSendtext();
                        deleteCustom();
                        list.remove(position);
                        setAdapter();

                        Toast.makeText(ManageCustomActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setCancelable(true).show();
        return false;
    }
    /**
     * 从数据库中删除问题
     */
    private void deleteCustom(){
        LitePal.deleteAll(Custom.class,"sendtext=?",question);//把这个问题对应的行全部删掉
    }

    /**
     * 更新适配器
     */
    private void setAdapter(){

        customAdapter = new CustomInfoAdapter(list);
        customListView.setAdapter(customAdapter);
        customAdapter.notifyDataSetChanged();
        customListView.setSelection(list.size()-1);
    }
}
