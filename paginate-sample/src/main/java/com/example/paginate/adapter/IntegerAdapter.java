package com.example.paginate.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class IntegerAdapter extends BindableAdapter<Integer> {

    private final int[] VALUES;

    public IntegerAdapter(Context context, int[] values) {
        super(context);
        this.VALUES = values;
    }

    @Override
    public int getCount() {
        return VALUES.length;
    }

    @Override
    public Integer getItem(int position) {
        return VALUES[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View newView(LayoutInflater inflater, int position, ViewGroup container) {
        return inflater.inflate(android.R.layout.simple_spinner_item, container, false);
    }

    @Override
    public void bindView(Integer item, int position, View view) {
        TextView tv = (TextView) view.findViewById(android.R.id.text1);
        tv.setText(String.valueOf(item));
    }

    @Override
    public View newDropDownView(LayoutInflater inflater, int position, ViewGroup container) {
        return inflater.inflate(android.R.layout.simple_spinner_dropdown_item, container, false);
    }

    /**
     * Returns index of value in adapters data set.
     * Returns -1 if value is not found.
     */
    public int getPositionForValue(int value) {
        for (int i = 0; i < VALUES.length; i++) {
            if (VALUES[i] == value) {
                return i;
            }
        }
        // value not found
        return -1;
    }

}