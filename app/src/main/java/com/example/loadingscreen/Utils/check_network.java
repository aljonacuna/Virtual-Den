package com.example.loadingscreen.Utils;

import android.content.Context;
import android.media.Image;
import android.net.ConnectivityManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;

public class check_network {

    public static void isConnected(Context context, ImageView imageView, TextView textView,
                                   RecyclerView recyclerView, ShimmerFrameLayout shimmerFrameLayout,
                                    ProgressBar progressBar){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
         boolean isconnected = connectivityManager.getActiveNetworkInfo() !=null &&
                 connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
         if (!isconnected){
             imageView.setVisibility(View.VISIBLE);
             textView.setText("No internet connection");
             textView.setVisibility(View.VISIBLE);
             recyclerView.setVisibility(View.GONE);
             shimmerFrameLayout.setVisibility(View.GONE);
             shimmerFrameLayout.stopShimmer();
             if (progressBar!=null) {
                 progressBar.setVisibility(View.GONE);
             }

         }
    }
    public static boolean isConnectedClass(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isconnected = connectivityManager.getActiveNetworkInfo() !=null &&
                connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
        return isconnected;
    }
    public static void hideshimmer(ShimmerFrameLayout shimmerFrameLayout, ProgressBar progressBar, SwipeRefreshLayout swipeRefreshLayout){
        shimmerFrameLayout.stopShimmer();
        shimmerFrameLayout.setVisibility(View.GONE);
        if (progressBar!=null) {
            progressBar.setVisibility(View.GONE);
        }
        if (swipeRefreshLayout!=null){
            swipeRefreshLayout.setVisibility(View.VISIBLE);
        }
    }
}
