package service;

import model.Boleto;
import model.ReporteVenta;
import model.TipoZona;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Clase principal del sistema del estadio.
 * Gestiona: venta de boletos, control de asientos,
 * asignación de zonas, manejo de precios y reportes.
 */
public class SistemaEstadio {

    private LinkedList<Boleto>       listaBoletos;
    private boolean[][]              matrizAsientos;
    private TipoZona[][]             matrizZonas;
    private HashMap<String, Double>  mapaPrecios;
    private Queue<ReporteVenta>      colaReportes;

    // ── Dimensiones del estadio ───────────────────────────────────────────────
    // Para una matriz 14×14:
    //   Filas  0-13  → tribunas Norte (0-5) y Sur (8-13), más el bloque central (6-7)
    //   Cols   0-13  → tribunas Oeste (0-5) y Este (8-13), más el bloque central (6-7)
    //
    //   NORTE (filas 0-5): fila 0 = GEN (lejos), fila 5 = VIP (junto cancha)
    //   SUR   (filas 8-13): fila 8 = VIP (junto cancha), fila 13 = GEN (lejos)
    //   OESTE (cols 0-5):  col 0 = GEN (lejos), col 5 = VIP (junto cancha)
    //   ESTE  (cols 8-13): col 8 = VIP (junto cancha), col 13 = GEN (lejos)
    //
    //   Las filas/cols 6-7 son la zona central (asientos junto al campo, VIP)

    private final int filaCancha1;  // primera fila del bloque central
    private final int filaCancha2;  // última  fila del bloque central
    private final int colCancha1;   // primera col  del bloque central
    private final int colCancha2;   // última  col  del bloque central

    public SistemaEstadio(int filas, int columnas) {
        this.listaBoletos  = new LinkedList<>();
        this.matrizAsientos = new boolean[filas][columnas];
        this.matrizZonas    = new TipoZona[filas][columnas];
        this.mapaPrecios    = new HashMap<>();
        this.colaReportes   = new LinkedList<>();

        // Bloque central = mitad de la matriz
        this.filaCancha1 = filas / 2 - 1;
        this.filaCancha2 = filas / 2;
        this.colCancha1  = columnas / 2 - 1;
        this.colCancha2  = columnas / 2;

        cargarPreciosIniciales();
        inicializarZonas();
    }

    private void cargarPreciosIniciales() {
        mapaPrecios.put("VIP",          1500.0);
        mapaPrecios.put("Preferencial",  800.0);
        mapaPrecios.put("General",       400.0);
    }

    /**
     * Asigna TipoZona a cada celda de la matriz.
     *
     * Regla: la distancia a la "cancha" determina la zona.
     *   distancia 0-1 → VIP
     *   distancia 2-3 → Preferencial
     *   distancia 4+  → General
     *
     * La distancia se calcula como el mínimo entre:
     *   - distancia a la cancha por filas  (tribunas Norte/Sur)
     *   - distancia a la cancha por columnas (tribunas Este/Oeste)
     */
    private void inicializarZonas() {
        int totalFilas = matrizZonas.length;
        int totalCols  = matrizZonas[0].length;

        for (int i = 0; i < totalFilas; i++) {
            for (int j = 0; j < totalCols; j++) {

                int distFila = distanciaFilaCancha(i, totalFilas);
                int distCol  = distanciaColCancha(j, totalCols);
                int dist     = Math.min(distFila, distCol);

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
     * Distancia de la fila i a la zona central (cancha) en el eje vertical.
     * La zona central está en filas [filaCancha1, filaCancha2].
     *   i < filaCancha1 → tribuna Norte → distancia = filaCancha1 - i - 1
     *   i > filaCancha2 → tribuna Sur   → distancia = i - filaCancha2 - 1
     *   i entre ambos   → zona central  → distancia = 0
     */
    private int distanciaFilaCancha(int i, int totalFilas) {
        if (i < filaCancha1)  return filaCancha1 - i - 1;
        if (i > filaCancha2)  return i - filaCancha2 - 1;
        return 0;
    }

    /**
     * Distancia de la columna j a la zona central en el eje horizontal.
     * La zona central está en cols [colCancha1, colCancha2].
     */
    private int distanciaColCancha(int j, int totalCols) {
        if (j < colCancha1)  return colCancha1 - j - 1;
        if (j > colCancha2)  return j - colCancha2 - 1;
        return 0;
    }

    // ── Compra de boleto ──────────────────────────────────────────────────────
    public boolean comprarBoleto(String id, String categoria, int fila, int col) {
        if (fila < 0 || fila >= matrizAsientos.length ||
                col  < 0 || col  >= matrizAsientos[0].length) return false;

        if (matrizAsientos[fila][col]) return false;

        double precio    = mapaPrecios.getOrDefault(categoria, 0.0);
        String ubicacion = "Fila " + fila + ", Asiento " + col;

        Boleto boleto = new Boleto(id, categoria, precio, ubicacion);
        boleto.setVendido(true);
        listaBoletos.add(boleto);

        matrizAsientos[fila][col] = true;

        colaReportes.add(new ReporteVenta(id, categoria, ubicacion, precio));
        return true;
    }

    // ── Consultas útiles para la GUI ──────────────────────────────────────────
    public String obtenerCategoriaAsiento(int fila, int col) {
        switch (matrizZonas[fila][col]) {
            case VIP:          return "VIP";
            case PREFERENCIAL: return "Preferencial";
            default:           return "General";
        }
    }

    public double obtenerPrecioAsiento(int fila, int col) {
        return mapaPrecios.getOrDefault(obtenerCategoriaAsiento(fila, col), 0.0);
    }

    public void actualizarPrecio(String categoria, double nuevoPrecio) {
        mapaPrecios.put(categoria, nuevoPrecio);
    }

    /**
     * Busca un boleto en la LinkedList por su ubicación (fila y columna).
     * Recorre la lista para verificar si existe un boleto vendido en ese asiento.
     * @return el Boleto encontrado, o null si no existe.
     */
    public Boleto buscarBoleto(int fila, int col) {
        String ubicacion = "Fila " + fila + ", Asiento " + col;
        for (Boleto b : listaBoletos) {
            if (b.getAsiento().equals(ubicacion)) {
                return b;
            }
        }
        return null;
    }

    /**
     * Elimina un boleto de la LinkedList por su ID.
     * Usado cuando se cancela o anula una venta.
     * @return true si se encontró y eliminó, false si no existía.
     */
    public boolean eliminarBoleto(String idBoleto) {
        return listaBoletos.removeIf(b -> b.getIdBoleto().equals(idBoleto));
    }

    public ReporteVenta extraerReporte() {
        return colaReportes.poll();
    }

    // ── Getters ───────────────────────────────────────────────────────────────
    public boolean[][]             getMatrizAsientos()      { return matrizAsientos; }
    public TipoZona[][]            getMatrizZonas()         { return matrizZonas; }
    public HashMap<String, Double> getMapaPrecios()         { return mapaPrecios; }
    public LinkedList<Boleto>      getListaBoletos()        { return listaBoletos; }
    public int                     getTotalReportesEnCola() { return colaReportes.size(); }
}