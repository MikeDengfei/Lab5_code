package com.example.a.myapplication;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a.myapplication.Goods;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.greenrobot.event.EventBus;

public class GoodsInfo extends AppCompatActivity {
    TextView name;
    TextView price;
    TextView information;
    ImageView collect;
    ImageView addlist;
    ImageView goods_pic;
    ListView operation;
    Goods goods;
    ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_info);
        //初始化
        initial();
        get_goods();
        /*-- - - - - - - -  -设置操作列表,屏幕最下方的那些-- - - - - - - -*/
        List<Map<String,Object>>list=new ArrayList<>();
        String[] operations=new String[]{"一键下单","分享商品","不感兴趣","查看更多商品促销信息"};
        for(int i=0;i<4;i++){
            Map<String,Object>tem=new LinkedHashMap<>();
            tem.put("opera",operations[i]);
            list.add(tem);
        }
        SimpleAdapter simpleAdapter=new SimpleAdapter(this,list,R.layout.operation_item,
                new String[]{"opera"},new int[]{R.id.opera});
        operation.setAdapter(simpleAdapter);

        //点击小星星
        collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(goods.getTag()==0) {
                    goods.setTag(1);
                    collect.setImageResource(R.mipmap.full_star);
                }
                else{
                    goods.setTag(0);
                    collect.setImageResource(R.mipmap.empty_star);
                }
            }
        });

        //点击添加购物
        addlist.setClickable(true);
        addlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goods.setShop(1);
                Toast.makeText(getApplicationContext(),"商品已加到购物车",Toast.LENGTH_SHORT).show();
                //动态注册广播
                BroadcastReceiver dynamic_receiver=new AppWidget();
                IntentFilter dynamic_filter=new IntentFilter();
                dynamic_filter.addAction("mydynamicfilter");
                registerReceiver(dynamic_receiver,dynamic_filter);
                //发送广播
                Intent intent=new Intent("mydynamicfilter");
                Bundle bundle=new Bundle();
                bundle.putSerializable("goods",goods);
                intent.putExtras(bundle);
                sendBroadcast(intent);
                //发布订阅事件
                EventBus.getDefault().post(new MessageEvent(goods));
            }
        });

        //点击返回，并传回商品的信息
        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent=new Intent(GoodsInfo.this,Goods_list.class);
                intent.putExtra("shop",goods);
                setResult(1,intent);
                finish();//返回到上一个activity
            }
        });
    }
    //初始化控件和变量
    public void initial(){
        name=(TextView)findViewById(R.id.name);
        price=(TextView)findViewById(R.id.price);
        information=(TextView)findViewById(R.id.information);
        collect=(ImageView)findViewById(R.id.collect);
        addlist=(ImageView)findViewById(R.id.AddList);
        back=(ImageView)findViewById(R.id.back);
        operation=(ListView)findViewById(R.id.operation);
        goods_pic=(ImageView)findViewById(R.id.goods_pic);
    }
    //得到商品的信息
    public void get_goods(){
        goods=(Goods)getIntent().getExtras().get("goods");
        goods.setShop(0);//初始化为未加入购物车
        if(goods!=null){
            name.setText(goods.getName());
            price.setText(goods.getPrice());
            information.setText(goods.getOther());
            goods_pic.setImageResource(goods.getImageid());
            if(goods.getTag()==0) collect.setImageResource(R.mipmap.empty_star);
            else collect.setImageResource(R.mipmap.full_star);
        }
    }
    //监听手机自带返回键
    @Override
    public boolean onKeyDown(int KeyCode, KeyEvent keyEvent){
        if(KeyCode==KeyEvent.KEYCODE_BACK){
            Intent intent=new Intent(GoodsInfo.this,Goods_list.class);
            intent.putExtra("shop",goods);
            setResult(1,intent);
            finish();
        }
        return true;
    }
}
