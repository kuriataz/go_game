package com.zkwd;

public class Intersection {
    private final int x;
    private final int y;
    private int state;

    public Intersection(int x, int y, int state)
    {
        this.x = x;
        this.y = y;
        this.state = state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
