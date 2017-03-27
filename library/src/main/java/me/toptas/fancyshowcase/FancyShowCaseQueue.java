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

    /**
     * Constructor
     */
    public FancyShowCaseQueue() {
        mQueue = new LinkedList<>();
        mCurrentOriginalDismissListener = null;
    }

    /**
     * adds a view to the queue
     *
     * @param showCaseView the view that should be added to the queue
     * @return Builder
     */
    public FancyShowCaseQueue add(FancyShowCaseView showCaseView) {
        mQueue.add(showCaseView);
        return this;
    }

    /**
     * starts displaying all views in order of their insertion in the queue, one after another
     */
    public void show() {
        if (!mQueue.isEmpty()) {
            FancyShowCaseView view = mQueue.poll();
            mCurrentOriginalDismissListener = view.getDismissListener();
            view.setDismissListener(this);
            view.show();
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
}
