# BossPersonalizados

Plugin para Paper 1.21.11 (Java 21) que permite crear bosses totalmente personalizados
mediante inventarios (GUIs), sin tocar código: mob, habilidades, armadura, encantamientos,
respawn y radios de detección/ataque.

## Comandos

- `/boss crear <nombre>` — abre el wizard empezando por la selección de mob (huevos).
- `/boss crear <nombre> <TIPO_ENTIDAD>` — salta la pantalla de mobs. **Obligatorio** para
  `ENDER_DRAGON` y `WITHER`, que no tienen huevo de spawn. También sirve para elegir cualquier
  otro mob por nombre si preferís no usar la GUI.
- `/boss lista` — muestra todos los bosses creados con su ID, mob y estado (vivo / muerto / sin ubicar).
- `/boss editar <id>` — reabre el wizard completo sobre un boss ya creado, con todos sus valores
  actuales precargados en cada pantalla (habilidades, armadura, respawn, radios, gracia, recompensas,
  etc). Se edita sobre una **copia**: si cancelás a mitad de camino, el boss original queda intacto;
  los cambios solo se guardan al llegar al final del wizard. No pide un huevo nuevo ni toca el
  punto de aparición ya colocado.
- `/boss editar <id> <TIPO_ENTIDAD>` — igual que arriba pero saltando la pantalla de mob (útil para
  cambiar el tipo de mob directamente, o para Ender Dragon/Wither).
- `/boss eliminar <id>` — borra el boss por completo: despawnea la entidad si está viva en el mundo,
  quita el punto de aparición y elimina el registro entero de `bosses.yml`.
- `/boss eliminarpunto <id>` — despawnea la entidad si está viva y quita **solo** el punto de
  aparición, conservando habilidades, armadura, respawn y radios ya configurados. Si lo ejecuta
  un jugador, recibe un huevo nuevo para volver a ubicarlo en otro lugar.
- `/boss huevo <id>` — vuelve a entregar el huevo de spawn de un boss que ya existe pero **todavía
  no tiene punto de aparición ubicado** (por ejemplo, si se perdió, se dropeó por accidente, o
  el jugador cerró sesión sin colocarlo). No borra ni recrea el boss, solo genera un huevo nuevo
  para el mismo ID. Si el boss ya tiene un punto ubicado, el comando lo rechaza y sugiere usar
  `/boss eliminarpunto <id>` primero.
- `/boss cancelar` — cancela el wizard en curso.

Permiso: `bosspersonalizados.admin` (default: op).

## Flujo de creación (wizard)

1. **Mob** — huevos de spawn de mobs hostiles. Ender Dragon / Wither vía comando.
2. **Habilidades** *(opcional, se puede saltar con "Siguiente")* — hasta 30 habilidades,
   selección múltiple con click.
3. **Armadura y arma** *(opcional)* — por cada pieza (casco/pecho/piernas/botas) click
   izquierdo/derecho para ciclar: Ninguna → Cuero → Hierro → Oro → Diamante → Netherite.
   El arma (espada/hacha) se elige aparte, con su propio material.
4. **Encantamientos** *(solo si hay armadura/arma equipada)* — por cada pieza, click
   izquierdo sube nivel, click derecho baja nivel.
5. **Respawn** — una sola vez, o con respawn cada X minutos (+1/+5/+10, -1/-5/-10).
6. **Radios** — radio de detección (a qué distancia aparece cuando se acerca un jugador) y
   radio de ataque/persecución (+5/-5 bloques).
7. **Tiempo de gracia** — segundos desde que se coloca el huevo hasta que el boss puede aparecer
   por primera vez, para que el staff que lo colocó tenga tiempo de alejarse. Empieza en 30
   segundos; botones de +30s/+1min/+5min y -30s/-1min/-5min. En 0 aparece de inmediato.
8. **Recompensa en monedas** — arranca en 500, se suma/resta de 50 en 50. Se entrega al
   jugador que mata al boss (requiere Vault + un plugin de economía; si no están instalados,
   el plugin sigue funcionando pero no reparte monedas).
9. **Recompensa en ítem** *(opcional)* — colocá el ítem que querés que suelte el boss al morir
   en el **primer slot de tu hotbar** (el más a la izquierda) mientras esta pantalla está
   abierta, y tocá "Confirmar". Si hay más de 1 unidad en ese slot, se toma el stack completo.
   "Saltar" si no querés recompensa en ítem.
10. **Nombre, vida, daño y experiencia** — el nombre se pide en el comando; vida, daño y
    experiencia se escriben por chat.
11. Al terminar, el jugador recibe un **huevo de boss** personalizado (solo al crear; al editar
    no se entrega huevo nuevo). Clic derecho sobre un bloque coloca el punto de aparición y
    arranca el tiempo de gracia.

En cualquier pantalla se puede usar "Atrás" para volver, "Cancelar" para abortar todo, o
avanzar sin elegir nada en los pasos opcionales (habilidades / armadura / encantamientos / ítem).

## Funcionamiento en el mundo

- Un scheduler revisa cada segundo los puntos de spawn: si hay un jugador dentro del radio
  de detección, ya pasó el tiempo de gracia, y el boss puede aparecer (primera vez, o pasó el
  intervalo de respawn), lo invoca.
- Otro scheduler revisa cada segundo los bosses vivos: dispara habilidades por cooldown
  aleatorio (dentro de su rango), por proximidad (solo si hay un jugador cerca), o al cruzar
  umbrales de vida baja (furia, huida, fase invulnerable, refuerzos), saltando el cooldown.
- Al morir, el boss:
  - da la experiencia configurada al jugador que lo mató;
  - le deposita la recompensa en monedas (vía Vault, si está disponible);
  - suelta la recompensa en ítem en el suelo, si se configuró una;
  - si tiene respawn habilitado, vuelve a estar disponible tras el intervalo configurado;
  - si es de **"una sola vez"**, el punto de aparición se borra automáticamente (igual que
    `/boss eliminarpunto`) y no vuelve a aparecer hasta que se le coloque un huevo de nuevo.

## Persistencia

Todo se guarda en `bosses.yml` dentro de la carpeta del plugin, con ID numérico autoincremental
por boss (nombre, mob, habilidades, equipo + encantamientos, configuración de respawn y radios,
y la ubicación del punto de spawn una vez colocado el huevo).

## Las 30 habilidades

**Ofensivas:** Golpe Pesado, Embestida, Lluvia de Flechas, Onda de Choque, Golpe Crítico,
Veneno en el Golpe, Debilitar, Lentitud en Área, Explosión Controlada, Rayo.

**Defensivas:** Escudo Temporal, Regeneración, Inmunidad 2s, Reflejo de Daño, Huida,
Absorción, Furia, Fase Invulnerable.

**Invocación:** Crear Súbditos, Invocar Oleada, Clon Fantasma, Refuerzos por Fase, Torreta,
Familiar Guardián.

**Control/Utilidad:** Empuje en Área, Ceguera, Confusión, Salto Repentino, Grito de Alarma,
Marca de Objetivo.

## Economía (Vault)

La recompensa en monedas requiere **Vault** + un plugin de economía (Essentials, CMI, etc.)
instalados en el servidor. Está declarado como `softdepend` en `plugin.yml`, así que si no
están presentes el plugin arranca igual: solo se salta el depósito de monedas (se loguea un
aviso una única vez) sin afectar el resto de las funciones.

## Notas de implementación / posibles ajustes

- "Golpe Crítico" y "Furia"/"Fase Invulnerable" están simplificados (efecto inmediato /
  un solo umbral de vida) para la primera versión; se pueden ampliar a múltiples fases (75/50/25%)
  si se quiere más profundidad.
- No se compiló localmente (el entorno de desarrollo no tiene acceso al repositorio de Paper);
  revisar con `mvn clean package` antes de desplegar.
