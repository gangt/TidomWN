package com.xiangpu.activity.usercenter.leftmenu;

/**
 * Created by Andi on 2017/12/1.
 */

public class LeftMenuBean {
    private int id;
    private String name;

    public LeftMenuBean() {

    }

    public LeftMenuBean(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ItemModel [id=" + id + ", name=" + name + "]";
    }

}
