package me.toptas.fancyshowcasesample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.toptas.fancyshowcase.FancyShowCaseQueue;
import me.toptas.fancyshowcase.FancyShowCaseView;

/**
 * Created by ftoptas on 27/03/17.
 */

public class QueueActivity extends BaseActivity {


    @BindView(R.id.btn_queue_1)
    Button mButton1;
    @BindView(R.id.btn_queue_2)
    Button mButton2;
    @BindView(R.id.btn_queue_3)
    Button mButton3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);
        ButterKnife.bind(this);

        final FancyShowCaseView fancyShowCaseView1 = new FancyShowCaseView.Builder(this)
                .title("First Queue Item")
                .focusOn(mButton1)
                .build();

        final FancyShowCaseView fancyShowCaseView2 = new FancyShowCaseView.Builder(this)
                .title("Second Queue Item")
                .focusOn(mButton2)
                .build();

        final FancyShowCaseView fancyShowCaseView3 = new FancyShowCaseView.Builder(this)
                .title("Third Queue Item")
                .focusOn(mButton3)
                .build();

        new FancyShowCaseQueue()
                .add(fancyShowCaseView1)
                .add(fancyShowCaseView2)
                .add(fancyShowCaseView3)
                .show();
    }
}
