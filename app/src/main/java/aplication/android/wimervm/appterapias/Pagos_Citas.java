package aplication.android.wimervm.appterapias;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Pagos_Citas extends AppCompatActivity implements View.OnClickListener{

    private DatabaseReference pacientesDatabase,spinerDatabase;
    private TextView tvNombre,tvNss,tvSeguro;
    private Spinner spTipoPaggo;
    List<String> listMId= new ArrayList<String>();
    private EditText edtMonto,edtEfectivo,edtSeguro;
    private Button btnGuardar;
    private View ViewFocus = null;
    private ProgressDialog mRegProgress;
    private String idPaciente, idEstado;
    private DatabaseReference PagosDatabase,citassDatabase;
    TextView tvDevolver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagos__citas);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.mytoolbar5);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = this.getIntent();
        idEstado = i.getExtras().getString("id_CITA");
        idPaciente = i.getExtras().getString("id");

        mRegProgress = new ProgressDialog(this);

        tvNombre=(TextView)findViewById(R.id.tvNombrePago);
        tvSeguro=(TextView)findViewById(R.id.tvSeguroPago);
        tvNss=(TextView)findViewById(R.id.tvNssPago);
        spTipoPaggo=(Spinner)findViewById(R.id.spinnerTipoPago);
        edtMonto=(EditText)findViewById(R.id.edMontoCita);
        edtEfectivo=(EditText)findViewById(R.id.edMontoEfectivo);
        edtSeguro=(EditText)findViewById(R.id.edMontoSeguro);
        btnGuardar=(Button)findViewById(R.id.btnGuardarCita);
        tvDevolver = (TextView) findViewById(R.id.tvDevolver);

        pacientesDatabase = FirebaseDatabase.getInstance().getReference().child("Pacientes").child(idPaciente);
        pacientesDatabase.keepSynced(true);

        citassDatabase = FirebaseDatabase.getInstance().getReference().child("Citas_Reservadas");
        citassDatabase.keepSynced(true);

        PagosDatabase= FirebaseDatabase.getInstance().getReference();
        PagosDatabase.keepSynced(true);

        pacientesDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("nombre").getValue().toString();
                String apellido = dataSnapshot.child("apellido").getValue().toString();
                String seguro = dataSnapshot.child("seguro").getValue().toString();
                String nss = dataSnapshot.child("Nss").getValue().toString();

                tvNombre.setText(name+" "+apellido);
                tvSeguro.setText(seguro);
                tvNss.setText(nss);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnGuardar.setOnClickListener(this);
        spinerPago();
        seleccion();
    }

    public void seleccion(){
        try {
            spTipoPaggo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    final String tipo = listMId.get(i);

                    if (tipo.equals("Efectivo")) {
                        edtMonto.setVisibility(View.VISIBLE);
                        edtSeguro.setVisibility(View.GONE);
                        edtEfectivo.setVisibility(View.VISIBLE);
                        btnGuardar.setVisibility(View.VISIBLE);
                        edtSeguro.setText("");
                        edtSeguro.setError(null);
                        tvDevolver.setVisibility(View.GONE);
                    } else if (tvSeguro.getText().equals("No tiene") && tipo.equals("Efectivo y Seguro") || tvSeguro.getText().equals("No tiene") && tipo.equals("Seguro")){
                        edtMonto.setError(null);
                        edtEfectivo.setError(null);
                        tvDevolver.setVisibility(View.GONE);
                        edtMonto.setVisibility(View.GONE);
                        edtEfectivo.setVisibility(View.GONE);
                        btnGuardar.setVisibility(View.GONE);
                        new android.app.AlertDialog.Builder(Pagos_Citas.this)
                                .setTitle("El paciente no tiene seguro!")
                                .setMessage("Favor elegir otro tipo de pago")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                }).show();
                    }else if(tipo.equals("Efectivo y Seguro")){
                        tvDevolver.setVisibility(View.GONE);
                        edtMonto.setVisibility(View.VISIBLE);
                        edtEfectivo.setVisibility(View.VISIBLE);
                        edtSeguro.setVisibility(View.VISIBLE);
                        btnGuardar.setVisibility(View.VISIBLE);
                    }else if(tipo.equals("Seguro")){
                        edtEfectivo.setText("");
                        edtEfectivo.setError(null);
                        tvDevolver.setVisibility(View.GONE);
                        edtMonto.setVisibility(View.VISIBLE);
                        edtEfectivo.setVisibility(View.GONE);
                        edtSeguro.setVisibility(View.VISIBLE);
                        btnGuardar.setVisibility(View.VISIBLE);
                    }else  if(tipo.equals("Gratis")){
                        tvDevolver.setVisibility(View.GONE);
                        edtMonto.setVisibility(View.VISIBLE);
                        edtEfectivo.setText("");
                        edtSeguro.setText("");
                        edtEfectivo.setError(null);
                        edtSeguro.setError(null);
                        edtEfectivo.setVisibility(View.GONE);
                        edtSeguro.setVisibility(View.GONE);
                        btnGuardar.setVisibility(View.VISIBLE);
                        new android.app.AlertDialog.Builder(Pagos_Citas.this)
                                .setTitle("Genial!")
                                .setMessage("Su cita a sido gratis")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                }).show();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }catch (Exception e){
            Toast.makeText(Pagos_Citas.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void spinerPago(){
        try {
            spinerDatabase = FirebaseDatabase.getInstance().getReference().child("tipo_de_pago");
            spinerDatabase.keepSynced(true);

            spinerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                            String areaName = areaSnapshot.child("tipo").getValue(String.class);
                            listMId.add(areaName);
                        }

                        ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(Pagos_Citas.this, android.R.layout.simple_spinner_item, listMId);
                        areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spTipoPaggo.setAdapter(areasAdapter);

                    } catch (Exception e) {
                        Toast.makeText(Pagos_Citas.this, "Error al cargar la data", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(Pagos_Citas.this, "Error de la base de datos", Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            Toast.makeText(Pagos_Citas.this, "Error de la base de datos", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                try {
                    Log.i("ActionBar", "Atrás!");
                    finish();
                }catch (Exception e){
                    Toast.makeText(Pagos_Citas.this,"Error al retroceder", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View view) {
        if(view==btnGuardar){
            String pago = spTipoPaggo.getSelectedItem().toString();

            if(pago.equals("Gratis") && edtMonto.getText().toString().length()==0){
                VACIO(edtMonto, ViewFocus);
            }else if(pago.equals("Efectivo y Seguro") && edtMonto.getText().toString().length()==0 && tvSeguro.getText().equals("No tiene")){
                Toast.makeText(Pagos_Citas.this,"Elija otro tipo de pago", Toast.LENGTH_SHORT).show();
            } else if(pago.equals("Seguro") && edtMonto.getText().toString().length()==0 && tvSeguro.getText().equals("No tiene")){
                Toast.makeText(Pagos_Citas.this,"Elija otro tipo de pago", Toast.LENGTH_SHORT).show();
            }else if(pago.equals("Efectivo") && edtMonto.getText().toString().length()==0){
                VACIO(edtMonto, ViewFocus);
            }else if(pago.equals("Efectivo y Seguro") && edtMonto.getText().toString().length()==0 && !tvSeguro.getText().equals("No tiene")){
                VACIO(edtMonto, ViewFocus);
            }else if(pago.equals("Seguro") && edtMonto.getText().toString().length()==0 && !tvSeguro.getText().equals("No tiene")){
                VACIO(edtMonto, ViewFocus);
            }else if(pago.equals("Seguro") && edtSeguro.getText().toString().length()==0 && !tvSeguro.getText().equals("No tiene")){
                    VACIO(edtSeguro, ViewFocus);
            } else if(pago.equals("Seguro") && edtSeguro.getText().toString().length()==0 && tvSeguro.getText().equals("No tiene")){
                Toast.makeText(Pagos_Citas.this,"Elija otro tipo de pago", Toast.LENGTH_SHORT).show();
            }else if(pago.equals("Efectivo") && edtEfectivo.getText().toString().length()==0){
                VACIO(edtEfectivo, ViewFocus);
            }else if(pago.equals("Efectivo y Seguro") && edtEfectivo.getText().toString().length()==0 && tvSeguro.getText().equals("No tiene")){
                Toast.makeText(Pagos_Citas.this,"Elija otro tipo de pago", Toast.LENGTH_SHORT).show();
            }else if(pago.equals("Efectivo y Seguro") && edtEfectivo.getText().toString().length()==0 && !tvSeguro.getText().equals("No tiene")){
                VACIO(edtEfectivo, ViewFocus);
            }else if(pago.equals("Efectivo y Seguro") && edtSeguro.getText().toString().length()==0 && !tvSeguro.getText().equals("No tiene")){
                VACIO(edtSeguro, ViewFocus);
            }else{
                try {
                    String pagos = spTipoPaggo.getSelectedItem().toString();

                    if (pagos.equals("Efectivo")) {
                        int efectivo = Integer.parseInt(edtEfectivo.getText().toString());
                        devolver("Efectivo",efectivo);
                    } else if (pagos.equals("Efectivo y Seguro")) {
                        int efectivo = Integer.parseInt(edtEfectivo.getText().toString());
                        devolver("Efectivo y Seguro",efectivo);
                    } else if (pagos.equals("Seguro")) {
                        int seguro = Integer.parseInt(edtSeguro.getText().toString());
                        devolver("Seguro",seguro);
                    }else if (pagos.equals("Gratis")) {
                        Guardar("Gratis");
                    }
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public  void  devolver(String tipo,int dinero){
        int monto = Integer.parseInt(edtMonto.getText().toString());
        int devolver;

        if(tipo.equals("Efectivo") || tipo.equals("Seguro")) {
            devolver = dinero - monto;
            String datos = String.valueOf(devolver);
            tvDevolver.setVisibility(View.VISIBLE);
            tvDevolver.setText("La cantidad a devolver es: RD$ " + datos);
            if (devolver == 0) {
                Guardar(datos);
            } else if (dinero < monto) {
                tvDevolver.setText("Faltan : RD$ " + datos);
            } else {
                Guardar(datos);
            }
        }else if(tipo.equals("Efectivo y Seguro")){
            int seguro = Integer.parseInt(edtSeguro.getText().toString());
            int cantidad= dinero+seguro;
            devolver = cantidad - monto;
            String datos = String.valueOf(devolver);
            tvDevolver.setVisibility(View.VISIBLE);
            tvDevolver.setText("La cantidad a devolver es: RD$ " + datos);
            if(devolver ==0){
                Guardar(datos);
            }else if(cantidad < monto) {
                tvDevolver.setText("Faltan : RD$ " + datos);
            }else{
                Guardar(datos);
            }
        }
    }

    public void Guardar(final String cantidad){
        if(cantidad.equals("Gratis")){
            new android.app.AlertDialog.Builder(Pagos_Citas.this)
                    .setTitle("Esta todo listo!!")
                    .setCancelable(false)
                    .setMessage("La cita sera gratis \n\n"+"Desea continuar?")
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            enviarDatos(cantidad);
                        }
                    }).show();
        }else{
            new android.app.AlertDialog.Builder(Pagos_Citas.this)
                    .setTitle("Esta todo listo!!")
                    .setCancelable(false)
                    .setMessage("La cantidad a devolver es: \nRD$ " +cantidad+"\n\n"+
                            "Realizar el pago de la cita?")
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            enviarDatos(cantidad);
                        }
                    }).show();
        }
    }

    public void enviarDatos(String cantidad){
        String monto=edtMonto.getText().toString();
        String efectivo=edtEfectivo.getText().toString();
        String seguro=edtSeguro.getText().toString();
        String pago= spTipoPaggo.getSelectedItem().toString();

        mRegProgress.setTitle("Relizando Pago");
        mRegProgress.setMessage("Espere mientras se realiza el pago!");
        mRegProgress.setCanceledOnTouchOutside(false);
        mRegProgress.setCancelable(false);
        mRegProgress.show();

        DatabaseReference pacientekey = PagosDatabase.child("Pago_Cita").push();

        final String pagopush = pacientekey.getKey();

        final String pagos = "Pago_Cita/" + pagopush;

        Map pago_reg = new HashMap();
        pago_reg.put("id", pagopush.toString());
        pago_reg.put("id_paciente", idPaciente);
        pago_reg.put("monto_cita", monto);
        if(pago.equals("Efectivo")){
            pago_reg.put("monto_efectivo",efectivo);
            pago_reg.put("monto_seguro", "0");
        }else  if(pago.equals("Efectivo y Seguro")){
            pago_reg.put("monto_efectivo",efectivo);
            pago_reg.put("monto_seguro", seguro);
        }else  if(pago.equals("Seguro")){
            pago_reg.put("monto_efectivo","0");
            pago_reg.put("monto_seguro", seguro);
        }else  if(pago.equals("Gratis")){
            pago_reg.put("monto_efectivo","0");
            pago_reg.put("monto_seguro", "0");
        }
        pago_reg.put("monto_devuelto", cantidad);
        pago_reg.put("tipo", pago);

        Map pago_agregado = new HashMap();
        pago_agregado.put(pagos, pago_reg);

        PagosDatabase.updateChildren(pago_agregado, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    mRegProgress.hide();
                    Toast.makeText(Pagos_Citas.this, "Fallo el registrar. Por favor intentelo de nuevo.", Toast.LENGTH_SHORT).show();
                } else {
                    citassDatabase.child(idEstado).child("estado").setValue("Pagado").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                citassDatabase.child(idEstado).child("id_pago").setValue(pagopush.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            mRegProgress.dismiss();
                                            finish();
                                            Intent intent = new Intent(getApplicationContext(), detallePago.class);
                                            intent.putExtra("id_Pago", pagopush.toString());
                                            intent.putExtra("id_CITA", idEstado);
                                            intent.putExtra("id", idPaciente);
                                            startActivity(intent);
                                            Toast.makeText(Pagos_Citas.this, "Pago realizado", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(Pagos_Citas.this, "Error.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(Pagos_Citas.this, "Error.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    public  void VACIO(EditText campo, View vista){
        try {
            campo.setError("No se puede dejar el campo vacio");
            vista = campo;
        }catch (Exception e){
            Toast.makeText(Pagos_Citas.this,"Error al verificar!", Toast.LENGTH_SHORT).show();
        }
    }
}