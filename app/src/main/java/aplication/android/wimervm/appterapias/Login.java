package aplication.android.wimervm.appterapias;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity implements View.OnClickListener  {

    private EditText Email;
    private EditText password;
    private Button btnIniciar;
    private FirebaseAuth mAuth;
    private ProgressDialog mLoginProgress;
    private TextView tvOLVIDO;
    private View ViewFocus = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        Email = (EditText) findViewById(R.id.txtemail);
        password = (EditText) findViewById(R.id.txtpass);
        btnIniciar = (Button) findViewById(R.id.btnLogin);
        tvOLVIDO=(TextView)findViewById(R.id.tv_Olvido);

        mLoginProgress = new ProgressDialog(this);

        btnIniciar.setOnClickListener(this);
        tvOLVIDO.setOnClickListener(this);

    }catch (Exception e){
        Toast.makeText(Login.this,"Error al abrir el login.",Toast.LENGTH_LONG).show();
    }
    }

    @Override
    public void onStart() {
        try {
            super.onStart();
            FirebaseUser currentUser = mAuth.getCurrentUser();

            if (currentUser != null) {
                Intent startIntent = new Intent(Login.this, Pantalla_Inicio.class);
                startActivity(startIntent);
                finish();
            }
        }catch (Exception e){
            Toast.makeText(Login.this,"Error al iniciar la sesion.",Toast.LENGTH_LONG).show();
        }
    }

    private void userLogin(String email,String pass){
        try{
            mAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                mLoginProgress.dismiss();
                                Intent intent = new Intent(Login.this, Pantalla_Inicio.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }else {
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthInvalidUserException e) {
                                    mLoginProgress.hide();
                                    Email.setError("El correo que ingreso no existe");
                                    ViewFocus = Email;
                                } catch (FirebaseAuthInvalidCredentialsException e) {
                                    mLoginProgress.hide();
                                    password.setError("La contraseña que ingreso es incorrecta");
                                    ViewFocus = password;
                                } catch (FirebaseNetworkException e) {
                                    mLoginProgress.hide();
                                    Toast.makeText(Login.this, "Necisita conexion a internet para poder iniciar sesion.", Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    mLoginProgress.hide();
                                    Toast.makeText(Login.this, "Fallo el iniciar sesion. Por favor intentelo de nuevo.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
        }catch (Exception e){
            Toast.makeText(Login.this,"Error al iniciar la sesion del usuario.",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        if(v==btnIniciar){
            try {
                String email=Email.getText().toString().trim();
                String pass=password.getText().toString().trim();

                if (Email.getText().toString().length() == 0) {
                    Email.setError("No se puede dejar el campo vacio");
                    ViewFocus = Email;
                } else if (password.getText().toString().length() == 0) {
                    password.setError("No se puede dejar el campo vacio");
                    ViewFocus = password;
                }else {
                    if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(pass)) {
                        mLoginProgress.setTitle("Iniciando Sesion");
                        mLoginProgress.setMessage("Espere mientras se inicia sesion !");
                        mLoginProgress.setCanceledOnTouchOutside(false);
                        mLoginProgress.show();
                        userLogin(email, pass);
                    }
                }
            }catch (Exception e){
                Toast.makeText(Login.this,"Error al Iniciar Sesion", Toast.LENGTH_SHORT).show();
            }
        }else if(v==tvOLVIDO){
            Intent startIntent = new Intent(Login.this, olvido_pass.class);
            startActivity(startIntent);
        }
    }
}