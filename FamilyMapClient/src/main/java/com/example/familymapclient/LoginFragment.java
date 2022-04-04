package com.example.familymapclient;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import android.text.TextWatcher;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import data.DataCache;
import data.ServerProxy;
import model.Person;
import requests.LoginRequest;
import requests.RegisterRequest;
import requests.UserRequest;
import result.LoginResult;
import result.Result;


public class LoginFragment extends Fragment {
    private Listener listener;
    private String gender;

    public interface Listener {
        void notifyDone();
    }

    public void registerListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        DataCache dataCache = DataCache.getInstance();

        EditText serverHost = (EditText) view.findViewById(R.id.serverHostField);
        EditText serverPort = (EditText) view.findViewById(R.id.serverPortField);

        EditText username = (EditText) view.findViewById(R.id.usernameField);
        EditText password = (EditText) view.findViewById(R.id.passwordField);
        EditText email = (EditText) view.findViewById(R.id.emailField);
        EditText firstName = (EditText) view.findViewById(R.id.firstNameField);
        EditText lastName = (EditText) view.findViewById(R.id.lastNameField);
        RadioButton maleButton = (RadioButton) view.findViewById(R.id.radio_male);
        RadioButton femaleButton = (RadioButton) view.findViewById(R.id.radio_female);

        Button loginButton = view.findViewById(R.id.loginButton);
        Button registerButton = view.findViewById(R.id.registerButton);

        serverHost.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {/*Do nothing*/}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                boolean isLogin = isLogin(serverHost.getText().toString(), serverPort.getText().toString(), username.getText().toString(), password.getText().toString());
                if (isLogin) {
                    loginButton.setEnabled(true);
                }
                else {
                    loginButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {/*Do nothing*/}
        });

        serverPort.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {/*Do nothing*/}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                boolean isLogin = isLogin(serverHost.getText().toString(), serverPort.getText().toString(), username.getText().toString(), password.getText().toString());
                if (isLogin) {
                    loginButton.setEnabled(true);
                }
                else {
                    loginButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {/*Do nothing*/}
        });

        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {/*Do nothing*/}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                boolean isLogin = isLogin(serverHost.getText().toString(), serverPort.getText().toString(), username.getText().toString(), password.getText().toString());
                if (isLogin) {
                    loginButton.setEnabled(true);
                }
                else {
                    loginButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {/*Do nothing*/}
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {/*Do nothing*/}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                boolean isLogin = isLogin(serverHost.getText().toString(), serverPort.getText().toString(), username.getText().toString(), password.getText().toString());
                if (isLogin) {
                    loginButton.setEnabled(true);
                }
                else {
                    loginButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {/*Do nothing*/}
        });

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {/*Do nothing*/}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                boolean isRegister = isRegister(serverHost.getText().toString(), serverPort.getText().toString(), username.getText().toString(), password.getText().toString(),
                        email.getText().toString(), firstName.getText().toString(), lastName.getText().toString(), gender);

                if (isRegister) {
                    registerButton.setEnabled(true);
                }
                else {
                    registerButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {/*Do nothing*/}
        });

        firstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {/*Do nothing*/}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                boolean isRegister = isRegister(serverHost.getText().toString(), serverPort.getText().toString(), username.getText().toString(), password.getText().toString(),
                        email.getText().toString(), firstName.getText().toString(), lastName.getText().toString(), gender);

                if (isRegister) {
                    registerButton.setEnabled(true);
                }
                else {
                    registerButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {/*Do nothing*/}
        });

        lastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {/*Do nothing*/}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                boolean isRegister = isRegister(serverHost.getText().toString(), serverPort.getText().toString(), username.getText().toString(), password.getText().toString(),
                        email.getText().toString(), firstName.getText().toString(), lastName.getText().toString(), gender);

                if (isRegister) {
                    registerButton.setEnabled(true);
                }
                else {
                    registerButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {/*Do nothing*/}
        });

        maleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checked = ((RadioButton) view).isChecked();

                switch(view.getId()) {
                    case R.id.radio_male:
                        if (checked)
                            gender = "m";
                        break;
                    case R.id.radio_female:
                        if (checked)
                            gender = "f";
                        break;
                }

                boolean isRegister = isRegister(serverHost.getText().toString(), serverPort.getText().toString(), username.getText().toString(), password.getText().toString(),
                        email.getText().toString(), firstName.getText().toString(), lastName.getText().toString(), gender);

                if (isRegister) {
                    registerButton.setEnabled(true);
                }
                else {
                    registerButton.setEnabled(false);
                }
            }
        });

        femaleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean checked = ((RadioButton) view).isChecked();

                switch(view.getId()) {
                    case R.id.radio_male:
                        if (checked)
                            gender = "m";
                        break;
                    case R.id.radio_female:
                        if (checked)
                            gender = "f";
                        break;
                }

                boolean isRegister = isRegister(serverHost.getText().toString(), serverPort.getText().toString(), username.getText().toString(), password.getText().toString(),
                        email.getText().toString(), firstName.getText().toString(), lastName.getText().toString(), gender);

                if (isRegister) {
                    registerButton.setEnabled(true);
                }
                else {
                    registerButton.setEnabled(false);
                }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataCache.setUrls(serverHost.getText().toString(), serverPort.getText().toString());
                Handler uiThreadMessageHandler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message message) {
                        Bundle bundle = message.getData();

                        handleResult(bundle);
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

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataCache.setUrls(serverHost.getText().toString(), serverPort.getText().toString());

                Handler uiThreadMessageHandler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message message) {
                        Bundle bundle = message.getData();

                        handleResult(bundle);
                    }
                };

                String usernameString = username.getText().toString();
                String passwordString = password.getText().toString();
                String emailString = email.getText().toString();
                String firstNameString = firstName.getText().toString();
                String lastNameString = lastName.getText().toString();

                UserRequest registerRequest = new RegisterRequest(usernameString, passwordString, emailString, firstNameString,
                        lastNameString, gender);
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

    private void handleResult(Bundle bundle) {
        if (bundle.getBoolean("success")) {
            Toast.makeText(getActivity(), bundle.getString("message"), Toast.LENGTH_LONG).show();
            if (listener != null) {
                listener.notifyDone();
            }
        }
        else {
            System.out.println("Failed login toast");
            Log.i("handleResult", "Failed login toast");
            Toast.makeText(getActivity(), bundle.getString("message"), Toast.LENGTH_SHORT).show();
        }
    }

    private String getUrl(String endPoint, String serverHost, String serverPort) {
        return "http://" + serverHost + ":" + serverPort + endPoint;
    }

    private boolean isLogin(String serverHost, String serverPort, String username, String password) {
        return !serverHost.equals("") && !serverPort.equals("") && !username.equals("") && !password.equals("");
    }

    private boolean isRegister(String serverHost, String serverPort, String username, String password, String email, String firstName, String lastName, String gender) {
        if (!hasNullComponents(serverHost, serverPort, username, password, email, firstName, lastName, gender)) {
            return !email.equals("") && !firstName.equals("") && !lastName.equals("") && !gender.equals("") && isLogin(serverHost, serverPort, username, password);
        }

        return false;
    }

    private boolean hasNullComponents(String serverHost, String serverPort, String username, String password, String email, String firstName, String lastName, String gender) {
        return serverHost == null || serverPort == null || username == null || password == null || email == null || firstName == null || lastName == null || gender == null;
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
                Result result = serverProxy.doPost(url, userRequest);
                if (result.isSuccess()) {
                    LoginResult loginResult = (LoginResult) result;
                    DataCache dataCache = DataCache.getInstance();
                    dataCache.setAuthToken(loginResult.getAuthtoken());
                    dataCache.setPersonID(loginResult.getPersonID());
                    dataCache.fillDataCache();
                }
                sendMessage(result);
            }
            catch (IOException e) {
                Log.e("Login task", e.getMessage(), e);
            }
        }

        private void sendMessage(Result result) {
            DataCache dataCache = DataCache.getInstance();

            Message message = Message.obtain();

            Bundle messageBundle = new Bundle();

            messageBundle.putBoolean("success", result.isSuccess());

            if (!result.isSuccess()) {
                messageBundle.putString("message", result.getMessage());
            }
            else {
                Person person = dataCache.getPerson(dataCache.getPersonID());
                String personString = person.getFirstName() + " " + person.getLastName();
                messageBundle.putString("message", personString);
            }

            message.setData(messageBundle);

            messageHandler.sendMessage(message);
        }
    }
}