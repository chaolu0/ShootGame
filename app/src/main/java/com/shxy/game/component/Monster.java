package com.shxy.game.component;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

public class Monster {

    public static final int TYPE_MAN = 1;
    public static final int TYPE_FLY = 2;
    public static final int TYPE_BOMB = 3;

    private int type;

    private int startX;
    private int startY;

    private int endX;
    private int endY;

    private int x;
    private int y;


    private int drawIndex;
    private int drawCount;

    private boolean isDie = false;

    private int drawDieCount = Integer.MAX_VALUE;

    private List<Bullet> bulletList = new ArrayList<>();

    public Monster(int type) {
        this.type = type;

        if (type == TYPE_MAN || type == TYPE_BOMB) {
            y = Player.Y_DEFAULT;
        } else if (type == TYPE_FLY) {
            y = ViewManager.SCREEN_HEIGHT / 2 + Utils.rand((int) (ViewManager.scale * 100));
        }
        // |----|  -> |---m|m
        x = ViewManager.SCREEN_WIDTH - (ViewManager.SCREEN_WIDTH >> 2) + Utils.rand(ViewManager.SCREEN_WIDTH / 2);
    }

    public void draw(Canvas canvas) {
        if (canvas == null) {
            return;
        }

        switch (type) {
            case TYPE_FLY:
                drawAni(canvas, isDie ? ViewManager.flyDieImage : ViewManager.flyImage);
                break;
            case TYPE_BOMB:
                drawAni(canvas, isDie ? ViewManager.bomb2Image : ViewManager.bombImage);
                break;
            case TYPE_MAN:
                drawAni(canvas, isDie ? ViewManager.manDieImage : ViewManager.manImgae);
                break;
        }
    }

    private void drawAni(Canvas canvas, Bitmap[] bitmaps) {
        if (canvas == null || bitmaps == null)
            return;
        if (isDie && drawDieCount == Integer.MAX_VALUE) {
            drawDieCount = bitmaps.length;
        }
        drawIndex = drawIndex % bitmaps.length;

        Bitmap bitmap = bitmaps[drawIndex];

        int drawX = x;
        if (isDie) {
            if (type == TYPE_BOMB) {
                drawX = (int) (x - ViewManager.scale * 50);
            } else if (type == TYPE_MAN) {
                drawX = (int) (x + ViewManager.scale * 50);
            }
        }
        if (bitmap == null){
            System.err.println("isDie?  = " + isDie);
            System.err.println("Bitmaps size  = " + bitmaps.length);
            System.err.println("drawIndex  = " + drawIndex);
        }
        int drawY = y - bitmap.getHeight();

        Graphics.drawMatrixImage(canvas, bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), Graphics.TRANS_NONE, drawX, drawY, 0, Graphics.TIMES_SCALE);

        startX = drawX;
        startY = drawY;

        endX = startX + bitmap.getWidth();
        endY = startY + bitmap.getHeight();

        drawCount++;

        if (drawCount >= (type == TYPE_MAN ? 6 : 4))  // ③
        {
            // 如果怪物是人，只在第3帧才发射子弹
            if (type == TYPE_MAN && drawIndex == 2) {
                addBullet();
            }
            // 如果怪物是飞机，只在最后一帧才发射子弹
            if (type == TYPE_FLY && drawIndex == bitmaps.length - 1) {
                addBullet();
            }
            drawIndex++;  // ②
            drawCount = 0;  // ④
        }

        if (isDie) {
            drawDieCount--;
        }
        drawBullet(canvas);
    }

    public boolean isHurt(int x, int y) {
        return x >= startX && x <= endX && y >= startY && y <= endY;
    }

    private int getBulletType() {
        switch (type) {

        }
        return 0;
    }

    private void addBullet() {
        int bulletType = getBulletType();
        if (bulletType <= 0)
            return;

        int drawX = x;
        int drawY = (int) (y - ViewManager.scale * 60);

        if (type == TYPE_FLY) {
            drawY = (int) (y - ViewManager.scale * 30);
        }

        Bullet bullet = new Bullet(bulletType, drawX, drawY, Player.DIR_LEFT);

        bulletList.add(bullet);
    }

    public void updateShift(int shift) {
        x -= shift;
        //update bullet
        for (Bullet bullet : bulletList) {
            if (bullet != null)
                bullet.setX(bullet.getX() - shift);
        }
    }


    private void drawBullet(Canvas canvas) {
        List<Bullet> delList = new ArrayList<>();
        Bullet bullet = null;
        for (int i = 0; i < bulletList.size(); i++) {
            bullet = bulletList.get(i);
            if (bullet == null)
                continue;

            if (bullet.getX() <= 0 || bullet.getX() >= ViewManager.SCREEN_WIDTH) {
                delList.add(bullet);
            }
        }

        bulletList.removeAll(delList);

        Bitmap bitmap = null;

        for (int i = 0; i < bulletList.size(); i++) {
            bullet = bulletList.get(i);
            if (bullet == null || bullet.getBitmap() == null)
                continue;

            bullet.move();
            bitmap = bullet.getBitmap();
            Graphics.drawMatrixImage(canvas, bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), Graphics.TRANS_NONE, bullet.getX(), bullet.getY(), 0,
                    Graphics.TIMES_SCALE);
        }
    }

    private void checkHurt() {
        List<Bullet> hurtList = new ArrayList<>();
        Bullet bullet = null;
        for (int i = 0; i < bulletList.size(); i++) {
            bullet = bulletList.get(i);
            // do something
        }
    }

    void checkBullet() {
        List<Bullet> delList = new ArrayList<>();

        for (Bullet bullet : bulletList) {

            if (bullet == null || !bullet.isEffect())
                continue;

            if (GameView.player.isHurt(bullet.getX(), bullet.getY(), bullet.getX(), bullet.getY())) {
                bullet.setEffect(false);

                GameView.player.setHp(GameView.player.getHp() - 5);

                delList.add(bullet);
            }
        }
        bulletList.removeAll(delList);
    }

    public int getDrawCount() {
        return drawCount;
    }

    public void setDrawCount(int drawCount) {
        this.drawCount = drawCount;
    }

    public int getDrawDieCount() {
        return drawDieCount;
    }

    public void setDrawDieCount(int drawDieCount) {
        this.drawDieCount = drawDieCount;
    }

    public int getDrawIndex() {
        return drawIndex;
    }

    public void setDrawIndex(int drawIndex) {
        this.drawIndex = drawIndex;
    }

    public int getEndX() {
        return endX;
    }

    public void setEndX(int endX) {
        this.endX = endX;
    }

    public int getEndY() {
        return endY;
    }

    public void setEndY(int endY) {
        this.endY = endY;
    }

    public boolean isDie() {
        return isDie;
    }

    public void setDie(boolean die) {
        isDie = die;
    }

    public int getStartX() {
        return startX;
    }

    public void setStartX(int startX) {
        this.startX = startX;
    }

    public int getStartY() {
        return startY;
    }

    public void setStartY(int startY) {
        this.startY = startY;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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
}
