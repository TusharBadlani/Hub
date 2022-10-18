package cessini.technology.profile.epoxydatabinding;

import com.airbnb.epoxy.EpoxyDataBindingLayouts;

import cessini.technology.profile.R2;

@EpoxyDataBindingLayouts({R2.layout.gallery_recycler_row,
        R2.layout.profile_video_row,
        R2.layout.list_item_request,
        R2.layout.list_item_room,
        R2.layout.list_item_room_public,
        R2.layout.list_item_sep_request,
        R2.layout.create_room_request,
        R2.layout.follower_following_item,
        R2.layout.item_chat_me,
        R2.layout.item_chat_other,
        R2.layout.item_chat_header,
        R2.layout.user_hub})
interface EpoxyDataBindingConfig {
}
