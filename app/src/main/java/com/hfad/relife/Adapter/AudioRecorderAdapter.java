package com.hfad.relife.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import com.hfad.relife.R;
import com.hfad.relife.Util.RecyclerViewCursorAdapter;



public class AudioRecorderAdapter extends RecyclerViewCursorAdapter<AudioRecorderAdapter.ViewHolder> {

    private ImageButtonOnClickListener mImageButtonOnClickListener;
    private Context context;


    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView mRecorderName;
        private final ImageButton mPlayButton;

        public ViewHolder(View view){
            super(view);
            mRecorderName = (TextView) view.findViewById(R.id.recorder_name);
            mPlayButton = (ImageButton) view.findViewById(R.id.recorder_play_button);
        }

        public TextView getmRecorderName() {
            return mRecorderName;
        }

        public ImageButton getmPlayButton() {
            return mPlayButton;
        }
    }

    public AudioRecorderAdapter(Context context, Cursor cursor, int flags){
        super(context,cursor,flags);
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recorder_row, parent, false);
        return new ViewHolder(v);
    }



    @Override
    public void onBindViewHolder(final ViewHolder holder, Cursor cursor) {
        String filepath = cursor.getString(cursor.getColumnIndex(AudioRecorderDbAdapter.COL_FILEPATH));
        System.out.println("filepath:");
        System.out.println(filepath);
        holder.getmRecorderName().setText(filepath);
        ImageButton imageButton = holder.getmPlayButton();
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mImageButtonOnClickListener != null){
                    mImageButtonOnClickListener.onPlay(holder.getAdapterPosition());
                }
            }
        });
    }


    @Override
    protected void onContentChanged() {

    }


    public interface ImageButtonOnClickListener{
        void onPlay(int position);
    }

    public void setImageButtonOnClickListener(ImageButtonOnClickListener imageButtonOnClickListener){
        this.mImageButtonOnClickListener = imageButtonOnClickListener;
    }

    public ImageButtonOnClickListener getImageButtonOnClickListener(){return this.mImageButtonOnClickListener;}
}
