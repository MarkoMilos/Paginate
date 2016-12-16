package com.paginate;

/**
 * Created by ikhlebunov on 16.12.16.
 */

public interface DynamicLoadingItemListener {
	void onAddLoadingItem(int position);

	void onRemoveLoadingItem(int position);
}
