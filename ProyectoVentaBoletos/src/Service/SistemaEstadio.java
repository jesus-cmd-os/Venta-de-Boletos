package service;

import model.Boleto;
import model.ReporteVenta;
import model.TipoZona;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

/**
 * SistemaEstadio — Núcleo de la lógica de negocio del sistema.
 *
 * Esta clase es el corazón del proyecto. Integra las cuatro estructuras
 * de datos requeridas y centraliza toda la lógica de ventas:
 *
 *   ESTRUCTURA 1 — LinkedList<Boleto>
 *     Almacena dinámicamente los boletos vendidos.
 *     Permite agregar, buscar y eliminar boletos en tiempo de ejecución.
 *
 *   ESTRUCTURA 2 — boolean[][] + TipoZona[][] (Matrices)
 *     boolean[][]  → controla la disponibilidad de cada asiento (true=ocupado)
 *     TipoZona[][] → almacena la zona (VIP/PREF/GEN) de cada celda
 *     Acceso directo O(1) por coordenada [fila][columna].
 *
 *   ESTRUCTURA 3 — HashMap<String, Double>
 *     Asocia cada categoría con su precio actual.
 *     Acceso O(1) por clave. Permite actualizar precios en tiempo real.
 *
 *   ESTRUCTURA 4 — Queue<ReporteVenta> (Cola FIFO)
 *     Almacena los reportes de venta en el orden en que se generan.
 *     GestorArchivos los desencola en ese mismo orden al guardar el .txt.
 *
 * Patrón de diseño: esta clase actúa como el MODEL en MVC.
 * La GUI (View) nunca modifica datos directamente; siempre pasa
 * por los métodos de esta clase.
 */
public class SistemaEstadio {

    // ESTRUCTURAS DE DATOS

    /**
     * ESTRUCTURA 1 — Lista enlazada de boletos vendidos.
     * Se usa LinkedList porque los boletos se insertan al final y se pueden
     * eliminar en cualquier posición, operaciones que son O(1) y O(n)
     * respectivamente, igual o más eficientes que ArrayList para este caso.
     */
    private LinkedList<Boleto> listaBoletos;

    /**
     * ESTRUCTURA 2a — Matriz de disponibilidad de asientos.
     * false = asiento disponible (valor inicial)
     * true  = asiento ocupado (se marca en comprarBoleto())
     * El acceso directo por [fila][col] permite validar disponibilidad en O(1).
     */
    private boolean[][] matrizAsientos;

    /**
     * ESTRUCTURA 2b — Matriz de zonas del estadio.
     * Cada celda almacena el TipoZona (VIP, PREFERENCIAL, GENERAL)
     * calculado por inicializarZonas() al construir el sistema.
     * Esta matriz NO cambia durante la ejecución.
     */
    private TipoZona[][] matrizZonas;

    /**
     * ESTRUCTURA 3 — Mapa de precios por categoría.
     * Clave: nombre de la categoría ("VIP", "Preferencial", "General")
     * Valor: precio en pesos (Double)
     * Permite consultar y actualizar precios en O(1).
     */
    private HashMap<String, Double> mapaPrecios;

    /**
     * ESTRUCTURA 4 — Cola FIFO de reportes de venta.
     * Cada compra agrega un ReporteVenta al final de la cola (add).
     * GestorArchivos los extrae del frente (poll) al guardar el archivo,
     * garantizando el orden cronológico de las ventas en el reporte.
     */
    private Queue<ReporteVenta> colaReportes;

    // LÍMITES DEL BLOQUE CENTRAL (CANCHA)

    /*
     * Para una matriz 14×14, el bloque central (cancha) ocupa las celdas
     * en filas 6-7 y columnas 6-7. Estos límites se calculan dinámicamente
     * para funcionar con cualquier tamaño de matriz.
     *
     * Distribución de tribunas en una matriz 14×14:
     *   Norte → filas  0-5  (fila 0 = General, fila 5 = VIP junto a cancha)
     *   Sur   → filas  8-13 (fila 8 = VIP junto a cancha, fila 13 = General)
     *   Oeste → cols   0-5  (col 0 = General, col 5 = VIP junto a cancha)
     *   Este  → cols   8-13 (col 8 = VIP junto a cancha, col 13 = General)
     */

    /** Primera fila de la zona central (cancha). Ejemplo: 6 en matriz 14×14 */
    private final int filaCancha1;

    /** Última fila de la zona central (cancha). Ejemplo: 7 en matriz 14×14 */
    private final int filaCancha2;

    /** Primera columna de la zona central (cancha). Ejemplo: 6 en matriz 14×14 */
    private final int colCancha1;

    /** Última columna de la zona central (cancha). Ejemplo: 7 en matriz 14×14 */
    private final int colCancha2;

    // CONSTRUCTOR

    /**
     * Inicializa el sistema completo del estadio.
     *
     * Crea e inicializa las 4 estructuras de datos, calcula los límites
     * de la cancha central y asigna la zona de cada asiento en la matriz.
     *
     * Se llama una sola vez desde ProyectoVentaBoletos.main()
     * con dimensiones 14×14: new SistemaEstadio(14, 14)
     *
     * @param filas    Número total de filas de la matriz del estadio
     * @param columnas Número total de columnas de la matriz del estadio
     */
    public SistemaEstadio(int filas, int columnas) {
        // Inicialización de las 4 estructuras de datos
        this.listaBoletos   = new LinkedList<>();
        this.matrizAsientos = new boolean[filas][columnas]; // Java inicializa en false
        this.matrizZonas    = new TipoZona[filas][columnas];
        this.mapaPrecios    = new HashMap<>();
        this.colaReportes   = new LinkedList<>(); // LinkedList implementa Queue

        // Calcular el bloque central de la cancha (mitad de la matriz)
        this.filaCancha1 = filas    / 2 - 1;
        this.filaCancha2 = filas    / 2;
        this.colCancha1  = columnas / 2 - 1;
        this.colCancha2  = columnas / 2;

        // Cargar los precios base en el HashMap
        cargarPreciosIniciales();

        // Asignar la zona de cada celda en la matriz de zonas
        inicializarZonas();
    }

    // INICIALIZACIÓN

    /**
     * Carga los precios iniciales en el HashMap.
     * Se llama una sola vez desde el constructor.
     * Los precios pueden actualizarse después con actualizarPrecio().
     */
    private void cargarPreciosIniciales() {
        mapaPrecios.put("VIP",          1500.0);
        mapaPrecios.put("Preferencial",  800.0);
        mapaPrecios.put("General",       400.0);
    }

    /**
     * Recorre toda la matriz y asigna el TipoZona a cada celda
     * según su distancia mínima a la zona central (cancha).
     *
     * Regla de asignación:
     *   distancia 0-1 → VIP          (filas/cols más cercanas a la cancha)
     *   distancia 2-3 → PREFERENCIAL (zona intermedia)
     *   distancia 4+  → GENERAL      (filas/cols más alejadas)
     *
     * Se usa la distancia MÍNIMA entre eje vertical y eje horizontal
     * para que las esquinas del estadio también queden bien clasificadas.
     *
     * Esta lógica es independiente del tamaño de la matriz,
     * por lo que funciona con cualquier dimensión de estadio.
     */
    private void inicializarZonas() {
        int totalFilas = matrizZonas.length;
        int totalCols  = matrizZonas[0].length;

        for (int i = 0; i < totalFilas; i++) {
            for (int j = 0; j < totalCols; j++) {
                // Calcular distancia por cada eje y tomar el mínimo
                int distFila = distanciaFilaCancha(i);
                int distCol  = distanciaColCancha(j);
                int dist     = Math.min(distFila, distCol);

                // Asignar zona según distancia
                if (dist <= 1) {
                    matrizZonas[i][j] = TipoZona.VIP;
                } else if (dist <= 3) {
                    matrizZonas[i][j] = TipoZona.PREFERENCIAL;
                } else {
                    matrizZonas[i][j] = TipoZona.GENERAL;
                }
            }
        }
    }

    /**
     * Calcula la distancia de una fila a la zona central de la cancha.
     *
     * Si la fila está ARRIBA de la cancha (tribuna Norte):
     *   distancia = filaCancha1 - i - 1
     *   (fila justo antes de la cancha tiene distancia 0, las superiores aumentan)
     *
     * Si la fila está ABAJO de la cancha (tribuna Sur):
     *   distancia = i - filaCancha2 - 1
     *   (fila justo después de la cancha tiene distancia 0, las inferiores aumentan)
     *
     * Si la fila está DENTRO del bloque de la cancha: distancia = 0
     *
     * @param i Índice de la fila a evaluar
     * @return Distancia de la fila a la zona central
     */
    private int distanciaFilaCancha(int i) {
        if (i < filaCancha1) return filaCancha1 - i - 1;
        if (i > filaCancha2) return i - filaCancha2 - 1;
        return 0;
    }

    /**
     * Calcula la distancia de una columna a la zona central de la cancha.
     * Misma lógica que distanciaFilaCancha() pero en el eje horizontal.
     *
     * @param j Índice de la columna a evaluar
     * @return Distancia de la columna a la zona central
     */
    private int distanciaColCancha(int j) {
        if (j < colCancha1) return colCancha1 - j - 1;
        if (j > colCancha2) return j - colCancha2 - 1;
        return 0;
    }

    // LÓGICA DE VENTAS

    /**
     * Procesa la compra de un boleto aplicando todas las validaciones.
     *
     * Pasos internos:
     *   1. Valida que las coordenadas estén dentro del rango de la matriz
     *   2. Consulta la matriz para verificar que el asiento no esté ocupado
     *   3. Obtiene el precio actual del HashMap según la categoría
     *   4. Crea el objeto Boleto y lo agrega a la LinkedList
     *   5. Marca el asiento como ocupado en la matriz (true)
     *   6. Crea un ReporteVenta y lo encola en la Cola FIFO
     *
     * @param id        Identificador único del boleto (generado con UUID)
     * @param categoria Zona del asiento: "VIP", "Preferencial" o "General"
     * @param fila      Fila del asiento en la matriz (0-indexed)
     * @param col       Columna del asiento en la matriz (0-indexed)
     * @return true si la compra fue exitosa, false si el asiento ya estaba ocupado
     *         o las coordenadas son inválidas
     */
    public boolean comprarBoleto(String id, String categoria, int fila, int col) {

        // Validación 1: Coordenadas dentro de los límites de la matriz
        if (fila < 0 || fila >= matrizAsientos.length ||
                col  < 0 || col  >= matrizAsientos[0].length) {
            return false;
        }

        // Validación 2: El asiento no debe estar ya ocupado
        if (matrizAsientos[fila][col]) {
            return false;
        }

        // Obtener precio del HashMap (0.0 si la categoría no existe)
        double precio    = mapaPrecios.getOrDefault(categoria, 0.0);
        String ubicacion = "Fila " + fila + ", Asiento " + col;

        // LINKEDLIST: crear y agregar el boleto a la lista enlazada
        Boleto boleto = new Boleto(id, categoria, precio, ubicacion);
        boleto.setVendido(true);
        listaBoletos.add(boleto);

        // MATRIZ: marcar la celda como ocupada para futuras validaciones
        matrizAsientos[fila][col] = true;

        // COLA FIFO: generar y encolar el reporte de esta venta
        colaReportes.add(new ReporteVenta(id, categoria, ubicacion, precio));

        return true;
    }

    // OPERACIONES SOBRE LA LINKEDLIST

    /**
     * Busca un boleto en la LinkedList por su ubicación física.
     *
     * Recorre la lista de forma secuencial comparando el campo "asiento"
     * de cada Boleto con la ubicación construida a partir de fila y col.
     * Complejidad: O(n) donde n es el número de boletos vendidos.
     *
     * @param fila Fila del asiento a buscar
     * @param col  Columna del asiento a buscar
     * @return El Boleto encontrado, o null si no hay boleto en esa ubicación
     */
    public Boleto buscarBoleto(int fila, int col) {
        String ubicacion = "Fila " + fila + ", Asiento " + col;
        for (Boleto b : listaBoletos) {
            if (b.getAsiento().equals(ubicacion)) {
                return b;
            }
        }
        return null; // No encontrado
    }

    /**
     * Elimina un boleto de la LinkedList por su ID único.
     *
     * Usa removeIf() que recorre la lista y elimina el primer elemento
     * cuyo idBoleto coincida con el proporcionado.
     * Útil para anular o cancelar una venta específica.
     *
     * @param idBoleto ID único del boleto a eliminar
     * @return true si el boleto fue encontrado y eliminado, false si no existía
     */
    public boolean eliminarBoleto(String idBoleto) {
        return listaBoletos.removeIf(b -> b.getIdBoleto().equals(idBoleto));
    }

    // OPERACIONES SOBRE EL HASHMAP

    /**
     * Actualiza el precio de una categoría en el HashMap.
     *
     * Al usar put(), si la clave ya existe el valor se sobreescribe.
     * El nuevo precio se usa automáticamente en todas las ventas posteriores,
     * ya que comprarBoleto() consulta el HashMap en cada transacción.
     *
     * Llamado desde VentanaPrincipal.actualizarPrecios() cuando el usuario
     * ingresa nuevos precios en el diálogo correspondiente.
     *
     * @param categoria   Clave del HashMap ("VIP", "Preferencial" o "General")
     * @param nuevoPrecio Nuevo precio en pesos (validado > 0 en la GUI)
     */
    public void actualizarPrecio(String categoria, double nuevoPrecio) {
        mapaPrecios.put(categoria, nuevoPrecio);
    }

    // OPERACIONES SOBRE LA COLA FIFO

    /**
     * Extrae y devuelve el siguiente ReporteVenta de la cola (FIFO).
     *
     * poll() es la operación de dequeue: devuelve y elimina el elemento
     * del frente de la cola. Si la cola está vacía, devuelve null.
     * GestorArchivos llama a este método en un loop hasta vaciar la cola.
     *
     * @return El ReporteVenta más antiguo de la cola, o null si está vacía
     */
    public ReporteVenta extraerReporte() {
        return colaReportes.poll();
    }

    // CONSULTAS PARA LA GUI

    /**
     * Devuelve el nombre de categoría del asiento en las coordenadas dadas.
     * Consulta la matrizZonas[][] y convierte el TipoZona a String.
     * Usado por PanelEstadio para mostrar el tooltip de cada botón.
     *
     * @param fila Fila del asiento
     * @param col  Columna del asiento
     * @return "VIP", "Preferencial" o "General"
     */
    public String obtenerCategoriaAsiento(int fila, int col) {
        switch (matrizZonas[fila][col]) {
            case VIP:          return "VIP";
            case PREFERENCIAL: return "Preferencial";
            default:           return "General";
        }
    }

    /**
     * Devuelve el precio actual del asiento en las coordenadas dadas.
     * Combina obtenerCategoriaAsiento() con una consulta al HashMap.
     * Usado por VentanaPrincipal al calcular el total de una compra.
     *
     * @param fila Fila del asiento
     * @param col  Columna del asiento
     * @return Precio actual en pesos según el HashMap de precios
     */
    public double obtenerPrecioAsiento(int fila, int col) {
        return mapaPrecios.getOrDefault(obtenerCategoriaAsiento(fila, col), 0.0);
    }

    // GETTERS — Acceso de solo lectura a las estructuras de datos

    /** @return Referencia a la matriz de disponibilidad de asientos */
    public boolean[][] getMatrizAsientos() { return matrizAsientos; }

    /** @return Referencia a la matriz de zonas del estadio */
    public TipoZona[][] getMatrizZonas() { return matrizZonas; }

    /** @return Referencia al HashMap de precios por categoría */
    public HashMap<String, Double> getMapaPrecios() { return mapaPrecios; }

    /** @return Referencia a la LinkedList de boletos vendidos */
    public LinkedList<Boleto> getListaBoletos() { return listaBoletos; }

    /** @return Número de reportes pendientes de guardar en la cola */
    public int getTotalReportesEnCola() { return colaReportes.size(); }
}