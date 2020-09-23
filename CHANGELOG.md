Change Log
==========

Version 1.0.0 *(2020-09-23)*
----------------------------

 * New: library migrated to `androidX` libraries
 * Update: minSdk is now set to 16
 * Update: recycler `WrapperAdapter` will now call `notifyItem[Inserted|Removed]` instead of `notifyDataSetChanged` when displayLoadingRow is called
 * Update: `WrapperAdapter` (for `RecyclerView`) will now propagate `onAttachedToRecyclerView` and `onDetachedFromRecyclerView` calls to wrapped adapter
 * Fix: fixed issue where `RecyclerView` was scrolled to the beginning (first item) after `paginate.unbind()` is called because of adapter swapping.
 * Fix: fixed initialization of `RecyclerView` `WrapperAdapter` to properly set `hasStableIds`
 * Fix: fixed bug where wrapper adapter is initialized to display a loading row even tough there is no more data to load.

Version 0.5.1 *(2016-01-03)*
----------------------------

 * Fix: calling the `displayLoadingRow(true|false)`upon AbsListView `WrapperAdapter` will now call
 `notifyDataSetChanged` if needed.

Version 0.5.0 *(2015-11-07)*
----------------------------

 * Initial public release