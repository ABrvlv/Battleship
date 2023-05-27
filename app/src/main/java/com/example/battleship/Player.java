package com.example.battleship;
enum ShootResult {
    HIT,
    MISS,
    KILL,
    END,
    MINE
}
    class Player {
    private Board board;
    GamePlayActivity.GamePlay game;
    private ShootResult lastShoot = null;

    Player(Board board, GamePlayActivity.GamePlay game) {
        this.board = board;
        this.game = game;
    }

    Board getBoard() {
        return board;
    }


    ShootResult shoot(Cell cellToShoot) {
        if(!cellToShoot.isHit()) {
            cellToShoot.hit();
            if(cellToShoot.hasShip()) {
                if(cellToShoot.getShip().isMine()) {
                    lastShoot = ShootResult.MINE;
                }
                else {
                    if(cellToShoot.getShip().isSunk()) {
                        cellToShoot.getShip().sink();
                        if(!cellToShoot.getShip().isMine()) {
                            board.incrNumOfShipsSunk();
                        }
                        if(board.areAllShipsSunk()) {
                            lastShoot = ShootResult.END;
                        } else {
                            lastShoot = ShootResult.KILL;
                        }
                    } else {
                        lastShoot = ShootResult.HIT;
                    }
            } }else {
                lastShoot = ShootResult.MISS;
            }
        } else {
            lastShoot = null;
        }
        return lastShoot;
    }

}
