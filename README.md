# About
I created this mod because I was sick of playing
new mods and datapacks that added new enchantments,
and not knowing the maximum levels of said new enchantments,
meaning that I would never know whether or not my gear was
maxed out or not.

If you have operator on the server, you could determine
the maximum level by using `/enchant @s <enchantmentId> 255`,
and if the command failed, it would tell you the maximum
obtainable level.

If you don't have operator, and knew what mods the server
you were connected to was using, you could install them
and do this in a singleplayer world.

But if you weren't operator, and didn't know what mods
the server you were connected to was using, then you would
never be able to know. This mod fixes that!

# Commands
`/getenchantinfo <enchantmentId>` - Tells you about an enchantment given its ID.

Example:
```text
Command:
/getenchantinfo minecraft:sharpness

Output:
Enchant info for Sharpness:
ID: minecraft:sharpness
Max level: 5
Incompatible with: Bane of Arthropods, Breach, Density, Impaling, Smite
Applied to: Diamond Sword, Stone Sword, Golden Sword, Netherite Sword, Wooden Sword, Iron Sword, Diamond Axe, Stone Axe, Golden Axe, Netherite Axe, Wooden Axe, Iron Axe
```

`/getenchantinfo <enchantmentId>` - Tells you about an enchantment given its name.
If one match is found, it will tell you about that match.
If more than one match is found, it will tell you the names and IDs of the enchantments,
so that you can decide which one you want to know more about.

`/getenchants [<itemId>]` - Tells you the acceptable enchants
for any item given its ID (currently held item if left blank).

Examples:
```text
Command:
/getenchants minecraft:mace

Output:
Conflicting enchantments (choose one per list):
 - Breach IV, Density V
Enchantments with no conflicts:
 - Fire Aspect II
 - Mending
 - Unbreaking III
 - Wind Burst III
 
 Command:
/getenchants minecraft:trident

Output:
Acceptable enchants for Trident:
Conflicting enchantments (choose one per list):
 - Riptide III, Loyalty III, Channeling
Enchantments with no conflicts:
 - Impaling V
 - Mending
 - Unbreaking III
```

`/blacklistedenchants query` - Displays a list of enchants that will not show up in `/getenchants` command outputs.
Example:
```text
Enchantment blacklist:
 - Curse of Binding
 - Curse of Vanishing
 - Blast Protection
 - Projectile Protection
 - Fire Protection
 - Thorns
 - Bane of Arthropods
 - Smite
 - Knockback
 - Frost Walker
```

`/blacklistedenchants add <enchantmentId>` - Adds an enchant to the blacklist (if it is not already blacklisted).

`/blacklistedenchants remove <enchantmentId>` - Removes an enchant from the blacklist (if it is blacklisted).