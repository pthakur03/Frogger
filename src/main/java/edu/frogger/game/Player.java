package edu.frogger.game;


public class Player {
    private int posX;
    private int posY;
    private int xIdx;
    private int yIdx;
    private int lives;
    private String character;


    Player(String difficulty, int character) {
        this.posX = TileMap.getBorderWidth() + (5 * TileMap.getTileWidth());
        this.posY = TileMap.getBorderHeight() + (15 * TileMap.getTileWidth());
        this.xIdx = 5;
        this.yIdx = 15;

        if (difficulty.equals("Easy")) {
            this.lives = 3;
        } else if (difficulty.equals("Medium")) {
            this.lives = 2;
        } else {
            this.lives = 1;
        }

        if (character == 1) {
            this.character = "Billy";
        } else if (character == 2) {
            this.character = "Josh";
        } else {
            this.character = "Aurora";
        }
    }

    public void resetPos() {
        this.posX = TileMap.getBorderWidth() + (5 * TileMap.getTileWidth());
        this.posY = TileMap.getBorderHeight() + (15 * TileMap.getTileWidth());
        this.xIdx = 5;
        this.yIdx = 15;
        PointSystem.resetPoints();
    }

    public void setPosX(int posX) {
        if (posX < TileMap.getMapWidth() * TileMap.getTileWidth() && posX >= 0) {
            if (this.posX < posX) {
                this.xIdx++;
            } else {
                this.xIdx--;
            }
            this.posX = posX;
            checkBadTile();
        }
    };


    public void setPosY(int posY) {
        if (posY < TileMap.getMapHeight() * TileMap.getTileWidth() + TileMap.getBorderHeight()
                && posY >= TileMap.getBorderHeight()) {
            if (this.posY < posY) {
                this.yIdx++;
            } else {
                this.yIdx--;
            }
            this.posY = posY;
            checkBadTile();
        }
    }

    private void checkBadTile() {
        int tileValue = TileMap.getTile(xIdx, yIdx);
        if (tileValue == 2) {
            lives--;
            resetPos();
        }
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public int getLives() {
        return lives;
    }

    public int getXIdx() {
        return xIdx;
    }

    public int getYIdx() {
        return yIdx;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public String getCharacter() {
        return character;
    }

}
