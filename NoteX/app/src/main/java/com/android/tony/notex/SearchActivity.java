package com.android.tony.notex;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Note> noteList;
    NoteAdapter noteAdapter;
    Toolbar toolbar;
    EditText searchboxEditText;
    ActionMode.Callback callback;
    ActionMode actionMode;
    int selectedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        callback = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.setTitle("");
                toolbar.setVisibility(View.GONE);
                mode.getMenuInflater().inflate(R.menu.action_mode_menu, menu);
                /*menu.getItem(0).setVisible(true);
                menu.getItem(1).setVisible(true);*/
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
                    case R.id.actionarchive:
                        Toast.makeText(getApplicationContext(), noteList.get(selectedItem).getNoteID(), Toast.LENGTH_SHORT).show();
                        NoteKeeperActivity.databaseHelper.updateData(noteList.get(selectedItem).getNoteID(), noteList.get(selectedItem).getNoteTitle(), noteList.get(selectedItem).getNoteContent(), "Archived");
                        noteList.remove(selectedItem);
                        noteAdapter.notifyDataSetChanged();
                        break;
                    case R.id.actiondel:
                        Toast.makeText(getApplicationContext(), noteList.get(selectedItem).getNoteID(), Toast.LENGTH_SHORT).show();
                        NoteKeeperActivity.databaseHelper.updateData(noteList.get(selectedItem).getNoteID(), noteList.get(selectedItem).getNoteTitle(), noteList.get(selectedItem).getNoteContent(), "Deleted");
                        noteList.remove(selectedItem);
                        noteAdapter.notifyDataSetChanged();
                        break;
                }
                mode.finish();
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                toolbar.setVisibility(View.VISIBLE);
            }
        };

        toolbar = findViewById(R.id.searchtoolbar);
        searchboxEditText = findViewById(R.id.searchboxeditText);
        recyclerView = findViewById(R.id.searchrecyclerview);
        noteList = new ArrayList<>();
        noteAdapter = new NoteAdapter(noteList);

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, 10, true));
        recyclerView.setAdapter(noteAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerViewClickListener(getApplicationContext(), recyclerView, new RecyclerViewClickListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Toast.makeText(getApplicationContext(), "OnClick", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), TakeNoteActivity.class);
                intent.putExtra("Location","Notes");
                intent.putExtra("Id", noteList.get(position).getNoteID());
                intent.putExtra("Title", noteList.get(position).getNoteTitle());
                intent.putExtra("Note", noteList.get(position).getNoteContent());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {
                Toast.makeText(getApplicationContext(), "Long", Toast.LENGTH_SHORT).show();
                if (actionMode != null) return;
                startActionMode(callback);
                selectedItem = position;
                view.setSelected(true);
            }
        }));

        searchboxEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                noteList.clear();
                Cursor cursor = NoteKeeperActivity.databaseHelper.getAllData();
                if (cursor.getCount() > 0 && !s.equals("")) {
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {
                        if (cursor.getString(3).equals("Notes") && (cursor.getString(1).contains(s) || cursor.getString(2).contains(s)))
                            noteList.add(new Note(cursor.getString(0), cursor.getString(1), cursor.getString(2)));
                        cursor.moveToNext();
                    }
                }
                noteAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(String.valueOf(s).isEmpty())
                    noteList.clear();
            }
        });
    }
    class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

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
}



