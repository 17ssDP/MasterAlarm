
package com.example.masteralarm.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.afollestad.aesthetic.Aesthetic;
import com.example.masteralarm.interfaces.Subscribable;

import androidx.viewpager.widget.ViewPager;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.jfenn.androidutils.DimenUtils;

public class PageIndicatorView extends View implements ViewPager.OnPageChangeListener, Subscribable {

    private int actualPosition;
    private float offset;
    private int size;
    private ViewPager viewPager;

    private IndicatorEngine engine;

    private int textColorPrimary;
    private int textColorSecondary;

    private Disposable textColorPrimarySubscription;
    private Disposable textColorSecondarySubscription;

    public PageIndicatorView(Context context) {
        this(context, null);
    }

    public PageIndicatorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PageIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        engine = new IndicatorEngine();

        engine.onInitEngine(this, context);
        size = 2;

        subscribe();
    }

    @Override
    public void subscribe() {
        textColorPrimarySubscription = Aesthetic.Companion.get()
                .textColorPrimary()
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        textColorPrimary = integer;
                        engine.updateTextColors(PageIndicatorView.this);
                        invalidate();
                    }
                });

        textColorSecondarySubscription = Aesthetic.Companion.get()
                .textColorSecondary()
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        textColorSecondary = integer;
                        engine.updateTextColors(PageIndicatorView.this);
                        invalidate();
                    }
                });
    }

    @Override
    public void unsubscribe() {
        textColorPrimarySubscription.dispose();
        textColorSecondarySubscription.dispose();
    }

    public int getTotalPages() {
        return size;
    }

    public int getActualPosition() {
        return actualPosition;
    }

    public float getPositionOffset() {
        return offset;
    }

    public void notifyNumberPagesChanged() {
        size = viewPager.getAdapter().getCount();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        engine.onDrawIndicator(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(engine.getMeasuredWidth(widthMeasureSpec, heightMeasureSpec), engine.getMeasuredHeight(widthMeasureSpec, heightMeasureSpec));
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        actualPosition = position;
        offset = positionOffset;
        invalidate();
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    /**
     * You must call this AFTER setting the Adapter for the ViewPager, or it won't display the right amount of points.
     *
     * @param viewPager
     */
    public void setViewPager(ViewPager viewPager) {
        this.viewPager = viewPager;
        viewPager.addOnPageChangeListener(this);
        size = viewPager.getAdapter().getCount();
        invalidate();
    }

    private static class IndicatorEngine {

        private Context context;

        private PageIndicatorView indicator;

        private Paint selectedPaint;
        private Paint unselectedPaint;

        public int getMeasuredHeight(int widthMeasuredSpec, int heightMeasuredSpec) {
            return DimenUtils.dpToPx(8);
        }

        public int getMeasuredWidth(int widthMeasuredSpec, int heightMeasuredSpec) {
            return DimenUtils.dpToPx(8 * (indicator.getTotalPages() * 2 - 1));
        }

        public void onInitEngine(PageIndicatorView indicator, Context context) {
            this.indicator = indicator;
            this.context = context;

            selectedPaint = new Paint();
            unselectedPaint = new Paint();

            selectedPaint.setColor(indicator.textColorPrimary);
            unselectedPaint.setColor(indicator.textColorSecondary);
            selectedPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
            unselectedPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        }

        public void updateTextColors(PageIndicatorView indicator) {
            selectedPaint.setColor(indicator.textColorPrimary);
            unselectedPaint.setColor(indicator.textColorSecondary);
        }

        public void onDrawIndicator(Canvas canvas) {
            int height = indicator.getHeight();

            for (int i = 0; i < indicator.getTotalPages(); i++) {
                int x = DimenUtils.dpToPx(4) + DimenUtils.dpToPx(16 * i);
                canvas.drawCircle(x, height / 2, DimenUtils.dpToPx(4), unselectedPaint);
            }

            int firstX;
            int secondX;

            firstX = DimenUtils.dpToPx(4 + indicator.getActualPosition() * 16);

            if (indicator.getPositionOffset() > .5f) {
                firstX += DimenUtils.dpToPx(16 * (indicator.getPositionOffset() - .5f) * 2);
            }

            secondX = DimenUtils.dpToPx(4 + indicator.getActualPosition() * 16);

            if (indicator.getPositionOffset() < .5f) {
                secondX += DimenUtils.dpToPx(16 * indicator.getPositionOffset() * 2);
            } else {
                secondX += DimenUtils.dpToPx(16);
            }

            canvas.drawCircle(firstX, DimenUtils.dpToPx(4), DimenUtils.dpToPx(4), selectedPaint);
            canvas.drawCircle(secondX, DimenUtils.dpToPx(4), DimenUtils.dpToPx(4), selectedPaint);
            canvas.drawRect(firstX, 0, secondX, DimenUtils.dpToPx(8), selectedPaint);
        }
    }
}