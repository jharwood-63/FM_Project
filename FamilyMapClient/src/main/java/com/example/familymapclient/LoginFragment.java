package com.example.familymapclient;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import data.DataCache;
import model.Person;
import requests.LoginRequest;
import requests.RegisterRequest;
import requests.UserRequest;
import result.LoginResult;


public class LoginFragment extends Fragment {
    private Listener listener;

    public interface Listener {
        void notifyDone();
    }

    public void registerListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        //Create another listener that works with the register button

        EditText serverHost = (EditText) view.findViewById(R.id.serverHostField);
        EditText serverPort = (EditText) view.findViewById(R.id.serverPortField);

        EditText username = (EditText) view.findViewById(R.id.usernameField);
        EditText password = (EditText) view.findViewById(R.id.passwordField);
        EditText email = (EditText) view.findViewById(R.id.emailField);
        EditText firstName = (EditText) view.findViewById(R.id.firstNameField);
        EditText lastName = (EditText) view.findViewById(R.id.lastNameField);
        EditText gender = (EditText) view.findViewById(R.id.genderField);

        Button loginButton = view.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataCache dataCache = DataCache.getInstance();
                dataCache.setUrls(serverHost.getText().toString(), serverPort.getText().toString());

                Handler uiThreadMessageHandler = new Handler() {
                    @Override
                    public void handleMessage(Message message) {
                        Bundle bundle = message.getData();

                        if (bundle.getBoolean("success")) {
                            if (listener != null) {
                                listener.notifyDone();
                            }
                        }
                        else {
                            Toast.makeText(getActivity(), "Login failed", Toast.LENGTH_LONG).show();
                        }
                    }
                };

                try {
                    UserRequest loginRequest = new LoginRequest(username.getText().toString(), password.getText().toString());
                    String urlString = getUrl(getString(R.string.login_url), serverHost.getText().toString(), serverPort.getText().toString());

                    URL loginUrl = new URL(urlString);
                    LoginRegisterTask loginTask = new LoginRegisterTask(uiThreadMessageHandler, loginUrl, loginRequest);
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    executor.submit(loginTask);
                }
                catch (IOException e) {
                    Log.e("Login Fragment", e.getMessage(), e);
                }
            }
        });

        Button registerButton = view.findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataCache dataCache = DataCache.getInstance();
                dataCache.setUrls(serverHost.getText().toString(), serverPort.getText().toString());

                Handler uiThreadMessageHandler = new Handler() {
                    @Override
                    public void handleMessage(Message message) {
                        Bundle bundle = message.getData();

                        if (bundle.getBoolean("success")) {
                            if (listener != null) {
                                listener.notifyDone();
                            }
                        }
                        else {
                            Toast.makeText(getActivity(), "Login failed", Toast.LENGTH_LONG).show();
                        }
                    }
                };

                String usernameString = username.getText().toString();
                String passwordString = password.getText().toString();
                String emailString = email.getText().toString();
                String firstNameString = firstName.getText().toString();
                String lastNameString = lastName.getText().toString();
                String genderString = gender.getText().toString();

                UserRequest registerRequest = new RegisterRequest(usernameString, passwordString, emailString, firstNameString,
                        lastNameString, genderString);
                String urlString = getUrl(getString(R.string.register_url), serverHost.getText().toString(), serverPort.getText().toString());

                try {
                    URL registerUrl = new URL(urlString);
                    LoginRegisterTask registerTask = new LoginRegisterTask(uiThreadMessageHandler, registerUrl, registerRequest);
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    executor.submit(registerTask);
                }
                catch (IOException e) {
                    Log.e("Login Fragment", e.getMessage(), e);
                }
            }
        });

        return view;
    }

    private String getUrl(String endPoint, String serverHost, String serverPort) {
        return "http://" + serverHost + ":" + serverPort + endPoint;
    }

    private static class LoginRegisterTask implements Runnable {
        private final Handler messageHandler;
        private final URL url;
        private final UserRequest userRequest;

        public LoginRegisterTask(Handler messageHandler, URL url, UserRequest userRequest) {
            this.messageHandler = messageHandler;
            this.url = url;
            this.userRequest = userRequest;
        }

        @Override
        public void run() {
            ServerProxy serverProxy = new ServerProxy();

            try {
                LoginResult loginRegisterResult = (LoginResult) serverProxy.doPost(url, userRequest);
                if (loginRegisterResult.isSuccess()) {
                    DataCache dataCache = DataCache.getInstance();
                    dataCache.setAuthToken(loginRegisterResult.getAuthtoken());
                    dataCache.setPersonID(loginRegisterResult.getPersonID());
                }
                sendMessage(loginRegisterResult);
            }
            catch (IOException e) {
                Log.e("Login task", e.getMessage(), e);
            }
        }

        private void sendMessage(LoginResult loginResult) {
            Message message = Message.obtain();

            Bundle messageBundle = new Bundle();

            messageBundle.putBoolean("success", loginResult.isSuccess());

            message.setData(messageBundle);

            messageHandler.sendMessage(message);
        }
    }
}