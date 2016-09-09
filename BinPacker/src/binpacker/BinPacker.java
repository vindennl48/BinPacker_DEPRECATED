/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package binpacker;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author SHOP
 */
public class BinPacker {

/*############################################################################*/
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        BinPacker bp = new BinPacker();
        
        System.out.println("Please Enter Data");
        
//        System.out.println("Size of Aluminum Sheet "
//                + "(width 'space' height 'enter'):");
        bp.tree.size.width = scan.nextInt();
        bp.tree.size.height = scan.nextInt();
        
//        System.out.println("Number of Tables:");
        int numTables = scan.nextInt();
        scan.nextLine();  //prepare for string input
        
//        System.out.println("List Out All Tables (Table Name 'space' "
//                + "Table Width 'space' Table Height 'space' "
//                + "Table Price 'enter'):");
        
        for(int i = 0; i < numTables; i++){
            
            String tName = scan.nextLine();
            double tWidth = scan.nextDouble();
            double tHeight = scan.nextDouble();
            double tPrice = scan.nextDouble();
            scan.nextLine();
            
            if(i == 0){
                bp.setFirstTable(tName, tWidth, tHeight, tPrice);
            }
            else{
                bp.addTable(tName, tWidth, tHeight, tPrice);
            }
            
        }
        
//        for(int i = 0; i < numTables; i++){
//            System.out.println(
//                    bp.tree.tableList.get(i).name + " "
//                    + Double.toString(bp.tree.tableList.get(i).sellingPrice)
//            );
//        }
        
        bp.run();
        
    }
    
/*############################################################################*/
    
//start of BinPacker Class
    
//DATA
    Node tree;
    Table t;
    
//CONSTRUCTOR
    public BinPacker(){
        tree = new Node();
        t = new Table();
    }
    
//MEMBERS
    void addTable(String newName, double newWidth, 
            double newHeight, double newPrice){
        
        tree.tableList.add(
            new Table(
                newName,
                0,
                newWidth,
                newHeight,
                newPrice
            )
        );
        
        addTableInv(newName, newWidth, newHeight, newPrice);
    }
    
    void addTableInv(String newName, double newWidth, 
            double newHeight, double newPrice){
        
        tree.tableList_inv.add(
            new Table(
                newName,
                90,
                newHeight,
                newWidth,
                newPrice
            )
        );
    }
    
    void setFirstTable(String newName, double newWidth, 
            double newHeight, double newPrice){
        tree.table = new Table(newName, 0, newWidth, newHeight,newPrice);
    }
    
    void run(){
        tree.setChildren();
        tree.runTree();
    }

    
//STRUCTURES
    class Node{
        
    //DATA
        //list of tables
        List<Table> tableList;
        
        //inverted list of tables (rotated 90deg)
        List<Table> tableList_inv;

        //children list
        List<Node> children;
        
        //branch selling amount
        double branchValue;
        String tablesOut;
        
        //size of empty space available for this node
        Rect size;
        
        //location of empty space within the original allotted space
        Rect location;
        
        //table that fits, if any
        Table table;
        
    //MEMBERS CONSTRUCTORS DESTRUCTORS
        public Node(List<Table> newList, List<Table> newInvList){
            tableList = newList;
            tableList_inv = newInvList;
            branchValue = 0;
            tablesOut = "";
            size = new Rect();
            location = new Rect();
            table = new Table();
        }
        public Node(){
            tableList = new ArrayList<Table>();
            tableList_inv = new ArrayList<Table>();
            children = new ArrayList<Node>();
            
            branchValue = 0;
            tablesOut = "";
            size = new Rect();
            location = new Rect();
            table = new Table();
        };
        
        
    //RUNNING THROUGH THE TREE
        void runTree(){
            setBranchValue();
            printAnswer();
        }
        
        void setBranchValue(){
            
            if(!children.isEmpty()){
                
                String newBranch = "fail";
                
                int ch_si = children.size();
                for(int i = 0; i < ch_si; i++){
                    
                    if(children.get(i).branchValue == 0){
                        children.get(i).setBranchValue();
                        
                        if(children.get(i).branchValue > this.branchValue)
                            this.branchValue = children.get(i).branchValue;
                            newBranch = children.get(i).tablesOut;
                    }
                    
                }
                
                this.branchValue += this.table.sellingPrice;
                
                tablesOut = String.format(
                        ","+this.table.name+"-%.4f,%.4f ",
                        this.location.width, 
                        this.location.height
                );
                tablesOut += newBranch;
                
            }
            else{
                
                this.branchValue = this.table.sellingPrice;
                this.tablesOut = String.format(
                        ","+this.table.name+"-%.4f,%.4f ",
                        this.location.width, 
                        this.location.height
                );
                
            }
            
        }
        
        void printAnswer(){
            System.out.println(tablesOut);
        }
        
        
    //SETTING UP THE TREE
        void setChildren(){
            if(!table.name.equals("NULL")){
                
                //get first rectangle size
                Rect rect1 = new Rect(
                    size.width,
                    (size.height - table.size.height)
                );
                //get first rectangle location
                Rect rect1Loc = new Rect(
                    location.width,
                    (table.size.height + location.height)
                );
                //create children for this rectangle size and 0 orientation
                setChildList(tableList, rect1, rect1Loc);
                
                
                //get second rectangle size
                Rect rect2 = new Rect(
                    (size.width - table.size.width),
                    size.height
                );
                //get second rectangle location
                Rect rect2Loc = new Rect(
                    (table.size.width + location.width),
                    location.height
                );
                //create children for this rectangle size and 90 orientation
                setChildList(tableList_inv, rect2, rect2Loc);
                
            }
            //if table is null then no more tables can be inserted
            //into this space so children are left as null
            //or a table has not been inserted yet
            
            //run through all children the same way
            runThroughChildren();
        }
        
        void setChildList(List<Table> newList, Rect newRect, Rect newLocation){
            //create children for this rectangle size and 0 orientation
            if(!newList.isEmpty()){
                
                for(Table t : newList){

                    //if table can fit
                    if(t.size.width <= newRect.width &&
                       t.size.height <= newRect.height){

                        //create child
                        Node child = new Node();

                        //set table lists
                        boolean removed = false;
                        for(Table a : tableList){
                            if(a.name.equals(t.name) && !removed){
                                removed = true;
                            }
                            else{
                                child.tableList.add(a);
                            }
                        }

                        for(Table a : tableList_inv){
                            child.tableList_inv.add(a);
                           if(a.name.equals(t.name) && !removed){
                                removed = true;
                            }
                            else{
                                child.tableList.add(a);
                            }
                        }

                        //set empty space
                        child.size = newRect;

                        //set location of child
                        child.location = newLocation;

                        //add table to child
                        child.table = t;

                        //add child to children list
                        children.add(child);

                    }
                }
                
            }
        }
        
        void runThroughChildren(){
            if(!children.isEmpty()){
                for(Node n : children){
                    n.setChildren();
                }
            }
        }
        
        
    //GETTERS SETTERS
        void setTable(Table newTable){
            table = newTable;
        }
        
        void setSize(Rect newSize){
            size = newSize;
        }
        

    }   

    class Rect{
        double width;
        double height;
        
        public Rect(){
            width = 0;
            height = 0;
        }
        public Rect(double newWidth, double newHeight){
            width = newWidth;
            height = newHeight;
        }
    }
    
    class Table{
        //if no table exists, name = "NULL"
        String name;
        Rect size;
        int orientation;
        double sellingPrice;
        
        public Table(){
            name = "NULL";
            orientation = 0;
            size = new Rect();
            sellingPrice = 0;
        }
        
        public Table(String newName, int newOrient, double newWidth, 
            double newHeight, double newPrice){
            name = newName;
            orientation = newOrient;
            size = new Rect(newWidth,newHeight);
            sellingPrice = newPrice;
        }
        
    }

}
