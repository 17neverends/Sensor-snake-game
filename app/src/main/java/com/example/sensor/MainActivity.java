package com.example.sensor;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

public class MainActivity extends Activity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private CustomDrawableView customDrawableView;

    private float appleX, appleY;
    private boolean appleEaten;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        customDrawableView = new CustomDrawableView(this);
        setContentView(customDrawableView);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        AppleView appleView = new AppleView(this);
        appleX = 300;
        appleY = 300;
        appleEaten = false;
        addContentView(appleView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float[] values = event.values;
            customDrawableView.setAcceleration(values[0], values[1]);
            if (!appleEaten) {
                checkCollision();
            }
        }
    }

    private void checkCollision() {
        float snakeX = customDrawableView.getXPosition();
        float snakeY = customDrawableView.getYPosition();
        float distance = (float) Math.sqrt(Math.pow(snakeX - appleX, 2) + Math.pow(snakeY - appleY, 2));
        if (distance < 60) {
            appleEaten = true;
            appleX = (float) Math.random() * customDrawableView.getWidth();
            appleY = (float) Math.random() * customDrawableView.getHeight();
        }
        if (appleEaten) {
            appleEaten = false;
            appleX = (float) Math.random() * customDrawableView.getWidth();
            appleY = (float) Math.random() * customDrawableView.getHeight();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    public static class CustomDrawableView extends View {
        private float xPosition, yPosition;
        private final Paint paint;

        public CustomDrawableView(Context context) {
            super(context);
            xPosition = 100;
            yPosition = 100;
            paint = new Paint();
            paint.setColor(Color.RED);
            paint.setStyle(Paint.Style.FILL);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawCircle(xPosition, yPosition, 30, paint);
            invalidate();
        }

        public void setAcceleration(float xAcceleration, float yAcceleration) {
            xPosition -= xAcceleration;
            yPosition += yAcceleration;
            if (xPosition < 0) xPosition = 0;
            if (xPosition > getWidth()) xPosition = getWidth();
            if (yPosition < 0) yPosition = 0;
            if (yPosition > getHeight()) yPosition = getHeight();
        }

        public float getXPosition() {
            return xPosition;
        }

        public float getYPosition() {
            return yPosition;
        }
    }

    public class AppleView extends View {
        private Paint paint;

        public AppleView(Context context) {
            super(context);
            paint = new Paint();
            paint.setColor(Color.GREEN);
            paint.setStyle(Paint.Style.FILL);
        }

        @Override
        protected void onDraw(@NonNull Canvas canvas) {
            if (!appleEaten) {
                canvas.drawCircle(appleX, appleY, 30, paint);
                invalidate();
            }
        }
    }
}
