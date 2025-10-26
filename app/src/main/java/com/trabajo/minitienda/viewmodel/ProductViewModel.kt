package com.trabajo.minitienda.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trabajo.minitienda.data.model.Product
import com.trabajo.minitienda.repository.ProductRepository
import kotlinx.coroutines.launch

class ProductViewModel(private val repository: ProductRepository) : ViewModel() {

    val products = MutableLiveData<List<Product>>()

    fun loadProducts() {
        viewModelScope.launch {
            products.value = repository.getAllProducts()
        }
    }

    fun addProduct(product: Product) {
        viewModelScope.launch {
            repository.insertProduct(product)
            loadProducts()
        }
    }
    
    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            repository.deleteProduct(product)
            loadProducts()
        }
    }

}