package com.hfad.relife.Listener;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by 18359 on 2017/11/20.
 */

public abstract class HidingScrollListener extends RecyclerView.OnScrollListener{

    //设置临界值，超过这个临界值隐藏toolbar和FAB
    private static final int HIDE_THRESHOLD = 20;
    private int scrolledDistance = 0;
    private boolean controlsVisible = true;
    private int mItemSize = 0;

    public HidingScrollListener(int itemSize){
        this.mItemSize = itemSize;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy){
        super.onScrolled(recyclerView,dx,dy);
        int firstVisibleItem = ((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        int lastVisibleItem = ((LinearLayoutManager)recyclerView.getLayoutManager()).findLastVisibleItemPosition();

        if(firstVisibleItem==0||lastVisibleItem==mItemSize){
            if(!controlsVisible){
                onShow();
                controlsVisible = true;
            }
        }else{
            if(scrolledDistance>HIDE_THRESHOLD&&controlsVisible){
                onHide();
                controlsVisible = false;
                scrolledDistance = 0;
            }
        }
        //在toolbar隐藏且上滚的时候或者toolbar未隐藏且下滚的时候
        if ((controlsVisible&&dy>0)||(!controlsVisible&&dy<0)){
            scrolledDistance+=dy;
        }
    }

    public abstract void onHide();
    public abstract void onShow();
}
