package qc.com.autoviewdemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * 自定义view制作绚丽的验证码
 */
public class CustomTitleView extends View {


    private String mTitleText;

    private int mTitleTextColor;

    private int mTitleTextSize;

    private Rect mBound;

    private Paint mPaint;
    private Paint mPointPaint;
    private Paint mLinePaint;


    public CustomTitleView(Context context) {
        this(context, null);
    }

    public CustomTitleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomTitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CustomTitleView, defStyleAttr, 0);

        int n = a.getIndexCount();

        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.CustomTitleView_customTitleText:
                    mTitleText = a.getString(attr);
                    break;
                case R.styleable.CustomTitleView_customTitleTextColor:
                    mTitleTextColor = a.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.CustomTitleView_customTitleTextSize:
                    mTitleTextSize = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()));
                    break;

            }

        }
        a.recycle();// 一个好的习惯是用完资源要记得回收，就想打开数据库和IO流用完后要记得关闭一样

        initPaint();


        this.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mTitleText = randomText();
                postInvalidate();
            }

        });

    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        mPointPaint = new Paint();
        mPointPaint.setStrokeWidth(6);
        mPointPaint.setStrokeCap(Paint.Cap.ROUND);


        mLinePaint = new Paint();
        mLinePaint.setStrokeWidth(3);
        mLinePaint.setColor(Color.GRAY);
        mLinePaint.setStrokeCap(Paint.Cap.ROUND);


        mPaint = new Paint();

        mPaint.setTextSize(mTitleTextSize);
        mBound = new Rect();
        mPaint.getTextBounds(mTitleText, 0, mTitleText.length(), mBound);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        if (MeasureSpec.EXACTLY == widthMode) {
            width = widthSize;
        } else {
            float textWidth = mBound.width();
            int desired = (int) (getPaddingLeft() + textWidth + getPaddingRight());
            width = desired;
        }
        if (MeasureSpec.EXACTLY == heightMode) {
            height = heightSize;
        } else {
            float textHeight = mBound.height();
            int desired = (int) (getPaddingTop() + textHeight + getPaddingBottom());
            height = desired;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int viewWidth = getMeasuredWidth();
        int viewHeight = getMeasuredHeight();
        mPaint.setColor(Color.YELLOW);
        canvas.drawRect(0, 0, viewWidth, viewHeight, mPaint);
        mPaint.setColor(mTitleTextColor);
        Random mRandom = new Random();
        int mWidth = mBound.width();
        int mHeight = mBound.height();
        int length = mTitleText.length();
        float charLength = viewWidth / length;
        for (int i = 1; i <= length; i++) {
            int offsetDegree = mRandom.nextInt(15);
            offsetDegree = mRandom.nextInt(2) == 1 ? offsetDegree : -offsetDegree;
            canvas.save();
            canvas.rotate(offsetDegree, mWidth / 2, mHeight / 2);
            mPaint.setARGB(255, mRandom.nextInt(200) + 20, mRandom.nextInt(200) + 20, mRandom.nextInt(200) + 20);
            canvas.drawText(String.valueOf(mTitleText.charAt(i - 1)), (i - 1) * charLength, (viewHeight + mHeight) / 2, mPaint);
            canvas.restore();
        }
        //画点
        drawPoint(canvas, viewWidth, viewHeight, mRandom, length);
        //画线
        drawLine(canvas, viewWidth, viewHeight, mRandom, length);

    }

    /**
     * 画线
     *
     * @param canvas
     * @param viewWidth
     * @param viewHeight
     * @param mRandom
     * @param length
     */
    private void drawLine(Canvas canvas, int viewWidth, int viewHeight, Random mRandom, int length) {
        for (int i = 1; i <= length; i++) {
            int startX = mRandom.nextInt(viewWidth / 3) + 10;
            int startY = mRandom.nextInt(viewHeight / 3) + 10;
            int endX = mRandom.nextInt(viewWidth / 2) + viewWidth / 2 - 10;
            int endY = mRandom.nextInt(viewHeight / 2) + viewHeight / 2 - 10;
            canvas.drawLine(startX, startY, endX, endY, mLinePaint);
        }
    }

    /**
     * 画点
     *
     * @param canvas
     * @param viewWidth
     * @param viewHeight
     * @param mRandom
     * @param length
     */
    private void drawPoint(Canvas canvas, int viewWidth, int viewHeight, Random mRandom, int length) {
        for (int i = 1; i <= length; i++) {
            mPointPaint.setARGB(255, mRandom.nextInt(200) + 20, mRandom.nextInt(200) + 20, mRandom.nextInt(200) + 20);
            canvas.drawPoint(mRandom.nextInt(viewWidth), mRandom.nextInt(viewHeight), mPointPaint);
        }
    }

    /**
     * 生成随机数验证码
     */
    private String randomText() {
        Random random = new Random();
        Set<Integer> set = new HashSet<Integer>();
        while (set.size() < 4) {
            int randomInt = random.nextInt(10);
            set.add(randomInt);
        }
        StringBuffer sb = new StringBuffer();
        for (Integer i : set) {
            sb.append("" + i);
        }

        return sb.toString();
    }
}
