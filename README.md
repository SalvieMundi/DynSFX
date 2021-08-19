A fabric mod offering: realistic(-ish) Minecraft sound filters and effects.  
Forked from https://gitlab.com/binero/minecraft-dynamic-sound-filters (then optimized for better FPS + TPS)  

download [here](https://gitlab.com/mikenrafter1/mc-dyn-sfx/-/releases)

---

### Features:

- in caves/ravines/large solid-walled rooms:
1. reverb
2. (TODO: reverse reverb)
3. (TODO: echo)

- underwater:
1. muffles sounds, and quiets them

- behind walls
1. sound dampening behind walls

- when near delicate objects
1. vibration hums

### **ALL configurable!**

---

### Planned Features:

- reverse reverb  
- echo  
- vibrations  
- volume tweaks (realism for reverb and echo)

> see progress [here](https://gitlab.com/mikenrafter1/mc-dyn-sfx/-/milestones)


### Known issues:
1. inacurrate to real life ( / percieved scale relative to minecraft) reverb effect

---

### ULTRA-ACCURATE-MODE (requires compile)

> NOTE: This' only noticable when moving extremely fast-ingame.
> Leaving this off keeps your FPS and TPS running smoother.  
> If you wish to remove this ~50ms delay in effect accuracy updates, please modify:

`src/main/java/me/andre111/dynamicsf/FilterManager.java`   LN:40  
from:
```java
        update = !update;
```
to:
```java
        update = true;
```

Then open a terminal/command line:  
Linux: Ctrl+Alt+T, may be different for your distro.  
Windows: Windows key (super/meta) + R, type cmd, Enter.  
Mac: Command+Space, type in terminal, Enter.  

now type:
```sh
cd path/into/wherever/you/downloaded/or/cloned/this/repo/to

# for Linux & Mac
./gradlew licenseFormat build

# for Windows
gradlew.bat licenseFormat build
```
Assuming you built it correctly, your `.jar` file will be in `build/lib/`  
It'll be the one without `-dev` and without `-source`  
Enjoy!

---

### contributing?
YES! PLEASE! Send a pull request or open an issue, all feedback is welcome.
