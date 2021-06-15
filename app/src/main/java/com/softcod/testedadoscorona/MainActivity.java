package com.softcod.testedadoscorona;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.softcod.testedadoscorona.processadores.Processos;

import org.json.JSONException;
import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private GoogleApiClient mGoogleApiClient;
    double latitude = 0.0, longitude = 0.0;

    private final String[] dados = new String[2];

    private Location location;
    Processos processos;
    SharedPreferences start = null;
    ProgressDialog pd;
    int dia, mes, ano;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        processos = new Processos(this);
        start = getSharedPreferences("execultado", MODE_PRIVATE);

        Date data = new Date();
        Calendar cal = Calendar.getInstance();

        cal.setTime(data);

         dia = cal.get(Calendar.DAY_OF_MONTH);
        mes = cal.get(Calendar.MONTH)+1;
        ano =  cal.get(Calendar.YEAR);
        processos.getDadoArray(dia,mes,ano);

        TextView mText = findViewById(R.id.txtResultado);
        mText.setMovementMethod(new ScrollingMovementMethod());
        mText.setText(processos.calculeMediaMovel(dia,mes,ano) );

        callConnection();

    }
    private synchronized void callConnection() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i("LOG", "onConnected("+bundle+")");

        location = LocationServices.FusedLocationApi.
                getLastLocation(mGoogleApiClient);
        if (location != null) {
            dados[0] = "\nLongitude: " + location.getLongitude();
            dados[1] = "\nLatitude: " + location.getLatitude();

            Log.i("LOG", "Latitude: "+location.getLatitude()
                    +"\narray" + dados[0]);
            Log.i("LOG", "Longitude: "+location.getLongitude()
                    +"\narray" + dados[1]);
        }
        alerta(this,dados[0],dados[1]);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("LOG", "onConnectionSuspended("+i+")");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i("LOG", "onConnectionFailed("+connectionResult+")");
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(start.getBoolean("execultado", true)){
            start.edit().putBoolean("execultado", false).apply();
        }else{

            processos.carregarBanco();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        pd = new ProgressDialog(this);
        pd.setTitle("Um momento");
        pd.setMessage("Estamos colhendo os dados.");
        pd.setCancelable(true);
        pd.setIndeterminate(true);
        pd.setIcon(android.R.drawable.ic_dialog_info);
        pd.show();

    }

    public void alerta(Context contexto, String lo, String la ){
        String dadosCompilador;

        dadosCompilador = "No mês passado tivemos:\n"+ processos.dadosMesPassado();
        dadosCompilador += ".\n Essa pesquisa foi feita na ";

            dadosCompilador += "\n" +la+ lo;

            Log.i("LOG", "Latitude: "+location.getLatitude()
                    +"\narray" + la);
            Log.i("LOG", "Longitude: "+location.getLongitude()
                    +"\narray" + lo);

        dadosCompilador += ".\n Hoje é: " + dia +"/"+ mes +"/"+ ano;

        AlertDialog.Builder alerta =new AlertDialog.Builder(contexto);
        TextView textView = new TextView(contexto);
        textView.setText(dadosCompilador);
        textView.setTextColor(Color.RED);
        alerta.setView(textView);
        alerta.setMessage("Podemos armazenar o histórico de pesquisa?\n");

        alerta.setPositiveButton("SIM",
                new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        processos.registrarConsulta(la,lo,dia,mes,ano);
                    }
                });
        alerta.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });
        pd.dismiss();
        alerta.show();
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}