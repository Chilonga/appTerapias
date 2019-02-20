package aplication.android.wimervm.appterapias;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static aplication.android.wimervm.appterapias.Reservar_Cita.compruebaConexion;

public class Agregar_Pacientes extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference SpinnerData;
    private RadioGroup radioCP;
    private RadioButton radioButtonCP;
    private String radioclickcp="Vacio";
    private EditText edtSeguro,edtCedula,edtNombre,edtApellido,edtCorreo,edtTelefono;
    private Button btnGuardar;
    private Spinner spSeguro,spSexo;
    private ProgressDialog mRegProgress;
    private View ViewFocus = null;
    private DatabaseReference PacienteDatabase;
    private String TutorId="No tiene";
    private CardView cvSeguro;
    private String registrado_tutor="No",id_tutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar__pacientes);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.mytoolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        radioCP=(RadioGroup)findViewById(R.id.radiogroupCP);

        mRegProgress = new ProgressDialog(this);

        PacienteDatabase=FirebaseDatabase.getInstance().getReference();
        PacienteDatabase.keepSynced(true);

        cvSeguro= (CardView) findViewById(R.id.cardviewSeguro);
        edtSeguro=(EditText)findViewById(R.id.editTextSeguro);
        edtCedula=(EditText)findViewById(R.id.editTextCedulaPaciente);
        edtNombre=(EditText)findViewById(R.id.editTextNombrePaciente);
        edtApellido=(EditText)findViewById(R.id.editTextApellidoPaciente);
        edtTelefono=(EditText)findViewById(R.id.edtTelefonoPaciente);
        edtCorreo=(EditText)findViewById(R.id.editTextEmailPaciente);
        btnGuardar=(Button)findViewById(R.id.buttonGuardarPaciente);
        spSeguro= (Spinner) findViewById(R.id.spinnerPaciente);
        spSexo= (Spinner) findViewById(R.id.SpinnerSexo);

        SpinnerData = FirebaseDatabase.getInstance().getReference("Seguros");
        SpinnerData.keepSynced(true);

        final List<String> Listsexo = new ArrayList<String>();
        Listsexo.add("Masculino");
        Listsexo.add("Femenino");

        ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(Agregar_Pacientes.this, android.R.layout.simple_spinner_item, Listsexo);
        areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSexo.setAdapter(areasAdapter);

        spinerSeguros();
        btnGuardar.setOnClickListener(this);
    }

    public void rbclicked(View v){
        try {
            int radiobutton_id = radioCP.getCheckedRadioButtonId();

            radioButtonCP = (RadioButton) findViewById(radiobutton_id);

            radioclickcp = radioButtonCP.getText().toString();

            if (radioclickcp.equals("Si")) {
                cvSeguro.setVisibility(View.VISIBLE);
            } else if (radioclickcp.equals("No")) {
                InputMethodManager imm = (InputMethodManager) getApplication().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edtSeguro.getWindowToken(), 0);
                cvSeguro.setVisibility(View.GONE);
                edtSeguro.setError(null);
                edtSeguro.setText("");
            }
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Error al hacer click", Toast.LENGTH_LONG).show();
        }
    }

    public void spinerSeguros(){
        try {
            SpinnerData.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        final List<String> areas = new ArrayList<String>();

                        for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                            String areaName = areaSnapshot.child("nombre").getValue(String.class);
                            areas.add(areaName);
                        }

                        ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(Agregar_Pacientes.this, android.R.layout.simple_spinner_item, areas);
                        areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spSeguro.setAdapter(areasAdapter);

                    } catch (Exception e) {
                        Toast.makeText(Agregar_Pacientes.this, "Error al cargar la data", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(Agregar_Pacientes.this, "Error de la base de datos", Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            Toast.makeText(Agregar_Pacientes.this, "Error de la base de datos", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                try { Log.i("ActionBar", "Atrás!");
                    finish();
                    return true;
                }catch (Exception e){
                    Toast.makeText(Agregar_Pacientes.this,"Error al retroceder",Toast.LENGTH_LONG).show();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void AgregatTutor(){
        try{
            LayoutInflater inflater = getLayoutInflater();
            final View dialoglayout = inflater.inflate(R.layout.datos_tutor, null);

            final String[] detalla = {""};
            final String[] idTutor = {""};

            final EditText tvcedulaTutor=(EditText)dialoglayout.findViewById(R.id.edcedulaTutor);
            final EditText tvNombreTutor=(EditText)dialoglayout.findViewById(R.id.edNombreTutor);
            final EditText tvApellidoTutor=(EditText)dialoglayout.findViewById(R.id.edApellidoTutor);
            final EditText tvTelefonoTutor=(EditText)dialoglayout.findViewById(R.id.edTelefonoTutor);
            final EditText tvDireccionTutor=(EditText)dialoglayout.findViewById(R.id.edDireccionTutor);
            final EditText tvCorreoTutor=(EditText)dialoglayout.findViewById(R.id.edCorreoTutor);
            final Button RegistrarTutor=(Button)dialoglayout.findViewById(R.id.btnRegistrarTutor);
            final Spinner spinnerSexoTutor=(Spinner)dialoglayout.findViewById(R.id.spinnerSexoTutor);

            RegistrarTutor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(detalla[0].equals("No tiene")){
                        new AlertDialog.Builder(Agregar_Pacientes.this)
                                .setTitle("Ya registro el tutor!")
                                .setMessage("Proceda a registrar los datos del paciente.")
                                .setCancelable(true)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                }).show();
                    }else if (tvcedulaTutor.getText().toString().length() == 0) {
                        VACIO(tvcedulaTutor, ViewFocus);
                    } else if(!detalla[0].equals("No tiene")){
                        Query query = PacienteDatabase.child("Tutor").orderByChild("cedula").equalTo(tvcedulaTutor.getText().toString());
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {

                                    PacienteDatabase.child("Tutor").child(tvcedulaTutor.getText().toString()).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            String nombre = dataSnapshot.child("nombre").getValue().toString();
                                            String apellido = dataSnapshot.child("apellido").getValue().toString();

                                            new AlertDialog.Builder(Agregar_Pacientes.this)
                                                    .setTitle("Registrar Tutor")
                                                    .setMessage("Agregar a: "+nombre+" "+apellido+"\n\nComo tutor del paciente?")
                                                    .setCancelable(true)
                                                    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            Toast.makeText(Agregar_Pacientes.this, "Tutor registrado", Toast.LENGTH_SHORT).show();

                                                            idTutor[0] = tvcedulaTutor.getText().toString();
                                                            detalla[0] = "No tiene";

                                                            tvcedulaTutor.setText("");
                                                            tvNombreTutor.setText("");
                                                            tvApellidoTutor.setText("");
                                                            tvTelefonoTutor.setText("");
                                                            tvDireccionTutor.setText("");
                                                            tvCorreoTutor.setText("");
                                                            dialog.cancel();
                                                        }
                                                    })
                                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            dialog.cancel();
                                                        }
                                                    }).show();
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                } else {
                                    if (tvNombreTutor.getText().toString().length() == 0) {
                                        VACIO(tvNombreTutor, ViewFocus);
                                    } else if (tvApellidoTutor.getText().toString().length() == 0) {
                                        VACIO(tvApellidoTutor, ViewFocus);
                                    } else if (tvTelefonoTutor.getText().toString().length() == 0) {
                                        VACIO(tvTelefonoTutor, ViewFocus);
                                    } else if (tvDireccionTutor.getText().toString().length() == 0) {
                                        VACIO(tvDireccionTutor, ViewFocus);
                                    } else if (tvCorreoTutor.getText().toString().length() == 0) {
                                        VACIO(tvCorreoTutor, ViewFocus);
                                    } else {
                                        mRegProgress.setTitle("Registrando Tutor");
                                        mRegProgress.setMessage("Espere mientras se registra el tutor!");
                                        mRegProgress.setCanceledOnTouchOutside(false);
                                        mRegProgress.setCancelable(false);
                                        mRegProgress.show();

                                        final String cedula = tvcedulaTutor.getText().toString().trim();
                                        String nombre = tvNombreTutor.getText().toString().trim();
                                        String apellido = tvApellidoTutor.getText().toString().trim();
                                        String telefono = tvTelefonoTutor.getText().toString().trim();
                                        String direccion = tvDireccionTutor.getText().toString().trim();
                                        String correo = tvCorreoTutor.getText().toString().trim();
                                        String sexo = spinnerSexoTutor.getSelectedItem().toString();

                                        final String tutor = "Tutor/" + cedula;

                                        Map tutor_reg = new HashMap();
                                        tutor_reg.put("id", cedula);
                                        tutor_reg.put("cedula", cedula);
                                        tutor_reg.put("nombre", nombre);
                                        tutor_reg.put("apellido", apellido);
                                        tutor_reg.put("telefono", telefono);
                                        tutor_reg.put("direccion", direccion);
                                        tutor_reg.put("correo", correo);
                                        tutor_reg.put("sexo", sexo);

                                        Map TUTOR_agregado = new HashMap();
                                        TUTOR_agregado.put(tutor, tutor_reg);

                                        String s= String.valueOf(compruebaConexion(getApplicationContext()));
                                        if(s.equals("false")) {
                                            mRegProgress.dismiss();
                                            Toast.makeText(Agregar_Pacientes.this, "tutor registrado sin conexion", Toast.LENGTH_SHORT).show();
                                            tvcedulaTutor.setText("");
                                            tvNombreTutor.setText("");
                                            tvApellidoTutor.setText("");
                                            tvTelefonoTutor.setText("");
                                            tvDireccionTutor.setText("");
                                            tvCorreoTutor.setText("");
                                            spinnerSexoTutor.setSelection(0);
                                            registrado_tutor="Si";
                                            id_tutor=cedula;
                                        }

                                        PacienteDatabase.updateChildren(TUTOR_agregado, new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                if (databaseError != null) {
                                                    mRegProgress.hide();
                                                    Toast.makeText(Agregar_Pacientes.this, "Fallo el registrar. Por favor intentelo de nuevo.", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    mRegProgress.dismiss();
                                                    Toast.makeText(Agregar_Pacientes.this, "Tutor registrado", Toast.LENGTH_SHORT).show();
                                                    idTutor[0] = cedula;
                                                    detalla[0] = "No tiene";

                                                    tvcedulaTutor.setText("");
                                                    tvNombreTutor.setText("");
                                                    tvApellidoTutor.setText("");
                                                    tvTelefonoTutor.setText("");
                                                    tvDireccionTutor.setText("");
                                                    tvCorreoTutor.setText("");
                                                    tvcedulaTutor.requestFocus();
                                                }
                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(getApplicationContext(), "Error" + databaseError, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });

            final List<String> Listsexo = new ArrayList<String>();
            Listsexo.add("Masculino");
            Listsexo.add("Femenino");

            ArrayAdapter<String> areasAdapter = new ArrayAdapter<String>(Agregar_Pacientes.this, android.R.layout.simple_spinner_item, Listsexo);
            areasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerSexoTutor.setAdapter(areasAdapter);

            final AlertDialog.Builder builder = new AlertDialog.Builder(Agregar_Pacientes.this);
            builder.setTitle("Registrar Tutor");
            builder.setView(dialoglayout);
            builder.setCancelable(false);
            builder.setNeutralButton("Cerrar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String s= String.valueOf(compruebaConexion(getApplicationContext()));
                    if(s.equals("false")) {
                        if (registrado_tutor.equals("Si") ) {
                            edtCedula.setVisibility(View.GONE);
                            edtCedula.setText("No tiene");
                            TutorId = id_tutor;
                            dialog.cancel();
                        } else if (registrado_tutor.equals("No") ) {
                            edtCedula.setVisibility(View.VISIBLE);
                            edtCedula.setText("");
                            edtCedula.requestFocus();
                            dialog.cancel();
                        }
                    }else{
                        if (detalla[0].equals("No tiene")) {
                            edtCedula.setVisibility(View.GONE);
                            edtCedula.setText("No tiene");
                            TutorId = idTutor[0].toString();
                            dialog.cancel();
                        } else {
                            edtCedula.setVisibility(View.VISIBLE);
                            edtCedula.setText("");
                            edtCedula.requestFocus();
                            dialog.cancel();
                        }
                    }
                }
            });
            builder.show();

        }catch (Exception e){
            Toast.makeText(Agregar_Pacientes.this,e.toString(),Toast.LENGTH_LONG).show();
        }
    }

    private void MenordeEdad(){
        new AlertDialog.Builder(Agregar_Pacientes.this)
                .setTitle("Falto la cedula!")
                .setMessage("El paciente es menor de edad?")
                .setCancelable(true)
                .setPositiveButton("si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        edtCedula.setVisibility(View.GONE);
                        edtCedula.setText("No tiene");
                        AgregatTutor();
                    }
                })
                .setNegativeButton("no", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        VACIO(edtCedula, ViewFocus);
                        edtCedula.setVisibility(View.VISIBLE);
                        dialog.cancel();
                    }
                }).show();
    }

    public void pre_registro(){
        if (edtNombre.getText().toString().length() == 0) {
            VACIO(edtNombre, ViewFocus);
        } else if (edtApellido.getText().toString().length() == 0) {
            VACIO(edtApellido, ViewFocus);
        }else if (radioclickcp.equals("Vacio")) {
            new AlertDialog.Builder(Agregar_Pacientes.this)
                    .setTitle("Seguro!")
                    .setMessage("Debe de elegir una opcion")
                    .setPositiveButton("si", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).show();
        } else if (radioclickcp.equals("Si") && edtSeguro.getText().toString().length() == 0) {
            VACIO(edtSeguro, ViewFocus);
        }else {
            mRegProgress.setTitle("Guardando Paciente");
            mRegProgress.setMessage("Espere mientras se guarda el cliente!");
            mRegProgress.setCanceledOnTouchOutside(false);
            mRegProgress.setCancelable(false);
            mRegProgress.show();
            Agregar_Paciente();
        }
    }

    @Override
    public void onClick(View v) {
        try {
            if (v == btnGuardar) {
                try {
                    if (edtCedula.getText().toString().length() == 0) {
                        MenordeEdad();
                    }else if(edtCedula.getText().toString().equals("No tiene")) {
                        pre_registro();
                    }else{
                        Query query = PacienteDatabase.child("Pacientes").orderByChild("cedula").equalTo(edtCedula.getText().toString());
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    new AlertDialog.Builder(Agregar_Pacientes.this)
                                            .setTitle("Paciente ya registrado!")
                                            .setMessage("El paciente ya habia sido registrado.")
                                            .setCancelable(true)
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    edtCedula.setText("");
                                                    edtCedula.requestFocus();
                                                    Intent intent = getIntent();
                                                    finish();
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                    startActivity(intent);
                                                    dialog.cancel();
                                                }
                                            }).show();
                                }else {
                                    if (edtNombre.getText().toString().length() == 0) {
                                        VACIO(edtNombre, ViewFocus);
                                    } else if (edtApellido.getText().toString().length() == 0) {
                                        VACIO(edtApellido, ViewFocus);
                                    } else if (edtCorreo.getText().toString().length() == 0) {
                                        VACIO(edtCorreo, ViewFocus);
                                    } else if (edtTelefono.getText().toString().length() == 0) {
                                        VACIO(edtTelefono, ViewFocus);
                                    } else if (radioclickcp.equals("Vacio")) {
                                        new AlertDialog.Builder(Agregar_Pacientes.this)
                                                .setTitle("Seguro!")
                                                .setMessage("Debe de elegir una opcion")
                                                .setPositiveButton("si", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.cancel();
                                                    }
                                                }).show();
                                    } else if (radioclickcp.equals("Si") && edtSeguro.getText().toString().length() == 0) {
                                        VACIO(edtSeguro, ViewFocus);
                                    }else {
                                        mRegProgress.setTitle("Guardando Paciente");
                                        mRegProgress.setMessage("Espere mientras se guarda el paciente!");
                                        mRegProgress.setCanceledOnTouchOutside(false);
                                        mRegProgress.setCancelable(false);
                                        mRegProgress.show();
                                        Agregar_Paciente();
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });

                    }
                }catch (Exception e){
                    Toast.makeText(Agregar_Pacientes.this,"Error al guardar", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Toast.makeText(Agregar_Pacientes.this,"Error al hacer click", Toast.LENGTH_SHORT).show();
        }
    }

    private void Agregar_Paciente(){
        try {
            String cedula = edtCedula.getText().toString().trim();
            String nombre = edtNombre.getText().toString().trim();
            String correo,telefono;
            String apellido = edtApellido.getText().toString().trim();
            if(edtCorreo.getText().toString().length()==0){
                correo ="No tiene";
            }else{
                correo = edtCorreo.getText().toString().trim();
            }
            if(edtTelefono.getText().toString().length()==0){
                telefono = "No tiene";
            }else{
                telefono = edtTelefono.getText().toString().trim();
            }
            String Nss = edtSeguro.getText().toString().trim();
            String seguro = spSeguro.getSelectedItem().toString();
            String sexo = spSexo.getSelectedItem().toString();

            DatabaseReference pacientekey = PacienteDatabase.child("Pacientes").push();

            String pacientepush = pacientekey.getKey();

            final String pacientes = "Pacientes/" + pacientepush;

            if(!TutorId.equals("No tiene")){
                DatabaseReference TutorKey = PacienteDatabase.child("Tutor_Hijo").child(TutorId).child( pacientepush.toString());

                final String tutor = "Tutor_Hijo/" + TutorId+"/"+pacientepush.toString();

                Map tutor_reg = new HashMap();
                tutor_reg.put("id_Hijo", pacientepush.toString());

                Map tutor_agregado = new HashMap();
                tutor_agregado.put(tutor, tutor_reg);

                String s= String.valueOf(compruebaConexion(getApplicationContext()));
                if(s.equals("false")) {
                    mRegProgress.dismiss();
                    Toast.makeText(Agregar_Pacientes.this, "Tutor registrado sin conexion", Toast.LENGTH_SHORT).show();
                    edtNombre.setVisibility(View.GONE);
                }

                PacienteDatabase.updateChildren(tutor_agregado, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            mRegProgress.hide();
                            Toast.makeText(Agregar_Pacientes.this, "Fallo el registrar. Por favor intentelo de nuevo.", Toast.LENGTH_SHORT).show();
                        } else {
                            mRegProgress.dismiss();
                            Toast.makeText(Agregar_Pacientes.this, "Tutor registrado", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            Map paciente_reg = new HashMap();
            paciente_reg.put("id", pacientepush.toString());
            paciente_reg.put("cedula", cedula);
            paciente_reg.put("nombre", nombre);
            paciente_reg.put("apellido", apellido);
            paciente_reg.put("correo", correo);
            paciente_reg.put("sexo", sexo);
            paciente_reg.put("telefono", telefono);
            paciente_reg.put("id_tutor", TutorId);
            if (radioclickcp.equals("Si")) {
                paciente_reg.put("Nss", Nss);
                paciente_reg.put("seguro", seguro);
            } else {
                paciente_reg.put("Nss", "No tiene");
                paciente_reg.put("seguro", "No tiene");
            }

            Map paciente_agregado = new HashMap();
            paciente_agregado.put(pacientes, paciente_reg);

            String s= String.valueOf(compruebaConexion(getApplicationContext()));
            if(s.equals("false")) {
                mRegProgress.dismiss();
                Toast.makeText(Agregar_Pacientes.this, "Paciente registrado sin conexion", Toast.LENGTH_SHORT).show();
                Intent intent = getIntent();
                finish();
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }

            PacienteDatabase.updateChildren(paciente_agregado, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        mRegProgress.hide();
                        Toast.makeText(Agregar_Pacientes.this, "Fallo el registrar. Por favor intentelo de nuevo.", Toast.LENGTH_SHORT).show();
                    } else {
                        mRegProgress.dismiss();
                        Toast.makeText(Agregar_Pacientes.this, "Paciente registrado", Toast.LENGTH_SHORT).show();
                        edtCedula.setVisibility(View.VISIBLE);
                        edtCedula.setText("");
                        edtNombre.setText("");
                        edtApellido.setText("");
                        edtCorreo.setText("");
                        edtTelefono.setText("");
                        radioButtonCP.setChecked(false);
                        spSexo.setSelection(0);
                        spSeguro.setSelection(0);
                        edtSeguro.setText("");
                        cvSeguro.setVisibility(View.GONE);
                        edtCedula.requestFocus();

                    }
                }
            });
        }catch (Exception e){
            Toast.makeText(Agregar_Pacientes.this,"Error al guardar!", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean compruebaConexion(Context context)
    {
        boolean connected = false;
        ConnectivityManager connec = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Recupera todas las redes (tanto móviles como wifi)
        NetworkInfo[] redes = connec.getAllNetworkInfo();

        for (int i = 0; i < redes.length; i++) {
            // Si alguna red tiene conexión, se devuelve true
            if (redes[i].getState() == NetworkInfo.State.CONNECTED) {
                connected = true;
            }
        }
        return connected;
    }

    public  void VACIO(EditText campo, View vista){
        try {
            campo.setError("No se puede dejar el campo vacio");
            vista = campo;
        }catch (Exception e){
            Toast.makeText(Agregar_Pacientes.this,"Error al verificar!", Toast.LENGTH_SHORT).show();
        }
    }
}