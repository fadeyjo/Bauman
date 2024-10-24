package com.example.lw_5_client

class Composition {
    var id: Int = 0
    var author: String
    var title: String
    var duration: Int = 0

    constructor(id: Int, author: String, title: String, duration: Int) {
        this.id = id
        this.author = author
        this.title = title
        this.duration = duration
    }

    constructor(author: String, title: String, duration: Int) {
        this.author = author
        this.title = title
        this.duration = duration
    }
}