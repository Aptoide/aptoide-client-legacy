package com.aptoide.amethyst.services;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;

import com.aptoide.amethyst.Aptoide;
import com.aptoide.amethyst.preferences.SecurePreferences;
import com.aptoide.amethyst.utils.AptoideUtils;
import com.aptoide.dataprovider.webservices.models.Constants;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;
import com.rabbitmq.client.impl.AMQConnection;
import com.rabbitmq.client.impl.ChannelN;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

/**
 * Created with IntelliJ IDEA.
 * User: brutus
 * Date: 24-10-2013
 * Time: 13:09
 * To change this template use File | Settings | File Templates.
 */
public class RabbitMqService extends Service {

    private final IBinder wBinder = new RabbitMqBinder();

    private ExecutorService thread_pool;
    private AMQConnection connection;

//    private Class appViewClass = Aptoide.getConfiguration().getAppViewActivityClass();

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        if(!isRunning) {
            //Toast.makeText(getApplicationContext(), "Starting amqp service", Toast.LENGTH_LONG).show();
            Aptoide.setWebInstallServiceRunning(true);
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//
//
//                    try {
//
//                        AccountManager manager = AccountManager.get(getApplicationContext());
//
//                        if (manager.getAccountsByType(Aptoide.getConfiguration().getAccountType()).length > 0) {
//
//                            final Account account = AccountManager.get(getApplicationContext()).getAccountsByType(Aptoide.getConfiguration().getAccountType())[0];
//
//                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//                            String queueName = sharedPreferences.getString("queueName", null);
//                            ContentResolver.setIsSyncable(account, Constants.WEBINSTALL_SYNC_AUTHORITY, 1);
//                            ContentResolver.setSyncAutomatically(account, Constants.WEBINSTALL_SYNC_AUTHORITY, true);
//
//                            if (Build.VERSION.SDK_INT >= 8) {
//                                ContentResolver.addPeriodicSync(account, Constants.WEBINSTALL_SYNC_AUTHORITY, new Bundle(), Constants.WEBINSTALL_SYNC_POLL_FREQUENCY);
//                            }
//
//                            String host = Constants.WEBINSTALL_HOST;
//                            isRunning = true;
//                            try {
//                                ConnectionFactory factory = new ConnectionFactory();
//                                factory.setHost(host);
//                                factory.setUsername("public");
//                                factory.setPassword("public");
//                                factory.setConnectionTimeout(20000);
//
//                                factory.setVirtualHost("webinstall");
//                                connection = (AMQConnection) factory.newConnection();
//                                newChannel(queueName, new AMQHandler() {
//                                    @Override
//                                    void handleMessage(String body) {
//
//                                        try {
//                                            JSONObject object = new JSONObject(body);
//
//                                            Intent i = new Intent(getApplicationContext(), appViewClass);
//                                            SharedPreferences securePreferences = SecurePreferences.getInstance();
//                                            String authToken = securePreferences.getString("devtoken", "");
//                                            String repo = object.getString("repo");
//                                            long id = object.getLong("id");
//                                            String md5sum = object.getString("md5sum");
//                                            i.putExtra("fromMyapp", true);
//                                            i.putExtra("repoName", repo);
//                                            i.putExtra("id", id);
//                                            i.putExtra("download_from", "webinstall");
//                                            i.putExtra("md5sum", md5sum);
//
//                                            String deviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
//
//                                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                            String hmac = object.getString("hmac");
//                                            String calculatedHmac = AptoideUtils.Algorithms.computeHmacSha1(repo + id + md5sum, authToken + deviceId);
//                                            if (hmac.equals(calculatedHmac)) {
//                                                getApplicationContext().startActivity(i);
//                                            } else {
//                                                Log.d("Aptoide-WebInstall", "Error validating message: received: " + hmac + " calculated:" + calculatedHmac);
//                                            }
//
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }
//
//
//                                    }
//                                });
//                            } catch (IOException e) {
//                                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean(Constants.WEBINSTALL_QUEUE_EXCLUDED, true).commit();
//                                e.printStackTrace();
//                                try {
//                                    if (channel != null && channel.isOpen()) {
//                                        channel.close();
//                                    }
//
//                                    if (connection != null && connection.isOpen()) {
//                                        connection.close();
//                                    }
//                                } catch (IOException e1) {
//                                    e1.printStackTrace();
//                                } catch (ShutdownSignalException e1) {
//                                    e1.printStackTrace();
//                                }
//                                isRunning = false;
//
//                            }
//                        }
//                    }catch (Exception e){
//                        e.printStackTrace();
//                    }
//                }
//            }).start();
        }

        return START_STICKY;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        onStartCommand(intent, 0, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return wBinder;
    }

    public void startAmqpService(){
        if(!isRunning){
            startService(new Intent(getApplicationContext(), RabbitMqService.class));
        }
        if(timer!=null){
            timer.cancel();
            timer.purge();
            timer = null;
        }


    }

    Timer timer;

    public void stopAmqpService(){
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                stopSelf();
                isRunning = false;
            }
        }, 600000);

        //Toast.makeText(getApplicationContext(), "OnUnbind timer started", Toast.LENGTH_LONG).show();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Aptoide-RabbitMqService", "RabbitMqService created!");
        thread_pool = Executors.newCachedThreadPool();




    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(timer!=null){
            timer.cancel();
            timer.purge();
        }
        Log.d("Aptoide-RabbitMqService", "RabbitMqService Destroyed!");

        try {

            isRunning = false;

            if(channel!=null && channel.isOpen()){
                channel.close();
            }

            if(connection != null && connection.isOpen()){
                connection.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ShutdownSignalException e){
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        Aptoide.setWebInstallServiceRunning(false);

    }

    public class RabbitMqBinder extends Binder {
        public RabbitMqService getService() {
            return RabbitMqService.this;
        }

    }


    private ChannelN channel;
    private QueueingConsumer consumer;

    public void newChannel(String queue_id, AMQHandler task) throws IOException {

        channel = (ChannelN) connection.createChannel();
        //channel.queueDeclare(queue_id, true, false, false, null);
        channel.basicQos(0);
        consumer = new QueueingConsumer(channel);
        channel.basicConsume(queue_id, false, consumer);
        task.setConsumer(consumer);
        thread_pool.execute(task);

    }

    private boolean isRunning = false;

    public abstract class AMQHandler implements Runnable {

        private QueueingConsumer consumer;

        public AMQHandler() {
        }

        @Override
        public void run() {

            while(isRunning){
                try {
                    QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                    String body = new String(delivery.getBody(), "UTF-8");
                    Log.d("Aptoide-RabbitMqService", "MESSAGE: " + body);
                    handleMessage(body);
                    channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    isRunning = false;
                    e.printStackTrace();
                } catch (ShutdownSignalException e){
                    isRunning = false;
                    try{
                        Log.d("Aptoide-WebInstall", "Connection closed with reason " + e.getReason().toString());
                    }catch (NullPointerException e1){
                        e1.printStackTrace();
                        Log.d("Aptoide-WebInstall", "Connection closed with unkonwn reason" );
                    }
                } catch (ConsumerCancelledException e){
                    isRunning = false;
                    Log.d("Aptoide-WebInstall", "Connection was canceled");
                }

            }
            try{
                if(channel != null && channel.isOpen()){
                    channel.close();
                    connection.disconnectChannel(channel);
                }

                if(connection!=null && connection.isOpen()){
                    connection.close();
                }


            } catch (Exception e){
                e.printStackTrace();
            }


        }

        abstract void handleMessage(String body);



        public void setConsumer(QueueingConsumer consumer) {
            this.consumer = consumer;
        }
    }

}


