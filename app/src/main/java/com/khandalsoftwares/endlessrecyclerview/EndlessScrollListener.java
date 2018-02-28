package com.khandalsoftwares.endlessrecyclerview;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Khandal Software on 2/28/18,21
 */

public abstract class EndlessScrollListener extends RecyclerView.OnScrollListener{
    LinearLayoutManager layoutManager;
    
    /**
     * Supporting only LinearLayoutManager for now.
     *
     * @param layoutManager
     */
    public EndlessScrollListener(LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }
    
    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        
        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
        
        if (!isLoading() && !isLastPage()) {
            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= 0
                    && totalItemCount >= getTotalPageCount()) {
                loadMoreItems();
            }
        }
        
    }
    
    protected abstract void loadMoreItems();
    
    public abstract int getTotalPageCount();
    
    public abstract boolean isLastPage();
    
    public abstract boolean isLoading();
}
