package cessini.technology.newrepository.mappers

import cessini.technology.model.Profile
import cessini.technology.newapi.services.myworld.model.response.ApiProfile

fun ApiProfile.toModel() = Profile(
    id = id,
    name = name,
    email = email,
    channelName = channelName,
    provider = provider,
    bio = bio,
    location = location,
    profilePicture = profilePicture,
    followerCount = followerCount,
    followingCount = followingCount,
    following = following,
    expertise = expertise,
    verified = verified
)
