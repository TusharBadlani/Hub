package cessini.technology.cvo.exploremodels

data class SearchVideoModel(
        val id: String?,
        val title: String?,
        val description: String?,
        val category: CategoryModel?,
        val duration: Int?,
        val thumbnail: String?,
        val upload_file: String?,
        val timestamp: String?,
        val profile: ProfileModel?
)
