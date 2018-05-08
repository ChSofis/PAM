package gr.uom.pam.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import gr.uom.pam.App;
import gr.uom.pam.BuildConfig;
import gr.uom.pam.R;
import gr.uom.pam.adapters.StoreAdapter;
import gr.uom.pam.model.Data;
import gr.uom.pam.model.Store;

public class StoreActivity extends Activity {
    public static final String STORE = App.NAMESPACE + ".store";

    StoreAdapter _adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        //set up list and add an adapter for it to retrieve its data from
        RecyclerView list = findViewById(R.id.store_list);
        _adapter = new StoreAdapter(Data.GetStores());
        list.setAdapter(_adapter);
        //add an event handler to the continue button
        findViewById(R.id.store_continue).setOnClickListener(this::do_continue);
        if (savedInstanceState != null && savedInstanceState.containsKey(STORE)) {
            Store store = savedInstanceState.getParcelable(STORE);
            _adapter.set_selected(store);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STORE,_adapter.get_selected());
    }

    void do_continue(@SuppressWarnings("unused") View view) {
        Store selected = _adapter.get_selected();
        if (selected == null)
            Toast.makeText(this, R.string.error_no_store_selected, Toast.LENGTH_LONG).show();
        else {
            startActivity(
                    new Intent(this, CategoryActivity.class)
                            .putExtra(STORE, selected)
            );
        }
    }
}
