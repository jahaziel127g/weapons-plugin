# WeaponsPlugin

A custom Minecraft Spigot/Paper (1.21) plugin that adds unique, powerful weapons obtained through immersive rituals.

## Features

### ⚔️ Custom Weapons
Each weapon has unique abilities and textures (via CustomModelData).

- **Scythe of Light** (Mace)
  - *Attributes*: 8 Attack Damage, 1.6 Attack Speed (Sword-like stats).
  - *Ability*: **Holy Smash**. Deals increased damage when falling (Native Mace mechanic).
  - *Passive*: Sparkle effects on hit.
- **Scythe of Darkness** (Netherite Axe)
  - *Ability*: **Dark Wave** (Right-click). Launches a wave of shadow particles that damages enemies and transfers your potion effects to them. Costs 25% health.
  - *Passive*: **Reap**. Pulls enemies towards you on hit.
- **Wither Launcher** (Crossbow)
  - *Ability*: **Wither Shot**. Leunches a non-destructive Wither Skull.
  - *Effect*: Applies Wither II to targets on impact.
- **Lifestealer** (Netherite Sword)
  - *Passive*: Heals wielder for 50% of damage dealt.
  - *Ability*: **Blood Shield** (Right-click). Grants high Absorption for 60s.
- **King's Crown** (Netherite Helmet)
  - *Passive*: Unbreakable.
  - *Ability*: **Bounty**. Allows the wearer to set bounties.

### 🕯️ Ritual System
Weapons are not crafted normally but created through a **Ritual**.
- **Usage**: `/ritual <item_id>` (OP only).
- **Process**:
  - The ritual builds a structure (Beacon + Spruce) and plays effects over time.
  - **Global Messages**: The entire server is notified when a ritual starts and completes.
- **Unique Limit**: Each weapon is **unique**. It can only be crafted once per server history unless destroyed.

### 💀 Destruction & Recovery
If a Legendary Weapon is lost, it can be crafted again.
- **Destruction Detection**:
  - Dropped in **Lava/Fire/Void**.
  - Destroyed by **Explosion**.
  - Player dies in the **Void**.
- **Effect**: A global message announces the destruction of the ancient weapon, and it is automatically unmarked from the registry, allowing a new ritual to start.

## Commands

- `/ritual <item_id>` - Start a ritual for a specific item (OP only).
  - IDs: `scythe_of_light`, `scythe_of_darkness`, `wither_launcher`, `lifestealer`, `kings_crown`.
- `/weapon reset` - Resets all "crafted" flags, allowing all items to be crafted again (OP only).
- `/bounty <player>` - Highlights a target player (Glowing). Only usable by the **King's Crown** wearer.

## Installation
1. Drop the jar into your server's `plugins` folder.
2. Restart the server.
3. Ensure you are running **Minecraft 1.21+**.

## Permissions
- `weapons.waiting` (Default: OP) - Required for commands.
