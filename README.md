# SpaceInventory API

The SpaceInventory API is a powerful inventory management system for Bukkit/Spigot servers, offering advanced features such as placeholders, interactive items, and pagination.

## Installation

To integrate the SpaceInventory API into your Bukkit/Spigot project using Gradle, add the following dependency to your `build.gradle` file:

```groovy
SOON...
```

## Getting Started

### Example Inventory

Let's delve into a detailed example of creating a custom inventory:

```java
package net.spacetivity.inventory.bukkit;

import net.spacetivity.inventory.api.annotation.InventoryProperties;
import net.spacetivity.inventory.api.inventory.InventoryController;
import net.spacetivity.inventory.api.inventory.InventoryProvider;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@InventoryProperties(id = "example_inv", rows = 3, columns = 9, permission = "test.open")
class ExampleInventory : InventoryProvider {

    override fun init(player: Player, controller: InventoryController) {
        // Add placeholders
        controller.placeholder(1, 1, Material.DIAMOND_SWORD)

        // Add an interactive item
        controller.setItem(2, 4, InteractiveItem(ItemStack(Material.APPLE)) { pos, item, event ->
                player.sendMessage("You clicked the apple!")
        })
    }
    
}
```

In the `init` function, you can build your interesting example by adding placeholders, interactive items, and more.

## API Overview

### Annotations

- `@InventoryProperties`: Annotate your custom inventory class with this to define its properties.

### Interfaces

- `InventoryController`: Interface representing the core functionality of an inventory controller.
- `InventoryHandler`: Interface for managing and caching inventories for players.
- `InventoryProvider`: Interface for initializing a specific inventory controller.

### Classes

- `SpaceInventoryProvider`: A class for registering the SpaceInventory API.

## Usage

### Creating Custom Inventories

To create a custom inventory, follow these steps:

1. **Create a Class:** Start by creating a Java class for your inventory and annotate it with `@InventoryProperties`.

    ```kotlin
    @InventoryProperties(id = "example_inv", rows = 3, columns = 9, permission = "test.open")
    class ExampleInventory : InventoryProvider {
    
    }
    ```

2. **Implement Interface:** Implement the `InventoryProvider` interface and override the `init` function to build your inventory.

    ```kotlin
    override fun init(player: Player, controller: InventoryController) {
    // Customize your inventory here
    }
    ```

### Interactive Items

Use the `InteractiveItem` class to create items that trigger custom actions when clicked. Here's a detailed breakdown:

- **Constructor:** The constructor takes an `ItemStack` and an action to be performed when the item is clicked.

    ```kotlin
    InteractiveItem(ItemStack(Material.APPLE)) { pos, item, event -> player.sendMessage("You clicked the apple!") }
    ```

- **Updating Items:** You can update items dynamically using the `update` method. For instance, change the item type or display name.

    ```kotlin
    item.update(controller, InteractiveItem.Modification.TYPE, Material.DIAMOND)
    ```

### Pagination

The SpaceInventory API provides a powerful pagination system for large inventories. Here's how to utilize it:

1. **Create a Pagination:**

    ```kotlin
    val pagination: Pagination = controller.createPagination()
    ```

2. **Distribute Items:**

    ```kotlin
    pagination.distributeItems(myItemList)
    ```

3. **Navigate Through Pages:**

    ```kotlin
    pagination.toNextPage()
    pagination.toPreviousPage()
    pagination.toFirstPage()
    pagination.toLastPage()
    ```

4. **Customize Pagination:**

    - Set the items per page limit.

        ```kotlin
        pagination.limitItemsPerPage(9)
        ```

    - Refresh the current page.

        ```kotlin
        pagination.refreshPage()
        ```

### Annotations

#### `@InventoryProperties`

The `@InventoryProperties` annotation is used to define properties for a custom inventory.

- **Attributes:**
    - `id` (String): Unique identifier for the inventory.
    - `rows` (int): Number of rows in the inventory.
    - `columns` (int): Number of columns in the inventory.
    - `permission` (String): Optional permission required to open the inventory.
    - `closeable` (boolean): Optional flag to determine if the inventory is closeable.

### Interfaces

#### `InventoryController`

The `InventoryController` interface represents the core functionality of an inventory controller.

- **Properties:**
    - `provider` (InventoryProvider): The provider associated with the controller.
    - `properties` (InventoryProperties): The properties of the inventory.
    - `inventorySlotCount` (int): The total number of slots in the inventory.
    - `isCloseable` (boolean): Flag indicating if the inventory is closeable.
    - `contents` (Map<InventoryPosition, InteractiveItem?>): Map of inventory positions to interactive items.
    - `pagination` (InventoryPagination?): The pagination associated with the inventory.
    - `rawInventory` (Inventory?): The raw Bukkit inventory object.
    - `overriddenInventoryId` (String?): The overridden inventory ID, if any.
    - `overriddenRows` (int): The overridden number of rows.
    - `overriddenColumns` (int): The overridden number of columns.

- **Methods:**
    - `getInventoryId()`: Get the inventory ID.
    - `getRows()`: Get the number of rows.
    - `getColumns()`: Get the number of columns.
    - `constructEmptyContent()`: Construct an empty inventory.
    - `placeholder(pos: InventoryPosition, type: Material)`: Add a placeholder at a specific position.
    - `placeholder(row: Int, column: Int, type: Material)`: Add a placeholder at a specific row and column.
    - `setItem(pos: InventoryPosition, item: InteractiveItem)`: Set an item at a specific position.
    - `addItem(item: InteractiveItem)`: Add an item to the inventory.
    - `removeItem(name: String)`: Remove an item by name.
    - `fill(fillType: FillType, item: InteractiveItem, vararg positions: InventoryPosition)`: Fill specific positions with an item.
    - `clearPosition(pos: InventoryPosition)`: Clear an item at a specific position.
    - `isPositionTaken(pos: InventoryPosition)`: Check if a position is already taken.
    - `getPositionOfItem(item: InteractiveItem)`: Get the position of a specific item.
    - `getFirstEmptyPosition()`: Get the first empty position in the inventory.
    - `getItem(pos: InventoryPosition)`: Get the item at a specific position.
    - `getItem(row: Int, column: Int)`: Get the item at a specific row and column.
    - `findFirstItemWithType(type: Material)`: Find the first item of a specific type.
    - `createPagination()`: Create a new pagination for the inventory.
    - `updateRawInventory()`:
