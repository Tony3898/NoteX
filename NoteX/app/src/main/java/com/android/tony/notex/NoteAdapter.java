package com.android.tony.notex;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.MyViewHolder> {

    List<Note> noteList;
    NoteAdapter(List<Note> noteList)
    {
        this.noteList = noteList;
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView noteTitleTextView,noteContentTextView;
        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTitleTextView  = itemView.findViewById(R.id.noteTitleTextView);
            noteContentTextView = itemView.findViewById(R.id.noteContentTextView);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.note_layout,viewGroup,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        Note note = noteList.get(i);
        myViewHolder.noteTitleTextView.setText(note.getNoteTitle());
        myViewHolder.noteContentTextView.setText(note.getNoteContent());
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }


}
