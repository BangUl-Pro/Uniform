package com.songjin.usum.controllers.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.songjin.usum.Global;
import com.songjin.usum.HashBiMap;
import com.songjin.usum.R;
import com.songjin.usum.constants.Category;
import com.songjin.usum.constants.Sex;
import com.songjin.usum.constants.Size;
import com.songjin.usum.controllers.activities.SchoolAutoCompleteArrayAdapter;
import com.songjin.usum.controllers.fragments.SettingFragment;
import com.songjin.usum.entities.ReservedCategoryEntity;
import com.songjin.usum.entities.SchoolEntity;
import com.songjin.usum.entities.UserEntity;
import com.songjin.usum.managers.SchoolManager;

import java.util.ArrayList;

public class ProductSearchForm extends LinearLayout {
    private static final String TAG = "ProductSearchFrom";
    private ViewHolder viewHolder;
    private int selectedSchoolId;
    private ArrayAdapter<String> sexAdapter;
    private ArrayAdapter<String> categoryAdapter;
    private ArrayAdapter<String> sizeAdapter;
    private UserEntity userEntity;

    public ProductSearchForm(Context context) {
        this(context, null);
    }

    public ProductSearchForm(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProductSearchForm(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.form_product_search, this);

        viewHolder = new ViewHolder(this);

        final SchoolManager schoolManager = new SchoolManager(getContext());
        ArrayList<SchoolEntity> list = schoolManager.selectSchools();
        ArrayAdapter<SchoolEntity> arrayAdapter = new SchoolAutoCompleteArrayAdapter(
                getContext(),
                R.layout.school_info_autocomplete_item,
                list
        );
        viewHolder.schoolName.setAdapter(arrayAdapter);
        viewHolder.schoolName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView schoolIdTextview = (TextView) view.findViewById(R.id.school_id);
                selectedSchoolId = Integer.parseInt(schoolIdTextview.getText().toString());

                updateReservationStatus();
            }
        });

        sexAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                new ArrayList<>(Sex.getHashBiMapExceptAll().values())
        );
        categoryAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                new ArrayList<>(Category.getHashBiMap(Sex.MAN).values())
        );
        sizeAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                new ArrayList<>(Size.getHashBiMap(Category.ALL).values())
        );

        viewHolder.sex.setAdapter(sexAdapter);
        viewHolder.category.setAdapter(categoryAdapter);
        viewHolder.size.setAdapter(sizeAdapter);

        viewHolder.sex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoryAdapter.clear();

                int selectedKey = Sex.getHashBiMapExceptAll().inverse().get(sexAdapter.getItem(position));
                categoryAdapter.addAll(Category.getHashBiMap(selectedKey).values());
                categoryAdapter.notifyDataSetChanged();

                updateReservationStatus();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        viewHolder.category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sizeAdapter.clear();

                int selectedSex = Sex.getHashBiMap().inverse().get(viewHolder.sex.getSelectedItem());
                int selectedKey = Category.getHashBiMap(selectedSex).inverse().get(categoryAdapter.getItem(position));
                sizeAdapter.addAll(Size.getHashBiMap(selectedKey).values());
                sizeAdapter.notifyDataSetChanged();

                updateReservationStatus();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        viewHolder.useReservationPush.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int selectedSex = Sex.getHashBiMap().inverse().get(viewHolder.sex.getSelectedItem());

                int position = viewHolder.category.getSelectedItemPosition();
                HashBiMap<String, Integer> inverseCategoryMap = Category.getHashBiMap(selectedSex).inverse();
                int selectedCategory = inverseCategoryMap.get(categoryAdapter.getItem(position));

                String categoryName = Category.getHashBiMap(selectedSex).get(selectedCategory);
                ArrayList<ReservedCategoryEntity> reservedCategories = SettingFragment.getReservedCategories();
                Boolean hasAlready = SettingFragment.findIndexOfReservedCategories(reservedCategories, selectedSchoolId, selectedCategory) != -1;
                Log.d(TAG, "hasAlready = " + hasAlready);
                if (isChecked) {
                    if (hasAlready)
                        return;

                    SettingFragment.addReservedCategory(selectedSchoolId, selectedCategory);
                    SchoolEntity schoolEntity = schoolManager.selectSchool(selectedSchoolId);
                    String msg = schoolEntity.schoolname + "의 " + categoryName + " 카테고리에 새로운 교복이 올라오면 알림이 가도록 예약되었습니다.";
                    Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                } else {
                    if (!hasAlready)
                        return;

                    SettingFragment.removeReservedCategory(selectedSchoolId, selectedCategory);
                    SchoolEntity schoolEntity = schoolManager.selectSchool(selectedSchoolId);
                    String msg = schoolEntity.schoolname + "의 " + categoryName + " 카테고리의 알림 예약이 취소되었습니다.";
                    Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                }
            }
        });

        switch (userEntity.userType) {
            case Global.GUEST:
                viewHolder.useReservationPushContainer.setVisibility(View.INVISIBLE);
                break;
            case Global.STUDENT:
                if (userEntity.schoolId != 0) {
                    SchoolEntity schoolEntity = schoolManager.selectSchool(userEntity.schoolId);
                    selectedSchoolId = schoolEntity.id;
                    viewHolder.schoolName.setText(schoolEntity.schoolname);
                }
                viewHolder.schoolName.setEnabled(false);
                viewHolder.schoolName.clearFocus();

                viewHolder.sex.setSelection(userEntity.sex - 1);
                break;
            case Global.PARENT:
                break;
        }
    }

    private void updateReservationStatus() {
        if (selectedSchoolId == 0) {
            viewHolder.useReservationPush.setEnabled(false);
            return;
        }

        int selectedSex = Sex.getHashBiMap().inverse().get(viewHolder.sex.getSelectedItem());
        int selectedCategory = Category.getHashBiMap(selectedSex).inverse().get(viewHolder.category.getSelectedItem());

        ArrayList<ReservedCategoryEntity> reservedCategories = SettingFragment.getReservedCategories();
        Boolean isReserved = SettingFragment.findIndexOfReservedCategories(reservedCategories, selectedSchoolId, selectedCategory) != -1;
        viewHolder.useReservationPush.setChecked(isReserved);
        viewHolder.useReservationPush.setEnabled(selectedCategory != Category.ALL);
    }

    public void submit() {
        viewHolder.submitButton.performClick();
    }


    public int getSelectedSchoolId() {
        return selectedSchoolId;
    }

    public int getSex() {
        int sexPosition = viewHolder.sex.getSelectedItemPosition();
        Log.d(TAG, "sexPosition = " + sexPosition);
        HashBiMap<String, Integer> inverseSex = Sex.getHashBiMapExceptAll().inverse();
        int selectedSex = inverseSex.get(sexAdapter.getItem(sexPosition));
        return selectedSex;
    }


    public int getCategory() {
        int categoryPosition = viewHolder.category.getSelectedItemPosition();
        Log.d(TAG, "categoryPosition = " + categoryPosition);
        HashBiMap<String, Integer> inverseCategory = Category.getHashBiMap(getSex()).inverse();
        int selectedCategory = inverseCategory.get(categoryAdapter.getItem(categoryPosition));
        if (selectedCategory == 0)
            selectedCategory = -1;
        return selectedCategory;
    }


    public int getSize() {
        int sizePosition = viewHolder.size.getSelectedItemPosition();
        Log.d(TAG, "sizePosition = " + sizePosition);
        HashBiMap<String, Integer> inverseSize = Size.getHashBiMap(getCategory()).inverse();
        int selectedSize = inverseSize.get(sizeAdapter.getItem(sizePosition));
        if (selectedSize == 0)
            selectedSize = -1;
        return selectedSize;
    }


//    public BaasioQuery getSearchQuery() {
//        BaasioQuery query = new BaasioQuery();
//        query.setType(ProductEntity.COLLECTION_NAME);
//        query.setOrderBy(
//                ProductEntity.PROPERTY_CREATED,
//                BaasioQuery.ORDER_BY.DESCENDING
//        );
//
//        String whereString = "";
//
//        if (selectedSchoolId != 0) {
//            if (!whereString.isEmpty()) {
//                whereString += " AND ";
//            }
//            whereString += ProductEntity.PROPERTY_SCHOOL_ID + "=" + selectedSchoolId;
//        }
//
//        HashBiMap<String, Integer> inverseSex = Sex.getHashBiMapExceptAll().inverse();
//        int sexPosition = viewHolder.sex.getSelectedItemPosition();
//        int selectedSex = inverseSex.get(sexAdapter.getItem(sexPosition));
//        if (!whereString.isEmpty()) {
//            whereString += " AND ";
//        }
//        whereString += ProductEntity.PROPERTY_SEX + "=" + selectedSex;
//
//        HashBiMap<String, Integer> inverseCategory = Category.getHashBiMap(selectedSex).inverse();
//        int categoryPosition = viewHolder.category.getSelectedItemPosition();
//        int selectedCategory = inverseCategory.get(categoryAdapter.getItem(categoryPosition));
//        if (selectedCategory != Category.ALL) {
//            if (!whereString.isEmpty()) {
//                whereString += " AND ";
//            }
//            whereString += ProductEntity.PROPERTY_CATEGORY + "=" + selectedCategory;
//        }
//
//        HashBiMap<String, Integer> inverseSize = Size.getHashBiMap(selectedCategory).inverse();
//        int sizePosition = viewHolder.size.getSelectedItemPosition();
//        int selectedSize = inverseSize.get(sizeAdapter.getItem(sizePosition));
//        if (selectedSize != Size.ALL) {
//            if (!whereString.isEmpty()) {
//                whereString += " AND ";
//            }
//            whereString += ProductEntity.PROPERTY_SIZE + "=" + selectedSize;
//        }
//
//        query.setWheres(whereString);
//
//        return query;
//    }

    public void setOnSubmitListener(OnSubmitListener listener) {
        viewHolder.submitButton.setOnClickListener(listener);
    }

    public interface OnSubmitListener extends OnClickListener {
    }

    private class ViewHolder {
        public AutoCompleteTextView schoolName;
        public Spinner sex;
        public Spinner category;
        public Spinner size;
        public LinearLayout useReservationPushContainer;
        public Switch useReservationPush;
        public Button submitButton;

        public ViewHolder(View view) {
            schoolName = (AutoCompleteTextView) view.findViewById(R.id.schoolname);
            sex = (Spinner) view.findViewById(R.id.sex);
            category = (Spinner) view.findViewById(R.id.category);
            size = (Spinner) view.findViewById(R.id.size);
            useReservationPushContainer = (LinearLayout) view.findViewById(R.id.use_reservation_push_container);
            useReservationPush = (Switch) view.findViewById(R.id.use_reservation_push);
            submitButton = (Button) view.findViewById(R.id.submit_button);
        }
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }
}
