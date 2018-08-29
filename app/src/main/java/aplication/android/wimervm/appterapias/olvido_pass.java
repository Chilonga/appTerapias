package aplication.android.wimervm.appterapias;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class olvido_pass extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private Button CambiarPass;
    private EditText edtCambiar;
    private DatabaseReference mUser;
    private ProgressDialog mRealizarProgress;
    private View ViewFocus = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_olvido_pass);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.mytoolbar3);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edtCambiar = (EditText) findViewById(R.id.txtemailcambiar);
        CambiarPass = (Button) findViewById(R.id.btncambiarpass);

        mRealizarProgress = new ProgressDialog(this);

        edtEscribiendo(edtCambiar);

        ChildEventListener mChildEventListener;
        mUser = FirebaseDatabase.getInstance().getReference().child("Medicos");
        mUser.keepSynced(true);

        CambiarPass.setOnClickListener(this);

    }catch (Exception e){
        Toast.makeText(olvido_pass.this,"Error al abrir!", Toast.LENGTH_SHORT).show();
    }
    }

    private void edtEscribiendo(final EditText editText){
        editText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                onPause(editText);
            }
        });
    }

    protected void onPause (EditText editText) {
        editText.setError(null);
    }

    @Override
    public void onClick(View v) {
        try {
            if (v == CambiarPass) {
                try {
                    if (edtCambiar.getText().toString().length() == 0) {
                        edtCambiar.setError("No se puede dejar el campo vacio");
                        ViewFocus = edtCambiar;
                    } else {
                        String email = edtCambiar.getText().toString();
                        mRealizarProgress.setTitle("Enviando peticion");
                        mRealizarProgress.setMessage("Espere mientras se envia!");
                        mRealizarProgress.setCanceledOnTouchOutside(false);
                        mRealizarProgress.setCancelable(false);
                        mRealizarProgress.show();
                        //verficar si el correo existe en la base de datos firebase
                        Query query = mUser.orderByChild("correo").equalTo(email);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    String email = edtCambiar.getText().toString();
                                    mAuth = FirebaseAuth.getInstance();
                                    Log.w(" Olvido su contraseña", "Recupere la contraseña con su email " + email);
                                    mAuth.sendPasswordResetEmail(email);
                                    mRealizarProgress.dismiss();
                                    edtCambiar.setText("");
                                    Toast.makeText(olvido_pass.this, "Peticion enviada.", Toast.LENGTH_LONG).show();
                                } else {
                                    mRealizarProgress.hide();
                                    edtCambiar.setError("El correo que ingreso no existe");
                                    ViewFocus = edtCambiar;
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(getApplicationContext(), "Error" + databaseError, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (Exception e) {
                    Toast.makeText(olvido_pass.this, "Error al enviar.", Toast.LENGTH_LONG).show();
                }
            }
        }catch (Exception e){
            Toast.makeText(olvido_pass.this,"Error!!!", Toast.LENGTH_SHORT).show();
        }
    }
}