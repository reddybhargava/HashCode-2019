package com.jnanesh.Hashcode;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.jnanesh.myapplication.R;

public class TrashMarking extends AppCompatActivity {
    FirebaseFirestore db;
    RadioGroup radioGroup,radioGroup2;
    RadioButton radioButton,radioButton1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash_marking);
        db=FirebaseFirestore.getInstance();

    }
    public void trashMark(View view){
        Intent in=new Intent(this,Fire_marking.class);
        radioGroup = (RadioGroup) findViewById(R.id.radiog);
        radioGroup2 = findViewById(R.id.radio2);
        int dept= radioGroup2.getCheckedRadioButtonId();
        int selectedId = radioGroup.getCheckedRadioButtonId();
        radioButton1 = findViewById(dept);
        radioButton = (RadioButton) findViewById(selectedId);
        if(radioButton.getText()!="" && radioButton1.getText()!="") {
            in.putExtra("place",radioButton.getText() );
            in.putExtra("DEPARTMENT",radioButton1.getText());
            startActivity(in);
        }
    }
}
