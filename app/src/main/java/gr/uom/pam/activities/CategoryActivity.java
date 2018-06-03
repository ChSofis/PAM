package gr.uom.pam.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import gr.uom.pam.App;
import gr.uom.pam.R;
import gr.uom.pam.adapters.CategoryAdapter;
import gr.uom.pam.model.Category;
import gr.uom.pam.model.Data;

public class CategoryActivity extends AppCompatActivity {
    public static final String CATEGORY = App.NAMESPACE + ".category";
    private CategoryAdapter _adapter;
    private CoordinatorLayout _coordinator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        //set up view needed for snackbars
        _coordinator = findViewById(R.id.coordinator);
        //set up list and add an adapter for it to retrieve its data from
        RecyclerView list = findViewById(R.id.list);
        _adapter = new CategoryAdapter(Data.GetCategories());
        list.setAdapter(_adapter);
        //set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> super.onBackPressed());
        toolbar.setOnMenuItemClickListener(this::do_continue);
        //restore instance state
        if (savedInstanceState != null && savedInstanceState.containsKey(CATEGORY)) {
            Category category = savedInstanceState.getParcelable(CATEGORY);
            _adapter.set_selected(category);
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
        outState.putParcelable(CATEGORY, _adapter.get_selected());
    }

    boolean do_continue(MenuItem item) {
        if (item == null || item.getItemId() != R.id.menu_action)
            return false;
        Category selected = _adapter.get_selected();
        if (selected == null)
            Snackbar.make(_coordinator, R.string.error_no_category_selected, Snackbar.LENGTH_LONG).show();
        else {
            startActivity(
                    new Intent(this, DateActivity.class)
                            .putExtras(this.getIntent()) //transfer all current intent data to the new activity
                            .putExtra(CATEGORY, selected) //and add the category
            );
        }
        return true;
    }
}
