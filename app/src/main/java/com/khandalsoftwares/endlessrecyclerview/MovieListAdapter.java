package com.khandalsoftwares.endlessrecyclerview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Khandal Software on 2/28/18,34
 */

public class MovieListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //Properties
    private static final int VIEW_ITEM = 0;
    private static final int VIEW_PROGRESS = 1;
    private static final String BASE_URL_IMG = "https://image.tmdb.org/t/p/w150";
    
    private Context mContext;
    private List<Result> mResultList;
    private boolean isLoading = false;
    private Animation startAnimation ;
    
    MovieListAdapter(Context mContext){
        this.mContext = mContext;
        startAnimation = AnimationUtils.loadAnimation(mContext, R.anim.blinking_animation);
        mResultList = new ArrayList<>();
        
    }
    
    public List<Result> getResultList(){
        return mResultList;
    }
    
    public void setResultList(List<Result> mResultList){
        this.mResultList = mResultList;
    }
    
    
    
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder  viewHolder = null;
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());
        switch (viewType){
            case VIEW_ITEM:
                viewHolder = new MovieItemViewHolder(mInflater.inflate(R.layout.list_item,parent,false));
                break;
            case VIEW_PROGRESS:
                viewHolder = new LoadingViewHolder(mInflater.inflate(R.layout.loading_layout,parent,false));
                break;
        }
        return viewHolder;
    }
    
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MovieItemViewHolder){
            final MovieItemViewHolder mSingleItemViewHolder = (MovieItemViewHolder)holder;
            Result result = mResultList.get(position);
            mSingleItemViewHolder.mMovieTitleTextView.setText(result.getTitle());
            mSingleItemViewHolder.mMovieReleaseDateTextView.setText(result.getReleaseDate().substring(0, 4));
            mSingleItemViewHolder.mMoviewDescTextView.setText(result.getOverview());
            mSingleItemViewHolder.mMovieOrignalLanguageTextView.setText(result.getOriginalLanguage().concat(" | "));
            mSingleItemViewHolder.mMovieTitleTextView.setText(result.getTitle());
            if(result.getAdult()){
                mSingleItemViewHolder.mMovieAdultTextView.setText("A");
            }else{
                mSingleItemViewHolder.mMovieAdultTextView.setText("U/A");
            }
            
            //For image load
            /*
             * Using Glide to handle image loading.
             */
            Glide
                    .with(mContext)
                    .load(BASE_URL_IMG + result.getPosterPath())
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            mSingleItemViewHolder.mProgressBar.setVisibility(View.GONE);
                            return false;
                        }
                
                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            // image ready, hide progress now
                            mSingleItemViewHolder.mProgressBar.setVisibility(View.GONE);
                            return false;   // return false if you want Glide to handle everything else.
                        }
                    })
                    .diskCacheStrategy(DiskCacheStrategy.ALL)   // cache both original & resized image
                    .centerCrop()
                    .crossFade()
                    .into(mSingleItemViewHolder.mMovieImageView);
            
        }else if(holder instanceof LoadingViewHolder){
                LoadingViewHolder loadingViewHolder = (LoadingViewHolder)holder;
            loadingViewHolder.relativeLayout.startAnimation(startAnimation);
        }
    }
    
    @Override
    public int getItemCount() {
        return mResultList == null?0:mResultList.size();
    }
    
    @Override
    public int getItemViewType(int position) {
        return (position == mResultList.size() - 1 && isLoading) ? VIEW_PROGRESS : VIEW_ITEM;
    }
    
    
    public void add(Result result){
        mResultList.add(result);
        notifyItemInserted(mResultList.size()-1);
    }
    
    public void addAll(List<Result> mResultList){
        for (Result result:mResultList) {
            add(result);
        }
    }
    
    public void removeItem(Result result){
        int position = mResultList.indexOf(result);
        if (position > -1) {
            mResultList.remove(position);
            notifyItemRemoved(position);
        }
    }
    
    public void clearProgress(){
        isLoading = false;
        while (getItemCount() > 0) {
            removeItem(getItem(0));
        }
    }
    
    public boolean isEmpty() {
        return getItemCount() == 0;
    }
    
    public void addLoadingFooter() {
        isLoading = true;
        add(new Result());
    }
    
    public void removeLoadingFooter() {
        isLoading = false;
        
        int position = mResultList.size() - 1;
        Result result = getItem(position);
        
        if (result != null) {
            mResultList.remove(position);
            notifyItemRemoved(position);
        }
    }
    
    public Result getItem(int position) {
        return mResultList.get(position);
    }
    public static class MovieItemViewHolder extends RecyclerView.ViewHolder{
        private ImageView mMovieImageView;
        private TextView mMovieTitleTextView;
        private TextView mMovieReleaseDateTextView;
        private TextView mMovieOrignalLanguageTextView;
        private TextView mMoviewDescTextView;
        private TextView mMovieAdultTextView;
        private ProgressBar mProgressBar;
         MovieItemViewHolder(View itemView) {
            super(itemView);
            mMovieImageView = itemView.findViewById(R.id.iv_movie_poster);
            mMovieTitleTextView = itemView.findViewById(R.id.tv_movie_title);
            mMovieReleaseDateTextView = itemView.findViewById(R.id.tv_release_date);
            mMovieOrignalLanguageTextView = itemView.findViewById(R.id.tv_orignal_language);
            mMoviewDescTextView = itemView.findViewById(R.id.tv_movie_desc);
            mMovieAdultTextView = itemView.findViewById(R.id.tv_adult);
            mProgressBar = itemView.findViewById(R.id.progress_image);
            
        }
    }
    
    public static class LoadingViewHolder extends RecyclerView.ViewHolder{
        ConstraintLayout relativeLayout;
         LoadingViewHolder(View itemView) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.cl_loading);
        }
    }
}
