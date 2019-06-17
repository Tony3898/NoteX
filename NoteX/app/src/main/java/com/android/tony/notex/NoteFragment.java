package com.android.tony.notex;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class NoteFragment extends Fragment {

    RecyclerView noteRecyclerView;
    List<Note> noteList;
    NoteAdapter noteAdapter;
    Toolbar toolbar;
    ActionMode.Callback callback;
    ActionMode actionMode;
    FloatingActionButton noteFloatingActionButton;
    int selectedItem;
    ConstraintLayout emptyNoteBodyConstraintLayout;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_note, container, false);
        noteRecyclerView = v.findViewById(R.id.noteRecyclerView);
        noteFloatingActionButton = v.findViewById(R.id.takeanotetextView);
        emptyNoteBodyConstraintLayout = v.findViewById(R.id.emptynoteBodyConstraintLayout);
        toolbar = getActivity().findViewById(R.id.toolbar);
        noteList = new ArrayList<>();
        noteAdapter = new NoteAdapter(noteList);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        callback = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.setTitle("");
                toolbar.setVisibility(View.GONE);
                mode.getMenuInflater().inflate(R.menu.action_mode_menu, menu);
                menu.getItem(3).setVisible(false);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.actionarchive:
                        Toast.makeText(getContext(), noteList.get(selectedItem).getNoteID(), Toast.LENGTH_SHORT).show();
                        NoteKeeperActivity.databaseHelper.updateData(noteList.get(selectedItem).getNoteID(), noteList.get(selectedItem).getNoteTitle(), noteList.get(selectedItem).getNoteContent(), "Archived");
                        noteList.remove(selectedItem);
                        noteAdapter.notifyDataSetChanged();
                        break;
                    case R.id.actiondel:
                        Toast.makeText(getContext(), noteList.get(selectedItem).getNoteID(), Toast.LENGTH_SHORT).show();
                        NoteKeeperActivity.databaseHelper.updateData(noteList.get(selectedItem).getNoteID(), noteList.get(selectedItem).getNoteTitle(), noteList.get(selectedItem).getNoteContent(), "Deleted");
                        noteList.remove(selectedItem);
                        noteAdapter.notifyDataSetChanged();
                        break;
                    case R.id.actionshare:
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_SUBJECT,noteList.get(selectedItem).getNoteTitle());
                        intent.putExtra(Intent.EXTRA_TEXT,noteList.get(selectedItem).getNoteContent());
                        startActivity(Intent.createChooser(intent,"Share"));
                }
                updateUserInterface();
                mode.finish();
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                toolbar.setVisibility(View.VISIBLE);
            }
        };

        noteFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), TakeNoteActivity.class));

            }
        });

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
        noteRecyclerView.setLayoutManager(mLayoutManager);
        noteRecyclerView.setItemAnimator(new DefaultItemAnimator());
        noteRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, 10, true));
        noteRecyclerView.addOnItemTouchListener(new RecyclerViewClickListener(getContext(), noteRecyclerView, new RecyclerViewClickListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(getContext(), TakeNoteActivity.class);
                intent.putExtra("Location","Notes");
                intent.putExtra("Id", noteList.get(position).getNoteID());
                intent.putExtra("Title", noteList.get(position).getNoteTitle());
                intent.putExtra("Note", noteList.get(position).getNoteContent());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {
                if (actionMode != null) return;
                getActivity().startActionMode(callback);
                selectedItem = position;
                view.setSelected(true);
            }
        }));
        noteRecyclerView.setAdapter(noteAdapter);

        noteList.clear();
        Cursor cursor = NoteKeeperActivity.databaseHelper.getAllData();
        if (cursor.getCount() > 0 ) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Log.i("Notex",String.format("%s %s %s %s",cursor.getString(0), cursor.getString(1), cursor.getString(2),cursor.getString(3)));
                if (cursor.getString(3).equals("Notes"))
                    noteList.add(new Note(cursor.getString(0), cursor.getString(1), cursor.getString(2)));
                cursor.moveToNext();
            }
        }
        updateUserInterface();
        noteAdapter.notifyDataSetChanged();
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }

    }

    public void updateUserInterface()
    {
        if(noteList.size()>0)
        {
           noteRecyclerView.setVisibility(View.VISIBLE);
            emptyNoteBodyConstraintLayout.setVisibility(View.GONE);
        }
        else {
            noteRecyclerView.setVisibility(View.GONE);
            emptyNoteBodyConstraintLayout.setVisibility(View.VISIBLE);
        }
    }
}
