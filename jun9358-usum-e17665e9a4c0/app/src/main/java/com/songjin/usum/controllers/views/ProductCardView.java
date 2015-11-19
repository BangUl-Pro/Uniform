package com.songjin.usum.controllers.views;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.songjin.usum.R;
import com.songjin.usum.constants.Category;
import com.songjin.usum.constants.Condition;
import com.songjin.usum.constants.Sex;
import com.songjin.usum.constants.Size;
import com.songjin.usum.constants.Status;
import com.songjin.usum.dtos.ProductCardDto;
import com.songjin.usum.entities.SchoolEntity;
import com.songjin.usum.managers.SchoolManager;

public class ProductCardView extends LinearLayout {
    private TextView writtenTime;
    private SquareImageView photo;
    private TextView schoolname;
    private TextView sex;
    private TextView category;
    private TextView size;
    private TextView condition;
    private TextView status;

    public ProductCardView(Context context) {
        this(context, null);
    }

    public ProductCardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProductCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void initView() {
        inflate(getContext(), R.layout.view_product_card, this);
        writtenTime = (TextView) findViewById(R.id.written_time);
        photo = (SquareImageView) findViewById(R.id.photo);
        schoolname = (TextView) findViewById(R.id.schoolname);
        sex = (TextView) findViewById(R.id.sex);
        category = (TextView) findViewById(R.id.category);
        size = (TextView) findViewById(R.id.size);
        condition = (TextView) findViewById(R.id.condition);
        status = (TextView) findViewById(R.id.status);
    }

    public void setProductCardDto(ProductCardDto productCardDto) {
        SchoolManager schoolManager = new SchoolManager(getContext());
        SchoolEntity schoolEntity = schoolManager.selectSchool(productCardDto.productEntity.school_id);

        writtenTime.setText(DateFormat.format("yyyy년 MM월 dd일 HH시 mm분에 작성", productCardDto.productEntity.created));

        photo.setImageResource(R.drawable.ic_launcher);
        if (0 < productCardDto.uris.size()) {
            photo.setImageURI(productCardDto.uris.get(0));
        } else if (0 < productCardDto.fileEntities.size()) {
            String photoUrl = "https://portal-access-api.baas.io/usum/usum/files/" + productCardDto.fileEntities.get(0).uuid + "/data";
            Ion.with(photo)
                    .placeholder(R.drawable.ic_launcher)
                    .error(R.drawable.ic_launcher)
                    .load(photoUrl);
        }
        schoolname.setText(schoolEntity.schoolname);
        sex.setText("[" + Sex.getHashBiMapExceptAll().get(productCardDto.productEntity.sex) + "]");
        category.setText(Category.getHashBiMap(productCardDto.productEntity.sex).get(productCardDto.productEntity.category));
        size.setText(Size.getHashBiMap(productCardDto.productEntity.category).get(productCardDto.productEntity.size));
        condition.setText(Condition.getHashBiMapExceptAll().get(productCardDto.productEntity.condition));
        if (productCardDto.transactionEntity != null) {
            status.setText(Status.getHashBiMap().get(productCardDto.transactionEntity.status));
        }
    }
}
