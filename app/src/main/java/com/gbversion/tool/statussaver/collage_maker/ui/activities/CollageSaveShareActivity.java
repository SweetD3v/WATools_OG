package com.gbversion.tool.statussaver.collage_maker.ui.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.gbversion.tool.statussaver.R;
import com.gbversion.tool.statussaver.databinding.CollageSaveLayoutBinding;
import com.gbversion.tool.statussaver.tools.BaseActivity;
import com.gbversion.tool.statussaver.tools.mycreation.MyCreationToolsActivity;
import com.gbversion.tool.statussaver.tools.photoeditor.PicEditorHomeActivity;
import com.gbversion.tool.statussaver.utils.AdsUtils;
import com.gbversion.tool.statussaver.utils.NetworkState;

import java.io.File;

public class CollageSaveShareActivity extends BaseActivity {
    CollageSaveLayoutBinding binding;

    ImageView preview;

    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        binding = CollageSaveLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (NetworkState.Companion.isOnline()) {
//            AdsUtils.Companion.loadBanner(this, binding.bannerContainer,
//                    getString(R.string.banner_id_details));
            AdsUtils.Companion.loadNativeProgress(
                    this,
                    getString(R.string.admob_native_id),
                    binding.adFrame,
                    binding.adProgress
            );
        }

        preview = findViewById(R.id.preview);

        binding.imgBack.setOnClickListener(v -> onBackPressed());

        binding.btnMyCreation.setOnClickListener(v -> {
            Intent intent = new Intent(CollageSaveShareActivity.this, MyCreationToolsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            if (getIntent().getIntExtra("type", 0) == 0) {
                intent.putExtra(MyCreationToolsActivity.CREATION_TYPE, "photo_editor");
            } else {
                intent.putExtra(MyCreationToolsActivity.CREATION_TYPE, "collage_maker");
            }
            startActivity(intent);
        });

        String string = getIntent().getStringExtra("path");
        Log.e("TAG", "onCreatePath: " + string);
        File file = new File(string);
        Glide.with(this).load(file).into(preview);

        final ImagePopup imagePopup = new ImagePopup(this);
        imagePopup.setWindowHeight(800);
        imagePopup.setWindowWidth(800);
        imagePopup.setBackgroundColor(Color.TRANSPARENT);
        imagePopup.setFullScreen(true);
        imagePopup.setHideCloseIcon(true);
        imagePopup.setImageOnClickClose(true);
        imagePopup.initiatePopupWithGlide(string);

//        findViewById(R.id.shareLayout).setOnClickListener(view -> {
//            Intent intent = new Intent("android.intent.action.SEND");
//            intent.setType("image/*");
//            intent.putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".provider", file));
//            startActivity(Intent.createChooser(intent, "Share"));
//        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        }
        finish();
        return true;
    }

    public void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        Intent intent;
        if (getIntent().getIntExtra("type", 0) == 0) {
            intent = new Intent(CollageSaveShareActivity.this, PicEditorHomeActivity.class);
        } else {
            intent = new Intent(CollageSaveShareActivity.this, CollageMakerHomeActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        AdsUtils.Companion.destroyBanner();
        super.onDestroy();
    }
}
