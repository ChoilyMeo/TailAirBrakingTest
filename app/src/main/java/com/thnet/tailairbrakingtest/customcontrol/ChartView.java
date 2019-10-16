package com.thnet.tailairbrakingtest.customcontrol;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.elvishew.xlog.XLog;
import com.thnet.tailairbrakingtest.testwind.TestContent;
import com.thnet.tailairbrakingtest.testwind.TestData;
import com.thnet.tailairbrakingtest.utility.DateTimeUtil;
import com.thnet.tailairbrakingtest.utility.DensityUtil;

import java.util.Date;

public class ChartView extends View {
    private final static int Y_SCALE_LEVEL_COUNT = 7;
    private final static String[] Y_SCALE_TEXT = {"700", "600", "500", "400", "300", "200", "100"};
    private final static int[] Y_SCALE_VALUES = {700, 600, 500, 400, 300, 200, 100};
    private final static int DRAW_LINE_WIDTH_AXIS = 2;
    private final static int DRAW_LINE_WIDTH_CHART = 1;
    /**
     * Y坐标的数据最大值
     */
    private final static int Y_MAX_DATA_VALUE = 700;
    private int mViewWidth, mViewHeight, mTextBaseLineToTop, mChartViewWidth, mChartViewHeight, mChartViewStartPos, mScaleYaxisTextSize, mTextHeight;
    private int mViewDataStartPos = 0;
    private float mScaleYaxis;
    private Paint mAxisPaint, mChartLinePaint, mLevelLinePaint, mTextPaint;
    private Path mLinePath;
    private TestData mViewTestData;
    private boolean mCanScroll = false;
    private float mMoveStartX;
    private int mMoveOffsetX;
    public ChartView(Context context) {
        super(context);
        init();
    }

    public ChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        try {
            setWillNotDraw(false);
            mLinePath = new Path();
            //初始化坐标轴画笔
            mAxisPaint = new Paint();
            mAxisPaint.setAntiAlias(true);
            mAxisPaint.setStrokeWidth(DRAW_LINE_WIDTH_AXIS);
            mAxisPaint.setStyle(Paint.Style.STROKE);
            //初始化水平线画笔
            mLevelLinePaint = new Paint();
            mLevelLinePaint.setAntiAlias(true);
            mLevelLinePaint.setStrokeWidth(1);
            mLevelLinePaint.setStyle(Paint.Style.STROKE);
            mLevelLinePaint.setPathEffect(new DashPathEffect(new float[]{2, 2}, 0));
            //初始化文字画笔
            mTextPaint = new Paint();
            mTextPaint.setAntiAlias(true);
            mTextPaint.setTextAlign(Paint.Align.LEFT);
            mTextPaint.setTextSize(DensityUtil.dp2px(14));
            mTextBaseLineToTop = mTextPaint.getFontMetricsInt().top;
            //初始化显示曲线画笔
            mChartLinePaint = new Paint();
            mChartLinePaint.setAntiAlias(true);
            mChartLinePaint.setStrokeWidth(DRAW_LINE_WIDTH_CHART);
            mChartLinePaint.setStyle(Paint.Style.STROKE);
            //计算显示文字高度
            mTextHeight = 0 - (mTextBaseLineToTop - mTextPaint.getFontMetricsInt().bottom);
            //计算Y坐标轴文字显示宽度
            mScaleYaxisTextSize = (int) mTextPaint.measureText(Y_SCALE_TEXT[0]) + 1;
        } catch (Exception ex) {
            XLog.e("试风曲线绘制控件初始化异常：" + ex.getMessage());
        }
    }

    /**
     * 画坐标轴
     * @param canvas 画布
     */
    private void drawAxis(Canvas canvas){
        //画坐标轴水平标线
        mTextPaint.setColor(Color.BLACK);
        for (int i = 0; i < Y_SCALE_LEVEL_COUNT && i < Y_SCALE_TEXT.length && i < Y_SCALE_VALUES.length; i++){
            canvas.drawText(Y_SCALE_TEXT[i], 0, (Y_MAX_DATA_VALUE - Y_SCALE_VALUES[i]) * mScaleYaxis - mTextBaseLineToTop, mTextPaint);
            mLinePath.reset();
            mLinePath.moveTo(mScaleYaxisTextSize, (Y_MAX_DATA_VALUE - Y_SCALE_VALUES[i]) * mScaleYaxis);
            mLinePath.lineTo(mViewWidth, (Y_MAX_DATA_VALUE - Y_SCALE_VALUES[i]) * mScaleYaxis);
            canvas.drawPath(mLinePath, mLevelLinePaint);
        }
        //画坐标轴Y轴
        mLinePath.reset();
        mLinePath.moveTo(mScaleYaxisTextSize,0);
        mLinePath.lineTo(mScaleYaxisTextSize, mChartViewHeight);
        canvas.drawPath(mLinePath, mAxisPaint);
        //画坐标轴X轴
        mLinePath.reset();
        mLinePath.moveTo(mScaleYaxisTextSize, mChartViewHeight);
        mLinePath.lineTo(mViewWidth, mChartViewHeight);
        canvas.drawPath(mLinePath, mAxisPaint);
    }

    private void drawChart(Canvas canvas){
        if (null != mViewTestData){
            mTextPaint.setColor(Color.BLACK);
            String viewEndTime = "", viewStartTime = "";
            if (null != mViewTestData.getStartTime()){
                viewStartTime = mViewTestData.getStartTime();
            }
            canvas.drawText(viewStartTime, mScaleYaxisTextSize, mChartViewHeight - mTextBaseLineToTop, mTextPaint);
            if (null == mViewTestData.getEndTime() || mViewTestData.getEndTime().isEmpty()){
                viewEndTime = DateTimeUtil.formatDateTimetoString(new Date(), DateTimeUtil.FMT_HHmmss);
            } else {
                viewEndTime = mViewTestData.getEndTime();
            }
            int viewTextSize = (int) mTextPaint.measureText(viewEndTime) + 1;
            canvas.drawText(viewEndTime, mViewWidth - viewTextSize, mChartViewHeight - mTextBaseLineToTop, mTextPaint);
            if (mViewTestData.lstPressureValue.size() > 0){
                String viewPressureValue = String.valueOf(mViewTestData.lstPressureValue.get(mViewTestData.lstPressureValue.size() - 1).getPressureValue());
                viewTextSize = (int)mTextPaint.measureText(viewPressureValue);
                canvas.drawText(viewPressureValue, mChartViewStartPos + mChartViewWidth / 2 - viewTextSize / 2, mChartViewHeight - mTextBaseLineToTop, mTextPaint);
                mLinePath.reset();
                mLinePath.moveTo(mChartViewStartPos, convertDataValueToYpos(mViewTestData.lstPressureValue.get(mViewDataStartPos).getPressureValue()));
                for (int i = mViewDataStartPos; i < mViewTestData.lstPressureValue.size(); i++){
                    mLinePath.lineTo(mChartViewStartPos + i - mViewDataStartPos, convertDataValueToYpos(mViewTestData.lstPressureValue.get(i).getPressureValue()));
                }
                mChartLinePaint.setColor(Color.RED);
                canvas.drawPath(mLinePath, mChartLinePaint);
                for (TestContent testContent : mViewTestData.listTest){
                    testContent.drawSelf(canvas, this);
                }
            }
        }
    }

    public float convertDataValueToYpos(int dataValue){
        if (dataValue < 0) {
            dataValue = 0;
        } else if (dataValue > Y_MAX_DATA_VALUE) {
            dataValue = Y_MAX_DATA_VALUE;
        }
        return (Y_MAX_DATA_VALUE - dataValue) * mScaleYaxis;
    }

    public int convertDataValuetoXpos(int dataValue){
        if (dataValue < mViewDataStartPos){
            dataValue = mViewDataStartPos;
        } else if (dataValue - mViewDataStartPos > mChartViewWidth){
            dataValue = mViewDataStartPos + mChartViewWidth;
        }
        return dataValue - mViewDataStartPos + mChartViewStartPos;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mViewWidth = getWidth() - 1;
        mViewHeight = getHeight() - 1;
        mChartViewWidth = mViewWidth - mScaleYaxisTextSize - DRAW_LINE_WIDTH_AXIS;
        mChartViewHeight = mViewHeight - mTextHeight;
        mChartViewStartPos = mScaleYaxisTextSize + DRAW_LINE_WIDTH_AXIS;
        mScaleYaxis = ((float) mChartViewHeight) / Y_MAX_DATA_VALUE;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try{
            drawAxis(canvas);
            drawChart(canvas);
            canvas.save();
        } catch (Exception ex) {
            XLog.e("试风曲线绘制异常：" + ex.getMessage());
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mCanScroll) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    XLog.d("点击事件：" + event.getX());
                    mMoveStartX = event.getX();
                    return true;
                case MotionEvent.ACTION_MOVE:
                    mMoveOffsetX = (int) (event.getX() - mMoveStartX);
                    return true;
                case MotionEvent.ACTION_UP:
                    XLog.d("横向移动距离" + mMoveOffsetX + "后松开事件：" + event.getX());
                    if (mMoveOffsetX < 0) {
                        viewRight(0 - mMoveOffsetX);
                    } else {
                        viewLeft(mMoveOffsetX);
                    }
                    return true;
                default:
                    break;
            }
        }
        return super.onTouchEvent(event);
    }

    public boolean validTestViewInRange(int beginPos, int endPos){
        return !(beginPos < 0 || beginPos < mViewDataStartPos || endPos < mViewDataStartPos || endPos < beginPos || endPos - mViewDataStartPos > mChartViewWidth);
    }

    public void viewToEnd(){
        if (mViewTestData.lstPressureValue.size() > mChartViewWidth){
            mViewDataStartPos = mViewTestData.lstPressureValue.size() - mChartViewWidth;
        }
        invalidate();
    }

    public void viewToBegin(){
        mViewDataStartPos = 0;
        invalidate();
    }

    public void viewLeft(int size){
        if (size <= 0) {
            return;
        }
        if (mCanScroll) {
            if (mViewDataStartPos >= size) {
                mViewDataStartPos -= size;
            } else {
                mViewDataStartPos = 0;
            }
            invalidate();
            XLog.d("向左移动"+size+"后刷新显示。");
        }
    }

    public void viewRight(int size){
        if (size <= 0) {
            return;
        }
        if (mCanScroll) {
            if (mViewDataStartPos + size < mViewTestData.lstPressureValue.size() - mChartViewWidth) {
                mViewDataStartPos += size;
            }
            invalidate();
            XLog.d("向右移动"+size+"后刷新显示。");
        }
    }

    public void setViewTestData(TestData mViewTestData) {
        this.mViewTestData = mViewTestData;
    }

    public Paint getChartLinePaint() {
        return mChartLinePaint;
    }

    public Paint getTextPaint() {
        return mTextPaint;
    }

    public boolean isCanScroll() {
        return mCanScroll;
    }

    public void setCanScroll(boolean mCanScroll) {
        this.mCanScroll = mCanScroll;
    }
}
