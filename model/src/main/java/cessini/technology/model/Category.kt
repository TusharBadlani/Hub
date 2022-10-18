package cessini.technology.model


data class Category(
    val message: String,
    val data: Map<String, List<Subcategory>>,
    val status: Boolean
)
