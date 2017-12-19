package me.toptas.fancyshowcasesample;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.toptas.fancyshowcase.FancyShowCaseQueue;
import me.toptas.fancyshowcase.FancyShowCaseView;
import me.toptas.fancyshowcase.OnViewInflateListener;

public class CustomQueueActivity extends BaseActivity {

    @BindView(R.id.btn_queue_1)
    Button mButton1;
    @BindView(R.id.btn_queue_2)
    Button mButton2;
    @BindView(R.id.btn_queue_3)
    Button mButton3;

    private FancyShowCaseQueue fancyShowCaseQueue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);
        ButterKnife.bind(this);

        final FancyShowCaseView fancyShowCaseView1 = new FancyShowCaseView.Builder(this)
                .title("First Queue Item")
                .focusOn(mButton1)
                .customView(R.layout.layout_my_custom_view, new OnViewInflateListener() {
                    @Override
                    public void onViewInflated(@NonNull View view) {
                        view.findViewById(R.id.btn_action_1).setOnClickListener(mClickListener);
                    }
                })
                .closeOnTouch(false)
                .build();

        final FancyShowCaseView fancyShowCaseView2 = new FancyShowCaseView.Builder(this)
                .title("Second Queue Item")
                .focusOn(mButton2)
                .customView(R.layout.layout_my_custom_view, new OnViewInflateListener() {
                    @Override
                    public void onViewInflated(@NonNull View view) {
                        view.findViewById(R.id.btn_action_1).setOnClickListener(mClickListener);
                    }
                })
                .closeOnTouch(false)
                .build();

        final FancyShowCaseView fancyShowCaseView3 = new FancyShowCaseView.Builder(this)
                .title("Third Queue Item")
                .focusOn(mButton3)
                .customView(R.layout.layout_my_custom_view, new OnViewInflateListener() {
                    @Override
                    public void onViewInflated(@NonNull View view) {
                        view.findViewById(R.id.btn_action_1).setOnClickListener(mClickListener);
                    }
                })
                .closeOnTouch(false)
                .build();

        fancyShowCaseQueue = new FancyShowCaseQueue()
                .add(fancyShowCaseView1)
                .add(fancyShowCaseView2)
                .add(fancyShowCaseView3);

        fancyShowCaseQueue.show();
    }


    View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.d("", "onClick: ");
            fancyShowCaseQueue.getCurrent().hide();
        }
    };

}
