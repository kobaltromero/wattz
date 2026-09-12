## Create: Wattz

### Installation Requirements

Create: Wattz runs on NeoForge 1.21.1 currently, but I am planning to backport to Forge 1.20.1 through 1.18.2 in the future. Don't ask for a Fabric Port, to my knowledge Voltaic and Electrodynamics won't be available on Fabric.

Download and install the latest version of NeoForge for 1.21.1 [here](https://neoforged.net/).

Download Voltaic API [here](https://www.curseforge.com/minecraft/mc-mods/voltaic-api).

Download Create [here](https://www.curseforge.com/minecraft/mc-mods/create).


### About

Create: Wattz is an addon for create intended to be used with mods that utilize the Voltaic API by aurilisdev. While Electrodynamics is not a required dependancy it is highly encouraged to install alongside this mod to be able to take full advantage of what the mod has to offer.

Currently, this mod adds 5 tiers of Alternator Blocks that convert Create's Kinetic Energy to Electricity that can be used by mods that use the Voltaic API.

By downloading this mod, I assume you understand how Electrodynamics works. If you do not, I highly recommend joining the Electrodynamics or Ampz Discord.

### Why

Sure, there's mods like Create: New Age and Create: Crafts and Additions that add a similar concept of converting RPM to FE but people have been requesting an addon for Electrodynamics that adds direct conversion to and from the Voltaic Electricity API. As someone who uses Create and Electrodynamics himself, I understand the appeal of this.

Yes, I am aware that you can convert FE to Electricity through a Battery Box or use Dynamic Electricity but the whole point of this mod is to eliminate the need for a middleman.

### Crafting Recipes
<details>
<summary>Electrodynamics</summary>

___Alternator (120V)___

```
"type": "minecraft:crafting_shaped",
  "pattern": [
    "011",
    "234",
    "051"
  ],
  "key": {
    "0": { "item": "electrodynamics:insulation" },
    "1": { "tag": "c:ingots/steel" },
    "2": { "item": "create:shaft" },
    "3": { "item": "electrodynamics:motor" },
    "4": { "item": "create:speedometer" },
    "5": { "item": "electrodynamics:multimeter"}
  },
  "result": {
    "id": "wattz:alternator_mk1",
    "count": 1
  }
```
___Alternator Mk 2 (240V)___
```
  "type": "minecraft:crafting_shaped",
  "pattern": [
    "011",
    "234",
    "051"
  ],
  "key": {
    "0": { "item": "electrodynamics:insulation" },
    "1": { "tag": "c:plates/steel" },
    "2": { "item": "create:shaft" },
    "3": { "item": "wattz:alternator_mk1" },
    "4": { "item": "electrodynamics:coil" },
    "5": { "tag": "c:circuits/basic"}
  },
  "result": {
    "id": "wattz:alternator_mk2",
    "count": 1
  }
```
___Alternator Mk 3 (480V)___
```
  "type": "minecraft:crafting_shaped",
  "pattern": [
    "011",
    "234",
    "051"
  ],
  "key": {
    "0": { "item": "electrodynamics:insulation" },
    "1": { "tag": "c:plates/steel" },
    "2": { "item": "create:shaft" },
    "3": { "item": "wattz:alternator_mk2" },
    "4": { "item": "electrodynamics:laminatedcoil" },
    "5": { "tag": "c:circuits/advanced"}
  },
  "result": {
    "id": "wattz:alternator_mk3",
    "count": 1
  }
```
___Alternator Mk 4 (960V)___
```
  "type": "minecraft:crafting_shaped",
  "pattern": [
    "011",
    "234",
    "051"
  ],
  "key": {
    "0": { "item": "electrodynamics:insulation" },
    "1": { "tag": "c:plates/steel" },
    "2": { "item": "create:shaft" },
    "3": { "item": "wattz:alternator_mk3" },
    "4": { "item": "electrodynamics:laminatedcoil" },
    "5": { "tag": "c:circuits/elite"}
  },
  "result": {
    "id": "wattz:alternator_mk4",
    "count": 1
  }
```
___Alternator Mk 5 (1.9kV)___
```
  "type": "minecraft:crafting_shaped",
  "pattern": [
    "011",
    "234",
    "051"
  ],
  "key": {
    "0": { "item": "electrodynamics:insulationceramic" },
    "1": { "tag": "c:plates/steel" },
    "2": { "item": "create:shaft" },
    "3": { "item": "wattz:alternator_mk4" },
    "4": { "item": "electrodynamics:laminatedcoil" },
    "5": { "tag": "c:circuits/ultimate"}
  },
  "result": {
    "id": "wattz:alternator_mk5",
    "count": 1
  }
```
</details>

<details>
<summary>Dynamic Electricity</summary>

___Alternator (120V)___

```
  "type": "minecraft:crafting_shaped",
  "pattern": [
    "012",
    "345",
    "062"
  ],
  "key": {
    "0": { "item": "electrodynamics:insulation" },
    "1": { "item": "create:speedometer" },
    "2": { "tag": "c:ingots/steel" },
    "3": { "item": "dynamicelectricity:alternator" },
    "4": { "item": "create:shaft" },
    "5": { "item": "dynamicelectricity:stator"},
    "6": { "item": "electrodynamics:multimeter"}
  },
  "result": {
    "id": "wattz:alternator_mk1",
    "count": 1
  }
```
___Alternator Mk 2 (240V)___
```
  "type": "minecraft:crafting_shaped",
  "pattern": [
    "011",
    "234",
    "051"
  ],
  "key": {
    "0": { "item": "electrodynamics:insulation" },
    "1": { "tag": "c:plates/steel" },
    "2": { "item": "dynamicelectricity:alternator" },
    "3": { "item": "wattz:alternator_mk1" },
    "4": { "item": "dynamicelectricity:stator" },
    "5": { "tag": "c:circuits/basic"}
  },
  "result": {
    "id": "wattz:alternator_mk2",
    "count": 1
  }
```
___Alternator Mk 3 (480V)___
```
  "type": "minecraft:crafting_shaped",
  "pattern": [
    "011",
    "234",
    "051"
  ],
  "key": {
    "0": { "item": "electrodynamics:insulation" },
    "1": { "tag": "c:plates/steel" },
    "2": { "item": "dynamicelectricity:alternator" },
    "3": { "item": "wattz:alternator_mk2" },
    "4": { "item": "dynamicelectricity:stator" },
    "5": { "tag": "c:circuits/advanced"}
  },
  "result": {
    "id": "wattz:alternator_mk3",
    "count": 1
  }
```
___Alternator Mk 4 (960V)___
```
  "type": "minecraft:crafting_shaped",
  "pattern": [
    "011",
    "234",
    "051"
  ],
  "key": {
    "0": { "item": "electrodynamics:insulation" },
    "1": { "tag": "c:plates/steel" },
    "2": { "item": "dynamicelectricity:alternator" },
    "3": { "item": "wattz:alternator_mk3" },
    "4": { "item": "dynamicelectricity:stator" },
    "5": { "tag": "c:circuits/elite"}
  },
  "result": {
    "id": "wattz:alternator_mk4",
    "count": 1
  }
```
___Alternator Mk 5 (1.9kV)___
```
  "type": "minecraft:crafting_shaped",
  "pattern": [
    "011",
    "234",
    "051"
  ],
  "key": {
    "0": { "item": "electrodynamics:insulationceramic" },
    "1": { "tag": "c:plates/steel" },
    "2": { "item": "dynamicelectricity:alternator" },
    "3": { "item": "wattz:alternator_mk4" },
    "4": { "item": "dynamicelectricity:stator" },
    "5": { "tag": "c:circuits/ultimate"}
  },
  "result": {
    "id": "wattz:alternator_mk5",
    "count": 1
  }
```
</details>

<small>Recipes are subject to change. What recipes are available depends on what mods are installed alongside this one and are up to the discretion of modpack makers.</small>

### Future Plans

- Add Electric Motors (Converts Electricity to RPM)

### Q&A

___Q. How do I check how much electricity an Alternator is producing?___

A. Put on the Engineers' Goggles. The Engineer's Goggles from Create will show the stats of each Alternator/Electric Motor.

___Q. What is "satisfaction"?___

A. Satisfaction is the measurement of how much RPM is going into the Alternator. The maximum speed is 256 RPM. The higher the satisfaction is, the more power it will produce.

___Q. Will you port this mod to <=1.17.x?___

A. I have zero plans on porting this mod to anything before 1.18.2. However, you are allowed to do so if you *really* want to. Just talk to me first about it. I don't like being kept in the dark.

___Q. Can I translate this mod?___

A. Sure, provide me language files and I'll add them to the mod.

___Q. If I encounter an issue with this mod, what do I do?___

A. Open an issue on the mod's github or join my discord and we'll help you. Alternatively, I am in the electrodynamics discord so you can ask me there as well. Same username as mine here on CurseForge.



