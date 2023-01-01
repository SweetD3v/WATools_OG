package com.gbversion.tool.statussaver.collage_maker.ui.adapters;

import com.steelkiwi.cropiwa.AspectRatio;

class CustomAspectRatio extends AspectRatio {
    private int selectedIem;
    private int unselectItem;
    String ratioText;

    CustomAspectRatio(int from, int to, int unselectItem, int selectedIem, String ratioText) {
        super(from, to);
        this.selectedIem = selectedIem;
        this.unselectItem = unselectItem;
        this.ratioText = ratioText;
    }

    public int getSelectedIem() {
        return this.selectedIem;
    }

    public int getUnselectItem() {
        return this.unselectItem;
    }

    public void setRatioText(String ratioText) {
        this.ratioText = ratioText;
    }

    public String getRatioText() {
        return ratioText;
    }
}
