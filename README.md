# BeyondDataBaseTransfer #

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=JCPrieto_BeyondDataBaseTransfer&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=JCPrieto_BeyondDataBaseTransfer)

Aplicación de uso interno de la empresa BeBeyond para realizar transferencias de esquemas de base de datos entre las 
distintas máquinas de producción, preproducción y desarrollo.

El funcionamiento es el siguiente:

* Se genera un dump del esquema indicado utilizando el comando mysqldump del cliente Mysql instalado en el equipo.
Para el dump se utilizan los parametros: '--max_allowed_packet=2048M --quick --single-transaction --events --routines
 --triggers'
* Se restaura el dump en la maquina remota mediante el comando mysql del cliente Mysql instalado en el equipo.

### Requisitos ###

* Java 21
* Cliente Mysql
* LibNotify (Para las notificaciones en Linux)

### Ejecución ###

* Windows:
    * Ejecutar BeyondDataBaseTransfer.bat dentro del directorio bin

* Linux:
    * Ejecutar BeyondDataBaseTransfer.sh dentro del directorio bin

### Tecnologías utilizadas ###

* Iconos: Papirus https://github.com/PapirusDevelopmentTeam/papirus-icon-theme
* Librerias:
    * Jackson https://github.com/FasterXML/jackson-core/wiki
    * Apache Commons https://commons.apache.org
    * SwingX 
  * GitHub Releases API https://docs.github.com/en/rest/releases/releases
    
### ToDo ###

* Crear los esquemas si no existen en la máquina de destino
* Crear solamente un backup, sin necesidad de transferirlo a ninguna máquina.
* Buscar los esquemas disponibles en el propio servidor de orígen.
* Permitir marcar un servidor como sólo de orígen de datos, nunca como destino.
* Eliminar logs vacíos
* Clonar esquemas en una misma máquina
* Mostrar un texto descriptivo en la barra de progreso

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
