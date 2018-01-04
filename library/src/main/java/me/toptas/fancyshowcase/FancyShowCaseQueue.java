package me.toptas.fancyshowcase;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Handles queues of {@link FancyShowCaseView} so that they are shown one after another
 * takes care to skip views that should not be shown because of their one shot id
 */
public class FancyShowCaseQueue implements DismissListener {

    private Queue<FancyShowCaseView> mQueue;
    private DismissListener mCurrentOriginalDismissListener;
    private FancyShowCaseView mCurrent;
    private OnCompleteListener mCompleteListener;

    /**
     * Constructor
     */
    public FancyShowCaseQueue() {
        mQueue = new LinkedList<>();
        mCurrentOriginalDismissListener = null;
    }

    /**
     * Adds a FancyShowCaseView to the queue
     *
     * @param showCaseView the view that should be added to the queue
     * @return Builder
     */
    public FancyShowCaseQueue add(FancyShowCaseView showCaseView) {
        mQueue.add(showCaseView);
        return this;
    }

    /**
     * Starts displaying all views in order of their insertion in the queue, one after another
     */
    public void show() {
        if (!mQueue.isEmpty()) {
            mCurrent = mQueue.poll();
            mCurrentOriginalDismissListener = mCurrent.getDismissListener();
            mCurrent.setDismissListener(this);
            mCurrent.show();
        } else if (mCompleteListener != null) {
            mCompleteListener.onComplete();
        }
    }

    /**
     * Cancels the queue
     * @param hideCurrent hides current FancyShowCaseView
     */
    public void cancel(boolean hideCurrent) {
        if (hideCurrent && mCurrent != null) {
            mCurrent.hide();
        }
        if (!mQueue.isEmpty()) {
            mQueue.clear();
        }
    }

    @Override
    public void onDismiss(String id) {
        if (mCurrentOriginalDismissListener != null) {
            mCurrentOriginalDismissListener.onDismiss(id);
        }
        show();
    }

    @Override
    public void onSkipped(String id) {
        if (mCurrentOriginalDismissListener != null) {
            mCurrentOriginalDismissListener.onSkipped(id);
        }
        show();
    }

    public void setCompleteListener(OnCompleteListener completeListener) {
        mCompleteListener = completeListener;
    }

    /**
     * Get the current item of the queue
     * @return FancyShowCaseView
     */
    public FancyShowCaseView getCurrent() {
        return mCurrent;
    }

}
