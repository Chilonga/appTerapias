package aplication.android.wimervm.appterapias;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

public class Pacientes_lista extends AppCompatActivity {

    private DatabaseReference pacientesDatabase;
    FirebaseAuth mAuth;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView mUsersList;
    private TextView txtResultado;
    String online_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_pacientes_lista);

            final Toolbar toolbar = (Toolbar) findViewById(R.id.mytoolbar3);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            mAuth = FirebaseAuth.getInstance();
            online_user_id = mAuth.getCurrentUser().getUid();

            pacientesDatabase = FirebaseDatabase.getInstance().getReference().child("Pacientes");
            pacientesDatabase.keepSynced(true);

            txtResultado = (TextView) findViewById(R.id.textViewResultados7);

            mLayoutManager = new LinearLayoutManager(this);
            mLayoutManager = new LinearLayoutManager(this);
            mUsersList = (RecyclerView) findViewById(R.id.recicler_pacientes);
            mUsersList.setHasFixedSize(true);
            mUsersList.setLayoutManager(mLayoutManager);

            FloatingActionButton floatAgregar=(FloatingActionButton)findViewById(R.id.fbRegistrar_pacientes);
            floatAgregar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(getApplicationContext(),Agregar_Pacientes.class);
                    startActivity(i);
                }
            });

        }catch (Exception e){
            Toast.makeText(getApplicationContext(), e.toString(),Toast.LENGTH_LONG).show();
        }
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

    private void buscador(String texto){
        try {

            final Query query=pacientesDatabase.orderByChild("nombre").startAt(texto).endAt(texto+"\uf8ff");

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

            FirebaseRecyclerAdapter<Pacientes, Pacientes_lista.UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Pacientes, UsersViewHolder>(
                    Pacientes.class,
                    R.layout.pacientes,
                    Pacientes_lista.UsersViewHolder.class,
                    query
            )  {
                @Override
                protected void populateViewHolder(final Pacientes_lista.UsersViewHolder usersViewHolder, final Pacientes users, final int position) {
                    usersViewHolder.setNombre(users.getNombre()+ " "+users.getApellido());
                    usersViewHolder.setCedula(users.getCedula());

                    usersViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //      Intent intent = new Intent(getApplicationContext(), Detalle_Cliente.class);
                            //     intent.putExtra("id", users.getId());
                            //   startActivity(intent);
                        }
                    });
                }
            };
            mUsersList.setAdapter(firebaseRecyclerAdapter);
        }catch (Exception e){
            Toast.makeText(Pacientes_lista.this, "Error al mostrar!!! ", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {

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

            FirebaseRecyclerAdapter<Pacientes, Pacientes_lista.UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Pacientes, UsersViewHolder>(
                    Pacientes.class,
                    R.layout.pacientes,
                    Pacientes_lista.UsersViewHolder.class,
                    query
            )  {
                @Override
                protected void populateViewHolder(final Pacientes_lista.UsersViewHolder usersViewHolder, final Pacientes users, final int position) {
                    usersViewHolder.setNombre(users.getNombre()+ " "+users.getApellido());
                    usersViewHolder.setCedula(users.getCedula());
                }
            };
            mUsersList.setAdapter(firebaseRecyclerAdapter);
        }catch (Exception e){
            Toast.makeText(Pacientes_lista.this, "Error al mostrar!!! ", Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.buscar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                try {
                    Log.i("ActionBar", "Atrás!");
                    finish();
                }catch (Exception e){
                    Toast.makeText(Pacientes_lista.this,"Error al retroceder", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}