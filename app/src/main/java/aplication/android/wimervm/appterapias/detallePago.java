package aplication.android.wimervm.appterapias;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class detallePago extends AppCompatActivity {

    private DatabaseReference pacientesDatabase,citassDatabase,PagoDatabase;
    private TextView tvNombrep,tvpseguro,tvpnss,tvProcedP,tvpPago,tvMcita,tvFcita,tvMseguro,tvDveu,tvpfecha,tvPhora,tvAfiliado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_detalle_pago);

            final Toolbar toolbar = (Toolbar) findViewById(R.id.mytoolbar6);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            Intent i = this.getIntent();
            String idPago = i.getExtras().getString("id_Pago");
            String idEstado = i.getExtras().getString("id_CITA");
            String idPaciente = i.getExtras().getString("id");

            tvNombrep = (TextView) findViewById(R.id.tvNombrep);
            tvpseguro = (TextView) findViewById(R.id.tvSegurop);
            tvpnss = (TextView) findViewById(R.id.tvNssp);
            tvProcedP = (TextView) findViewById(R.id.tvProcedP);
            tvAfiliado = (TextView) findViewById(R.id.tvAfiliado);
            tvpPago = (TextView) findViewById(R.id.tvpPago);
            tvMcita = (TextView) findViewById(R.id.tvMcita);
            tvFcita = (TextView) findViewById(R.id.tvFcita);
            tvMseguro = (TextView) findViewById(R.id.tvMseguro);
            tvDveu = (TextView) findViewById(R.id.tvDveu);
            tvpfecha = (TextView) findViewById(R.id.tvpfecha);
            tvPhora = (TextView) findViewById(R.id.tvPhora);

            pacientesDatabase = FirebaseDatabase.getInstance().getReference().child("Pacientes").child(idPaciente);
            pacientesDatabase.keepSynced(true);

            citassDatabase = FirebaseDatabase.getInstance().getReference().child("Citas_Reservadas").child(idEstado);
            citassDatabase.keepSynced(true);

            PagoDatabase = FirebaseDatabase.getInstance().getReference().child("Pago_Cita").child(idPago);
            PagoDatabase.keepSynced(true);

            pacientesDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.child("nombre").getValue().toString();
                    String apellido = dataSnapshot.child("apellido").getValue().toString();
                    String seguro = dataSnapshot.child("seguro").getValue().toString();
                    String nss = dataSnapshot.child("Nss").getValue().toString();

                    tvNombrep.setText(name + " " + apellido);
                    tvpseguro.setText(seguro);
                    tvpnss.setText(nss);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            citassDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String fecha = dataSnapshot.child("fecha").getValue().toString();
                    String hora = dataSnapshot.child("hora").getValue().toString();
                    String procedimiento = dataSnapshot.child("procedimiento").getValue().toString();

                    tvPhora.setText(hora);
                    tvpfecha.setText(fecha);
                    tvProcedP.setText(procedimiento);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            PagoDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String tipo = dataSnapshot.child("tipo").getValue().toString();
                    String monto_cita = dataSnapshot.child("monto_cita").getValue().toString();
                    String monto_devuelto = dataSnapshot.child("monto_devuelto").getValue().toString();
                    String monto_efectivo = dataSnapshot.child("monto_efectivo").getValue().toString();
                    String monto_seguro = dataSnapshot.child("monto_seguro").getValue().toString();
                    String num_afiliado = dataSnapshot.child("numero_afiliado").getValue().toString();


                    tvpPago.setText(tipo);
                    tvMcita.setText(monto_cita);
                    tvFcita.setText(monto_efectivo);
                    tvMseguro.setText(monto_seguro);
                    tvDveu.setText(monto_devuelto);
                    tvAfiliado.setText(num_afiliado);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"Error al abrir",Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(detallePago.this,"Error al retroceder", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
