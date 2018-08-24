package aplication.android.wimervm.appterapias;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import javax.security.auth.callback.Callback;

import de.hdodenhof.circleimageview.CircleImageView;

public class Pantalla_Principal extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference mMedicoDatabase;
    private FirebaseUser mCurrentUser;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla__principal);

        mAuth = FirebaseAuth.getInstance();

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();

        mMedicoDatabase = FirebaseDatabase.getInstance().getReference().child("Medicos").child(current_uid);
        mMedicoDatabase.keepSynced(true);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onStart() {
        try{
            super.onStart();
            currentUser=mAuth.getCurrentUser();
            if(currentUser==null){
                sendToStart();
            }else if(currentUser!=null){

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
                        Toast.makeText(Pantalla_Principal.this, "Error de la base de datos", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }catch (Exception e){
            Toast.makeText(Pantalla_Principal.this,"Error al abrir la app", Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.pantalla__principa, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendToStart() {
        try {
                Intent startintent = new Intent(Pantalla_Principal.this, Login.class);
            startActivity(startintent);
            finish();
        }catch (Exception e){
            Toast.makeText(Pantalla_Principal.this,"Error al abrir la app", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_sesion) {
            try {
                new AlertDialog.Builder(Pantalla_Principal.this)
                        .setTitle("Cerrar Sesion")
                        .setMessage("Desea cerrar la sesion?")
                        .setPositiveButton("si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().signOut();
                                sendToStart();
                                Toast.makeText(Pantalla_Principal.this, "Se ah cerrado la sesion.", Toast.LENGTH_SHORT).show();
                            }
                        }).setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
            } catch (Exception e){
                Toast.makeText(Pantalla_Principal.this,"Error al cerrar sesion", Toast.LENGTH_SHORT).show();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}