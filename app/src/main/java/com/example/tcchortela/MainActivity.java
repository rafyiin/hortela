package com.example.tcchortela;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.snackbar.Snackbar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvForgotPassword, tvNoAccount;
    String[] mensagens = {"Preencha todos os campos", "E-mail ou senha inválidos"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IniciarComponentes();

        tvNoAccount.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Cadastro.class);
            startActivity(intent);
        });

        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RedefinirSenhaCodigo.class);
            startActivity(intent);
        });

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Snackbar snackbar = Snackbar.make(v, mensagens[0], Snackbar.LENGTH_SHORT);
                snackbar.setBackgroundTint(Color.WHITE);
                snackbar.setTextColor(Color.BLACK);
                snackbar.show();
            } else {
                autenticarUsuario(v, email, password);
            }
        });
    }

    private void IniciarComponentes() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvNoAccount = findViewById(R.id.tvNoAccount);
    }

    private void autenticarUsuario(View v, String email, String pass) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            DatabaseHelper databaseHelper = new DatabaseHelper();
            int accessLevel = databaseHelper.loginUser(email, pass);

            handler.post(() -> {
                if (accessLevel == -1) {
                    Snackbar snackbar = Snackbar.make(v, mensagens[1], Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                } else {
                    // Sucesso no login
                    String[] userData = databaseHelper.getUserDataByEmail(email);  // Recupera os dados do usuário
                    String nome = userData != null ? userData[0] : "Usuário"; // Obtém o nome do array ou usa um valor padrão

                    Intent intent = null;
                    if (accessLevel == 0) {
                        intent = new Intent(MainActivity.this, TelaPrincipal.class);
                    } else if (accessLevel == 1) {
                        intent = new Intent(MainActivity.this, TelaPrincipalCarente.class);
                    } else if (accessLevel == 2) {
                        intent = new Intent(MainActivity.this, TelaPrincipalVol.class);
                    }

                    if (intent != null) {
                        intent.putExtra("email", email);  // Passa o e-mail do usuário
                        intent.putExtra("nome", nome);    // Passa o nome do usuário
                        startActivity(intent);
                        finish();
                    }
                }
            });
        });
    }
}
