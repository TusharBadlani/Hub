package cessini.technology.home.pagingSource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import cessini.technology.home.grid.home_data

/*
class HomeFeedPagingSource: PagingSource<Int, home_data>() {
    override fun getRefreshKey(state: PagingState<Int, home_data>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, home_data> {
        val pageNumber = params.key ?: 1
        val previousKey = if (pageNumber == 1) null else pageNumber - 1

        val pageRequest = "" // TODO: Make Network Call when API is active
        TODO: Handle Network Call Exception
           pageRequest.exception?.let {
            return
        }

        TODO: Return result
           return LoadResult.Page(
            data = pageRequest.body.results.map { response ->

            },
            prevKey = previousKey,
            nextKey = getPageIndexFromNext(pageRequest.body.info.next)
        )

    }

    private fun getPageIndexFromNext(next: String?): Int? {
        return next?.split("?page=")?.get(1)?.toInt()
    }
}*/
