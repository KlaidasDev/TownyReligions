# TownyReligions

**TownyReligions** is a small addon for **Towny** that lets you give religions to players, towns, and nations.
It’s easy to set up, works with **Pl3xMap**, and supports **PlaceholderAPI**.

---

## How It Works

When you set a religion for a town or nation, the plugin automatically changes its **Towny board** to show the religion name.

For example:

<img width="551" height="131" alt="image" src="https://github.com/user-attachments/assets/c088cb87-07b8-4f73-9973-47f179b82082" />


> ⚠️ You must disable players from using `/town set board` or `/nation set board`,
> otherwise they can overwrite it. The plugin needs full control of the board text.

---

## Features

* Set religions for **residents**, **towns**, and **nations**
* Supports **PlaceholderAPI** placeholders
* Easy config reload with `/religion reload`
* Fully customizable list of allowed religions

---

## Commands

| Command                                      | Description                  | Permission                                             |
| -------------------------------------------- | ---------------------------- | ------------------------------------------------------ |
| `/religion list`                             | Show all available religions | none                                                   |
| `/religion get`                              | Show your current religion   | none                                                   |
| `/religion set resident <player> <religion>` | Set a player’s religion      | `townyreligions.admin` *(or player can set their own)* |
| `/religion set town <town> <religion>`       | Set a town’s religion        | Mayor or admin only                                    |
| `/religion set nation <nation> <religion>`   | Set a nation’s religion      | Nation leader or admin only                            |
| `/religion reload`                           | Reloads the config           | `townyreligions.admin`                                 |

---

## Placeholders (PlaceholderAPI)

* `%townyreligions_religion%` — player’s religion
* `%townyreligions_townreligion%` — town religion
* `%townyreligions_nationreligion%` — nation religion

---

## Default Config (`config.yml`)

```yaml
#Religions Fully Customizable
allowed-religions:
  - Orthodox
  - Catholic
  - Protestant
  - Muslim
  - Jewish
  - Pagan
  - Atheist

messages:
  prefix: '&7[&dReligion&7] '
  list-header: '&aAvailable religions:'
  list-commands: '&aReligion Commands:'
  set-success: '&aSet &e%target%&a to religion &e%religion%&a.'
  invalid-religion: '&cThat religion does not exist. Use /religion list.'
  current: '&aYour current religion: &e%religion%'
  none: '&cYou have not set a religion yet.'
  no-permission: '&cYou don’t have permission to do that.'
```

---

## Installation

1. Drop **TownyReligions.jar** into your `/plugins` folder
2. Restart your server
3. Edit the config to add your religions
4. Run `/religion reload` in game

---

## Requirements

* Paper/Spigot 1.19+
* Towny (latest version)
* *(optional)* PlaceholderAPI

---
## WebMap Integration
- If you would like to display your "Religion" on the webmap:
Please navigate to /plugins/MapTowny/click_tooltip.html
Here you can add 'Religion: %board%'

Now it can show up like this:

<img width="308" height="143" alt="image" src="https://github.com/user-attachments/assets/e304d969-ef2b-450a-b547-a6a6e333045b" />
---

## Developer
- If you have any questions feel free to reach out to my discord: 
https://discord.com/users/655781647899164692

**Author:** [KlaidasDev](https://github.com/KlaidasDev)
License: MIT 
