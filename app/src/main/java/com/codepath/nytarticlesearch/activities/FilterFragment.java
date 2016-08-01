package com.codepath.nytarticlesearch.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.codepath.nytarticlesearch.Filters;
import com.codepath.nytarticlesearch.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by sam on 7/31/16.
 */
public class FilterFragment extends DialogFragment {

    private EditText tvPickerBeginDate;
    private Spinner spinnerSortOrder;
    private CheckBox checkboxArts;
    private CheckBox checkboxSports;
    private CheckBox checkboxFashionStyle;
    private DatePickerDialog datePickerDialog;
    private Button btCancel;
    private Button btOK;
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd", Locale.US);

    public FilterFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static FilterFragment newInstance(String title) {
        FilterFragment frag = new FilterFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    // 1. Defines the listener interface with a method passing back data result.
    public interface FilterDialogListener {
        void onFinishEditDialog(Filters filters);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_filter, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        tvPickerBeginDate = (EditText) view.findViewById(R.id.tvPickerBeginDate);
        spinnerSortOrder = (Spinner) view.findViewById(R.id.spinnerSortOrder);
        checkboxArts = (CheckBox) view.findViewById(R.id.checkBoxArts);
        checkboxSports = (CheckBox) view.findViewById(R.id.checkboxSports);
        checkboxFashionStyle = (CheckBox) view.findViewById(R.id.checkboxFashionStyle);
        btCancel = (Button) view.findViewById(R.id.btCancel);
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        btOK = (Button) view.findViewById(R.id.btOK);
        btOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FilterDialogListener listener = (FilterDialogListener) getActivity();
                final Filters filters = new Filters();
                filters.setBeginDate(tvPickerBeginDate.getText().toString());
                filters.setSorter(spinnerSortOrder.getSelectedItem().toString());
                StringBuilder sb = new StringBuilder();
                if(checkboxArts.isChecked()) sb.append("Arts");
                if(checkboxSports.isChecked()) sb.append(" " + "Sports");
                if(checkboxFashionStyle.isChecked()) sb.append(" " + "Fashion&Style");
                filters.setDesks(sb.toString().trim());
                listener.onFinishEditDialog(filters);
                // Close the dialog and return back to the parent activity
                dismiss();
            }
        });
        ArrayAdapter<CharSequence> sAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.status_levels, android.R.layout.simple_spinner_item);
        sAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSortOrder.setAdapter(sAdapter);

        //tvPickerBeginDate.setText(dateFormatter.format(Calendar.getInstance().getTime()));
        //tvPickerBeginDate.setSelection(tvPickerBeginDate.getText().length());
        tvPickerBeginDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final Calendar cal = Calendar.getInstance();
                datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(year, monthOfYear, dayOfMonth);
                        tvPickerBeginDate.setText(dateFormatter.format(newDate.getTime()));
                        tvPickerBeginDate.setSelection(tvPickerBeginDate.getText().length());
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Filters");
        getDialog().setTitle(title);
        // Show soft keyboard automatically and request focus to field
        //mEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        int width = getResources().getDimensionPixelSize(R.dimen.popup_width);
        int height = getResources().getDimensionPixelSize(R.dimen.popup_height);
        getDialog().getWindow().setLayout(width, height);
    }
}
