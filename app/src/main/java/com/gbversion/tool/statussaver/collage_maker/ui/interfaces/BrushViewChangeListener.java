package com.gbversion.tool.statussaver.collage_maker.ui.interfaces;

import com.gbversion.tool.statussaver.tools.photoeditor.BrushDrawingView;

public interface BrushViewChangeListener {
    void onStartDrawing();

    void onStopDrawing();

    void onViewAdd(BrushDrawingView brushDrawingView);

    void onViewRemoved(BrushDrawingView brushDrawingView);
}
