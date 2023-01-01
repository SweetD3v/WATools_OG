package com.gbversion.tool.statussaver.collage_maker.features.crop;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gbversion.tool.statussaver.R;
import com.gbversion.tool.statussaver.collage_maker.features.crop.adapter.AspectRatioPreviewAdapter;
import com.isseiaoki.simplecropview.CropImageView;
import com.steelkiwi.cropiwa.AspectRatio;


public class PicsartCropDialogFragment extends DialogFragment implements AspectRatioPreviewAdapter.OnNewSelectedListener {
    private static final String TAG = "CropDialogFrag";
    private Bitmap bitmap;
    private RelativeLayout loadingView;
    public CropImageView mCropView;
    public OnCropPhoto onCropPhoto;

    public interface OnCropPhoto {
        void finishCrop(Bitmap bitmap);
    }

    public void setBitmap(Bitmap bitmap2) {
        this.bitmap = bitmap2;
    }

    public static PicsartCropDialogFragment show(@NonNull AppCompatActivity appCompatActivity, OnCropPhoto onCropPhoto2, Bitmap bitmap2) {
        PicsartCropDialogFragment picsartCropDialogFragment = new PicsartCropDialogFragment();
        picsartCropDialogFragment.setBitmap(bitmap2);
        picsartCropDialogFragment.setOnCropPhoto(onCropPhoto2);
        picsartCropDialogFragment.show(appCompatActivity.getSupportFragmentManager(), TAG);
        return picsartCropDialogFragment;
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    public void setOnCropPhoto(OnCropPhoto onCropPhoto2) {
        this.onCropPhoto = onCropPhoto2;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setRetainInstance(true);
    }

    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.dialog_bg_black));
        }
    }

    @Nullable
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View inflate = layoutInflater.inflate(R.layout.crop_layout, viewGroup, false);
        AspectRatioPreviewAdapter aspectRatioPreviewAdapter = new AspectRatioPreviewAdapter();
        aspectRatioPreviewAdapter.setListener(this);
        RecyclerView recyclerView = inflate.findViewById(R.id.fixed_ratio_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        recyclerView.setAdapter(aspectRatioPreviewAdapter);
        this.mCropView = inflate.findViewById(R.id.crop_view);
        this.mCropView.setCropMode(CropImageView.CropMode.FREE);
        inflate.findViewById(R.id.rotate).setOnClickListener(view -> PicsartCropDialogFragment.this.mCropView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D));

        inflate.findViewById(R.id.imgSave).setOnClickListener(view -> new OnSaveCrop().execute(new Void[0]));
        this.loadingView = inflate.findViewById(R.id.loadingView);
        this.loadingView.setVisibility(View.GONE);
        inflate.findViewById(R.id.imgClose).setOnClickListener(view -> PicsartCropDialogFragment.this.dismiss());
        return inflate;
    }

    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.mCropView = view.findViewById(R.id.crop_view);
        this.mCropView.setImageBitmap(this.bitmap);
    }

    public void onNewAspectRatioSelected(AspectRatio aspectRatio) {
        if (aspectRatio.getWidth() == 10 && aspectRatio.getHeight() == 10) {
            this.mCropView.setCropMode(CropImageView.CropMode.FREE);
        } else {
            this.mCropView.setCustomRatio(aspectRatio.getWidth(), aspectRatio.getHeight());
        }
    }

    class OnSaveCrop extends AsyncTask<Void, Bitmap, Bitmap> {
        OnSaveCrop() {
        }

        public void onPreExecute() {
            PicsartCropDialogFragment.this.showLoading(true);
        }

        public Bitmap doInBackground(Void... voidArr) {
            return PicsartCropDialogFragment.this.mCropView.getCroppedBitmap();
        }

        public void onPostExecute(Bitmap bitmap) {
            PicsartCropDialogFragment.this.showLoading(false);
            PicsartCropDialogFragment.this.onCropPhoto.finishCrop(bitmap);
            PicsartCropDialogFragment.this.dismiss();
        }
    }

    public void showLoading(boolean z) {
        if (z) {
            getActivity().getWindow().setFlags(16, 16);
            this.loadingView.setVisibility(View.VISIBLE);
            return;
        }
        getActivity().getWindow().clearFlags(16);
        this.loadingView.setVisibility(View.GONE);
    }
}
