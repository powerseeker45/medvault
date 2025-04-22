package com.example.medvault;

import android.os.Bundle;
import android.content.Intent;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.medvault.AppDatabase;
import com.example.medvault.FamilyMember;
import androidx.appcompat.widget.Toolbar;
import java.util.ArrayList;
import java.util.List;

public class FamilyActivity extends AppCompatActivity {

    EditText nameInput, ageInput, relationInput;
    Button addBtn;
    ListView familyList;
    AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family);

        nameInput = findViewById(R.id.nameInput);
        ageInput = findViewById(R.id.ageInput);
        relationInput = findViewById(R.id.relationInput);
        addBtn = findViewById(R.id.addBtn);
        familyList = findViewById(R.id.familyList);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Health Records");
        }

        db = AppDatabaseInstance.getInstance(this);


        loadMembers();

        addBtn.setOnClickListener(v -> {
            FamilyMember member = new FamilyMember();
            member.name = nameInput.getText().toString();
            member.age = Integer.parseInt(ageInput.getText().toString());
            member.relation = relationInput.getText().toString();
            db.familyMemberDao().insert(member);
            Toast.makeText(this, "Member added", Toast.LENGTH_SHORT).show();
            loadMembers();
        });
    }

    void loadMembers() {
        List<FamilyMember> all = db.familyMemberDao().getAll();
        ArrayList<String> list = new ArrayList<>();
        for (FamilyMember m : all) {
            list.add(m.name + " (" + m.relation + ", " + m.age + " yrs)");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, list);
        familyList.setAdapter(adapter);

        familyList.setOnItemClickListener((parent, view, position, id) -> {
            FamilyMember selected = db.familyMemberDao().getAll().get(position);
            Intent i = new Intent(FamilyActivity.this, FamilyMemberRecordsActivity.class);
            i.putExtra("memberId", selected.id);
            startActivity(i);
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        return true;
    }
}
