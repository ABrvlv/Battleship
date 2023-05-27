package com.example.battleship;


import java.util.ArrayList;


class Ship {
    private int size;
    private int id;
    private boolean mine;
    private boolean isHorizontal = true;
    private ArrayList<Cell> placedCells;
    private ArrayList<Cell> surroundingCells;
    private boolean isVisible = true;
    private boolean isPlaced = false;

    Ship(int size, int id, boolean mine) {
        this.size = size;
        this.id = id;
        this.mine = mine;
    }

    boolean isPlaced() {
        return isPlaced;
    }

    boolean isVisible() { return isVisible; }

    boolean isMine() {
        return mine;
    }

    void setInvisible() { this.isVisible = false; }

    int getSize() {
        return size;
    }

    int getId() {
        return id;
    }

    int getImageId() {
        int imageId;
        if (!isMine()) {
            imageId = R.drawable.ship_0;
        }
        else {
            imageId = R.drawable.mine_0;
        }

        return imageId;
    }

    boolean isHorizontal() {
        return isHorizontal;
    }

    boolean isSunk() {
        for(Cell cell : placedCells) {
            if(!cell.isHit()) {
                return false;
            }
        }
        return true;
    }

    void place(ArrayList<Cell> cellsToPlace, ArrayList<Cell> surroundingCells) {
        if(isPlaced) {
            remove();
        }
        this.placedCells = cellsToPlace;
        this.surroundingCells = surroundingCells;

        for(int i = 0; i < size; i++) {
            placedCells.get(i).placeShip(this, getImageId());
        }

        for(Cell cell : surroundingCells) {
            cell.setSurroundingShip(this);
        }
        isPlaced = true;
    }

    private void remove() {
        if(!isPlaced) return;
        for(Cell cell : placedCells) {
            cell.removeShip(this);
        }
        for(Cell cell : surroundingCells) {
            cell.removeShip(this);
        }
        isPlaced = false;
    }

    Cell rotate() {
        isHorizontal = !isHorizontal;
        Cell headCell = null;
        if(isPlaced) {
            headCell = placedCells.get(0);
            remove();
        }
        return headCell;
    }

    void sink() {
        for(Cell cell : surroundingCells) {
            cell.hit();
        }
        isVisible = true;
    }

    Cell getHeadCell() {
        return placedCells.get(0);
    }
}
