package me.toptas.fancyshowcasesample;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.toptas.fancyshowcase.FancyShowCaseView;

public class SecondActivity extends BaseActivity {

    @BindView(R.id.button1)
    Button mButton;

    @BindView(R.id.toolbar)
    android.support.v7.widget.Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);

        mButton.performClick();
    }

    /**
     * Focus on a view
     */
    @OnClick(R.id.button1)
    public void focus() {
        if (!isFinishing()) {
            new FancyShowCaseView.Builder(SecondActivity.this)
                    .focusOn(mButton)
                    .title("Focus a view")
                    .fitSystemWindows(true)
                    .build()
                    .show();
        }
    }

    /**
     * Toggle toolbar visibility
     */
    @OnClick(R.id.button2)
    public void toggleToolbar() {
        if (mToolbar.getVisibility() == View.VISIBLE) {
            mToolbar.setVisibility(View.GONE);
        } else {
            mToolbar.setVisibility(View.VISIBLE);
        }
    }
}
