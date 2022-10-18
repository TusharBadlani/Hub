package cessini.technology.cvo.homemodels;
import android.graphics.drawable.Drawable
import com.google.gson.annotations.SerializedName

//Int is just temporary in profileImage Url
data class GridItems(
        var profession: String,
        var name: String,
        var profileImageUrl: String,
        var drawable:Drawable?
);