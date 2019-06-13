package com.skyworthdigital.voice.iot;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by yokey on 2018/12/14.
 */

public class IoTCommand implements Serializable {
    @SerializedName("cmd")
    protected String cmd="remoteCmd";
    @SerializedName("cmdType")
    protected String cmdType="";
    @SerializedName("oper")
    protected String oper="";
    @SerializedName("uid")
    protected String uid="speech";
    @SerializedName("value")
    protected String value="";
    @SerializedName("location")
    protected String location="";

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getCmdType() {
        return cmdType;
    }

    public void setCmdType(String cmdType) {
        this.cmdType = cmdType;
    }

    public String getOper() {
        return oper;
    }

    public void setOper(String oper) {
        this.oper = oper;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLocation(){
        return location;
    }


    public String toJsonStr(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public void setLocation(String value){
        this.location=value;
    }

    public String parseCmdType(String name)
    {
        if(name.equals("空调"))
        {
            return "airCond";
        }else if(name.equals("灯")||name.equals("灯光"))
        {
            return "light";
        }else if(name.equals("窗帘"))
        {
            return "curtain";
        }else if (name.equals("插座")||name.equals("智能插座")||name.equals("开关")||name.equals("电视"))
        {
            return "slot";
        }else if (name.equals("浴缸"))
        {
            return "bathtub";
        }else if (name.equals("烟机")||name.equals("油烟机")||name.equals("抽油烟机"))
        {
            return "rangeHood";
        }else if (name.equals("空气净化仪")||name.equals("空气净化器")||name.equals("养生仪"))
        {
            return "airCleaner";
        }else if (name.equals("新风")) {
            return "ventilation";
        }else if (name.equals("床垫")){
            return "mattress";
        }else if (name.equals("远红外"))
        {
            return "farIr";
        }else if (name.equals("热水器")){
            return "waterHeater";
        }else if (name.equals("沙发"))
        {
            return "sofa";
        }else if (name.equals("场景"))
        {
            return "scene";
        }
        else
            return "";
    }

    //客厅，卧室，餐厅，厨房，洗⼿间，⽞关，展⽰间
    //LivingRoom/BedRoom/DiningRoom/KitchenRoom/WashRoom/Porch/ShowRoom
    public String parseLocation(String name)
    {
        if(name.equals("客厅"))
        {
            return "LivingRoom";
        }else if (name.equals("卧室"))
        {
            return "BedRoom";
        }else if (name.equals("餐厅"))
        {
            return "DiningRoom";
        }else if (name.equals("厨房"))
        {
            return "KitchenRoom";
        }else if (name.equals("洗手间")||name.equals("卫生间"))
        {
            return "WashRoom";
        }else if (name.equals("玄关"))
        {
            return "Porch";
        }else if (name.equals("展示间"))
        {
            return "ShowRoom";
        }else
            return "";
    }

    public boolean isValid()
    {
        if(cmdType.isEmpty()||oper.isEmpty()||value.isEmpty())
            return false;
        else
            return true;
    }

}
