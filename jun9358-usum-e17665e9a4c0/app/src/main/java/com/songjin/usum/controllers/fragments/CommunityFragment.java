package com.songjin.usum.controllers.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.songjin.usum.Global;
import com.songjin.usum.R;
import com.songjin.usum.controllers.activities.BaseActivity;
import com.songjin.usum.controllers.views.SchoolRankingCardView;
import com.songjin.usum.controllers.views.SchoolRankingRecyclerView;
import com.songjin.usum.dtos.SchoolRanking;
import com.songjin.usum.entities.MySchoolRankingEntity;
import com.songjin.usum.entities.SchoolEntity;
import com.songjin.usum.managers.AuthManager;
import com.songjin.usum.managers.RequestManager;
import com.songjin.usum.managers.SchoolManager;
import com.songjin.usum.managers.SchoolRankingManager;
import com.songjin.usum.slidingtab.SlidingBaseFragment;

import java.util.ArrayList;
import java.util.List;

public class CommunityFragment extends SlidingBaseFragment {
    private static final String TAG = "CommunityFragment";
    private SchoolRankingManager schoolRankingManager;


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

        return view;
    }


    public void addSchoolRankings(ArrayList<SchoolRanking> schoolRankings) {
        viewHolder.schoolRankings.addSchoolRankings(schoolRankings);
        if (Global.userEntity.userType == Global.STUDENT) {
            initMySchoolRankingCard((int) schoolRankings.get(0).point);
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        new SchoolManager(getActivity());
        schoolRankingManager = new SchoolRankingManager(getActivity());

        List<SchoolRanking> schoolRankings = schoolRankingManager.selectSchoolRankings();
        if (schoolRankings.size() > 0) {
            addSchoolRankings(new ArrayList(schoolRankings));
        }

        RequestManager.getSchoolRanking(new RequestManager.OnGetSchoolRanking() {
            @Override
            public void onSuccess(final ArrayList<SchoolRanking> schoolRankings) {
                Log.d(TAG, "학교 랭킹 성공");
                addSchoolRankings(schoolRankings);

                schoolRankingManager.deleteAllSchoolRankings();
                schoolRankingManager.insertSchoolRankings(schoolRankings);
            }

            @Override
            public void onException() {
                new MaterialDialog.Builder(BaseActivity.context)
                        .title(R.string.app_name)
                        .content("학교순위를 가져오는 중에 문제가 발생하였습니다.")
                        .show();
            }
        });


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

    private void initMySchoolRankingCard(final int topPoint) {
        SchoolManager schoolManager = new SchoolManager(getActivity());

//        UserEntity userEntity = new UserEntity(Baas.io().getSignedInUser());
        final SchoolEntity mySchoolEntity = schoolManager.selectSchool(Global.userEntity.schoolId);
        if (mySchoolEntity != null)
            viewHolder.mySchoolRankingCardView.setSchoolEntity(mySchoolEntity);

        final SharedPreferences preferences = getActivity().getSharedPreferences(Global.APP_NAME, getActivity().MODE_PRIVATE);
        int rank = preferences.getInt(Global.RANK, -1);
        final long point = preferences.getLong(Global.POINT, -1);
        if (rank != -1 && point != -1) {
            MySchoolRankingEntity mySchoolRankingEntity = new MySchoolRankingEntity(rank, point);
            viewHolder.mySchoolRankingCardView.setRanking(mySchoolRankingEntity.rank);
            int myProgress = SchoolRankingCardView.calcProgress(mySchoolRankingEntity.point, topPoint);
            viewHolder.mySchoolRankingCardView.setProgress(myProgress);
        }

        // Realm 데이터 리셋
        RequestManager.getMySchoolRanking(new RequestManager.OnGetMySchoolRanking() {
            @Override
            public void onSuccess(final int rank) {
                RequestManager.getSchoolRanking(new RequestManager.OnGetSchoolRanking() {
                    @Override
                    public void onSuccess(ArrayList<SchoolRanking> schoolRankings) {
                        final SchoolRanking ranking = schoolRankings.get(0);
                        viewHolder.mySchoolRankingCardView.setRanking(rank);
                        int myProgress = SchoolRankingCardView.calcProgress(ranking.point, topPoint);
                        viewHolder.mySchoolRankingCardView.setProgress(myProgress);

                        final SharedPreferences.Editor editor = preferences.edit();
                        editor.putInt(Global.RANK, rank);
                        editor.putLong(Global.POINT, point);
                        editor.commit();
                    }

                    @Override
                    public void onException() {

                    }
                }, mySchoolEntity.id);
            }

            @Override
            public void onException() {
                Log.d(TAG, "내 학교 순위 얻기 실패");
            }
        }, Global.userEntity.schoolId);
    }
}