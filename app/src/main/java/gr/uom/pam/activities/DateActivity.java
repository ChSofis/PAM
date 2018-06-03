package gr.uom.pam.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import gr.uom.pam.App;
import gr.uom.pam.R;

public class DateActivity extends AppCompatActivity {
    public static final String DATE_START = App.NAMESPACE + ".date_start";
    public static final String DATE_END = App.NAMESPACE + ".date_end";
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private Calendar _date_start;
    private Calendar _date_end;
    private CoordinatorLayout _coordinator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date);
        //set up view needed for snackbars
        _coordinator = findViewById(R.id.coordinator);
        //set_ups select from date
        findViewById(R.id.start).setOnClickListener(v -> ask_for_date(true));
        //set up select to date
        findViewById(R.id.end).setOnClickListener(v -> ask_for_date(false));
        //set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> super.onBackPressed());
        toolbar.setOnMenuItemClickListener(this::do_continue);
        //restore state after a configuration change
        if (savedInstanceState != null && savedInstanceState.containsKey(DATE_START))
            set_date(true, (Calendar) savedInstanceState.getSerializable(DATE_START));
        if (savedInstanceState != null && savedInstanceState.containsKey(DATE_END))
            set_date(false, (Calendar) savedInstanceState.getSerializable(DATE_END));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_continue,menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(DATE_START, _date_start);
        outState.putSerializable(DATE_END, _date_end);
    }

    private boolean do_continue(MenuItem item) {
        if (item == null || item.getItemId() != R.id.menu_action)
            return false;
        if (_date_start == null || _date_end == null) {
            Snackbar.make(_coordinator, R.string.error_no_dates_selected, Snackbar.LENGTH_LONG).show();
        } else if (_date_start.compareTo(_date_end) > 0) {
            Snackbar.make(_coordinator, R.string.error_start_date_after_end_date, Snackbar.LENGTH_SHORT).show();
        } else {
            startActivity(
                    new Intent(this, CommentActivity.class)
                            .putExtras(this.getIntent())
                            .putExtra(DATE_START, _date_start)
                            .putExtra(DATE_END, _date_end)
            );
        }
        return true;
    }

    private void ask_for_date(boolean start) {
        Calendar selected = start ? _date_start : _date_end;
        if (selected == null)
            selected = Calendar.getInstance();
        new DatePickerDialog(this
                , (v, y, m, d) -> date_selected(start, y, m, d)
                , selected.get(Calendar.YEAR)
                , selected.get(Calendar.MONTH)
                , selected.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    void date_selected(boolean start, int year, int month, int dayOfMonth) {
        Calendar date = Calendar.getInstance();
        date.set(year, month, dayOfMonth, 0, 0, 0);
        date.set(Calendar.MILLISECOND, 0);
        set_date(start, date);
    }

    private void set_date(boolean start, Calendar date) {
        if (start) {
            _date_start = date;
            ((Button) findViewById(R.id.start)).setText(FORMATTER.format(date.getTime()));
        } else {
            _date_end = date;
            ((Button) findViewById(R.id.end)).setText(FORMATTER.format(date.getTime()));
        }
    }
}
