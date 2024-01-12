package net.spacetivity.inventory.api.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class InventoryProperties(
    val id: String,
    val rows: Int = 1,
    val columns: Int = 9,
    val permission: String = "",
    val closeable: Boolean = true
)
