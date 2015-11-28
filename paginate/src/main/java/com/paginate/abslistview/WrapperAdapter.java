package com.paginate.abslistview;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.WrapperListAdapter;

class WrapperAdapter extends BaseAdapter implements WrapperListAdapter {

    private final BaseAdapter wrappedAdapter;
    private final LoadingListItemCreator loadingListItemCreator;
    private boolean displayLoadingRow = true;

    public WrapperAdapter(BaseAdapter wrappedAdapter, LoadingListItemCreator loadingListItemCreator) {
        this.wrappedAdapter = wrappedAdapter;
        this.loadingListItemCreator = loadingListItemCreator;
    }

    @Override
    public ListAdapter getWrappedAdapter() {
        return wrappedAdapter;
    }

    @Override
    public int getCount() {
        return displayLoadingRow ? wrappedAdapter.getCount() + 1 : wrappedAdapter.getCount();
    }

    @Override
    public Object getItem(int position) {
        return isLoadingRow(position) ? null : wrappedAdapter.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return isLoadingRow(position) ? -1 : wrappedAdapter.getItemId(position);
    }

    @Override
    public int getViewTypeCount() {
        return displayLoadingRow ? wrappedAdapter.getViewTypeCount() + 1 : wrappedAdapter.getViewTypeCount();
    }

    @Override
    public int getItemViewType(int position) {
        return isLoadingRow(position) ? getViewTypeCount() - 1 : wrappedAdapter.getItemViewType(position);
    }

    @Override
    public boolean isEnabled(int position) {
        return !isLoadingRow(position) && wrappedAdapter.isEnabled(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (isLoadingRow(position)) {
            if (convertView == null) {
                convertView = loadingListItemCreator.newView(position, parent);
            }
            loadingListItemCreator.bindView(position, convertView);
            return convertView;
        } else {
            return wrappedAdapter.getView(position, convertView, parent);
        }
    }

    void displayLoadingRow(boolean displayLoadingRow) {
        if (this.displayLoadingRow != displayLoadingRow) {
            this.displayLoadingRow = displayLoadingRow;
            notifyDataSetChanged();
        }
    }

    boolean isLoadingRow(int position) {
        return displayLoadingRow && position == getLoadingRowPosition();
    }

    private int getLoadingRowPosition() {
        return displayLoadingRow ? getCount() - 1 : -1;
    }

}