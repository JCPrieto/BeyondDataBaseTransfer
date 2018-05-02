# BeyondDataBaseTransfer #

Aplicación de uso interno de la empresa BeBeyond para realizar transferencias de esquemas de base de datos entre las 
distintas máquinas de producción, preproducción y desarrollo.

El funcionamiento es el siguiente:

* Se realiza una conexión por ssh a la máquina con el servidor MySql de orígen.
* Un dump del esquema indicado es generado y descargado al equipo.
* Por SCP el dump a la máquina de destino
* Y por último se realiza la restauración del dump.

### Requisitos ###

* Java 1.8

### Ejecución ###

* Windows:
    * Ejecutar BeyondDataBaseTransfer.bat dentro del directorio bin

* Linux:
    * Ejecutar BeyondDataBaseTransfer.sh dentro del directorio bin

### Tecnologías utilizadas ###

* Iconos: Papirus https://github.com/PapirusDevelopmentTeam/papirus-icon-theme
* Librerias:
    * Sshj https://github.com/hierynomus/sshj
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