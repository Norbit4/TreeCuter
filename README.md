<div align="center">
  
  <a href="https://github.com/Norbit4/TreeCuter/" target="_blank" rel="noreferrer"> 
  <img src="https://github.com/Norbit4/TreeCuter/assets/46154743/90006b9b-da83-4dd6-b4ef-56c0b336c97c" width=950" alt="logo"/></a>

  [![Spigot](https://img.shields.io/badge/Download-Spigot-gold.svg)](https://www.spigotmc.org/resources/treecuter.110213/) 
  [![builtbybit](https://img.shields.io/badge/Download-BuiltByBit-blue.svg)](https://builtbybit.com/resources/treecuter-cut-down-trees.32962/)                                                                                                          
  [![License: GPL v3](https://img.shields.io/badge/license-GPLv3-orange.svg)](https://github.com/Norbit4/TreeCuter/blob/master/LICENSE)                                                                                                                

  <h6 align="center">Used Languages and Tools:</h6>
  
  <p align="center">
  <a href="https://skillicons.dev">
    <img src="https://skillicons.dev/icons?i=java,idea"/>
  </a>    
 </p>
</p>
</div> 

<h1 align="left"><b>TreeCuter</b></h1>

<p align="left">

<div align="center">    
                   
  **Cut down trees in a more realistic way! 🪓🌳**    
                  

  <a href="https://github.com/Norbit4/TreeCuter/" target="_blank" rel="noreferrer"> 
  <img src="https://github.com/Norbit4/TreeCuter/assets/46154743/8f97482e-d239-427c-8398-c61ba2c815ed" width=50" alt="logo"/></a>
                                                                                                                
  ⚠️**DEAFULT**: You need use **SHIFT** key to cut tree!
</div> 
 

<h2 align="left" id="content">Table of contents</h2>

- [Features](#features)
- [Commands](#commands)
- [Addons](#addons)
- [Glowing](#glowing)
- [API](#api)

<h2 align="left" id="features">Features</h2>

> [!note]
> - More **efficient** tree destruction
> - **Glowing** cut tree
> - Support custom trees
> - Support **Jobs** plugin (**need:** [TreeCuterJobs](https://www.spigotmc.org/resources/%E2%9C%A8treecuterjobs%E2%9C%A8-addon-%E2%9C%85.113357/))
> - Destroys tools
> - Auto plant trees
> - Permissions
> - **Worlds** blacklist!
> - **Min/max** blocks to cut down tree
> - **Full Configurable**
> - Worldguard regions support!
> - **On/off** player command!
> - **Custom cut tool!** [(Support ItemsAdder)](https://www.spigotmc.org/resources/%E2%9C%A8itemsadder%E2%AD%90emotes-mobs-items-armors-hud-gui-emojis-blocks-wings-hats-liquids.73355/)
> - Developer API

  
<h2 align="left" id="commands">Commands</h2>

Command | Permission | Info | 
--- | --- | --- |
**/treecuter** | *treecuter.help* | Help  |
**/treecuter reload** | *treecuter.reload* | **Reload** config.yml  |
**/treecuter get** | *treecuter.get* | Get **custom** tool  |
**/treecuter toggle** | *treecuter.toggle* | **On/off** cut trees  |

  
<h2 align="left" id="addons">Addons</h2>

> [!TIP]
> *How to install addon?*

Put the **plugin** in the plugins folder with the treecuter core. **That's all!**
<br><br>
**1.TreeCuterJobs** - [Download](https://www.spigotmc.org/resources/%E2%9C%A8treecuterjobs%E2%9C%A8-addon-%E2%9C%85.113357/)


<h2 align="left" id="glowing">Glowing</h2>                                                                                                                        

> [!WARNING]
> *Glow supports only paper or forks.*

versions: **1.17.1, 1.18.2, 1.19.4, 1.20.2, 1.20.4, 1,20.6, 1.21!**        

<h2 align="left" id="api">API</h2>     

**TreeCutEvent**
```java
  public class TreeCutListener implements Listener {
  
      @EventHandler
      public void onTreeCut(TreeCutEvent e) {
          //player triggered event
          Player p = e.getPlayer();
  
          //block to cut
          List<Block> blocks = e.getBlocks();
  
          //can be canceled
          e.setCancelled(true);
      }
  } 
```

**TreeGlowEvent**
```java
  public class TreeGlowListener implements Listener {
  
      @EventHandler
      public void onTreeCut(TreeGlowEvent e) {
          //player triggered event
          Player p = e.getPlayer();
  
          //block to glow
          List<Block> blocks = e.getBlocks();
  
          //can be canceled
          e.setCancelled(true);
      }
  }
```

> [!IMPORTANT]
> Don't forget to register listeners!

```java
  @Override
  public void onEnable() {
      
      getServer().getPluginManager().registerEvents(new TreeGlowListener(), this);
      getServer().getPluginManager().registerEvents(new TreeCutListener(), this);
  }
```


<h2 align="left" id="download">Download</h2>
 
 [![spigot](https://img.shields.io/badge/Download-Spigot-gold.svg)](https://www.spigotmc.org/resources/treecuter.110213/)    
 [![builtbybit](https://img.shields.io/badge/Download-BuiltByBit-blue.svg)](https://builtbybit.com/resources/treecuter-cut-down-trees.32962/)    
