package com.fppreactnativemodule;
import mcv.facepass.FacePassHandler;

public class FacePassHandlerHolder {
    private static FacePassHandler myObject;

    public static FacePassHandler getMyObject() {
        return myObject;
    }

    public static void setMyObject(FacePassHandler object) {
        myObject = object;
    }
}
