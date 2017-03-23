package ysg.gdcp.cn.swipelayout;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import ysg.gdcp.cn.swipelayout.manager.SwipeLayoutManager;

/**
 * Created by Administrator on 2017/3/23 16:09.
 * 这是滑动删除的自定义控件，此自定义控件有两个子View
 *
 * @author ysg
 */

public class SwipeLayout extends FrameLayout {

    private View contentView;//内容区域
    private View deleteView; //删除区域
    private int contentWidth; //删内容区域宽度
    private int measuredHeight; //内容和删除区域宽度
    private int deleteWidth;  //删除区域宽度
    private ViewDragHelper viewDragHelper;
    private OnSwipeStateChangeListener listener;

    public SwipeLayout(Context context) {
        super(context);
        init();
    }

    public SwipeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SwipeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    enum SwipeState {
        Open, Close;
    }

    private SwipeState currentState = SwipeState.Close;

    private void init() {
        viewDragHelper = ViewDragHelper.create(this, callback);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 2) {
            throw new IllegalArgumentException("此自定义控件只含有两个子View");
        }
        contentView = getChildAt(0);
        deleteView = getChildAt(1);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        measuredHeight = contentView.getMeasuredHeight();
        contentWidth = contentView.getMeasuredWidth();
        deleteWidth = deleteView.getMeasuredWidth();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        contentView.layout(0, 0, contentWidth, measuredHeight);
        deleteView.layout(contentView.getRight(), 0, contentView.getRight() + deleteWidth, measuredHeight);
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean result = viewDragHelper.shouldInterceptTouchEvent(ev);
        //如果又打开，就需要拦截，交给onTouch处理
        if (!SwipeLayoutManager.getSwipeLayoutManager().isShouldSwipe(this)) {
            SwipeLayoutManager.getSwipeLayoutManager().closeCurrent();
            result= true;
        }
        return result;
    }

    private float dx, dy;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!SwipeLayoutManager.getSwipeLayoutManager().isShouldSwipe(this)) {
            requestDisallowInterceptTouchEvent(true);
            return true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                dx = event.getX();
                dy = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float mX = event.getX();
                float mY = event.getY();
                float delatX = mX - dx;
                float delatY = mY - dy;
                if (Math.abs(delatX) > Math.abs(delatY)) {
                    requestDisallowInterceptTouchEvent(true);
                }
                dx = mX;
                dy = mY;
                break;
            case MotionEvent.ACTION_UP:
                break;

        }
        viewDragHelper.processTouchEvent(event);
        return true;
    }

    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == contentView || child == deleteView;
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return deleteWidth;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (child == contentView) {
                if (left > 0) {
                    left = 0;
                } else if (left < -deleteWidth) {
                    left = -deleteWidth;
                }

            }
            if (child == deleteView) {
                if (left > contentWidth) {
                    left = contentWidth;
                } else if (left < contentWidth - deleteWidth) {
                    left = contentWidth - deleteWidth;
                }
            }
            return left;
        }


        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            if (changedView == contentView) {
                deleteView.layout(deleteView.getLeft() + dx, deleteView.getTop() + dy, deleteView.getRight() + dx, deleteView.getBottom() + dy);
            }
            if (changedView == deleteView) {
                contentView.layout(contentView.getLeft() + dx, contentView.getTop() + dy, contentView.getRight() + dx, contentView.getBottom() + dy);
            }

            if (contentView.getLeft() == 0 && currentState != SwipeState.Close) {
                currentState = SwipeState.Close;
                if (listener!=null){
                    listener.onClose(getTag());
                }
                SwipeLayoutManager.getSwipeLayoutManager().clearCurrentLayout();
            } else if (contentView.getLeft() == -deleteWidth && currentState != SwipeState.Open) {
                currentState = SwipeState.Open;
                if (listener!=null){
                listener.onOpen(getTag());
                }
                //记录一下已打开的的Layout
                SwipeLayoutManager.getSwipeLayoutManager().setSwipeLayout(SwipeLayout.this);
            }
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            if (contentView.getLeft() < -deleteWidth / 2) {
                //打开
                open();
            } else {
                //关闭
                close();
            }
        }
    };

    public void close() {
        viewDragHelper.smoothSlideViewTo(contentView, 0, contentView.getTop());
        ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);
    }

    public void open() {
        viewDragHelper.smoothSlideViewTo(contentView, -deleteWidth, contentView.getTop());
        ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);
    }

    @Override
    public void computeScroll() {
        if (viewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void setOnSwipeStateChangeListener(OnSwipeStateChangeListener listener) {
        this.listener = listener;
    }

    public interface OnSwipeStateChangeListener {
        void onOpen(Object tag);

        void onClose(Object tag);
    }
}
