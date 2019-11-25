package com.example.localizer;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ListeNoteRecyclerViewAdapter extends RecyclerView.Adapter<ListeNoteRecyclerViewAdapter.ViewHolder> {

    private final ListeNotes mValues;
    private final ListeFragment.OnListFragmentInteractionListener mListener;

    public ListeNoteRecyclerViewAdapter(ListeNotes items, ListeFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_preview_note, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        Note n = mValues.get(position);

        holder.mItem = n;
        holder.mTextView.setText(n.getTitre());
        holder.mImageView.setImageResource(n.getImage());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.getNbNote();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public View mView;
        public TextView mTextView;
        public ImageView mImageView;
        public Note mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTextView = view.findViewById(R.id.titre);
            mImageView = view.findViewById(R.id.image);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}
