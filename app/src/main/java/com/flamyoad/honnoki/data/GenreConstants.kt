package com.flamyoad.honnoki.data

import android.content.Context
import android.os.Parcelable
import com.flamyoad.honnoki.R
import kotlinx.android.parcel.Parcelize

@Parcelize
enum class GenreConstants(val stringResource: Int): Parcelable {
    ALL(R.string.genre_all),
    ACTION(R.string.genre_action),
    ADULT(R.string.genre_adult),
    ADVENTURE(R.string.genre_adventure),
    COMEDY(R.string.genre_comedy),
    COOKING(R.string.genre_cooking),
    DOUJINSHI(R.string.genre_doujinshi),
    DRAMA(R.string.genre_drama),
    ECCHI(R.string.genre_ecchi),
    FANTASY(R.string.genre_fantasy),
    GENDER_BENDER(R.string.genre_gender_bender),
    HAREM(R.string.genre_harem),
    HISTORICAL(R.string.genre_historical),
    HORROR(R.string.genre_horror),
    ISEKAI(R.string.genre_isekai),
    JOSEI(R.string.genre_josei),
    MANHUA(R.string.genre_manhua),
    MANHWA(R.string.genre_manhwa),
    MARTIAL_ARTS(R.string.genre_martial_arts),
    MATURE(R.string.genre_mature),
    MECHA(R.string.genre_mecha),
    MEDICAL(R.string.genre_medical),
    MYSTERY(R.string.genre_mystery),
    ONE_SHOT(R.string.genre_oneshot),
    PSYCHOLOGICAL(R.string.genre_psychological),
    ROMANCE(R.string.genre_romance),
    SCHOOL_LIFE(R.string.genre_school_life),
    SCIFI(R.string.genre_scifi),
    SEINEN(R.string.genre_seinen),
    SHOUJO(R.string.genre_shoujo),
    SHOUJO_AI(R.string.genre_shoujo_ai),
    SHOUNEN(R.string.genre_shounen),
    SHOUNEN_AI(R.string.genre_shounen_ai),
    SLICE_OF_LIFE(R.string.genre_slice_of_life),
    SMUT(R.string.genre_smut),
    SPORTS(R.string.genre_sports),
    SUPERNATURAL(R.string.genre_supernatural),
    TRAGEDY(R.string.genre_tragedy),
    WEBTOONS(R.string.genre_webtoons),
    YAOI(R.string.genre_yaoi),
    YURI(R.string.genre_yuri);

    fun toReadableName(context: Context): String = context.resources.getString(this.stringResource)

    companion object {
        fun getByOrdinal(ordinal: Int): GenreConstants? = values().getOrNull(ordinal)
    }
}