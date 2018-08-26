package aplication.android.wimervm.appterapias;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class Agregar_Clientes extends AppCompatActivity {

    private RadioGroup radioCP;
    private RadioButton radioButtonCP;
    private String radioclickcp="";
    private EditText edtSeguro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar__clientes);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.mytoolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        radioCP=(RadioGroup)findViewById(R.id.radiogroupCP);

        edtSeguro=(EditText)findViewById(R.id.editTextSeguro);
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
                edtSeguro.setText("");
            }
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Error al hacer click", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(Agregar_Clientes.this,"Error al retroceder",Toast.LENGTH_LONG).show();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}