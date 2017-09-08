package com.qinggan.testtouchbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Description: 一个选择日期的触控条
 * Author: Seven
 * Mail: huangyawen.happy@gmail.com
 * Date: 17-8-30.
 */

public class DateTouchBar extends View
{
    private static final String TAG = "Test";
    private Context context;
    private int mWidth;
    private int mHeight;

    private String startDate; // 开始日期
    private int days; // 最大天数

    private List<Integer> nums; // 分解的日的集合
    private int choseStartIndex; //选中的起始日在集合中的索引
    private int choseEndIndex; //选中的结束日在集合中的索引

    private Paint textPaint;
    private float textSize;
    private int textHigh; // 文本高度

    private Paint rectPaint;
    private int touchBarHeight; // 触控条的高度
    private int touchBarColor;

    private int paddingLeft;
    private int paddingTop;
    private float itemWidth; // 每一个日期所占的宽度

    private final int NOCHANGE = 0;
    private final int ENDDATECHANGE = 1;
    private final int STARTDATECHANGE = 2;
    private int moveAction;

    private DateChangedListener listener;

    public DateTouchBar(Context context)
    {
        this(context, null);
    }

    public DateTouchBar(Context context, @Nullable AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public DateTouchBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs)
    {
        this.context = context;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TouchBarStyle);
        touchBarHeight = (int) ta.getDimension(R.styleable.TouchBarStyle_touchBarHeight, DensityUtil.dip2px(context, 60));
        paddingLeft = (int) ta.getDimension(R.styleable.TouchBarStyle_touchBarPaddingLeft, DensityUtil.dip2px(context, 10));
        days = ta.getInteger(R.styleable.TouchBarStyle_days, 7);
        textSize = ta.getDimension(R.styleable.TouchBarStyle_numTextSize, 36);
        touchBarColor = ta.getColor(R.styleable.TouchBarStyle_touchBarColor, context.getResources().getColor(R.color.blue));

        ta.recycle();

        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        startDate = dateFormat.format(now);
        choseStartIndex = 0;
        choseEndIndex = 1;


        initDate();
        initPaint();
    }

    /**
     *
     * @param start 起始日期
     * @param end 结束日期
     */
    public void setDate(String start, String end)
    {
        int tmp = daysBetween(start, end);
        if(tmp <= 0)
            throw new IllegalArgumentException("起始日期必须小于结束日期！");
        else if(tmp > (days - 1))
            throw new IllegalArgumentException("日期间隔必须小于设置的天数！");

        startDate = start;
        choseStartIndex = 0;
        choseEndIndex = tmp;

        initDate();
        invalidate();
    }

    private void initPaint()
    {
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(textSize);

        textHigh = (int) (textPaint.descent() - textPaint.ascent() + 0.5f);

        rectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        rectPaint.setStyle(Paint.Style.FILL);
        rectPaint.setColor(touchBarColor);

    }

    private void initDate()
    {
        nums = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date1;
        try
        {
            date1 = sdf.parse(startDate);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date1);
            nums.add(cal.get(Calendar.DAY_OF_MONTH));
            for (int i = 0; i < days - 1; i++)
            {
                cal.add(Calendar.DATE, 1);
                nums.add(cal.get(Calendar.DAY_OF_MONTH));
            }
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        itemWidth = (mWidth - 2.0f * paddingLeft) / days;
        paddingTop = (mHeight - touchBarHeight) / 2;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        drawRect(canvas);
        drawNums(canvas);
    }

    private void drawRect(Canvas canvas)
    {
        Path path = new Path();
        RectF startRect = new RectF(paddingLeft + choseStartIndex * itemWidth, paddingTop,
                paddingLeft + (choseStartIndex + 1) * itemWidth, paddingTop + touchBarHeight);

        RectF endRect = new RectF(paddingLeft + choseEndIndex * itemWidth, paddingTop,
                paddingLeft + (choseEndIndex + 1) * itemWidth, paddingTop + touchBarHeight);

        path.addArc(startRect, 90, 180);
        path.lineTo(paddingLeft + (choseEndIndex + 1) * itemWidth - itemWidth / 2, paddingTop + touchBarHeight);
        path.addArc(endRect, 270, 180);
        path.lineTo(paddingLeft + (choseStartIndex + 1) * itemWidth - itemWidth / 2, paddingTop);

        canvas.drawPath(path, rectPaint);
    }

    private void drawNums(Canvas canvas)
    {
        Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
        int top = (mHeight - textHigh) / 2;
        for (int i = 0; i < nums.size(); i++)
        {
            Rect targetRect = new Rect((int) (paddingLeft + i * itemWidth), top, (int) (paddingLeft + (i + 1) * itemWidth), top + textHigh);

            if (i >= choseStartIndex && i <= choseEndIndex)
            {
                textPaint.setColor(Color.WHITE);
            }
            else
            {
                textPaint.setColor(context.getResources().getColor(R.color.half_trasparent_white));
            }
            int baseline = (targetRect.bottom + targetRect.top - fontMetrics.bottom - fontMetrics.top) / 2;
            // 下面这行是实现水平居中，drawText对应改为传入targetRect.centerX()
            textPaint.setTextAlign(Paint.Align.CENTER);
            String drawNum;
            if (nums.get(i) < 10)
            {
                drawNum = "0" + String.valueOf(nums.get(i));
            }
            else
            {
                drawNum = String.valueOf(nums.get(i));
            }
            canvas.drawText(drawNum, targetRect.centerX(), baseline, textPaint);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        float x = event.getX();
        int tmp = (int) ((x - paddingLeft) / itemWidth);
        if(tmp < 0)
            tmp = 0;
        else if(tmp > (days - 1))
            tmp = days - 1;

        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                if(tmp == choseEndIndex)
                    moveAction = ENDDATECHANGE;
                else if(tmp == choseStartIndex)
                    moveAction = STARTDATECHANGE;
                else
                    moveAction = NOCHANGE;
                break;
            case MotionEvent.ACTION_MOVE:
                handlerMoveAction(tmp);
                break;
            case MotionEvent.ACTION_UP:
                if(listener != null)
                {
                    notifyDateChanged();
                }
                break;
        }

        return true;
    }

    private void notifyDateChanged()
    {
        List<String> list = new ArrayList<>();
        list.add(getDate(choseStartIndex));
        list.add(getDate(choseEndIndex));

        listener.dateChanged(list);
    }

    private String getDate(int index)
    {
        String tmp;
        if(nums.get(index) < 10)
        {
            tmp = "0" + String.valueOf(nums.get(index));
        }
        else
        {
            tmp = String.valueOf(nums.get(index));
        }

        String [] temp = startDate.split("-");
        tmp = temp[0] + "-" + temp[1] + "-" + tmp;
        return tmp;
    }

    private void handlerMoveAction(int touchArea)
    {
        switch (moveAction)
        {
            case ENDDATECHANGE:
                if(choseEndIndex > touchArea)
                {
                    if(touchArea > choseStartIndex)
                    {
                        choseEndIndex = touchArea;
                        invalidate();
                    }
                }
                else if(choseEndIndex < touchArea)
                {
                    choseEndIndex = touchArea;
                    invalidate();
                }
                break;
            case STARTDATECHANGE:
                if(choseStartIndex > touchArea)
                {
                    choseStartIndex = touchArea;
                    invalidate();
                }
                else if(choseStartIndex < touchArea)
                {
                    if(touchArea < choseEndIndex)
                    {
                        choseStartIndex = touchArea;
                        invalidate();
                    }
                }
                break;
        }
    }

    private int daysBetween(String datebefore, String date)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date1, date2;
        int days = 0;
        try
        {
            date1 = sdf.parse(datebefore);
            date2 = sdf.parse(date);
            Calendar cal = Calendar.getInstance();

            cal.setTime(date1);
            long time1 = cal.getTimeInMillis();

            cal.setTime(date2);
            long time2 = cal.getTimeInMillis();

            long between_days = (time2 - time1) / (1000 * 3600 * 24);
            days = (int) between_days;

        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return days;
    }


    public void setDateChangedListener(DateChangedListener listener)
    {
        this.listener = listener;
    }

    public interface DateChangedListener
    {
         void dateChanged(List<String> date);
    }
}
