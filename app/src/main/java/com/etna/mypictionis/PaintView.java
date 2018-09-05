package com.etna.mypictionis;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.ref.Reference;
import java.util.HashMap;
import java.util.Map;

public class PaintView extends View {
    private Bitmap bitmap;
    private Canvas canvas;
    private Path path;
    private Paint bitmapPaint;
    private Paint paint;
    private Context context;
    private AttributeSet attrs;
    private DatabaseReference reference;
    static public String roomName;
    private Map<String, Path> paths = new HashMap<String, Path>();
    public ChildEventListener mListener;

    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);

        path = new Path();
        bitmapPaint = new Paint(Paint.DITHER_FLAG);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(9);
        new android.os.Handler().postDelayed(
            new Runnable() {
                public void run() {
                    /*
                    reference = FirebaseDatabase.getInstance().getReference().child(roomName);
                    mListener = reference.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                            /*
                            String key = dataSnapshot.getKey();
                            // dataSnapshot.getChildren();
                            Log.d("KEY OF THE KEYKEY", key);
                            /*
                             * QUAND ON RECOIS UN PATH
                             *
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    */
                }
            },
300);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap, 0, 0, bitmapPaint);
        canvas.drawPath(path, paint);
    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    /*
    public class Position {
        public float x;
        public float y;

        public float quadX;
        public float quadY;
        public float quadX2;
        public float quadY2;

        public Position(float x, float y)
        {
            this.x = x;
            this.y = y;
        }

        public Position(float quadX, float quadY, float quadX2, float quadY2)
        {
            this.quadX = quadX;
            this.quadY = quadY;
            this.quadX2 = quadX2;
            this.quadY2 = quadY2;
        }
    }
    */

    private void touchStart(float x, float y) {
        Map<String, Object> map = new HashMap<String, Object>();
        String pos = Float.toString(x) + "/" + Float.toString(y);
        map.put("touchStart", pos);
        reference.updateChildren(map);
        mX = x;
        mY = y;
        /*
        path.reset();
        path.moveTo(x, y);
        */
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);

        Map<String, Object> map = new HashMap<String, Object>();


        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            // path.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            // mX, mY, (x + mX) / 2, (y + mY) / 2)
            float posX = (x + mX) / 2;
            float posY = (y + mY) / 2;

            String pos = Float.toString(mX) + "/" + Float.toString(mY) + "/" + Float.toString(posX) + "/" + Float.toString(posY);
            map.put("touchMove", pos);
            reference.updateChildren(map);

            mX = x;
            mY = y;
        }
    }

    private void touchUp() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("touchUp", Float.toString(mX) + "/" +  Float.toString(mY));
        reference.updateChildren(map);
        // path.lineTo(mX, mY);
        // canvas.drawPath(path, paint);
        // path.reset();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchStart(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touchUp();
                invalidate();
                break;
        }
        return true;
    }

    public void clearCanvas() {
        bitmap.eraseColor(Color.WHITE);
        invalidate();
        System.gc();
    }

    public void setColor(int color)
    {
        paint.setStrokeWidth(9);
        paint.setColor(color);
    }

    public void selectClearBrush()
    {
        paint.setStrokeWidth(50);
        paint.setColor(Color.WHITE);
    }
}
