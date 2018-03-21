# BeyondDataBaseTransfer #

Aplicación de uso interno de la empresa BeBeyond para realizar transferencias de esquemas de base de datos entre las 
distintas máquinas de producción, preproducción y desarrollo.

El funcionamiento es el siguiente:
* Se realiza una conexión por ssh a la máquina con el servidor MySql de orígen.
* Un dump del esquema indicado es generado y descargado al equipo.
* Por SCP el dump a la máquina de destino
* Y por último se realiza la restauración del dump.

### Tecnologías utilizadas ###

* Iconos: Papirus https://github.com/PapirusDevelopmentTeam/papirus-icon-theme
* Librerias:
    * Sshj https://github.com/hierynomus/sshj
    * Jackson https://github.com/FasterXML/jackson-core/wiki
    * Apache Commons https://commons.apache.org
    * SwingX 
    * Firebase https://firebase.google.com
    
### ToDo ###

* En el menú de configuracion: Al seleccionar un servidor, mostrar su configuración.
* Crear los esquemas si no existen en la máquina de destino
* Limpiar el esquema en la máquina de destino antes de restaurar.
* Crear solamente un backup, sin necesidad de transferirlo a ninguna máquina.
* Buscar los esquemas disponibles en el propio servidor de orígen.
* Permitir marcar un servidor como sólo de orígen de datos, nunca como destino.
* Eliminar logs vacios
* Clonar esquemas en una misma máquina
* Mostras un texto descriptivo en la barra de progreso

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