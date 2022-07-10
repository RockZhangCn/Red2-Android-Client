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
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.rockzhang.red2.R;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * A colorful circle view with text.
 *
 * @author SongNing
 */

public class LayoutView extends View {
    public static final int TOTAL_CHESS = 30;
    private final int DEFAULT_CIRCLE_SIZE;
    private Paint mLinePaint;
    private Paint mVerticalTextPaint;
    ////////////////////////////////////////////////
    private int mLineColor;
    private float GRID_UNIT_WIDTH = 0;
    private float CHESS_UNIT_WIDTH = 0;
    private int VIEW_CENTER_X = 0;
    private int VIEW_CENTER_Y = 0;
    private int DOUBLE_LINE_CAP_WIDTH = 12;
    private int BASE_LINE_WIDTH = 8;
    private boolean needDrawRect = false;
    private int selectX = 0;
    private int selectY = 0;
    private List<Byte> chessLayoutArray = null;
    private int initDrawIndex = 0;
    private int mPreviousFocusIndex = -1;

    public LayoutView(Context context) {
        this(context, null);
    }

    public LayoutView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LayoutView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        DEFAULT_CIRCLE_SIZE = dp2px(context, 48);
        final int DEFAULT_CIRCLE_COLOR = 0xFF03A9F4;
        mLineColor = DEFAULT_CIRCLE_COLOR;

        setBackgroundColor(0xFF777777);
        init();
    }

    private void init() {
        initDrawIndex = 0;
        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setDither(true);

        mLineColor = Color.BLUE;

        mLinePaint.setColor(mLineColor);
        mLinePaint.setStrokeWidth(BASE_LINE_WIDTH);

        mVerticalTextPaint = new Paint();
        mVerticalTextPaint.setAntiAlias(true);
        mVerticalTextPaint.setDither(true);

//        chessLayoutArray = Arrays.asList(
//                COMMANDER_35, COMMANDER_36, COMMANDER_40, COMMANDER_32, COMMANDER_39,
//                COMMANDER_33,CHESS_DEFAULT_EMPTY, COMMANDER_32,CHESS_DEFAULT_EMPTY, WEAPON_BOMB,
//                COMMANDER_33, COMMANDER_34, CHESS_DEFAULT_EMPTY, COMMANDER_37, COMMANDER_35,
//                COMMANDER_34, CHESS_DEFAULT_EMPTY, COMMANDER_38, CHESS_DEFAULT_EMPTY, COMMANDER_38,
//                COMMANDER_34, WEAPON_LANDMINE,COMMANDER_36,  COMMANDER_32, WEAPON_BOMB,
//                WEAPON_LANDMINE,WEAPON_FLAG, WEAPON_LANDMINE, COMMANDER_33, COMMANDER_37
//            );
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

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.poker_0);
        Rect src = new Rect(0, 0, 200, 200);
        Rect dst = new Rect(0, 0, 200, 150);
        canvas.drawBitmap(bitmap,src,dst,mLinePaint);

        mLinePaint.setStrokeWidth(BASE_LINE_WIDTH);
        final int paddingLeft = 0;
        final int paddingRight = 0;
        final int paddingTop = getPaddingTop();
        final int paddingBottom = getPaddingBottom();

        int width = getWidth() - paddingLeft - paddingRight;
        int height = getHeight() - paddingTop - paddingBottom;

        GRID_UNIT_WIDTH = Math.min(width, height) / 5;
        VIEW_CENTER_X = paddingLeft + width / 2;
        VIEW_CENTER_Y = paddingTop - (int) GRID_UNIT_WIDTH * 2;


        CHESS_UNIT_WIDTH = GRID_UNIT_WIDTH / 8 * 6;

        canvas.translate(VIEW_CENTER_X, VIEW_CENTER_Y);

        int shift = DOUBLE_LINE_CAP_WIDTH / 2;

        drawLayoutBoard(canvas, shift, GRID_UNIT_WIDTH);

//        drawAllChess(canvas, GRID_UNIT_WIDTH, CHESS_UNIT_WIDTH, gColorArray[0].getPlayerColor());

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
            needDrawRect = !needDrawRect;

            int currentSwapIndex = 0;
            if (needDrawRect) {
                mPreviousFocusIndex = translatePosToIndex(x, y);
            } else {
                currentSwapIndex = translatePosToIndex(x, y);
                if (mPreviousFocusIndex != -1) {
                    int previous = chessLayoutArray.get(mPreviousFocusIndex);
                    int current = chessLayoutArray.get(currentSwapIndex);

                    Log.e("ROCK", needDrawRect + " swap " + chessLayoutArray.get(mPreviousFocusIndex) + " <--> " + chessLayoutArray.get(currentSwapIndex));

//                    if (((previous == WEAPON_FLAG || previous == WEAPON_LANDMINE) && currentSwapIndex < 20) ||
//                            ((current == WEAPON_FLAG || current == WEAPON_LANDMINE) && mPreviousFocusIndex < 20) ||
//                            ((previous == WEAPON_FLAG) && currentSwapIndex != 28 && currentSwapIndex != 26) ||
//                            ((current == WEAPON_FLAG) && mPreviousFocusIndex != 28 && mPreviousFocusIndex != 26) ||
//                            ((previous == WEAPON_BOMB) && currentSwapIndex < 5) ||
//                            ((current == WEAPON_BOMB) && mPreviousFocusIndex < 5)
//                            ) {
//                        Log.e("ROCK", "swap failed");
//                        GameSound.getInstance().playSound(SoundType.SOUND_WAS_KILLED);
//                    } else {
//                        Collections.swap(chessLayoutArray, mPreviousFocusIndex, currentSwapIndex);
//                        GameSound.getInstance().playSound(SoundType.SOUND_GAME_CLOCK);
//                    }
                }
            }

            invalidate();
        }

        return true;
    }

    private int translatePosToIndex(int x, int y) {
        return (y - 3) * 5 + 2 + x;
    }

    private void drawAllChess(Canvas canvas, float block, float chessWidth, int color) {

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

                    mLinePaint.setColor(Color.WHITE);
                    mLinePaint.setTextSize(52);
                    mLinePaint.setStrokeWidth(4);

                    Paint.FontMetricsInt fontMetrics = mLinePaint.getFontMetricsInt();
                    float baseline = (rectF.bottom + rectF.top - fontMetrics.bottom - fontMetrics.top) / 2;
                    mLinePaint.setTextAlign(Paint.Align.CENTER);

//                    Chess ch = new Chess(chessID);
//                    canvas.drawText(ch.getLongName(), rectF.centerX(), baseline, mLinePaint);

                    if (initDrawIndex == TOTAL_CHESS) {
                        initDrawIndex = 0;
                    }
               // }
            }
        }
    }

    private void drawLayoutBoard(Canvas canvas, int shift, float block) {
        mLinePaint.setColor(mLineColor);
        mLinePaint.setStyle(Paint.Style.STROKE);

        canvas.drawLine(-2 * block - shift, 3 * block - shift, 2 * block + shift, 3 * block - shift, mLinePaint);
        canvas.drawLine(-2 * block - shift, 3 * block + shift, 2 * block + shift, 3 * block + shift, mLinePaint);

        canvas.drawLine(-2 * block - shift, 4 * block, 2 * block + shift, 4 * block, mLinePaint);
        canvas.drawLine(-2 * block - shift, 5 * block, 2 * block + shift, 5 * block, mLinePaint);
        canvas.drawLine(-2 * block - shift, 6 * block, 2 * block + shift, 6 * block, mLinePaint);


        canvas.drawLine(-2 * block - shift, 7 * block - shift, 2 * block + shift, 7 * block - shift, mLinePaint);
        canvas.drawLine(-2 * block - shift, 7 * block + shift, 2 * block + shift, 7 * block + shift, mLinePaint);

        canvas.drawLine(-2 * block - shift, 8 * block, 2 * block + shift, 8 * block, mLinePaint);

        //left double line
        canvas.drawLine(-2 * block - shift, 3 * block, -2 * block - shift, 7 * block + shift, mLinePaint);
        canvas.drawLine(-2 * block + shift, 3 * block, -2 * block + shift, 7 * block + shift, mLinePaint);

        //right double line.
        canvas.drawLine(2 * block + shift, 3 * block, 2 * block + shift, 7 * block + shift, mLinePaint);
        canvas.drawLine(2 * block - shift, 3 * block, 2 * block - shift, 7 * block + shift, mLinePaint);

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
