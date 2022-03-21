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
    private static final String LOGIN_URL = "http://10.0.2.2:7979/user/login";
    private static final String REGISTER_URL = "http://10.0.2.2:7979/user/register";
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
                try {
                    Handler uiThreadMessageHandler = new Handler() {
                        @Override
                        public void handleMessage(Message message) {
                            String toastString = "";
                            try {
                                Bundle bundle = message.getData();

                                DataCache dataCache = DataCache.getInstance();
                                dataCache.setAuthToken(bundle.getString("authtoken"));
                                dataCache.setPerson(bundle.getString("personID"));
                                Person person = dataCache.getUserPerson();

                                dataCache.fillDataCache();

                                toastString = person.getFirstName() + " " + person.getLastName();
                            } catch (IOException e) {
                                toastString = "Error: Unable to fetch data from database";
                            } finally {
                                Toast.makeText(getActivity(), toastString, Toast.LENGTH_LONG).show();
                            }
                        }
                    };

                    UserRequest loginRequest = new LoginRequest(username.getText().toString(), password.getText().toString());
                    URL loginUrl = new URL(LOGIN_URL);
                    LoginRegisterTask loginTask = new LoginRegisterTask(uiThreadMessageHandler, loginUrl, loginRequest);
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    executor.submit(loginTask);

                    if (listener != null) {
                        listener.notifyDone();
                    }
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
                String usernameString = username.getText().toString();
                String passwordString = password.getText().toString();
                String emailString = email.getText().toString();
                String firstNameString = firstName.getText().toString();
                String lastNameString = lastName.getText().toString();
                String genderString = gender.getText().toString();

                UserRequest registerRequest = new RegisterRequest(usernameString, passwordString, emailString, firstNameString,
                        lastNameString, genderString);

                try {
                    URL registerUrl = new URL(REGISTER_URL);
                    Handler uiThreadMessageHandler = new Handler() {
                        @Override
                        public void handleMessage(Message message) {
                            String toastString = "";
                            try {
                                Bundle bundle = message.getData();

                                DataCache dataCache = DataCache.getInstance();
                                dataCache.setAuthToken(bundle.getString("authtoken"));
                                dataCache.setPerson(bundle.getString("personID"));
                                Person person = dataCache.getUserPerson();

                                dataCache.fillDataCache();

                                toastString = person.getFirstName() + " " + person.getLastName();
                            } catch (IOException e) {
                                toastString = "Error: Unable to fetch data from database";
                            } finally {
                                Toast.makeText(getActivity(), toastString, Toast.LENGTH_LONG).show();
                            }
                        }
                    };

                    LoginRegisterTask registerTask = new LoginRegisterTask(uiThreadMessageHandler, registerUrl, registerRequest);
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    executor.submit(registerTask);

                    if (listener != null) {
                        listener.notifyDone();
                    }
                }
                catch (IOException e) {
                    Log.e("Login Fragment", e.getMessage(), e);
                }
            }
        });

        return view;
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
            System.out.println("LoginTask");
            ServerProxy serverProxy = new ServerProxy();
            //create the request for either the login or the register
            //is the way im doing it gonna erase data?

            LoginResult loginRegisterResult = null;

            try {
                loginRegisterResult = (LoginResult) serverProxy.doPost(url, userRequest);
            }
            catch (IOException e) {
                //do something with this but you can't throw it
                //maybe put something in the bundle to say there was an error and then deal with it somewhere else
            }
            finally {
                sendMessage(loginRegisterResult);
            }
        }

        private void sendMessage(LoginResult loginResult) {
            Message message = Message.obtain();

            Bundle messageBundle = new Bundle();

            messageBundle.putString("authtoken", loginResult.getAuthtoken());
            messageBundle.putString("username", loginResult.getUsername());
            messageBundle.putString("personID", loginResult.getPersonID());

            message.setData(messageBundle);

            messageHandler.sendMessage(message);
        }
    }
}