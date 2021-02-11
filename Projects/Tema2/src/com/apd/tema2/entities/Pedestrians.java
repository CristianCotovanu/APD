package com.apd.tema2.entities;

import com.apd.tema2.utils.Constants;

/**
 * Clasa utilizata pentru gestionarea oamenilor care se strang la trecerea de pietoni.
 */
public class Pedestrians implements Runnable {
    private int pedestriansNo = 0;
    private int maxPedestriansNo;
    private boolean pass = false;
    private boolean finished = false;
    private int executeTime;
    private long startTime;

    public Pedestrians(int executeTime, int maxPedestriansNo) {
        this.startTime = System.currentTimeMillis();
        this.executeTime = executeTime;
        this.maxPedestriansNo = maxPedestriansNo;
    }

    @Override
    public void run() {
        while (System.currentTimeMillis() - startTime < executeTime) {
            try {
                pedestriansNo++;
                Thread.sleep(Constants.PEDESTRIAN_COUNTER_TIME);

                if (pedestriansNo == maxPedestriansNo) {
                    pedestriansNo = 0;
                    pass = true;
                    Thread.sleep(Constants.PEDESTRIAN_PASSING_TIME);
                    pass = false;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        finished = true;
    }

    public boolean isPass() {
        return pass;
    }

    public boolean isFinished() {
        return finished;
    }
}
