package com.myproject.library.di

import com.myproject.library.repository.LibraryRepository

object Injection {
    fun providedeRepository() : LibraryRepository {
        return LibraryRepository.getInstance()
    }
}