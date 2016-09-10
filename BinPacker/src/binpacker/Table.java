/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package binpacker;

/**
 *
 * @author SHOP
 */
public class Table {

//DATA
    //if no table exists, name = "NULL"
    private String name;
    private Rect size;
    private int orientation;
    private double price;

    
//CONSTRUCTORS
    public Table(){
        setName("NULL");
        setOrient(0);
        size = new Rect();
        setPrice(0);
    }
    public Table(String newName, int newOrient, double newWidth, 
        double newHeight, double newPrice){
        
        size = new Rect();
        
        setName(newName);
        setOrient(newOrient);
        setSize(newWidth, newHeight);
        setPrice(newPrice);
    }


//MEMBERS
    public void print(){
        System.out.print(
        String.format(
            "----------------------------\n"
          + "Table: %s \n"
          + "Size: %.4f, %.4f \n"
          + "Price: %.2f \n"
          + "----------------------------\n",
            name, size.getWidth(), size.getHeight(), price
        )
        );
    }
    public void invertSize(){
        double newHeight = getWidth();
        double newWidth = getHeight();
        setWidth(newWidth);
        setHeight(newHeight);
        setOrient(90);
    }
    
    
//GETTERS SETTERS
    public String getName(){
        return name;
    }
    public Rect getSize(){
        return size;
    }
    public double getWidth(){
        return size.getWidth();
    }
    public double getHeight(){
        return size.getHeight();
    }
    public int getOrient(){
        return orientation;
    }
    public double getPrice(){
        return price;
    }
    public void setName(String newName){
        name = newName;
    }
    public void setSize(double newWidth, double newHeight){
        size.setWidth(newWidth);
        size.setHeight(newHeight);
    }
    public void setSize(Rect newSize){
        setWidth(newSize.getWidth());
        setHeight(newSize.getHeight());
    }
    public void setWidth(double newWidth){
        size.setWidth(newWidth);
    }
    public void setHeight(double newHeight){
        size.setHeight(newHeight);
    }
    public void setOrient(int newOrient){
        orientation = newOrient;
    }
    public void setPrice(double newPrice){
        price = newPrice;
    }
    public void set(Table t){
        setName(t.getName());
        setSize(t.getSize());
        setOrient(t.getOrient());
        setPrice(t.getPrice());
    }
}