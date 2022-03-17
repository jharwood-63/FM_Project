package com.example.familymapclient;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.net.URL;

import data.DataCache;
import model.Person;
import model.User;
import requests.LoginRequest;
import requests.UserRequest;
import result.LoginResult;
import result.RegisterResult;
import result.Result;


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

        EditText username = (EditText) view.findViewById(R.id.usernameField);
        EditText password = (EditText) view.findViewById(R.id.passwordField);

        Button loginButton = view.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Handler uiThreadMessageHandler = new Handler() {
                    @Override
                    public void handleMessage(Message message) {
                        Bundle bundle = message.getData();

                        DataCache dataCache = DataCache.getInstance();
                        dataCache.setAuthToken(bundle.getString("authtoken"));
                        dataCache.setPerson(bundle.getString("personID"));
                        Person person = dataCache.getUserPerson();

                        dataCache.fillDataCache();

                        String toastString = person.getFirstName() + " " + person.getLastName();
                        Toast.makeText(getActivity(), toastString,Toast.LENGTH_LONG).show();
                    }
                };

                UserRequest loginRequest = new LoginRequest(username.getText().toString(), password.getText().toString());

                if (listener != null) {
                    listener.notifyDone();
                }
            }
        });

        Button registerButton = view.findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    //how do I tell it that its a register?
                    //with the data passing thing
                    listener.notifyDone();
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