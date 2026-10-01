# Checklist de la rúbrica · Sprint 0

Este documento sirve como guía de defensa y localiza la evidencia dentro del repositorio.

## 1. Buenas prácticas · 15 %

### Repositorio Git y ramas

Ramas utilizadas:

```text
main
master
develop
sensor-real-final
```

- `main`: Sprint 0 reproducible.
- `master`: versión estable del Sprint 0.
- `develop`: rama de desarrollo.
- `sensor-real-final`: evolución posterior con sensor físico.

### README

`README.md` incluye:

- descripción del proyecto;
- arquitectura;
- despliegue;
- ejecución de tests;
- test manual;
- criterio de aceptación;
- ramas y estructura.

### Comentarios de código

Los ficheros fuente principales incorporan cabecera con:

```text
nombre
descripción
copyright
fecha
autor
aportación
```

Las funciones principales incluyen:

```text
diseño lógico
breve descripción
```

Evidencia: `firmware/`, `android/app/src/`, `server/`, `web/`.

---

## 2. Ingeniería inversa y diseño · 20 %

Diseño formal disponible en:

```text
docs/disenos/
```

Incluye:

- tipos comunes y notación;
- firmware;
- Android;
- lógica de negocio;
- base de datos;
- API REST;
- web;
- protocolo y flujo;
- tests y criterios.

La documentación se ha extraído de la implementación actual del Sprint 0 y utiliza tipos abstractos `N`, `Z`, `R`, `VoF`, `Texto`, colecciones y firmas `entrada --> funcion() --> salida`.

---

## 3. Arquitectura y lógica del negocio · 30 %

### Lógica de negocio

Implementación:

```text
server/Logica.php
```

Separada de HTTP:

```text
web/api.php        -> adaptación HTTP/JSON
server/Logica.php  -> reglas y persistencia
```

### Lógica fake en clientes

Android:

```text
android/.../LogicaFake.java
```

Navegador:

```text
web/js/LogicaFake.js
```

### Diseño coincidente con implementación

Evidencias:

```text
docs/architecture.md
docs/disenos/
docs/class-audit.md
```

---

## 4. Tests · 25 %

### Android

```text
ProtocolUnitTest.java
ServidorUnitTest.java
AppInstrumentedTest.java
```

### Lógica PHP

```text
server/tests/LogicaUnitTest.php
```

Resultado ya validado durante el desarrollo:

```text
10/10 tests correctos
```

### Integración API

```text
server/tests/ApiIntegracionTest.php
```

Resultado ya validado durante el desarrollo:

```text
6/6 tests correctos
```

### Test manual de aceptación

Procedimiento:

```text
docs/validation.md
```

Evidencias visuales:

```text
docs/evidencias/
```

---

## 5. Uso de herramientas IA · 10 %

Los prompts detallados y reproducibles se encuentran en:

```text
docs/prompts/
```

Contienen:

- contexto;
- nombres y estructura exacta;
- diseños lógicos;
- requisitos funcionales;
- comentarios obligatorios;
- tests;
- criterios de aceptación;
- restricciones de seguridad/secretos;
- formato de salida esperado.

El prompt maestro permite a otra IA generar el sistema completo sin contexto previo.

---

# Test de funcionamiento obligatorio

Valores iniciales:

```text
O3 = 123 ppb
Temperatura = -12 °C
```

Cadena:

```text
C++ ficticio
  -> BLE/iBeacon
  -> Android
  -> API REST
  -> lógica de negocio
  -> MariaDB
  -> web
```

Criterio:

```text
valor final = valor ficticio inicial
```

Evidencia: `docs/evidencias/`.
