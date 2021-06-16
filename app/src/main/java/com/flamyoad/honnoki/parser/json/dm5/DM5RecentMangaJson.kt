package com.flamyoad.honnoki.parser.json.dm5

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DM5RecentMangaJson(
    @Json(name = "UpdateComicItems") val items: List<DM5RecentMangaItem>
)

@JsonClass(generateAdapter = true)
data class DM5RecentMangaItem(
    @Json(name ="ID") val id: Int,
    @Json(name ="Title") val title: String,
    @Json(name ="UrlKey")val urlKey: String,
    @Json(name ="ShowLastPartName")val showLastPartName: String,
    @Json(name ="ShowPicUrlB")val showPicUrlB: String,
    @Json(name ="ShowConver") val showConver: String
)
/*
{
         "ID":69640,
         "Title":"从玻璃之瞳中窥视",
         "UrlKey":"manhua-congbolizhitongzhongkuishi",
         "Logo":null,
         "LastPartUrl":"m1150926",
         "ShowLastPartName":"第1话 ",
         "ShowPicUrlB":"https://mhfm8us.cdnmanhua.net/70/69640/20210613235014_180x240_21.jpg",
         "ShowConver":"https://mhfm6us.cdnmanhua.net/70/69640/20210613235040_480x369_68.jpg",
         "ComicPart":null,
         "Author":[
            "井上きぬ"
         ],
         "ShowReads":"72.3万",
         "Content":"一场美丽的邂逅，一个助攻的人偶，这将会给邂逅的两人带来...",
         "Star":2,
         "ShowSource":null,
         "Status":0,
         "LastUpdateTime":"06月13号 更新",
         "ShelvesTime":null
      }
 */
