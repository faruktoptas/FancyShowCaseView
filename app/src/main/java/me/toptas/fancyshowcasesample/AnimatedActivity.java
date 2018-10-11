package me.toptas.fancyshowcasesample;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.toptas.fancyshowcase.FancyShowCaseQueue;
import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.OnViewInflateListener;


public class AnimatedActivity extends BaseActivity {

    @BindView(R.id.btn_focus)
    Button mButton;

    @BindView(R.id.btn_focus2)
    Button mButton2;


    private FancyShowCaseQueue mQueue;
    private FancyShowCaseView mFancyView, mFancyView2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animated);
        ButterKnife.bind(this);

        mFancyView = new FancyShowCaseView.Builder(this)
                .focusOn(mButton)
                .customView(R.layout.layout_animated_view, new OnViewInflateListener() {
                    @Override
                    public void onViewInflated(@NonNull View view) {
                        setAnimatedContent(view, mFancyView);
                    }
                })
                .build();

        mFancyView2 = new FancyShowCaseView.Builder(this)
                .focusOn(mButton2)
                .customView(R.layout.layout_animated_view, new OnViewInflateListener() {
                    @Override
                    public void onViewInflated(@NonNull View view) {
                        setAnimatedContent(view, mFancyView2);
                    }
                })
                .build();


    }

    private void setAnimatedContent(final View view, final FancyShowCaseView fancyShowCaseView) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                TextView tvMain = (TextView) view.findViewById(R.id.tvMain);
                final TextView tvSub = (TextView) view.findViewById(R.id.tvSub);
                TextView tvNext = (TextView) view.findViewById(R.id.btn_next);
                TextView tvDismiss = (TextView) view.findViewById(R.id.btn_dismiss);

                if (fancyShowCaseView.equals(mFancyView2)) {
                    tvMain.setText("My Fancy Title 2");
                    tvSub.setText("My fancy description can be a longer text.2");
                    tvNext.setText("Close");
                }

                tvNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fancyShowCaseView.hide();
                    }
                });

                tvDismiss.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mQueue.cancel(true);
                    }
                });

                final Animation mainAnimation = AnimationUtils.loadAnimation(AnimatedActivity.this, R.anim.slide_in_left);
                mainAnimation.setFillAfter(true);

                final Animation subAnimation = AnimationUtils.loadAnimation(AnimatedActivity.this, R.anim.slide_in_left);
                subAnimation.setFillAfter(true);
                tvMain.startAnimation(mainAnimation);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tvSub.startAnimation(subAnimation);

                    }
                }, 80);
            }
        }, 200);
    }

    @OnClick(R.id.btn_focus)
    public void focus(View view) {
        mQueue = new FancyShowCaseQueue();
        mQueue.add(mFancyView);
        mQueue.add(mFancyView2);
        mQueue.show();
    }
}
