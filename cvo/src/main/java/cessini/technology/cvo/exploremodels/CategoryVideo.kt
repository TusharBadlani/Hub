package cessini.technology.cvo.exploremodels

data class CategoryVideo(
    var id: String?,
    var title: String?,
    var description: String?,
    var duration: Int?,
    var thumbnail: String?,
    var upload_file: String?,
    var timestamp: String?,
    var profile: ProfileModel
)
