package com.myproject.library.repository

import com.myproject.library.data.model.LibraryData
import com.myproject.library.data.model.LibraryOrders
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class LibraryRepository {
    private val libraryData = mutableListOf<LibraryOrders>()

    init {
        if (libraryData.isEmpty()) {
            LibraryData.library.forEach {
                libraryData.add(LibraryOrders(it, 0))
            }
        }
    }
    
    fun getAllLibrary() : Flow<List<LibraryOrders>> {
        return flowOf(libraryData)
    }
    
    fun getLibraryById(id: Int): LibraryOrders {
        return libraryData.first {
            it.library.id == id
        }
    }
    
    fun searchLibrary(data: String) : List<LibraryOrders> {
        return libraryData.filter {
            it.library.name.contains(data, ignoreCase = true)
        }
    }
    
    fun updateFavorite(id: Int, like: Int) : Flow<Boolean> {
        val index = libraryData.indexOfFirst { it.library.id == id}
        val result = if (index >= 0) {
            val LibraryCount = libraryData[index]
            libraryData[index] = LibraryCount.copy(library = LibraryCount.library, count = like)
            true
        } else {
            false
        }
        return flowOf(result)
    }
    
    fun getLibraryOrder() : Flow<List<LibraryOrders>> {
        return getAllLibrary()
            .map { LibraryOrders ->
                LibraryOrders.filter {
                    it.count != 0
                }
            }
    }
    
    companion object {
        @Volatile
        private var instance: LibraryRepository? = null
    
        fun getInstance() : LibraryRepository =
            instance ?: synchronized(this) {
                LibraryRepository().apply {
                    instance = this
                }
            }
    }
}