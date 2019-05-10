package net.wildfyre.client.views.markdown

import net.wildfyre.client.Constants
import net.wildfyre.client.data.Image

fun String.prepareForMarkdown(imageUrls: List<Image>?): String =
    replace(Constants.Api.IMAGE_REGEX) {
        val imageNum = it.groups[1]?.value?.toInt() ?: 0
        val image = imageUrls?.find { img -> img.num == imageNum.toLong() }
        if (imageUrls != null) "![${image?.comment}](${image?.image})" else it.value
    }