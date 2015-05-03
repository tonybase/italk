package iqq.app.core.service;

/**
 * Created by Tony on 5/3/15.
 */
public interface TimerService {

    void setInterval(Runnable runnable, long interval);

    void setTimeout(Runnable runnable, long delay);

    void killTimer(Runnable runnable);

}
