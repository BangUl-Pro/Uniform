package com.ironfactory.donation.controllers.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ironfactory.donation.Global;
import com.ironfactory.donation.R;
import com.ironfactory.donation.controllers.views.AttachedImageRecyclerView;
import com.ironfactory.donation.controllers.views.WriterView;
import com.ironfactory.donation.dtos.TimelineCardDto;
import com.ironfactory.donation.entities.FileEntity;
import com.ironfactory.donation.entities.UserEntity;
import com.ironfactory.donation.socketIo.SocketException;
import com.ironfactory.donation.socketIo.SocketService;

import java.io.File;
import java.util.ArrayList;

import nl.changer.polypicker.ImagePickerActivity;

public class TimelineWriteActivity extends BaseActivity {
    private static final String TAG = "TimelineWriteActivity";

    private class ViewHolder {
        public WriterView writerView;
        public EditText timelineContents;
        public AttachedImageRecyclerView selectedImages;
        public Button attachPhotoButton;

        public ViewHolder(View view) {
            writerView = (WriterView) view.findViewById(R.id.writer_view);
            timelineContents = (EditText) view.findViewById(R.id.contents);
            selectedImages = (AttachedImageRecyclerView) view.findViewById(R.id.selected_images);
            attachPhotoButton = (Button) view.findViewById(R.id.attach_photo);
        }
    }

    private ViewHolder viewHolder;
    private Boolean isUpdate;
    private TimelineCardDto timelineCardDtoForUpdate;

    private static final int INTENT_REQUEST_GET_IMAGES = 1;
    private static final int MAXIMUM_IMAGES = 10;
    private Menu menu;
    private ArrayList<Uri> selectedImageUris;
//    private TimelineInsertCallback timelineInsertCallback = new TimelineInsertCallback();
//    private FileUploadCallback fileUploadCallback = new FileUploadCallback();
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "액티비티 시작");

        isUpdate = getIntent().getBooleanExtra("isUpdate", false);

        context = this;
        selectedImageUris = new ArrayList<>();
        initViews(R.layout.activity_timeline_write);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, 0, 0, "쓰기").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        if (this.menu == null) {
            this.menu = menu;
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case 0:
                String contents = viewHolder.timelineContents.getText().toString();
                if (contents.isEmpty()) {
                    new MaterialDialog.Builder(BaseActivity.context)
                            .title(R.string.app_name)
                            .content("내용을 입력해주세요.")
                            .show();
                    return true;
                }

                UserEntity userEntity = Global.userEntity;

                onWriteBefore();
                if (isUpdate) {
                    timelineCardDtoForUpdate.timelineEntity.contents = contents;
                    Intent intent = new Intent(getApplicationContext(), SocketService.class);
                    intent.putExtra(Global.COMMAND, Global.DELETE_FILE);
                    intent.putExtra(Global.FILE, timelineCardDtoForUpdate.fileEntities);
                    startService(intent);

                    intent = new Intent(getApplicationContext(), SocketService.class);
                    intent.putExtra(Global.COMMAND, Global.UPDATE_TIMELINE);
                    intent.putExtra(Global.TIMELINE, timelineCardDtoForUpdate);
                    startService(intent);

//                    RequestManager.deleteFileEntities(timelineCardDtoForUpdate.fileEntities);
//                    RequestManager.updateTimeline(timelineCardDtoForUpdate, timelineInsertCallback);
                } else {
                    Intent intent = new Intent(getApplicationContext(), SocketService.class);
                    intent.putExtra(Global.COMMAND, Global.INSERT_TIMELINE);
                    intent.putExtra(Global.SCHOOL_ID, userEntity.schoolId);
                    intent.putExtra(Global.TIMELINE_CONTENT, contents);
                    startService(intent);

//                    RequestManager.insertTimeline(userEntity.schoolId, contents, timelineInsertCallback);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onWriteBefore() {
        menu.getItem(0).setEnabled(false);
        showLoadingView();
    }

    private void onWriteAfter(boolean isSuccess) {
        menu.getItem(0).setEnabled(true);
        hideLoadingView();
        if (isSuccess) {
            finish();
        } else {
            new MaterialDialog.Builder(BaseActivity.context)
                    .title(R.string.app_name)
                    .content("타임라인을 쓰는 중에 문제가 발생하였습니다.")
                    .show();
        }
    }

    @Override
    protected void initViews(int layoutResID) {
        setContentView(layoutResID);

        // 액션바 설정
        getSupportActionBar().setTitle("타임라인 쓰기");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewHolder = new ViewHolder(getWindow().getDecorView());

        viewHolder.attachPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ImagePickerActivity.class);
                intent.putExtra(ImagePickerActivity.EXTRA_SELECTION_LIMIT, MAXIMUM_IMAGES);
                startActivityForResult(intent, INTENT_REQUEST_GET_IMAGES);
            }
        });

        if (isUpdate) {
            timelineCardDtoForUpdate = getIntent().getParcelableExtra("timelineCardDto");
            viewHolder.timelineContents.setText(timelineCardDtoForUpdate.timelineEntity.contents);
            viewHolder.writerView.setUserEntity(timelineCardDtoForUpdate.userEntity);
            viewHolder.writerView.setWrittenTime(timelineCardDtoForUpdate.timelineEntity.created);

            for (FileEntity fileEntity : timelineCardDtoForUpdate.fileEntities) {
//                Uri uri = Uri.fromFile(new File(BaseActivity.context.getCacheDir() + fileEntity.uuid));
                Uri uri = Uri.fromFile(new File(BaseActivity.context.getCacheDir() + fileEntity.id));
                selectedImageUris.add(uri);
            }
            viewHolder.selectedImages.setUris(selectedImageUris);
        } else {
//            viewHolder.writerView.setUserEntity(new UserEntity(Baas.io().getSignedInUser()));
            viewHolder.writerView.setUserEntity(Global.userEntity);
            viewHolder.writerView.setWrittenTime(System.currentTimeMillis());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resuleCode, Intent intent) {
        super.onActivityResult(requestCode, resuleCode, intent);

        if (resuleCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case INTENT_REQUEST_GET_IMAGES:
                Parcelable[] parcelableUris = intent.getParcelableArrayExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);

                if (parcelableUris == null) {
                    return;
                }

                selectedImageUris.clear();
                for (Parcelable parcelableUri : parcelableUris) {
                    selectedImageUris.add(Uri.parse(parcelableUri.toString()));
                }

                viewHolder.selectedImages.setUris(selectedImageUris);
                break;
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        if (intent != null) {
            String command = intent.getStringExtra(Global.COMMAND);
            if (command != null) {
                int code = intent.getIntExtra(Global.CODE, -1);
                if (code != -1) {
                    SocketException.printErrMsg(code);
                    SocketException.toastErrMsg(code);
                    if (command.equals(Global.UPDATE_TIMELINE)) {
                        // 타임라인 업데이트
                        processInsertTimeline(code, intent);
                    } else if (command.equals(Global.INSERT_TIMELINE)) {
                        // 타임라인 글 쓰기
                        processInsertTimeline(code, intent);
                    }
                }
            }
        }
    }


    // TODO: 15. 11. 28. 타임라인 삽입 응답
    private void processInsertTimeline(int code, Intent intent) {
        if (code == SocketException.SUCCESS) {
            // 성공
            if (0 < selectedImageUris.size()) {
                Uri selectedUri = selectedImageUris.get(0);
                selectedImageUris.remove(0);

                TimelineCardDto timelineCardDto = intent.getParcelableExtra(Global.TIMELINE);
//                String parentUuid = baasioEntity.getUuid().toString();
                final String parentUuid = timelineCardDto.timelineEntity.id;

                Intent intent1 = new Intent(getApplicationContext(), SocketService.class);
                intent1.putExtra(Global.COMMAND, Global.INSERT_FILE);
                intent1.putExtra(Global.PRODUCT_ID, parentUuid);
                intent1.putExtra(Global.PATH, selectedUri);
                startActivity(intent1);

                Global.OnInsertFile = new Global.onInsertFile() {
                    @Override
                    public void onSuccess() {
                        if (0 < selectedImageUris.size()) {
                            Uri selectedUri = selectedImageUris.get(0);
                            selectedImageUris.remove(0);

//                            String parentUuid = baasioFile.getProperty("parent_uuid").asText();
                            Intent intent = new Intent(getApplicationContext(), SocketService.class);
                            intent.putExtra(Global.COMMAND, Global.INSERT_FILE);
                            intent.putExtra(Global.PRODUCT_ID, parentUuid);
                            intent.putExtra(Global.PATH, selectedUri);
                            startActivity(intent);
                        } else {
                            onWriteAfter(true);
                        }
                    }

                    @Override
                    public void onException(int code) {
                        onWriteAfter(false);
                    }
                };
//                RequestManager.insertFile(parentUuid, selectedUri, fileUploadCallback);
            } else {
                onWriteAfter(true);
            }
        } else {
            // 실패
            onWriteAfter(false);
        }
    }


//    private class TimelineInsertCallback implements BaasioCallback<BaasioEntity> {
//        @Override
//        public void onResponse(BaasioEntity baasioEntity) {
//            if (0 < selectedImageUris.size()) {
//                Uri selectedUri = selectedImageUris.get(0);
//                selectedImageUris.remove(0);
//
//                String parentUuid = baasioEntity.getUuid().toString();
//                RequestManager.insertFile(parentUuid, selectedUri, fileUploadCallback);
//            } else {
//                onWriteAfter(true);
//            }
//        }
//
//        @Override
//        public void onException(BaasioException e) {
//            onWriteAfter(false);
//        }
//    }

//    private class FileUploadCallback implements BaasioUploadCallback {
//        @Override
//        public void onResponse(BaasioFile baasioFile) {
//            if (0 < selectedImageUris.size()) {
//                Uri selectedUri = selectedImageUris.get(0);
//                selectedImageUris.remove(0);
//
//                String parentUuid = baasioFile.getProperty("parent_uuid").asText();
//                RequestManager.insertFile(parentUuid, selectedUri, fileUploadCallback);
//            } else {
//                onWriteAfter(true);
//            }
//        }
//
//        @Override
//        public void onException(BaasioException e) {
//            onWriteAfter(false);
//        }
//
//        @Override
//        public void onProgress(long l, long l2) {
//             TODO 진행상황 표시
//        }
//    }
}
