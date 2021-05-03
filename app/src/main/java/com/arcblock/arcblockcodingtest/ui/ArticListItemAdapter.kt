package com.arcblock.arcblockcodingtest.ui

import ArticListDataItem
import android.widget.ImageView
import coil.load
import com.arcblock.arcblockcodingtest.R
import com.arcblock.arcblockcodingtest.logic.network.ServiceCreator
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

/**
 * 文章列表Item adapter类
 * @constructor
 */
class ArticListItemAdapter(layoutResId: Int, data: MutableList<ArticListDataItem>?) :
    BaseQuickAdapter<ArticListDataItem, BaseViewHolder>(layoutResId, data) {

    override fun convert(holder: BaseViewHolder, item: ArticListDataItem) {
        holder.setText(R.id.artic_title_tv, item.frontmatter.title)
        holder.setText(R.id.artic_time_tv, "- ${item.frontmatter.date}")
        holder.setText(R.id.artic_categories_tv, item.frontmatter.categories[0])
        val tags = item.frontmatter.tags.map {//这里简单的对每个标签进行拼接转换处理
            "#$it "
        }.toString().replace("[", "").replace("]", "").replace(",", "")
        holder.setText(R.id.artic_tags_tv, tags)
        holder.getView<ImageView>(R.id.artic_src_iv)
            .load(ServiceCreator.BASE_URL + item.frontmatter.banner.childImageSharp.fixed.src)
    }
}