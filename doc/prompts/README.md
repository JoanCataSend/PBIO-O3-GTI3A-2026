# Especificaciones para generación con IA

Estos son **exactamente los seis prompts** exigidos en la segunda tarea. Cada uno parte del diseño previo de `doc/` y obliga a la IA a respetarlo, documentar funciones con la notación oficial y generar pruebas automáticas.

1. `01_database.md` - base de datos.
2. `02_business_logic.md` - lógica de negocio real del backend.
3. `03_api_rest.md` - servidor/adaptador REST.
4. `04_android_fake.md` - lógica fake del teléfono.
5. `05_web_fake.md` - lógica fake del navegador.
6. `06_web_ux.md` - UX del navegador.

Regla común: ningún prompt autoriza a la IA a rediseñar el componente. Si detecta una contradicción, debe señalarla antes de generar código.
