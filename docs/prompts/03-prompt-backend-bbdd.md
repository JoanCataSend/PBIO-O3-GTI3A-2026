# Prompt · Base de datos + lógica de negocio + API

```text
Genera backend PHP 8 + MariaDB para PBIO Sprint 0.

Modelo:
Dispositivo(dispositivoId PK auto, uuid UNIQUE, nombre)
TipoMedida(tipoMedidaId PK, nombre UNIQUE, unidad)
Medida(medidaId PK auto, dispositivoId FK, tipoMedidaId FK, valor INT, contador TINYINT UNSIGNED CHECK 0..255, rssi SMALLINT, fechaHora DATETIME(3) default current timestamp).
Índices: (dispositivoId,fechaHora) y (tipoMedidaId,fechaHora).
Seed: EPSG-GTI-PROY-3A / GTI Joan; 11 CO2 ppm; 12 Temperatura °C; 13 Ruido dB; 14 O3 ppb.

Crea server/database/schema.sql, seed.sql, drop.sql.

Crea server/Logica.php con funciones exactas:
conexionBD, probarConexion, insertarMedida, buscarMedidaConId, listarMedidas, listarDispositivos, listarTiposMedida, buscarUltimaMedida, validarMedidaEntrada, consultaMedidaVista, normalizarMedidaVista.

MedidaEntrada=(uuid:Texto,tipoMedidaId:N,valor:Z,contador:N,rssi:Z). Validar todos los campos; uuid no vacío; numéricos enteros; contador 0..255. insertarMedida debe comprobar que dispositivo y tipo existen. Usa prepared statements. La lógica no conoce HTTP.

Crea server/SDBaseDatos.example.php con host localhost, port 3306, database jcatsen_pbio, user jcatsen_pbio_user y password CAMBIAR_EN_EL_SERVIDOR. No incluyas credenciales reales.

Crea web/api.php como adaptador HTTP. POST inserta y devuelve 201. GET lista. accion=health/dispositivos/tipos/ultima. InvalidArgumentException->400, DomainException->404, método no soportado->405, Throwable->500 genérico. Debe encontrar Logica.php tanto como ./server/Logica.php en Plesk como ../server/Logica.php en repositorio.

Añade cabeceras y diseño lógico antes de cada función.

Crea tests PHP: LogicaUnitTest sin BD y ApiIntegracionTest de solo lectura contra https://jcatsen.upv.edu.es/biometria/api.php. No abras MariaDB remotamente.

Entrega archivos completos.
```
