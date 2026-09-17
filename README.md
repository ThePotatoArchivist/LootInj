# LootInj

LootInj is a simple data-driven loot modification library, primarily for mods. It essentially acts as a JSON frontend to the Fabric API `LootModification.MODIFY` event.

## Features

- Can add pools & modifiers, and modify pools
- Supports data generation via `FabricDynamicRegistryProvider` (26.3+) or `FabricCodecDataProvider` (26.1-26.2)
  - Datagen matches the vanilla/FAPI `LootTable.Builder` and `LootPool.Builder` api
- Can access other loot registries such as `minecraft:predicate`, `minecraft:item_modifier`, `minecraft:context_int_provider`, etc. (26.3+)

## Format

In your mod data/datapack, loot modifications are JSON files at `data/<namespace>/lootinj/loot_modification/<path>.json`. The exact namespace and path do not matter, they should just be unique.

The format generally follows the vanilla loot table format:

<details>

<summary>26.3+ Format</summary>

```jsonc
{
  // All fields are optional except `targets`

  // A loot table id or list of ids to modify
  "targets": [
    "minecraft:blocks/dirt"
  ],

  // A list of loot pools to add
  "pools": [
    {
      "rolls": 1,
      "entries": [
        {
          "type": "minecraft:item",
          "name": "minecraft:diamond"
        }
      ]
    }
  ],

  // A loot function or list of functions to add to the whole table
  "modifiers": {
    "count": 2,
    "function": "minecraft:set_count"
  },

  // Modify all existing pools
  // Requires some extra processing so only include if you need to modify pools
  "modify_pools": {
    // Again, all fields are optional

    // A list of entries to add to the pool
    "entries": [
      {
        "type": "minecraft:item",
        "name": "minecraft:gold_ingot"
      }
    ],
    
    // A loot condition or list of conditions to add to the whole pool
    "condition": {
      "type": "minecraft:survives_explosion"
    },

    // A loot function or list of functions to add to the whole pool
    "modifier": {
      "count": 3,
      "function": "minecraft:set_count"
    }

  }

}
```

</details>

<details>

<summary>26.1-26.2 Format</summary>

```jsonc
{
  // All fields are optional except `targets`

  // A loot table id or list of ids to modify
  "targets": [
    "minecraft:blocks/dirt"
  ],

  // A list of loot pools to add
  "pools": [
    {
      "rolls": 1,
      "entries": [
        {
          "type": "minecraft:item",
          "name": "minecraft:diamond"
        }
      ]
    }
  ],

  // A list of loot functions to add to the whole table
  "functions": [
    {
      "count": 2,
      "function": "minecraft:set_count"
    }
  ],

  // Modify all existing pools
  // Requires some extra processing so only include if you need to modify pools
  "modify_pools": {
    // Again, all fields are optional

    // A list of entries to add to the pool
    "entries": [
      {
        "type": "minecraft:item",
        "name": "minecraft:gold_ingot"
      }
    ],
    
    // A list of loot condition to add to the whole pool
    "conditions": [
      {
        "type": "minecraft:survives_explosion"
      }
    ],

    // A list of loot functions to add to the whole pool
    "functions": [
      {
        "count": 3,
        "function": "minecraft:set_count"
      }
    ]

  }

}
```

</details>

## Comparison

I made this library because [Datapatched](https://modrinth.com/mod/datapatched) did not quite fit my needs:

- I usually like to datagen as much as possible, and Datapatched does not work with data generation
- I usually try to update my mods within a few days of a minecraft update, and Datapatched sometimes takes longer to update
- I primarily mod for Fabric 26.1+ so this library will only support those versions

If you are an end user who doesn't care about these and/or is on Neoforge, Datapatched will probably work better for you as it is better documented and supports more versions & loaders. 

---

<details>

<summary>Developer Installation</summary>

Modrinth maven is preferred, but the project is not yet approved on Modrinth. Until then, install and JiJ the mod from JitPack:

```properties
# gradle.properties
lootinj_version=1.0.2+mc26.3
```

```groovy
// build.gradle
repositories {
    maven {
        name = "JitPack"
        url = "https://jitpack.io"
    }
}

dependencies {
    // ...
    implementation include("com.github.ThePotatoArchivist:LootInj:${project.lootinj_version}")
}
```

</details>
