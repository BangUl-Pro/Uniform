package com.songjin.usum.controllers.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.songjin.usum.R;
import com.songjin.usum.entities.SchoolEntity;

import java.util.ArrayList;
import java.util.List;

public class SchoolAutoCompleteArrayAdapter extends ArrayAdapter<SchoolEntity> implements Filterable {

    private final SchoolAutocompleteFilter filter = new SchoolAutocompleteFilter();
    private List<SchoolEntity> originalItems;
    private List<SchoolEntity> items;
    private Context context;

    public SchoolAutoCompleteArrayAdapter(Context context, int resource, List<SchoolEntity> items) {
        super(context, resource, items);

        this.items = items;
        this.originalItems = new ArrayList<>(items);
        this.context = context;
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    @Override
    public SchoolEntity getItem(int position) {
        return items.get(position);
    }

    @Override
    public int getPosition(SchoolEntity item) {
        return items.indexOf(item);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.school_info_autocomplete_item, parent, false);
        } else {
            row = convertView;
        }

        // 학교 ID
        TextView schoolId = (TextView) row.findViewById(R.id.school_id);
        schoolId.setText(String.valueOf(getItem(position).id));

        // 학교명
        TextView schoolName = (TextView) row.findViewById(R.id.schoolname);
        schoolName.setText(getItem(position).schoolname);

        // 상세주소
        TextView detailAddress = (TextView) row.findViewById(R.id.school_address);
        detailAddress.setText(getItem(position).address);

        return row;
    }

    private class SchoolAutocompleteFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence contain) {
            FilterResults results = new FilterResults();

            if (originalItems == null) {
                originalItems = new ArrayList<>(items);
            }

            if (contain == null || contain.length() == 0) {
                ArrayList<SchoolEntity> list;
                list = new ArrayList<>(originalItems);
                results.values = list;
                results.count = list.size();
            } else {
                String containString = contain.toString().toLowerCase();

                ArrayList<SchoolEntity> values;
                values = new ArrayList<>(originalItems);

                final ArrayList<SchoolEntity> newValues = new ArrayList<>();

                for (SchoolEntity value : values) {
                    final String valueText = value.schoolname.toLowerCase();

                    // Contain against the whole, non-splitted value
                    if (valueText.contains(containString)) {
                        newValues.add(value);
                    } else {
                        final String[] words = valueText.split(" ");

                        // Start at index 0, in case valueText starts with space(s)
                        for (String word : words) {
                            if (word.contains(containString)) {
                                newValues.add(value);
                                break;
                            }
                        }
                    }
                }

                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //noinspection unchecked
            items = (List<SchoolEntity>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return resultValue == null ? "" : ((SchoolEntity) resultValue).schoolname;
        }
    }
}
