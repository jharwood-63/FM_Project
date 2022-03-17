package com.example.familymapclient;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.net.URL;


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



        Button loginButton = view.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //I think this is where you put the code for the login

                Handler uiThreadMessageHandler = new Handler() {
                    @Override
                    public void handleMessage(Message message) {
                        //I think this is where you do the toast that displays the first and last name of whoever is logging in
                        Bundle bundle = message.getData();
                        String toastString = getString(R.string.login_user_name, bundle.getString("name"));
                        Toast.makeText(getActivity(), toastString,Toast.LENGTH_LONG).show();
                    }
                };

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

    private static class LoginTask implements Runnable {
        private final Handler messageHandler;
        private final URL url;

        public LoginTask(Handler messageHandler, URL url) {
            this.messageHandler = messageHandler;
            this.url = url;
        }

        @Override
        public void run() {
            ServerProxy serverProxy = new ServerProxy();




        }
    }
}