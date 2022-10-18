package cessini.technology.cvo.homemodels

data class HomeFeedResponseItem(
    var id: Int,
    var profile: VideoProfileModel?,
    var title: String?,
    var description: String?,
    var category: String?,
    var thumbnail: String?,
    var duration: String?,
    var upload_file: String?,
    var timestamp: String?
)