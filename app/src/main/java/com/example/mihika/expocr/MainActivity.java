package com.example.mihika.expocr;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, TabFragment.OnFragmentInteractionListener {

    private int u_id;
    private String u_name;
    private String u_email;

    private TabFragmentAdapter tabAdapter;
    private ViewPager tabPager;
    private FloatingActionButton myFAB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        u_id = intent.getIntExtra("u_id", 1);
        u_name = intent.getStringExtra("u_name");
        u_email = intent.getStringExtra("u_email");
        System.out.println("u_id: " + u_id);
        System.out.println("u_name: " + u_name);
        System.out.println("u_email: " + u_email);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddTransactionActivity.class);
                intent.putExtra("u_id", u_id);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View nav_header_view = navigationView.getHeaderView(0);
        TextView nav_header_name = (TextView) nav_header_view.findViewById(R.id.nav_header_name);
        nav_header_name.setText(u_name);
        TextView nav_header_email = (TextView) nav_header_view.findViewById(R.id.nav_header_email);
        nav_header_email.setText(u_email);

        tabAdapter = new TabFragmentAdapter(getSupportFragmentManager());
        tabPager = (ViewPager) findViewById(R.id.tabPager);
        tabPager.setAdapter(tabAdapter);
        //Todo: if there are too much views in the pager, then we need to reconsider a proper offscreenpagelimit
        tabPager.setOffscreenPageLimit(tabAdapter.getCount() - 1);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(tabPager);

        myFAB = (FloatingActionButton) findViewById(R.id.fab);
        myFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity", "Add button clicked");
                Intent transaciton = new Intent(MainActivity.this, AddTransactionActivity.class);
                startActivity(transaciton);
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        setIntent(intent);
        if(intent.hasExtra("addTransaction")){
            tabAdapter.refreshTabs();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getResources().getString(R.string.action_search));
        //Todo: add different listeners for searchView to offer suggestions based on query text

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_friend) {
            Toast.makeText(getApplicationContext(), "To be implemented", Toast.LENGTH_LONG).show();
            return true;
        }else if(id == R.id.action_create_group){
            Toast.makeText(getApplicationContext(), "To be implemented", Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id == R.id.nav_home){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        else if(id == R.id.nav_account){
            Toast.makeText(getApplicationContext(), "To be implemented", Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_plan) {
            Toast.makeText(getApplicationContext(), "To be implemented", Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_camera) {
            Intent intent = new Intent(MainActivity.this, PhotoCaptureActivity.class);
            //Todo: bundle data to be transferred
            startActivity(intent);
        } else if (id == R.id.nav_summary) {
            Toast.makeText(getApplicationContext(), "To be implemented", Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_trend) {
            Toast.makeText(getApplicationContext(), "To be implemented", Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_settings) {
            Toast.makeText(getApplicationContext(), "To be implemented", Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_contact) {
            Toast.makeText(getApplicationContext(), "To be implemented", Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_logout){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Todo: specify commnication with TabFragment
    @Override
    public String onFragmentRefresh(String page_title) {
        StringBuilder builder = new StringBuilder();
        builder.append("You are in page ").append(page_title).append("!\n");
        builder.append("Current Time: ").append(DateFormat.getDateTimeInstance().format(new Date()));
        return builder.toString();
    }

    public int getU_id(){
        return this.u_id;
    }

}

class TabFragmentAdapter extends FragmentPagerAdapter {
    private FragmentManager fm;
    private String[] tab_titles;
    private Fragment[] tab_fragments;

    public TabFragmentAdapter(FragmentManager fm) {
        super(fm);
        this.fm = fm;
        //Todo: put strings in strings.xml
        this.tab_titles = new String[]{"FRIENDS", "GROUPS", "EXPENSES"};
        this.tab_fragments = new Fragment[tab_titles.length];
        for(int index = 0; index < tab_titles.length; index++){
            tab_fragments[index] = TabFragment.newInstance(tab_titles[index]);
        }
    }

    @Override
    public Fragment getItem(int position) {
        return tab_fragments[position % tab_fragments.length];
    }

    @Override
    public int getCount() {
        return tab_titles.length;
    }

    @Override
    public CharSequence getPageTitle(int position){
        return tab_titles[position % tab_titles.length];
    }

    public void refreshTabs(){
        for(Fragment tab: tab_fragments){
            ((TabFragment)tab).refreshTabFragment();
        }
    }
}