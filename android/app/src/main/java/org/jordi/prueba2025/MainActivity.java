package org.jordi.prueba2025;

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

    public void botonBuscarNuestroDispositivoBTLEPulsado(View v) {
        iniciarBusqueda();
    }

    public void botonDetenerBusquedaDispositivosBTLEPulsado(View v) {
        detenerBusqueda();
    }

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

            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                procesarResultado(result);
            }

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
                            + " | Minor: " + minorSigned
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

    @Override
    protected void onResume() {
        super.onResume();

        if (bluetoothAdapter != null && tengoPermisosBLE()) {
            comprobarBluetooth();
        }
    }

    @Override
    protected void onDestroy() {
        detenerBusqueda();
        super.onDestroy();
    }
}
