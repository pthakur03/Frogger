package edu.frogger.game;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class Game extends SurfaceView implements SurfaceHolder.Callback {

    private GameThread thread;
    private Bitmap playerSprite;
    private Bitmap carSprite;
    private Bitmap racecarSprite;
    private Bitmap truckSprite;
    private final Paint spritePaint = new Paint();
    private final Paint textPaint = new Paint();
    private TileMap tileMap;
    private final String name;
    private final String difficulty;
    private final int character;
    private int framesPassed = 0;
    private long timeWhenHit = 0;
    private final ArrayList<Vehicle> vehicles = new ArrayList<>();
    private int screenWidth;
    private int screenHeight;
    private Player player;

    public Game(Context context, String name, String difficulty, int character) {
        super(context);
        this.name = name;
        this.difficulty = difficulty;
        this.character = character;
        getHolder().addCallback(this);
        thread = new GameThread(getHolder(), this);
        setFocusable(true);
        init();
    }

    private void init() {
        PointSystem.resetPoints();
        PointSystem.resetMaxPoints();

        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(50);

        screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;

        tileMap = new TileMap(screenWidth, screenHeight, this);

        if (character == 1) {
            playerSprite = BitmapFactory.decodeResource(getResources(), R.drawable.character1);
            playerSprite = Bitmap.createScaledBitmap(playerSprite, TileMap.getTileWidth(), TileMap
                    .getTileWidth(), false);

        } else if (character == 2) {
            playerSprite = BitmapFactory.decodeResource(getResources(), R.drawable.character2);
            playerSprite = Bitmap.createScaledBitmap(playerSprite, TileMap.getTileWidth(), TileMap
                    .getTileWidth(), false);
        } else {
            playerSprite = BitmapFactory.decodeResource(getResources(), R.drawable.character3);
            playerSprite = Bitmap.createScaledBitmap(playerSprite, TileMap.getTileWidth(), TileMap
                    .getTileWidth(), false);
        }

        carSprite = BitmapFactory.decodeResource(getResources(), R.drawable.car);
        carSprite = Bitmap.createScaledBitmap(carSprite, TileMap.getTileWidth(), TileMap
                .getTileWidth(), false);
        racecarSprite = BitmapFactory.decodeResource(getResources(), R.drawable.racecar);
        racecarSprite = Bitmap.createScaledBitmap(racecarSprite, TileMap.getTileWidth(), TileMap
                .getTileWidth(), false);
        racecarSprite = Util.flipBitmapX(racecarSprite);
        truckSprite = BitmapFactory.decodeResource(getResources(), R.drawable.truck);
        truckSprite = Bitmap.createScaledBitmap(truckSprite, TileMap.getTileWidth() * 2, TileMap
                .getTileWidth(), false);

        player = new Player(difficulty, character);
    }

    public Player getPlayer() {
        return player;
    }

    public void update() {
        // Spawns Racecars every 150 ms
        if (framesPassed % 150 == 0) {
            vehicles.add(new Racecar(-TileMap.getTileWidth(),
                    screenHeight - TileMap.getBorderHeight() - (9 * TileMap.getTileWidth())));
            vehicles.add(new Racecar(-TileMap.getTileWidth(),
                    screenHeight - TileMap.getBorderHeight() - (11 * TileMap.getTileWidth())));
        }

        // Spawns trucks every three seconds
        if (framesPassed % 180 == 0) {
            vehicles.add(new Truck(screenWidth + TileMap.getTileWidth(),
                    screenHeight - TileMap.getBorderHeight() - (3 * TileMap.getTileWidth())));
            vehicles.add(new Truck(screenWidth + TileMap.getTileWidth(),
                    screenHeight - TileMap.getBorderHeight() - (12 * TileMap.getTileWidth())));
        }

        // Spawns cars every 2 seconds
        if (framesPassed % 120 == 0) {
            vehicles.add(new Car(screenWidth + TileMap.getTileWidth(),
                    screenHeight - TileMap.getBorderHeight() - (2 * TileMap.getTileWidth())));
            vehicles.add(new Car(screenWidth + TileMap.getTileWidth(),
                    screenHeight - TileMap.getBorderHeight() - (10 * TileMap.getTileWidth())));
        }

        // Calculates time since last hit
        long timeSinceHit = System.currentTimeMillis() - timeWhenHit;


        // Moves each vehicle and checks for collision
        for (Vehicle vehicle : vehicles) {
            vehicle.move();
            if (Util.checkCollision(player.getPosX(), player.getPosY(), TileMap.getTileWidth(),
                    vehicle.getPosX(), vehicle.getPosY(),
                    vehicle.getWidth() * TileMap.getTileWidth())) {
                if (timeSinceHit > 1000) {
                    vehicle.collisionOccurred(player);
                    timeWhenHit = System.currentTimeMillis();
                }
            }
        }

        // Removes vehicles that are off screen
        for (int i = 0; i < vehicles.size(); i++) {
            if (vehicles.get(i).getPosX() < -TileMap.getTileWidth() * 3
                    || vehicles.get(i).getPosX() > screenWidth + TileMap.getTileWidth() * 3) {
                vehicles.remove(vehicles.get(i));
            }
        }

        // Adds points if height is less than before
        PointSystem.addPoints(player.getXIdx(), player.getYIdx());

        // Checks if player has lost all of their lives or if they have reached the end
        if ((PointSystem.getPoints() >= 370 || player.getLives() <= 0)) {
            endGame();
        }


        framesPassed++;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        // Paint tile map to screen
        tileMap.draw(canvas);

        // Paint text to screen
        canvas.drawText(String.valueOf(thread.getAvgFPS()), 50, 50, textPaint);
        canvas.drawText("Name: " + name, 50, 100, textPaint);
        canvas.drawText("High Score: " + PointSystem.getMaxPoints(), 50, 150, textPaint);
        canvas.drawText("Score: " + PointSystem.getPoints(), 50, 200, textPaint);
        canvas.drawText("Difficulty: " + difficulty, 50, 250, textPaint);
        canvas.drawText("Lives: " + player.getLives(), 50, 300, textPaint);

        // Paint player sprite to screen
        canvas.drawBitmap(playerSprite, player.getPosX(), player.getPosY(), spritePaint);

        // Paint Vehicles to screen
        for (Vehicle vehicle : vehicles) {
            if (vehicle instanceof Car) {
                canvas.drawBitmap(carSprite, vehicle.getPosX(), vehicle.getPosY(), spritePaint);
            }
            if (vehicle instanceof Racecar) {
                canvas.drawBitmap(racecarSprite, vehicle.getPosX(), vehicle.getPosY(), spritePaint);
            }
            if (vehicle instanceof Truck) {
                canvas.drawBitmap(truckSprite, vehicle.getPosX(), vehicle.getPosY(), spritePaint);
            }
        }
    }

    public GameThread getThread() {
        return thread;
    }

    public void endGame() {
        int score = PointSystem.getMaxPoints();
        Intent intent = new Intent(getContext(), GameOver.class);
        intent.putExtra("score", Integer.toString(score));
        getContext().startActivity(intent);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        if (thread.getState().equals(Thread.State.TERMINATED)) {
            surfaceHolder = getHolder();
            surfaceHolder.addCallback(this);
            thread = new GameThread(surfaceHolder, this);
        }
        thread.startLoop();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int format,
                               int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        thread.stopLoop();
    }
}
