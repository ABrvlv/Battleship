package com.example.battleship;

import android.os.Handler;

import java.util.ArrayList;
import java.util.Random;

class ComPlayer extends Player {
    private Cell firstHit = null;
    private Cell nextHit = null;
    private ComPlayer com = this;
    private ArrayList<Integer> secondShootDir;
    private ShootResult lastShootResult = null;
    private boolean SkipTurn;


    ComPlayer(Board board, GamePlayActivity.GamePlay game) {
        super(board, game);
        board.placeShipRandomly();

        SkipTurn = false;
    }
    void randomlyShoot(final Board userBoard, final BoardView userBoardView) {
        game.disableBoard(com);
        if (SkipTurn) {
            SkipTurn = false;
            game.changeArrowDir();
            game.enableComBoard();
        }
        else {
            int delay;
            if (lastShootResult == ShootResult.HIT) {
                delay = 500;
            } else {
                delay = 800 + (int) (Math.random() * 500);
            }

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Random rand = new Random();
                    boolean shootSuccess = false;
                    while (!shootSuccess) {
                        int x = 0, y = 0;

                        if (firstHit == null) {
                            x = rand.nextInt(10);
                            y = rand.nextInt(10);
                            if (userBoard.getCell(x, y).isHit()) {
                                continue;
                            }
                        } else if (nextHit == null) {
                            boolean success = false;
                            while (!success) {
                                int randNum = rand.nextInt(secondShootDir.size());
                                int randDir = secondShootDir.get(randNum);
                                int firstX = firstHit.getX();
                                int firstY = firstHit.getY();
                                switch (randDir) {
                                    case 0:
                                        if (!userBoard.isOutOfBounds(firstX, firstY - 1) && !userBoard.getCell(firstX, firstY - 1).isHit()) {
                                            x = firstX;
                                            y = firstY - 1;
                                            break;
                                        } else {
                                            secondShootDir.remove(Integer.valueOf(randDir));
                                            continue;
                                        }
                                    case 1:
                                        if (!userBoard.isOutOfBounds(firstX, firstY + 1) && !userBoard.getCell(firstX, firstY + 1).isHit()) {
                                            x = firstX;
                                            y = firstY + 1;
                                            break;
                                        } else {
                                            secondShootDir.remove(Integer.valueOf(randDir));
                                            continue;
                                        }
                                    case 2:
                                        if (!userBoard.isOutOfBounds(firstX - 1, firstY) && !userBoard.getCell(firstX - 1, firstY).isHit()) {
                                            x = firstX - 1;
                                            y = firstY;
                                            break;
                                        } else {
                                            secondShootDir.remove(Integer.valueOf(randDir));
                                            continue;
                                        }
                                    case 3:
                                        if (!userBoard.isOutOfBounds(firstX + 1, firstY) && !userBoard.getCell(firstX + 1, firstY).isHit()) {
                                            x = firstX + 1;
                                            y = firstY;
                                            break;
                                        } else {
                                            secondShootDir.remove(Integer.valueOf(randDir));
                                            continue;
                                        }
                                    default:
                                        continue;
                                }
                                success = true;
                            }
                        } else {
                            int firstX = firstHit.getX();
                            int firstY = firstHit.getY();
                            int nextX = nextHit.getX();
                            int nextY = nextHit.getY();
                            if (nextY == firstY) {
                                if (nextX > firstX) {
                                    if (!userBoard.isOutOfBounds(nextX + 1, nextY) && !userBoard.getCell(nextX + 1, nextY).isHit()) {
                                        x = nextX + 1;
                                    } else {
                                        x = firstX - 1;
                                    }
                                    y = nextY;
                                } else {
                                    if (!userBoard.isOutOfBounds(nextX - 1, nextY) && !userBoard.getCell(nextX - 1, nextY).isHit()) {
                                        x = nextX - 1;
                                    } else {
                                        x = firstX + 1;
                                    }
                                    y = nextY;
                                }
                            }
                            if (nextX == firstX) {
                                if (nextY > firstY) {
                                    x = nextX;
                                    if (!userBoard.isOutOfBounds(nextX, nextY + 1) && !userBoard.getCell(nextX, nextY + 1).isHit()) {
                                        y = nextY + 1;
                                    } else {
                                        y = firstY - 1;
                                    }
                                } else {
                                    x = nextX;
                                    if (!userBoard.isOutOfBounds(nextX, nextY - 1) && !userBoard.getCell(nextX, nextY - 1).isHit()) {
                                        y = nextY - 1;
                                    } else {
                                        y = firstY + 1;
                                    }
                                }
                            }
                        }
                        ShootResult result = shoot(userBoard.getCell(x, y));
                        lastShootResult = result;
                        if (result == ShootResult.MINE) {
                            SkipTurn = true;
                            game.enableComBoard();
                            game.changeArrowDir();
                        } else if (result == ShootResult.MISS) {
                            game.enableComBoard();
                            game.changeArrowDir();
                        } else if (result == ShootResult.END) {
                            game.endGame(com);
                        } else if (result == ShootResult.KILL) {
                            firstHit = null;
                            nextHit = null;
                            randomlyShoot(userBoard, userBoardView);
                        } else {
                            if (firstHit == null) {
                                firstHit = userBoard.getCell(x, y);
                                secondShootDir = new ArrayList<>();
                                secondShootDir.add(0);
                                secondShootDir.add(1);
                                secondShootDir.add(2);
                                secondShootDir.add(3);
                            } else {
                                nextHit = userBoard.getCell(x, y);
                            }
                            randomlyShoot(userBoard, userBoardView);
                        }
                        shootSuccess = true;
                        userBoardView.invalidate();
                    }
                }
            }, delay);
        }


    }

}
