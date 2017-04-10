package me.toptas.fancyshowcasesample;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Activity for RecyclerView sample
 */
public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private final List<MyModel> mMyModelList;
    private View.OnClickListener mClickListener;

    public MyRecyclerViewAdapter(List<MyModel> myModelList) {
        mMyModelList = myModelList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_recycler, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MyModel myModel = mMyModelList.get(position);
        holder.title.setText(myModel.getTitle());
        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    mClickListener.onClick(v);
                }
            }
        });
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    mClickListener.onClick(v);
                }
            }
        });
        holder.imageView.setVisibility(position % 5 == 2 ? View.VISIBLE : View.GONE);

    }

    @Override
    public int getItemCount() {
        return mMyModelList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvMain)
        TextView title;
        @BindView(R.id.ivIcon)
        ImageView imageView;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public void setClickListener(View.OnClickListener clickListener) {
        mClickListener = clickListener;
    }

    public static class MyModel {
        String mTitle;

        public MyModel(String title) {
            mTitle = title;
        }

        public String getTitle() {
            return mTitle;
        }
    }

}