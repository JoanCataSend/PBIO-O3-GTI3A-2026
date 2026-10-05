# Preparación para revisión automatizada de Sprint

Este documento resume cómo la entrega satisface la estructura exigida por el agente revisor, sin sustituir a las especificaciones canónicas de `doc/`.

## Correspondencia estructural

```text
author.md                         -> autor de la entrega

doc/firmware_design.md           -> src/firmware/
doc/android_design.md            -> src/android/
doc/business_logic_design.md     -> src/business_logic/
doc/database_design.md           -> src/database/
doc/api_rest_design.md           -> src/api_rest/
doc/web_design.md                -> src/web/
```

Cada `xxx_design.md` incluye explícitamente:

1. `Component Design (Diseño del Componente)`;
2. `Design Clarifications (Aclaraciones del Diseño)`;
3. `General Rules (Reglas Generales)`.

En `General Rules` se declaran el lenguaje objetivo, la regla de cabeceras lógicas con delimitadores `--------------------`, la legibilidad y las pruebas automatizadas.

## Correspondencia diseño-código

La carpeta `src/` es una vista canónica para revisión. El código operativo se conserva además en las rutas requeridas por Arduino, Gradle y Plesk. `scripts/audit-agent-ready.py` comprueba byte a byte que ambas vistas coinciden para todos los archivos operativos relevantes.

## Seguridad de la entrega

No se distribuyen secretos locales. En particular no se incluyen:

```text
server/SDBaseDatos.php
android/local.properties
.env
```

Solo se entrega `server/SDBaseDatos.example.php` con la estructura de configuración y sin credenciales privadas.

## Comprobaciones automáticas añadidas

- Auditoría estructural `AGENTS.md`: 201/201 comprobaciones correctas en la generación de esta entrega.
- Lógica de negocio PHP: 10/10 tests unitarios correctos.
- Firmware: test de contrato de IDs, valores fake y codificación Major.
- Base de datos: test de contrato del esquema, claves, restricciones, índices y seed.
- Web: test unitario sobre las funciones reales `fechaSql()` y `escapar()`.
- Android: el proyecto conserva sus tests JUnit/instrumentados y la versión operativa fue validada con Gradle/JDK 17 antes de preparar esta vista de revisión; los cambios posteriores en código Android se limitan a comentarios de diseño lógico.
- API: se conserva el test de integración HTTPS del endpoint real.
