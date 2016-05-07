package com.songjin.usum.controllers.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.songjin.usum.controllers.activities.BaseActivity;
import com.songjin.usum.controllers.views.SchoolRankingCardView;
import com.songjin.usum.controllers.views.SchoolRankingRecyclerView;
import com.songjin.usum.Global;
import com.songjin.usum.R;
import com.songjin.usum.dtos.SchoolRanking;
import com.songjin.usum.entities.SchoolEntity;
import com.songjin.usum.managers.AuthManager;
import com.songjin.usum.managers.RequestManager;
import com.songjin.usum.managers.SchoolManager;
import com.songjin.usum.slidingtab.SlidingBaseFragment;

import java.util.ArrayList;

public class CommunityFragment extends SlidingBaseFragment {
    private static final String TAG = "CommunityFragment";
    private class ViewHolder {
        public SchoolRankingRecyclerView schoolRankings;
        public LinearLayout mySchoolRankingCardContainer;
        public SchoolRankingCardView mySchoolRankingCardView;

        public ViewHolder(View view) {
            this.schoolRankings = (SchoolRankingRecyclerView) view.findViewById(R.id.school_rankings);
            this.mySchoolRankingCardContainer = (LinearLayout) view.findViewById(R.id.my_school_ranking_card_container);
            this.mySchoolRankingCardView = (SchoolRankingCardView) view.findViewById(R.id.my_school_ranking_card);
        }
    }

    private ViewHolder viewHolder;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onPageSelected() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community, container, false);

        viewHolder = new ViewHolder(view);
        final SchoolManager schoolManager = new SchoolManager(getActivity());
        RequestManager.getSchoolRanking(new RequestManager.OnGetSchoolRanking() {
            @Override
            public void onSuccess(ArrayList<SchoolRanking> schoolRankings) {
                setSchoolRankings(schoolRankings);
            }

            @Override
            public void onException() {
                new MaterialDialog.Builder(BaseActivity.context)
                        .title(R.string.app_name)
                        .content("학교순위를 가져오는 중에 문제가 발생하였습니다.")
                        .show();
            }
        });

//        RequestManager.getSchoolRankingsInBackground(schoolManager, new BaasioQueryCallback() {
//                    @Override
//                    public void onResponse(List<BaasioBaseEntity> entities, List<Object> objects, BaasioQuery baasioQuery, long l) {
//                        ArrayList<SchoolRanking> schoolRankings = new ArrayList<>();
//                        for (BaasioBaseEntity entity : entities) {
//                            schoolRankings.add(new SchoolRanking(entity));
//                        }
//                        viewHolder.schoolRankings.setSchoolRankings(schoolRankings);
//
//                        initMySchoolRankingCard(schoolRankings);
//                    }
//
//                    @Override
//                    public void onException(BaasioException e) {
//                        new MaterialDialog.Builder(BaseActivity.context)
//                                .title(R.string.app_name)
//                                .content("학교순위를 가져오는 중에 문제가 발생하였습니다.")
//                                .show();
//                    }
//                }
//        );

        return view;
    }


    public void setSchoolRankings(ArrayList<SchoolRanking> schoolRankings) {
        viewHolder.schoolRankings.setSchoolRankings(schoolRankings);
        if (Global.userEntity.userType == Global.STUDENT) {
            initMySchoolRankingCard(schoolRankings);
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        switch (AuthManager.getSignedInUserType()) {
            case Global.GUEST:
                viewHolder.mySchoolRankingCardContainer.setVisibility(View.GONE);
                break;
            case Global.STUDENT:
                break;
            case Global.PARENT:
                viewHolder.mySchoolRankingCardContainer.setVisibility(View.GONE);
                break;
        }
    }

    private void initMySchoolRankingCard(ArrayList<SchoolRanking> schoolRankings) {
        SchoolManager schoolManager = new SchoolManager(getActivity());

//        UserEntity userEntity = new UserEntity(Baas.io().getSignedInUser());
        SchoolEntity mySchoolEntity = schoolManager.selectSchool(Global.userEntity.schoolId);
        Log.d(TAG, "userId = " + Global.userEntity.id);
        Log.d(TAG, "schoolId = " + Global.userEntity.schoolId);
        Log.d(TAG, "schoolName = " + mySchoolEntity.schoolname);
        Log.d(TAG, "mySchoolEntity = " + mySchoolEntity);
        Log.d(TAG, "mySchoolEntity.Id = " + mySchoolEntity.id);
        if (mySchoolEntity != null)
            viewHolder.mySchoolRankingCardView.setSchoolEntity(mySchoolEntity);

        int myRanking = 0;
        int myProgress = 0;
        for (int i = 0; i < schoolRankings.size(); i++) {
            SchoolRanking schoolRanking = schoolRankings.get(i);

            if (Global.userEntity.schoolId == schoolRanking.school_id) {
                myRanking = i + 1;
                myProgress = SchoolRankingCardView.calcProgress(schoolRanking.point, schoolRankings.get(0).point);
                break;
            }
        }
        viewHolder.mySchoolRankingCardView.setRanking(myRanking);
        viewHolder.mySchoolRankingCardView.setProgress(myProgress);
    }
}