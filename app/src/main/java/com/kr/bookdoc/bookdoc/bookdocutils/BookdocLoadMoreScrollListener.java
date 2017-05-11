package com.kr.bookdoc.bookdoc.bookdocutils;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;


public abstract class BookdocLoadMoreScrollListener extends RecyclerView.OnScrollListener {

    private int visibleThreshold = 10;

    private int currentPage = 1;

    private int previousTotalItemCount = 0;

    private boolean loading = true;

    private int startingPageIndex = 1;

    int firstVisibleItem, visibleItemCount, totalItemCount;

    boolean checkLinear = false;

    RecyclerView.LayoutManager mLayoutManager;

    public BookdocLoadMoreScrollListener(StaggeredGridLayoutManager layoutManager) {
        this.mLayoutManager = layoutManager;
        visibleThreshold = visibleThreshold * layoutManager.getSpanCount();
    }
    public BookdocLoadMoreScrollListener(GridLayoutManager layoutManager) {
        this.mLayoutManager = layoutManager;
        visibleThreshold = visibleThreshold * layoutManager.getSpanCount();
    }

    public BookdocLoadMoreScrollListener(LinearLayoutManager layoutManager) {
        this.mLayoutManager = layoutManager;
        checkLinear = true;
    }

    public int getLastVisibleItem(int[] lastVisibleItemPositions) {
        int maxSize = 0;
        for (int i = 0; i < lastVisibleItemPositions.length; i++) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i];
            }
            else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i];
            }
        }
        return maxSize;
    }

    @Override
    public void onScrolled(RecyclerView view, int dx, int dy) {
        if(!checkLinear){
            int lastVisibleItemPosition;
            int totalItemCount = mLayoutManager.getItemCount();
            int[] lastVisibleItemPositions = ((StaggeredGridLayoutManager) mLayoutManager).findLastVisibleItemPositions(null);

            lastVisibleItemPosition = getLastVisibleItem(lastVisibleItemPositions);

            if (totalItemCount < previousTotalItemCount) {
                this.currentPage = this.startingPageIndex;
                this.previousTotalItemCount = totalItemCount;
                if (totalItemCount == 0) {
                    this.loading = true;
                }
            }

            if (loading && (totalItemCount > previousTotalItemCount)) {
                loading = false;
                previousTotalItemCount = totalItemCount;
            }


            if (!loading && (lastVisibleItemPosition + visibleThreshold) > totalItemCount) {
                currentPage++;
                onLoadMore(currentPage, totalItemCount);
                loading = true;
            }
        }else{
            super.onScrolled(view, dx, dy);
            visibleItemCount = view.getChildCount();
            totalItemCount = mLayoutManager.getItemCount();
            firstVisibleItem = ((LinearLayoutManager) mLayoutManager).findFirstVisibleItemPosition();

            if (loading) {
                if (totalItemCount > previousTotalItemCount) {
                    loading = false;
                    previousTotalItemCount = totalItemCount;
                }
            }

            if (!loading && (totalItemCount - visibleItemCount)
                    <= (firstVisibleItem + visibleThreshold)) {

                currentPage++;

                onLoadMore(currentPage);

                loading = true;
            }
        }
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if(newState != recyclerView.SCROLL_STATE_IDLE){
            setVisibleTopButton();
        }else{
            setInvisibleTopButton();
        }
    }


    public abstract void onLoadMore(int page, int totalItemsCount);
    public abstract void onLoadMore(int page);
    public abstract void setVisibleTopButton();
    public abstract void setInvisibleTopButton();
    public void initData(){
        visibleThreshold = 10;

        currentPage = 1;

        previousTotalItemCount = 0;

        loading = true;

        startingPageIndex = 1;
    }
}