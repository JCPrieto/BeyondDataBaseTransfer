# Changelog

* 1.5.0

    * Sustitucion del campo manual de esquema por un selector que consulta los esquemas disponibles en el servidor de
      origen mediante el cliente `mysql`.
    * Recarga asincrona del selector de esquemas al cambiar el servidor de origen, limpiando la seleccion anterior y
      bloqueando la interfaz mientras finaliza la consulta.
    * Notificaciones nativas mediante Two-Slices y D-Bus, eliminando el uso de Systray y `notify-send`, con dialogos
      Swing como fallback.
    * Mejoras de integracion en Linux/GNOME para asociar el icono de la aplicacion en el dock, incluyendo `WM_CLASS`,
      icono de Taskbar y script de registro de lanzador en la distribucion.
    * Inicializacion de la interfaz en el EDT de Swing y carga centralizada de iconos mediante `IconUtils`.
    * Actualizacion del dialogo "Acerca de" con licencia GPL-3.0 y listado de librerias actualizado.
    * Configuracion de JaCoCo para generar cobertura XML usada por SonarQube en el workflow de release.

* 1.4.0

    * Mejora del proceso de copia de esquemas usando `ProcessBuilder`, argumentos separados, validacion del nombre del
      esquema y control del codigo de salida de `mysql` y `mysqldump`.
    * Uso de archivos temporales seguros para los backups intermedios y limpieza garantizada al finalizar la copia.
    * Nuevo cifrado de contrasenas con AES-GCM, IV aleatorio, formato versionado y compatibilidad con credenciales
      cifradas por versiones anteriores.
    * Comprobacion de nuevas versiones en segundo plano para evitar bloqueos de la interfaz al iniciar la aplicacion.
    * Logs en ubicacion fija por sistema operativo, rotacion simple y eliminacion de logs vacios.
    * Fallback de notificaciones mediante dialogos Swing cuando no estan disponibles Systray o `notify-send`.
    * Aislamiento de configuracion y logs durante los tests para evitar modificaciones en datos reales del usuario.
    * Nuevos tests unitarios para copia de esquemas, cifrado, configuracion y rutas de logs.

* 1.3.6

    * Correccion al eliminar servidores y carpetas de sistema desde la configuracion para mantener sincronizados el
      arbol y
      la configuracion persistida.
    * Correccion de la importacion de configuraciones de servidores para copiar carpetas anidadas con nuevos
      identificadores.
    * Compatibilidad con los ejecutables `mysql.exe` y `mysqldump.exe` en Windows.
    * Actualizacion de dependencias de Apache Commons y configuracion de SonarQube en el workflow de release.
    * Nuevos tests unitarios para la eliminacion de nodos de configuracion y la importacion de servidores.

* 1.3.5

    * Actualizacion de la configuracion de Gradle para versiones modernas.
    * Actualizacion del dialogo "Acerca de" con URL y licencia actualizadas.
    * Compilación de la aplicación con Java 21
    * API de Github Releases para la detección y descarga de nuevas versiones.

* 1.3.4

    * Correciones de seguridad y estabilidad
    * Nueva URL de la web

* 1.3.3

    * Correciones de seguridad y estabilidad

* 1.3.2

    * Actualización de seguridad de despendencias.

* 1.3.1

    * Actualización de seguridad de la librería de Jackson

* 1.3.0

    * Migracion a Java 11.
    * Eliminamos ControlFX por problemas de compatibilidad con OpenJDK 11 y en su lugar utilizamos Systray en S.O
      Windows
      y LibNotofy en S.O Linux.

* 1.2.1:

    * Correción a la hora de importar un archivo de configuracion de servidores anterior a la version 1.0.0.

* 1.2.0:

    * Se elimina el icono de Systray, por la incompatibilidad con Gnome3 y utilizamos ControlsFX para monstrar las
      notificaciones.

* 1.1.0:

    * Se añade la opción de limpiar el esquema en la máquina de destino antes de restaurar.

* 1.0.1:

    * Controlar y mostrar mensaje con los errores de los comandos mysqldump y mysql.
    * Se añade el parametro "--max_allowed_packet=2048M" para evitar que se corte la conexion con la BBDD a la hora de
      realizar el dump.

* 1.0.0:

    * El copiado de esquemas a partir de ahora se realiza utilizando un cliente de Mysql ejecutandose en el equipo
      local.

* 0.3.0:

    * Optimización a la hora de realizar los backup
    * Corrección en el campo del puerto en el formulario de configuración del servidor.
    * Corrección en el formulario de configuración del servidor a la hora de refrescar los elementos.

* 0.2.9:

    * Alineacion correcta del botón de descarga de la nueva versión a la derecha del todo de la ventana.

* 0.2.8:

    * En el menú de configuracion: Al seleccionar un servidor, mostrar su configuración.
