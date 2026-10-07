# Trazabilidad del uso de IA

La IA se usa como herramienta de implementación y revisión, pero el diseño actúa como contrato previo.

## Flujo aplicado

```text
diseño oficial -> prompt del componente -> código generado/adaptado -> tests -> revisión diseño/código
```

Los seis prompts de `doc/prompts/` corresponden a los seis entregables solicitados en la segunda tarea. La versión final se ha revisado en ambos sentidos:

- **Diseño → código:** cada responsabilidad/operación diseñada debe localizarse en la implementación correspondiente.
- **Código → diseño:** no se mantiene lógica de dominio relevante que no esté representada en el diseño.

También se ha comprobado que las cabeceras lógicas usen exclusivamente los tipos, agregaciones y colecciones definidos por la notación oficial.

La corrección funcional extremo a extremo se valida mediante `doc/acceptance_test.md`, porque el agente de revisión no sustituye la prueba presencial.
