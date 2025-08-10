package com.example.android_clean_network_layer;

import android.app.Application;
import lib.net.NetworkManager;

public class MyApplication extends Application {

    private static MyApplication instance;
    private NetworkManager networkManager;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        networkManager = NetworkManager.create("https://jsonplaceholder.typicode.com");
    }

    public static NetworkManager getNetworkManager() {
        if (instance == null) {
            throw new IllegalStateException("Uygulama henüz başlatılmadı.");
        }
        return instance.networkManager;
    }
}

/*
import android.app.Application;
import lib.net.NetworkManager;

public class MyApplication extends Application {

    private static MyApplication instance;
    private NetworkManager networkManager;

    @Override
    public void onCreate() {
        super.onCreate();

        // Uygulama başladığında statik instance'ı ata
        instance = this;

        // Ağ yöneticisini bir kez başlat
        networkManager = NetworkManager.create("https://jsonplaceholder.typicode.com");
    }

    // Uygulamanın her yerinden erişim için güvenli statik metot
    public static NetworkManager getNetworkManager() {
        if (instance == null) {
            throw new IllegalStateException("Uygulama henüz başlatılmadı.");
        }
        return instance.networkManager;
    }
}
*/