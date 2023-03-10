package com.gbversion.tool.statussaver.collage_maker.features.college.layout.straight;


import com.gbversion.tool.statussaver.collage_maker.features.college.Line;
import com.gbversion.tool.statussaver.collage_maker.features.college.PuzzleLayout;
import com.gbversion.tool.statussaver.collage_maker.features.college.straight.StraightPuzzleLayout;

public class NineStraightLayout extends NumberStraightLayout {
    public int getThemeCount() {
        return 8;
    }

    public NineStraightLayout(StraightPuzzleLayout straightPuzzleLayout, boolean z) {
        super(straightPuzzleLayout, z);
    }

    public NineStraightLayout(int i) {
        super(i);
    }

    public void layout() {
        switch (this.theme) {
            case 0:
                cutAreaEqualPart(0, 2, 2);
                return;
            case 1:
                addLine(0, Line.Direction.VERTICAL, 0.75f);
                addLine(0, Line.Direction.VERTICAL, 0.33333334f);
                cutAreaEqualPart(2, 4, Line.Direction.HORIZONTAL);
                cutAreaEqualPart(0, 4, Line.Direction.HORIZONTAL);
                return;
            case 2:
                addLine(0, Line.Direction.HORIZONTAL, 0.75f);
                addLine(0, Line.Direction.HORIZONTAL, 0.33333334f);
                cutAreaEqualPart(2, 4, Line.Direction.VERTICAL);
                cutAreaEqualPart(0, 4, Line.Direction.VERTICAL);
                return;
            case 3:
                addLine(0, Line.Direction.HORIZONTAL, 0.75f);
                addLine(0, Line.Direction.HORIZONTAL, 0.33333334f);
                cutAreaEqualPart(2, 3, Line.Direction.VERTICAL);
                addLine(1, Line.Direction.VERTICAL, 0.75f);
                addLine(1, Line.Direction.VERTICAL, 0.33333334f);
                cutAreaEqualPart(0, 3, Line.Direction.VERTICAL);
                return;
            case 4:
                addLine(0, Line.Direction.VERTICAL, 0.75f);
                addLine(0, Line.Direction.VERTICAL, 0.33333334f);
                cutAreaEqualPart(2, 3, Line.Direction.HORIZONTAL);
                addLine(1, Line.Direction.HORIZONTAL, 0.75f);
                addLine(1, Line.Direction.HORIZONTAL, 0.33333334f);
                cutAreaEqualPart(0, 3, Line.Direction.HORIZONTAL);
                return;
            case 5:
                cutAreaEqualPart(0, 3, Line.Direction.VERTICAL);
                addLine(2, Line.Direction.HORIZONTAL, 0.75f);
                addLine(2, Line.Direction.HORIZONTAL, 0.33333334f);
                cutAreaEqualPart(1, 3, Line.Direction.HORIZONTAL);
                addLine(0, Line.Direction.HORIZONTAL, 0.75f);
                addLine(0, Line.Direction.HORIZONTAL, 0.33333334f);
                return;
            case 6:
                cutAreaEqualPart(0, 3, Line.Direction.HORIZONTAL);
                addLine(2, Line.Direction.VERTICAL, 0.75f);
                addLine(2, Line.Direction.VERTICAL, 0.33333334f);
                cutAreaEqualPart(1, 3, Line.Direction.VERTICAL);
                addLine(0, Line.Direction.VERTICAL, 0.75f);
                addLine(0, Line.Direction.VERTICAL, 0.33333334f);
                return;
            case 7:
                addLine(0, Line.Direction.HORIZONTAL, 0.5f);
                cutAreaEqualPart(1, 1, 3);
                return;
            default:
        }
    }

    public PuzzleLayout clone(PuzzleLayout PuzzleLayout) {
        return new NineStraightLayout((StraightPuzzleLayout) PuzzleLayout, true);
    }
}
