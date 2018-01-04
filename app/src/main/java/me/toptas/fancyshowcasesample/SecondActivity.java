package me.toptas.fancyshowcasesample;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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

        focusOnButton();
    }

    /**
     * Focus on a view
     */
    @OnClick(R.id.button1)
    public void focus() {
        if (!isFinishing()) {
            focusOnButton();
        }
    }

    private void focusOnButton() {
        new FancyShowCaseView.Builder(SecondActivity.this)
                .focusOn(mButton)
                .title("Focus a view")
                .fitSystemWindows(true)
                .build()
                .show();
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }


    /**
     * Shows a FancyShowCaseView that focuses to ActionBar items
     *
     * @param item actionbar item to focus
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        new FancyShowCaseView.Builder(this)
                .focusOn(findViewById(item.getItemId()))
                .title("Focus on Actionbar items")
                .fitSystemWindows(true)
                .build()
                .show();
        return true;
    }
}
