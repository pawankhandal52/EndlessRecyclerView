package com.khandalsoftwares.endlessrecyclerview;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.khandalsoftwares.endlessrecyclerview.apiconfig.TheMovieDBApi;
import com.khandalsoftwares.endlessrecyclerview.apiconfig.TheMovieDBService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private final String TAG = MainActivity.class.getSimpleName();
    private MovieListAdapter movieListAdapter;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    
    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    // limiting to 5 for this tutorial, since total pages in actual API is very large. Feel free to modify.
    private int TOTAL_PAGES = 4;
    private int currentPage = PAGE_START;
    
    private TheMovieDBService theMovieDBService;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressBar = findViewById(R.id.main_progress);
        mRecyclerView = findViewById(R.id.rv_movie_list);
    
        movieListAdapter = new MovieListAdapter(this);
        LinearLayoutManager linearLayoutManager =new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(movieListAdapter);
        
        mRecyclerView.addOnScrollListener(new EndlessScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;
    
                // mocking network delay for API call
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadMore();
                    }
                }, 1000);
            }
    
            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }
    
            @Override
            public boolean isLastPage() {
                return isLastPage;
            }
    
            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
        
        //init service and call
        theMovieDBService = TheMovieDBApi.getClient(this).create(TheMovieDBService.class);
        load();
    }
    
    
    
    
    private void load() {
        Log.d(TAG, "loadFirstPage: ");
        
        callTopRatedMoviesApi().enqueue(new Callback<TopRatedMovies>() {
            @Override
            public void onResponse(Call<TopRatedMovies> call, Response<TopRatedMovies> response) {
                // Got data. Send it to adapter
    
                Log.e(TAG, "onResponse: loadfirst"+ response.isSuccessful() );
                if (response.isSuccessful()){
                    List<Result> results = fetchResults(response);
                    mProgressBar.setVisibility(View.GONE);
                    movieListAdapter.addAll(results);
    
                    if (currentPage <= TOTAL_PAGES) movieListAdapter.addLoadingFooter();
                    else isLastPage = true;
                }else{
                    Log.e(TAG, "onResponse: not succesfully"+ response.errorBody() );
                }
                
            }
            
            @Override
            public void onFailure(Call<TopRatedMovies> call, Throwable t) {
                t.printStackTrace();
                Log.e(TAG, "onFailure: "+ t.getLocalizedMessage());
            }
        });
        
    }
    
    
    private void loadMore() {
        Log.d(TAG, "loadNextPage: " + currentPage);
        
        callTopRatedMoviesApi().enqueue(new Callback<TopRatedMovies>() {
            @Override
            public void onResponse(Call<TopRatedMovies> call, Response<TopRatedMovies> response) {
                movieListAdapter.removeLoadingFooter();
                isLoading = false;
    
    
                Log.e(TAG, "onResponse: loadfirst"+ response.isSuccessful() );
                if (response.isSuccessful()){
                    List<Result> results = fetchResults(response);
                    mProgressBar.setVisibility(View.GONE);
                    movieListAdapter.addAll(results);
        
                    if (currentPage <= TOTAL_PAGES) movieListAdapter.addLoadingFooter();
                    else isLastPage = true;
                }else{
                    Log.e(TAG, "onResponse: not succesfully"+ response.headers().get("status_message") );
                }
            }
            
            @Override
            public void onFailure(Call<TopRatedMovies> call, Throwable t) {
                t.printStackTrace();
                Log.e(TAG, "onFailure: "+ t.getLocalizedMessage());
            }
        });
    }
    private Call<TopRatedMovies> callTopRatedMoviesApi() {
        return theMovieDBService.getTopRatedMovies(
                BuildConfig.ApiKey,
                currentPage
        );
    }
    
    /**
     * @param response extracts List<{@link Result>} from response
     * @return
     */
    private List<Result> fetchResults(Response<TopRatedMovies> response) {
        TopRatedMovies topRatedMovies = response.body();
        return topRatedMovies.getResults();
    }
}
