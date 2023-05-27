package com.example.calculator;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText edit_input1;
    private EditText edit_input2;
    private Button edit_addition;
    private Button edit_subtraction;
    private Button edit_multiplication;
    private Button edit_division;
    private Button edit_clear;
    private TextView edit_result;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edit_input1 = (EditText) findViewById(R.id.first_input);
        edit_input2 = (EditText) findViewById(R.id.second_input);
        edit_addition = (Button) findViewById(R.id.addition_btn);
        edit_subtraction = (Button) findViewById(R.id.substraction_btn);
        edit_multiplication=(Button) findViewById(R.id.multiplication_btn);
        edit_division=(Button) findViewById(R.id.divission_btn);
        edit_clear = (Button) findViewById(R.id.clear_btn);
        edit_result = (TextView) findViewById(R.id.display_reasult);


        edit_addition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(edit_input1.getText().length()>0 && edit_input2.getText().length()>0) {
                    Double input1 = Double.parseDouble(edit_input1.getText().toString());
                    Double input2 = Double.parseDouble(edit_input2.getText().toString());

                    Double final_result;
                    final_result = input1 + input2;

                    edit_result.setText(Double.toString(final_result));
                }
                else{
                    Toast.makeText(MainActivity.this, "Please write number in both fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        edit_subtraction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(edit_input1.getText().length()>0 && edit_input2.getText().length()>0) {
                    Double input1 = Double.parseDouble(edit_input1.getText().toString());
                    Double input2 = Double.parseDouble(edit_input2.getText().toString());

                    Double final_result;
                    final_result = input1 - input2;

                    edit_result.setText(Double.toString(final_result));
                }
                else{
                    Toast.makeText(MainActivity.this, "Please write number in both fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        edit_multiplication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(edit_input1.getText().length()>0 && edit_input2.getText().length()>0) {
                    Double input1 = Double.parseDouble(edit_input1.getText().toString());
                    Double input2 = Double.parseDouble(edit_input2.getText().toString());

                    Double final_result;
                    final_result = input1 * input2;

                    edit_result.setText(Double.toString(final_result));
                }
                else{
                    Toast.makeText(MainActivity.this, "Please write number in both fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        edit_division.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(edit_input1.getText().length()>0 && edit_input2.getText().length()>0) {
                    Double input1 = Double.parseDouble(edit_input1.getText().toString());
                    Double input2 = Double.parseDouble(edit_input2.getText().toString());

                    Double final_result;
                    final_result = input1 / input2;

                    edit_result.setText(Double.toString(final_result));
                }
                else{
                    Toast.makeText(MainActivity.this, "Please write number in both fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        edit_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit_input1.setText("");
                edit_input2.setText("");
                edit_result.setText("0.00");

                edit_input1.requestFocus();
            }
        });

    }
}