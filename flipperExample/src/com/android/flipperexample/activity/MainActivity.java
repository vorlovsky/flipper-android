package com.android.flipperexample.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.android.flipperexample.R;
import com.android.flipperexample.ui.SampleFlipper;

public class MainActivity extends Activity {

	private SampleFlipper flipMeter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

		flipMeter = (SampleFlipper) findViewById(R.id.flipmeter);

		final EditText text = (EditText) findViewById(R.id.edittext);
		final View button = findViewById(R.id.button);

		button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                try {
                    String input = text.getText().toString().trim();

                    int newValue = Integer.parseInt(input);
                    int oldValue = Integer.parseInt(flipMeter.getString());

                    flipMeter.setString(input, true, (newValue > oldValue));
                    text.setText("");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
	}
}
