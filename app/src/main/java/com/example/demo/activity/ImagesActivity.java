package com.example.demo.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.example.demo.ImageUtils;
import com.example.demo.R;
import com.example.demo.data.Card;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;

public class ImagesActivity extends AppCompatActivity {
    ImageView back_btn;

    private Card card;
    private String cardId;
    private PhotoView imageView;
    private TextView card_author,title;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);
        imageView = findViewById(R.id.imageView);
        card_author = findViewById(R.id.card_author);


        // 获取传递的卡片ID
        //cardId = getIntent().getStringExtra("card_id");
        card = getIntent().getParcelableExtra("card");
        if(card!=null){
            updateUI2(card);
        }
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ImagesActivity.this);
                builder.setMessage("是否要下载到本地？")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // 用户点击了“是”，执行下载操作
                                downloadImage(card.getImg_url());
                            }
                        })
                        .setNegativeButton("否", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // 用户点击了“否”，关闭对话框
                                dialog.dismiss();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();

                return true;
            }


        });


        back_btn = findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void downloadImage(String imageUrl) {
        // 使用 Glide 加载图片，并将其保存到本地
        Glide.with(ImagesActivity.this)
                .asFile()
                .load(imageUrl)
                .into(new Target<File>() {
                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        // 图片加载开始时的操作
                    }

                    @Override
                    public void onLoadFailed(Drawable errorDrawable) {
                        // 图片加载失败时的操作
                        Toast.makeText(ImagesActivity.this, "图片加载失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResourceReady(File resource, Transition<? super File> transition) {
                        // 图片加载成功时的操作
                        saveImageToLocal(resource);
                        Toast.makeText(ImagesActivity.this, "图片加载成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onLoadCleared(Drawable placeholder) {
                        // 图片加载被清除时的操作
                    }

                    @Override
                    public void getSize(SizeReadyCallback cb) {
                        // 获取图片大小的操作
                    }

                    @Override
                    public void removeCallback(SizeReadyCallback cb) {
                        // 移除回调的操作
                    }

                    @Override
                    public void setRequest(Request request) {
                        // 设置请求的操作
                    }

                    @Override
                    public Request getRequest() {
                        // 获取请求的操作
                        return null;
                    }

                    @Override
                    public void onStart() {
                        // 开始的操作
                    }

                    @Override
                    public void onStop() {
                        // 停止的操作
                    }

                    @Override
                    public void onDestroy() {
                        // 销毁的操作
                    }
                });
    }
    private void saveImageToLocal(File imageFile) {
        // 保存图片到本地图库
        String imagePath = ImageUtils.saveImageToGallery(ImagesActivity.this, imageFile);
        if (imagePath != null) {
            // 发送广播通知系统图库扫描新建的图片文件
            Uri uri = Uri.fromFile(new File(imagePath));
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            Toast.makeText(ImagesActivity.this, "图片已保存至本地", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(ImagesActivity.this, "保存图片失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUI2(Card card) {

        String temptxt = card.getAuthor();
        if(temptxt.length()>20){
            temptxt=temptxt.substring(0, 15);
            temptxt+="...";
        }
        card_author.setText(temptxt);
        Glide.with(this)
                .load(card.getImg_url())
                .error(R.drawable.rounded_box) // 加载失败显示的图像
                .into(imageView);




    }
}
