package cessini.technology.model.recordmyspacegrid

data class viewpagerItem(
    val categoryName:String,
    val grids: MutableList<recordGrid> = mutableListOf()
)
