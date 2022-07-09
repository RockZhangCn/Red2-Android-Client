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

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Arrays;
import java.util.List;

public class BoardView extends View implements ValueAnimator.AnimatorUpdateListener {
    public final int TOTAL_CHESS = 30;
    private final int DEFAULT_CIRCLE_SIZE;
    private Paint mLinePaint;
    ////////////////////////////////////////////////
    private int mLineColor;
    private float GRID_UNIT_WIDTH = 0;
    private float CHESS_UNIT_WIDTH = 0;
    private List<Byte> chessLayoutArray = null;
    private int VIEW_CENTER_X = 0;
    private int VIEW_CENTER_Y = 0;
    private int DOUBLE_LINE_CAP_WIDTH = 2;
    private int BASE_LINE_WIDTH = 3;

    private int initDrawIndex = 0;
    private int selectX = 0;
    private int selectY = 0;
    private boolean needDrawRect = false;


    public BoardView(Context context) {
        this(context, null);
    }

    public BoardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        DEFAULT_CIRCLE_SIZE = dp2px(context, 48);
        final int DEFAULT_CIRCLE_COLOR = 0xFF03A9F4;
        mLineColor = DEFAULT_CIRCLE_COLOR;

        setBackgroundColor(0xFF777777);

        init();
    }

    private void init() {
        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setDither(true);

        mLineColor = 0xFFF44336;

        mLinePaint.setColor(mLineColor);
        mLinePaint.setStrokeWidth(BASE_LINE_WIDTH);

        initDrawIndex = 0;

    }

    public List<Byte> getLayoutResults() {
        return chessLayoutArray;
    }

    public void setLayoutResults(List<Byte> layout) {
        chessLayoutArray = layout;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = measureSpec(widthMeasureSpec);
        final int height = measureSpec(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    private int measureSpec(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else if (specMode == MeasureSpec.AT_MOST) {
            result = Math.min(DEFAULT_CIRCLE_SIZE, specSize);
        } else {
            result = DEFAULT_CIRCLE_SIZE;
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();

        final int paddingLeft = 0;
        final int paddingRight = 0;
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();

        int width = getWidth() - paddingLeft - paddingRight;
        int height = getHeight() - paddingTop - paddingBottom;

        VIEW_CENTER_X = paddingLeft + width / 2;
        VIEW_CENTER_Y = paddingTop + height / 2;

        GRID_UNIT_WIDTH = Math.min(width, height) / 17;

        CHESS_UNIT_WIDTH = GRID_UNIT_WIDTH / 8 * 7;

        canvas.translate(VIEW_CENTER_X, VIEW_CENTER_Y);

        int shift = DOUBLE_LINE_CAP_WIDTH / 2;

        Log.e("ROCK", "Block size is " + GRID_UNIT_WIDTH);

        final int FOUR = 4;
        for (int i = 0; i < FOUR; i++) {
            drawOneBoard(i, canvas, shift, GRID_UNIT_WIDTH);
            canvas.rotate(-90);
        }

//        drawMyChess(0, canvas, GRID_UNIT_WIDTH, CHESS_UNIT_WIDTH, gColorArray[0].getPlayerColor());

        if (needDrawRect) {
            float centerX = selectX * GRID_UNIT_WIDTH;
            float centerY = selectY * GRID_UNIT_WIDTH;
            mLinePaint.setStyle(Paint.Style.STROKE);
            mLinePaint.setStrokeWidth(BASE_LINE_WIDTH * 2);
            mLinePaint.setColor(Color.YELLOW);
            RectF rectF = new RectF(centerX - CHESS_UNIT_WIDTH / 2, centerY - CHESS_UNIT_WIDTH / 2, centerX + CHESS_UNIT_WIDTH / 2, centerY + CHESS_UNIT_WIDTH / 2);
            canvas.drawRect(rectF, mLinePaint);
        }


        canvas.restore();
    }

    public boolean onTouchEvent(MotionEvent event) {
        float fx = event.getX() - VIEW_CENTER_X;
        float fy = event.getY() - VIEW_CENTER_Y;

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int x = Math.round(fx / GRID_UNIT_WIDTH);
            int y = Math.round(fy / GRID_UNIT_WIDTH);
            Log.e("ROCK", "we click " + x + ", " + y);

            //barback
            if ((x == -1 || x == 1) && y == 4) {
                return true;
            }

            //barback
            if (x == 0 && y == 5) {
                return true;
            }

            //barback
            if ((x == -1 || x == 1) && y == 6) {
                return true;
            }

            if (x < -2 || x > 2 || y < 3 || y > 8) {
                return true;
            }

            selectX = x;
            selectY = y;
            needDrawRect = true;

            invalidate();
        }

        return true;
    }

    private void drawMyChess(int pos, Canvas canvas, float block, float chessWidth, int color) {

        mLinePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        for (int y = 3; y <= 8; y++) {
            for (int x = -2; x <= 2; x++) {
                byte chessID = chessLayoutArray.get(initDrawIndex++);
//                if (chessID != CHESS_DEFAULT_EMPTY) {
                    float centerX = x * block;
                    float centerY = y * block;

//                    if (chessID == WEAPON_FLAG) {
//                        mLinePaint.setColor(color + 0x00013333);
//                    } else {
//                        mLinePaint.setColor(color);
//                    }

                    RectF rectF = new RectF(centerX - chessWidth / 2, centerY - chessWidth / 2, centerX + chessWidth / 2, centerY + chessWidth / 2);
                    canvas.drawRect(rectF, mLinePaint);

                    if (pos == 0) {
                        mLinePaint.setColor(Color.WHITE);
                        mLinePaint.setTextSize(52);
                        mLinePaint.setStrokeWidth(4);

                        Paint.FontMetricsInt fontMetrics = mLinePaint.getFontMetricsInt();
                        float baseline = (rectF.bottom + rectF.top - fontMetrics.bottom - fontMetrics.top) / 2;
                        mLinePaint.setTextAlign(Paint.Align.CENTER);

//                        Chess ch = new Chess(chessID);
//                        canvas.drawText(ch.getShortName(), rectF.centerX(), baseline, mLinePaint);

                        if (initDrawIndex == TOTAL_CHESS) {
                            initDrawIndex = 0;
                        }
                    }
//              /  }
            }
        }
    }

    private void drawOneBoard(int pos, Canvas canvas, int shift, float block) {
        mLinePaint.setColor(mLineColor);
        mLinePaint.setStyle(Paint.Style.STROKE);
        //Horizontal lines.
        canvas.drawLine(-2 * block - shift, 2 * block - shift, 2 * block + shift, 2 * block - shift, mLinePaint);
        canvas.drawLine(-2 * block - shift, 2 * block + shift, 2 * block + shift, 2 * block + shift, mLinePaint);

        canvas.drawLine(-2 * block - shift, 3 * block - shift, 2 * block + shift, 3 * block - shift, mLinePaint);
        canvas.drawLine(-2 * block - shift, 3 * block + shift, 2 * block + shift, 3 * block + shift, mLinePaint);

        canvas.drawLine(-2 * block - shift, 4 * block, 2 * block + shift, 4 * block, mLinePaint);
        canvas.drawLine(-2 * block - shift, 5 * block, 2 * block + shift, 5 * block, mLinePaint);
        canvas.drawLine(-2 * block - shift, 6 * block, 2 * block + shift, 6 * block, mLinePaint);


        canvas.drawLine(-2 * block - shift, 7 * block - shift, 2 * block + shift, 7 * block - shift, mLinePaint);
        canvas.drawLine(-2 * block - shift, 7 * block + shift, 2 * block + shift, 7 * block + shift, mLinePaint);

        canvas.drawLine(-2 * block - shift, 8 * block, 2 * block + shift, 8 * block, mLinePaint);

        //Vertical lines.

        //middle double line.
        canvas.drawLine(-1 * shift, 0, -1 * shift, 3 * block, mLinePaint);
        canvas.drawLine(shift, 0, shift, 3 * block, mLinePaint);

        //left double line
        canvas.drawLine(-2 * block - shift, 2 * block, -2 * block - shift, 7 * block + shift, mLinePaint);
        canvas.drawLine(-2 * block + shift, 2 * block, -2 * block + shift, 7 * block + shift, mLinePaint);

        //right double line.
        canvas.drawLine(2 * block + shift, 2 * block, 2 * block + shift, 7 * block + shift, mLinePaint);
        canvas.drawLine(2 * block - shift, 2 * block, 2 * block - shift, 7 * block + shift, mLinePaint);

        //left bottom single line
        canvas.drawLine(-2 * block, 7 * block, -2 * block, 8 * block, mLinePaint);
        //right bottom single line
        canvas.drawLine(2 * block, 7 * block, 2 * block, 8 * block, mLinePaint);


        //left single line.
        canvas.drawLine(-1 * block, 3 * block - shift, -1 * block, 8 * block, mLinePaint);
        //middle single line
        canvas.drawLine(0, 3 * block - shift, 0, 8 * block, mLinePaint);
        //right single line.
        canvas.drawLine(block, 3 * block - shift, block, 8 * block, mLinePaint);

        // draw xie xian /
        canvas.drawLine(-2 * block, 5 * block, 0, 3 * block, mLinePaint);
        canvas.drawLine(-2 * block, 7 * block, 2 * block, 3 * block, mLinePaint);
        canvas.drawLine(0, 7 * block, 2 * block, 5 * block, mLinePaint);

        // draw xie xian \
        canvas.drawLine(0, 3 * block, 2 * block, 5 * block, mLinePaint);
        canvas.drawLine(-2 * block, 3 * block, 2 * block, 7 * block, mLinePaint);
        canvas.drawLine(-2 * block, 5 * block, 0, 7 * block, mLinePaint);

        mLinePaint.setStrokeWidth(BASE_LINE_WIDTH * 2 - 2);
        RectF rect = new RectF(-4 * block - BASE_LINE_WIDTH / 2, 2 * block + BASE_LINE_WIDTH / 2, -2 * block - BASE_LINE_WIDTH / 2, 4 * block + BASE_LINE_WIDTH / 2);
        canvas.drawArc(rect, 270, 90, false, mLinePaint);
        mLinePaint.setStrokeWidth(BASE_LINE_WIDTH);


        //left castle.
        mLinePaint.setColor(0xFFAAAAAA);
        mLinePaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(-1 * block, 4 * block, block / 2, mLinePaint);
        canvas.drawCircle(1 * block, 4 * block, block / 2, mLinePaint);

        canvas.drawCircle(-1 * block, 6 * block, block / 2, mLinePaint);
        canvas.drawCircle(1 * block, 6 * block, block / 2, mLinePaint);

        canvas.drawCircle(0, 5 * block, block / 2, mLinePaint);

        mLinePaint.setColor(mLineColor);
        mLinePaint.setStyle(Paint.Style.STROKE);

        //For debug.
        mLinePaint.setTextSize(48);
        canvas.drawText("0" + pos, 3 * block, 6 * block, mLinePaint);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        float mAngle = (float) animation.getAnimatedValue();
        invalidate();
    }

    private int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    private int sp2px(Context context, float sp) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * fontScale + 0.5f);
    }

}
