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
import gr.uom.pam.adapters.CategoryAdapter;
import gr.uom.pam.model.Category;
import gr.uom.pam.model.Data;

public class CategoryActivity extends Activity {
    public static final String CATEGORY = App.NAMESPACE + ".category";
    private CategoryAdapter _adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        //set up list and add an adapter for it to retrieve its data from
        RecyclerView list = findViewById(R.id.category_list);
        _adapter = new CategoryAdapter(Data.GetCategories());
        list.setAdapter(_adapter);
        //add an event handler to the continue button
        findViewById(R.id.category_continue).setOnClickListener(this::do_continue);
        //restore saved state
        if (savedInstanceState != null && savedInstanceState.containsKey(CATEGORY)) {
            Category category = savedInstanceState.getParcelable(CATEGORY);
            _adapter.set_selected(category);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(CATEGORY,_adapter.get_selected());
    }

    private void do_continue(@SuppressWarnings("unused") View view) {
        Category selected = _adapter.get_selected();
        if (selected == null)
            Toast.makeText(this, R.string.error_no_category_selected, Toast.LENGTH_LONG).show();
        else {
            startActivity(
                    new Intent(this, DateActivity.class)
                            .putExtras(this.getIntent()) //transfer all current intent data to the new activity
                            .putExtra(CATEGORY, selected) //and add the category
            );
        }
    }
}
