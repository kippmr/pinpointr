package com.example.android.tflitecamerademo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;


public class TagListBottomFragment extends Fragment {

    private TagListBottomAdapter adapter;

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

        view.findViewById(R.id.btnAddMoreItems).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewTagToList();
            }
        });

        ((RecyclerView) view.findViewById(R.id.rclItems)).setAdapter(adapter);
    }

    private void addNewTagToList() {
        adapter.addItem("New Tag");
    }

    public void addGeneratedTag(String tag){
        adapter.addItem(tag);
    }

}
