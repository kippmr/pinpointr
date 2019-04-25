package com.example.android.tflitecamerademo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.AbstractMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;


public class TagListBottomFragment extends Fragment {

    protected TagListBottomAdapter adapter;
    private Button mBtnNext;
    private Button mBtnAddTag;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        adapter = new TagListBottomAdapter();
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_tag, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBtnAddTag = view.findViewById(R.id.btnAddMoreItems);
        mBtnNext = view.findViewById(R.id.btnTagMenuNext);

        view.findViewById(R.id.btnAddMoreItems).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewTagToList();
            }
        });

        ((RecyclerView) view.findViewById(R.id.rclItems)).setAdapter(adapter);

    }

    //Add a new tag to the recycler view adapter
    private void addNewTagToList() {
        adapter.addItem("");
    }


    public void addGeneratedTag(String tag){
        adapter.addItem(tag);
    }

    public List<String> getTagList() {
        return adapter.mTagListItems;
    }

    public Button getNextButton() {
        return mBtnNext;
    }

    public Button getAddTagButton() {
        return mBtnAddTag;
    }




}
