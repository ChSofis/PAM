package gr.uom.pam.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import gr.uom.pam.App;
import gr.uom.pam.R;
import gr.uom.pam.adapters.StoreAdapter;
import gr.uom.pam.model.Data;
import gr.uom.pam.model.Store;

public class StoreActivity extends AppCompatActivity {
    public static final String STORE = App.NAMESPACE + ".store";

    private StoreAdapter _adapter;
    private CoordinatorLayout _coordinator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        //set up view needed for snackbars
        _coordinator = findViewById(R.id.coordinator);
        //set up list and add an adapter for it to retrieve its data from
        RecyclerView list = findViewById(R.id.list);
        _adapter = new StoreAdapter(Data.GetStores());
        list.setAdapter(_adapter);
        //set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> super.onBackPressed());
        toolbar.setOnMenuItemClickListener(this::do_continue);
        //t&C
        onfirst();
        //restore instance state
        if (savedInstanceState != null && savedInstanceState.containsKey(STORE)) {
            Store store = savedInstanceState.getParcelable(STORE);
            _adapter.set_selected(store);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_continue,menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STORE, _adapter.get_selected());
    }

    boolean do_continue(MenuItem item) {
        if (item == null || item.getItemId() != R.id.menu_action)
            return false;
        Store selected = _adapter.get_selected();
        if (selected == null)
            Snackbar.make(_coordinator, R.string.error_no_store_selected, Snackbar.LENGTH_LONG).show();
        else {
            startActivity(
                    new Intent(this, CategoryActivity.class)
                            .putExtra(STORE, selected)
            );
        }
        return true;
    }
        protected void onfirst()
        {
            boolean isFirstRun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isFirstRun",true);
            if (isFirstRun) {
                new AlertDialog.Builder(StoreActivity.this)
                        .setTitle("Terms & Conditions")
                        .setMessage("T&C")
                        .setNegativeButton("Decline", (dialog, which) -> {
                            finish();
                            System.exit(0);
                        })
                        .setPositiveButton("Accept", (dialog, which) -> getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                                .edit()
                                .putBoolean("isFirstRun", false)
                                .apply()).show();
            }}}
