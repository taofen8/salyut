package com.trico.salyut.browser;

import com.trico.salyut.STab;
import com.trico.salyut.engine.ExecResult;
import com.trico.salyut.engine.ExecUnit;
import com.trico.salyut.exception.SalyutException;
import com.trico.salyut.exception.SalyutExceptionType;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.RemoteWebDriver;

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
                        execResultListener.setResult(execUnit.execute());
                    }catch (Exception e){
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
                        else if (e instanceof WebDriverException){
                            driverStateListener.driverIsDown();
                        }

                        execResultListener.setResult(new ExecResult.Builder()
                                .type(ExecResult.InterruptType.FAILED)
                                .jobId(execUnit.jobId())
                                .build());
                        execUnit.reset();

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
