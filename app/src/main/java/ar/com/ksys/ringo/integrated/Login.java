package ar.com.ksys.ringo.integrated;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ar.com.ksys.ringo.R;

public class Login extends Activity {

    // Email, password edittext
    EditText usuario, contraseña;

    // login button
    Button btnLogin;

    // Alert Dialog Manager
    AlertDialogManager alerta = new AlertDialogManager();

    // Session Manager Class
    SessionManager sesion;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Session Manager
        sesion = new SessionManager(getApplicationContext());

        // Email, Password input text
        usuario = (EditText) findViewById(R.id.txtUsuario);
        contraseña = (EditText) findViewById(R.id.txtPsw);

        Toast.makeText(getApplicationContext(), "Sesión iniciada: " + sesion.isLoggedIn(), Toast.LENGTH_LONG).show();


        // Login button
        btnLogin = (Button) findViewById(R.id.btnLogin);


        // Login button click event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Get username, password from EditText
                String username = usuario.getText().toString();
                String password = contraseña.getText().toString();

                // Check if username, password is filled
                if(username.trim().length() > 0 && password.trim().length() > 0){
                    // For testing puspose username, password is checked with sample data
                    // username = test
                    // password = test
                    if(username.equals("test") && password.equals("test")){

                        // Creating user login session
                        // For testing i am stroing name, email as follow
                        // Use user real data
                        sesion.crearSesion(username, "email");

                        // Staring MainActivity
                        Intent i = new Intent(getApplicationContext(), Menu.class);
                        startActivity(i);
                        finish();

                    }else{
                        // username / password doesn't match
                        alerta.showAlertDialog(Login.this, "Falló inicio de sesión..", "Usuario/contraseña incorrectos", false);
                    }
                }else{
                    // user didn't entered username or password
                    // Show alert asking him to enter the details
                    alerta.showAlertDialog(Login.this, "Falló inicio de sesión..", "Ingresar usuario y contraseña", false);
                }

            }
        });
    }
}