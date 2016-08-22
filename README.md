# Surge
Surge is a modular performance improvement mod, which aims to increase performance and stability of the game. 

# Possibly Asked Questions
**Can I use this in my modpack?**    
Yes, this mod is intended to be used by mod packs. A link back to this page would be appreciated.

**Can I use this on a vanilla server?**    
Yes, this mod does not require the server to have it installed. This mod should work with any server. 

**Is this mod open to suggestions?**    
Yes, if you know of a way to improve game performance let us know and we can look into it. If you are a mod developer, you can also create a pull request. 

**Do I need OP or cheats to use commands added by this mod?**    
All commands added by this mod are client side, and should work regardless of permission level. You can use the `/surge` command for more info about our commands.

**This mod crashed! / This mod is incompatible with _____!**    
One of the primary goals for Surge is to provide players with a tool that is stable and compatible with their favorite mods. If you encounter an issue, please report it, and it will be looked into. 

# Features
**Animations**    
This feature adds the `/surge animation` command. If this command is executed, texture animation will be disabled. This will prevent things like water or lava from having animated textures, but it will also increase performance. Running the command again will disable the feature, allowing animations to work as they normally would. This command does **not** require OP, or cheats to be enabled.

**Hide Players**    
This feature adds the `/surge hideplayers` command. When this command is executed, only your player will be shown. This feature also adds the `/surge whitelist [add|remove] [username]` command which allows you to disable this effect for specific players. 

**Redstone Toggle Fix**    
This feature fixes a memory leak in redstone torches. A memory leak is a type of bug, where a program claims some memroy (RAM) for a specific task, but does not release it when said task is complete. The leak fixed by this feature caused information about a world to stay loaded, even when that world was not being used. 

**Load Time Analysis**    
This feature will analyse the load time of every mod installed. Once the game has started a file will be created in `.minecraft/surge/loadtimes` which contains approximate load times of every mod. While this shouldn't be used as undeniable proof that a mod is slow or broken, it can be used to spot mods which are bogging down load times. This feature is disabled by default, as it will slightly slow down load times while analyzing the load times of other mods.

**GPU Cloud Geometry**    
This feature significantly improves the performance of vanilla cloud rendering. Traditionally clouds have been very resource intensive, taking up a decent chunk of the total game render time. This feature greatly reduces that chunk of time, allowing you to play with clouds enabled, without losing performance. This feature also adds a `/surge clouds` command which allows you to toggle this special rendering off and on. Full credit to [Zaggy1024](https://github.com/Zaggy1024) for this feature.
