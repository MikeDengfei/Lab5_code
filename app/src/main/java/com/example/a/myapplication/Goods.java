package com.example.a.myapplication;
import  java.io.Serializable;
/**
 * Created by a on 2017/10/20.
 */

public class Goods implements Serializable {
    private String name;//商品名字
    private String price;//商品价格
    private String other;//商品的其他信息
    private int imageid;//商品对应的图片id
    private String firstletter;//商品的名字的首字母
    private int tag;//标记是否被收藏
    private int id;//商品编号
    private int shop;//是否添加到购物车
    public Goods(String Name,String Price,String Other,String FirstLetter,int ImageId,int Id){
        name=Name;
        price=Price;
        other=Other;
        firstletter=FirstLetter;
        imageid=ImageId;
        tag=0;
        id=Id;
        shop=0;
    }
    public int getId(){
        return id;
    }
    public String getName(){
        return name;
    }
    public String getPrice(){
        return price;
    }
    public String getOther(){
        return other;
    }
    public String getFirstletter(){
        return firstletter;
    }
    public int getImageid(){
        return imageid;
    }
    public int getTag(){
        return tag;
    }
    public int getShop(){return shop;}
    public void  setTag(int Tag){
        tag=Tag;
    }
    public void setShop(int Shop){shop=Shop;}
}
