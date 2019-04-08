package com.example.android.tflitecamerademo;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.ViewGroup;
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

    public ImageData imgData;
    private final List<String> mTagListItems = new ArrayList<>();
    private TextView.OnEditorActionListener editListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            log.e("Caught", "Catch");
            if (event==null) {
                if (actionId==EditorInfo.IME_ACTION_DONE);
                    // Capture soft enters in a singleLine EditText that is the last EditText.
                else if (actionId==EditorInfo.IME_ACTION_NEXT);
                    // Capture soft enters in other singleLine EditTexts
                else return false;  // Let system handle all other null KeyEvents
            }
            else if (actionId==EditorInfo.IME_NULL) {
                // Capture most soft enters in multi-line EditTexts and all hard enters.
                // They supply a zero actionId and a valid KeyEvent rather than
                // a non-zero actionId and a null event like the previous cases.
                if (event.getAction()==KeyEvent.ACTION_DOWN);
                    // We capture the event when key is first pressed.
                else  return true;   // We consume the event when the key is released.
            }
            else  return false;
            // We let the system handle it when the listener
            // is triggered by something that wasn't an enter.


            // Code from this point on will execute whenever the user
            // presses enter in an attached view, regardless of position,
            // keyboard, or singleLine status.
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
        holder.mEditText.setText("");
        holder.mEditText.setOnEditorActionListener(editListener);

        holder.mBtnRemoveTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position <= mTagListItems.size() - 1) {
                    log.e("Removing item", "Removing item " + position);
                    //Remove the item from the map
                    imgData.SortedLabels.remove(mTagListItems.get(position));
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


    public void addItem(String item) {
        try {
            mTagListItems.add(item);
            this.imgData.SortedLabels.add(new AbstractMap.SimpleEntry<String, Float>(item, 100.0f));
            log.e("s", "Updated SortedLabels");
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
