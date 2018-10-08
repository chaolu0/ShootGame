package com.shxy.game.component;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class Player {

    public static final int MAX_HP = 500;

    public static final int ACTION_STAND_RIGHT = 1;
    public static final int ACTION_STAND_LEFT = 2;
    public static final int ACTION_RUN_RIGHT = 3;
    public static final int ACTION_RUN_LEFT = 4;
    public static final int ACTION_JUMP_RIGHT = 5;
    public static final int ACTION_JUMP_LEFT = 6;

    public static final int DIR_RIGHT = 1;
    public static final int DIR_LEFT = 2;

    public static int X_DEFAULT = 0;
    public static int Y_DEFAULT = 0;
    public static int Y_JUMP_MAX = 0;

    private String name;

    private int hp;

    private int gun;

    private int action = ACTION_STAND_RIGHT;

    private int x;
    private int y;

    private List<Bullet> bulletList = new ArrayList<>();

    public static final int MOVE_STAND = 0;
    public static final int MOVE_LEFT = 1;
    public static final int MOVE_RIGHT = 2;

    private int move = MOVE_STAND;

    public static final int MAX_LEFT_SHOOT_TIME = 6;

    private int leftShootTime = 0;

    private boolean isJump = false;

    private boolean isJumpMax = false;

    private int jumpStopCount = 0;

    //about draw

    private int indexLeg = 0;
    private int indexHead = 0;

    private int currentHeadDrawX = 0;
    private int currentHeadDrawY = 0;

    private Bitmap currentLegBitmap = null;
    private Bitmap currentHeadBitmap = null;

    private int drawCount = 0;

    public Player(String name, int hp) {
        this.name = name;
        this.hp = hp;
    }

    private void initPosition() {
        x = ViewManager.SCREEN_WIDTH * 15 / 100;
        y = ViewManager.SCREEN_HEIGHT * 75 / 100;

        X_DEFAULT = x;
        Y_DEFAULT = y;

        Y_JUMP_MAX = ViewManager.SCREEN_HEIGHT / 2;
    }

    private int getDir() {
        return action % 2 == 0 ? DIR_LEFT : DIR_RIGHT;
    }

    public int getShift() {
        if (x <= 0 || y <= 0)
            initPosition();
        return X_DEFAULT - x;
    }

    public boolean isDie() {
        return hp <= 0;
    }

    public List<Bullet> getBulletList() {
        return bulletList;
    }

    public void draw(Canvas canvas) {
        if (canvas == null)
            return;

        switch (action) {
            case ACTION_STAND_LEFT:
                drawAni(canvas, ViewManager.legStandImage, ViewManager.headStandImage, DIR_LEFT);
                break;
            case ACTION_STAND_RIGHT:
                drawAni(canvas, ViewManager.legStandImage, ViewManager.headStandImage, DIR_RIGHT);
                break;
            case ACTION_RUN_LEFT:
                drawAni(canvas, ViewManager.legRunImage, ViewManager.headRunImage, DIR_LEFT);
                break;
            case ACTION_RUN_RIGHT:
                drawAni(canvas, ViewManager.legRunImage, ViewManager.headRunImage, DIR_RIGHT);
                break;
            case ACTION_JUMP_LEFT:
                drawAni(canvas, ViewManager.legJumpImage, ViewManager.headJumpImage, DIR_LEFT);
                break;
            case ACTION_JUMP_RIGHT:
                drawAni(canvas, ViewManager.legJumpImage, ViewManager.headJumpImage, DIR_RIGHT);
                break;
        }
    }

    private void drawAni(Canvas canvas, Bitmap[] legArr, Bitmap[] headArrFrom, int dir) {
        if (canvas == null || legArr == null)
            return;

        Bitmap[] headArr = headArrFrom;

        if (leftShootTime > 0) {
            headArr = ViewManager.headShootImage;
            leftShootTime--;
        }

        if (headArr == null)
            return;

        indexLeg = indexLeg % legArr.length;
        indexHead = indexHead % headArr.length;

        int trans = dir == DIR_RIGHT ? Graphics.TRANS_MIRROR : Graphics.TRANS_NONE;

        Bitmap bitmap = legArr[indexLeg];
        if (bitmap == null || bitmap.isRecycled())
            return;

        //画脚
        int drawX = X_DEFAULT;
        int drawY = y - bitmap.getHeight();

        Graphics.drawMatrixImage(canvas, bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                trans, drawX, drawY, 0, Graphics.TIMES_SCALE);
        currentLegBitmap = bitmap;

        //画头
        Bitmap bitmap2 = headArr[indexHead];
        if (bitmap2 == null || bitmap2.isRecycled())
            return;
        /**
         * ????????????????????????
         */
        drawX = drawX - ((bitmap2.getWidth() - bitmap.getWidth()) >> 1);
        if (action == ACTION_STAND_LEFT) {
            drawX += (int) (6 * ViewManager.scale);
        }
        drawY = drawY - bitmap2.getHeight() + (int) (10 * ViewManager.scale);

        Graphics.drawMatrixImage(canvas, bitmap2, 0, 0, bitmap2.getWidth(), bitmap2.getHeight(),
                trans, drawX, drawY, 0, Graphics.TIMES_SCALE);

        currentHeadDrawX = drawX;
        currentHeadDrawY = drawY;
        currentHeadBitmap = bitmap2;

        drawCount++;

        if (drawCount >= 4) {
            drawCount = 0;
            indexLeg++;
            indexHead++;
        }

        drawBullet(canvas);

        drawHead(canvas);
    }

    private void drawHead(Canvas canvas) {
        if (ViewManager.head == null) {
            return;
        }
        // 画头像
        Graphics.drawMatrixImage(canvas, ViewManager.head, 0, 0,
                ViewManager.head.getWidth(), ViewManager.head.getHeight(),
                Graphics.TRANS_MIRROR, 0, 0, 0, Graphics.TIMES_SCALE);
        Paint p = new Paint();
        p.setTextSize(30);
        // 画名字
        Graphics.drawBorderString(canvas, 0xa33e11, 0xffde00, name,
                ViewManager.head.getWidth(), (int) (ViewManager.scale * 20), 3, p);
        // 画生命值
        Graphics.drawBorderString(canvas, 0x066a14, 0x91ff1d, "HP: " + hp,
                ViewManager.head.getWidth(), (int) (ViewManager.scale * 40), 3, p);

    }

    private void drawBullet(Canvas canvas) {
        List<Bullet> delList = new ArrayList<>();

        for (Bullet bullet : bulletList) {
            if (bullet == null)
                continue;
            if (bullet.getX() <= 0 || bullet.getX() >= ViewManager.SCREEN_WIDTH) {
                delList.add(bullet);
            }

        }
        Bitmap bitmap;
        bulletList.removeAll(delList);

        for (Bullet bullet : bulletList) {
            if (bullet == null)
                continue;
            bitmap = bullet.getBitmap();
            if (bitmap == null || bitmap.isRecycled())
                continue;
            bullet.move();
            int trans = bullet.getDir() == Player.DIR_LEFT ?
                    Graphics.TRANS_MIRROR : Graphics.TRANS_NONE;
            Graphics.drawMatrixImage(canvas, bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                    trans, bullet.getX(), bullet.getY(), 0, Graphics.TIMES_SCALE);
        }
    }


    boolean isHurt(int startX, int startY, int endX, int endY) {
        if (currentHeadBitmap == null || currentLegBitmap == null) {
            return false;
        }

        int playerStartX = currentHeadDrawX;
        int playerEndX = playerStartX + currentHeadBitmap.getWidth();
        int playerStartY = currentHeadDrawY;
        int playerEndY = playerStartY + currentHeadBitmap.getHeight()
                + currentLegBitmap.getHeight();

        return ((startX >= playerStartX && startX <= playerEndX) ||
                (endX >= playerStartX && endX <= playerEndX))
                && ((startY >= playerStartY && startY <= playerEndY) ||
                (endY >= playerStartY && endY <= playerEndY));
    }

    public void addBullet() {
        int bulletX = (int) (getDir() == DIR_RIGHT ? X_DEFAULT + ViewManager.scale * 50 :
                X_DEFAULT - ViewManager.scale * 50);

        Bullet bullet = new Bullet(Bullet.BULLET_TYPE_1, bulletX,
                (int) (y - ViewManager.scale * 60), getDir());
        bulletList.add(bullet);

        leftShootTime = MAX_LEFT_SHOOT_TIME;

        ViewManager.soundPool.play(ViewManager.soundMap.get(1), 1, 1, 0, 0, 1);
    }

    private void move() {
//        System.out.println("move = " + move);
        if (move == MOVE_RIGHT) {
            MonsterManager.updatePosition((int) (ViewManager.scale * 6));

            setX((int) (getX() + 6 * ViewManager.scale));
            if (!isJump) {
                setAction(ACTION_RUN_RIGHT);
            }
        } else if (move == MOVE_LEFT) {
            if (getX() - (int) (6 * ViewManager.scale) < Player.X_DEFAULT) {
                MonsterManager.updatePosition(-(getX() - Player.X_DEFAULT));
            } else {
                MonsterManager.updatePosition(-(int) (6 * ViewManager.scale));
            }
            setX(getX() - (int) (6 * ViewManager.scale));
            if (!isJump) {
                setAction(Player.ACTION_RUN_LEFT);
            }
        } else if (getAction() != Player.ACTION_JUMP_LEFT
                && getAction() != ACTION_JUMP_RIGHT) {
            if (!isJump) {
                setAction(Player.ACTION_STAND_RIGHT);
            }
        }

    }

    public void logic() {
        if (!isJump) {
            move();
            return;
        }
        if (!isJumpMax) {
//            System.out.println("!max dir = " + action);
            setAction(getDir() == DIR_RIGHT ? Player.ACTION_JUMP_RIGHT :
                    Player.ACTION_JUMP_LEFT);
            setY(getY() - (int) (8 * ViewManager.scale));
            setBulletYAccelate(-(int) (2 * ViewManager.scale));

            if (getY() <= Player.Y_JUMP_MAX) {
                isJumpMax = true;
            }
        } else {
//            System.out.println("max dir = " + action);
            jumpStopCount--;
            if (jumpStopCount <= 0) {
                setY(getY() + (int) (8 * ViewManager.scale));
                setBulletYAccelate((int) (2 * ViewManager.scale));

                if (getY() >= Player.Y_DEFAULT) {
                    setY(Player.Y_DEFAULT);
                    isJump = false;
                    isJumpMax = false;
                    setAction(Player.ACTION_STAND_RIGHT);
                } else {
                    setAction(getDir() == Player.DIR_RIGHT ? Player.ACTION_JUMP_RIGHT :
                            ACTION_JUMP_LEFT);
                }
            }
        }
        move();
    }

    private void setBulletYAccelate(int acc) {

        for (Bullet bullet : bulletList) {
            if (bullet == null || bullet.getyAccelate() != 0) {
                continue;
            }
            bullet.setyAccelate(acc);
        }
    }

    public void updateBulletShift(int shift) {
        for (Bullet bullet : bulletList) {
            if (bullet == null) {
                continue;
            }
            bullet.setX(bullet.getX() - shift);
        }
    }

    public int getMove() {
        return move;
    }

    public void setMove(int move) {
        this.move = move;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getX() {
        if (x <= 0 || y <= 0)
            initPosition();
        return x;
    }

    public void setX(int x) {
        this.x = x % (ViewManager.map.getWidth() + X_DEFAULT);
        if (this.x < X_DEFAULT) {
            this.x = X_DEFAULT;
        }
    }

    public int getY() {
        if (x <= 0 || y <= 0)
            initPosition();
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isJump() {
        return isJump;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
//        System.out.println("action is " + action);
        this.action = action;
    }


    public void setJump(boolean jump) {
        isJump = jump;
        jumpStopCount = 6;
    }

    public int getLeftShootTime() {
        return leftShootTime;
    }

    public void setLeftShootTime(int leftShootTime) {
        this.leftShootTime = leftShootTime;
    }
}
