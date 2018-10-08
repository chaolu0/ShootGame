package com.shxy.game.component;

import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caolu on 2018/10/4.
 */
public class MonsterManager {
    public static final List<Monster> dieMonsterList = new ArrayList<>();
    public static final List<Monster> monsterList = new ArrayList<>();

    public static void generateMonster() {

        if (monsterList.size() < 3 + Utils.rand(3)) {
            Monster monster = new Monster(1 + Utils.rand(3));
            monsterList.add(monster);
        }
    }

    public static void updatePosition(int shift) {
        Monster monster = null;
        List<Monster> delList = new ArrayList<>();

        for (int i = 0; i < monsterList.size(); i++) {
            monster = monsterList.get(i);
            if (monster == null)
                continue;
            monster.updateShift(shift);
            if (monster.getX() < 0) {
                delList.add(monster);
            }
        }

        monsterList.removeAll(delList);
        delList.clear();

        for (int i = 0; i < dieMonsterList.size(); i++) {
            monster = dieMonsterList.get(i);

            if (monster == null)
                continue;
            monster.updateShift(shift);
            if (monster.getX() < 0)
                delList.add(monster);
        }
        dieMonsterList.removeAll(delList);
        GameView.player.updateBulletShift(shift);
    }

    public static void checkMonster() {
        List<Bullet> bulletList = GameView.player.getBulletList();
        if (bulletList == null) {
            bulletList = new ArrayList<>();
        }
        Monster monster = null;

        List<Monster> delList = new ArrayList<>();
        List<Bullet> delBulletList = new ArrayList<>();

        for (int i = 0; i < monsterList.size(); i++) {
            monster = monsterList.get(i);
            if (monster == null)
                continue;

            if (monster.getType() == Monster.TYPE_BOMB) {
                if (GameView.player.isHurt(monster.getStartX(), monster.getStartY(),
                        monster.getEndX(), monster.getEndY())) {
                    monster.setDie(true);
                    ViewManager.soundPool.play(ViewManager.soundMap.get(2),
                            1, 1, 0, 0, 1);
                    delList.add(monster);
                    GameView.player.setHp(GameView.player.getHp() - 10);
                }
                continue;
            }
            for (Bullet bullet : bulletList) {
                if (bullet == null || !bullet.isEffect())
                    continue;

                if (monster.isHurt(bullet.getX(), bullet.getY())) {
                    bullet.setEffect(false);
                    monster.setDie(true);
                    if (monster.getType() == Monster.TYPE_FLY) {
                        ViewManager.soundPool.play(ViewManager.soundMap.get(2),
                                1, 1, 0, 0, 1);
                    }
                    if (monster.getType() == Monster.TYPE_MAN) {
                        ViewManager.soundPool.play(
                                ViewManager.soundMap.get(3), 1, 1, 0, 0, 1);
                    }
                    delList.add(monster);
                    delBulletList.add(bullet);
                }
            }
            bulletList.removeAll(delBulletList);
            monster.checkBullet();
        }
        dieMonsterList.addAll(delList);
        monsterList.removeAll(delList);
    }

    public static void drawMonster(Canvas canvas) {
        Monster monster = null;

        for (int i = 0; i < monsterList.size(); i++) {
            monster = monsterList.get(i);
            if (monster == null)
                continue;
            monster.draw(canvas);
        }

        List<Monster> delList = new ArrayList<>();
        for (int i = 0; i < dieMonsterList.size(); i++) {
            monster = dieMonsterList.get(i);
            if (monster == null)
                continue;
            monster.draw(canvas);
            if (monster.getDrawDieCount() <= 0) {
                System.out.println("drawDieCount = " + monster.getDrawDieCount());
                delList.add(monster);
            }
        }
        dieMonsterList.removeAll(delList);
    }
}
