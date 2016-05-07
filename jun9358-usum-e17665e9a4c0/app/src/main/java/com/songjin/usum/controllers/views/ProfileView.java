package com.songjin.usum.controllers.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.songjin.usum.Global;
import com.songjin.usum.R;
import com.songjin.usum.controllers.activities.BaseActivity;
import com.songjin.usum.controllers.activities.EditProfileActivity;
import com.songjin.usum.entities.SchoolEntity;
import com.songjin.usum.entities.UserEntity;
import com.songjin.usum.managers.SchoolManager;

public class ProfileView extends LinearLayout {
    private class ViewHolder {
        public SquareImageView profilePicture;
        public TextView name;
        public TextView sex;
        public TextView schoolname;
        public TextView usertype;
        public TextView phone;
        public Button editProfileButton;

        public ViewHolder(View view) {
            profilePicture = (SquareImageView) view.findViewById(R.id.profile_picture);
            name = (TextView) view.findViewById(R.id.name);
            sex = (TextView) view.findViewById(R.id.sex);
            schoolname = (TextView) view.findViewById(R.id.schoolname);
            usertype = (TextView) view.findViewById(R.id.usertype);
            phone = (TextView) view.findViewById(R.id.phone);
            editProfileButton = (Button) view.findViewById(R.id.edit_profile_button);
        }
    }

    private ViewHolder viewHolder;

    public ProfileView(Context context) {
        this(context, null);
    }

    public ProfileView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProfileView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView();
    }

    private void initView() {
        View view = inflate(getContext(), R.layout.view_profile, this);

        viewHolder = new ViewHolder(view);
        viewHolder.editProfileButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseActivity.startActivityUsingStack(EditProfileActivity.class);
            }
        });
    }

    public void setUserEntity(UserEntity userEntity) {
        SchoolManager schoolManager = new SchoolManager(getContext());
        SchoolEntity schoolEntity = schoolManager.selectSchool(userEntity.schoolId);

        Ion.with(viewHolder.profilePicture)
                .placeholder(R.drawable.ic_launcher)
                .error(R.drawable.ic_launcher)
                .load(userEntity.picture);
        viewHolder.name.setText(userEntity.realName);
        if (userEntity.sex == Global.MAN) {
            viewHolder.sex.setText("남자");
        } else if (userEntity.sex == Global.WOMAN) {
            viewHolder.sex.setText("여자");
        }
        viewHolder.schoolname.setText(schoolEntity.schoolname);
        if (userEntity.userType == Global.PARENT) {
            viewHolder.usertype.setText("학부모");
        } else if (userEntity.userType == Global.STUDENT) {
            viewHolder.usertype.setText("학생/졸업생");
        }
        viewHolder.phone.setText(userEntity.phone);

        if (userEntity.id.equals(Global.userEntity.id)) {
            viewHolder.editProfileButton.setVisibility(View.VISIBLE);
        } else {
            viewHolder.editProfileButton.setVisibility(View.GONE);
        }
    }
}
