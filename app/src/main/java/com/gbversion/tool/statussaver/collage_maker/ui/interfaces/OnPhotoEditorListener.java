package com.gbversion.tool.statussaver.collage_maker.ui.interfaces;

import com.gbversion.tool.statussaver.tools.photoeditor.BrushViewType;

public interface OnPhotoEditorListener {
    void onAddViewListener(BrushViewType brushViewType, int i);


    void onRemoveViewListener(int i);

    void onRemoveViewListener(BrushViewType brushViewType, int i);

    void onStartViewChangeListener(BrushViewType brushViewType);

    void onStopViewChangeListener(BrushViewType brushViewType);
}
