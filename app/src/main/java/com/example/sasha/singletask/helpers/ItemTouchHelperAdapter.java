package com.example.sasha.singletask.helpers;

public interface ItemTouchHelperAdapter {
    /*
        * This method will be called when item has been dragged to be moved
        * but still didn't find his destination place.
        * @param fromPosition The start position of the moved item.
        * @param toPosition Then end position of the moved item.
     */
    void onItemMove(int fromPosition, int toPosition);

    /*
        * This method will be called when item was dismissed by swipe.
        * @param position The position of the dismissed item.
     */
    void onItemDismiss(int position);
}

