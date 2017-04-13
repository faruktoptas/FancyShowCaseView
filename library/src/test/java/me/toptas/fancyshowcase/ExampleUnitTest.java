package me.toptas.fancyshowcase;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.*;
import android.widget.Button;
import android.widget.FrameLayout;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 22 )
public class ExampleUnitTest {
    // Tag for container view
    private static final String CONTAINER_TAG = "ShowCaseViewTag";

    private Activity activity;

    @Before
    public void buildActivity() {
        activity = Robolectric.buildActivity(Activity.class).create().start().resume().visible().get();
    }

    private FancyShowCaseView.Builder createBuilderForView(final View view) {
        final FrameLayout layout = new FrameLayout(activity);
        LayoutParams layoutParams = new LayoutParams(MATCH_PARENT, MATCH_PARENT);
        layout.addView(view);
        activity.setContentView(layout, layoutParams);
        final FancyShowCaseView.Builder builder = new FancyShowCaseView.Builder(activity);
        return builder;
    }

    @Test
    public void testSimpleCircleFocus() {
        // setting params for the test method
        int viewWidth = 120;
        int viewHeight = 80;
        int viewMarginLeft = 120;
        int viewMarginRight = 120;
        double circleRadiusFactor = 1.2;
        boolean fitSystemWindows = true;
        // Arranging phase, we create and set up library under test with params
        Button button = new Button(activity);
        FrameLayout.LayoutParams btnLayoutParams = new FrameLayout.LayoutParams(viewWidth, viewHeight);
        btnLayoutParams.setMargins(viewMarginLeft, viewMarginRight, 0, 0);
        button.setLayoutParams(btnLayoutParams);
        // Acting phase
        createBuilderForView(button)
                .focusOn(button)
                .title("Simple circle focus")
                .fitSystemWindows(fitSystemWindows)
                .focusCircleRadiusFactor(circleRadiusFactor)
                .build()
                .show();
        // Calculating phase
        ViewGroup androidContent = (ViewGroup) activity.findViewById(android.R.id.content);
        ViewGroup mRoot = (ViewGroup) androidContent.getParent().getParent();
        final FrameLayout mContainer = (FrameLayout) mRoot.findViewWithTag(CONTAINER_TAG);
        FancyImageView fancyImageView = (FancyImageView) mContainer.getChildAt(0);
        int [] points = new int[2];
        button.getLocationInWindow(points);
        int mCircleCenterX = points[0] + viewWidth / 2;
        int mCircleCenterY = points[1] + viewHeight / 2 - fancyImageView.mCalculator.adjustHeight(activity, fitSystemWindows);
        int radius = (int) ((int) (Math.hypot(button.getWidth(), button.getHeight()) / 2) * circleRadiusFactor);
        // Asserting phase
        Assert.assertEquals(FocusShape.CIRCLE, fancyImageView.mCalculator.getFocusShape());
        Assert.assertEquals(mCircleCenterX, fancyImageView.mCalculator.getCircleCenterX());
        Assert.assertEquals(mCircleCenterY, fancyImageView.mCalculator.getCircleCenterY());
        Assert.assertEquals(radius, fancyImageView.mCalculator.getViewRadius());
    }


}