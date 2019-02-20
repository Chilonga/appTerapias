package aplication.android.wimervm.appterapias;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;

public class detallePaciente extends AppCompatActivity {

    private DatabaseReference pacienteDatabase,TutorDatabase;
    private TextView tvNombre,tvCedula,tvSexo,tvSeguro,tvNss,tvCorreo,tvTelefono;
    private Button btnTutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_paciente);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.mytoolbar12);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = this.getIntent();
        String  idPaciente = i.getExtras().getString("id");

        pacienteDatabase = FirebaseDatabase.getInstance().getReference().child("Pacientes").child(idPaciente);
        pacienteDatabase.keepSynced(true);

        TutorDatabase = FirebaseDatabase.getInstance().getReference().child("Tutor");
        TutorDatabase.keepSynced(true);

        tvNombre=(TextView)findViewById(R.id.tvPacientede);
        tvCedula=(TextView)findViewById(R.id.tvCedulade);
        tvSexo=(TextView)findViewById(R.id.tvSexode);
        tvSeguro=(TextView)findViewById(R.id.tvSegurode);
        tvNss=(TextView)findViewById(R.id.tvNsssde);
        tvCorreo=(TextView)findViewById(R.id.tvCorreode);
        tvTelefono=(TextView)findViewById(R.id.tvTelefonode);
        btnTutor=(Button)findViewById(R.id.btnTutor);

        final TextView textcedula=(TextView)findViewById(R.id.textView14);
        final TextView texttutor=(TextView)findViewById(R.id.textView9);

        pacienteDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("nombre").getValue().toString();
                String apellido = dataSnapshot.child("apellido").getValue().toString();
                String cedula = dataSnapshot.child("cedula").getValue().toString();
                String sexo = dataSnapshot.child("sexo").getValue().toString();
                String seguro = dataSnapshot.child("seguro").getValue().toString();
                String nss = dataSnapshot.child("Nss").getValue().toString();
                String correo = dataSnapshot.child("correo").getValue().toString();
                String telefono = dataSnapshot.child("telefono").getValue().toString();
                final String id_tutor = dataSnapshot.child("id_tutor").getValue().toString();

                tvNombre.setText(name+" "+apellido);
                if(cedula.equals("No tiene") && !id_tutor.equals("No tiene")){
                    TutorDatabase.child(id_tutor).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String nom = dataSnapshot.child("nombre").getValue().toString();
                            String ape = dataSnapshot.child("apellido").getValue().toString();
                            textcedula.setVisibility(View.GONE);
                            texttutor.setVisibility(View.VISIBLE);
                            tvCedula.setText(nom+" "+ape );
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    btnTutor.setVisibility(View.VISIBLE);
                    btnTutor.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            tutor(id_tutor);
                        }
                    });
                }else{
                    tvCedula.setText(cedula);
                }
                tvSexo.setText(sexo);
                tvSeguro.setText(seguro);
                tvNss.setText(nss);
                tvCorreo.setText(correo);
                tvTelefono.setText(telefono);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void tutor(String id){
        try{
            LayoutInflater inflater = getLayoutInflater();
            final View dialoglayout = inflater.inflate(R.layout.detalle_tutor, null);

            AlertDialog.Builder builder = new AlertDialog.Builder(detallePaciente.this);
            builder.setView(dialoglayout);

            final TextView tvNombret=(TextView)dialoglayout.findViewById(R.id.tvNombreTDe);
            final TextView tvCedulat=(TextView)dialoglayout.findViewById(R.id.tvcedulat);
            final TextView tvSexot=(TextView)dialoglayout.findViewById(R.id.tvsexot);
            final TextView tvdirecciont=(TextView)dialoglayout.findViewById(R.id.tvdirecct);
            final TextView tvCorreot=(TextView)dialoglayout.findViewById(R.id.tvcorreot);
            final TextView tvTelefonot=(TextView)dialoglayout.findViewById(R.id.tvtelefonot);

            TutorDatabase.child(id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.child("nombre").getValue().toString();
                    String apellido = dataSnapshot.child("apellido").getValue().toString();
                    String cedula = dataSnapshot.child("cedula").getValue().toString();
                    String sexo = dataSnapshot.child("sexo").getValue().toString();
                    String direccion = dataSnapshot.child("direccion").getValue().toString();
                    String correo = dataSnapshot.child("correo").getValue().toString();
                    String telefono = dataSnapshot.child("telefono").getValue().toString();

                    tvNombret.setText(name+" "+apellido);
                    tvCedulat.setText(cedula);
                    tvSexot.setText(sexo);
                    tvdirecciont.setText(direccion);
                    tvCorreot.setText(correo);
                    tvTelefonot.setText(telefono);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            builder.setView(dialoglayout)
                    .setTitle("Datos del tutor")
                    .setCancelable(false)
                    .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.cancel();
                        }
                    });
            builder.show();

        }catch (Exception e){
            Toast.makeText(detallePaciente.this,"Error al abrir la fecha",Toast.LENGTH_LONG).show();
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
                    Toast.makeText(detallePaciente.this,"Error al retroceder", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
