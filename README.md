# Honnoki ほんの木

Basically another Manga Reader

Heavily uses flavour of the year libs like [Paging 3](https://developer.android.com/topic/libraries/architecture/paging/v3-overview), [Flow](https://developer.android.com/kotlin/flow),  [ConcatAdapter](https://developer.android.com/reference/androidx/recyclerview/widget/ConcatAdapter), and [Jetpack DataStore](https://developer.android.com/topic/libraries/architecture/datastore) on top of your usual MVVM+Retrofit+Koin architecture

The reader screen in this app mimics the ones from [DM5.cn](https://www.dm5.com/download/]). It is like a continuous scrolling list that is able to prefetch next chapter without the need for Prev/Next buttons. I used [ConcatAdapter](https://developer.android.com/reference/androidx/recyclerview/widget/ConcatAdapter) to achieve all that but the DM5 app was released like way back in 2015(?). So I think they used another method to achieve the style & functionality. Big applause to the dev of DM5 because IMO it was ahead of its time in terms of UX & performance.

#### Sources supported
* MangaNelo (EN)
* MangaDex (EN + Others)
* ReadMng (EN)
* DM5.cn (CH)
* SenManga (JP)

## Preview

| Home(Light) | Overview  | Reader
| :---------: |:---------:|:-------:
| ![Screenshot_2021-06-27-23-42-02-026_com flamyoad honnoki](https://user-images.githubusercontent.com/35066207/123550760-aac9f200-d7a1-11eb-9904-3b086418aec1.jpg)| ![Screenshot_2021-06-01-14-09-21-908_com flamyoad honnoki](https://user-images.githubusercontent.com/35066207/120275270-a403a880-c2e3-11eb-9fa3-b97123c1b16f.jpg)| ![Screenshot_2021-06-18-11-56-31-635_com flamyoad honnoki](https://user-images.githubusercontent.com/35066207/123550967-4ce9da00-d7a2-11eb-8268-31729fba431d.jpg)

| Bookmarks | Read History | Search
| :-------: |:------------:|:-------:
|![Screenshot_2021-06-21-18-25-39-446_com flamyoad honnoki](https://user-images.githubusercontent.com/35066207/123550852-ff6d6d00-d7a1-11eb-9c60-9b4364f5aebd.jpg) | ![Screenshot_2021-06-21-18-26-13-820_com flamyoad honnoki](https://user-images.githubusercontent.com/35066207/123550904-1f9d2c00-d7a2-11eb-8485-a2ed039d2ad7.jpg) | ![Screenshot_2021-06-27-23-38-24-625_com flamyoad honnoki](https://user-images.githubusercontent.com/35066207/123550956-42c7db80-d7a2-11eb-959f-e0ff6eedd35f.jpg)


