A fabric mod offering: realistic(-ish) Minecraft sound filters and effects.
Forked from https://gitlab.com/binero/minecraft-dynamic-sound-filters (then optimized for better FPS + TPS)

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

### **ALL configurable!**

---

> Any help with optimization work regarding reverb, and implementation of echo and reverse reverb, would be appreciated.

---

### Known issues:
1. lag in nether, this' due to the reverb method being imperformant, I've some ideas as to how to fix
2. inacurrate to real life ( / percieved scale relative to minecraft) reverb effect  
These two may be fixed in the next release

3. no echo
4. no reverse reverb  
These may take longer.

---

### Known non-issues:
1. delay for applying effects
> NOTE: This' only noticable when moving extremely fast-ingame.
> This keeps your FPS and TPS running smoother.  
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
Linux: Ctrl+Shift+T, may be different for your distro.  
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
