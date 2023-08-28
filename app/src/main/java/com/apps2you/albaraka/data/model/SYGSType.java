package com.apps2you.albaraka.data.model;

public enum SYGSType {
    Default(0),
    General(1),
    Vehicles(2),
    Property(3),
    Lands(4);
    private int id;

    SYGSType(int id){
        this.setId(id);
    }
    public void setId(int id){
        this.id =id;
    }

    public int getId(){
        return this.id;
    }

    static public SYGSType getTypeById(int id){
        switch (id){
            case 1:
                return General;
            case 2:
                return Vehicles;
            case 3:
                return Property;
            case 4:
                return Lands;
            default:
                return Default;
        }
    }
}
