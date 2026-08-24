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
- `/boss eliminar <id>` — borra el boss por completo: despawnea la entidad si está viva en el mundo,
  quita el punto de aparición y elimina el registro entero de `bosses.yml`.
- `/boss eliminarpunto <id>` — despawnea la entidad si está viva y quita **solo** el punto de
  aparición, conservando habilidades, armadura, respawn y radios ya configurados. Si lo ejecuta
  un jugador, recibe un huevo nuevo para volver a ubicarlo en otro lugar.
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
7. **Nombre, vida, daño y experiencia** — el nombre se pide en el comando; vida, daño y
   experiencia se escriben por chat.
8. Al terminar, el jugador recibe un **huevo de boss** personalizado. Clic derecho sobre un
   bloque coloca el punto de aparición.

En cualquier pantalla se puede usar "Atrás" para volver, "Cancelar" para abortar todo, o
avanzar sin elegir nada en los pasos opcionales (habilidades / armadura / encantamientos).

## Funcionamiento en el mundo

- Un scheduler revisa cada segundo los puntos de spawn: si hay un jugador dentro del radio
  de detección y el boss puede aparecer (primera vez, o pasó el intervalo de respawn), lo invoca.
- Otro scheduler revisa cada segundo los bosses vivos: dispara habilidades por cooldown
  aleatorio (dentro de su rango), por proximidad (solo si hay un jugador cerca), o al cruzar
  umbrales de vida baja (furia, huida, fase invulnerable, refuerzos), saltando el cooldown.
- Al morir, el boss da la experiencia configurada al jugador que lo mató y, si tiene respawn
  habilitado, vuelve a estar disponible tras el intervalo configurado.

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

## Notas de implementación / posibles ajustes

- "Golpe Crítico" y "Furia"/"Fase Invulnerable" están simplificados (efecto inmediato /
  un solo umbral de vida) para la primera versión; se pueden ampliar a múltiples fases (75/50/25%)
  si se quiere más profundidad.
- No se compiló localmente (el entorno de desarrollo no tiene acceso al repositorio de Paper);
  revisar con `mvn clean package` antes de desplegar.
