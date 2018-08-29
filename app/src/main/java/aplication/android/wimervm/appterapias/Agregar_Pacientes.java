package aplication.android.wimervm.appterapias;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Agregar_Pacientes extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference SpinnerData;
    private RadioGroup radioCP;
    private RadioButton radioButtonCP;
    private String radioclickcp="Vacio";
    private EditText edtSeguro,edtCedula,edtNombre,edtApellido,edtCorreo;
    private Button btnGuardar;
    private Spinner spSeguro;
    private ProgressDialog mRegProgress;
    private View ViewFocus = null;
    private DatabaseReference PacienteDatabase;

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

        edtSeguro=(EditText)findViewById(R.id.editTextSeguro);
        edtCedula=(EditText)findViewById(R.id.editTextCedulaPaciente);
        edtNombre=(EditText)findViewById(R.id.editTextNombrePaciente);
        edtApellido=(EditText)findViewById(R.id.editTextApellidoPaciente);
        edtCorreo=(EditText)findViewById(R.id.editTextEmailPaciente);
        btnGuardar=(Button)findViewById(R.id.buttonGuardarPaciente);
        spSeguro= (Spinner) findViewById(R.id.spinnerPaciente);

        SpinnerData = FirebaseDatabase.getInstance().getReference("Seguros");
        SpinnerData.keepSynced(true);

        spinerSeguros();
        btnGuardar.setOnClickListener(this);
    }

    public void rbclicked(View v){
        try {
            CardView cvSeguro = (CardView) findViewById(R.id.cardviewSeguro);

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

    @Override
    public void onClick(View v) {
        try {
            if (v == btnGuardar) {
                try {
                    if (edtCedula.getText().toString().length() == 0) {
                        VACIO(edtCedula, ViewFocus);
                    } else if (edtNombre.getText().toString().length() == 0) {
                        VACIO(edtNombre, ViewFocus);
                    } else if (edtApellido.getText().toString().length() == 0) {
                        VACIO(edtApellido, ViewFocus);
                    } else if (edtCorreo.getText().toString().length() == 0) {
                        VACIO(edtCorreo, ViewFocus);
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
                    }else{
                        mRegProgress.setTitle("Guardando Paciente");
                        mRegProgress.setMessage("Espere mientras se guarda el cliente!");
                        mRegProgress.setCanceledOnTouchOutside(false);
                        mRegProgress.setCancelable(false);
                        mRegProgress.show();
                        Agregar_Paciente();
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
            String apellido = edtApellido.getText().toString().trim();
            String correo = edtCorreo.getText().toString().trim();
            String Nss = edtSeguro.getText().toString().trim();
            String seguro = spSeguro.getSelectedItem().toString();

            DatabaseReference clientekey = PacienteDatabase.child("Pacientes").push();

            String clientepush = clientekey.getKey();

            final String pacientes = "Pacientes/" + clientepush;

            Map paciente_reg = new HashMap();
            paciente_reg.put("id", clientepush.toString());
            paciente_reg.put("cedula", cedula);
            paciente_reg.put("nombre", nombre);
            paciente_reg.put("apellido", apellido);
            paciente_reg.put("correo", correo);
            if (radioclickcp.equals("Si")) {
                paciente_reg.put("Nss", Nss);
                paciente_reg.put("seguro", seguro);
            } else {
                paciente_reg.put("Nss", "No tiene");
                paciente_reg.put("seguro", "No tiene");
            }

            Map paciente_agregado = new HashMap();
            paciente_agregado.put(pacientes, paciente_reg);

            PacienteDatabase.updateChildren(paciente_agregado, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        mRegProgress.hide();
                        Toast.makeText(Agregar_Pacientes.this, "Fallo el registrar. Por favor intentelo de nuevo.", Toast.LENGTH_SHORT).show();
                    } else {
                        mRegProgress.dismiss();
                        Toast.makeText(Agregar_Pacientes.this, "Paciente registrado", Toast.LENGTH_SHORT).show();
                        Intent intent = getIntent();
                        finish();
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                    }
                }
            });
        }catch (Exception e){
            Toast.makeText(Agregar_Pacientes.this,"Error al guardar!", Toast.LENGTH_SHORT).show();
        }
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