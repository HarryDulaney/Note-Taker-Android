package com.ethical_techniques.notemaker.decorators;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SpacingItemDecoration extends RecyclerView.ItemDecoration {
    private int spacingPx;
    private boolean addStartSpacing;
    private boolean addEndSpacing;

    public SpacingItemDecoration(int spacingPx) {
        this(spacingPx, false, false);
    }

    public SpacingItemDecoration(int spacingPx, boolean addStartSpacing, boolean addEndSpacing) {
        this.spacingPx = spacingPx;
        this.addStartSpacing = addStartSpacing;
        this.addEndSpacing = addEndSpacing;
    }

    @Override
    public void getItemOffsets(@NotNull Rect outRect, @NotNull View view, @NotNull RecyclerView parent, @NotNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (spacingPx <= 0) {
            return;
        }

        if (addStartSpacing && parent.getChildLayoutPosition(view) < 1 || parent.getChildLayoutPosition(view) >= 1) {
            if (getOrientation(parent) == LinearLayoutManager.VERTICAL) {
                outRect.top = spacingPx;
            } else {
                outRect.left = spacingPx;
            }
        }

        if (addEndSpacing && parent.getChildAdapterPosition(view) == getTotalItemCount(parent) - 1) {
            if (getOrientation(parent) == LinearLayoutManager.VERTICAL) {
                outRect.bottom = spacingPx;
            } else {
                outRect.right = spacingPx;
            }
        }
    }

    private int getTotalItemCount(RecyclerView parent) {
        return Objects.requireNonNull(parent.getAdapter()).getItemCount();
    }

    private int getOrientation(RecyclerView parent) {
        if (parent.getLayoutManager() instanceof LinearLayoutManager) {
            return ((LinearLayoutManager) parent.getLayoutManager()).getOrientation();
        } else {
            throw new IllegalStateException("SpacingItemDecoration can only be used with a LinearLayoutManager.");
        }
    }
}