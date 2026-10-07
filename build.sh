#!/usr/bin/env bash
# Builds the mod with plain javac (no Gradle/Loom; MC 26.1 is unobfuscated).
#
#   MC_DIR=/path/to/minecraft/install MODS_DIR=/path/to/instance/mods ./build.sh
#
# MC_DIR must contain versions/26.1.2/26.1.2.jar and libraries/.
# MODS_DIR must contain litematica, malilib, backpacked and framework jars.
# JDK 25+ compiles directly; with an older JDK (21+) the class-file versions of the
# compile classpath are patched down in a temp dir first (tools/patchver.py).
set -euo pipefail
cd "$(dirname "$0")"
: "${MC_DIR:?set MC_DIR}"; : "${MODS_DIR:?set MODS_DIR}"

jars=(
  "$MC_DIR/versions/26.1.2/26.1.2.jar"
  $(find "$MC_DIR/libraries" -path '*sponge-mixin/0.17.4*' -name '*.jar')
  $(find "$MC_DIR/libraries" -path '*fastutil/8.5.18*' -name '*.jar')
  $(find "$MC_DIR/libraries" -path '*datafixerupper/9.0.19*' -name '*.jar')
  $(find "$MC_DIR/libraries" -path '*guava/33.5.0-jre*' -name 'guava-*.jar')
  $(find "$MC_DIR/libraries" -path '*brigadier/1.3.10*' -name '*.jar')
  $(find "$MC_DIR/libraries" -path '*jspecify/1.0.0*' -name '*.jar')
  $(find "$MC_DIR/libraries" -path '*slf4j-api/2.0.17*' -name '*.jar')
  "$MODS_DIR"/litematica-fabric-*.jar
  "$MODS_DIR"/malilib-fabric-*.jar
  "$MODS_DIR"/backpacked-fabric-*.jar
  "$MODS_DIR"/framework-fabric-*.jar
)

jdk=$(javac -version 2>&1 | grep '^javac' | grep -oE '[0-9]+' | head -1)
cpdir=$(mktemp -d)
out=$(mktemp -d)
trap 'rm -rf "$cpdir" "$out"' EXIT
cp=""
for j in "${jars[@]}"; do
  if [ "$jdk" -lt 25 ]; then
    python3 -I tools/patchver.py "$j" "$cpdir/$(basename "$j")"
    cp+="$cpdir/$(basename "$j"):"
  else
    cp+="$j:"
  fi
done

mkdir -p dist
javac -nowarn --release 21 -proc:none -cp "$cp" -d "$out" $(find src -name '*.java')
cp -r res/* "$out"/
name=$(basename "$PWD")
ver=$(python3 -c "import json,sys;print(json.load(open(sys.argv[1]))['version'])" res/fabric.mod.json)
(cd "$out" && jar --create --file "$OLDPWD/dist/$name-$ver.jar" .)
echo "built dist/$name-$ver.jar"
