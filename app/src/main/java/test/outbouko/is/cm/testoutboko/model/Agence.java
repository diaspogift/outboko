package test.outbouko.is.cm.testoutboko.model;

/**
 * Created by Ange_Douki on 24/08/2016.
 */
public class Agence {
    private int id;
    private String name;

    public Agence(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString(){
        return this.name;
    }
}
