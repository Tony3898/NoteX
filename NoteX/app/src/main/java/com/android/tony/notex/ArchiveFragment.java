package com.android.tony.notex;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class ArchiveFragment extends Fragment {

    RecyclerView archiveRecyclerView;
    List<Note> noteList;
    NoteAdapter noteAdapter;
    Toolbar toolbar;
    ActionMode.Callback callback;
    ActionMode actionMode;
    int selectedItem;
    ConstraintLayout emptyArchiveBodyConstraintLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_archive, container, false);
        archiveRecyclerView = v.findViewById(R.id.archiverecyclerview);
        emptyArchiveBodyConstraintLayout = v.findViewById(R.id.emptyarchiveBodyConstraintLayout);
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
                menu.getItem(0).setVisible(false);
                menu.getItem(2).setVisible(false);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.actiondel:
                        NoteKeeperActivity.databaseHelper.updateData(noteList.get(selectedItem).getNoteID(), noteList.get(selectedItem).getNoteTitle(), noteList.get(selectedItem).getNoteContent(), "Deleted");
                        noteList.remove(selectedItem);
                        noteAdapter.notifyDataSetChanged();
                        break;

                    case R.id.actionrestore:
                        Toast.makeText(getContext(),"Note Restored",Toast.LENGTH_SHORT).show();
                        NoteKeeperActivity.databaseHelper.updateData(noteList.get(selectedItem).getNoteID(), noteList.get(selectedItem).getNoteTitle(), noteList.get(selectedItem).getNoteContent(), "Notes");
                        noteList.remove(selectedItem);
                        noteAdapter.notifyDataSetChanged();
                        break;
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

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
        archiveRecyclerView.setLayoutManager(mLayoutManager);
        archiveRecyclerView.setItemAnimator(new DefaultItemAnimator());
        archiveRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, 10, true));

        archiveRecyclerView.addOnItemTouchListener(new RecyclerViewClickListener(getContext(), archiveRecyclerView, new RecyclerViewClickListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Toast.makeText(getContext(),"OnClick",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(),TakeNoteActivity.class);
                intent.putExtra("Location","Archived");
                intent.putExtra("Id",noteList.get(position).getNoteID());
                intent.putExtra("Title",noteList.get(position).getNoteTitle());
                intent.putExtra("Note",noteList.get(position).getNoteContent());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {
                Toast.makeText(getContext(),"Long",Toast.LENGTH_SHORT).show();
                if(actionMode!=null) return;
                getActivity().startActionMode(callback);
                selectedItem = position;
                view.setSelected(true);
            }
        }));
        archiveRecyclerView.setAdapter(noteAdapter);

        noteList.clear();
        Cursor cursor = NoteKeeperActivity.databaseHelper.getAllData();
        if(cursor.getCount()>0)
        {
            cursor.moveToFirst();
            while (!cursor.isAfterLast())
            {
                if(cursor.getString(3).equals("Archived"))
                    noteList.add(new Note(cursor.getString(0),cursor.getString(1),cursor.getString(2)));
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
            archiveRecyclerView.setVisibility(View.VISIBLE);
            emptyArchiveBodyConstraintLayout.setVisibility(View.GONE);
        }
        else {
            archiveRecyclerView.setVisibility(View.GONE);
            emptyArchiveBodyConstraintLayout.setVisibility(View.VISIBLE);
        }
    }
}
