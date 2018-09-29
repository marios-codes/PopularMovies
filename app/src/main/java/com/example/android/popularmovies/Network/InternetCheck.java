package com.example.android.popularmovies.Network;

import android.os.AsyncTask;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Class that checks if there is internet connectivity
 * Source: https://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out
 */

public class InternetCheck extends AsyncTask<Void, Void, Boolean> {

  private Consumer mConsumer;

  public interface Consumer {

    void onConnectivityCheck(Boolean isConnected);
  }

  public InternetCheck(Consumer consumer) {
    mConsumer = consumer;
    execute();
  }

  @Override
  protected Boolean doInBackground(Void... voids) {
    try {
      // TCP/HTTP/DNS (depending on the port, 53=DNS, 80=HTTP, etc.)
      Socket sock = new Socket();
      //check on Google's DNS address
      sock.connect(new InetSocketAddress("8.8.8.8", 53), 1500);
      sock.close();
      return true;
    } catch (IOException e) {
      return false;
    }
  }

  @Override
  protected void onPostExecute(Boolean isConnected) {
    mConsumer.onConnectivityCheck(isConnected);
  }
}
