package com.example.studmy.fragments;


import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.example.studmy.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DialogInstruction extends DialogFragment {

    private WebView webView;

    public DialogInstruction() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_view, null);

        webView = view.findViewById(R.id.webView);
        webView.loadUrl("file:///android_asset/infoMin.html");

        return view;
    }

}
