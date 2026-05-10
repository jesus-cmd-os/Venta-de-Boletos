package model;

/**
 * TipoZona — Enumeración de las zonas disponibles en el estadio.
 *
 * Se usa para clasificar cada celda de la matriz matrizZonas[][]
 * en SistemaEstadio. Al usar un enum en lugar de Strings, el
 * compilador detecta errores de escritura en tiempo de compilación
 * en lugar de en tiempo de ejecución.
 *
 * La zona de cada asiento se determina por su distancia a la cancha:
 *   VIP          → distancia 0-1 (más cerca de la cancha)
 *   PREFERENCIAL → distancia 2-3 (zona intermedia)
 *   GENERAL      → distancia 4+  (parte alta, más lejos)
 *
 * Esta clasificación aplica igual para las 4 tribunas del estadio
 * (Norte, Sur, Este, Oeste).
 */
public enum TipoZona {

    /** Zona premium, filas/columnas más cercanas a la cancha. Precio: $1,500 */
    VIP,

    /** Zona intermedia, entre VIP y General. Precio: $800 */
    PREFERENCIAL,

    /** Zona alta, filas/columnas más alejadas de la cancha. Precio: $400 */
    GENERAL
}