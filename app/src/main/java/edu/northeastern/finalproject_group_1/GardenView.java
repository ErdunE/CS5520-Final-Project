package edu.northeastern.finalproject_group_1;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GardenView extends View {

    // Paints for different elements
    private Paint skyPaint;
    private Paint groundPaint;
    private Paint pathPaint;
    private Paint plantStemPaint;
    private Paint plantLeafPaint;
    private Paint flowerPaint;
    private Paint sunPaint;

    // Dimensions and positions
    private float groundLevel;
    private List<Plant> plants;
    private Random random;

    // Plant growth stages (0 = seed, 1 = sprout, 2 = young plant, 3 = mature plant, 4 = flowering)
    private static final int MAX_GROWTH_STAGE = 4;

    public GardenView(Context context) {
        super(context);
        init();
    }

    public GardenView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GardenView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // Initialize paints
        skyPaint = new Paint();
        skyPaint.setColor(Color.rgb(135, 206, 235)); // Sky blue

        groundPaint = new Paint();
        groundPaint.setColor(Color.rgb(139, 69, 19)); // Brown

        pathPaint = new Paint();
        pathPaint.setColor(Color.rgb(210, 180, 140)); // Tan
        pathPaint.setStyle(Paint.Style.FILL);

        plantStemPaint = new Paint();
        plantStemPaint.setColor(Color.rgb(34, 139, 34)); // Forest green
        plantStemPaint.setStrokeWidth(10f);
        plantStemPaint.setStyle(Paint.Style.STROKE);

        plantLeafPaint = new Paint();
        plantLeafPaint.setColor(Color.rgb(50, 205, 50)); // Lime green
        plantLeafPaint.setStyle(Paint.Style.FILL);

        flowerPaint = new Paint();
        flowerPaint.setStyle(Paint.Style.FILL);

        sunPaint = new Paint();
        sunPaint.setColor(Color.YELLOW);
        sunPaint.setStyle(Paint.Style.FILL);

        // Initialize plants list and random
        plants = new ArrayList<>();
        random = new Random();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        groundLevel = h * 0.7f; // Ground at 70% of the view height

        // Reset plants when size changes
        plants.clear();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw sky
        canvas.drawRect(0, 0, getWidth(), groundLevel, skyPaint);

        // Draw sun
        canvas.drawCircle(getWidth() * 0.8f, getHeight() * 0.15f, 60, sunPaint);

        // Draw ground
        canvas.drawRect(0, groundLevel, getWidth(), getHeight(), groundPaint);

        // Draw garden path
        Path path = new Path();
        path.moveTo(getWidth() * 0.1f, groundLevel);
        path.quadTo(getWidth() * 0.5f, groundLevel + 20, getWidth() * 0.9f, groundLevel);
        path.lineTo(getWidth() * 0.9f, getHeight());
        path.lineTo(getWidth() * 0.1f, getHeight());
        path.close();
        canvas.drawPath(path, pathPaint);

        // Draw all plants
        for (Plant plant : plants) {
            plant.draw(canvas);
        }
    }

    // Method to add a new plant - removed plant limit
    public void addPlant(String habitName) {
        // Get available width (from 10% to 90% of the garden width)
        float availableWidth = getWidth() * 0.8f;

        // Calculate a position with some randomness
        // Use modulo to create a wrapping effect for better distribution
        int plantCount = plants.size();
        float basePosition = (plantCount % 5) / 5.0f; // This cycles through 0.0, 0.2, 0.4, 0.6, 0.8

        // Add randomness within a small range around the base position
        float randomOffset = (random.nextFloat() - 0.5f) * 0.15f;
        float xPosition = getWidth() * (0.1f + basePosition + randomOffset);

        // Ensure position stays within bounds
        xPosition = Math.max(getWidth() * 0.1f, Math.min(xPosition, getWidth() * 0.9f));

        plants.add(new Plant(xPosition, groundLevel, habitName));
        invalidate(); // Redraw the view
    }
    // Method to grow a specific plant (increase its growth stage)
    public void growPlant(int plantIndex) {
        if (plantIndex >= 0 && plantIndex < plants.size()) {
            plants.get(plantIndex).grow();
            invalidate(); // Redraw the view
        }
    }

    // Method to grow a plant by its associated habit name
    public void growPlantByHabitName(String habitName) {
        for (int i = 0; i < plants.size(); i++) {
            if (plants.get(i).habitName.equals(habitName)) {
                plants.get(i).grow();
                invalidate(); // Redraw the view
                break;
            }
        }
    }

    // Method to get the number of plants
    public int getPlantCount() {
        return plants.size();
    }

    public void resetPlants() {
        plants = new ArrayList<>();
    }

    // Inner class to represent a plant
    private class Plant {
        private float x;
        private float y;
        private int growthStage;
        private String habitName;
        private int flowerColor;

        public Plant(float x, float y, String habitName) {
            this.x = x;
            this.y = y;
            this.growthStage = 0; // Every plant starts as a seed!
            this.habitName = habitName;

            // Random flower color
            int[] flowerColors = {
                    Color.RED, Color.MAGENTA, Color.rgb(255, 165, 0), // Orange
                    Color.BLUE, Color.rgb(148, 0, 211) // Purple
            };
            this.flowerColor = flowerColors[random.nextInt(flowerColors.length)];
        }

        public void grow() {
            if (growthStage < MAX_GROWTH_STAGE) {
                growthStage++;
            }
        }

        public void draw(Canvas canvas) {
            switch (growthStage) {
                case 0: // Seed
                    canvas.drawCircle(x, y - 5, 5, plantStemPaint);
                    break;

                case 1: // Sprout
                    // Small stem
                    canvas.drawLine(x, y, x, y - 20, plantStemPaint);

                    // Small leaves
                    Path leafPath = new Path();
                    leafPath.moveTo(x, y - 15);
                    leafPath.quadTo(x + 10, y - 20, x, y - 25);
                    leafPath.quadTo(x - 10, y - 20, x, y - 15);
                    canvas.drawPath(leafPath, plantLeafPaint);
                    break;

                case 2: // Young plant
                    // Medium stem
                    canvas.drawLine(x, y, x, y - 40, plantStemPaint);

                    // Medium leaves
                    drawLeaf(canvas, x, y - 20, 15, -20);
                    drawLeaf(canvas, x, y - 20, 15, 20);
                    drawLeaf(canvas, x, y - 35, 10, -15);
                    drawLeaf(canvas, x, y - 35, 10, 15);
                    break;

                case 3: // Mature plant
                    // Tall stem
                    canvas.drawLine(x, y, x, y - 70, plantStemPaint);

                    // Multiple leaves
                    drawLeaf(canvas, x, y - 20, 20, -30);
                    drawLeaf(canvas, x, y - 20, 20, 30);
                    drawLeaf(canvas, x, y - 40, 15, -25);
                    drawLeaf(canvas, x, y - 40, 15, 25);
                    drawLeaf(canvas, x, y - 60, 10, -20);
                    drawLeaf(canvas, x, y - 60, 10, 20);
                    break;

                case 4: // Flowering plant
                    // Tall stem
                    canvas.drawLine(x, y, x, y - 80, plantStemPaint);

                    // Multiple leaves
                    drawLeaf(canvas, x, y - 20, 20, -30);
                    drawLeaf(canvas, x, y - 20, 20, 30);
                    drawLeaf(canvas, x, y - 40, 18, -28);
                    drawLeaf(canvas, x, y - 40, 18, 28);
                    drawLeaf(canvas, x, y - 60, 15, -25);
                    drawLeaf(canvas, x, y - 60, 15, 25);

                    // Flower
                    flowerPaint.setColor(flowerColor);
                    for (int i = 0; i < 8; i++) {
                        float angle = (float) (i * Math.PI / 4);
                        float pX = x + (float) (Math.cos(angle) * 15);
                        float pY = (y - 80) + (float) (Math.sin(angle) * 15);

                        Path petalPath = new Path();
                        petalPath.moveTo(x, y - 80);
                        petalPath.quadTo(
                                x + (float) (Math.cos(angle) * 10),
                                (y - 80) + (float) (Math.sin(angle) * 10),
                                pX, pY
                        );
                        petalPath.quadTo(
                                x + (float) (Math.cos(angle) * 10),
                                (y - 80) + (float) (Math.sin(angle) * 10),
                                x, y - 80
                        );
                        canvas.drawPath(petalPath, flowerPaint);
                    }

                    // Flower center
                    flowerPaint.setColor(Color.YELLOW);
                    canvas.drawCircle(x, y - 80, 8, flowerPaint);
                    break;
            }
        }

        private void drawLeaf(Canvas canvas, float stemX, float stemY, float size, float angle) {
            // Save the current state of the canvas
            canvas.save();

            // Translate and rotate
            canvas.translate(stemX, stemY);
            canvas.rotate(angle);

            // Draw the leaf
            Path leafPath = new Path();
            leafPath.moveTo(0, 0);
            leafPath.quadTo(size, -size/2, size, 0);
            leafPath.quadTo(size, size/2, 0, 0);
            canvas.drawPath(leafPath, plantLeafPaint);

            // Restore the canvas to its previous state
            canvas.restore();
        }

    }
}