package com.example.loadingscreen.win.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.loadingscreen.R;

public class MessageOrgFragment<officerView> extends Fragment {
    View messageView;
    public MessageOrgFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        messageView=inflater.inflate(R.layout.messageorg, container, false);
        return messageView;
    }


}