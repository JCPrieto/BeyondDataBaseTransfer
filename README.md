# BeyondDataBaseTransfer #

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=JCPrieto_BeyondDataBaseTransfer&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=JCPrieto_BeyondDataBaseTransfer)

Aplicación de uso interno de la empresa BeBeyond para realizar transferencias de esquemas de base de datos entre las 
distintas máquinas de producción, preproducción y desarrollo.

La aplicacion permite:

* Copiar un esquema desde un servidor origen a un servidor destino. Si el esquema no existe en destino, se crea antes
  de restaurar el backup.
* Crear un backup SQL local de un esquema del servidor origen.
* Restaurar un backup SQL local en un servidor destino.
* Clonar un esquema dentro del mismo servidor usando un nuevo nombre de esquema.
* Marcar servidores como "Solo origen" para evitar que aparezcan como posibles destinos.

Para las operaciones de backup se utiliza `mysqldump` del cliente Mysql instalado en el equipo con los parametros:
`--max_allowed_packet=2048M --quick --single-transaction --routines --triggers`. La restauracion se realiza mediante
el comando `mysql` del mismo cliente.

### Requisitos ###

* Java 21
* Cliente Mysql
* D-Bus disponible en Linux para notificaciones nativas. Si no esta disponible, la aplicacion usa dialogos Swing como
  fallback.

### Ejecución ###

* Windows:
    * Ejecutar BeyondDataBaseTransfer.bat dentro del directorio bin

* Linux:
    * Ejecutar BeyondDataBaseTransfer.sh dentro del directorio bin
  * Opcionalmente, ejecutar `sh ./install-linux-desktop-entry.sh` desde el directorio descomprimido para registrar el
    lanzador en GNOME y asociar correctamente el icono del dock.

### Configuracion y logs ###

* La configuracion de usuario se guarda en `~/.BeyondDataBaseTransfer/config.json` por defecto.
* Los logs se escriben en una carpeta fija segun el sistema operativo:
  * Windows: `%LOCALAPPDATA%/BeyondDataBaseTransfer/logs`
  * macOS: `~/Library/Application Support/BeyondDataBaseTransfer/logs`
  * Linux: `~/.local/share/BeyondDataBaseTransfer/logs`
* Los logs tienen rotacion simple por tamano y se eliminan los archivos vacios al iniciar.

### Tecnologías utilizadas ###

* Iconos: Papirus https://github.com/PapirusDevelopmentTeam/papirus-icon-theme
* Librerias:
    * Jackson https://github.com/FasterXML/jackson-core/wiki
    * Apache Commons https://commons.apache.org
    * SwingX 
  * Two-Slices https://github.com/sshtools/two-slices
  * D-Bus Java https://github.com/hypfvieh/dbus-java
  * GitHub Releases API https://docs.github.com/en/rest/releases/releases
    
### Changelog ###

Consulta el historial de cambios en [CHANGELOG.md](CHANGELOG.md).

### Licencia ### 

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
