#!/bin/sh
set -eu

APP_NAME="BeyondDataBaseTransfer"
DESKTOP_ID="es-jklabs-BeyondDataBaseTransfer"
SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
APP_DIR="$SCRIPT_DIR"
APP_EXEC="$APP_DIR/bin/$APP_NAME"
APP_ICON="$APP_DIR/$APP_NAME.png"
APPLICATIONS_DIR="${XDG_DATA_HOME:-$HOME/.local/share}/applications"

mkdir -p "$APPLICATIONS_DIR"

cat > "$APPLICATIONS_DIR/$DESKTOP_ID.desktop" <<EOF
[Desktop Entry]
Name=$APP_NAME
Comment=Transferencia de esquemas de base de datos
Exec=$APP_EXEC
Icon=$APP_ICON
Terminal=false
Type=Application
Categories=Development;Database;Utility;
StartupWMClass=$DESKTOP_ID
StartupNotify=true
EOF

command -v update-desktop-database >/dev/null 2>&1 && update-desktop-database "$APPLICATIONS_DIR" || true

echo "Entrada de escritorio instalada en $APPLICATIONS_DIR/$DESKTOP_ID.desktop"
