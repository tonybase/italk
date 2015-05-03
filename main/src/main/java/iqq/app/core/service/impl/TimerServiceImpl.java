package iqq.app.core.service.impl;

import iqq.app.core.service.TimerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 默认定时器实现
 * <p/>
 * Created by Tony on 5/3/15.
 */
@Service
public class TimerServiceImpl implements TimerService, InitializingBean {

    private static Logger LOG = LoggerFactory.getLogger(TimerServiceImpl.class);
    private Timer timer;
    private Map<Runnable, TimerAdapter> map;

    @Override
    public void afterPropertiesSet() throws Exception {
        timer = new Timer();
        map = new HashMap<>();
    }

    @Override
    public void setInterval(Runnable runnable, long interval) {
        TimerAdapter adapter = new TimerAdapter(runnable);
        map.put(runnable, adapter);
        timer.schedule(adapter, interval, interval);
    }

    @Override
    public void setTimeout(Runnable runnable, long delay) {
        TimerAdapter adapter = new TimerAdapter(runnable);
        map.put(runnable, adapter);
        timer.schedule(adapter, delay);
    }

    @Override
    public void killTimer(Runnable runnable) {
        if (map.containsKey(runnable)) {
            TimerAdapter adapter = map.get(runnable);
            adapter.cancel();
            timer.purge();
        }
    }

    private static class TimerAdapter extends TimerTask {
        private Runnable runnable;

        public TimerAdapter(Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        public void run() {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        runnable.run();
                    } catch (Throwable e) {
                        LOG.warn("TimerAdapter run timer error!", e);
                    }
                }
            });
        }

    }

}
