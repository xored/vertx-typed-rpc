package com.xored.vertx.typed.rpc;

import java.awt.Point;

/**
 * @author Konstantin Zaitsev
 */
public class TestObject {
    private String str;
    private int num;
    private Point p;

    public TestObject(String str, int num, int x, int y) {
        this.str = str;
        this.num = num;
        this.p = new Point(x, y);
    }

    public String getStr() {
        return str;
    }

    public int getNum() {
        return num;
    }

    public Point getP() {
        return p;
    }
}
