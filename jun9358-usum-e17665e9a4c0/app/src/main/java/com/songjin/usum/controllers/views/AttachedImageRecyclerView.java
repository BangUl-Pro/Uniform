package com.songjin.usum.controllers.views;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class AttachedImageRecyclerView extends RecyclerView {
    private ArrayList<Uri> uris;

    public AttachedImageRecyclerView(Context context) {
        this(context, null);
    }

    public AttachedImageRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AttachedImageRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        uris = new ArrayList<>();
        setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        setAdapter(new AttachedImageAdapter(uris));
    }

    public void setUris(ArrayList<Uri> uris) {
        this.uris.clear();
        this.uris.addAll(uris);
        getAdapter().notifyDataSetChanged();
    }

    public ArrayList<Uri> getUris() {
        return this.uris;
    }

    public void clear() {
        this.uris.clear();
        getAdapter().notifyDataSetChanged();
    }

    private class AttachedImageAdapter extends Adapter<AttachedImageAdapter.ViewHolder> {
        private ArrayList<Uri> uris;

        public AttachedImageAdapter(ArrayList<Uri> uris) {
            this.uris = uris;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            ImageCardView imageCardView = new ImageCardView(getContext());
            imageCardView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
            imageCardView.setTag("ImageCardView");

            return new ViewHolder(imageCardView);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            viewHolder.imageCardView.setUri(uris.get(position));
        }

        @Override
        public int getItemCount() {
            return uris.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            public ImageCardView imageCardView;

            public ViewHolder(View view) {
                super(view);

                imageCardView = (ImageCardView) view.findViewWithTag("ImageCardView");
            }
        }
    }
}
