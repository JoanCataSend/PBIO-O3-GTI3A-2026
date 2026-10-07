/*
 * Archivo: MainActivity.java
 * Descripción: actividad principal Android. Gestiona permisos BLE, escaneo del
 *              nodo GTI Joan, decodificación de iBeacon, UI y envío de medidas.
 * Copyright: 2026 Joan Catala Sendra (uso académico PBIO - UPV)
 * Fecha: 2026-10-07
 * Autor: Joan Catala Sendra
 * Aportación: recepción BLE del protocolo PBIO y conexión con LogicaFake.
 */

package es.upv.jcatsen.pbio;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = ">>>>";

    // IMPORTANTE:
    // ActivityCompat.requestPermissions() solo admite los 16 bits bajos
    // para requestCode, por eso usamos un valor pequeño.
    private static final int CODIGO_PERMISOS = 1001;
    private static final int CODIGO_ACTIVAR_BT = 1002;

    private static final String NOMBRE_NODO = "GTI Joan";
    private static final String UUID_PROYECTO = "EPSG-GTI-PROY-3A";

    // Mismos IDs que en Publicador.h
    private static final int ID_TEMPERATURA = 12;
    private static final int ID_O3 = 14;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner scanner;
    private ScanCallback callback;

    private TextView textoEstado;
    private TextView textoO3;
    private TextView textoTemperatura;
    private TextView textoContador;
    private TextView textoRssi;
    private TextView textoTrama;
    private TextView textoServidor;

    private int ultimoContadorO3Enviado = -1;
    private int ultimoContadorTemperaturaEnviado = -1;

    /**
     * --------------------
     * Diseño lógico: onCreate() -->
     * Descripción: enlaza UI, obtiene BluetoothAdapter y solicita permisos necesarios.
     * --------------------
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textoEstado = findViewById(R.id.textoEstado);
        textoO3 = findViewById(R.id.textoO3);
        textoTemperatura = findViewById(R.id.textoTemperatura);
        textoContador = findViewById(R.id.textoContador);
        textoRssi = findViewById(R.id.textoRssi);
        textoTrama = findViewById(R.id.textoTrama);
        textoServidor = findViewById(R.id.textoServidor);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            textoEstado.setText("Este teléfono no tiene Bluetooth");
            return;
        }

        pedirPermisosSiHacenFalta();

        // Solo comprobamos/arrancamos Bluetooth si ya están concedidos.
        if (tengoPermisosBLE()) {
            comprobarBluetooth();
        }
    }

    /**
     * --------------------
     * Diseño lógico: tengoPermisosBLE() --> concedidos: B
     * Descripción: comprueba los permisos requeridos según la versión de Android.
     * --------------------
     */
    private boolean tengoPermisosBLE() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_SCAN
            ) == PackageManager.PERMISSION_GRANTED
                    &&
                    ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.BLUETOOTH_CONNECT
                    ) == PackageManager.PERMISSION_GRANTED
                    &&
                    ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED;
        }

        return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * --------------------
     * Diseño lógico: pedirPermisosSiHacenFalta() -->
     * Descripción: solicita únicamente los permisos BLE/localización aún no concedidos.
     * --------------------
     */
    private void pedirPermisosSiHacenFalta() {

        if (tengoPermisosBLE()) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            ArrayList<String> permisos = new ArrayList<>();

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED) {
                permisos.add(Manifest.permission.BLUETOOTH_SCAN);
            }

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED) {
                permisos.add(Manifest.permission.BLUETOOTH_CONNECT);
            }

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
                permisos.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }

            if (!permisos.isEmpty()) {
                ActivityCompat.requestPermissions(
                        this,
                        permisos.toArray(new String[0]),
                        CODIGO_PERMISOS
                );
            }

        } else {

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION
                        },
                        CODIGO_PERMISOS
                );
            }
        }
    }

    /**
     * --------------------
     * Diseño lógico: comprobarBluetooth() -->
     * Descripción: valida permisos/estado Bluetooth y obtiene el escáner BLE.
     * --------------------
     */
    private void comprobarBluetooth() {

        if (!tengoPermisosBLE()) {
            textoEstado.setText("Faltan permisos Bluetooth");
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, CODIGO_ACTIVAR_BT);
            textoEstado.setText("Activa Bluetooth");
            return;
        }

        scanner = bluetoothAdapter.getBluetoothLeScanner();

        if (scanner == null) {
            textoEstado.setText("No se pudo iniciar el escáner BLE");
            return;
        }

        textoEstado.setText("Listo para buscar " + NOMBRE_NODO);
    }

    /**
     * --------------------
     * Diseño lógico: botonBuscarNuestroDispositivoBTLEPulsado() -->
     * Descripción: inicia la búsqueda cuando el usuario pulsa Buscar.
     * Nota: el parámetro View es un detalle de implementación Android y se omite del diseño.
     * --------------------
     */
    public void botonBuscarNuestroDispositivoBTLEPulsado(View v) {
        iniciarBusqueda();
    }

    /**
     * --------------------
     * Diseño lógico: botonDetenerBusquedaDispositivosBTLEPulsado() -->
     * Descripción: detiene el escaneo cuando el usuario pulsa Detener.
     * --------------------
     */
    public void botonDetenerBusquedaDispositivosBTLEPulsado(View v) {
        detenerBusqueda();
    }

    /**
     * --------------------
     * Diseño lógico: iniciarBusqueda() -->
     * Descripción: configura un filtro por nombre GTI Joan e inicia escaneo BLE rápido.
     * --------------------
     */
    private void iniciarBusqueda() {

        if (!tengoPermisosBLE()) {
            pedirPermisosSiHacenFalta();
            textoEstado.setText("Concede los permisos y vuelve a pulsar Buscar");
            return;
        }

        comprobarBluetooth();

        if (scanner == null) {
            return;
        }

        detenerBusqueda();

        callback = new ScanCallback() {

            /**
             * --------------------
             * Diseño lógico: resultado: ResultadoBLE --> onScanResult() -->
             * Descripción: delega el resultado BLE válido en el procesado del dominio.
             * --------------------
             */
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                procesarResultado(result);
            }

            /**
             * --------------------
             * Diseño lógico: error_code: N --> onScanFailed() -->
             * Descripción: actualiza el estado cuando Android informa de un fallo de escaneo.
             * --------------------
             */
            @Override
            public void onScanFailed(int errorCode) {
                Log.e(TAG, "Scan fallido: " + errorCode);

                runOnUiThread(() ->
                        textoEstado.setText("Error de escaneo: " + errorCode)
                );
            }
        };

        ScanFilter filtro = new ScanFilter.Builder()
                .setDeviceName(NOMBRE_NODO)
                .build();

        List<ScanFilter> filtros = new ArrayList<>();
        filtros.add(filtro);

        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();

        try {
            scanner.startScan(filtros, settings, callback);
            textoEstado.setText("Buscando " + NOMBRE_NODO + "...");
        } catch (SecurityException e) {
            Log.e(TAG, "Sin permiso para iniciar BLE", e);
            textoEstado.setText("Faltan permisos Bluetooth");
        }
    }

    /**
     * --------------------
     * Diseño lógico: detenerBusqueda() -->
     * Descripción: detiene el callback de escaneo activo de forma segura.
     * --------------------
     */
    private void detenerBusqueda() {

        if (scanner == null || callback == null) {
            return;
        }

        if (!tengoPermisosBLE()) {
            return;
        }

        try {
            scanner.stopScan(callback);
        } catch (SecurityException e) {
            Log.e(TAG, "Sin permiso para detener BLE", e);
        }

        callback = null;
        textoEstado.setText("Escaneo detenido");
    }

    /**
     * --------------------
     * Diseño lógico: resultado: ResultadoBLE --> procesarResultado() -->
     * Descripción: valida iBeacon/UUID, decodifica ID-contador-valor, actualiza UI y
     * entrega la medida nueva a la lógica fake del cliente.
     * --------------------
     */
    private void procesarResultado(ScanResult resultado) {

        if (resultado == null || resultado.getScanRecord() == null) {
            return;
        }

        byte[] bytes = resultado.getScanRecord().getBytes();

        TramaIBeacon trama = new TramaIBeacon(bytes);

        if (!trama.esValida()) {
            return;
        }

        String uuid = Utilidades.bytesToString(trama.getUUID());

        if (!UUID_PROYECTO.equals(uuid)) {
            return;
        }

        int major = Utilidades.bytesToUnsignedInt(trama.getMajor());

        int idMedida = (major >> 8) & 0xFF;
        int contador = major & 0xFF;

        int minorUnsigned =
                Utilidades.bytesToUnsignedInt(trama.getMinor());

        int minorSigned =
                Utilidades.bytesToSignedInt16(trama.getMinor());

        int rssi = resultado.getRssi();

        Log.d(
                TAG,
                "iBeacon " + UUID_PROYECTO
                        + " id=" + idMedida
                        + " contador=" + contador
                        + " minor=" + minorSigned
                        + " RSSI=" + rssi
        );

        int valorParaServidor =
                idMedida == ID_O3
                        ? minorUnsigned
                        : minorSigned;

        enviarMedidaSiEsNueva(
                uuid,
                idMedida,
                valorParaServidor,
                contador,
                rssi
        );

        runOnUiThread(() -> {

            textoEstado.setText(
                    "Recibiendo " + NOMBRE_NODO
            );

            textoContador.setText(
                    "Contador: " + contador
            );

            textoRssi.setText(
                    "RSSI: " + rssi + " dBm"
            );

            textoTrama.setText(
                    "Major: " + major
                            + " | ID: " + idMedida
                            + " | Minor: " + valorParaServidor
            );

            if (idMedida == ID_O3) {

                textoO3.setText(
                        "O₃: " + minorUnsigned + " ppb"
                );

            } else if (idMedida == ID_TEMPERATURA) {

                textoTemperatura.setText(
                        "Temperatura: " + minorSigned + " °C"
                );
            }
        });
    }


    /**
     * --------------------
     * Diseño lógico:
     * uuid: Text, id_medida: N, valor: Z, contador: N, rssi: Z --> enviarMedidaSiEsNueva() -->
     * Descripción: evita duplicados por contador y solicita insertar la medida al servidor.
     * --------------------
     */
    private void enviarMedidaSiEsNueva(
            String uuid,
            int idMedida,
            int valor,
            int contador,
            int rssi
    ) {

        if (idMedida != ID_O3
                && idMedida != ID_TEMPERATURA) {
            return;
        }

        if (idMedida == ID_O3) {

            if (contador == ultimoContadorO3Enviado) {
                return;
            }

            ultimoContadorO3Enviado = contador;

        } else {

            if (contador == ultimoContadorTemperaturaEnviado) {
                return;
            }

            ultimoContadorTemperaturaEnviado = contador;
        }

        MedidaEntrada datos =
                new MedidaEntrada(
                        uuid,
                        idMedida,
                        valor,
                        contador,
                        rssi
                );

        runOnUiThread(() ->
                textoServidor.setText(
                        "Servidor: enviando tipo "
                                + idMedida
                                + "..."
                )
        );

        LogicaFake.insertarMedida(
                datos,
                new PeticionarioREST.Callback() {

                    /**
                     * --------------------
                     * Diseño lógico: respuesta: MedidaVista --> correcto() -->
                     * Descripción: confirma en la interfaz que el servidor guardó la medida.
                     * --------------------
                     */
                    @Override
                    public void correcto(
                            org.json.JSONObject respuesta
                    ) {

                        runOnUiThread(() ->
                                textoServidor.setText(
                                        "Servidor: medida guardada"
                                )
                        );
                    }

                    /**
                     * --------------------
                     * Diseño lógico: mensaje: Text --> error() -->
                     * Descripción: permite reintento y muestra el fallo de comunicación.
                     * --------------------
                     */
                    @Override
                    public void error(String mensaje) {

                        // Permitimos reintentar esta medida si vuelve
                        // a recibirse durante la ventana de advertising.
                        if (idMedida == ID_O3
                                && ultimoContadorO3Enviado == contador) {
                            ultimoContadorO3Enviado = -1;
                        }

                        if (idMedida == ID_TEMPERATURA
                                && ultimoContadorTemperaturaEnviado == contador) {
                            ultimoContadorTemperaturaEnviado = -1;
                        }

                        Log.e(
                                TAG,
                                "Error REST: " + mensaje
                        );

                        runOnUiThread(() ->
                                textoServidor.setText(
                                        "Servidor: error de envío"
                                )
                        );
                    }
                }
        );
    }

    /**
     * --------------------
     * Diseño lógico: resultado: ResultadoPermisos --> onRequestPermissionsResult() -->
     * Descripción: continúa la inicialización BLE cuando el usuario responde a permisos.
     * --------------------
     */
    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults
    ) {

        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );

        if (requestCode == CODIGO_PERMISOS) {

            if (tengoPermisosBLE()) {
                comprobarBluetooth();
            } else {
                textoEstado.setText(
                        "Necesito permisos Bluetooth para buscar el nodo"
                );
            }
        }
    }

    /**
     * --------------------
     * Diseño lógico: onResume() -->
     * Descripción: vuelve a comprobar Bluetooth al recuperar el foco de la actividad.
     * --------------------
     */
    @Override
    protected void onResume() {
        super.onResume();

        if (bluetoothAdapter != null && tengoPermisosBLE()) {
            comprobarBluetooth();
        }
    }

    /**
     * --------------------
     * Diseño lógico: onDestroy() -->
     * Descripción: detiene el escaneo antes de destruir la actividad.
     * --------------------
     */
    @Override
    protected void onDestroy() {
        detenerBusqueda();
        super.onDestroy();
    }
}
