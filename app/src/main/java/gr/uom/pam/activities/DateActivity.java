package gr.uom.pam.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import gr.uom.pam.App;
import gr.uom.pam.BuildConfig;
import gr.uom.pam.R;

public class DateActivity extends AppCompatActivity {
    public static final String DATE_START = App.NAMESPACE + ".date_start";
    public static final String DATE_END = App.NAMESPACE + ".date_end";
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    Calendar _date_start;
    Calendar _date_end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date);
        //set_ups select from date
        findViewById(R.id.date_start).setOnClickListener(v -> ask_for_date(true));
        //set up select to date
        findViewById(R.id.date_end).setOnClickListener(v -> ask_for_date(false));
        //set up continue
        findViewById(R.id.date_continue).setOnClickListener(this::do_continue);
        //restore state after a configuration change
        if (savedInstanceState != null && savedInstanceState.containsKey(DATE_START))
            set_date(true, (Calendar) savedInstanceState.getSerializable(DATE_START));
        if (savedInstanceState != null && savedInstanceState.containsKey(DATE_END))
            set_date(false, (Calendar) savedInstanceState.getSerializable(DATE_END));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(DATE_START, _date_start);
        outState.putSerializable(DATE_END, _date_end);
    }

    private void do_continue(@SuppressWarnings("unused") View view) {
        if (_date_start == null || _date_end == null) {
            Toast.makeText(this, R.string.error_no_dates_selected, Toast.LENGTH_SHORT).show();
        } else if (_date_start.compareTo(_date_end) > 0) {
            Toast.makeText(this, R.string.error_start_date_after_end_date, Toast.LENGTH_SHORT).show();
        } else {
            startActivity(
                    new Intent(this, CommentActivity.class)
                            .putExtras(this.getIntent())
                            .putExtra(DATE_START, _date_start)
                            .putExtra(DATE_END, _date_end)
            );
        }
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
            ((Button) findViewById(R.id.date_start)).setText(FORMATTER.format(date.getTime()));
        } else {
            _date_end = date;
            ((Button) findViewById(R.id.date_end)).setText(FORMATTER.format(date.getTime()));
        }
    }
}
