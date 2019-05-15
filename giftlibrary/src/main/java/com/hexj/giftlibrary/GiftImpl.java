package com.hexj.giftlibrary;

import java.util.List;

public interface GiftImpl {

    /**
     * 设置放大比例
     *
     * @param multiple
     */
    void setAnimMultiple(float multiple);

    /**
     * 初始化礼物图片大小，这里的大小为giftView初始化的状态的大小
     *
     * @param width  px
     * @param height px
     */
    void initStartSize(int width, int height);

    /**
     * @param horizontalLocation 横向相对于parentView的位置
     * @param verticalLocation   纵向相对于parentView的位置
     */
    void initLocationOfParent(float horizontalLocation, float verticalLocation);

    /**
     * 设置动画时长
     *
     * @param duration
     */
    void initAnimDuration(long duration);

    /**
     * 连续赠送礼物时，两个礼物之间的时间间隔
     *
     * @param interval
     */
    void initGiftInterval(long interval);

    /**
     * 赠送礼物
     */
    void giving(GiftByView gift);

    /**
     * 赠送礼物
     */
    void giving(List<GiftByView> giftList);

    /**
     * 同一个用户送出礼物，且每个接受者只能接收一个礼物
     * @param giftList
     */
    void givingByOne(List<GiftByView> giftList);
}