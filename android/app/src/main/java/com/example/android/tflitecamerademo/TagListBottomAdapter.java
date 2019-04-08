package com.example.android.tflitecamerademo;

import android.os.Bundle;
import android.util.ArrayMap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import static com.loopj.android.http.AsyncHttpClient.log;


public class TagListBottomAdapter extends RecyclerView.Adapter<TagListBottomAdapter.TagListItemViewHolder> {

    // The list of tags shown in the ViewHolder
    protected final List<String> mTagListItems = new ArrayList<>();


    //Listener to capture events in the adapter_tag edittext box (NOTE this won't work for capturing events from soft keyboards)
    private TextView.OnEditorActionListener editListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            if (event==null) {
                // Capture soft enters in singleLine EditTexts
                if (actionId==EditorInfo.IME_ACTION_DONE || actionId==EditorInfo.IME_ACTION_NEXT) {
                    log.e("Caught Keypress", "Pressed Enter");
                    String newTag = view.getText().toString();
                    if (!newTag.isEmpty()) {
                        log.e("Added item ", newTag);
                        addItem(newTag);
                    }
                }
                else return false;  // Let system handle all other null KeyEvents
            }
/*            else if (actionId==EditorInfo.IME_NULL) {
                // Capture most soft enters in multi-line EditTexts and all hard enters.
                // They supply a zero actionId and a valid KeyEvent rather than
                // a non-zero actionId and a null event like the previous cases.
                if (event.getAction()==KeyEvent.ACTION_DOWN);
                    // We capture the event when key is first pressed.
                else  return true;   // We consume the event when the key is released.
            }*/
            else  return false;
            return true;   // Consume the event
        }
    };

    @NonNull
    @Override
    public TagListItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        RelativeLayout v = (RelativeLayout) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_tag, viewGroup, false);
        return new TagListItemViewHolder(v);



    }

    @Override
    public void onBindViewHolder(@NonNull TagListItemViewHolder holder, int position) {

        //Set the
        holder.mEditText.setOnEditorActionListener(editListener);
        holder.mEditText.setText(mTagListItems.get(position));


        //Listener for removing tags using the delete button
        holder.mBtnRemoveTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                log.e("Removing item", "Removing item " + position);
                if (position <= mTagListItems.size() - 1) {
                    //Remove the item from the tags
                    mTagListItems.remove(position);
                }
                notifyItemRemoved(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mTagListItems.size();
    }


    //Add a new item to the Tag List
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
    //Retrieve references to views we want to work with
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
