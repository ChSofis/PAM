package gr.uom.pam.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import gr.uom.pam.App;
import gr.uom.pam.BuildConfig;
import gr.uom.pam.R;

public class CommentActivity extends AppCompatActivity {
    public static final String COMMENT = App.NAMESPACE + ".comment";
    TextInputLayout _comment;

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        //assign view
        _comment = findViewById(R.id.comment_text);
        //set up continue listener
        findViewById(R.id.comment_continue).setOnClickListener(this::do_continue);
        //restore saved state
        if (savedInstanceState != null && savedInstanceState.containsKey(COMMENT))
            _comment.getEditText().setText(savedInstanceState.getString(COMMENT, ""));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //noinspection ConstantConditions
        outState.putString(COMMENT, _comment.getEditText().getText().toString());
    }

    private void do_continue(@SuppressWarnings("unused") View view) {
        //noinspection ConstantConditions
        if (_comment.getEditText().getText().length() == 0) {
            Toast.makeText(this, R.string.error_no_comment_selected, Toast.LENGTH_SHORT).show();
        } else {
            startActivity(
                    new Intent(this, AddressActivity.class)
                            .putExtras(this.getIntent())
                            .putExtra(COMMENT, _comment.getEditText().getText().toString())
            );
        }
    }
}
