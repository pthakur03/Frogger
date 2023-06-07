package edu.frogger.game;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class SwipeListener implements View.OnTouchListener {
    //create gesture detector variable
    private GestureDetector gestureDetector;

    //create constructor
    SwipeListener(View view, Game game) {
        int threshold = 100;
        int velocityThreshold = 100;
        GestureDetector.SimpleOnGestureListener listener =
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDown(MotionEvent e) {
                        return super.onDown(e);
                    }

                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2,
                                           float velocityX, float velocityY) {
                        //get x and y difference
                        float xDiff = e2.getX() - e1.getX();
                        float yDiff = e2.getY() - e1.getY();

                        try {
                            if (Math.abs(xDiff) > Math.abs(yDiff)) {
                                if (Math.abs(xDiff) > threshold
                                        && Math.abs(velocityX) > velocityThreshold) {
                                    if (xDiff > 0) { //right
                                        int newPosX = game.getPlayer().getPosX()
                                                + TileMap.getTileWidth();
                                        game.getPlayer().setPosX(newPosX);

                                        System.out.println("right swipe");

                                    } else if (xDiff < 0) { //left
                                        int newPosX = game.getPlayer().getPosX()
                                                - TileMap.getTileWidth();
                                        game.getPlayer().setPosX(newPosX);
                                        System.out.println("left swipe");
                                    }
                                    return true;
                                }
                            } else {
                                if (Math.abs(yDiff) > threshold
                                        && Math.abs(velocityY) > velocityThreshold) {
                                    if (yDiff > 0) { //down
                                        int newPosY = game.getPlayer().getPosY()
                                                + TileMap.getTileWidth();
                                        game.getPlayer().setPosY(newPosY);
                                    } else if (yDiff < 0) { //up
                                        int newPosY = game.getPlayer().getPosY()
                                                - TileMap.getTileWidth();
                                        game.getPlayer().setPosY(newPosY);
                                    }
                                    return true;
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return false;
                    }
                };
        //init gesture

        gestureDetector = new GestureDetector(game.getContext(), listener);

        view.setOnTouchListener(this);



    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return gestureDetector.onTouchEvent(motionEvent);
    }
}