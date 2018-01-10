package com.hfad.relife.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.hfad.relife.R;
import com.hfad.relife.Util.RecyclerViewCursorAdapter;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;

/**
 * Created by 18359 on 2017/11/20.
 */

public class NoteAdapter extends RecyclerViewCursorAdapter<NoteAdapter.NoteViewHolder> {

    private Context mContext;
    private RecyclerViewOnItemClickListener mOnItemClickListener;
    private onSwipeListener mOnSwipeListener;

    public NoteAdapter(Context context, Cursor cursor, int flags){
        super(context,cursor,flags);
        this.mContext = context;
    }

    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_row, parent, false);
        NoteViewHolder holder = new NoteViewHolder(root);
        return holder;
    }

    @Override
    public void onBindViewHolder(final NoteViewHolder holder, Cursor cursor){
        int position = cursor.getPosition();
        holder.tv.setText(cursor.getString(cursor.getColumnIndex(NoteDbAdapter.COL_CONTENT)));
        holder.tv_dateTime.setText(cursor.getString(cursor.getColumnIndex(NoteDbAdapter.COL_DATETIME)));
        holder.mRowtab.setBackgroundColor(cursor.getInt(cursor.getColumnIndex(NoteDbAdapter.COL_IMPORTANT))==1?
                mContext.getResources().getColor(R.color.colorAccent):mContext.getResources().getColor(android.R.color.white));
        holder.root.setTag(position);
        ((SwipeMenuLayout)holder.root.findViewById(R.id.swipeMenuLayout)).setIos(false).setLeftSwipe(false).setSwipeEnable(true);

        holder.btnTop.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(mOnSwipeListener!=null){
                    mOnSwipeListener.onTop(holder.getAdapterPosition());
                }
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(mOnSwipeListener!=null){
                    mOnSwipeListener.onDel(holder.getAdapterPosition());
                }
            }
        });

        holder.tv.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(mOnItemClickListener!=null){
                    mOnItemClickListener.onItemClickListener(view,holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    protected void onContentChanged(){

    }

    /*点击事件接口*/
    public interface RecyclerViewOnItemClickListener{
        void onItemClickListener(View view, int position);
    }

    public void setRecyclerViewOnItemClickListener(RecyclerViewOnItemClickListener onItemClickListener){
        this.mOnItemClickListener = onItemClickListener;
    }

    public RecyclerViewOnItemClickListener getmOnItemClickListener(){
        return mOnItemClickListener;
    }

    /*侧滑事件接口*/
    public interface onSwipeListener{
        void onDel(int pos);
        void onTop(int pos);
    }

    public void setOnSwipeListener(onSwipeListener mOnSwipeListener){
        this.mOnSwipeListener = mOnSwipeListener;
    }

    public onSwipeListener getmOnSwipeListener(){
        return mOnSwipeListener;
    }

    class NoteViewHolder extends RecyclerView.ViewHolder{
        private TextView tv;
        private TextView tv_dateTime;
        private View mRowtab;
        private Button btnTop;
        private Button btnDelete;
        private View root;

        public NoteViewHolder(View root){
            super(root);
            this.root = root;
            tv = (TextView)root.findViewById(R.id.row_text);
            tv_dateTime = (TextView)root.findViewById(R.id.tv_note_time);
            mRowtab = root.findViewById(R.id.row_tab);
            btnTop = (Button)root.findViewById(R.id.btnTop);
            btnDelete = (Button)root.findViewById(R.id.btnDelete);
        }
    }
}
