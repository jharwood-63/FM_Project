package com.example.familymapclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import data.DataCache;
import model.Person;
import requests.UserRequest;
import result.LoginResult;

public class MainActivity extends AppCompatActivity implements LoginFragment.Listener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = this.getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragmentFrameLayout);
        if (fragment == null) {
            fragment = createLoginFragment();

            fragmentManager.beginTransaction()
                    .add(R.id.fragmentFrameLayout, fragment)
                    .commit();
        }
        else {
            if (fragment instanceof LoginFragment) {
                ((LoginFragment) fragment).registerListener(this);
            }
        }
    }

    private Fragment createLoginFragment() {
        LoginFragment loginFragment = new LoginFragment();
        loginFragment.registerListener(this);
        return loginFragment;
    }

    @Override
    public void notifyDone() {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        Fragment fragment = new MapFragment();

        fragmentManager.beginTransaction()
                .replace(R.id.fragmentFrameLayout, fragment)
                .commit();

        Handler uiThreadMessageHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                Bundle bundle = message.getData();

                Toast.makeText(MainActivity.this, bundle.getString("message"), Toast.LENGTH_SHORT).show();
            }
        };

        FillDataTask fillDataTask = new FillDataTask(uiThreadMessageHandler);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(fillDataTask);
    }

    private static class FillDataTask implements Runnable {
        private final Handler messageHandler;

        public FillDataTask(Handler messageHandler) {
            this.messageHandler = messageHandler;
        }

        @Override
        public void run() {
            DataCache dataCache = DataCache.getInstance();
            Person person = null;
            try {
                dataCache.fillDataCache();

                String personID = dataCache.getPersonID();

                person = dataCache.getPerson(personID);
            }
            catch (IOException e) {
                Log.e("Main Activity", "IOException");
            }

            sendMessage(person);
        }

        private void sendMessage(Person person) {
            Message message = Message.obtain();
            Bundle messageBundle = new Bundle();

            String toastString = "";
            if (person != null) {
                toastString = person.getFirstName() + " " + person.getLastName();
            }
            else {
                toastString = "Unable to fillDataCache";
            }

            messageBundle.putString("message", toastString);
            message.setData(messageBundle);
            messageHandler.sendMessage(message);
        }
    }
}