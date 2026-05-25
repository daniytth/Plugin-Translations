<p align="center">
  <b><a>Welcome to FarmSystem's repository!</a></b><br/>
  <i>Lightweight, Folia-compatible crop regeneration plugin with WorldGuard region support.</i>
</p>

## Features
- **Folia Support** - Built with FoliaLib's region scheduler, fully compatible with both Paper and Folia servers.
- **WorldGuard Integration** - Crop regeneration only triggers inside configured WorldGuard regions; vanilla behavior is preserved everywhere else.
- **Per-crop Configuration** - Independently configure drop amount, regeneration delay, and regeneration age for each crop type.
- **Chunk-level Caching** - Region lookups are cached at the chunk level to minimize WorldGuard query overhead.
- **Automatic Regeneration** - Broken crops are replaced after a configurable delay, restored at a specific growth stage.
- **Custom Drop Handling** - Drops are given directly to the player's inventory, with natural item drops for overflow.
- **Live Reload** - Reload configuration and flush region cache at runtime without restarting the server.

## Supported Crops
- Wheat
- Carrots
- Potatoes
- Beetroots
- Nether Wart

## Commands
| Command | Description | Permission |
|---|---|---|
| `/farm reload` | Reload configuration and invalidate region cache | `farm.reload` |

## Permissions
| Permission | Description | Default |
|---|---|---|
| `farm.reload` | Use `/farm reload` | op |

## Configuration
```yaml
# WorldGuard region names where crop regeneration is enabled.
# Crops broken outside these regions behave vanilla.
regions:
  - "farm1"
  - "farm2"

# Per-crop settings.
crops:
  wheat:
    drop-amount: 2 # Number of items dropped on break
    regeneration-delay: 2 # Seconds before the crop regrows
    regeneration-age: 7 # Growth stage the crop regrows at (max for wheat = 7)
```

## Libraries
FarmSystem uses and is compiled with the following libraries:
- [FoliaLib](https://github.com/TechnicallyCoded/FoliaLib) (included) - Cross-platform scheduler library for Paper and Folia compatibility.
- [WorldGuard](https://dev.enginehub.org/worldguard/) (soft dependency) - Region API used to scope crop regeneration to defined areas.
