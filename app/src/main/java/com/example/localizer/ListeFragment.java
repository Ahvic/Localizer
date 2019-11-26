package com.example.localizer;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ListeFragment extends Fragment {

    private OnListFragmentInteractionListener mListener;
    private ListeNotes Notes;
    private ListeNoteRecyclerViewAdapter mAdapter;

    public ListeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_liste, container, false);

        if(view instanceof RecyclerView){

            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            if(Notes == null)
                Notes = new ListeNotes(this.getContext());

            if(mAdapter == null)
                mAdapter = new ListeNoteRecyclerViewAdapter(Notes, mListener);

            recyclerView.setAdapter(mAdapter);
            ((MainActivity)getActivity()).envoiListeNote(Notes);
        }

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void supprimerNote(Note n){
        int index = Notes.effacerNote(n.getTitre());
        ((MainActivity)getActivity()).envoiListeNote(Notes);

        mAdapter.notifyItemRemoved(index);
    }

    public void creerNote(String titre, String contenu, int image, double coordN, double coordO){
        int index = Notes.creerNote(titre, contenu, image, coordN, coordO);
        ((MainActivity)getActivity()).envoiListeNote(Notes);

        if(index != -1)
            mAdapter.notifyItemInserted(index);
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Note item);
    }
}
