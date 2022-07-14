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
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.rockzhang.red2.R;
import com.rockzhang.red2.app.Red2App;
import com.rockzhang.red2.log.VLog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PokerView extends View {

    private static boolean sPokerBitmapInitialized = false;
    private static Bitmap[] PokerBitmap = new Bitmap[52];

    private static int[] PokerBitmapIndex = new int[] {
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

    private int mDrawCenterStartPos = 0;

    public static void LoadPokerBitmap(Context context) {
        if (sPokerBitmapInitialized)
            return;

        sPokerBitmapInitialized = true;

        for(int i = 0; i < PokerBitmapIndex.length; i++)
            PokerBitmap[i] = BitmapFactory.decodeResource(context.getResources(), PokerBitmapIndex[i]);
    }

    public static final int POKER_GAP_PX =  dp2px(Red2App.getInstance(), 24);
    private int POKER_DRAW_WIDTH = 0;

    private int mBitmapWidth = 0;
    private int mBitmapHeight = 0;

    private Paint mBitmapPaint;
    private Rect mPicRect;

    private final List<Integer> mWeSelectedPokerList = new ArrayList<>(24);

    private List<Integer> mDrawingPokers = new ArrayList<>(32);

    public PokerView(Context context) {
        this(context, null);
    }

    public PokerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PokerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        mDrawingPokers.add(3);
//        mDrawingPokers.add(4);
//        mDrawingPokers.add(7);
//        mDrawingPokers.add(13);
//        mDrawingPokers.add(18);
//        mDrawingPokers.add(24);
//        mDrawingPokers.add(32);
//        mDrawingPokers.add(48);
//        mDrawingPokers.add(48);
//        Collections.sort(mDrawingPokers,  Collections.reverseOrder());

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

    public List<Integer> getSelectedCards() {

        List<Integer> selectedList = new ArrayList<>(24);
        for (int i = 0; i < mDrawingPokers.size(); i++) {
            if (mWeSelectedPokerList.contains(i)) {
                selectedList.add(mDrawingPokers.get(i));
            }
        }

        VLog.info("We selected pokers " + Arrays.toString(selectedList.toArray()));

        return selectedList;
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

        if (mDrawingPokers.size() == 0)
            return;

        canvas.save();

        final int paddingLeft = getPaddingLeft();
        final int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();

        int width = getWidth() - paddingLeft - paddingRight;
        int height = getHeight() - paddingTop - paddingBottom;

        VLog.debug("padding l " + paddingLeft + " r " + paddingRight + " t " + paddingTop + " b " + paddingBottom);

        VLog.debug("View size " + getWidth() + ", " + getHeight());

        int totalUsedSpace = (mDrawingPokers.size() + 3) * POKER_GAP_PX;
        // for draw pokers in center.
        mDrawCenterStartPos = (width - totalUsedSpace) / 2;
        for (int i = 0; i < mDrawingPokers.size(); i++) {
            //BitmapFactory.decodeResource(getResources(), PokerBitmapIndex[mDrawingPokers.get(i)]);
            Bitmap bitmap = PokerBitmap[mDrawingPokers.get(i)];

            if (POKER_DRAW_WIDTH == 0) {
                mBitmapWidth = bitmap.getWidth();
                mBitmapHeight = bitmap.getHeight();
                VLog.debug("Picture size " + mBitmapWidth + ", " + mBitmapHeight);
                POKER_DRAW_WIDTH = (int) Math.round((1.0 * mBitmapWidth * height) / mBitmapHeight);
                mPicRect = new Rect(0, 0, mBitmapWidth, mBitmapHeight);
            }

            int drawStartX = paddingLeft + i * POKER_GAP_PX + mDrawCenterStartPos;

            if (mWeSelectedPokerList.contains(i)) {
                paddingTop = 0;
            } else {
                paddingTop = getPaddingTop();
            }

            Rect dst = new Rect(drawStartX, paddingTop, POKER_DRAW_WIDTH + drawStartX, height + paddingTop);
            VLog.debug("src " + mPicRect.toString() + "  dst " + dst.toString());
            canvas.drawBitmap(bitmap, mPicRect, dst, mBitmapPaint);
        }

        mSelectPokerIndex = 0;
        canvas.restore();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // mTotalWidth = w;
        // mTotalHeight = h;
    }

    private int mSelectPokerIndex = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float fx = event.getX();
        float fy = event.getY();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int x = Math.round(fx);
            int index = (x - getPaddingLeft() - mDrawCenterStartPos)/ POKER_GAP_PX;

            int totalUsedSpace = (mDrawingPokers.size() + 3) * POKER_GAP_PX;
            // for draw pokers in center.

            if (index < 0 || x > (mDrawCenterStartPos + totalUsedSpace))
                return false;

            if (index > (mDrawingPokers.size() - 1) && x < getPaddingLeft() + (index * POKER_GAP_PX) + mBitmapWidth)
                index = mDrawingPokers.size() - 1;


            VLog.info("We Clicked the poker index " + index);
            if (mWeSelectedPokerList.contains(index)) {
                mWeSelectedPokerList.remove(Integer.valueOf(index));
            } else {
                mWeSelectedPokerList.add(index);
            }
            mSelectPokerIndex = index;
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
