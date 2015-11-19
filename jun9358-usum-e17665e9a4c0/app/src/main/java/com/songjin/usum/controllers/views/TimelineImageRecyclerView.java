package com.songjin.usum.controllers.views;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.songjin.usum.entities.FileEntity;

import java.util.ArrayList;

public class TimelineImageRecyclerView extends RecyclerView {
    private ArrayList<FileEntity> fileEntities;

    public TimelineImageRecyclerView(Context context) {
        this(context, null);
    }

    public TimelineImageRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimelineImageRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        fileEntities = new ArrayList<>();
        setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        setAdapter(new TimelineImageAdapter(fileEntities));
        setVisibility(View.GONE);
    }

    public void setFileEntities(ArrayList<FileEntity> fileEntities) {
        this.fileEntities.clear();
        this.fileEntities.addAll(fileEntities);

        if (0 < this.fileEntities.size()) {
            setVisibility(View.VISIBLE);
        } else {
            setVisibility(View.GONE);
        }
        getAdapter().notifyDataSetChanged();
    }

    private class TimelineImageAdapter extends Adapter<TimelineImageAdapter.ViewHolder> {
        private ArrayList<FileEntity> fileIEntities;

        public TimelineImageAdapter(ArrayList<FileEntity> fileIEntities) {
            this.fileIEntities = fileIEntities;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
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
            FileEntity fileEntity = fileIEntities.get(position);
            viewHolder.imageCardView.setFileEntity(fileEntity);
        }

        @Override
        public int getItemCount() {
            return fileIEntities.size();
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
