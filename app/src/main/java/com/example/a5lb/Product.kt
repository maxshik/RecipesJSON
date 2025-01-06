package com.example.a5lb

data class Product (var name: String, var kitchen: String,
                    var shortDescription: String,
                    var steps: String,
                    var dishImg: String,
                    var products: MutableList<String>)