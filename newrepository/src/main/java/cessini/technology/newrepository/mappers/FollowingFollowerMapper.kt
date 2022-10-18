package cessini.technology.newrepository.mappers

import cessini.technology.model.User
import cessini.technology.newapi.services.myworld.model.response.ApiFollowerFollowing

fun ApiFollowerFollowing.toModel(): List<User> {
    var followersOrFollowing = emptyList<User>()

    if(data.isNotEmpty()) {
        followersOrFollowing = data.map {
            it.toModel()
        }
    }

    return followersOrFollowing
}
