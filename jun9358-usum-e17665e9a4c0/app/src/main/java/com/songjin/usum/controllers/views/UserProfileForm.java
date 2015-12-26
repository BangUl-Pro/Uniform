package com.songjin.usum.controllers.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.songjin.usum.Global;
import com.songjin.usum.R;
import com.songjin.usum.controllers.activities.SchoolAutoCompleteArrayAdapter;
import com.songjin.usum.entities.SchoolEntity;
import com.songjin.usum.entities.UserEntity;
import com.songjin.usum.managers.SchoolManager;

import java.util.ArrayList;

public class UserProfileForm extends ScrollView {
    private static final String TAG = "UserProfileForm";
    private long id;
    private class ViewHolder {
        public EditText realNameEditText;
        public RadioGroup sexRadioGroup;
        public RadioGroup userTypeRadioGroup;
        public EditText phoneEditText;
        public AutoCompleteTextView schoolNameAutoCompleteTextView;
        public Button submitButton;

        public ViewHolder(View view) {
            realNameEditText = (EditText) view.findViewById(R.id.realname);
            sexRadioGroup = (RadioGroup) view.findViewById(R.id.sex);
            userTypeRadioGroup = (RadioGroup) view.findViewById(R.id.usertype);
            phoneEditText = (EditText) view.findViewById(R.id.phone);
            schoolNameAutoCompleteTextView = (AutoCompleteTextView) view.findViewById(R.id.schoolname);
            submitButton = (Button) view.findViewById(R.id.submit_button);
        }
    }

    private ViewHolder viewHolder;

    private SchoolManager schoolManager;
    private int selectedSchoolId;

    public UserProfileForm(Context context) {
        this(context, null);
    }

    public UserProfileForm(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UserProfileForm(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.form_user_profile, this);
        viewHolder = new ViewHolder(this);

        schoolManager = new SchoolManager(getContext());

        ArrayList<SchoolEntity> schoolEntities = schoolManager.selectSchools();
        // 값 초기화
        ArrayAdapter<SchoolEntity> arrayAdapter = new SchoolAutoCompleteArrayAdapter(
                getContext(),
                R.layout.school_info_autocomplete_item,
                schoolEntities
        );
        viewHolder.schoolNameAutoCompleteTextView.setAdapter(arrayAdapter);
        viewHolder.schoolNameAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView schoolIdTextview = (TextView) view.findViewById(R.id.school_id);
                selectedSchoolId = Integer.parseInt(schoolIdTextview.getText().toString());
            }
        });
        viewHolder.schoolNameAutoCompleteTextView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        viewHolder.schoolNameAutoCompleteTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_DONE:
                        viewHolder.submitButton.performClick();
                        return true;
                }
                return false;
            }
        });
    }

    public enum Mode {
        SIGN_UP, EDIT
    }

    public void setMode(Mode mode) {
        switch (mode) {
            case SIGN_UP:
                setSignUpMode();
                break;
            case EDIT:
                setEditMode();
                break;
        }
    }

    private void setSignUpMode() {

    }

    private void setEditMode() {
//        UserEntity userEntity = new UserEntity(Baas.io().getSignedInUser());
        UserEntity userEntity = Global.userEntity;
        viewHolder.realNameEditText.setText(userEntity.realName);
        viewHolder.realNameEditText.setEnabled(false);
        viewHolder.sexRadioGroup.check(getSexResId(userEntity.sex));
        disableSexRadioButtons();
        viewHolder.userTypeRadioGroup.check(getUserTypeResId(userEntity.userType));
        disableUserTypeRadioButtons();
        viewHolder.phoneEditText.setText(userEntity.phone);
        viewHolder.schoolNameAutoCompleteTextView.setText(getSchoolName(userEntity.schoolId));
        selectedSchoolId = userEntity.schoolId;
    }

    private int getSexResId(int sexType) {
        switch (sexType) {
            case Global.MAN:
                return R.id.sex_man;
            case Global.WOMAN:
                return R.id.sex_woman;
            default:
                return 0;
        }
    }

    private int getUserTypeResId(int userType) {
        switch (userType) {
            case Global.GUEST:
                return 0;
            case Global.STUDENT:
                return R.id.usertype_student;
            case Global.PARENT:
                return R.id.usertype_parents;
            default:
                return 0;
        }
    }

    private String getSchoolName(int schoolId) {
        SchoolEntity schoolEntity = schoolManager.selectSchool(schoolId);
        return schoolEntity.schoolname;
    }

    private void disableUserTypeRadioButtons() {
        RadioButton usertypeStudent = (RadioButton) this.findViewById(R.id.usertype_student);
        RadioButton usertypeParents = (RadioButton) this.findViewById(R.id.usertype_parents);

        usertypeStudent.setEnabled(false);
        usertypeParents.setEnabled(false);
    }

    private void disableSexRadioButtons() {
        RadioButton sexMan = (RadioButton) this.findViewById(R.id.sex_man);
        RadioButton sexWoman = (RadioButton) this.findViewById(R.id.sex_woman);

        sexMan.setEnabled(false);
        sexWoman.setEnabled(false);
    }

    public UserEntity getUserEntity() {
        UserEntity userEntity = new UserEntity();
        userEntity.realName = viewHolder.realNameEditText.getText().toString();
        userEntity.sex = getUserSex();
        userEntity.userType = getUserType();
        userEntity.phone = viewHolder.phoneEditText.getText().toString();
        userEntity.schoolId = selectedSchoolId;

        //
        userEntity.id = String.valueOf(id);

        return userEntity;
    }


    public void setUserId(long id) {
        this.id = id;
    }


    private int getUserSex() {
        switch (viewHolder.sexRadioGroup.getCheckedRadioButtonId()) {
            case R.id.sex_man:
                return Global.MAN;
            case R.id.sex_woman:
                return Global.WOMAN;
            default:
                return Global.MAN;
        }
    }

    private int getUserType() {
        switch (viewHolder.userTypeRadioGroup.getCheckedRadioButtonId()) {
            case R.id.usertype_student:
                return Global.STUDENT;
            case R.id.usertype_parents:
                return Global.PARENT;
            default:
                return Global.STUDENT;
        }
    }

    public String validateForm() {
        UserEntity userEntity = getUserEntity();
        if (userEntity.realName.isEmpty()) {
            return "실명을 입력해주세요.";
        }
        if (userEntity.phone.isEmpty()) {
            return "전화번호를 입력해주세요.";
        }
        if (userEntity.schoolId == 0) {
            return "소속학교를 입력해주세요.";
        }
        SchoolEntity schoolEntity = schoolManager.selectSchool(userEntity.schoolId);
        String schoolName = viewHolder.schoolNameAutoCompleteTextView.getText().toString();
        Log.d(TAG, "schoolName = " + schoolName);
        Log.d(TAG, "schoolEntity Name = " + schoolEntity.schoolname);
        if (!schoolName.equals(schoolEntity.schoolname)) {
            return "소속학교를 다시 선택해주세요.";
        }

        return "";
    }

    public void setOnSubmitListener(OnSubmitListener listener) {
        viewHolder.submitButton.setOnClickListener(listener);
    }

    public interface OnSubmitListener extends OnClickListener {
    }
}
