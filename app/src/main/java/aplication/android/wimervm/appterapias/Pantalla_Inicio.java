package aplication.android.wimervm.appterapias;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class Pantalla_Inicio extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference mMedicoDatabase;
    private DatabaseReference PacienteDatabase;
    private FirebaseUser mCurrentUser;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;
    private DatabaseReference citassDatabase;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView mUsersList;
    private TextView txtResultado;
    String online_user_id;
    private CalendarView calendarView;
    final Calendar calendario = Calendar.getInstance();
    String fecha_calendario;
    int año=calendario.get(Calendar.YEAR);
    int mes=calendario.get(Calendar.MONTH);
    int dias=calendario.get(Calendar.DAY_OF_MONTH);
    private ProgressDialog mCambioProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla__inicio);

        mAuth = FirebaseAuth.getInstance();

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();

        mMedicoDatabase = FirebaseDatabase.getInstance().getReference().child("Medicos").child(current_uid);
        mMedicoDatabase.keepSynced(true);

        citassDatabase = FirebaseDatabase.getInstance().getReference().child("Citas_Reservadas");
        citassDatabase.keepSynced(true);

        mCambioProgress = new ProgressDialog(Pantalla_Inicio.this);
        mCambioProgress.setTitle("Actualizando cita");
        mCambioProgress.setMessage("Espere mientras se actualizan los datos !");
        mCambioProgress.setCancelable(false);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.MyToolbar);
        setSupportActionBar(toolbar);

        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapse_toolbar);
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
        collapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);
        //Color de la barra
        Context context = this;
        collapsingToolbarLayout.setContentScrimColor(ContextCompat.getColor(context, R.color.colorPrimary));

        AppBarLayout appBarLayout=(AppBarLayout)findViewById(R.id.MyAppbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset)-appBarLayout.getTotalScrollRange() == 0)
                {
                    collapsingToolbarLayout.setTitle("Terapias");
                }
                else
                {
                    collapsingToolbarLayout.setTitle(" ");
                }
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),Reservar_Cita.class);
                startActivity(i);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager = new LinearLayoutManager(this);
        mUsersList = (RecyclerView) findViewById(R.id.reciclercitas);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(mLayoutManager);

        txtResultado=(TextView)findViewById(R.id.textViewResultados12);
        calendarView=(CalendarView) findViewById(R.id.calendarView);

        fecha_calendario= String.valueOf(dias+"/"+mes+"/"+año);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                fecha_calendario=dayOfMonth+"/"+month+"/"+year;
                Citas(fecha_calendario);

            }
        });
    }

    protected void Citas(String fecha) {
        try {

            final Query query=citassDatabase.orderByChild("fecha_calendario").startAt(fecha).endAt(fecha+"\uf8ff");;

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

            FirebaseRecyclerAdapter<citas, Pantalla_Inicio.UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<citas, Pantalla_Inicio.UsersViewHolder>(
                    citas.class,
                    R.layout.rcv_citas,
                    Pantalla_Inicio.UsersViewHolder.class,
                    query
            )  {
                @Override
                protected void populateViewHolder(final Pantalla_Inicio.UsersViewHolder usersViewHolder, final citas users, final int position) {
                    usersViewHolder.setHora(users.getFecha());
                    usersViewHolder.setProcedimiento(users.getProcedimiento());
                    usersViewHolder.setEstado(users.getEstado());
                    String id=users.getId_paciente();

                    PacienteDatabase = FirebaseDatabase.getInstance().getReference().child("Pacientes").child(id);
                    PacienteDatabase.keepSynced(true);

                    PacienteDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String name = dataSnapshot.child("nombre").getValue().toString();
                            String apellido = dataSnapshot.child("apellido").getValue().toString();

                            usersViewHolder.setNombre(name+ " "+apellido);

                            usersViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(getApplicationContext(), Pagos_Citas.class);
                                    intent.putExtra("id", users.getId());
                                    startActivity(intent);
                                }
                            });

                            usersViewHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View view) {
                                    final String estado= users.getEstado();

                                    if(estado.equals("Activo")){
                                        new AlertDialog.Builder(Pantalla_Inicio.this)
                                                .setTitle("Cancelar cita")
                                                .setCancelable(false)
                                                .setMessage("Desea cancelar la cita?")
                                                .setPositiveButton("si", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(final DialogInterface dialog, int which) {

                                                        mCambioProgress.show();

                                                        citassDatabase.child(users.getId()).child("estado").setValue("Cancelado").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            mCambioProgress.dismiss();
                                                                            dialog.cancel();
                                                                        }else{
                                                                            mCambioProgress.hide();
                                                                            Toast.makeText(Pantalla_Inicio.this, "Necisita conexion a internet para poder iniciar sesion.", Toast.LENGTH_SHORT).show();
                                                                        }

                                                            }
                                                        });
                                                    }
                                                })
                                                .setNegativeButton("no", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(final DialogInterface dialog, int which) {
                                                        dialog.cancel();
                                                    }
                                                }).show();
                                    }else if(estado.equals("Cancelado")){
                                            new AlertDialog.Builder(Pantalla_Inicio.this)
                                                    .setTitle("La cita esta cancelada")
                                                    .setMessage("Desea activarla?")
                                                    .setPositiveButton("si", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(final DialogInterface dialog, int which) {

                                                            mCambioProgress.show();

                                                            citassDatabase.child(users.getId()).child("estado").setValue("Activo").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        mCambioProgress.dismiss();
                                                                        dialog.cancel();
                                                                    } else {
                                                                        mCambioProgress.hide();
                                                                        Toast.makeText(Pantalla_Inicio.this, "Necisita conexion a internet para poder iniciar sesion.", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                            dialog.cancel();
                                                        }
                                                    })
                                                    .setNegativeButton("no", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.cancel();
                                                        }
                                                    }).show();
                                    }
                                    return true;
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    usersViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });
                }
            };
            mUsersList.setAdapter(firebaseRecyclerAdapter);
        }catch (Exception e){
            Toast.makeText(Pantalla_Inicio.this, "Error al mostrar!!! ", Toast.LENGTH_SHORT).show();
        }
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setHora(String hora){
            TextView userStatusView = (TextView) mView.findViewById(R.id.txt_fecha);
            userStatusView.setText(hora);
        }

        public void setNombre(String nombre){
            TextView userStatusView = (TextView) mView.findViewById(R.id.txtPaciente);
            userStatusView.setText(nombre);
        }

        public void setEstado(String estado){
            TextView userStatusView = (TextView) mView.findViewById(R.id.textViewEstado);
            if(estado.equals("Cancelado")){
                userStatusView.setVisibility(View.VISIBLE);
                userStatusView.setText(estado);
            }else if (estado.equals("Activo")){
                userStatusView.setVisibility(View.GONE);
            }
        }


        public void setProcedimiento(String Procedimiento){
            TextView userStatusView = (TextView) mView.findViewById(R.id.txtProcedimiento);
            userStatusView.setText(Procedimiento);
        }
    }

    @Override
    public void onStart() {
        try{
            super.onStart();
            currentUser=mAuth.getCurrentUser();
            if(currentUser==null){
                sendToStart();
            }else if(currentUser!=null){
                Citas(fecha_calendario);
                mMedicoDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String name = dataSnapshot.child("nombre").getValue().toString();
                        String apellido = dataSnapshot.child("apellido").getValue().toString();
                        String correo = dataSnapshot.child("correo").getValue().toString();

                        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                        View headerView = navigationView.getHeaderView(0);

                        TextView tvCorreo = (TextView) headerView.findViewById(R.id.tvCorreoHeader);
                        TextView tvNombre= (TextView) headerView.findViewById(R.id.tvNombreHeader);

                        tvNombre.setText(name+" "+apellido);
                        tvCorreo.setText(correo);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(Pantalla_Inicio.this, "Error de la base de datos", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }catch (Exception e){
            Toast.makeText(Pantalla_Inicio.this,"Error al abrir la app", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void sendToStart() {
        try {
            Intent startintent = new Intent(Pantalla_Inicio.this, Login.class);
            startActivity(startintent);
            finish();
        }catch (Exception e){
            Toast.makeText(Pantalla_Inicio.this,"Error al abrir la app", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_sesion) {
            try {
                new AlertDialog.Builder(Pantalla_Inicio.this)
                        .setTitle("Cerrar Sesion")
                        .setMessage("Desea cerrar la sesion?")
                        .setPositiveButton("si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().signOut();
                                sendToStart();
                                Toast.makeText(Pantalla_Inicio.this, "Se ah cerrado la sesion.", Toast.LENGTH_SHORT).show();
                            }
                        }).setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
            } catch (Exception e){
                Toast.makeText(Pantalla_Inicio.this,"Error al cerrar sesion", Toast.LENGTH_SHORT).show();
            }
        }else  if (id == R.id.nav_Pacientes) {
            Intent i=new Intent(getApplicationContext(), Pacientes_lista.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}