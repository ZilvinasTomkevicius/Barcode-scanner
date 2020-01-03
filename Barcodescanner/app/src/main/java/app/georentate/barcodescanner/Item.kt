package app.georentate.barcodescanner

data class Item(
    val key: String,
    val code: String,
    val date: String
) {
    constructor() : this (
        "",
        "",
        ""
    )
}