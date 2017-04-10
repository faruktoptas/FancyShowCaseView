package me.toptas.fancyshowcasesample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.toptas.fancyshowcase.FancyShowCaseView;

/**
 * Created by ftoptas on 10/04/17.
 */

public class RecyclerViewActivity extends BaseActivity {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view);
        ButterKnife.bind(this);

        List<MyRecyclerViewAdapter.MyModel> modelList = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            modelList.add(new MyRecyclerViewAdapter.MyModel("Item " + i));
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        MyRecyclerViewAdapter adapter = new MyRecyclerViewAdapter(modelList);
        adapter.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FancyShowCaseView.Builder(RecyclerViewActivity.this)
                        .focusOn(v)
                        .title("Focus RecyclerView Items")
                        .build()
                        .show();
            }
        });
        mRecyclerView.setAdapter(adapter);

    }
}
