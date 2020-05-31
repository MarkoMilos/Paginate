package com.example.paginate;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Spinner;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.paginate.adapter.EnumAdapter;
import com.example.paginate.adapter.IntegerAdapter;

public abstract class BaseActivity extends AppCompatActivity {

    public enum LayoutManagerEnum {
        LINEAR,
        GRID,
        STAGGERED
    }

    public enum Orientation {
        VERTICAL,
        HORIZONTAL
    }

    public enum AbsListViewType {
        LIST_VIEW,
        GRID_VIEW
    }

    // Common options
    protected int threshold = 4;
    protected int totalPages = 3;
    protected int itemsPerPage = 10;
    protected long networkDelay = 2000;
    protected boolean addLoadingRow = true;
    protected boolean customLoadingListItem = false;

    // RecyclerView specific options
    protected LayoutManagerEnum layoutManagerEnum = LayoutManagerEnum.LINEAR;
    protected Orientation orientation = Orientation.VERTICAL;
    protected boolean reverseLayout = false;

    // AbsListView specific options
    protected AbsListViewType absListViewType = AbsListViewType.LIST_VIEW;
    protected boolean useHeaderAndFooter = false;

    private ActionBarDrawerToggle drawerToggle;
    private FrameLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_layout_container);
        container = (FrameLayout) findViewById(R.id.container);

        setupBasicUI();
        setupOptions();
    }

    protected ViewGroup getContainer() {
        return container;
    }

    protected abstract void setupPagination();

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        // Pass any configuration change to the drawer toggle
        drawerToggle.onConfigurationChanged(configuration);
    }

    private void setupBasicUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_closed);
        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

    private void setupOptions() {

        // Common options

        Spinner thresholdView = (Spinner) findViewById(R.id.spinner_threshold);
        final IntegerAdapter thresholdAdapter = new IntegerAdapter(this, new int[]{0, 1, 2, 3, 4, 5, 6, 7});
        thresholdView.setAdapter(thresholdAdapter);
        thresholdView.setSelection(thresholdAdapter.getPositionForValue(threshold));
        thresholdView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                int selected = thresholdAdapter.getItem(position);
                if (selected != threshold) {
                    threshold = selected;
                    setupPagination();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        Spinner pagesView = (Spinner) findViewById(R.id.spinner_pages);
        final IntegerAdapter pagesAdapter = new IntegerAdapter(this, new int[]{1, 2, 3, 4, 5, 6, 7});
        pagesView.setAdapter(pagesAdapter);
        pagesView.setSelection(pagesAdapter.getPositionForValue(totalPages));
        pagesView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                int selected = pagesAdapter.getItem(position);
                if (selected != totalPages) {
                    totalPages = selected;
                    setupPagination();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        Spinner itemsPerPageView = (Spinner) findViewById(R.id.spinner_items_per_page);
        final IntegerAdapter itemsPerPageAdapter = new IntegerAdapter(this, new int[]{2, 5, 10, 20, 30});
        itemsPerPageView.setAdapter(itemsPerPageAdapter);
        itemsPerPageView.setSelection(itemsPerPageAdapter.getPositionForValue(itemsPerPage));
        itemsPerPageView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                itemsPerPage = itemsPerPageAdapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        Spinner networkDelayView = (Spinner) findViewById(R.id.spinner_delay);
        final IntegerAdapter delayAdapter = new IntegerAdapter(this, new int[]{1000, 2000, 3000, 5000});
        networkDelayView.setAdapter(delayAdapter);
        networkDelayView.setSelection(delayAdapter.getPositionForValue(networkDelay));
        networkDelayView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                networkDelay = delayAdapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        CheckBox addLoadingRowCb = (CheckBox) findViewById(R.id.cb_add_loading_row);
        addLoadingRowCb.setChecked(addLoadingRow);
        addLoadingRowCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (addLoadingRow != isChecked) {
                    addLoadingRow = isChecked;
                    setupPagination();
                }
            }
        });

        CheckBox customLoadingListItemCb = (CheckBox) findViewById(R.id.cb_custom_row);
        customLoadingListItemCb.setChecked(customLoadingListItem);
        customLoadingListItemCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (customLoadingListItem != isChecked) {
                    customLoadingListItem = isChecked;
                    setupPagination();
                }
            }
        });

        // Recycler specific options

        Spinner layoutManagerView = (Spinner) findViewById(R.id.spinner_layout_mng);
        final EnumAdapter<LayoutManagerEnum> layoutManagerAdapter = new EnumAdapter<>(this, LayoutManagerEnum.class);
        layoutManagerView.setAdapter(layoutManagerAdapter);
        layoutManagerView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                LayoutManagerEnum selected = layoutManagerAdapter.getItem(position);
                if (selected != layoutManagerEnum) {
                    layoutManagerEnum = selected;
                    setupPagination();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        Spinner orientationView = (Spinner) findViewById(R.id.spinner_orientation);
        final EnumAdapter<Orientation> orientationAdapter = new EnumAdapter<>(this, Orientation.class);
        orientationView.setAdapter(orientationAdapter);
        orientationView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                Orientation selected = orientationAdapter.getItem(position);
                if (selected != orientation) {
                    orientation = selected;
                    setupPagination();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        CheckBox reverseLayoutCb = (CheckBox) findViewById(R.id.cb_reverse);
        reverseLayoutCb.setChecked(reverseLayout);
        reverseLayoutCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (reverseLayout != isChecked) {
                    reverseLayout = isChecked;
                    setupPagination();
                }
            }
        });

        // AbsListView specific options

        Spinner absListViewTypeView = (Spinner) findViewById(R.id.spinner_abs_list_type);
        final EnumAdapter<AbsListViewType> absListViewTypeAdapter = new EnumAdapter<>(this, AbsListViewType.class);
        absListViewTypeView.setAdapter(absListViewTypeAdapter);
        absListViewTypeView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                AbsListViewType selected = absListViewTypeAdapter.getItem(position);
                if (selected != absListViewType) {
                    absListViewType = selected;
                    setupPagination();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        CheckBox userHeaderAndFooterCb = (CheckBox) findViewById(R.id.cb_header_and_footer);
        userHeaderAndFooterCb.setChecked(useHeaderAndFooter);
        userHeaderAndFooterCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (useHeaderAndFooter != isChecked) {
                    useHeaderAndFooter = isChecked;
                    setupPagination();
                }
            }
        });
    }

}