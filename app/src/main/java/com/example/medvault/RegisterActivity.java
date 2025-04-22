package com.example.medvault;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.medvault.User;
import com.example.medvault.UserDao;

public class RegisterActivity extends AppCompatActivity {

    EditText nameInput, emailInput, passwordInput;
    Button signupBtn;
    UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        signupBtn = findViewById(R.id.signupBtn);

        userDao = AppDatabaseInstance.getInstance(this).userDao();

        signupBtn.setOnClickListener(v -> {
            String name = nameInput.getText().toString();
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();

            if (userDao.getUserByEmail(email) != null) {
                Toast.makeText(this, "User already exists!", Toast.LENGTH_SHORT).show();
                return;
            }

            User user = new User(name, email, password);
            userDao.insert(user);

            Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
            finish(); // Go back to LoginActivity
        });
    }
}
