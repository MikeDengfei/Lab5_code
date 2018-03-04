package com.example.a.myapplication;

/**
 * Created by a on 2017/10/28.
 */

public class MessageEvent {
    private Goods goods;
    public MessageEvent(Goods goods){
        this.goods=goods;
    }
    public Goods getgoods(){
        return goods;
    }
}
