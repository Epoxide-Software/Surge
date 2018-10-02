# Surge [![](http://cf.way2muchnoise.eu/250290.svg)](https://minecraft.curseforge.com/projects/250290) [![](http://cf.way2muchnoise.eu/versions/250290.svg)](https://minecraft.curseforge.com/projects/250290)
Surge is an open source mod which aims to improve the load time and performance of the game. 

## Features

### Load Times
- **Fast Prefix Checking** - Cleans up Forge's ID prefix checking, to improve the time it takes to create and register new things to the game registry. 
- **Animated JSON Model Checking** - Cleans up Forge's custom animated model loader to greatly improve model loading times.
- **Disable Debug Sound Info** - Turns off debug code for missing sounds and missing subtitles. This will improve sound loading times.

### Performance
- **SheepDyeBlendTable** - Switches sheep color blending code to use a predefined table instead of the vanilla behaviour which is to do a recipe lookup.

### Bug Fixes
- **Max Rename Length** - Fixes a bug where renaming long items in an anvil will cause the name to error out.
- **Entity Wall Glitching** - Fixes a bug which allows entities to glitch into and through walls on chunk load. [MC-2025](https://bugs.mojang.com/browse/MC-2025)

### Misc
- **Show Total Load Time** - Displays the total game load time in the console. This lets you see how changing your game impacts load time.