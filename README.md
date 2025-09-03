# WeaponsPlugin

![WeaponsPlugin](https://img.shields.io/badge/Version-1.0-blue)  
Minecraft Paper 1.21 plugin that adds custom weapons with abilities, visual effects, and rituals for crafting.

---

## **Features**

- **Custom Weapons:**
    - **Scythe of Light** – heavy sword with extra damage and feather particle effects on hit.
    - **Scythe of Darkness** – axe that pulls enemies toward you and creates a shadow wave (right-click).
    - **Wither Launcher** – crossbow that shoots wither skulls, applying Wither effects on enemies.
    - **Lifestealer** – sword that heals you for 50% of the damage you deal; temporary absorption on right-click.
    - **King’s Crown** – helmet that grants regal cosmetics and can be extended with passive bonuses.

- **Ritual System:**
    - OP-only rituals to craft weapons at specific locations.
    - BossBar shows ritual progress.
    - Fireworks, titles, and sounds indicate completion.
    - Keeps a persistent record of crafted weapons.

- **Visual Effects:**
    - Feather trails and sparkles for attacks and heals.
    - Smoke, sweep, and particle effects for abilities.
    - Sound effects for actions and ability feedback.

- **Persistent Items:**
    - Each weapon has a unique **persistent ID** stored via Bukkit `PersistentDataContainer`.
    - Items keep their identity when stored in chests or containers.

- **Cooldowns & Balancing:**
    - Cooldown manager ensures abilities can’t be spammed.
    - Configurable cooldowns in code (can be moved to `config.yml` later).

---

## **Installation**

1. **Build the plugin:**

```bash
mvn clean package
