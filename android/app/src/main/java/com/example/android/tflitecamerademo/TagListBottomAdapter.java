package com.example.android.tflitecamerademo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class TagListBottomAdapter extends RecyclerView.Adapter<TagListBottomAdapter.TagListItemViewHolder> {

    // The list of tags shown in the ViewHolder
    private final List<String> mTagListItems = new ArrayList<>();

    @NonNull
    @Override
    public TagListItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        RelativeLayout v = (RelativeLayout) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_tag, viewGroup, false);
        return new TagListItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TagListItemViewHolder holder, int position) {
        holder.mEditText.setText(mTagListItems.get(position));

        holder.mBtnRemoveTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTagListItems.remove(position);
                notifyItemRemoved(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mTagListItems.size();
    }


    public void addItem(String item) {

        try {
            mTagListItems.add(item);
            notifyDataSetChanged();
        }catch(Exception ex){
            //TODO
        }
    }


//    private void setItemEditable(Integer position){
//        for(int i=0; i< mTagListItems.size(); i++){
//
//        }
//    }

    //TODO remove "Sample" etc. from class/var/res names
    public class TagListItemViewHolder extends RecyclerView.ViewHolder {
        EditText mEditText;
        ImageButton mBtnRemoveTag;

        TagListItemViewHolder(RelativeLayout v) {
            super(v);
            mEditText = v.findViewById(R.id.etTag);

//            mEditText.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    setItemEditable(getAdapterPosition());
//                }
//            });

            mBtnRemoveTag = v.findViewById(R.id.btnRemoveTag);

        }
    }

}
