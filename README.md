# PROGRESO V2
✔️ - Completado  
⏳ - En proceso  
🕙 - Pendiente  
  
`<>` - Campo obligatorio  
`[]` - Campo opcional  
`a|b` - Expresión A o B
## Discord
* ✔️ `/link [código]` (MC y Discord)
* ✔️ `/unlink` (MC y Discord)
* ✔️ `/city`
* ✔️ `/scoreboard`
## Utilidades
* ✔️ `/height`
* ✔️ `/googlemaps`
* ✔️ `/tour [ciudad]`
* ✔️ `/banners <colorLetra> <colorFondo> <texto>`
* ✔️ `/get`
* ⏳ `/help [comando]`
* ✔️ `/scoreboard [<tipo>|auto]`
* 🕙 `/streaming <link>`
* ✔️ `/prefix`
* ✔️ `/nightvision`
* 🕙 `/lobby`
* ✔️ `/tpdir <nombre>`  
  **Mejoras:**
  * Menú de selección si se encuentran múltiples opciones. 
### PWarps
* ✔️ `/pwarp set <nombre>`
* ✔️ `/pwarp delete <nombre>`
* ✔️ `/pwarp list`
* ✔️ `/pwarp <nombre>`
## WorldEdit
* ✔️ Atajos
* ✔️ `//polywalls <patrón> [-l|-last]`
  **Cambios:**
  * Se agregó la *flag* `-l` o `-last` que evita que se conecten el último punto con el primero.
* ✔️ `//terraform (<patrón>|border|height|desel)`  
**Cambios:**
  * El borde ahora se selecciona con `poly`.
  * Los bordes ya no representan puntos de altura.
  * Todos los puntos de altura ahora deben ser seleccionados con `convex` y el subcomando `height`.
* ✔️ `//selundo`
* ✔️ `//selredo`
* ✔️ `//divide <cantidad>`
* ✔️ `/incremento <cantidad>` 
### Presets
* ✔️ Presets  
**Cambios:**
  * El preset ahora se usa como `$preset$`. Ahora se puede utilizar el preset dentro de más texto sin ningún problema.
    * Ejemplo: `//set 1,1:5,$calles$,252:8`
* ✔️ `/presets set <nombre> <texto>`
* ✔️ `/presets delete <nombre>`
* ✔️ `/presets list`
### Assets
* ✔️ Pegado
* ✔️ Rotación
* ✔️ Marcado de favoritos
* ✔️ `/asset create <nombre> [-ar]`
* ✔️ `/asset setOrigin`
* ✔️ `/asset editName <id> <nuevoNombre>`
* ✔️ `/asset editAutoRotate <id> <(true|false)>`
* ✔️ `/asset settags <id> [etiquetas]`
* ✔️ `/asset delete <id>`
* ✔️ `/asset search [texto]`
* ✔️ `/asset fav`
* ✔️ `/assetgroup create <nombre>`
* ✔️ `/assetgroup delete <nombre>`
* ✔️ `/assetgroup list [nombre]`
* ✔️ `//assetfill <(nombreGrupo|ID)> <distanciaMínima>`  
**Comando nuevo:**
  * Cubre un área poligonal con los _assets_ de un grupo de _assets_ o un _asset_ en específico.
  * Los assets no se pegarán a menos de la distancia mínima especificada uno del otro.
  * Si se usa ID, esta debe corresponder si o si a un _asset_ que tenga rotación automática.
  * Los _assets_ se pegan siempre en el bloque más alto, siempre y cuando esta altura esté contenida dentro de la selección principal.
  * Si se aplica una máscara global, esta determinará **sobre** qué bloques se pegarán _assets_.
    * Ejemplo: `//gmask 2` solo permitirá _assets_ **sobre** bloques de pasto.
## Administración
* 🕙 `/donador <nombre>`
* 🕙 `/streamer <nombre>`
* ✔️ `/deletePlayerData <uuid>`  
**Nuevo:**
  * Elimina el archivo de `playerdata` de un jugador. Solo ejecutable en la consola.
### Ciudades
* ✔️ `/city create <país> <nombre> <nombreDisplay>`
* ✔️ `/city setDisplay <nombre> <nuevoNombreDisplay>`
* ✔️ `/city setUrban <nombre>`
* ✔️ `/city deleteUrban <nombre>`
* ✔️ `/city redefine <nombre>`
* 🕙 `/city delete <nombre>`
## Proyectos
* ✔️ *Action Bar*
* ✔️ Publicaciones
* ✔️ `/project create [<tipo> <puntos>]`
* ✔️ `/project delete`
* ✔️ `/project name [nombre]`
* ✔️ `/project borders`
* ✔️ `/project manage`
* ✔️ `/project tag`
* ✔️ `/project claim`
* ✔️ `/project find`
* ✔️ `/project review`
* ✔️ `/project request`
* ✔️ `/project progress`  
* ✔️ `/project redefine`  
**Subcomando nuevo:**
  * Muestra tu progreso en los tipos de proyecto. Cuáles has desbloqueado y cuáles no.
* 🕙 `/project tutorial`
## Chat
* ✔️ `/chat [nombre]`
* ✔️ `/chat set`
* ✔️ `/chat default [set]`
* ✔️ `/chat invite <nombre>`
* ✔️ `/chat <código>`
* ✔️ `/nickname <nickname>`