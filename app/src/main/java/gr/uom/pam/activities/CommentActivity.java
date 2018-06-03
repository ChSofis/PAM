package gr.uom.pam.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import gr.uom.pam.App;
import gr.uom.pam.R;

public class CommentActivity extends AppCompatActivity {
    public static final String COMMENT = App.NAMESPACE + ".comment";
    EditText _comment;
    private CoordinatorLayout _coordinator;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        //set up view needed for snackbars
        _coordinator = findViewById(R.id.coordinator);
        //assign view
        _comment = findViewById(R.id.text);
        //set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> super.onBackPressed());
        toolbar.setOnMenuItemClickListener(this::do_continue);
        //restore saved state
        if (savedInstanceState != null && savedInstanceState.containsKey(COMMENT))
            _comment.setText(savedInstanceState.getString(COMMENT, ""));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_continue,menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(COMMENT, _comment.getText().toString());
    }

    private boolean do_continue(MenuItem item) {
        if (item == null || item.getItemId() != R.id.menu_action)
            return false;
        if (_comment.getText().length() == 0) {
            Snackbar.make(_coordinator, R.string.error_no_comment_selected, Snackbar.LENGTH_SHORT).show();
        } else {
            startActivity(
                    new Intent(this, AddressActivity.class)
                            .putExtras(this.getIntent())
                            .putExtra(COMMENT, _comment.getText().toString())
            );
        }
        return true;
    }
}
