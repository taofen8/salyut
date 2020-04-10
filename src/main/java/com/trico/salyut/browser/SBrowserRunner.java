package com.trico.salyut.browser;

import com.trico.salyut.STab;
import com.trico.salyut.engine.ExecResult;
import com.trico.salyut.engine.ExecUnit;
import com.trico.salyut.exception.SalyutException;
import com.trico.salyut.exception.SalyutExceptionType;
import com.trico.salyut.log.Log;
import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.remote.UnreachableBrowserException;

import java.util.List;

public class SBrowserRunner<T extends ExecUnit> extends Thread {
    private List<T> resources;
    private ExecResultListener execResultListener;
    private PendingJobListener pendingJobListener;
    private DriverStateListener driverStateListener;
    private int upperLimit;
    private int counter;

    private SBrowserRunner(Builder<T> builder){
        this.resources = builder.resources;
        this.execResultListener = builder.execResultListener;
        this.pendingJobListener = builder.pendingJobListener;
        this.driverStateListener = builder.driverStateListener;
        this.upperLimit = builder.upperLimit;
    }

    public boolean isDriverDownMessage(String message){
        if (message.contains("Failed to decode response from marionette")
        ||message.contains("Failed to interpret value as array")){
            return true;
        }
        return false;
    }

    public boolean isDriverDownException(Exception e){
        if (e instanceof NoSuchSessionException
                || e instanceof UnreachableBrowserException){
            return true;
        }
        return false;
    }

    @Override
    public void run() {
        for (;;) {
            if (isInterrupted()) {
                return;
            }

            boolean idle = true;

            for (T execUnit:resources) {
                idle &= execUnit.isIdle();
            }

            if (idle){
                pendingJobListener.open();
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException e) {
                    return;
                }
            }

            for(T execUnit:resources){
                if (execUnit.isReady()){
                    try {
                        execResultListener.setResult(execUnit.execute(),false);
                    }catch (Exception e){
                        boolean driverDown = false;
                        if (e instanceof SalyutException){
                            if (((SalyutException) e).getType().equals(SalyutExceptionType.Stop)){
                                //do nothing;
                            }
                            else  if (((SalyutException) e).getType().equals(SalyutExceptionType.MobileMode)){
                                driverStateListener.mobileMode();
                                break;
                            }
                            else{
                                ((STab) execUnit).offerMessage(e.getMessage());
                            }
                        }
                        else{
                            ((STab) execUnit).offerMessage(SalyutExceptionType.SeleniumError.getExplain()+e.getMessage());
                            driverDown = isDriverDownMessage(e.getMessage()) || isDriverDownException(e);
                            if (driverDown){
                                ((STab) execUnit).offerMessage(SalyutExceptionType.RuntimeError.getExplain()+
                                        "driver is down, try to restart it... Please re-run the script. ");
                            }
                        }

                        try{
                            execResultListener.setResult(new ExecResult.Builder()
                                    .type(ExecResult.InterruptType.FAILED)
                                    .jobId(execUnit.jobId())
                                    .build(),driverDown);

                        }catch (Exception e1){
                            Log.logger.info("unhandled exception",e1);
                        }
                        finally {
                            execUnit.reset();
                        }

                        if (driverDown){
                            driverStateListener.driverIsDown();
                        }

                    }
                    finally {
                        counter++;
                        if (counter >= upperLimit){
                            driverStateListener.driverIsDown();
                            counter = 0;
                        }
                    }

                }
            }

        }
    }

    public static class Builder<T extends ExecUnit>{
        List<T> resources;
        ExecResultListener execResultListener;
        PendingJobListener pendingJobListener;
        DriverStateListener driverStateListener;
        int upperLimit;

        public Builder resources(List<T> resources){
            this.resources = resources;
            return this;
        }

        public Builder execResultListener(ExecResultListener execResultListener){
            this.execResultListener = execResultListener;
            return this;
        }

        public Builder pendingJobListener(PendingJobListener pendingJobListener){
            this.pendingJobListener = pendingJobListener;
            return this;
        }

        public Builder driverStateListener(DriverStateListener driverStateListener){
            this.driverStateListener = driverStateListener;
            return this;
        }

        public Builder upperLimit(int upperLimit){
            this.upperLimit = upperLimit;
            return this;
        }

        public SBrowserRunner<T> build(){
            return new SBrowserRunner<>(this);
        }
    }
}
