package com.trico.salyut.bash;

import com.trico.salyut.log.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class BashCmd {
    public static void killProcess(String processName){
        Log.logger.info("kill " + processName);
        try
        {
            String[] cmd = { "/bin/sh", "-c", "ps x | grep " + processName + " | grep -v grep | awk  '{print $1}'" };
            Process ps = Runtime.getRuntime().exec(cmd);
            ps.waitFor();

            BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
            String line = null;
            while ((line = br.readLine()) != null)
            {
                String pid = line.trim();
                Log.logger.info("find " + processName + " with pid:" + pid);

                ps = Runtime.getRuntime().exec("kill -9 " + pid);
                ps.waitFor();
                Log.logger.info("kill pid:" + pid);
            }
            if (br != null) {
                br.close();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.logger.info("kill the " + processName + " meet error:" + e.getMessage());
        }
    }
}
