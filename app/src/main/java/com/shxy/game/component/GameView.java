package com.shxy.game.component;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.shxy.game.MainActivity;
import com.shxy.game.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by caolu on 2018/10/3.
 */

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    public static Player player = new Player("shxy", Player.MAX_HP);
    private Context mainContext = null;

    private Paint paint = null;
    private Canvas canvas = null;

    private SurfaceHolder surfaceHolder;

    public static final int STAGE_NO_CHANGE = 0;
    public static final int STAGE_INIT = 1;
    public static final int STAGE_LOGIN = 2;
    public static final int STAGE_GAME = 3;
    public static final int STAGE_LOSE = 4;
    public static final int STAGE_QUIT = 99;
    public static final int STAGE_ERROR = 255;

    private int gStage = 0;

    public static final List<Integer> stageList = Collections.synchronizedList(new ArrayList<Integer>());

    public GameView(Context context, int firstStage) {
        super(context);
        mainContext = context;
        paint = new Paint();
        paint.setAntiAlias(true);
        setKeepScreenOn(true);
        setFocusable(true);
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        ViewManager.initScreen(MainActivity.windowWidth
                , MainActivity.windowHeight);
        gStage = firstStage;
    }

    private static final int INIT = 1;
    private static final int LOGIC = 2;
    private static final int CLEAN = 3;
    private static final int PAINT = 4;

    public int doStage(int stage, int step) {
        int nextStage;
        switch (stage) {
            case STAGE_INIT:
                nextStage = doInit(step);
                break;
            case STAGE_LOGIN:
                nextStage = doLogin(step);
                break;
            case STAGE_GAME:
                nextStage = doGame(step);
                break;
            case STAGE_LOSE:
                nextStage = doLose(step);
                break;
            default:
                nextStage = STAGE_ERROR;
                break;
        }
        return nextStage;
    }
    public void stageLogic()
    {
        int newStage = doStage(gStage, LOGIC);
        if (newStage != STAGE_NO_CHANGE && newStage != gStage)
        {
            doStage(gStage, CLEAN); // 清除旧的场景
            gStage = newStage & 0xFF;
            doStage(gStage, INIT);
        }
        else if (stageList.size() > 0)
        {
            newStage = STAGE_NO_CHANGE;
            synchronized (stageList)
            {
                newStage = stageList.get(0);
                stageList.remove(0);
            }
            if (newStage == STAGE_NO_CHANGE)
            {
                return;
            }
            doStage(gStage, CLEAN);
            gStage = newStage & 0xFF;
            doStage(gStage, INIT);
        }
    }

    private int doInit(int step) {
        ViewManager.loadResouce();
        return STAGE_LOGIN;
    }

    public Handler setViewHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            RelativeLayout layout = (RelativeLayout) msg.obj;
            if (layout != null) {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                MainActivity.mainLayout.addView(layout, params);
            }
        }
    };

    public Handler delViewHandler = new Handler() {
        public void handleMessage(Message msg) {
            RelativeLayout layout = (RelativeLayout) msg.obj;
            if (layout != null) {
                MainActivity.mainLayout.removeView(layout);
            }
        }
    };

    RelativeLayout gameLayout = null;

    private static final int ID_LEFT = 900000;
    private static final int ID_FIRE = ID_LEFT + 1;

    private int doGame(int step) {
        switch (step) {
            case INIT:
                if (gameLayout == null) {
                    gameLayout = new RelativeLayout(mainContext);
                    Button button = new Button(mainContext);
                    button.setId(ID_LEFT);
                    // 设置按钮的背景图片
                    button.setBackground(getResources().getDrawable(R.drawable.left));
                    RelativeLayout.LayoutParams params = new RelativeLayout
                            .LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    params.setMargins((int) (ViewManager.scale * 20),
                            0, 0, (int) (ViewManager.scale * 10));
                    // 向游戏界面上添加向左的按钮
                    gameLayout.addView(button, params);
                    button.setOnTouchListener(new OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            switch (event.getAction()) {
                                case MotionEvent.ACTION_DOWN:
                                    player.setMove(Player.MOVE_LEFT);
                                    break;
                                case MotionEvent.ACTION_UP:
                                    player.setMove(Player.MOVE_STAND);
                                    break;
                                case MotionEvent.ACTION_MOVE:
                                    break;
                            }
                            return false;
                        }
                    });
                    button = new Button(mainContext);
                    // 设置按钮的背景图片
                    button.setBackground(getResources().getDrawable(R.drawable.right));
                    params = new RelativeLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.RIGHT_OF, ID_LEFT);
                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    params.setMargins((int) (ViewManager.scale * 20),
                            0, 0, (int) (ViewManager.scale * 10));
                    // 向游戏界面上添加向右的按钮
                    gameLayout.addView(button, params);
                    // 为按钮添加事件监听器
                    button.setOnTouchListener(new OnTouchListener() {
                        public boolean onTouch(View v, MotionEvent event) {
                            switch (event.getAction()) {
                                case MotionEvent.ACTION_DOWN:
                                    player.setMove(Player.MOVE_RIGHT);
                                    break;
                                case MotionEvent.ACTION_UP:
                                    player.setMove(Player.MOVE_STAND);
                                    break;
                                case MotionEvent.ACTION_MOVE:
                                    break;
                            }
                            return false;
                        }
                    });
                    button = new Button(mainContext);
                    button.setId(ID_FIRE);
                    // 设置按钮的背景图片
                    button.setBackground(getResources().getDrawable(R.drawable.fire));
                    params = new RelativeLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    params.setMargins(0, 0, (int) (ViewManager.scale * 20),
                            (int) (ViewManager.scale * 10));
                    // 向游戏界面上添加射击的按钮
                    gameLayout.addView(button, params);
                    // 为按钮添加事件监听器
                    button.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 当角色的leftShootTime为0时（上一枪发射结束），角色才能发射下一枪。
                            if (player.getLeftShootTime() <= 0) {
                                player.addBullet();
                            }
                        }
                    });
                    // 添加跳的按钮
                    button = new Button(mainContext);
                    // 设置按钮的背景图片
                    button.setBackground(getResources().getDrawable(R.drawable.jump));
                    params = new RelativeLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.LEFT_OF, ID_FIRE);
                    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    params.setMargins(0, 0, (int) (ViewManager.scale * 20),
                            (int) (ViewManager.scale * 10));
                    // 向游戏界面上添加跳的按钮
                    gameLayout.addView(button, params);
                    // 为按钮添加事件监听器
                    button.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            player.setJump(true);
                        }
                    });
                    setViewHandler.sendMessage(setViewHandler
                            .obtainMessage(0, gameLayout));  // ③
                }
                break;
            case LOGIC:
                MonsterManager.generateMonster();
                MonsterManager.checkMonster();
                player.logic();
                if (player.isDie()) {
                    stageList.add(STAGE_LOSE);
                }
                break;
            case CLEAN:
                if (gameLayout != null) {

                }
                break;
            case PAINT:
                ViewManager.clearScreen(canvas);
                ViewManager.drawGame(canvas);
                break;
        }
        return STAGE_NO_CHANGE;
    }

    private RelativeLayout loginView;
    public int doLogin(int step)
    {
        switch (step)
        {
            case INIT:
                // 初始化角色血量
                player.setHp(Player.MAX_HP);
                // 初始化登录界面
                if (loginView == null)
                {
                    loginView = new RelativeLayout(mainContext);
                    loginView.setBackgroundResource(R.drawable.game_back);
                    // 创建按钮
                    Button button = new Button(mainContext);
                    // 设置按钮的背景图片
                    button.setBackgroundResource(R.drawable.button_selector);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.CENTER_IN_PARENT);
                    // 添加按钮
                    loginView.addView(button, params);
                    button.setOnClickListener(new OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            // 将游戏场景的常量添加到stageList集合中
                            stageList.add(STAGE_GAME);
                        }
                    });
                    // 通过Handler通知主界面加载loginView组件
                    setViewHandler.sendMessage(setViewHandler
                            .obtainMessage(0, loginView));  // ①
                }
                break;
            case LOGIC:
                break;
            case CLEAN:
                // 清除登录界面
                if (loginView != null)
                {
                    // 通过Handler通知主界面删除loginView组件
                    delViewHandler.sendMessage(delViewHandler
                            .obtainMessage(0, loginView));  // ②
                    loginView = null;
                }
                break;
            case PAINT:
                break;
        }
        return STAGE_NO_CHANGE;
    }

    // 定义游戏失败界面
    private RelativeLayout loseView;
    public int doLose(int step)
    {
        switch (step)
        {
            case INIT:
                // 初始化失败界面
                if (loseView == null)
                {
                    // 创建失败界面
                    loseView = new RelativeLayout(mainContext);
                    loseView.setBackgroundResource(R.drawable.game_back);
                    Button button = new Button(mainContext);
                    button.setBackgroundResource(R.drawable.again);
                    RelativeLayout.LayoutParams params = new RelativeLayout
                            .LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.CENTER_IN_PARENT);
                    loseView.addView(button, params);
                    button.setOnClickListener(new OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            // 跳转到继续游戏的界面
                            stageList.add(STAGE_GAME);
                            // 让角色的生命值回到最大值
                            player.setHp(Player.MAX_HP);
                        }
                    });
                    setViewHandler.sendMessage(setViewHandler
                            .obtainMessage(0, loseView));
                }
                break;
            case LOGIC:
                break;
            case CLEAN:
                // 清除界面
                if (loseView != null)
                {
                    delViewHandler.sendMessage(delViewHandler
                            .obtainMessage(0, loseView));
                    loseView = null;
                }
                break;
            case PAINT:
                break;
        }
        return STAGE_NO_CHANGE;
    }

    public static final int SLEEP_TIME = 40;
    public static final int MIN_SLEEP = 5;

    class GameThread extends Thread{
        SurfaceHolder surfaceHolder = null;
        boolean needStop = false;

        public GameThread(SurfaceHolder surfaceHolder) {
            this.surfaceHolder = surfaceHolder;
        }

        @Override
        public void run() {
            long t1,t2;
            Looper.prepare();
            synchronized (surfaceHolder){
                while (gStage!=STAGE_QUIT && needStop == false){
                    try {
                        stageLogic();
                        t1 = System.currentTimeMillis();
                        canvas = surfaceHolder.lockCanvas();
                        if (canvas!=null){
                            doStage(gStage,PAINT);
                        }
                        t2 = System.currentTimeMillis();
                        int paintTime = (int) (t2-t1);
                        long millis = SLEEP_TIME - paintTime;
                        if (millis<MIN_SLEEP){
                            millis = MIN_SLEEP;
                        }
                        sleep(millis);
                    }catch (Exception e){
                        e.printStackTrace();
                    }finally {
                        if (canvas!=null){
                            surfaceHolder.unlockCanvasAndPost(canvas);
                        }
                    }
                }
                Looper.loop();
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private GameThread thread = null;

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        paint.setTextSize(15);
        if (thread!=null){
            thread.needStop = true;
        }
        thread = new GameThread(surfaceHolder);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
