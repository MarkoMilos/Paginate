package com.paginate;

/**
 * Created by kolobchanin on 22.02.17.
 */

public interface MultiPaginateInterface {
	int getListSize(int index);
	int getCurrentListIndex(int visibleItemPosition);
	int globalPositionToLocal(int globalPosition, int paginatorType);
}
