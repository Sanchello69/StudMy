package com.example.studmy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class InfoActivity extends AppCompatActivity {

    public static final String EXTRA_NAME = "name"; //Имя дополнительного значения, передаваемого в интенте
    public static final String EXTRA_ADDRESS = "address";
    public static final String EXTRA_DISCOUNT = "discount";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        Intent intent = getIntent();
        String addressText = intent.getStringExtra(EXTRA_ADDRESS); //Получаем интент и извлекаем из него сообщение вызовом getStringExtra().
        TextView addressView = (TextView)findViewById(R.id.info_marker);
        addressView.setText(addressText); // Добавляем текст в надпись с идентификатором addressText
    }
}
