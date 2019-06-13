package com.skyworthdigital.voice.videosearch.manager;

public interface IModelManager {
    public static final int PERSON_TYPE = 0x0001;
    public static final int SUBJECT_TYPE = 0x0002;
    public static final int VIDEO_TYPE = 0x0003;

    public boolean isDeleteModel();

    public void delete(int type, int id);

}
