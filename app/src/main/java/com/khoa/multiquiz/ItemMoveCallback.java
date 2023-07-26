package com.khoa.multiquiz;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.khoa.multiquiz.adapter.QuestionAdapter;

public class ItemMoveCallback extends ItemTouchHelper.Callback {

    private final QuestionAdapter adapter;

    public ItemMoveCallback(QuestionAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = 0;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder source, @NonNull RecyclerView.ViewHolder target) {
        int fromPosition = source.getAdapterPosition();
        int toPosition = target.getAdapterPosition();
        adapter.onItemMove(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        // Handle swipe events if needed (not used in this example)
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

}
