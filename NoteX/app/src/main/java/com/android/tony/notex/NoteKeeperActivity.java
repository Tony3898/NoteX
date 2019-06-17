package com.android.tony.notex;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


public class NoteKeeperActivity extends AppCompatActivity {

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    static DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_keeper);
        switchFragment(new NoteFragment());
        databaseHelper = new DatabaseHelper(getApplicationContext());

        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        navigationView.setCheckedItem(navigationView.getMenu().getItem(0).getItemId());


        /*View v1 = navigationView.getHeaderView(0);
        TextView textView = v1.findViewById(R.id.headertextView);
        textView.setText("Tejas Rana");*/

        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.START);
            }
        });
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),toolbar.getTitle().toString(),Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(),SearchActivity.class));
            }
        });


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("ResourceType")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                drawerLayout.closeDrawers();
                switch (menuItem.getItemId())
                {
                    case R.id.note:
                        toolbar.setClickable(true);
                        toolbar.setTitle("Search your note...");
                        switchFragment(new NoteFragment());
                        break;
                    case R.id.archive:
                        switchFragment(new ArchiveFragment());
                        toolbar.setTitle("Archive");
                        toolbar.setClickable(false);
                        break;
                    case R.id.delete:
                        switchFragment(new DeleteFragment());
                        toolbar.setTitle("Delete");
                        toolbar.setClickable(false);
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(!drawerLayout.isDrawerOpen(navigationView))
        {
            super.onBackPressed();
        }
        else
        {
            drawerLayout.closeDrawers();
        }
    }

    void switchFragment(Fragment fragment)
    {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame,fragment).commit();
    }
}
