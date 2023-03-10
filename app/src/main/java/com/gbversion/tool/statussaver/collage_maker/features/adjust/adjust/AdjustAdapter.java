package com.gbversion.tool.statussaver.collage_maker.features.adjust.adjust;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.gbversion.tool.statussaver.R;
import com.gbversion.tool.statussaver.tools.photoeditor.PicImageEditor;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class AdjustAdapter extends RecyclerView.Adapter<AdjustAdapter.ViewHolder> {
    public String FILTER_CONFIG_TEMPLATE = "@adjust brightness 0 @adjust contrast 1 @adjust saturation 1 @adjust sharpen 0";
    public AdjustListener adjustListener;
    private Context context;
    public List<AdjustModel> lstAdjusts;
    public int selectedFilterIndex = 0;

    public AdjustAdapter(Context context2, AdjustListener adjustListener2) {
        this.context = context2;
        this.adjustListener = adjustListener2;
        init();
    }

    public void setSelectedAdjust(int i) {
        this.adjustListener.onAdjustSelected(this.lstAdjusts.get(i));
    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_adjust_view, viewGroup, false));
    }

    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.toolName.setText(this.lstAdjusts.get(i).name);
        viewHolder.icon.setImageDrawable(this.selectedFilterIndex != i ? this.lstAdjusts.get(i).icon : this.lstAdjusts.get(i).selectedIcon);
        if (selectedFilterIndex == i) {
            viewHolder.toolName.setTextColor(ContextCompat.getColor(context, R.color.black));
        } else {
            viewHolder.toolName.setTextColor(Color.parseColor("#bfbfbf"));
        }
    }

    public int getItemCount() {
        return this.lstAdjusts.size();
    }

    public String getFilterConfig() {
        String str = this.FILTER_CONFIG_TEMPLATE;
        return MessageFormat.format(str, this.lstAdjusts.get(0).originValue + "", this.lstAdjusts.get(1).originValue + "", this.lstAdjusts.get(2).originValue + "", Float.valueOf(this.lstAdjusts.get(3).originValue));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView toolName;

        ViewHolder(View view) {
            super(view);
            this.icon = view.findViewById(R.id.icon);
            this.toolName = view.findViewById(R.id.tool_name);
            view.setOnClickListener(view1 -> {
                selectedFilterIndex = ViewHolder.this.getLayoutPosition();
                adjustListener.onAdjustSelected(lstAdjusts.get(selectedFilterIndex));
                notifyDataSetChanged();
            });
        }
    }

    public AdjustModel getCurrentAdjustModel() {
        return this.lstAdjusts.get(this.selectedFilterIndex);
    }

    private void init() {
        this.lstAdjusts = new ArrayList();
        this.lstAdjusts.add(new AdjustModel(0, this.context.getString(R.string.brightness), "brightness",
                this.context.getDrawable(R.drawable.ic_new_unpress_brightness), this.context.getDrawable(R.drawable.ic_new_press_brightness), -1.0f, 0.0f, 1.0f));
        this.lstAdjusts.add(new AdjustModel(1, this.context.getString(R.string.contrast), "contrast",
                this.context.getDrawable(R.drawable.ic_new_unpress_contrast), this.context.getDrawable(R.drawable.ic_new_press_contrast), 0.1f, 1.0f, 3.0f));
        this.lstAdjusts.add(new AdjustModel(2, this.context.getString(R.string.saturation), "saturation",
                this.context.getDrawable(R.drawable.ic_new_unpress_saturation), this.context.getDrawable(R.drawable.ic_new_press_saturation), 0.0f, 1.0f, 3.0f));
        this.lstAdjusts.add(new AdjustModel(3, this.context.getString(R.string.sharpen), "sharpen",
                this.context.getDrawable(R.drawable.ic_new_unpress_sharp), this.context.getDrawable(R.drawable.ic_new_press_sharp), -1.0f, 0.0f, 10.0f));
    }

    public class AdjustModel {
        String code;
        Drawable icon;
        public int index;
        public float intensity;
        public float maxValue;
        public float minValue;
        public String name;
        public float originValue;
        Drawable selectedIcon;
        public float slierIntensity = 0.5f;

        AdjustModel(int i, String str, String str2, Drawable drawable, Drawable drawable2, float f, float f2, float f3) {
            this.index = i;
            this.name = str;
            this.code = str2;
            this.icon = drawable;
            this.minValue = f;
            this.originValue = f2;
            this.maxValue = f3;
            this.selectedIcon = drawable2;
        }

        public void setIntensity(PicImageEditor picImageEditor, float f, boolean z) {
            if (picImageEditor != null) {
                this.slierIntensity = f;
                this.intensity = calcIntensity(f);
                picImageEditor.setFilterIntensityForIndex(this.intensity, this.index, z);
            }
        }


        public float calcIntensity(float f) {
            if (f <= 0.0f) {
                return this.minValue;
            }
            if (f >= 1.0f) {
                return this.maxValue;
            }
            if (f <= 0.5f) {
                return this.minValue + ((this.originValue - this.minValue) * f * 2.0f);
            }
            return this.maxValue + ((this.originValue - this.maxValue) * (1.0f - f) * 2.0f);
        }
    }
}
