package ysg.gdcp.cn.swipelayout.manager;

import ysg.gdcp.cn.swipelayout.SwipeLayout;

/**
 * Created by Administrator on 2017/3/23 20:38.
 *
 * @author ysg
 */

public class SwipeLayoutManager {
    private SwipeLayoutManager() {
    }

    private static SwipeLayoutManager mInstance = new SwipeLayoutManager();

    public static SwipeLayoutManager getSwipeLayoutManager() {
        return mInstance;
    }

    private SwipeLayout currentLayout;

    public void setSwipeLayout(SwipeLayout layout) {
        this.currentLayout = layout;
    }

    public void  clearCurrentLayout(){
        currentLayout=null;
    }

    /**
     * 关闭已经打开的layout
     */
    public void closeCurrent() {
        if (currentLayout != null) {
            currentLayout.close();
        }
    }
    /**
     * 判断当前是否滑动，如果没有打开，则可滑
     * 如果有打开 则判断打开的Layout和当前的是否同一个
     *
     * @param swipeLayout
     * @return
     */
    public boolean isShouldSwipe(SwipeLayout swipeLayout) {
        if (currentLayout == null) {
            return true;
        } else {
            return swipeLayout == currentLayout;
        }
    }



}
