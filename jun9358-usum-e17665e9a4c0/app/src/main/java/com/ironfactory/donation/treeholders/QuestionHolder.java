package com.ironfactory.donation.treeholders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.github.johnkil.print.PrintView;
import com.ironfactory.donation.R;
import com.unnamed.b.atv.model.TreeNode;

public class QuestionHolder extends TreeNode.BaseNodeViewHolder<QuestionHolder.IconTreeItem> {
    public QuestionHolder(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(TreeNode node, IconTreeItem value) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.layout_question_node, null, false);
        TextView tvValue = (TextView) view.findViewById(R.id.node_value);
        tvValue.setText(value.text);

        PrintView iconView = (PrintView) view.findViewById(R.id.icon);
        iconView.setIconText(context.getResources().getString(value.icon));

        return view;
    }

    @Override
    public void toggle(boolean active) {
    }

    @Override
    public int getContainerStyle() {
        return R.style.TreeNodeStyleCustom;
    }

    public static class IconTreeItem {
        public int icon;
        public String text;

        public IconTreeItem(int icon, String text) {
            this.icon = icon;
            this.text = text;
        }
    }
}
