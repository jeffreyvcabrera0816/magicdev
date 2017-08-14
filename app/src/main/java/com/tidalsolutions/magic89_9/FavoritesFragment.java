package com.tidalsolutions.magic89_9;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FavoritesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FavoritesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavoritesFragment extends android.support.v4.app.Fragment implements SwipeRefreshLayout.OnRefreshListener {

    WebView mWebView;
    WebSettings webSettings;
    View view;
    String url;

    private SwipeRefreshLayout swipeRefreshLayout;

    public FavoritesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_favorites, container, false);
        url = "http://embedapi.magic89dev.tidalsolutions.com.ph/examples/";
        //http://embedapi.magic89dev.tidalsolutions.com.ph/examples/
        mWebView = (WebView) view.findViewById(R.id.webview);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRadio);
        swipeRefreshLayout.setOnRefreshListener(this);

        webSettings = mWebView.getSettings();

        loadRadio();

        return view;
    }

    @Override
    public void onRefresh() {
        loadRadio();
    }

    public void loadRadio() {

        if (swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        mWebView.getSettings().setDomStorageEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        mWebView.loadUrl(url);
        mWebView.setWebViewClient(new WebViewClient());
    }
}
