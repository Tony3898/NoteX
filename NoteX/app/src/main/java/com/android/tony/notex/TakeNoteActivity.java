package com.android.tony.notex;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;


public class TakeNoteActivity extends AppCompatActivity {

    EditText titleEditText, contentEditText;
    Intent intent;
    Toolbar toolbar;
    InputMethodManager inputMethodManager;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.action_mode_menu, menu);
        if (intent.getExtras() == null)
            return false;
        else {
            if (intent.getStringExtra("Location").equals("Notes"))
                menu.getItem(3).setVisible(false);
            else if (intent.getStringExtra("Location").equals("Archived")) {
                menu.getItem(0).setVisible(false);
                menu.getItem(2).setVisible(false);
            } else {
                menu.getItem(2).setVisible(false);
            }
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionarchive:
                saveNote("Archived");
                break;
            case R.id.actiondel:
                if (!intent.getStringExtra("Location").equals("Deleted"))
                    saveNote("Deleted");
                else {
                    new AlertDialog.Builder(getApplicationContext()).setIcon(R.mipmap.ic_launcher_round).setTitle(R.string.app_name).setMessage("Are you want to delete it permanently").setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            NoteKeeperActivity.databaseHelper.deleteData(intent.getStringExtra("Id"));
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
                }
                break;
            case R.id.actionshare:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT,titleEditText.getText().toString());
                intent.putExtra(Intent.EXTRA_TEXT,contentEditText.getText().toString());
                startActivity(Intent.createChooser(intent,"Share"));
                break;
            case R.id.actionrestore:
                saveNote("Notes");
                break;
        }
        TakeNoteActivity.this.finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_note);
        setTitle("");

        toolbar = findViewById(R.id.takeNotetToolbar);
        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentEditText);
        inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        intent = getIntent();

        setSupportActionBar(toolbar);

        titleEditText.setText(intent.getStringExtra("Title"));
        contentEditText.setText(intent.getStringExtra("Note"));


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (intent.getStringExtra("Title") == null && intent.getStringExtra("Note") == null)
                    saveNote("Notes");
                else
                    saveNote(intent.getStringExtra("Location"));
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (intent.getStringExtra("Title") == null && intent.getStringExtra("Note") == null)
            saveNote("Notes");
        else
            saveNote(intent.getStringExtra("Location"));
    }

    void saveNote(String location) {
        if (titleEditText.getText().toString().isEmpty() && contentEditText.getText().toString().isEmpty())
            Toast.makeText(getApplicationContext(), "Empty note discarded", Toast.LENGTH_SHORT).show();
        else {
            if (intent.getStringExtra("Title") == null && intent.getStringExtra("Note") == null) {
                if (NoteKeeperActivity.databaseHelper.ipnutData(titleEditText.getText().toString(), contentEditText.getText().toString()))
                    Toast.makeText(getApplicationContext(), "Note Saved", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getApplicationContext(), "Note didn't saved, something went wrong", Toast.LENGTH_LONG).show();
            } else {
                if (NoteKeeperActivity.databaseHelper.updateData(intent.getStringExtra("Id"), titleEditText.getText().toString(), contentEditText.getText().toString(), location))
                    Toast.makeText(getApplicationContext(), "Note Saved", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getApplicationContext(), "Note didn't saved, something went wrong", Toast.LENGTH_LONG).show();
            }
        }
        startActivity(new Intent(getApplicationContext(), NoteKeeperActivity.class));
        TakeNoteActivity.this.finish();
    }
}
