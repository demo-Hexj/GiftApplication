package com.hexj.giftlibrary;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Handler;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class GiftImplDelegate implements GiftImpl {

    private final Handler mHandler;
    private int parentViewX;
    private int parentViewY;
    private float multiple = 2f;
    private BlockingQueue<ImageView> giftViewQueue = new LinkedBlockingQueue<>(30);
    private HashMap<Integer, View> cacheViewMap = new HashMap<>(10);

    /**
     * 所有礼物的队列
     */
    private LinkedList<GiftByView> giftByViewQueue = new LinkedList<>();

    /**
     * 是否正在循环
     */
    private boolean isLooping = false;

    /**
     * 父容器
     */
    private final RelativeLayout parentView;

    /**
     * 父容器宽
     */
    private int parentWidth;

    /**
     * 父容器高
     */
    private int parentHeight;

    /**
     * 礼物view的大小
     */
    private int giftViewHeight = -1;
    private int giftViewWidth = -1;

    /**
     * 是否调用了initStartSize()
     */
    private boolean giftViewSizeIsInitialized = false;

    /**
     * 礼物的中转位置
     */
    private float verticalLocation = 0.5f;
    private float horizontalLocation = 0.5f;

    /**
     * 礼物动画时长
     */
    private long animDuration = 800;

    /**
     * 连续赠送礼物时，两个礼物之间的时间间隔
     */
    private long giftInterval = 80;

    public GiftImplDelegate(RelativeLayout parentView) {
        this.parentView = parentView;
        if (parentView == null) {
            throw new NullPointerException("parentView cannot be null!");
        }
        mHandler = new Handler();
    }

    public void initSize() {
        this.parentWidth = parentView.getMeasuredWidth();
        this.parentHeight = parentView.getMeasuredHeight();
    }

    @Override
    public void setAnimMultiple(float multiple) {
        this.multiple = multiple;
    }

    @Override
    public void initStartSize(int width, int height) {
        giftViewSizeIsInitialized = true;
        this.giftViewWidth = width;
        this.giftViewHeight = height;
    }

    @Override
    public void initLocationOfParent(float horizontalLocation,float verticalLocation) {
        this.horizontalLocation = horizontalLocation < 0 ? 0 : horizontalLocation > 1 ? 1 : horizontalLocation;
        this.verticalLocation = verticalLocation < 0 ? 0 : verticalLocation > 1 ? 1 : verticalLocation;
    }


    @Override
    public void initAnimDuration(long duration) {
        this.animDuration = duration;
    }

    @Override
    public void initGiftInterval(long interval) {
        this.giftInterval = interval;
    }

    @Override
    public void giving(GiftByView gift) {
        GiftByView lastGiftByQueue = null;
        if (giftByViewQueue.size() > 0) {
            lastGiftByQueue = giftByViewQueue.getLast();
        }
        if (lastGiftByQueue != null) {
            gift.setShowAtTime(lastGiftByQueue.getShowAtTime() + giftInterval);
        } else {
            gift.setShowAtTime(0);
        }
        giftByViewQueue.offer(gift);
        loop();
    }

    @Override
    public void giving(List<GiftByView> giftList) {
        for (int i = 0; i < giftList.size(); i++) {
            GiftByView gift = giftList.get(i);
            GiftByView lastGiftByQueue = null;
            if (giftByViewQueue.size() > 0) {
                lastGiftByQueue = giftByViewQueue.getLast();
            }
            if (lastGiftByQueue != null) {
                gift.setShowAtTime(lastGiftByQueue.getShowAtTime() + giftInterval);
            } else {
                gift.setShowAtTime(0);
            }
            giftByViewQueue.offer(gift);
        }
        loop();
    }


    private void loop() {
        if (isLooping) {
            return;
        }

        for (; ; ) {
            isLooping = true;
            final GiftByView gift = giftByViewQueue.poll();
            if (gift == null) {
                isLooping = false;
                break;
            }
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showGift(gift);
                }
            }, gift.getShowAtTime());
        }
    }

    private void showGift(GiftByView gift) {
        //这里优先从缓存中取，尽量减少findViewById次数
        View startView = cacheViewMap.get(gift.getStartViewId());
        View targetView = cacheViewMap.get(gift.getTargetViewId());
        if (startView == null) {
            startView = parentView.findViewById(gift.getStartViewId());
            cacheViewMap.put(gift.getStartViewId(), startView);
        }
        if (targetView == null) {
            targetView = parentView.findViewById(gift.getTargetViewId());
            cacheViewMap.put(gift.getTargetViewId(), targetView);
        }

        int[] location = new int[2];
        if (parentViewX <= 0) {
            parentView.getLocationOnScreen(location);//获取在整个屏幕内的绝对坐标
            parentViewX = location[0];
            parentViewY = location[1];
        }

        startView.getLocationOnScreen(location);//获取在整个屏幕内的绝对坐标
        final int startViewX = location[0];
        final int startViewY = location[1];
        targetView.getLocationOnScreen(location);//获取在整个屏幕内的绝对坐标
        final int targetViewX = location[0];
        final int targetViewY = location[1];

        final int startExcursionX = (startView.getMeasuredWidth() - giftViewWidth) / 2;
        final int startExcursionY = (startView.getMeasuredHeight() - giftViewHeight) / 2;
        final int targetExcursionX = (targetView.getMeasuredWidth() - giftViewWidth) / 2;
        final int targetExcursionY = (targetView.getMeasuredHeight() - giftViewHeight) / 2;


        //这里优先从缓存中去giftView,尽量减少new giftView次数
        ImageView giftView = giftViewQueue.poll();
        RelativeLayout.LayoutParams lp;
        //如果初始化了giftView的大小，这里就用初始化的大小，如果没有初始化大小就用包裹模式
        //采用包裹模式时，由于后面有view放大的动画过程，可能会导致图片失真情况，所以尽量初始化giftView的大小
        if (giftViewSizeIsInitialized) {
            lp = new RelativeLayout.LayoutParams(giftViewWidth, giftViewHeight);
        } else {
            lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        }
        if (giftView == null) {
            giftView = new ImageView(parentView.getContext());
            if (gift.getStartViewId() < 0) {
                throw new NullPointerException("startView cannot be null!");
            } else {
                lp.leftMargin = startViewX - parentViewX + startExcursionX;
                lp.topMargin = startViewY - parentViewY + startExcursionY;

                parentView.addView(giftView, lp);
                Glide.with(parentView.getContext()).load(gift.getGiftResource()).into(giftView);
                //如果没有初始化giftView的大小，则在这里计算
                if (!giftViewSizeIsInitialized) {
                    int width = View.MeasureSpec.makeMeasureSpec((1 << 30) - 1, View.MeasureSpec.AT_MOST);
                    int height = View.MeasureSpec.makeMeasureSpec((1 << 30) - 1, View.MeasureSpec.AT_MOST);
                    giftView.measure(width, height);
                    giftViewHeight = giftView.getMeasuredHeight();
                    giftViewWidth = giftView.getMeasuredWidth();
                }
            }
        } else {
            if (gift.getStartViewId() < 0) {
                throw new NullPointerException("startView cannot be null!");
            } else {
                lp.leftMargin = startViewX - parentViewX + startExcursionX;
                lp.topMargin = startViewY - parentViewY + startExcursionY;
            }
            giftView.setLayoutParams(lp);
            parentView.requestLayout();
            Glide.with(parentView.getContext()).load(gift.getGiftResource()).into(giftView);
        }

        //以下是动画过程
        final ImageView finalGiftView = giftView;
        AnimatorSet step1AnimSet = new AnimatorSet();
        ObjectAnimator[] items1 = new ObjectAnimator[]{
                ObjectAnimator.ofFloat(finalGiftView, "scaleX", 1, multiple),
                ObjectAnimator.ofFloat(finalGiftView, "scaleY", 1, multiple),
                ObjectAnimator.ofFloat(finalGiftView, "translationX", parentWidth * horizontalLocation - lp.leftMargin - giftViewWidth / 2),
                ObjectAnimator.ofFloat(finalGiftView, "translationY", parentHeight * verticalLocation - lp.topMargin - giftViewHeight / 2)};
        step1AnimSet.playTogether(items1);
        step1AnimSet.setDuration(animDuration / 3);

        AnimatorSet step2AnimSet = new AnimatorSet();
        ObjectAnimator[] items2 = new ObjectAnimator[]{
                ObjectAnimator.ofFloat(finalGiftView, "scaleX", multiple, 1),
                ObjectAnimator.ofFloat(finalGiftView, "scaleY", multiple, 1),
                ObjectAnimator.ofFloat(finalGiftView, "translationX", targetViewX - startViewX + (targetExcursionX - startExcursionX)),
                ObjectAnimator.ofFloat(finalGiftView, "translationY", targetViewY - startViewY + (targetExcursionY - startExcursionY))};
        step2AnimSet.playTogether(items2);
        step2AnimSet.setDuration(animDuration / 3);

        AnimatorSet step3AnimSet = new AnimatorSet();
        ObjectAnimator[] items3 = new ObjectAnimator[]{
                ObjectAnimator.ofFloat(finalGiftView, "scaleX", 1, 0.3f, 0.5f, 0),
                ObjectAnimator.ofFloat(finalGiftView, "scaleY", 1, 0.3f, 0.5f, 0)};
        step3AnimSet.playTogether(items3);
        step3AnimSet.setDuration(animDuration / 3);

        AnimatorSet animSet = new AnimatorSet();
        animSet.playSequentially(step1AnimSet, step2AnimSet, step3AnimSet);
        animSet.setInterpolator(new LinearInterpolator());
        animSet.start();

        animSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!giftViewQueue.offer(finalGiftView)) {
                    parentView.removeView(finalGiftView);
                } else {
                    finalGiftView.setTranslationX(0);
                    finalGiftView.setTranslationY(0);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }
}
