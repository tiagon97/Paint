package com.example.paint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import android.graphics.PorterDuff.Mode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class PaintView extends View {

    public static int BRUSH_SIZE = 1;
    public static final int DEFAULT_COLOR = Color.RED;
    public static final int DEFAULT_BG_COLOR = Color.WHITE;
    private static final float TOUCH_TOLERANCE = 4;

    private float mX, mY, x,y;
    private Path mPath;
    private Paint mPaint;
    private int currentColor;
    private int backgroundColor = DEFAULT_BG_COLOR;
    private int strokeWidth;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private boolean emboss;
    private boolean blur;
    private boolean fill;
    private MaskFilter mEmboss;
    private MaskFilter mBlur;
    private Paint mBitmapPaint = new Paint(Paint.DITHER_FLAG);
    DrawOption drawOption;


    protected float mStartX;
    protected float mStartY;
    protected float right;
    protected float left;
    protected float top;
    protected float bottom;
    protected boolean isDrawing=false;

    private ArrayList<Draw> paths = new ArrayList<>();
    private ArrayList<Draw> undo = new ArrayList<>();

    public PaintView(Context context) {

        super(context, null);

    }

    public PaintView(Context context, AttributeSet attrs) {

        super(context, attrs);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(DEFAULT_COLOR);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setXfermode(null);
        mPaint.setAlpha(0xff);

        mEmboss=new EmbossMaskFilter(new float[] {1,1,1},0.4f,6,3.5f);
        mBlur=new BlurMaskFilter(5,BlurMaskFilter.Blur.NORMAL);

    }

    public void initialise (DisplayMetrics displayMetrics) {

        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

        currentColor = DEFAULT_COLOR;
        strokeWidth = BRUSH_SIZE;
        drawOption=new DrawOption();

    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.save();
        mCanvas.drawColor(backgroundColor);// WRONG
        for (Draw draw : paths) {
            mPaint.setColor(draw.color); // WRONG
            mPaint.setStrokeWidth(draw.strokeWidth);
            mPaint.setMaskFilter(null);

            if(draw.emboss)
                mPaint.setMaskFilter(mEmboss);
            else if(draw.blur)
                mPaint.setMaskFilter(mBlur);
            if(draw.fill)
                mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            else
                mPaint.setStyle(Paint.Style.STROKE);
            mCanvas.drawPath(draw.path, mPaint);
        }
        String drawOpt=drawOption.getDrawOpt();
        if(drawOpt=="LINE"){ onDrawLine(mCanvas);}
        if(drawOpt=="RECTANGLE"){ onDrawRectangle(mCanvas);}
        if(drawOpt=="SQUARE"){ onDrawRectangle(mCanvas);}
        if(drawOpt=="CIRCLE"){ onDrawCircle(mCanvas);}
        if(isDrawing==false){
            mCanvas.drawColor(backgroundColor);
            for (Draw draw : paths) {
                mPaint.setColor(draw.color); // WRONG
                mPaint.setStrokeWidth(draw.strokeWidth);
                mPaint.setMaskFilter(null);

                if(draw.emboss)
                    mPaint.setMaskFilter(mEmboss);
                else if(draw.blur)
                    mPaint.setMaskFilter(mBlur);
                if(draw.fill)
                    mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                else
                    mPaint.setStyle(Paint.Style.STROKE);

                mCanvas.drawPath(draw.path, mPaint);
            }
        }




        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.restore();

    }

    private void onDrawLine(Canvas canvas) {
        float dx = Math.abs(x - mStartX);
        float dy = Math.abs(y - mStartY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            canvas.drawLine(mStartX, mStartY, x, y, mPaint);
        }
    }
    private void onDrawCircle(Canvas canvas){
        canvas.drawCircle(mStartX, mStartY, calculateRadius(mStartX, mStartY, x, y), mPaint);
    }
    protected float calculateRadius(float x1, float y1, float x2, float y2) {

        return (float) Math.sqrt(
                Math.pow(x1 - x2, 2) +
                        Math.pow(y1 - y2, 2)
        );
    }
    private void onTouchEventCircle(MotionEvent event){
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isDrawing=true;
                mStartX=x;
                mStartY=y;
                touchStart(x,y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                isDrawing=false;
                touchUpCircle();
                invalidate();
                break;

        }
    }
    private void onDrawRectangle(Canvas canvas){
        if(drawOption.getDrawOpt()=="SQUARE"){adjustSquare(x,y);
        }
        right = mStartX > x ? mStartX : x;
        left = mStartX > x ? x : mStartX;
        bottom = mStartY > y ? mStartY : y;
        top = mStartY > y ? y : mStartY;
        canvas.drawRect(left, top , right, bottom, mPaint);
    }

    private void onTouchEventLine(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isDrawing=true;
                mStartX=x;
                mStartY=y;
                touchStart(x,y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = Math.abs(x - mStartX);
                float dy = Math.abs(y - mStartY);
                if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                    mX=x;
                    mY=y;
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                isDrawing=false;
                touchUp();
                invalidate();
                break;
        }
    }

    private void onTouchEventRectangle(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isDrawing = true;
                mStartX=x;
                mStartY=y;
                touchStart(x,y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                if(drawOption.getDrawOpt()=="SQUARE"){adjustSquare(x,y);
                }
                touchMoveRect(x,y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                isDrawing = false;
                if(drawOption.getDrawOpt()=="SQUARE"){adjustSquare(x,y);
                }
                touchUpRect();
                invalidate();
                break;
        }
        ;
    }

    public void normal(){
        emboss=false;
        blur=false;

    }

    public void blur(){
        emboss=false;
        blur=true;

    }

    public void emboss(){
        emboss=true;
        blur=false;

    }
    public void setStyle(){
        if(mPaint.getStyle()== Paint.Style.STROKE){
            fill=true;
        }else{
            fill=false;
        }
    }

    private void onTouchEventBrush(MotionEvent event){
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                touchStart(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touchUp();
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                invalidate();
                break;

        }
    }

    private void touchMoveRect(float x,float y){
        right = mStartX > x ? mStartX : x;
        left = mStartX > x ? x : mStartX;
        bottom = mStartY > y ? mStartY : y;
        top = mStartY > y ? y : mStartY;
    }

    private void touchUpRect(){
        mPath.addRect(left,top,right,bottom, Path.Direction.CW);
    }
    private void touchUpCircle(){
        mPath.addCircle(mX,mY,calculateRadius(mX,mY,x,y), Path.Direction.CCW);
    }
    private void touchStart (float x, float y) {
        mPath = new Path();
        Draw draw = new Draw(currentColor, strokeWidth, mPath,emboss,blur,fill);
        paths.add(draw);
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touchMove (float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void touchUp () {
        mPath.lineTo(mX, mY);
    }
    protected void adjustSquare(float fx, float fy) {
        float dX = Math.abs(mStartX - fx);
        float dY = Math.abs(mStartY - fy);

        float max = Math.max(dX, dY);

        x = mStartX - fx < 0 ? mStartX + max : mStartX - max;
        y = mStartY - fy < 0 ? mStartY + max : mStartY - max;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        x = event.getX();
        y = event.getY();
        String drawOpt=drawOption.getDrawOpt();
        switch (drawOpt) {
            case "LINE":
                onTouchEventLine(event);
                break;
            case "BRUSH":
                onTouchEventBrush(event);
                break;
            case "RECTANGLE":
                onTouchEventRectangle(event);
                break;
            case "SQUARE":
                onTouchEventRectangle(event);
                break;
            case "CIRCLE":
                onTouchEventCircle(event);
                break;
        }
        return true;
    }
    public void clear () {

        backgroundColor = DEFAULT_BG_COLOR;
        mCanvas.drawColor(backgroundColor);
        paths.clear();
        normal();
        invalidate();

    }

    public void setStrokeWidth (int width) {

        strokeWidth = width;

    }

    public void setColor (int color) {

        currentColor = color;

    }




    public void saveImage () {

        int count = 0;

        File sdDirectory = Environment.getExternalStorageDirectory();
        File subDirectory = new File(sdDirectory.toString() + "/Pictures/Paint");

        if (subDirectory.exists()) {

            File[] existing = subDirectory.listFiles();

            for (File file : existing) {

                if (file.getName().endsWith(".jpg") || file.getName().endsWith(".png")) {

                    count++;

                }

            }

        } else {

            subDirectory.mkdir();

        }

        if (subDirectory.exists()) {

            File image = new File(subDirectory, "/drawing_" + (count + 1) + ".png");
            FileOutputStream fileOutputStream;

            try {

                fileOutputStream = new FileOutputStream(image);

                mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);

                fileOutputStream.flush();
                fileOutputStream.close();

                Toast.makeText(getContext(), "Zapisano", Toast.LENGTH_LONG).show();

            } catch (FileNotFoundException e) {


            } catch (IOException e) {


            }

        }

    }
    public void undo () {

        if (paths.size() > 0) {

            undo.add(paths.remove(paths.size() - 1));
            invalidate();

        } else {

            Toast.makeText(getContext(), "Nie można nic cofnąć.", Toast.LENGTH_LONG).show();

        }

    }

    public void redo(){
        if (undo.size() > 0) {

            paths.add(undo.remove(undo.size() - 1));
            invalidate(); // add

        } else {

            Toast.makeText(getContext(), "Nie można przywrócić", Toast.LENGTH_LONG).show();

        }
    }

}