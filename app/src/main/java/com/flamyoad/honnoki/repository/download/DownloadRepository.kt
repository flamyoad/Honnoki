package com.flamyoad.honnoki.repository.download

import com.flamyoad.honnoki.data.entities.Chapter

interface DownloadRepository {
    suspend fun downloadChapters(chapters: List<Chapter>)
}