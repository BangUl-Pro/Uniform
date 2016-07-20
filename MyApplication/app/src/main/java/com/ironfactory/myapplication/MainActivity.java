package com.ironfactory.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    TextView textView;
    TextView textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView  = (TextView) findViewById(R.id.ttt);
         textView2  = (TextView) findViewById(R.id.ttt2);

        checkPermission();
    }


    private void setFile() {
        List<File> files = new ArrayList<>();
        File[] files2;
        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();

//        files = searchAllFile(new File("/storage/"), "andrew.avi");
        files2 = new File("/storage/").listFiles();
        for (int i = 0; i < files2.length; i++) {
            sb.append(files2[i].getPath() + "\n");
        }

        files2 = new File("/storage/").listFiles();
        for (int i = 0; i < files2.length; i++) {
            if (!files2[i].getName().equals("emulated")) {
                File file = new File(files2[i].getPath() + "/");
                Log.d(TAG, "file = " + file.getPath());
                File[] tempFiles = file.listFiles();
                if (tempFiles != null) {
                    for (int j = 0; j < tempFiles.length; j++) {
                        if (tempFiles[j].getName().equals("AllInOne")) {
                            File allInOneFile = new File(tempFiles[j].getPath());
                            sb2.append(allInOneFile.getPath() + "\n");
                            File[] allInOneFiles = allInOneFile.listFiles();
                            for (int k = 0; k < allInOneFiles.length; k++) {
                                sb2.append(allInOneFiles[k].getPath() + "\n");
                            }
                        }
                    }
                }
            }
        }

        textView.setText(sb.toString());
        textView2.setText(sb2.toString());
    }




    public static List<File> searchAllFile(File fileList, String endStr){
        List<File> curFileList = new ArrayList<>();
        File[] list = fileList.listFiles();

        if(list==null)
            return null;
        for(File file : list){
            if (file.isDirectory()) {
                if (file.getName().equals("emulated"))
                    continue;
                List<File> curFiles = searchAllFile(file, endStr);
                if (curFiles != null && curFiles.size() > 0) {
                    curFileList.addAll(curFiles);
                }
            }

            if(file.getName().endsWith(endStr)) {
                curFileList.add(file);
//                Log.d(",,", "filename = " + file.getName());
            }
        }
        return curFileList;
    }

    final int MY_PERMISSION_REQUEST_STORAGE = 300;

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // Explain to the user why we need to write the permission.
                    Toast.makeText(this, "Read/Write external storage", Toast.LENGTH_SHORT).show();
                }

                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSION_REQUEST_STORAGE);

                // MY_PERMISSION_REQUEST_STORAGE is an
                // app-defined int constant

            } else {
                // 다음 부분은 항상 허용일 경우에 해당이 됩니다.
                setFile();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    setFile();

                    // permission was granted, yay! do the
                    // calendar task you need to do.

                } else {

                    Log.d(TAG, "Permission always deny");

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
        }
    }
}
