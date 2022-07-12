/*
 * Copyright (C) 2016 SongNing. https://github.com/backkomyoung
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rockzhang.red2.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.rockzhang.red2.R;
import com.rockzhang.red2.app.Red2App;
import com.rockzhang.red2.log.VLog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A colorful circle view with text.
 *
 * @author SongNing
 */

public class PokerView extends View {
    private Paint mBitmapPaint;

    private int[] PokerBitmapIndex = new int[] {
            R.drawable.poker_0, R.drawable.poker_1, R.drawable.poker_2, R.drawable.poker_3,
            R.drawable.poker_4, R.drawable.poker_5, R.drawable.poker_6, R.drawable.poker_7,
            R.drawable.poker_8, R.drawable.poker_9, R.drawable.poker_10, R.drawable.poker_11,
            R.drawable.poker_12, R.drawable.poker_13, R.drawable.poker_14, R.drawable.poker_15,
            R.drawable.poker_16, R.drawable.poker_17, R.drawable.poker_18, R.drawable.poker_19,
            R.drawable.poker_20, R.drawable.poker_21, R.drawable.poker_22, R.drawable.poker_23,
            R.drawable.poker_24, R.drawable.poker_25, R.drawable.poker_26, R.drawable.poker_27,
            R.drawable.poker_28, R.drawable.poker_29, R.drawable.poker_30, R.drawable.poker_31,
            R.drawable.poker_32, R.drawable.poker_33, R.drawable.poker_34, R.drawable.poker_35,
            R.drawable.poker_36, R.drawable.poker_37, R.drawable.poker_38, R.drawable.poker_39,
            R.drawable.poker_40, R.drawable.poker_41, R.drawable.poker_42, R.drawable.poker_43,
            R.drawable.poker_44, R.drawable.poker_45, R.drawable.poker_46, R.drawable.poker_47,
            R.drawable.poker_48, R.drawable.poker_49, R.drawable.poker_50, R.drawable.poker_51
    };



    public static final int POKER_GAP_PX =  dp2px(Red2App.getInstance(), 24);
    private static int POKER_DRAW_WIDTH = 0;
    private static int mBitmapWidth = 0;
    private static int mBitmapHeight = 0;

    private final List<Integer> mWeSelectedPokerIndex = new ArrayList<>(24);

    private List<Integer> mDrawingPokers = new ArrayList<>(32);

    public PokerView(Context context) {
        this(context, null);
    }

    public PokerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PokerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mDrawingPokers.add(3);
        mDrawingPokers.add(4);
        mDrawingPokers.add(7);
        mDrawingPokers.add(13);
        mDrawingPokers.add(18);
        mDrawingPokers.add(24);
        mDrawingPokers.add(32);

        setBackgroundColor(0xFF777777);
        init();
    }

    private void init() {
        mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setDither(true);


    }

    public List<Integer> getDisplayCards() {
        return mDrawingPokers;
    }

    public void setDisplayCards(List<Integer> cards) {
        mDrawingPokers = cards;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = measureSpec(widthMeasureSpec);
        final int height = measureSpec(heightMeasureSpec);
        setMeasuredDimension(width, height);

        // super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private int measureSpec(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            result = Math.min(dp2px(Red2App.getInstance(), 60), specSize);
        } else {
            result = dp2px(Red2App.getInstance(), 60);
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();

        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();

        int width = getWidth() - paddingLeft - paddingRight;
        int height = getHeight() - paddingTop - paddingBottom;

        VLog.info("padding l " + paddingLeft + " r " + paddingRight + " t " + paddingTop + " b " + paddingBottom);

        VLog.info("View size " + getWidth() + ", " + getHeight());
        for (int i = 0; i < mDrawingPokers.size(); i++) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), PokerBitmapIndex[mDrawingPokers.get(i)]);

            if (POKER_DRAW_WIDTH == 0) {
                mBitmapWidth = bitmap.getWidth();
                mBitmapHeight = bitmap.getHeight();
                VLog.info("Picture size " + mBitmapWidth + ", " + mBitmapHeight);
                POKER_DRAW_WIDTH = (int) Math.round((1.0 * mBitmapWidth * height) / mBitmapHeight);
            }

            int drawStartX = paddingLeft + i * POKER_GAP_PX;

            Rect src = new Rect(0, 0, mBitmapWidth, mBitmapHeight);

            if (mWeSelectedPokerIndex.contains(i)) {
                paddingTop = 0;
            } else {
                paddingTop = getPaddingTop();
            }

            Rect dst = new Rect(drawStartX, paddingTop, POKER_DRAW_WIDTH + drawStartX, height + paddingTop);
            VLog.info("src " + src.toString() + "  dst " + dst.toString());
            canvas.drawBitmap(bitmap, src, dst, mBitmapPaint);
        }

        canvas.restore();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // mTotalWidth = w;
        // mTotalHeight = h;
    }

    public boolean onTouchEvent(MotionEvent event) {
        float fx = event.getX();
        float fy = event.getY();

        // VLog.info("We click PokerView " + fx + ", " + fy);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int x = Math.round(fx);
            int index = (x - getPaddingLeft())/ POKER_GAP_PX;

            if (index > (mDrawingPokers.size() - 1) && x < getPaddingLeft() + (index * POKER_GAP_PX) + mBitmapWidth)
                index = mDrawingPokers.size() - 1;

            VLog.info("We Clicked the poker index " + index);
            if (mWeSelectedPokerIndex.contains(index)) {
                mWeSelectedPokerIndex.remove(Integer.valueOf(index));
            } else {
                mWeSelectedPokerIndex.add(index);
            }
            invalidate();
        }

        return true;
    }

    private static int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    private static int sp2px(Context context, float sp) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * fontScale + 0.5f);
    }

}
