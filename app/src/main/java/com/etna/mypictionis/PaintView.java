package com.etna.mypictionis;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

public class PaintView extends View {
    private Bitmap bitmap;
    private Canvas canvas;
    private Path path;
    private Paint bitmapPaint;
    private Paint paint;
    private DatabaseReference reference;
    static public String roomName;
    static public String roomOwner;
    static public String userName;
    public ChildEventListener mListener;
    String wordToFind;

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
                    reference = FirebaseDatabase.getInstance().getReference().child(roomName);
                    if (reference != null) {
                        mListener = reference.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                            }

                            @Override
                            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                String key = dataSnapshot.getKey();
                                String value = dataSnapshot.getValue().toString();

                                switch (key) {
                                    case "touchStart":
                                        float touchStartx = Float.parseFloat(value.split("/")[0]);
                                        float touchStarty = Float.parseFloat(value.split("/")[1]);
                                        path.reset();
                                        path.moveTo(touchStartx, touchStarty);
                                    break;

                                    case "touchMove":
                                        float touchMovemX = Float.parseFloat(value.split("/")[0]);
                                        float touchMovemY = Float.parseFloat(value.split("/")[1]);
                                        float touchMovex = Float.parseFloat(value.split("/")[2]);
                                        float touchMovey = Float.parseFloat(value.split("/")[3]);

                                        path.quadTo(touchMovemX, touchMovemY, (touchMovex + touchMovemX) / 2, (touchMovey + touchMovemY) / 2);
                                    break;

                                    case "touchUp":
                                        float touchUpmX = Float.parseFloat(value.split("/")[0]);
                                        float touchUpmY = Float.parseFloat(value.split("/")[1]);

                                        path.lineTo(touchUpmX, touchUpmY);
                                        canvas.drawPath(path, paint);
                                        path.reset();
                                    break;
                                    case "word_to_find":
                                        wordToFind = value;
                                        Log.d("VALUE TO FIND", value);
                                    break;
                                }

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
                    }
                }
            }
        , 900);
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

    private void touchStart(float x, float y) {
        Map<String, Object> map = new HashMap<String, Object>();
        String pos = Float.toString(x) + "/" + Float.toString(y);
        map.put("touchStart", pos);
        reference.updateChildren(map);
        mX = x;
        mY = y;
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);

        Map<String, Object> map = new HashMap<String, Object>();

        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
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
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        Log.d("OWNER", roomOwner);
        Log.d("USERNAME", userName);

        if (roomOwner.equals(userName) && wordToFind.length() > 0) {
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
