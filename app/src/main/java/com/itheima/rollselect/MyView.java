package com.itheima.rollselect;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mylovehang on 17/5/12.
 */

public class MyView extends View {

    private int mHeight;
    private Paint mLinePaint;
    private int count = 7;
    private int mItemHeight = 130;
    private int mWidth;
    private Paint mDataPaint;
    private int selectedSize = 26;
    private int mTopY;
    private int mBottomY;
    private List<WheelItem> mDatas;
    private int mDownY;
    private OnIsSelectedListener mIsSelectedListener;
    private long mDownTime;


    public MyView(Context context) {
        this(context,null);
    }

    public MyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(Color.BLACK);

        mDataPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDataPaint.setColor(Color.BLACK);


    }

    public void addData(List<String> list) {
        mDatas = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            WheelItem item = new WheelItem();
            item.id = i;
            item.itemY = i * mItemHeight;
            item.desc = list.get(i);
            mDatas.add(item);
        }
        postInvalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = mItemHeight * count;
        setMeasuredDimension(mWidth,mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCenterLine(canvas);

        drawDatas(canvas);
    }

    private void drawCenterLine(Canvas canvas) {
        mTopY = mItemHeight * 3;
        mBottomY = mItemHeight * 4;
        canvas.drawLine(0, mTopY,mWidth, mTopY, mLinePaint);
        canvas.drawLine(0, mBottomY,mWidth, mBottomY, mLinePaint);
    }

    private void drawDatas(Canvas canvas){
        for (int i = 0; i < mDatas.size(); i++) {
            mDatas.get(i).drawDesc(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int y = (int) event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mDownY = (int) event.getY();
                mDownTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:
                actionMove(y - mDownY);
                break;
            case MotionEvent.ACTION_UP:
                long currentTime = System.currentTimeMillis();
                if(currentTime - mDownTime < 100 && y - mDownY > 100){
                    slowSlide(y - mDownY);
                }else {
                    actionUp(y - mDownY);
                }
                break;
        }
        return true;
    }

    private synchronized void slowSlide(final int a){
        new Thread(new Runnable() {
            @Override
            public void run() {
                int height = Math.abs(a);
                int distance = 0;
                while(distance < height){
                    SystemClock.sleep(10);
                    actionMove(a > 0 ? distance : (-1)*distance);
                    distance += height;
                }
                actionUp(a);
            }
        }).start();
    }

    private void actionMove(int a){
        for(WheelItem item : mDatas){
            item.offsetY = a;
        }
        postInvalidate();
    }

    private void actionUp(int a){
        if(isEdge(a)) return;

        resetOffset(a);

        for(WheelItem item: mDatas){
            if(item.isSelected()){

                if(mIsSelectedListener != null){
                    mIsSelectedListener.onIsSelected(item.id,item.desc);
                }
                int y = mTopY - item.itemY;
                if(y != 0){
                    actionMove(y);
                    resetOffset(y);
                }
                break;
            }
        }

        postInvalidate();
    }

    private void resetOffset(int a){
        for (WheelItem item : mDatas) {
            item.offsetY = 0;
            item.itemY += a;
        }
    }

    private boolean isEdge(int move){
        if(move > 0 && mDatas.get(0).itemY + move > mTopY){
            for (int i = 0; i < mDatas.size(); i++) {
                mDatas.get(i).itemY = mTopY + i * mItemHeight;
                mDatas.get(i).offsetY = 0;
            }
            if(mIsSelectedListener != null){
                mIsSelectedListener.onIsSelected(mDatas.get(0).id,mDatas.get(0).desc);
            }
            postInvalidate();
            return true;
        }else if(move < 0 && mDatas.get(mDatas.size() - 1).itemY + move < mTopY){
            for (int i = 0; i < mDatas.size(); i++) {
                mDatas.get(i).itemY = mTopY - (mDatas.size() - 1 - i) * mItemHeight;
                mDatas.get(i).offsetY = 0;
            }
            if(mIsSelectedListener != null){
                mIsSelectedListener.onIsSelected(mDatas.get(mDatas.size() - 1).id,mDatas.get(mDatas.size() - 1).desc);
            }
            postInvalidate();
            return true;
        }
        return false;
    }

    public class WheelItem {
        public int id;
        public int itemY;
        public int offsetY;
        public String desc;
        public int size;

        public boolean isVisible(){
            return (itemY + offsetY > 0 && (itemY + offsetY < mHeight));
        }

        public boolean isSelected(){
            if(offsetY + itemY + mItemHeight/2 > mTopY && offsetY + itemY + mItemHeight/2 < mBottomY){
                return true;
            }
            return false;
        }


        public void drawDesc(Canvas canvas) {
            if(!isVisible()){
                return;
            }

            if(isSelected()){
                mDataPaint.setTextSize(selectedSize);
                mDataPaint.setColor(Color.RED);
            }else{
                size = selectedSize - 2 *(Math.abs(itemY - mTopY))/mItemHeight;
                mDataPaint.setTextSize(size);
            }
            Rect bounds = new Rect();
            mDataPaint.getTextBounds(desc,0,desc.length(),bounds);
            int start = mWidth/2 - bounds.width()/2;
            int top = offsetY + itemY + mItemHeight/2 - size/2;
            canvas.drawText(desc,start,top,mDataPaint);
        }

    }

    public interface OnIsSelectedListener{
        void onIsSelected(int id,String desc);
    }

    public void setOnIsSelectedListener(OnIsSelectedListener listener){
        mIsSelectedListener = listener;
    }
}
