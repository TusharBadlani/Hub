package cessini.technology.home.model

data class HomeEpoxyStreamsModel(
    val link:String,
    val title:String,
    val room: String,
    val admin:String,
    /*val adminProfileImageUrl:String,*/
    val user:User,
    val email:String
)
