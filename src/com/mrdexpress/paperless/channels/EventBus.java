package com.mrdexpress.paperless.channels;

/**
 * Created by hannobean on 2014/05/08.
 */
public class EventBus {
    public static class ManagerBackToDriverHome {
        public static String stopids;
        public static String driverid;

        public ManagerBackToDriverHome(){

        }
        public ManagerBackToDriverHome(String stopid){
            stopids = stopid;
        }
        public ManagerBackToDriverHome(String stopid , String did){
            stopids = stopid;
            driverid = did;
        }
    }

    public static class refreshWorkflow {

    }
}
