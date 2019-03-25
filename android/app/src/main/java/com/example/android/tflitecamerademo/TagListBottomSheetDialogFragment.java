package com.example.android.tflitecamerademo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class TagListBottomSheetDialogFragment extends BottomSheetDialogFragment {



    public TagListBottomFragment tagListBottomFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_tag, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tagListBottomFragment = new TagListBottomFragment();
        getChildFragmentManager().beginTransaction().add(R.id.frmContent, tagListBottomFragment).commit();
    }


}

