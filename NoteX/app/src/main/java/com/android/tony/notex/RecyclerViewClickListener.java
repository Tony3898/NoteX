package com.android.tony.notex;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;


public class RecyclerViewClickListener implements RecyclerView.OnItemTouchListener{

    private GestureDetector gestureDetector;
    private ClickListener clickListener;

    RecyclerViewClickListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener)
    {
        this.clickListener = clickListener;
        gestureDetector = new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                return true;
            }



            @Override
            public void onLongPress(MotionEvent motionEvent) {
                View view = recyclerView.findChildViewUnder(motionEvent.getX(),motionEvent.getY());
                if(view!=null && clickListener!=null)
                    clickListener.onLongClick(view,recyclerView.getChildAdapterPosition(view));
            }
        });
    }
    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
        View view = recyclerView.findChildViewUnder(motionEvent.getX(),motionEvent.getY());
        if(view!=null && clickListener!=null && gestureDetector.onTouchEvent(motionEvent))
            clickListener.onClick(view,recyclerView.getChildAdapterPosition(view));
        return false;
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean b) {

    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }
}
