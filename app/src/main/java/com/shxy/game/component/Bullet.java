package com.shxy.game.component;


import android.graphics.Bitmap;

public class Bullet {

    public static final int BULLET_TYPE_1 = 1;
    public static final int BULLET_TYPE_2 = 2;
    public static final int BULLET_TYPE_3 = 3;
    public static final int BULLET_TYPE_4 = 4;

    private int type;

    private int dir;

    private int yAccelate = 0;

    private int x;
    private int y;

    private boolean isEffect = true;

    public Bullet(int bulletType, int drawX, int drawY, int dir) {
        this.type = bulletType;
        this.x = drawX;
        this.y = drawY;
        this.dir = dir;
    }

    public Bitmap getBitmap() {
        Bitmap bitmap = null;
        switch (type) {
            case BULLET_TYPE_1:
                bitmap = ViewManager.bulletImage[0];
                break;
            case BULLET_TYPE_2:
                bitmap = ViewManager.bulletImage[1];
                break;
            case BULLET_TYPE_3:
                bitmap = ViewManager.bulletImage[2];
                break;
            case BULLET_TYPE_4:
                bitmap = ViewManager.bulletImage[3];
                break;
        }
        return bitmap;
    }

    public int getSpeedX() {
        int sign = dir == Player.DIR_RIGHT ? 1 : 2;
        switch (type) {
            case BULLET_TYPE_1:
                return (int) (ViewManager.scale * 12 * sign);
            case BULLET_TYPE_2:
            case BULLET_TYPE_3:
            case BULLET_TYPE_4:
                return (int) (ViewManager.scale * 8 * sign);
            default:
                return (int) (ViewManager.scale * 8 * sign);
        }
    }

    public int getSpeedY() {
        if (yAccelate != 0)
            return yAccelate;
        switch (type) {
            case BULLET_TYPE_3:
                return (int) (ViewManager.scale * 6);
            case BULLET_TYPE_1:
            case BULLET_TYPE_2:
            case BULLET_TYPE_4:
            default:
                return 0;
        }
    }

    public void move() {
        x += getSpeedX();
        y += getSpeedY();
    }


    public boolean isEffect() {
        return isEffect;
    }

    public void setEffect(boolean effect) {
        isEffect = effect;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getDir() {
        return dir;
    }

    public void setDir(int dir) {
        this.dir = dir;
    }
}
