package world.neptuns.inventory.api.inventory

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class InventoryProperties(
    val id: String,
    val rows: Int = 1,
    val columns: Int = 9,
    val permission: String = "",
    val closeable: Boolean = true,
    val playSoundOnClick: Boolean = true,
    val playSoundOnOpen: Boolean = true,
    val playSoundOnClose: Boolean = true,
    val playSoundOnPageSwitch: Boolean = true
)