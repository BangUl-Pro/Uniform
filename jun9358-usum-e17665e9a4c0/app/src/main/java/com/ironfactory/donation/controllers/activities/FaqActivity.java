package com.ironfactory.donation.controllers.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.ironfactory.donation.R;
import com.ironfactory.donation.treeholders.QuestionHolder;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.util.ArrayList;

public class FaqActivity extends BaseActivity {

    private static final String TAG = "FaqActivity";

    private class ViewHolder {
        public ViewGroup treeviewContainer;
        public AndroidTreeView treeView;

        public ViewHolder(View view) {
            treeviewContainer = (ViewGroup) view.findViewById(R.id.treeview_container);
            treeView = new AndroidTreeView(BaseActivity.context, getFaqRoot());
            treeviewContainer.addView(treeView.getView());
        }
    }

    private ViewHolder viewHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews(R.layout.activity_faq);
    }

    @Override
    protected void initViews(int layoutResID) {
        setContentView(layoutResID);
        Log.d(TAG, "액티비티 시작");

        viewHolder = new ViewHolder(getWindow().getDecorView());
    }

    private TreeNode getFaqRoot() {
        TreeNode root = TreeNode.root();
        root.addChildren(
                getSignUpFaqNode(),
                getUniformDonationFaqNode(),
                getReceiveDonatedUniformFaqNode(),
                getCommunityFaqNode(),
                getSettingFaqNode(),
                getSolvingProblemFaqNode(),
                getSupportFaqNode()
        );
        return root;
    }

    private TreeNode getSignUpFaqNode() {
        TreeNode parent = new TreeNode(new QuestionHolder.IconTreeItem(R.string.ic_live_help, "1. 가입/회원정보")).setViewHolder(new QuestionHolder(BaseActivity.context));

        ArrayList<TreeNode> children = new ArrayList<>();
        children.add(new TreeNode(new QuestionHolder.IconTreeItem(R.string.ic_live_help, "Q. 회원가입 방법이 궁금해요.")).setViewHolder(new QuestionHolder(BaseActivity.context))
                .addChild(new TreeNode("앱을 다운받은 후 실행해서, 카카오톡 ID를 통한 자동 회원가입을 하시면 됩니다.")));
        children.add(new TreeNode(new QuestionHolder.IconTreeItem(R.string.ic_live_help, "Q. 회원 등급 권한은 어떻게 되나요?")).setViewHolder(new QuestionHolder(BaseActivity.context))
                .addChild(new TreeNode("학생/졸업생 회원과 학부모회원, 비회원으로 구분됩니다.\n" +
                        "학생/졸업생 회원은 1개 학교를 지정, 커뮤니티 활동(글쓰기, 댓글 쓰기, 좋아요)에 참여할 수 있습니다. 그리고 자신의 학교에 대해 교복을 기부/거래 가능합니다.\n" +
                        "학부모 회원은 모든 학교의 커뮤니티 활동에 참여할 수 없고 조회만 가능합니다. 하지만, 모든 학교에 대해 교복을 기부/거래 가능합니다.\n" +
                        "비회원은 모든 부분에 조회만 가능합니다.")));
        children.add(new TreeNode(new QuestionHolder.IconTreeItem(R.string.ic_live_help, "Q. 회원정보를 수정하고 싶어요.")).setViewHolder(new QuestionHolder(BaseActivity.context))
                .addChild(new TreeNode("마이 페이지에서 정보를 수정 가능합니다.")));
        children.add(new TreeNode(new QuestionHolder.IconTreeItem(R.string.ic_live_help, "Q. 로그아웃과, 탈퇴는 어떻게 하나요?")).setViewHolder(new QuestionHolder(BaseActivity.context))
                .addChild(new TreeNode("로그아웃은 [설정>로그아웃]으로 하실 수 있습니다.\n" +
                        "탈퇴는 [설정>회원탈퇴]로 하실 수 있습니다.")));
        children.add(new TreeNode(new QuestionHolder.IconTreeItem(R.string.ic_live_help, "Q. 재가입은 탈퇴 후 언제 다시 할 수 있나요?")).setViewHolder(new QuestionHolder(BaseActivity.context))
                .addChild(new TreeNode("탈퇴 후 바로 재가입할 수 있습니다.")));

        parent.addChildren(children);
        return parent;
    }

    private TreeNode getUniformDonationFaqNode() {
        TreeNode parent = new TreeNode(new QuestionHolder.IconTreeItem(R.string.ic_live_help, "2. 교복 기증")).setViewHolder(new QuestionHolder(BaseActivity.context));

        ArrayList<TreeNode> children = new ArrayList<>();
        children.add(new TreeNode(new QuestionHolder.IconTreeItem(R.string.ic_live_help, "Q. 교복을 어떻게 기증할 수 있나요?")).setViewHolder(new QuestionHolder(BaseActivity.context))
                .addChild(new TreeNode("'마켓'메뉴 상단의 '기부하기'를 누르시면 교복을 등록할 수 있습니다.\n" +
                        "회원님이 등록한 교복을 누군가가 기증받기 원하면, 알림으로 알려드립니다.\n" +
                        "교복을 해당자에게 발송한 후, '발송완료' 버튼을 눌러주세요.\n" +
                        "기증이 완료되면 회원님의 학교에 +100점 추가됩니다.")));
        children.add(new TreeNode(new QuestionHolder.IconTreeItem(R.string.ic_live_help, "Q. 비회원도 교복 기증이 가능한가요?")).setViewHolder(new QuestionHolder(BaseActivity.context))
                .addChild(new TreeNode("회원가입 후 로그인 하셔야만 교복 기증이 가능합니다.")));
        children.add(new TreeNode(new QuestionHolder.IconTreeItem(R.string.ic_live_help, "Q. 사진 업로드 시 최적 사이즈는 어떻게 되나요?")).setViewHolder(new QuestionHolder(BaseActivity.context))
                .addChild(new TreeNode("정해진 최적 사이즈는 없습니다. 다만, 너무 큰 사이즈의 사진은 업로드 시 문제가 발생될 수 있습니다. 만약, 업로드 시 오류가 지속적으로 발생된다면 사진과 함께 jerryjang99@naver.com 를 통해 문의 주시기 바랍니다.")));
        children.add(new TreeNode(new QuestionHolder.IconTreeItem(R.string.ic_live_help, "Q. 사진을 수정하고 싶어요.")).setViewHolder(new QuestionHolder(BaseActivity.context))
                .addChild(new TreeNode("카카오톡 API를 사용하기 때문에, 사진은 자동으로 카카오톡의 사진으로 우선 등록되게 됩니다. 프로필 사진은 다시 수정할 수 있습니다.")));
        children.add(new TreeNode(new QuestionHolder.IconTreeItem(R.string.ic_live_help, "Q. 사진을 등록하는 데 문제가 있어요.")).setViewHolder(new QuestionHolder(BaseActivity.context))
                .addChild(new TreeNode("일시적으로 네트워크가 원활하지 않을 수 있습니다. 만약 일시적인 것이 아니라 지속적으로 오류가 발생한다면 jerryjang99@naver.com 를 통해 문의 주시기 바랍니다.")));
        children.add(new TreeNode(new QuestionHolder.IconTreeItem(R.string.ic_live_help, "Q. 기증 취소가 가능한가요?")).setViewHolder(new QuestionHolder(BaseActivity.context))
                .addChild(new TreeNode("배송하기 전에는 수동으로 취소가 가능합니다. 또한, 거래 시작 후 일주일 이상 배송하지 않으면 자동 취소가 됩니다.")));

        parent.addChildren(children);
        return parent;
    }

    private TreeNode getReceiveDonatedUniformFaqNode() {
        TreeNode parent = new TreeNode(new QuestionHolder.IconTreeItem(R.string.ic_live_help, "3. 교복 기증받기")).setViewHolder(new QuestionHolder(BaseActivity.context));

        ArrayList<TreeNode> children = new ArrayList<>();
        children.add(new TreeNode(new QuestionHolder.IconTreeItem(R.string.ic_live_help, "Q. 교복을 기증받는 방법은 무엇인가요?")).setViewHolder(new QuestionHolder(BaseActivity.context))
                .addChild(new TreeNode("마켓에서 교복을 검색 후 클릭하면, 하단에 기증받기 버튼이 있습니다.\n" +
                        "기증받기를 누르시면 신청이 완료되고, 기증자가 교복을 발송합니다.\n" +
                        "교복을 받으시면 '수령완료' 버튼을 꼭 눌러주세요.")));
        children.add(new TreeNode(new QuestionHolder.IconTreeItem(R.string.ic_live_help, "Q. 예약을 눌렀는데 아무 반응이 없어요.")).setViewHolder(new QuestionHolder(BaseActivity.context))
                .addChild(new TreeNode("일시적으로 네트워크가 원활하지 않을 수 있습니다. 만약 일시적인 것이 아니라 지속적으로 오류가\n" +
                        "발생한다면 jerryjang99@naver.com 를 통해 문의 주시기 바랍니다.")));
        children.add(new TreeNode(new QuestionHolder.IconTreeItem(R.string.ic_live_help, "Q. ‘기증받기’ 신청 완료 후 단순변심으로 인한 취소가 가능한가요?")).setViewHolder(new QuestionHolder(BaseActivity.context))
                .addChild(new TreeNode("기증자가 ‘발송완료’를 누르기 전까지만 거래취소가 가능합니다.")));

        parent.addChildren(children);
        return parent;
    }

    private TreeNode getCommunityFaqNode() {
        TreeNode parent = new TreeNode(new QuestionHolder.IconTreeItem(R.string.ic_live_help, "4. 커뮤니티")).setViewHolder(new QuestionHolder(BaseActivity.context));

        ArrayList<TreeNode> children = new ArrayList<>();
        children.add(new TreeNode(new QuestionHolder.IconTreeItem(R.string.ic_live_help, "Q. 우리 학교 등록은 어떻게 하나요?")).setViewHolder(new QuestionHolder(BaseActivity.context))
                .addChild(new TreeNode("회원가입 시 등록할 수 있습니다.\n" +
                        "만약 등록을 하지 못하셨다면, 마이페이지에서 등록할 수 있습니다.")));
        children.add(new TreeNode(new QuestionHolder.IconTreeItem(R.string.ic_live_help, "Q. 우리 학교 순위 (점수)는 어떻게 올릴 수 있나요?")).setViewHolder(new QuestionHolder(BaseActivity.context))
                .addChild(new TreeNode("교복 기부를 완료하면 100점, 커뮤니티에 글을 등록하면 10점을 얻을 수 있습니다.\n" +
                        "이 점수로 학교 순위가 결정됩니다.")));
        children.add(new TreeNode(new QuestionHolder.IconTreeItem(R.string.ic_live_help, "Q. 글 수정/삭제는 어떻게 할 수 있나요?")).setViewHolder(new QuestionHolder(BaseActivity.context))
                .addChild(new TreeNode("내가 쓴 글은 수정/삭제를 할 수 있습니다. 글 상단 메뉴 버튼을 누르시면 수정 및 삭제가 가능합니다.")));
        children.add(new TreeNode(new QuestionHolder.IconTreeItem(R.string.ic_live_help, "Q. 커뮤니티에 부적절한 글이 올라왔는데 어떻게 해야 하나요?")).setViewHolder(new QuestionHolder(BaseActivity.context))
                .addChild(new TreeNode("개인 비방, 성적 수치심을 유발하는 글과 같은 부적절한 글은 해당 글 상단의 ‘신고하기’를 클릭해주세요.\n" +
                        "관리자에게 연락이 가고, 조치를 취할 수 있습니다.")));
        children.add(new TreeNode(new QuestionHolder.IconTreeItem(R.string.ic_live_help, "Q. 다른 학교 커뮤니티에는 글 작성을 할 수 없나요?")).setViewHolder(new QuestionHolder(BaseActivity.context))
                .addChild(new TreeNode("학생의 경우 1개의 본인 학교 커뮤니티에만 접속이 가능하며, 다른 학교 커뮤니티 진입 시 글쓰기 권한이 없습니다. 글씨기와 더불어 댓글 쓰기도 불가능하며, ‘좋아요’기능도 사용하실 수 없습니다.")));

        parent.addChildren(children);
        return parent;
    }

    private TreeNode getSettingFaqNode() {
        TreeNode parent = new TreeNode(new QuestionHolder.IconTreeItem(R.string.ic_live_help, "5. 설정")).setViewHolder(new QuestionHolder(BaseActivity.context));

        ArrayList<TreeNode> children = new ArrayList<>();
        children.add(new TreeNode(new QuestionHolder.IconTreeItem(R.string.ic_live_help, "Q. 푸쉬 알람을 끄고 싶어요.")).setViewHolder(new QuestionHolder(BaseActivity.context))
                .addChild(new TreeNode("'설정'화면에서 거래 푸쉬/거래 외 푸쉬를 끄고 켤 수 있습니다.")));
        children.add(new TreeNode(new QuestionHolder.IconTreeItem(R.string.ic_live_help, "Q. 거래 푸쉬란 무엇인가요?")).setViewHolder(new QuestionHolder(BaseActivity.context))
                .addChild(new TreeNode("내가 등록한 교복을 누군가가 신청했을 때, 내가 신청한 교복을 기증자가 발송했을 때 등 교복 기증에 관련된 푸쉬를 말합니다.")));
        children.add(new TreeNode(new QuestionHolder.IconTreeItem(R.string.ic_live_help, "Q. 거래 외 푸쉬란 무엇인가요?")).setViewHolder(new QuestionHolder(BaseActivity.context))
                .addChild(new TreeNode("거래 푸쉬를 제외한 우리 학교 순위 변동 및 커뮤니티 등의 푸쉬를 말합니다.")));

        parent.addChildren(children);
        return parent;
    }

    private TreeNode getSolvingProblemFaqNode() {
        TreeNode parent = new TreeNode(new QuestionHolder.IconTreeItem(R.string.ic_live_help, "6. 문제해결")).setViewHolder(new QuestionHolder(BaseActivity.context));

        ArrayList<TreeNode> children = new ArrayList<>();
        children.add(new TreeNode(new QuestionHolder.IconTreeItem(R.string.ic_live_help, "Q. 접속이 갑자기 느립니다. 어떻게 하나요?")).setViewHolder(new QuestionHolder(BaseActivity.context))
                .addChild(new TreeNode("이동 중이거나 건물 지하와 같은 특정 공간에서는 네트워크가 원활하지 않을 수 있습니다. 만약 일시적인 것이 아니라 지속적으로 오류가 발생한다면 jerryjang99@naver.com 를 통해 문의 주시기 바랍니다.")));

        parent.addChildren(children);
        return parent;
    }

    private TreeNode getSupportFaqNode() {
        TreeNode parent = new TreeNode(new QuestionHolder.IconTreeItem(R.string.ic_live_help, "7. 후원")).setViewHolder(new QuestionHolder(BaseActivity.context));

        ArrayList<TreeNode> children = new ArrayList<>();
        children.add(new TreeNode(new QuestionHolder.IconTreeItem(R.string.ic_live_help, "Q. 인앱결제를 통해 후원하려면 로그인이 필요한가요?")).setViewHolder(new QuestionHolder(BaseActivity.context))
                .addChild(new TreeNode("네. 인앱결제는 구글 플레이스토어를 통해 이루어지기 때문에 반드시 로그인을 해주셔야 합니다.")));
        children.add(new TreeNode(new QuestionHolder.IconTreeItem(R.string.ic_live_help, "Q. 후원금이 어디에 쓰이는지 알고 싶어요.")).setViewHolder(new QuestionHolder(BaseActivity.context))
                .addChild(new TreeNode("서포트 메뉴에서 확인이 가능합니다.")));

        parent.addChildren(children);
        return parent;
    }
}
