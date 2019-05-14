package com.hexj.giftlibrary;

public class GiftByView {
    /**
     * * @param startViewId    起始viewid
     * * @param targetViewId   目标viewid
     * * @param giftResource 礼物图片，由于initStartSize的大小为giftView初始化的状态的大小
     * *                       所以礼物图片的大小应设置为：initStartSize * setAnimMultiple的大小才能保证不失真
     */
    private int startViewId;
    private int targetViewId;
    private Object giftResource;

    /**
     * gift展示的时间
     */
    private long showAtTime;

    public GiftByView() {
    }

    public GiftByView(int startViewId, int targetViewId, Object giftResource) {
        this.startViewId = startViewId;
        this.targetViewId = targetViewId;
        this.giftResource = giftResource;
    }

    public int getStartViewId() {
        return startViewId;
    }

    public void setStartViewId(int startViewId) {
        this.startViewId = startViewId;
    }

    public int getTargetViewId() {
        return targetViewId;
    }

    public void setTargetViewId(int targetViewId) {
        this.targetViewId = targetViewId;
    }

    public Object getGiftResource() {
        return giftResource;
    }

    public void setGiftResource(Object giftResource) {
        this.giftResource = giftResource;
    }

    public long getShowAtTime() {
        return showAtTime;
    }

    public void setShowAtTime(long showAtTime) {
        this.showAtTime = showAtTime;
    }

    @Override
    public String toString() {
        return "GiftByView{" +
                "startViewId=" + startViewId +
                ", targetViewId=" + targetViewId +
                ", giftResource=" + giftResource +
                ", showAtTime=" + showAtTime +
                '}';
    }
}
