package com.ysk.turingv2;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.ysk.turingv2.adapter.RecyclerViewAdapter;
import com.ysk.turingv2.bean.Ask;
import com.ysk.turingv2.bean.Chat;
import com.ysk.turingv2.bean.ChatHistory;
import com.ysk.turingv2.bean.Take;
import com.ysk.turingv2.net.Api;
import com.ysk.turingv2.util.JsonParser;
import com.ysk.turingv2.util.L;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;
import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //  聊天消息列表
    private RecyclerView recyclerView;

    //  输入框
    private EditText editText;

    //  发送按钮
    private Button mButton;

    //    对话信息集合
    private List<Chat> list = new ArrayList<>();//声明一个集合list来存储我们的聊天数据

    //    适配器
    private RecyclerViewAdapter recyclerViewAdapter;
    
    private String text;//语音转文字的最终text

    private  String mText;//图灵机器人回复的最终text

    private  String username;

    //以下是讯飞部分
    //语音按钮
    private Button vButton;
    private static final String TAG = MainActivity.class .getSimpleName();
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String , String>();
    //打开app
    private String appName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED) {
            //检查是否授权，高版本手机必须有这个再次检查授权，否则会闪退
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},1);
        }
        Intent intent = getIntent();//接收登录传值
         username = intent.getStringExtra("userName");

       //数据库litepal建表
        Connector.getDatabase();
       //写入聊天记录
        initMsg();
       //初始化讯飞语音
        initSpeech() ;
//      初始化数据
        initView();
//       加载数据
        initData();
//      设置RecyclerView的布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerViewAdapter = new RecyclerViewAdapter(this, list);//将集合数据填充到适配器中
        recyclerView.setAdapter(recyclerViewAdapter);


    }
    /**
     * @return 获取聊天的当前时间
     */
    public String getCurrentTime() {
        //long currentTime = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//把日期格式化
        Date curDate = new Date();
        String time = format.format(curDate);
        /*if (currentTime != oldTime ) {//实时更新时间
            oldTime = currentTime;*/
            return time;//返回时间给调用该函数的哥们
        /*} else {
            return "";
        }*/
    }

    /**
     * 加载列表布局数据
     */
    private void initData() {
        Chat c1 = new Chat("你好，我叫小可爱", Chat.TYPE_RECEIVED,getCurrentTime());
        list.add(c1);
       /* Chat c2 = new Chat("你好，你现在会些什么呢？", Chat.TYPE_SENT);
        list.add(c2);
        Chat c3 = new Chat("我还在成长中，很多东西还不懂，但是你可以考考我", Chat.TYPE_RECEIVED);
        list.add(c3);
        Chat c4 = new Chat("1+1等于几?", Chat.TYPE_RECEIVED);
        list.add(c4);*/

    }

    /**
     * 初始化控件
     */
    private void initView() {
        recyclerView = findViewById(R.id.recycler);
        editText = findViewById(R.id.et_text);
        mButton = findViewById(R.id.btn_send);
        mButton.setOnClickListener(this);
        vButton=findViewById(R.id.btn_voice);
        vButton.setOnClickListener(this);//设置按钮监听器
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send:
                /**
                 * 1，获取输入框的内容
                 * 2，判断是否为空
                 * 3，发送后清空当前的输入框
                 */
//              1,获取输入框的内容
                String text = editText.getText().toString();
//              2,判断是否为空
                if (!TextUtils.isEmpty(text)) {
//                  把要发送的数据添加到addData方法中，并把数据类型也填入，这里我们的类型是TYPE_SENT，发送数据类型
                    addData(text, Chat.TYPE_SENT,getCurrentTime());
                    saveSendData();//保存发送数据
//                  清空输入框
                    editText.setText("");
//                  把发送的文本数据传递到request方法中，请求数据
                    request(text);//图灵请求数据
                }
                break;
            case R.id.btn_voice:
                startSpeechDialog();//显示讯飞语音输入对话框


        }
    }

    /**
     * 通过传递进来的test和type创建数据实体类，添加到聊天数据集合list中
     * @param text
     * @param type
     */
    public void addData(String text, int type,String time) {
        Chat c = new Chat(text, type,getCurrentTime());
        list.add(c);
        //当有新消息时，刷新显示
        recyclerViewAdapter.notifyItemInserted(list.size() - 1);
        //定位的最后一行
        recyclerView.scrollToPosition(list.size() - 1);

    }

    /**图灵
     * 请求数据
     *
     * @param text 输入框的发送数据
     */
    private void request(String text) {
//      把输入的文本数据存储在请求实体类中
        Ask ask = new Ask();
        Ask.UserInfoBean info = new Ask.UserInfoBean();//用户信息
        info.setApiKey("e54abcf09bb44bbd87966e9cc5367424");//将机器人的key值填入  c00282de107144fb940adab994d9ff98
        info.setUserId("319103");//将用户id填入  225167
        ask.setUserInfo(info);
        Ask.PerceptionBean.InputTextBean pre = new Ask.PerceptionBean.InputTextBean(text);//将要发送给机器人的文本数据text的对象（就像遥控器）命名为pre
        ask.setPerception(new Ask.PerceptionBean(pre));//???

//       创建Retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://openapi.tuling123.com/")//设置网络请求url，后面一段写在网络请求接口里面
                .addConverterFactory(GsonConverterFactory.create())//Gson解析完成
                .build();
//       创建网络请求接口的实例，这样才能调用接口
        Api api = retrofit.create(Api.class);
//      Take为响应实体类，用来接受机器人返回的回复数据，以下为接口调用
        //// 用法和OkHttp的call如出一辙,
        //// 不同的是如果是Android系统回调方法执行在主线程
        Call<Take> call = api.request(ask);
//
        call.enqueue(new Callback<Take>() {
            //          请求成功
            @Override
            public void onResponse(Call<Take> call, Response<Take> response) {
//              接受到的机器人回复的数据
                 mText= response.body().getResults().get(0).getValues().getText();
//              把接受到的数据传入addData方法中，类型是TYPE_RECEIVED接受数据
                addData(mText, Chat.TYPE_RECEIVED,getCurrentTime());//机器人说的话添加到接收文本框里
                saveReceiveData();//保存接收到的数据
                speakText(mText);//读出图灵机器人说的话的文本
                L.d("接受到的机器人回复的数据： "+mText);
            }
            //            请求失败
            @Override
            public void onFailure(Call<Take> call, Throwable t) {
                L.d("请求失败： "+t.toString());
            }
        });
    }

    /*
    **以下是讯飞语音部分
     */


    private void initSpeech() {
        // 将“12345678”替换成您申请的 APPID，申请地址： http://www.xfyun.cn
        // 请勿在 “ =”与 appid 之间添加任务空字符或者转义符
        SpeechUtility. createUtility( this, SpeechConstant. APPID + "=5b9e0175" );
    }
    public void speakText(String s) {   //读文本
        //1. 创建 SpeechSynthesizer 对象 , 第二个参数： 本地合成时传 InitListener
        SpeechSynthesizer mTts = SpeechSynthesizer.createSynthesizer( this, null);
//2.合成参数设置，详见《 MSC Reference Manual》 SpeechSynthesizer 类
//设置发音人（更多在线发音人，用户可参见 附录 13.2
        mTts.setParameter(SpeechConstant. VOICE_NAME, "xiaoqi" ); // 设置发音人
        mTts.setParameter(SpeechConstant. SPEED, "50" );// 设置语速
        mTts.setParameter(SpeechConstant. VOLUME, "80" );// 设置音量，范围 0~100
        mTts.setParameter(SpeechConstant. ENGINE_TYPE, SpeechConstant. TYPE_CLOUD); //设置云端
        //mTts.setParameter(SpeechConstant.ASR_PTT, "0");
//设置合成音频保存位置（可自定义保存位置），保存在 “./sdcard/iflytek.pcm”
//保存在 SD 卡需要在 AndroidManifest.xml 添加写 SD 卡权限
//仅支持保存为 pcm 和 wav 格式， 如果不需要保存合成音频，注释该行代码
      //  mTts.setParameter(SpeechConstant. TTS_AUDIO_PATH, "./sdcard/iflytek.pcm" );
//3.开始合成

        mTts.startSpeaking( s, new MySynthesizerListener()) ;//开始读字符串s

    }

    class MySynthesizerListener implements SynthesizerListener {

        @Override
        public void onSpeakBegin() {
            showTip(" 开始播放 ");
        }

        @Override
        public void onSpeakPaused() {
            showTip(" 暂停播放 ");
        }

        @Override
        public void onSpeakResumed() {
            showTip(" 继续播放 ");
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos , String info) {
            // 合成进度
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // 播放进度
        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
                showTip("播放完成 ");
            } else if (error != null ) {
                showTip(error.getPlainDescription( true));
            }
        }

        @Override
        public void onEvent(int eventType, int arg1 , int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话 id，当业务出错时将会话 id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话 id为null
            //if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //     String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //     Log.d(TAG, "session id =" + sid);
            //}
        }
    }

    private void startSpeechDialog() {
        //1. 创建RecognizerDialog对象
        RecognizerDialog mDialog = new RecognizerDialog(this, new MyInitListener()) ;
        //2. 设置accent、 language等参数
        mDialog.setParameter(SpeechConstant. LANGUAGE, "zh_cn" );// 设置中文
        mDialog.setParameter(SpeechConstant. ACCENT, "mandarin" );

        // 若要将UI控件用于语义理解，必须添加以下参数设置，设置之后 onResult回调返回将是语义理解
        // 结果
        // mDialog.setParameter("asr_sch", "1");
        // mDialog.setParameter("nlp_version", "2.0");
        //3.设置回调接口
        mDialog.setListener( new MyRecognizerDialogListener()) ;
        //4. 显示dialog，接收语音输入
        mDialog.show() ;
    }

    class MyRecognizerDialogListener implements RecognizerDialogListener {

        /**
         * @param results
         * @param isLast  是否说完了
         */
        @Override
        public void onResult(RecognizerResult results, boolean isLast) {

            String result = results.getResultString(); //未解析的
          //  showTip(result) ;
            Log.e(TAG, "没有解析的："+result);

            text = JsonParser.parseIatResult(result) ;//解析过后的text

            text = text.replaceAll("\\p{P}", "a");//把解析后的text中所有标点符号换成a


            String sn = null;
            // 读取json结果中的 sn字段
            try {
                JSONObject resultJson = new JSONObject(results.getResultString()) ;
                sn = resultJson.optString("sn" );
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mIatResults .put(sn, text) ;//没有得到一句，添加到

            StringBuffer resultBuffer = new StringBuffer();
            for (String key : mIatResults.keySet()) {
                resultBuffer.append(mIatResults .get(key));
            }
            if (text.contains("a")){
                return;//如果text含有a,就退出这个text的操作，舍弃掉这个text
            } else if (text.contains("打开")){//打开app部分
                int num = text.indexOf("打开");
                appName = text.substring(num + 2, text.length());//截取打开后面的字符串
                Log.e("appName", appName);
                openAppByName();
                addData(text, Chat.TYPE_SENT,getCurrentTime());//装配语音文字到发送文本框
                saveSendData();//保存发送的数据

            }else{
                request(resultBuffer.toString());//语音转文字过后发送图灵问答请求
                addData(text, Chat.TYPE_SENT,getCurrentTime());//装配语音文字到发送文本框
                saveSendData();//保存发送的数据
            }


        }

        @Override
        public void onError(SpeechError speechError) {

        }
    }

    class MyInitListener implements InitListener {

        @Override
        public void onInit(int code) {
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败 ");
            }

        }
    }

    /**
     * 语音识别
     */
    private void startSpeech() {
        //1. 创建SpeechRecognizer对象，第二个参数： 本地识别时传 InitListener
        SpeechRecognizer mIat = SpeechRecognizer.createRecognizer( this, null); //语音识别器
        //2. 设置听写参数，详见《 MSC Reference Manual》 SpeechConstant类
        mIat.setParameter(SpeechConstant. DOMAIN, "iat" );// 短信和日常用语： iat (默认)
        mIat.setParameter(SpeechConstant. LANGUAGE, "zh_cn" );// 设置中文
        mIat.setParameter(SpeechConstant. ACCENT, "mandarin" );// 设置普通话
        mIat.setParameter(SpeechConstant.ASR_PTT, "0");
        mIat.setParameter(SpeechConstant.ASR_PTT, "1");
        //3. 开始听写
        mIat.startListening( mRecoListener);
    }


    // 听写监听器
    private RecognizerListener mRecoListener = new RecognizerListener() {

        // 听写结果回调接口 (返回Json 格式结果，用户可参见附录 13.1)；
//一般情况下会通过onResults接口多次返回结果，完整的识别内容是多次结果的累加；
//关于解析Json的代码可参见 Demo中JsonParser 类；
//isLast等于true 时会话结束。
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.e (TAG, results.getResultString());
            System.out.println(results.getResultString()) ;
           // showTip(results.getResultString()) ;
        }

        // 会话发生错误回调接口
        public void onError(SpeechError error) {
           showTip(error.getPlainDescription(true)) ;
            // 获取错误码描述
            Log. e(TAG, "error.getPlainDescription(true)==" + error.getPlainDescription(true ));
        }

        // 开始录音
        public void onBeginOfSpeech() {
           //showTip(" 开始录音 ");
        }

        //volume 音量值0~30， data音频数据
        public void onVolumeChanged(int volume, byte[] data) {
           // showTip(" 声音改变了 ");
        }

        // 结束录音
        public void onEndOfSpeech() {
          //  showTip(" 结束录音 ");
        }

        // 扩展用接口
        public void onEvent(int eventType, int arg1 , int arg2, Bundle obj) {
        }
    };

    private void showTip (String data) {
        Toast.makeText( this, data, Toast.LENGTH_SHORT).show() ;
    }
    /**
     * 用于打开应用
     */
    private void openAppByName(){
        OpenAppAction openAppAction = new OpenAppAction(MainActivity.this, appName);
        openAppAction.start();
    }

    /**
     * 数据库保存聊天记录方法
     */

    public void saveSendData(){//保存发送的数据
        ChatHistory chatHistory=new ChatHistory();
        chatHistory.setTime(getCurrentTime());
        chatHistory.setType(ChatHistory.TYPE_SENT);
        chatHistory.setChattext(text);//发送的text
        chatHistory.setUsername(username);
        chatHistory.save();

    }
    public void saveReceiveData(){//保存收到的数据
        ChatHistory chatHistory=new ChatHistory();
        chatHistory.setTime(getCurrentTime());
        chatHistory.setType(ChatHistory.TYPE_RECEIVED);
        chatHistory.setChattext(mText);//收到的mText
        chatHistory.setUsername(username);
        chatHistory.save();

    }

    //写入聊天记录
    public void initMsg(){

        List<ChatHistory> chatHistoryList= LitePal.where("username=?",username)
                                             .order("time asc").find(ChatHistory.class);//asc表升序

        for (ChatHistory chatHistory:chatHistoryList){
            if(chatHistory.getType()==1){//发送的消息
                Chat c1 = new Chat(chatHistory.getChattext(), ChatHistory.TYPE_SENT,chatHistory.getTime());
                list.add(c1);

            }
            if (chatHistory.getType()==0){//接收的消息
                Chat c2 = new Chat(chatHistory.getChattext(), ChatHistory.TYPE_RECEIVED,chatHistory.getTime());
                list.add(c2);
            }

        }

    }

}

