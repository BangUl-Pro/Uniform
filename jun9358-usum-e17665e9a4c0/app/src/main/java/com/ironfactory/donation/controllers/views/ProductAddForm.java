package com.ironfactory.donation.controllers.views;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.ironfactory.donation.Global;
import com.ironfactory.donation.HashBiMap;
import com.ironfactory.donation.R;
import com.ironfactory.donation.constants.Category;
import com.ironfactory.donation.constants.Condition;
import com.ironfactory.donation.constants.Sex;
import com.ironfactory.donation.constants.Size;
import com.ironfactory.donation.controllers.activities.BaseActivity;
import com.ironfactory.donation.controllers.activities.SchoolAutoCompleteArrayAdapter;
import com.ironfactory.donation.dtos.ProductCardDto;
import com.ironfactory.donation.entities.FileEntity;
import com.ironfactory.donation.entities.ProductEntity;
import com.ironfactory.donation.entities.SchoolEntity;
import com.ironfactory.donation.entities.TransactionEntity;
import com.ironfactory.donation.entities.UserEntity;
import com.ironfactory.donation.managers.AuthManager;
import com.ironfactory.donation.managers.SchoolManager;

import java.io.File;
import java.util.ArrayList;

public class ProductAddForm extends CardView {
    private class ViewHolder {
        AutoCompleteTextView schoolName;
        Spinner sex;
        Spinner category;
        Spinner size;
        Spinner condition;
        EditText contents;
        Button attachImageButton;
        AttachedImageRecyclerView attachedImages;
        Button submitButton;

        public ViewHolder(View view) {
            schoolName = (AutoCompleteTextView) view.findViewById(R.id.schoolname);
            sex = (Spinner) view.findViewById(R.id.sex);
            category = (Spinner) view.findViewById(R.id.category);
            size = (Spinner) view.findViewById(R.id.size);
            condition = (Spinner) view.findViewById(R.id.condition);
            contents = (EditText) view.findViewById(R.id.contents);
            attachImageButton = (Button) view.findViewById(R.id.attach_image_button);
            attachedImages = (AttachedImageRecyclerView) view.findViewById(R.id.attached_images);
            submitButton = (Button) view.findViewById(R.id.submit_button);
        }
    }

    private ViewHolder viewHolder;

    private int selectedSchoolId;
    private ArrayAdapter<String> sexAdapter;
    private ArrayAdapter<String> categoryAdapter;
    private ArrayAdapter<String> sizeAdapter;
    private ArrayAdapter<String> conditionAdapter;
    private ArrayList<Uri> selectedImageUris;

    public ProductAddForm(Context context) {
        this(context, null);
    }

    public ProductAddForm(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProductAddForm(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.form_product_add, this);

        selectedImageUris = new ArrayList<>();

        viewHolder = new ViewHolder(this);
        initSchoolNameAutoComplete();
        initSpinnerAdapters();
    }

    private void initSchoolNameAutoComplete() {
        SchoolManager schoolManager = new SchoolManager(getContext());
        ArrayAdapter<SchoolEntity> arrayAdapter = new SchoolAutoCompleteArrayAdapter(
                getContext(),
                R.layout.school_info_autocomplete_item,
                schoolManager.selectSchools()
        );
        viewHolder.schoolName.setAdapter(arrayAdapter);
        viewHolder.schoolName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView schoolIdTextview = (TextView) view.findViewById(R.id.school_id);
                selectedSchoolId = Integer.parseInt(schoolIdTextview.getText().toString());
            }
        });
    }

    private void initSpinnerAdapters() {
        sexAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                new ArrayList<>(Sex.getHashBiMapExceptAll().values())
        );
        categoryAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                new ArrayList<>(Category.getHashBiMapExceptAll(Sex.MAN).values())
        );
        sizeAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                new ArrayList<>(Size.getHashBiMapExceptAll(Category.MAN_JACKET).values())
        );
        conditionAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                new ArrayList<>(Condition.getHashBiMapExceptAll().values())
        );

        viewHolder.sex.setAdapter(sexAdapter);
        viewHolder.category.setAdapter(categoryAdapter);
        viewHolder.size.setAdapter(sizeAdapter);
        viewHolder.condition.setAdapter(conditionAdapter);

        viewHolder.sex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selectedSex = Sex.getHashBiMapExceptAll().inverse().get(sexAdapter.getItem(position));
                setCategoryAdapter(selectedSex);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        viewHolder.category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selectedSex = Sex.getHashBiMap().inverse().get(viewHolder.sex.getSelectedItem());
                int selectedKey = Category.getHashBiMap(selectedSex).inverse().get(categoryAdapter.getItem(position));
                setSizeAdapter(selectedKey);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        SchoolManager schoolManager = new SchoolManager(getContext());
        switch (AuthManager.getSignedInUserType()) {
            case GUEST:
                break;
            case STUDENT:
                UserEntity userEntity = Global.userEntity;
                if (userEntity.schoolId != 0) {
                    SchoolEntity schoolEntity = schoolManager.selectSchool(userEntity.schoolId);
                    selectedSchoolId = schoolEntity.id;
                    viewHolder.schoolName.setText(schoolEntity.schoolname);
                    viewHolder.schoolName.clearFocus();
                }
                viewHolder.schoolName.setEnabled(false);

                viewHolder.sex.setSelection(userEntity.sex.ordinal());
                break;
            case PARENT:
                break;
        }
    }

    private void setCategoryAdapter(int sex)
    {
        categoryAdapter.clear();
        categoryAdapter.addAll(Category.getHashBiMapExceptAll(sex).values());
        categoryAdapter.notifyDataSetChanged();
    }

    private void setSizeAdapter(int category)
    {
        sizeAdapter.clear();
        sizeAdapter.addAll(Size.getHashBiMapExceptAll(category).values());
        sizeAdapter.notifyDataSetChanged();
    }

    public void clear() {
        initSpinnerAdapters();
        viewHolder.attachedImages.clear();
        selectedImageUris.clear();
    }

    public ProductCardDto getProductCardDto() {
        ProductCardDto productCardDto = new ProductCardDto();
        productCardDto.productEntity = getProductEntity();
        productCardDto.transactionEntity = getTransactionEntity();
        productCardDto.uris.addAll(selectedImageUris);

        return productCardDto;
    }

    private TransactionEntity getTransactionEntity() {
        TransactionEntity transactionEntity = new TransactionEntity();
        transactionEntity.status = TransactionEntity.STATUS_TYPE.REGISTERED;
        return transactionEntity;
    }

    private ProductEntity getProductEntity() {
        ProductEntity productEntity = new ProductEntity();
        productEntity.school_id = selectedSchoolId;

        HashBiMap<String, Integer> inverseSex = Sex.getHashBiMapExceptAll().inverse();
        int sexPosition = viewHolder.sex.getSelectedItemPosition();
        int selectedSex = inverseSex.get(sexAdapter.getItem(sexPosition));
        productEntity.sex = selectedSex;

        HashBiMap<String, Integer> inverseCategory = Category.getHashBiMapExceptAll(selectedSex).inverse();
        int categoryPosition = viewHolder.category.getSelectedItemPosition();
        int selectedCategory = inverseCategory.get(categoryAdapter.getItem(categoryPosition));
        productEntity.category = selectedCategory;

        HashBiMap<String, Integer> inverseSize = Size.getHashBiMapExceptAll(selectedCategory).inverse();
        int sizePosition = viewHolder.size.getSelectedItemPosition();
        int selectedSize = inverseSize.get(sizeAdapter.getItem(sizePosition));
        productEntity.size = selectedSize;

        HashBiMap<String, Integer> inverseCondition = Condition.getHashBiMapExceptAll().inverse();
        int conditionPosition = viewHolder.condition.getSelectedItemPosition();
        int selectedCondition = inverseCondition.get(conditionAdapter.getItem(conditionPosition));
        productEntity.condition = selectedCondition;

        UserEntity userEntity = Global.userEntity;
        productEntity.user_id = userEntity.id;

        productEntity.contents = viewHolder.contents.getText().toString();

        productEntity.created = System.currentTimeMillis();

        return productEntity;
    }

    public void setProductCardDto(ProductCardDto productCardDto) {
        SchoolManager schoolManager = new SchoolManager(getContext());
        selectedSchoolId = productCardDto.productEntity.school_id;
        SchoolEntity schoolEntity = schoolManager.selectSchool(productCardDto.productEntity.school_id);
        viewHolder.schoolName.setText(schoolEntity.schoolname);

        HashBiMap<Integer, String> sex = Sex.getHashBiMapExceptAll();
        viewHolder.sex.setSelection(sexAdapter.getPosition(sex.get(productCardDto.productEntity.sex)));

        setCategoryAdapter(productCardDto.productEntity.sex);
        HashBiMap<Integer, String> category = Category.getHashBiMapExceptAll(productCardDto.productEntity.sex);
        viewHolder.category.setSelection(categoryAdapter.getPosition(category.get(productCardDto.productEntity.category)));

        setSizeAdapter(productCardDto.productEntity.category);
        HashBiMap<Integer, String> size = Size.getHashBiMapExceptAll(productCardDto.productEntity.category);
        viewHolder.size.setSelection(sizeAdapter.getPosition(size.get(productCardDto.productEntity.size)));

        HashBiMap<Integer, String> condition = Condition.getHashBiMapExceptAll();
        viewHolder.condition.setSelection(conditionAdapter.getPosition(condition.get(productCardDto.productEntity.condition)));

        viewHolder.contents.setText(productCardDto.productEntity.contents);

        for (FileEntity fileEntity : productCardDto.fileEntities) {
            Uri uri = Uri.fromFile(new File(BaseActivity.context.getCacheDir() + fileEntity.id));
            selectedImageUris.add(uri);
        }
        viewHolder.attachedImages.clear();
        viewHolder.attachedImages.setUris(selectedImageUris);
    }

    public void setSelectedImageUris(ArrayList<Uri> selectedImageUris) {
        this.selectedImageUris = selectedImageUris;
        viewHolder.attachedImages.setUris(selectedImageUris);
    }

    public void setAttachImageButtonOnClickListener(OnClickListener listener) {
        viewHolder.attachImageButton.setOnClickListener(listener);
    }

    public void setOnSubmitListener(OnSubmitListener listener) {
        viewHolder.submitButton.setOnClickListener(listener);
    }

    public interface OnSubmitListener extends OnClickListener {
    }
}
