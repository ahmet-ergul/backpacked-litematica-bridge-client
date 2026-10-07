# Backpack Litematica Bridge

Client-only Fabric mod: counts the items in your equipped
[Backpacked](https://modrinth.com/mod/backpacked) backpacks in
[Litematica](https://modrinth.com/mod/litematica)'s material list, so the
"available" column reflects what you actually carry.

Backpacked doesn't sync backpack contents to the client — the client only sees
them while the backpack screen is open. So **open your backpack (B) once per
session**; the mod snapshots the contents while it is open and keeps counting
them after you close it.

No server mod needed.

## Requirements

- Minecraft 26.1.x, Fabric Loader 0.19+
- Litematica (tested 0.27.9) + MaLiLib
- Backpacked (tested 3.0.4) + Framework

The mod mixes into Litematica internals, so a Litematica update may need small fixes.

## Install

Drop `backpack-litematica-bridge-<version>.jar` into your `mods` folder.

## Build

No Gradle: MC 26.1 is unobfuscated, so plain `javac` against the game and mod jars is enough.

```sh
MC_DIR=/path/to/minecraft/install \
MODS_DIR=/path/to/instance/mods \
./build.sh
```

`MC_DIR` must contain `versions/26.1.2/26.1.2.jar` and `libraries/`; `MODS_DIR`
must contain the Litematica, MaLiLib, Backpacked and Framework jars. The jar
lands in `dist/`. JDK 25+ is preferred; with JDK 21–24 the script patches the
class-file version of the compile classpath in a temp dir (`tools/patchver.py`).

## See also

[backpack-easyplace](https://github.com/) — pulls missing blocks out of your
backpack automatically while using Litematica's Easy Place.

## License

MIT
