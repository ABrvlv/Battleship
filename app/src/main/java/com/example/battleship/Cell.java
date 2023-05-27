package com.example.battleship;

import java.util.ArrayList;

class Cell {
    private int x = 0;
    private int y = 0;
    private boolean isHit = false;
    private Ship ship = null;
    private int imageId = -1;
    private ArrayList<Ship> surroundingShips = new ArrayList<>();

    Cell (int x, int y) {
        this.x = x;
        this.y = y;
    }

    boolean hasShipAround() {
        return !surroundingShips.isEmpty();
    }

    int getX() {
        return x;
    }

    int getY() {
        return y;
    }

    boolean isHit() {
        return isHit;
    }

    Ship getShip() {
        return ship;
    }

    int getImageId() {
        return imageId;
    }

    boolean hasShip() {
        return ship != null;
    }

    boolean nearAShipOtherThan(Ship ship) {
        if(surroundingShips.size() > 1) {
            return true;
        }
        if(surroundingShips.isEmpty()) {
            return false;
        }
        return !surroundingShips.contains(ship);
    }

    void hit() {
        if(hasShip()) {
            imageId = ship.getImageId()+1;
        } else {
            imageId = R.drawable.cell_miss;
        }
        isHit = true;
    }

    void placeShip(Ship ship, int imageId) {
        this.ship = ship;
        this.imageId = imageId;
    }

    void setSurroundingShip(Ship ship) {
        surroundingShips.add(ship);
    }

    void removeShip(Ship ship) {
        this.ship = null;
        this.imageId = 0;
        surroundingShips.remove(ship);
    }
}
