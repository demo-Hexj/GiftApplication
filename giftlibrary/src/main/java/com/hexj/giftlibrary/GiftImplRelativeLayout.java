package com.hexj.giftlibrary;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import java.util.List;

public class GiftImplRelativeLayout extends RelativeLayout implements GiftImpl {
    private GiftImplDelegate giftDelegate;

    public GiftImplRelativeLayout(Context context) {
        this(context, null);
    }

    public GiftImplRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GiftImplRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        giftDelegate = new GiftImplDelegate(this);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        giftDelegate.initSize();
    }

    @Override
    public void setAnimMultiple(float multiple) {
        giftDelegate.setAnimMultiple(multiple);
    }

    @Override
    public void initStartSize(int width, int height) {
        giftDelegate.initStartSize(width, height);
    }

    @Override
    public void initLocationOfParent(float horizontalLocation, float verticalLocation) {
        giftDelegate.initLocationOfParent(horizontalLocation,verticalLocation);
    }

    @Override
    public void initAnimDuration(long duration) {
        giftDelegate.initAnimDuration(duration);
    }

    @Override
    public void initGiftInterval(long interval) {
        giftDelegate.initGiftInterval(interval);
    }

    @Override
    public void giving(GiftByView gift) {
        giftDelegate.giving(gift);
    }

    @Override
    public void giving(List<GiftByView> giftList) {
        giftDelegate.giving(giftList);
    }

    @Override
    public void givingByOne(List<GiftByView> giftList) {
        giftDelegate.givingByOne(giftList);
    }
}
