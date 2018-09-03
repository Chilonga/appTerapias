    package aplication.android.wimervm.appterapias;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Reservar_Cita extends AppCompatActivity {

    private DatabaseReference pacientesDatabase;
    private DatabaseReference CitaDatabase;
    FirebaseAuth mAuth;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView mUsersList;
    MaterialSearchView searchView;
    private TextView txtResultado;
    String online_user_id;
    private DatabaseReference spinerDatabase;
    private TextView tVNombre,edtFecha;
    private Spinner consultaSpiner,HorarioSpiner;
    final Calendar calendario = Calendar.getInstance();
    String fecha_calendario,fecha_seleccionada,fecha_selecc;
    int año=calendario.get(Calendar.YEAR);
    int mes=calendario.get(Calendar.MONTH)+1;
    int mesC=calendario.get(Calendar.MONTH);
    int dias=calendario.get(Calendar.DAY_OF_MONTH);
    private ProgressDialog mRegProgress;
    private String fecha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccionar_paciente_cita);

        fecha_calendario= String.valueOf(dias+"/"+mes+"/"+año);

        fecha_selecc= String.valueOf(dias+"/"+mesC+"/"+año);

        mRegProgress = new ProgressDialog(this);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.mytoolbar3);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        online_user_id = mAuth.getCurrentUser().getUid();

        pacientesDatabase = FirebaseDatabase.getInstance().getReference().child("Pacientes");
        pacientesDatabase.keepSynced(true);

        Intent i = this.getIntent();
        fecha = i.getExtras().getString("fecha");

        CitaDatabase = FirebaseDatabase.getInstance().getReference();
        CitaDatabase.keepSynced(true);

        txtResultado = (TextView) findViewById(R.id.textViewResultados8);

        searchView=(MaterialSearchView)findViewById(R.id.search_view);

        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager = new LinearLayoutManager(this);
        mUsersList = (RecyclerView) findViewById(R.id.recicler_pacientes_seleccion);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(mLayoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();

        buscar("lista");

    }

    @Override
    protected void onResume() {
        super.onResume();

        final Query query=pacientesDatabase;

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    txtResultado.setVisibility(View.INVISIBLE);
                }else{
                    txtResultado.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        try {
            if (searchView.isSearchOpen()) {
                searchView.closeSearch();
            } else {
                super.onBackPressed();
            }
        }catch (Exception e){
            Toast.makeText(Reservar_Cita.this,"Error!!!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.pantalla__principa, menu);
        MenuItem item=menu.findItem(R.id.buscar);
        searchView.setMenuItem(item);
        searchView.setHint("Ingrese el nombre del paciente");
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                buscar(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                buscar(query);
                return true;
            }
        });
        return true;
    }

    protected void buscar(String texto) {
        try {
            final Query query;
            if(texto.equals("lista")){
               query=pacientesDatabase;

            }else{
                query=pacientesDatabase.orderByChild("nombre").startAt(texto).endAt(texto+"\uf8ff");
            }

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        txtResultado.setVisibility(View.INVISIBLE);
                    }else{
                        txtResultado.setText("No se encontraron resultados");
                        txtResultado.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            FirebaseRecyclerAdapter<Pacientes, Reservar_Cita.UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Pacientes, Reservar_Cita.UsersViewHolder>(
                    Pacientes.class,
                    R.layout.pacientes,
                    Reservar_Cita.UsersViewHolder.class,
                    query
            )  {
                @Override
                protected void populateViewHolder(final Reservar_Cita.UsersViewHolder usersViewHolder, final Pacientes users, final int position) {
                    usersViewHolder.setNombre(users.getNombre()+ " "+users.getApellido());
                    usersViewHolder.setCedula(users.getCedula());

                    usersViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                InputMethodManager imm = (InputMethodManager) getApplication().getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                                Reservar_Cita(users.getNombre()+ " "+users.getApellido(),users.getId());
                            }catch (Exception e){
                                Toast.makeText(Reservar_Cita.this, e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            };
            mUsersList.setAdapter(firebaseRecyclerAdapter);

        }catch (Exception e){
            Toast.makeText(Reservar_Cita.this, "Error al mostrar!!! ", Toast.LENGTH_SHORT).show();
        }
    }

    public void Reservar_Cita(String nombreyApellido, final String idPaciente){
        try{
            LayoutInflater inflater = getLayoutInflater();
            final View dialoglayout = inflater.inflate(R.layout.activity_reservar_citas, null);

            tVNombre=(TextView)dialoglayout.findViewById(R.id.textNombreCita);
            consultaSpiner=(Spinner)dialoglayout.findViewById(R.id.spinnerConsulta);
            HorarioSpiner=(Spinner)dialoglayout.findViewById(R.id.spinnerHorarioCita);
            edtFecha=(TextView) dialoglayout.findViewById(R.id.editTFechaCita);
            Button btnReservar=(Button) dialoglayout.findViewById(R.id.buttonReservar);

            edtFecha.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fechaCalendario();
                }
            });

            tVNombre.setText(nombreyApellido);

            final String id=idPaciente;

            btnReservar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String hora = HorarioSpiner.getSelectedItem().toString();
                    String procedimiento = consultaSpiner.getSelectedItem().toString();
                    String fecha = edtFecha.getText().toString();
                    String myFecha=fecha_selecc;

                    mRegProgress.setTitle("Reservando cita");
                    mRegProgress.setMessage("Espere mientras se reserva la cita!");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.setCancelable(false);
                    mRegProgress.show();
                    Agregar_cita(id,hora,procedimiento,fecha,myFecha);
                }
            });

            final AlertDialog.Builder builder = new AlertDialog.Builder(Reservar_Cita.this);
            builder.setView(dialoglayout);
            builder.setCancelable(false);
            builder.setNeutralButton("Cerrar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();

            edtFecha.setText(fecha);
            spinerDatos("hora",HorarioSpiner,"Horario");
            spinerDatos("tipo",consultaSpiner,"Tipo_Consulta");

        }catch (Exception e){
            Toast.makeText(Reservar_Cita.this,e.toString(),Toast.LENGTH_LONG).show();
        }
    }

    private void Agregar_cita(String idPaciente, String hora, String procedimiento,String fecha,String fechaCalendar){
        try {
            DatabaseReference clientekey = CitaDatabase.child("Citas_Reservadas").push();

            String clientepush = clientekey.getKey();

            final String pacientes = "Citas_Reservadas/" + clientepush;

            Map cita_reg = new HashMap();
            cita_reg.put("id", clientepush.toString());
            cita_reg.put("id_paciente", idPaciente);
            cita_reg.put("hora", hora);
            cita_reg.put("procedimiento", procedimiento);
            cita_reg.put("fecha", fecha);
            cita_reg.put("fecha_calendario", fechaCalendar);
            cita_reg.put("estado", "Activo");

            Map cita_agregado = new HashMap();
            cita_agregado.put(pacientes, cita_reg);

            CitaDatabase.updateChildren(cita_agregado, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        mRegProgress.hide();
                        Toast.makeText(Reservar_Cita.this, "Fallo al reservar la cita. Por favor intentelo de nuevo.", Toast.LENGTH_SHORT).show();
                    } else {
                        mRegProgress.dismiss();
                        Toast.makeText(Reservar_Cita.this, "Cita reservada", Toast.LENGTH_SHORT).show();
                       // Intent intent = getIntent();
                       // finish();
                        //intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        //startActivity(intent);
                    }
                }
            });
        }catch (Exception e){
            Toast.makeText(Reservar_Cita.this,"Error al guardar!", Toast.LENGTH_SHORT).show();
        }
    }

    public void spinerDatos(final String tipo, final Spinner spinner,String child){
        try {

            spinerDatabase = FirebaseDatabase.getInstance().getReference().child(child);
            spinerDatabase.keepSynced(true);

            spinerDatabase.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        final List<String> areas = new ArrayList<String>();

                        for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                            String areaName = areaSnapshot.child(tipo).getValue(String.class);
                            areas.add(areaName);
                        }

                        ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(Reservar_Cita.this, android.R.layout.simple_spinner_item, areas);
                        areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(areasAdapter);

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

    public void fechaCalendario(){
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
                    fecha_selecc=dayOfMonth+"/"+month+"/"+year;
                }
            });

            calendarView.setDate(new SimpleDateFormat("dd/MM/yyyy").parse(edtFecha.getText().toString()).getTime(), true, true);

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
            case R.id.agregar_pacientes: {
                Intent i=new Intent(getApplicationContext(), Agregar_Pacientes.class);
                startActivity(i);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setCedula(String cedula){
            TextView userStatusView = (TextView) mView.findViewById(R.id.txtCedulaPacientes);
            userStatusView.setText(cedula);
        }

        public void setNombre(String nombre){
            TextView userStatusView = (TextView) mView.findViewById(R.id.txtPaciente);
            userStatusView.setText(nombre);
        }
    }
}