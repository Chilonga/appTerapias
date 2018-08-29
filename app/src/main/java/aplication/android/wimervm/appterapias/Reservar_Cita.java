package aplication.android.wimervm.appterapias;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Reservar_Cita extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference mPacienteDatabase;
    private DatabaseReference consultaDatabase,HorarioDatabase;
    private TextView tVNombre;
    private Spinner consultaSpiner,HorarioSpiner;
    final Calendar calendario = Calendar.getInstance();
    String fecha_calendario,fecha_seleccionada;
    int año=calendario.get(Calendar.YEAR);
    int mes=calendario.get(Calendar.MONTH)+1;
    int dias=calendario.get(Calendar.DAY_OF_MONTH);
    private EditText edtFecha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservar_citas);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.mytoolbar4);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final String user_id = getIntent().getStringExtra("user_id");

        mPacienteDatabase = FirebaseDatabase.getInstance().getReference().child("Pacientes").child(user_id);
        mPacienteDatabase.keepSynced(true);

        consultaDatabase = FirebaseDatabase.getInstance().getReference().child("Tipo_Consulta");
        consultaDatabase.keepSynced(true);

        HorarioDatabase = FirebaseDatabase.getInstance().getReference().child("Horario");
        HorarioDatabase.keepSynced(true);

        tVNombre=(TextView)findViewById(R.id.textNombreCita);

        consultaSpiner=(Spinner)findViewById(R.id.spinnerConsulta);
        HorarioSpiner=(Spinner)findViewById(R.id.spinnerHorarioCita);

        edtFecha=(EditText) findViewById(R.id.editTFechaCita);

        fecha_calendario= String.valueOf(dias+"/"+mes+"/"+año);

        edtFecha.setText(fecha_calendario);

        edtFecha.setOnClickListener(this);

        BuscarPaciente();
        spinerConsulta();
        spinerHorario();

    }

    public void spinerHorario(){
        try {
            HorarioDatabase.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        final List<String> areas = new ArrayList<String>();

                        for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                            String areaName = areaSnapshot.child("hora").getValue(String.class);
                            areas.add(areaName);
                        }

                        ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(Reservar_Cita.this, android.R.layout.simple_spinner_item, areas);
                        areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        HorarioSpiner.setAdapter(areasAdapter);

                    } catch (Exception e) {
                        Toast.makeText(Reservar_Cita.this, "Error al cargar la data", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(Reservar_Cita.this, "Error de la base de datos", Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            Toast.makeText(Reservar_Cita.this, "Error de la base de datos", Toast.LENGTH_SHORT).show();
        }
    }


    public void spinerConsulta(){
        try {
            consultaDatabase.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        final List<String> areas = new ArrayList<String>();

                        for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                            String areaName = areaSnapshot.child("tipo").getValue(String.class);
                            areas.add(areaName);
                        }

                        ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(Reservar_Cita.this, android.R.layout.simple_spinner_item, areas);
                        areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        consultaSpiner.setAdapter(areasAdapter);

                    } catch (Exception e) {
                        Toast.makeText(Reservar_Cita.this, "Error al cargar la data", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(Reservar_Cita.this, "Error de la base de datos", Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            Toast.makeText(Reservar_Cita.this, "Error de la base de datos", Toast.LENGTH_SHORT).show();
        }
    }

    private void BuscarPaciente(){

        mPacienteDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("nombre").getValue().toString();
                String apellido = dataSnapshot.child("apellido").getValue().toString();

                tVNombre.setText(name+" "+apellido);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void fechaCalendar(){
        try{
            LayoutInflater inflater = getLayoutInflater();
            final View dialoglayout = inflater.inflate(R.layout.fecha_calendar, null);

            final CalendarView calendarView;

            calendarView=(CalendarView) dialoglayout.findViewById(R.id.calendarView_fecha);


            calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                @Override
                public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                    int monthh=month+1;
                    fecha_seleccionada=dayOfMonth+"/"+monthh+"/"+year;
                }
            });

            AlertDialog.Builder builder = new AlertDialog.Builder(Reservar_Cita.this);
            builder.setView(dialoglayout);

            builder.setView(dialoglayout)
                    .setPositiveButton("Aceptar",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            edtFecha.setText(fecha_seleccionada);
                            dialog.cancel();
                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            builder.show();

        }catch (Exception e){
            Toast.makeText(Reservar_Cita.this,"Error al abrir la fecha",Toast.LENGTH_LONG).show();
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
                    Toast.makeText(Reservar_Cita.this,"Error al retroceder", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        if(v==edtFecha){
            fechaCalendar();
        }
    }
}