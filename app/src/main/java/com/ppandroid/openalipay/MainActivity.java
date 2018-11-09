package com.ppandroid.openalipay;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    boolean isSetAccount=false;
    Button btn_send_account;
    Button btn_key_start;
    Button btn_open_record;
    Button btn_open_qr_code;
    Button btn_open_set_money;
    EditText et_account;

    private void toast(String msg){
        if (!TextUtils.isEmpty(msg)){
            Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context context=this;


        btn_send_account=findViewById(R.id.btn_send_account);
        btn_key_start=findViewById(R.id.btn_key_start);
        btn_open_record=findViewById(R.id.btn_open_record);
        btn_open_qr_code=findViewById(R.id.btn_open_qr_code);
        btn_open_set_money=findViewById(R.id.btn_open_set_money);
        et_account=findViewById(R.id.et_account);
        btn_key_start.setOnClickListener(mOnClickListener);
        btn_open_record.setOnClickListener(mOnClickListener);
        btn_open_qr_code.setOnClickListener(mOnClickListener);
        btn_open_set_money.setOnClickListener(mOnClickListener);
        findViewById(R.id.btn_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String SETTING_CHANGED = "name.alipay.SETTING_CHANGED2";
                Intent intent = new Intent(SETTING_CHANGED);
                intent.putExtra("isNeedUpload",false);
                try {
                    sendBroadcast(intent);
                }catch (Exception e){
                    toast("发送广播有问题");
                    e.printStackTrace();
                }
                isSetAccount=true;
                toast("发送成功");
            }
        });


        btn_send_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(et_account.getText())){
                    toast("账户输入为空");
                    return;
                }
                String SETTING_CHANGED = "name.alipay.SETTING_CHANGED";
                Intent intent = new Intent(SETTING_CHANGED);
                intent.putExtra("et_account",et_account.getText().toString());
                try {
                    sendBroadcast(intent);
                }catch (Exception e){
                    toast("设置账户的时候出现了问题");
                    e.printStackTrace();
                }
                isSetAccount=true;
                toast("发送成功");

            }
        });




    }

    private String open_qr_code="am start -n com.eg.android.AlipayGphone/com.alipay.mobile.payee.ui.PayeeQRActivity";
    private String open_set_money="am start -n com.eg.android.AlipayGphone/com.alipay.mobile.payee.ui.PayeeQRSetMoneyActivity";
    private String open_record="am start -n com.eg.android.AlipayGphone/com.alipay.mobile.bill.list.ui.BillMainListActivity";
   private View.OnClickListener mOnClickListener=new View.OnClickListener() {
       @Override
       public void onClick(View v) {
           if (!isSetAccount){
               toast("打开后，要回来页面发送一下账号才开始执行轮询任务");
           }
           switch (v.getId()){
               case R.id.btn_key_start://一件开启
                   ShellUtils.upgradeRootPermission(getPackageCodePath());

                   break;
               case R.id.btn_open_qr_code://打开付款码页面
                   ShellUtils.execRootCmd(open_qr_code);
                   break;
               case R.id.btn_open_set_money:///打开付款码设置金额页面
                   ShellUtils.execRootCmd(open_set_money);
                   break;
               case R.id.btn_open_record://打开交易记录页面
                   ShellUtils.execRootCmd(open_record);

                   break;
           }
       }
   };



}
