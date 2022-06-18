# Mine.py

A python scripting plugin for bukkit!

## Commands

### `/pyreload`
Execute `end()` on loaded scripts, and reload scripts on `plugins/minepy`!
#### Required Permission: `py.reload`

## Mine.py API

### MPy

#### Methods

##### `getPlugin(): JavaPlugin`

Get MinePyPlugin's main instance

###### Examples
```python
from minepy import MPy
plugin = MPy.getPlugin() # <--
```

### Events

#### Methods

##### `registerListener(eventClazz: Class, priority: EventPriority, ignoreCancelled: bool, listener: Function): Listener`

Register event's listener! Must be call `unregisterListener` with returned value in this function!

###### Examples
```python
from minepy import Events
from minepy import Events
from java.lang import Class

def onMove(event):
    event.player.sendMessage('Moved!')

PlayerMoveEvent = Class.forName('org.bukkit.event.player.PlayerMoveEvent')
listenerMove = Events.registerListener(PlayerMoveEvent, EventPriority.NORMAL, False, onMove) # <--

def end():
    Events.unregisterListener(PlayerMoveEvent, listenerMove)
    print('End')
```

##### `unregisterListener(eventClazz: Class, listener: Listener)`

Unregister listener with event(eventClazz)!

###### Examples
```python
from minepy import Events
from minepy import Events
from java.lang import Class

def onMove(event):
    event.player.sendMessage('Moved!')

PlayerMoveEvent = Class.forName('org.bukkit.event.player.PlayerMoveEvent')
listenerMove = Events.registerListener(PlayerMoveEvent, EventPriority.NORMAL, False, onMove)

def end():
    Events.unregisterListener(PlayerMoveEvent, listenerMove) # <---
    print('End')
```

## Example Codes

### Importing java classes
```python
from org.bukkit import Bukkit

Bukkit.broadcastMessage('Test')
```

### Registering event handlers
```python
from minepy import Events
from java.lang import Class

def onMove(event):
    event.player.sendMessage('Moved!')

PlayerMoveEvent = Class.forName('org.bukkit.event.player.PlayerMoveEvent')
listenerMove = Events.registerListener(PlayerMoveEvent, EventPriority.NORMAL, False, onMove)

def end():
    Events.unregisterListener(PlayerMoveEvent, listenerMove)
    print('End')
```

## Example Code
```python
from java.lang import Class
from org.bukkit.event import EventPriority
from minepy import Events
from minepy import MPy
from org.bukkit.potion import PotionEffect
from org.bukkit.potion import PotionEffectType
from org.bukkit import Bukkit

def onChat(event):
    def pot():
        pos = event.player.getLocation().add(0.0, 1.0, 0.0)
        event.player.teleport(pos)
        event.player.addPotionEffect(PotionEffect(PotionEffectType.SPEED, 20 * 5, 5, False, False))
    Bukkit.getScheduler().scheduleSyncDelayedTask(MPy.getPlugin(), pot)

AsyncChatEvent = Class.forName('io.papermc.paper.event.player.AsyncChatEvent')
listenerChat = Events.registerListener(AsyncChatEvent, EventPriority.NORMAL, False, onChat)

def end():
    Events.unregisterListener(AsyncChatEvent, listenerChat)
    print("Ending!")
```