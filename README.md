# BeyondDataBaseTransfer #

Aplicación de uso interno de la empresa BeBeyond para realizar transferencias de esquemas de base de datos entre las 
distintas máquinas de producción, preproducción y desarrollo.

El funcionamiento es el siguiente:

* Se genera un dump del esquema indicado utilizando el comando mysqldump del cliente Mysql instalado en el equipo.
Para el dump se utilizan los parametros: '--quick --single-transaction --events --routines --triggers'
* Se restaura el dump en la maquina remota mediante el comando mysql del cliente Mysql instalado en el equipo.

### Requisitos ###

* Java 1.8
* Cliente Mysql

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
    * Firebase https://firebase.google.com
    
### ToDo ###

* Crear los esquemas si no existen en la máquina de destino
* Limpiar el esquema en la máquina de destino antes de restaurar.
* Crear solamente un backup, sin necesidad de transferirlo a ninguna máquina.
* Buscar los esquemas disponibles en el propio servidor de orígen.
* Permitir marcar un servidor como sólo de orígen de datos, nunca como destino.
* Eliminar logs vacios
* Clonar esquemas en una misma máquina
* Mostras un texto descriptivo en la barra de progreso

### Changelog ###

1.0.1:

* Controlar y mostrar mensaje con los errores de los comandos mysqldump y mysql.

1.0.0:

* El copiado de esquemas a partir de ahora se realiza utilizando un cliente de Mysql ejecutandose en el equipo local.

0.3.0:

* Optimización a la hora de realizar los backup
* Corrección en el campo del puerto en el formulario de configuración del servidor.
* Corrección en el formulario de configuración del servidor a la hora de refrescar los elementos.

0.2.9:

* Alineacion correcta del botón de descarga de la nueva versión a la derecha del todo de la ventana.

0.2.8: 

* En el menú de configuracion: Al seleccionar un servidor, mostrar su configuración.

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