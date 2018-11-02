package com.ppandroid.openalipay;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.uuzuche.lib_zxing.activity.CodeUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText et_amount;
    private EditText et_beizhu;
    private Button btn_send;
    private TextView tv_log;
    private ListView listView;
    private void toast(String msg){
        if (!TextUtils.isEmpty(msg)){
            Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
        }
    }
    QrAdapter mQrAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et_amount=findViewById(R.id.et_amount);
        et_beizhu=findViewById(R.id.et_beizhu);
        btn_send=findViewById(R.id.btn_send);
        listView=findViewById(R.id.listView);
        mQrAdapter=new QrAdapter();
        listView.setAdapter(mQrAdapter);

        tv_log=findViewById(R.id.tv_log);
        setRecBroadcast();

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(et_amount.getText())||TextUtils.isEmpty(et_beizhu.getText())){
                    toast("金额和备注不能为空");
                    return;
                }
                String SETTING_CHANGED = "name.alipay.SETTING_CHANGED";
                Intent intent = new Intent(SETTING_CHANGED);
                intent.putExtra("isStart",true);
                intent.putExtra("defaultAmount",et_amount.getText().toString());
                intent.putExtra("defaultBeizhu",et_beizhu.getEditableText().toString());
                try {
                    sendBroadcast(intent);
                }catch (Exception e){
                    Log.d("yeqinfu","=================");
                    e.printStackTrace();
                }

            }
        });


    }

    List<String> qrList=new ArrayList<>();

    /**
     * 设置接收的广播
     */
    private void setRecBroadcast() {
        IntentFilter intentFilter = new IntentFilter();
        String SETTING_CHANGED = "name.alipay.receiver.all";
        intentFilter.addAction(SETTING_CHANGED);
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String msg=tv_log.getText().toString();
                String qrCodeUrl=intent.getStringExtra("qrCodeUrl");
                if (!TextUtils.isEmpty(qrCodeUrl)){
                    qrList.add(qrCodeUrl);
                    tv_log.setText(msg+"\n"+qrCodeUrl);
                    mQrAdapter.notifyDataSetChanged();
                }

            }
        },intentFilter);
    }

    class QrAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return qrList.size();
        }

        @Override
        public Object getItem(int position) {
            return qrList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view=null;
            if (convertView!=null){
                view=convertView;

            }else{
                view= LayoutInflater.from(MainActivity.this).inflate(R.layout.item_qr,null);
            }
            Button btn=view.findViewById(R.id.btn_generate);
            TextView qr_item=view.findViewById(R.id.tv_qr_code);
            qr_item.setText(qrList.get(position));
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                   showQrDialog(qrList.get(position));
                }
            });
            return view;
        }


    }
    PopupWindow popupWindow=null;
    private void showQrDialog(String s) {

        if (popupWindow!=null){
            popupWindow.dismiss();
            popupWindow=null;
        }
         popupWindow=new PopupWindow();
        View view=LayoutInflater.from(MainActivity.this).inflate(R.layout.show_qr_code,null);
        popupWindow.setContentView(view);
        ImageView imageView=view.findViewById(R.id.iv_show_qr);
        Bitmap mBitmap = CodeUtils.createImage(s, 400, 400, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        imageView.setImageBitmap(mBitmap);
        popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);

    }

}
