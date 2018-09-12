package aplication.android.wimervm.appterapias;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Pagos_Citas extends AppCompatActivity {

    private DatabaseReference pacientesDatabase,spinerDatabase;
    private TextView tvNombre,tvNss,tvSeguro;
    private Spinner spTipoPaggo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagos__citas);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.mytoolbar5);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = this.getIntent();
        String id = i.getExtras().getString("id_CITA");
        String id_PACIENTE = i.getExtras().getString("id");

        tvNombre=(TextView)findViewById(R.id.tvNombrePago);
        tvSeguro=(TextView)findViewById(R.id.tvSeguroPago);
        tvNss=(TextView)findViewById(R.id.tvNssPago);
        spTipoPaggo=(Spinner)findViewById(R.id.spinnerTipoPago);

        pacientesDatabase = FirebaseDatabase.getInstance().getReference().child("Pacientes").child(id_PACIENTE);
        pacientesDatabase.keepSynced(true);

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

        spinerPago();
    }


    public void spinerPago(){
        try {

            spinerDatabase = FirebaseDatabase.getInstance().getReference().child("tipo_de_pago");
            spinerDatabase.keepSynced(true);

            spinerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        final List<String> areas = new ArrayList<String>();

                        for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                            String areaName = areaSnapshot.child("tipo").getValue(String.class);
                            areas.add(areaName);
                        }

                        ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(Pagos_Citas.this, android.R.layout.simple_spinner_item, areas);
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
}
