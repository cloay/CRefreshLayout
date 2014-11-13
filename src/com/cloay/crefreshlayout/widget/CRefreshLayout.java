package com.cloay.crefreshlayout.widget;

import com.cloay.crefreshlayout.widget.CRefreshView.CRefreshLayoutState;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;
import android.widget.AbsListView;

public class CRefreshLayout extends ViewGroup{
	private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;
    private static final int DRAG_MAX_DISTANCE = 84;
    private static final int INVALID_POINTER = -1;
    private static final float DRAG_RATE = .8f;

    public static final int MODE_TOP = 0;
    public static final int MODE_BOTTOM = 1;

    private View mTarget;
    private CRefreshView mRefreshView;
    private Interpolator mDecelerateInterpolator;
    private int mTouchSlop;
    private int mMediumAnimationDuration;
    private int mSpinnerFinalOffset;
    private int mTotalDragDistance;
    private int mCurrentOffsetTop;
    private boolean mRefreshing;
    private int mActivePointerId;
    private boolean mIsBeingDragged;
    private float mInitialMotionY;
    private int mFrom;
    private boolean mNotify;
    private OnRefreshListener mListener;
    public CRefreshLayout(Context context) {
		super(context);
		init(context);
	}
	
	public CRefreshLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	public CRefreshLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	
	public void init(Context context){
        mDecelerateInterpolator = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mMediumAnimationDuration = getResources().getInteger(
                android.R.integer.config_mediumAnimTime);;
        mSpinnerFinalOffset = mTotalDragDistance = dp2px(DRAG_MAX_DISTANCE);

        mRefreshView = new CRefreshView(context);
        addView(mRefreshView);
        setWillNotDraw(false);
	}
	
	public int getFinalOffset(){
        return mSpinnerFinalOffset;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        ensureTarget();
        if (mTarget == null)
            return;

        widthMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth() - getPaddingRight() - getPaddingLeft(), MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY);
        mTarget.measure(widthMeasureSpec, heightMeasureSpec);
        mRefreshView.measure(widthMeasureSpec, heightMeasureSpec);
//        mRefreshView.measure(MeasureSpec.makeMeasureSpec(mRefreshViewWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(mRefreshViewHeight, MeasureSpec.EXACTLY));
    }

    private void ensureTarget() {
        if (mTarget != null)
            return;
        if (getChildCount() > 0) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (child != mRefreshView)
                    mTarget = child;
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        if (!isEnabled() || canChildScrollUp() || mRefreshing) {
            return false;
        }

        final int action = MotionEventCompat.getActionMasked(ev);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                setTargetOffsetTop(0, true);
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                mIsBeingDragged = false;
                final float initialMotionY = getMotionEventY(ev, mActivePointerId);
                if (initialMotionY == -1) {
                    return false;
                }
                mInitialMotionY = initialMotionY;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mActivePointerId == INVALID_POINTER) {
                    return false;
                }
                final float y = getMotionEventY(ev, mActivePointerId);
                if (y == -1) {
                    return false;
                }
                final float yDiff = y - mInitialMotionY;
//                if (Math.abs(yDiff) > mTouchSlop && !mIsBeingDragged) {
//                    mIsBeingDragged = true;
//                    mMode = yDiff > 0 ? MODE_TOP : MODE_BOTTOM;
//                }
                if (yDiff > mTouchSlop && !mIsBeingDragged) {
                    mIsBeingDragged = true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsBeingDragged = false;
                mActivePointerId = INVALID_POINTER;
                break;
            case MotionEventCompat.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;
        }

        return mIsBeingDragged;
    }

    @SuppressLint("ClickableViewAccessibility")
	@Override
    public boolean onTouchEvent(MotionEvent ev) {

        if (!mIsBeingDragged) {
            return super.onTouchEvent(ev);
        }

        final int action = MotionEventCompat.getActionMasked(ev);

        switch (action) {
            case MotionEvent.ACTION_MOVE: {
                final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                if (pointerIndex < 0) {
                    return false;
                }

                final float y = MotionEventCompat.getY(ev, pointerIndex);
//                final float yDiff = Math.abs(y - mInitialMotionY);
                final float yDiff = y - mInitialMotionY;
                final float scrollTop = yDiff * DRAG_RATE;
                float originalDragPercent = scrollTop / mTotalDragDistance;
                if (originalDragPercent < 0) {
                    return false;
                }
                float dragPercent = Math.min(1f, Math.max(0f, Math.abs(originalDragPercent)));
//                float adjustedPercent = (float) Math.max(dragPercent - .4, 0) * 5 / 3;
//                    float adjustedPercent = dragPercent;
                float extraOS = Math.abs(scrollTop) - mTotalDragDistance;
                float slingshotDist = mSpinnerFinalOffset;
                float tensionSlingshotPercent = Math.max(0,
                        Math.min(extraOS, slingshotDist * 2) / slingshotDist);
                float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math.pow(
                        (tensionSlingshotPercent / 4), 2)) * 2f;
                float extraMove = (slingshotDist) * tensionPercent * 2;
                int targetY = (int) ((slingshotDist * dragPercent) + extraMove);
                if (scrollTop < mTotalDragDistance) {
                	if(mRefreshView.state == CRefreshLayoutState.CRefreshLayoutStateIdle){
                		mRefreshView.updateBarItemsWithProgress(dragPercent);
                	}
                }
                setTargetOffsetTop(targetY - mCurrentOffsetTop, true);
                break;
            }
            case MotionEventCompat.ACTION_POINTER_DOWN:
                final int index = MotionEventCompat.getActionIndex(ev);
                mActivePointerId = MotionEventCompat.getPointerId(ev, index);
                break;
            case MotionEventCompat.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                if (mActivePointerId == INVALID_POINTER) {
                    return false;
                }
                final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                final float y = MotionEventCompat.getY(ev, pointerIndex);
                final float overscrollTop = (y - mInitialMotionY) * DRAG_RATE;
                mIsBeingDragged = false;
                if (overscrollTop > mTotalDragDistance) {
                	mRefreshView.state = CRefreshLayoutState.CRefreshLayoutStateRefreshing;
                    setRefreshing(true, true);
                } else {
                    mRefreshing = false;
                    final float yDiff = y - mInitialMotionY;
                    final float scrollTop = yDiff * DRAG_RATE;
                    float originalDragPercent = scrollTop / mTotalDragDistance;
                    if (originalDragPercent < 0) {
                        return false;
                    }
                    float dragPercent = Math.min(1f, Math.abs(originalDragPercent));
                    mRefreshView.setDisappearProgress(dragPercent);
                    mRefreshView.updateDisappearProgress();
                    animateOffsetToStartPosition();
                }
                mActivePointerId = INVALID_POINTER;
                return false;
            }
        }

        return true;
    }


    private void animateOffsetToStartPosition() {
        mFrom = mCurrentOffsetTop;
        mAnimateToStartPosition.reset();
        mAnimateToStartPosition.setDuration(mMediumAnimationDuration);
        mAnimateToStartPosition.setInterpolator(mDecelerateInterpolator);
        mAnimateToStartPosition.setAnimationListener(mToStartListener);
        mRefreshView.clearAnimation();
        mRefreshView.startAnimation(mAnimateToStartPosition);
    }

    private void animateOffsetToCorrectPosition() {
        mFrom = mCurrentOffsetTop;
        mAnimateToCorrectPosition.reset();
        mAnimateToCorrectPosition.setDuration(mMediumAnimationDuration);
        mAnimateToCorrectPosition.setInterpolator(mDecelerateInterpolator);
        mAnimateToCorrectPosition.setAnimationListener(mRefreshListener);
        mRefreshView.clearAnimation();
        mRefreshView.startAnimation(mAnimateToCorrectPosition);
    }

    private final Animation mAnimateToStartPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            moveToStart(interpolatedTime);
        }
    };

    private final Animation mAnimateToCorrectPosition = new Animation() {
        @Override
        public void applyTransformation(float interpolatedTime, Transformation t) {
            int targetTop = 0;
            int endTarget = (int) mSpinnerFinalOffset;
            targetTop = (mFrom + (int) ((endTarget - mFrom) * interpolatedTime));
            int offset = targetTop - mTarget.getTop();
            setTargetOffsetTop(offset, false /* requires update */);
        }
    };

    private void moveToStart(float interpolatedTime) {
        int targetTop = mFrom - (int) (mFrom * interpolatedTime);
        int offset = targetTop - mTarget.getTop();
        setTargetOffsetTop(offset, false);
    }

    public void setRefreshing(boolean refreshing) {
        if (mRefreshing != refreshing) {
            setRefreshing(refreshing, false /* notify */);
        }
    }

    private void setRefreshing(boolean refreshing, final boolean notify) {
        if (mRefreshing != refreshing) {
            mNotify = notify;
            ensureTarget();
            mRefreshing = refreshing;
            if (mRefreshing) {
                animateOffsetToCorrectPosition();
            } else {
            	mRefreshView.finishingLoading();
                animateOffsetToStartPosition();
            }
        }
    }

    private Animation.AnimationListener mRefreshListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
        	if(mRefreshing)
        		mRefreshView.startLoadingAnimation();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (mRefreshing) {
                if (mNotify) {
                    if (mListener != null) {
                        mListener.onRefresh();
                    }
                }
            } else {
                animateOffsetToStartPosition();
            }
            mCurrentOffsetTop = mTarget.getTop();
        }
    };

    private Animation.AnimationListener mToStartListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
        	
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
        	mRefreshView.state = CRefreshLayoutState.CRefreshLayoutStateIdle;
            mCurrentOffsetTop = mTarget.getTop();
        }
    };

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = MotionEventCompat.getActionIndex(ev);
        final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
        if (pointerId == mActivePointerId) {
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
        }
    }

    private float getMotionEventY(MotionEvent ev, int activePointerId) {
        final int index = MotionEventCompat.findPointerIndex(ev, activePointerId);
        if (index < 0) {
            return -1;
        }
        return MotionEventCompat.getY(ev, index);
    }

    private void setTargetOffsetTop(int offset, boolean requiresUpdate) {
        mRefreshView.bringToFront();
        mRefreshView.offsetTopAndBottom(offset/6);
        mTarget.offsetTopAndBottom(offset);
        mCurrentOffsetTop = mTarget.getTop();
        if (requiresUpdate && android.os.Build.VERSION.SDK_INT < 11) {
            invalidate();
        }
    }

    private boolean canChildScrollUp() {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (mTarget instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mTarget;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return mTarget.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mTarget, -1);
        }
    }

//    private boolean canChildScrollDown() {
//        if (android.os.Build.VERSION.SDK_INT < 14) {
//            if (mTarget instanceof AbsListView) {
//                final AbsListView absListView = (AbsListView) mTarget;
//                return absListView.getChildCount() > 0
//                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
//                        .getTop() < absListView.getPaddingTop());
//            } else {
//                return mTarget.getScrollY() > 0;
//            }
//        } else {
//            return ViewCompat.canScrollVertically(mTarget, -1);
//        }
//    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        ensureTarget();
        if (mTarget == null)
            return;
        int height = getMeasuredHeight();
        int width = getMeasuredWidth();
        int left = getPaddingLeft();
        int top = getPaddingTop();
        int right = getPaddingRight();
        int bottom = getPaddingBottom();

        mTarget.layout(left, top + mCurrentOffsetTop, left + width - right, top + height - bottom + mCurrentOffsetTop);
//        mRefreshView.layout(width / 2 - mRefreshViewWidth / 2, -mRefreshViewHeight + mCurrentOffsetTop, width / 2 + mRefreshViewHeight / 2, mCurrentOffsetTop);
        mRefreshView.layout(left, top, left + width - right, top + height - bottom);
        Log.v("CRefreshLayout", "left=" + left + " width=" + width + " height=" + height);
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        mListener = listener;
    }

    public static interface OnRefreshListener {
        public void onRefresh();
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getContext().getResources().getDisplayMetrics());
    }
}
