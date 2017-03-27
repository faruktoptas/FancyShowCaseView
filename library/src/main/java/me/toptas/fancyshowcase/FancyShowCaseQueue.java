package me.toptas.fancyshowcase;

import java.util.LinkedList;
import java.util.Queue;

public class FancyShowCaseQueue implements DismissListener {

    private Queue<FancyShowCaseView> mQueue;
    private DismissListener mCurrentOriginalDismissListener;

    public FancyShowCaseQueue() {
        mQueue = new LinkedList<>();
        mCurrentOriginalDismissListener = null;
    }

    public FancyShowCaseQueue add(FancyShowCaseView showCaseView) {
        mQueue.add(showCaseView);
        return this;
    }

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
