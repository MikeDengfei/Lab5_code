package com.example.a.myapplication;

import android.app.AlertDialog;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;

public class Goods_list extends AppCompatActivity {
    RecyclerView mRecyclerView;
    List<Map<String,Object>> listItems;         //存储商品列表
    List<Goods>data;                             //存储商品信息
    FloatingActionButton cut;
    FloatingActionButton change;
    ListView mListView;
    boolean flag;//商品列表和购物车的判断
    boolean flag1;
    CommonAdapter commonAdapter;
    List<Map<String,Object>> shopItems;         //存储购物车商品
    SimpleAdapter simpleAdapter;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_list);
        //初始化
        initial();
        initial_goods();
        initial_data();
        //发送广播
        Broadcast();
        EventBus.getDefault().register(this);
        /*- - - - - - - - - - - - - - - Recyclerview - - - - - - - - - - - - - - - - - -*/
        /*- - - - - - 按钮切换商品列表的显示方式 - - - - - - -- */
        final Context context=this;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flag1){
                    flag1=false;
                    change.setImageResource(R.mipmap.uni_col);
                    mRecyclerView.setLayoutManager(new GridLayoutManager(context,2));
                }
                else{
                    flag1=true;
                    change.setImageResource(R.mipmap.double_col);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                }
            }
        });
        /*- - - - - - - - - - - - - - - 定义适配器 - - - - - - - - - - - - - - - - - -*/
        commonAdapter=new CommonAdapter<Map<String,Object>>(this,R.layout.goods_list_item,listItems) {
            @Override
            /*- - - - - - -  重写convert()方法，显示信息 - - - - - - -*/
            protected void convert(ViewHolder holder,Map<String,Object>s) {
                TextView name=holder.getView(R.id.name);
                name.setText(s.get("name").toString());
                TextView first=holder.getView(R.id.First);
                first.setText(s.get("firstletter").toString());
            }
        };
        commonAdapter.setOnItemClickListener(new CommonAdapter.OnItemClickListener() {
            /* - - - - - - - - - 单击商品跳转到商品详情- - - - - -*/
            @Override
            public void onClick(int position) {
                Intent intent1=new Intent(Goods_list.this,GoodsInfo.class);
                Bundle bundle=new Bundle();
                //得到商品在data中的下标
                int i=Integer.parseInt(listItems.get(position).get("index").toString());
                bundle.putSerializable("goods",data.get(i));
                intent1.putExtras(bundle);
                startActivityForResult(intent1,1);
            }
            /*- - - - - - -  - - - 长按商品删除 - - - - - -- - - - -*/
            @Override
            public void onLongClick(int position) {
//                commonAdapter.Remove(position);
//                Toast.makeText(getApplicationContext(),"移除第"+position+"个商品", Toast.LENGTH_SHORT).show();
            }
        });
        /*- - - - - - - - - - - -为商品列表设置动画 - - - - - - - - - - -*/
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        ScaleInAnimationAdapter animationAdapter=new ScaleInAnimationAdapter(commonAdapter);
        animationAdapter.setDuration(1000);
        mRecyclerView.setAdapter(animationAdapter);
        /*- - - - - - - - - - 为商品列表设置触摸事件，定义一个ItemTouchHelper - - - - -  - - - - - -*/
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            //返回int表示是否监听该方向
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.UP|ItemTouchHelper.DOWN;//拖拽
                int swipeFlags = ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT;//侧滑删除
                return makeMovementFlags(dragFlags,swipeFlags);
            }
            //上下拖动事件
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Collections.swap(listItems,viewHolder.getAdapterPosition(),target.getAdapterPosition());
                commonAdapter.notifyItemMoved(viewHolder.getAdapterPosition(),target.getAdapterPosition());
                return true;
            }
            //侧滑删除事件
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                listItems.remove(viewHolder.getAdapterPosition());
                commonAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
            }
            //是否可拖拽
            @Override
            public boolean isLongPressDragEnabled() {
                return true;
            }
        });
        helper.attachToRecyclerView(mRecyclerView);//触摸事件绑定mRecyclerView

         /*- - - - - - - - - - ListView购物车列表 - - - - -  - - - - - -*/
        simpleAdapter=new SimpleAdapter(this,shopItems,R.layout.goods_list_item,
                new String[]{"name","firstletter","price"},new int[]{R.id.name,R.id.First,R.id.price});
        mListView.setAdapter(simpleAdapter);
        /*- - - - - - - - -设置点击列表项事件监听器- -  - - - - - -*/
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if(position>0){//第一个按了没反应
                Intent intent1=new Intent(Goods_list.this,GoodsInfo.class);
                Bundle bundle=new Bundle();
                int i=Integer.parseInt(shopItems.get(position).get("index").toString());//得到商品在data中的id号
                bundle.putSerializable("goods",data.get(i));//传入商品的详细信息
                intent1.putExtras(bundle);
                startActivityForResult(intent1,1);//跳转到新的Acitivity，并设置请求码为1
                }
            }
        });
        /*- - - - - - - - -设置长按列表项事件监听器- -  - - - - - -*/
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                if(i>0) {//第一个按了没反应
                    Vibrator vib = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
                    vib.vibrate(10);//设置振动效果
                    //弹出对话框
                    AlertDialog.Builder alterDialog = new AlertDialog.Builder(Goods_list.this);
                    alterDialog.setTitle("移除商品")
                            .setMessage("从购物车移除" + shopItems.get(i).get("name").toString() + "?")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            })
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int s) {
                                    shopItems.remove(i);
                                    simpleAdapter.notifyDataSetChanged();
                                }
                            })
                            .create()
                            .show();
                }
                return true;
            }
        });
        /*- - - - - - - -点击浮动按钮切换商品列表和购物车- - - - - - - - - - - -*/
        cut.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(flag){
                    change.setVisibility(View.INVISIBLE);
                    mListView.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.INVISIBLE);
                    cut.setImageResource(R.mipmap.mainpage);
                    flag=false;
                }
                else {
                    change.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mListView.setVisibility(View.INVISIBLE);
                    cut.setImageResource(R.mipmap.shoplist);
                    flag=true;
                }
            }
        });
    }
    /*- - - - - - - - - - - - - - - - - 商品信息- - - - - - - - - - - - -  - - - - - - */
    String[]Name=new String[]{"Enchated Forest","Aela Milk","Devondale Milk","Kindle Oasis",
                               "waitrose 早餐麦片","Mcvitie's 饼干","Ferrero Rocher","Maltesers",
                               "Lindt","Borggreve" };
    String[]First=new String[]{"E","A","D","K","w","M","F","M","L","B"};
    String[]Price=new String[]{"￥5.00","￥59.00","￥79.00","￥2300.00","￥179.00","￥14.90",
                                "￥132.59","￥141.13","￥139.43","￥28.90"};
    String[]Other=new String[]{"作者 Johanna Basford","产地 德国","产地 澳大利亚","版本 8G",
                                "重量 2Kg","产地 英国","重量 300g","重量 118g","重量 249g","重量 640g"};
    int []ImageId=new int[]{ R.mipmap.enchatedforest,R.mipmap.arla,R.mipmap.devondale,R.mipmap.kindle,
                               R.mipmap.waitrose,R.mipmap.mcvitie,R.mipmap.ferrero,R.mipmap.maltesers,
                               R.mipmap.lindt,R.mipmap.borggreve};
    /*- - - - - - - - - - - - - - - - - 商品信息- - - - - - - - - - - - -  - - - - - - */
    //初始化控件和变量
    public void initial(){
        mRecyclerView=(RecyclerView)findViewById(R.id.goods_list);
        listItems=new ArrayList<>();
        cut=(FloatingActionButton)findViewById(R.id.cut);
        flag=true;
        mListView=(ListView)findViewById(R.id.shop_list);
        data=new ArrayList<>();
        shopItems=new ArrayList<>();
        change=(FloatingActionButton)findViewById(R.id.change);
        flag1=true;
    }
    //初始化商品列表和购物车列表
    public void initial_goods() {
        for(int i=0;i<10;i++) {
            Map<String,Object> tem=new LinkedHashMap<>();
            tem.put("name",Name[i]);
            tem.put("firstletter",First[i]);
            tem.put("index",i);//商品在data中的下标
            listItems.add(tem);
        }
        Map<String,Object> tem=new LinkedHashMap<>();
        tem.put("name","购物车");
        tem.put("firstletter","*");
        tem.put("price","价格");
        shopItems.add(tem);
    }
    //初始化商品信息
    public void initial_data(){
        for(int i=0;i<10;i++){
            data.add(new Goods(Name[i],Price[i],Other[i],First[i],ImageId[i],i));
        }
    }
    //数据的回调
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent intent){
        if(requestCode==1){
            if(resultCode==1){
                Bundle extras=intent.getExtras();
                if(extras!=null){
                    Goods goods=(Goods)extras.get("shop");
//                    if(goods.getShop()==1) {//如果标记为已添加
//                        Map<String, Object> tem = new LinkedHashMap<>();
//                        tem.put("name", goods.getName());
//                        tem.put("firstletter", goods.getFirstletter());
//                        tem.put("price", goods.getPrice());
//                        tem.put("index", goods.getId());
//                        shopItems.add(tem);//添加到购物车
//                        simpleAdapter.notifyDataSetChanged();
//                    }
                    data.set(goods.getId(),goods);//更新商品信息，因为可能被收藏或取消收藏
                }
            }
        }
    }
    String STATICACTION="mystaticfilter";
    public void Broadcast(){
        Random random=new Random();
        int i=random.nextInt(10);
        Intent intent=new Intent(STATICACTION);
        Bundle bundle=new Bundle();
        bundle.putSerializable("goods",data.get(i));
        intent.putExtras(bundle);
        sendBroadcast(intent);
    }

    @Override
    protected  void onNewIntent(Intent intent){
        Bundle extras=intent.getExtras();
        if(extras!=null){
            if(extras.get("cut").equals("cut")){
                change.setVisibility(View.INVISIBLE);
                mListView.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.INVISIBLE);
                cut.setImageResource(R.mipmap.mainpage);
                flag=false;
            }
        }
    }
    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onMessageEvent(MessageEvent event){
        Goods goods=event.getgoods();
        Map<String,Object>tem=new LinkedHashMap<>();
        tem.put("name",goods.getName());
        tem.put("firstletter",goods.getFirstletter());
        tem.put("price",goods.getPrice());
        tem.put("index",goods.getId());
        shopItems.add(tem);
        simpleAdapter.notifyDataSetChanged();
    }
    @Override
    public void onBackPressed() {
        //将此任务转向后台
        moveTaskToBack(false);
    }
}
