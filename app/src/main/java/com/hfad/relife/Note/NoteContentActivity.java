package com.hfad.relife.Note;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.view.MenuItem;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Toast;

import com.hfad.relife.R;
import com.hfad.relife.Util.DateUtil;
import com.hfad.relife.Util.ImageUtils;
import com.hfad.relife.Util.SDCardUtil;
import com.hfad.relife.Util.ScreenUtils;
import com.hfad.relife.Util.StringUtils;
import com.sendtion.xrichtext.RichTextEditor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.iwf.photopicker.PhotoPicker;
import rx.Subscriber;
import rx.Subscription;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class NoteContentActivity extends AppCompatActivity {

    private ProgressDialog loadingDialog;//加载数据时提示
    private ProgressDialog insertDialog;//插入图片时提示
    private int screenWidth;
    private int screenHeight;
    private Subscription subsLoading;
    private Subscription subsInsert;

    private Toolbar mToolbar;
    private RichTextEditor mEtNoteContent; //笔记内容
    private ScrollView mScrollView;
    private Note mNote;
    private boolean isImportant = true; //笔记是否有星号
    private Intent mIntent;
    private int mNoteID;

    private static final int REQUEST_CODE = 0;
    private boolean isRequireCheck; //是否需要系统权限检测
    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    private static final int PERMISSION_REQUEST_CODE = 0;
    private static final String PACKAGE_URL_SCHEMA = "package:";
    //private static int REQUEST_ORIGINAL = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_note);
        mEtNoteContent = (RichTextEditor) findViewById(R.id.et_note_content);

        mScrollView = (ScrollView) findViewById(R.id.scrollview_note_content);
        mIntent = this.getIntent();

        //获取屏幕大小
        screenWidth = ScreenUtils.getScreenWidth(this);
        screenHeight = ScreenUtils.getScreenHeight(this);

        //加载时提示
        insertDialog = new ProgressDialog(this);
        insertDialog.setMessage("正在插入图片...");
        insertDialog.setCanceledOnTouchOutside(false);

        loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage("图片解析中...");
        loadingDialog.setCanceledOnTouchOutside(false);

        if ((mNote = (Note) mIntent.getSerializableExtra("note")) != null) {
            mNoteID = mNote.getId();
            mEtNoteContent.post(new Runnable() {
                @Override
                public void run() {
                    mEtNoteContent.clearAllLayout();
                    showDataSync(mNote.getContent());
                }
            });
            isImportant = mNote.getImportant() == 1 ? true : false;
            Log.d("NoteContentActivity", mNote.getContent() + isImportant);
        }
        initToolbar(isImportant);
        isRequireCheck = true;

        //解决相机拍照出现的问题
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }


    /**
     * 初始化toolbar
     */
    private void initToolbar(boolean isImportant) {
        mToolbar = (Toolbar) findViewById(R.id.toolbar_note_content);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_star).setChecked(isImportant);
        setItemIcon(menu.findItem(R.id.action_star), isImportant);

        return super.onPrepareOptionsMenu(menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_star://星型按钮
                if (item.isChecked()) {
                    item.setChecked(false);
                    setItemIcon(item, false);
                    Snackbar.make(mScrollView,R.string.not_important,Snackbar.LENGTH_SHORT).show();
                } else {
                    item.setChecked(true);
                    setItemIcon(item, true);
                    Snackbar.make(mScrollView,R.string.important,Snackbar.LENGTH_SHORT).show();
                }
                break;
            case R.id.action_save://保存按钮
                if (mNote != null) {//编辑之后的便签，需要保存
                    String content = getEditData();//以Html格式获取编辑过后的文本
                    Note editNote = new Note(mNote.getId(), content, isImportant ? 1 : 0,DateUtil.formatDateTime());
                    int result=NoteActivity.mNoteDbAdapter.updateNote(editNote);
                    Log.d("NoteContentActivity", "在数据库更新数据结果为：" + result);

                    finish();
                    break;
                } else {//新建便签
                    if (TextUtils.isEmpty(getEditData().toString())) {
                        Toast.makeText(NoteContentActivity.this, R.string.not_empty, Toast.LENGTH_SHORT).show();
                        Snackbar.make(mScrollView, R.string.not_empty, Snackbar.LENGTH_SHORT).show();
                        break;
                    } else {
                        String content = getEditData();
                        long result = NoteActivity.mNoteDbAdapter.createNote(content, isImportant, DateUtil.formatDateTime());
                        Log.d("NoteContentActivity", "向数据库中插入数据结果为：" + result);
                        finish();
                        break;
                    }
                }
            case R.id.action_take_photo:
                if (isRequireCheck) {
                    //权限没有授权，进入授权界面
                    if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_CODE);
                    }else{
                        callGallery();
                    }
                }else{
                    isRequireCheck = true;
                    callGallery();
                }
                break;
        }
        return  super.onOptionsItemSelected(item);
    }

    //设置星型的图片
    private void setItemIcon(MenuItem item, boolean isImportant) {
        item.setIcon(isImportant ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);
        this.isImportant = isImportant;
    }

    private String getEditData() {
        List<RichTextEditor.EditData> editList = mEtNoteContent.buildEditData();
        StringBuffer content = new StringBuffer();
        for (RichTextEditor.EditData itemData : editList) {
            if (itemData.inputStr != null) {
                content.append(itemData.inputStr);
            } else if (itemData.imagePath != null) {
                content.append("<img src=\"").append(itemData.imagePath).append("\"/>");
            }
        }
        return content.toString();
    }

    //使用Rxjava异步数据库中加载数据
    private void showDataSync(final String html){
        loadingDialog.show();

        subsLoading = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                showEditData(subscriber, html);
            }
        })
          .onBackpressureBuffer()
          .subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())//消费事件在主线程
          .subscribe(new Observer<String>(){
              @Override
              public void onCompleted(){
                  loadingDialog.dismiss();
              }

              @Override
              public void onError(Throwable e){
                  loadingDialog.dismiss();
                  Toast.makeText(NoteContentActivity.this, "解析错误：图片不存在或已经损坏", Toast.LENGTH_SHORT).show();
              }

              @Override
              public void onNext(String text){
                  if(text.contains(SDCardUtil.getPictureDir())){
                      mEtNoteContent.addImageViewAtIndex(mEtNoteContent.getLastIndex(),text);
                  }else{
                      mEtNoteContent.addEditTextAtIndex(mEtNoteContent.getLastIndex(),text);
                  }
              }
          });
    }

    protected void showEditData(Subscriber<? super String> subscriber, String html){
        try{
            List<String>textList = StringUtils.cutStringByImgTag(html);
            for(int i=0;i<textList.size();i++){
                String text = textList.get(i);
                if(text.contains("<img")){
                    String imagePath = StringUtils.getImgSrc(text);
                    if(new File(imagePath).exists()){
                        subscriber.onNext(imagePath);
                    }else{
                        Toast.makeText(NoteContentActivity.this, "图片"+i+"已丢失，请重新插入", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    subscriber.onNext(text);
                }
            }
            subscriber.onCompleted();
        }catch (Exception e){
            e.printStackTrace();
            subscriber.onError(e);
        }
    }

    private void insertImagesSync(final Intent data){
        insertDialog.show();

        subsInsert = Observable.create(new Observable.OnSubscribe<String>(){
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try{
                    mEtNoteContent.measure(0,0);
                    int width = ScreenUtils.getScreenWidth(NoteContentActivity.this);
                    int height = ScreenUtils.getScreenHeight(NoteContentActivity.this);
                    ArrayList<String> photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                    //同时插入多张图片
                    for(String imagePath:photos){
                        Log.i("NewActivity", "###path=" + imagePath);
                        Bitmap bitmap = ImageUtils.getSmallBitmap(imagePath,width,height);
                        imagePath = SDCardUtil.saveToSdCard(bitmap);
                        subscriber.onNext(imagePath);
                    }
                    subscriber.onCompleted();
                }catch(Exception e){
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        })
        .onBackpressureBuffer()
        .subscribeOn(Schedulers.io())//生产事件在io
        .observeOn(AndroidSchedulers.mainThread())//消费事件在UI线程
        .subscribe(new Observer<String>(){
            @Override
            public void onCompleted(){
                insertDialog.dismiss();
                mEtNoteContent.addEditTextAtIndex(mEtNoteContent.getLastIndex()," ");
                Toast.makeText(NoteContentActivity.this, "图片插入成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable e){
                insertDialog.dismiss();
                Toast.makeText(NoteContentActivity.this, "图片插入失败:"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(String imagePath){
                mEtNoteContent.insertImage(imagePath,mEtNoteContent.getMeasuredWidth());
            }
        });
    }

    private void callGallery(){
        PhotoPicker.builder()
                .setPhotoCount(5)
                .setShowCamera(true)
                .setShowGif(true)
                .setPreviewEnabled(true)
                .start(this,PhotoPicker.REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode==RESULT_OK){
            if(requestCode==PhotoPicker.REQUEST_CODE){
                insertImagesSync(data);
            }
        }
    }
}