package com.example.countdowntimer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText editTextTime;
    private TextView textViewTimer;
    private Button buttonStart, buttonStop;
    private CountdownTask countdownTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Referensi ke View
        editTextTime = findViewById(R.id.editTextTime);
        textViewTimer = findViewById(R.id.textViewTimer);
        buttonStart = findViewById(R.id.buttonStart);
        buttonStop = findViewById(R.id.buttonStop);

        // Atur klik listener untuk tombol Start
        buttonStart.setOnClickListener(v -> {
            String timeInput = editTextTime.getText().toString().trim();
            if (!timeInput.isEmpty()) {
                int timeInSeconds = Integer.parseInt(timeInput);
                if (timeInSeconds > 0) {
                    if (countdownTask == null || countdownTask.getStatus() == AsyncTask.Status.FINISHED) {
                        countdownTask = new CountdownTask();
                        countdownTask.execute(timeInSeconds);
                        buttonStop.setVisibility(Button.VISIBLE); // Tampilkan tombol Stop
                    } else {
                        Toast.makeText(this, "Timer sedang berjalan!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Masukkan waktu lebih dari 0 detik!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Masukkan waktu terlebih dahulu!", Toast.LENGTH_SHORT).show();
            }
        });

        // Atur klik listener untuk tombol Stop
        buttonStop.setOnClickListener(v -> {
            if (countdownTask != null) {
                countdownTask.cancel(true); // Hentikan task
                textViewTimer.setText("Dihentikan");
                resetButtons();
            }
        });
    }

    // AsyncTask untuk countdown
    private class CountdownTask extends AsyncTask<Integer, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            buttonStart.setEnabled(false);
            editTextTime.setEnabled(false);
        }

        @Override
        protected Void doInBackground(Integer... params) {
            int timeLeft = params[0];
            while (timeLeft > 0 && !isCancelled()) {
                publishProgress(timeLeft);
                timeLeft--;
                try {
                    Thread.sleep(1000); // Tunggu 1 detik
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            textViewTimer.setText(values[0] + " detik");
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (!isCancelled()) {
                textViewTimer.setText("Selesai!");
            }
            resetButtons();
        }
    }

    private void resetButtons() {
        buttonStart.setEnabled(true);
        editTextTime.setEnabled(true);
        buttonStop.setVisibility(Button.GONE);
    }
}
